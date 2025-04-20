-- Ubacujemo testne podatke
INSERT INTO student (name, email) VALUES ('Ana Kovač', 'ana.kovac@example.com');
INSERT INTO student (name, email) VALUES ('Marko Horvat', 'marko.horvat@example.com');

INSERT INTO seminar (topic, lecturer) VALUES ('Java Development', 'Ivana Petrović');
INSERT INTO seminar (topic, lecturer) VALUES ('Spring Boot Basics', 'Luka Marić');

INSERT INTO registration (student_id, seminar_id, registered_at)
VALUES (1, 1, CURRENT_TIMESTAMP);
INSERT INTO registration (student_id, seminar_id, registered_at)
VALUES (2, 2, CURRENT_TIMESTAMP);