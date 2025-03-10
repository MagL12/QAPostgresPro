-- Вставка данных в таблицу students
INSERT INTO students (name, start_year) VALUES
                                            ('Alice', 2020),
                                            ('Bob', 2021),
                                            ('Charlie', 2022),
                                            ('Eve', 2023);

-- Вставка данных в таблицу courses
INSERT INTO courses (title, hours) VALUES
                                       ('Java Basics', 40),
                                       ('SQL Fundamentals', 30),
                                       ('Data Structures', 50);

-- Вставка данных в таблицу exams
INSERT INTO exams (s_id, c_no, score) VALUES
                                          (1, 1, 90),  -- Alice сдала Java Basics на 90
                                          (1, 2, 85),  -- Alice сдала SQL Fundamentals на 85
                                          (2, 1, 75),  -- Bob сдал Java Basics на 75
                                          (3, 3, 95);  -- Charlie сдал Data Structures на 95