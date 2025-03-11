# Решение задания по базам данных

---

## **1. Установка PostgreSQL в Docker**
**Команды и настройки:**  
См. файл `docker-compose.yml` в корне проекта.  
**Ключевые строки:**  
```yaml
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: academy
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "15432:5432"
    volumes:
      - ./src/main/resources/init.sql:/docker-entrypoint-initdb.d/init.sql
```

---

## **2. Создать БД academy**
**Скрипт:**  
Файл `init.sql` в папке `src/main/resources/` содержит создание таблиц.  
**Команда для ручного создания БД:**
```sql
CREATE DATABASE academy;
```

---

## **3. Создать таблицы по схеме**
**Скрипт:**  
См. файл `init.sql` в `src/main/resources/`.  
**Структура таблиц:**
```sql
-- Students
CREATE TABLE students (
  s_id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  start_year INTEGER NOT NULL CHECK (start_year >= 2000)
);

-- Courses
CREATE TABLE courses (
  c_no SERIAL PRIMARY KEY,
  title VARCHAR(100) NOT NULL UNIQUE,
  hours INTEGER NOT NULL CHECK (hours > 0)
);

-- Exams
CREATE TABLE exams (
  s_id INTEGER REFERENCES students(s_id),
  c_no INTEGER REFERENCES courses(c_no),
  score INTEGER CHECK (score BETWEEN 0 AND 100),
  PRIMARY KEY (s_id, c_no)
);
```

---

## **4. Добавить несколько записей**
**Скрипт:**  
См. файл `seed.sql` в `src/main/resources/`.  
**Пример вставки:**
```sql
INSERT INTO students (name, start_year) VALUES ('Alice', 2020);
INSERT INTO courses (title, hours) VALUES ('Java Basics', 40);
INSERT INTO exams (s_id, c_no, score) VALUES (1, 1, 90);
```

---

## **5. Запрос: Студенты без экзаменов**
**Скрипт:**
```sql
SELECT s.*
FROM students s
LEFT JOIN exams e ON s.s_id = e.s_id
WHERE e.s_id IS NULL;
```

---

## **6. Запрос: Студенты и количество экзаменов**
**Скрипт:**
```sql
SELECT
    s.name,
    COUNT(e.c_no) AS exams_count
FROM
    students s
        INNER JOIN
    exams e ON s.s_id = e.s_id
GROUP BY
    s.s_id, s.name
ORDER BY
    exams_count DESC;
```

---

## **7. Запрос: Средний балл по курсам**
**Скрипт:**
```sql
SELECT 
    c.title, 
    AVG(e.score) AS avg_score
FROM courses c
JOIN exams e ON c.c_no = e.c_no
GROUP BY c.title
ORDER BY avg_score DESC;
```

---

## **8. Генерация данных (скрипт)**
**Скрипт:**  
См. класс `DataGenerator.java` в папке `src/main/java/`.  
**Основные настройки генерации:**
```properties
# В файле config.properties
students.count=100
courses.count=20
exams.count=200
```

---

### **Где найти скрипты:**
| Задание | Путь к скрипту | Описание |
|--------|----------------|----------|
| **2. Создание БД** | `init.sql` в `src/main/resources/` | Создание таблиц `students`, `courses`, `exams`. |
| **4. Вставка данных** | `seed.sql` в `src/main/resources/` | Примеры записей для тестирования. |
| **5-7. Запросы** | В текущем файле `TaskSolution2.md` | SQL-запросы для выполнения задач. |
| **8. Генерация данных** | `DataGenerator.java` в `src/main/java/` | Java-скрипт для генерации случайных данных. |

---

### **Примечания**
- Все скрипты находятся в папке `src/main/resources/` (для SQL) или `src/main/java/` (для Java).
- Параметры генерации (количество студентов/курсов/экзаменов) указаны в `config.properties`.

```