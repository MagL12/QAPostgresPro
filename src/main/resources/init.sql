CREATE TABLE students (
                          s_id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          start_year INTEGER NOT NULL CHECK (start_year >= 2000)
);

CREATE TABLE courses (
                         c_no SERIAL PRIMARY KEY,
                         title VARCHAR(100) NOT NULL UNIQUE,
                         hours INTEGER NOT NULL CHECK (hours > 0)
);

CREATE TABLE exams (
                       s_id INTEGER REFERENCES students(s_id),
                       c_no INTEGER REFERENCES courses(c_no),
                       score INTEGER CHECK (score BETWEEN 0 AND 100),
                       PRIMARY KEY (s_id, c_no)
);