package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static int STUDENTS_COUNT;
    private static int COURSES_COUNT;
    private static int EXAMS_COUNT;

    // Статический блок для загрузки конфига
    static {
        try (InputStream input = DataGenerator.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл config.properties не найден!");
            }
            Properties prop = new Properties();
            prop.load(input);

            URL = prop.getProperty("db.url");
            USER = prop.getProperty("db.user");
            PASSWORD = prop.getProperty("db.password");
            STUDENTS_COUNT = Integer.parseInt(prop.getProperty("students.count", "100"));
            COURSES_COUNT = Integer.parseInt(prop.getProperty("courses.count", "20"));
            EXAMS_COUNT = Integer.parseInt(prop.getProperty("exams.count", "200"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void truncateTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE exams CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE courses CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE students CASCADE");
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            truncateTables(conn);
            generateStudents(conn);
            generateCourses(conn);
            generateExams(conn);
            System.out.println("Данные сгенерированы!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Генерация студентов
    private static void generateStudents(Connection conn) throws SQLException {
        String sql = "INSERT INTO students (name, start_year) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Random rand = new Random();
            for (int i = 3; i < STUDENTS_COUNT; i++) {
                String name = generateRandomName();
                int startYear = 2000 + rand.nextInt(25);  // Год от 2000 до 2025
                pstmt.setString(1, name);
                pstmt.setInt(2, startYear);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    // Генерация курсов
    private static void generateCourses(Connection conn) throws SQLException {
        String sql = "INSERT INTO courses (title, hours) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < COURSES_COUNT; i++) {
                String title;
                do {
                    title = generateRandomCourseName();
                } while (!isCourseTitleUnique(conn, title));  // Проверяем уникальность

                int hours = 10 + ThreadLocalRandom.current().nextInt(90);
                pstmt.setString(1, title);
                pstmt.setInt(2, hours);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    // Генерация экзаменов с уникальными парами (s_id, c_no)
    private static void generateExams(Connection conn) throws SQLException {
        String sql = "INSERT INTO exams (s_id, c_no, score) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Получаем список всех студентов и курсов
            List<Integer> studentIds = getIds(conn, "students", "s_id");
            List<Integer> courseIds = getIds(conn, "courses", "c_no");

            // Убедимся, что есть студенты и курсы
            if (studentIds.isEmpty() || courseIds.isEmpty()) {
                throw new SQLException("Студенты или курсы не найдены!");
            }

            // Используем Set для отслеживания уникальных пар (s_id, c_no)
            Set<String> uniquePairs = new HashSet<>();
            Random rand = new Random();

            for (int i = 0; i < EXAMS_COUNT; i++) {
                int s_id = studentIds.get(rand.nextInt(studentIds.size()));
                int c_no = courseIds.get(rand.nextInt(courseIds.size()));

                // Формируем ключ для проверки уникальности
                String key = s_id + "-" + c_no;

                // Проверяем, не существует ли уже такой пары
                while (uniquePairs.contains(key)) {
                    s_id = studentIds.get(rand.nextInt(studentIds.size()));
                    c_no = courseIds.get(rand.nextInt(courseIds.size()));
                    key = s_id + "-" + c_no;
                }

                // Добавляем пару в Set
                uniquePairs.add(key);

                int score = rand.nextInt(101);  // 0-100
                pstmt.setInt(1, s_id);
                pstmt.setInt(2, c_no);
                pstmt.setInt(3, score);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }

    // Получение списка ID из таблицы
    private static List<Integer> getIds(Connection conn, String table, String idColumn) throws SQLException {
        String sql = "SELECT " + idColumn + " FROM " + table;
        List<Integer> ids = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        }
        return ids;
    }

    // Генерация случайного имени студента
    private static String generateRandomName() {
        String[] names = {"Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Harry", "Ivy", "Jack"};
        return names[new Random().nextInt(names.length)] + " " + generateRandomSurname();
    }

    // Генерация случайной фамилии
    private static String generateRandomSurname() {
        String[] surnames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"};
        return surnames[new Random().nextInt(surnames.length)];
    }

    // Генерация случайного названия курса
    private static String generateRandomCourseName() {
        String[] topics = {
                "Java", "Python", "SQL", "C++", "Data Science", "AI", "Web Dev", "Cloud", "Security",
                "Algorithms", "Databases", "Machine Learning", "React", "Spring Boot", "Kubernetes",
                "DevOps", "Big Data", "Full Stack", "Mobile Dev", "Game Dev"
        };
        String[] levels = {
                "Basics", "Advanced", "Masterclass", "Fundamentals", "Pro", "Specialized",
                "Beginner", "Intermediate", "Expert", "Certification"
        };
        return
                topics[new Random().nextInt(topics.length)] + " " +
                        levels[new Random().nextInt(levels.length)] +
                        " " +
                        (new Random().nextInt(1000) + 1);  // Случайное число
    }

    private static boolean isCourseTitleUnique(Connection conn, String title) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM courses WHERE title = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return !rs.getBoolean(1);
            }
        }
    }
}
