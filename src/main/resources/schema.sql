-- Prvo dropamo ako već postoje (radi testiranja)
DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS seminar;
DROP TABLE IF EXISTS student;

-- Kreiramo tablice
CREATE TABLE student (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL
);

CREATE TABLE seminar (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         topic VARCHAR(255) NOT NULL,
                         lecturer VARCHAR(255) NOT NULL
);

CREATE TABLE registration (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              student_id BIGINT,
                              seminar_id BIGINT,
                              registered_at TIMESTAMP,
                              CONSTRAINT fk_registration_student FOREIGN KEY (student_id) REFERENCES student(id),
                              CONSTRAINT fk_registration_seminar FOREIGN KEY (seminar_id) REFERENCES seminar(id)
);