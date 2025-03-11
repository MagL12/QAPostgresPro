
# Решение тестового задания для стажера QA в Postgres Pro

## Содержание
1. [Описание заданий и решений](#1-описание-заданий-и-решений)
2. [Запуск через Docker Compose](#2-запуск-через-docker-compose)
3. [Проверка корректности](#3-проверка-корректности)
4. [Выполнение SQL-запросов](#4-выполнение-sql-запросов)
5. [Структура проекта](#5-структура-проекта)
6. [Очистка окружения](#6-очистка-окружения)
7. [Дополнительные команды](#7-дополнительные-команды)

---

## 1. Описание заданий и решений
- **Задания:**  
  Все задания описаны в файле `Tasks.md` в папке `src/main/resources/`.

- **Ответ на первое задание (теория тестирования):**  
  Файл `TaskSolution1.md` в папке `src/main/resources/`.

- **Ответ на второе задание (базы данных):**  
  Файл `TaskSolution2.md` в папке `src/main/resources/` (включает SQL-скрипты и запросы).

---

## 2. Запуск через Docker Compose
### Для начала работы:
```bash
docker-compose up --build -d
```

### Проверка запуска:
```bash
docker ps  # Убедитесь, что контейнеры postgres-academy и data-generator запущены
```

---

## 3. Проверка корректности
### **Проверка запущенных контейнеров:**
```bash
docker ps
```

### **Проверка данных в БД:**
```bash
# Подключитесь к PostgreSQL
docker exec -it postgres-academy psql -U postgres -d academy

# Примеры проверки:
academy=# SELECT COUNT(*) FROM students;  # Должно быть 100+ (если генератор запустился)
academy=# SELECT * FROM courses LIMIT 2;
academy=# SELECT * FROM exams LIMIT 2;
```

### **Проверка логов генератора:**
```bash
docker logs data-generator-1
```

---

## 4. Выполнение SQL-запросов
### Подключение к PostgreSQL:
```bash
docker exec -it postgres-academy psql -U postgres -d academy
```

### Примеры запросов (из `TaskSolution2.md`):
#### **Запрос 5: Студенты без экзаменов**
```sql
SELECT s.*
FROM students s
LEFT JOIN exams e ON s.s_id = e.s_id
WHERE e.s_id IS NULL;
```

#### **Запрос 6: Количество экзаменов у студентов**
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

#### **Запрос 7: Средний балл по курсам**
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

## 5. Структура проекта
```
QAPostgresProTest/
├── docker-compose.yml  # Настройка Docker
├── Dockerfile          # Сборка Java-приложения
├── pom.xml             # Maven-конфигурация
└── src/
    └── main/
        ├── java/          # Java-код (генератор данных)
        │   └── DataGenerator.java
        └── resources/     # SQL-скрипты и конфигурация
            ├── config.properties
            ├── init.sql    # Создание таблиц
            ├── Tasks.md
            ├── TaskSolution1.md
            ├── TaskSolution2.md
            └── seed.sql    # Тестовые данные
```

---

## 6. Очистка окружения
```bash
docker-compose down -v  # Остановить и удалить контейнеры
docker volume prune      # Удалить оставшиеся volume
```

---
## 7. Дополнительные команды
### **Запуск только PostgreSQL:**
```bash
docker-compose up -d postgres
```

### **Запуск только генератора данных:**
```bash
docker-compose up data-generator
```