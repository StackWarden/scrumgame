DROP TABLE IF EXISTS question_log;
DROP TABLE IF EXISTS monster_log_questions;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS level_log;
DROP TABLE IF EXISTS monster_log;
DROP TABLE IF EXISTS session;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS player;

-- 1. Player table
CREATE TABLE player (
                        id INT NOT NULL AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id)
);

-- 2. Question table
CREATE TABLE question (
                          id INT NOT NULL AUTO_INCREMENT,
                          text TEXT NOT NULL,
                          correct_answer VARCHAR(255) NOT NULL,
                          hint VARCHAR(255),
                          type VARCHAR(255),
                          PRIMARY KEY (id)
);

-- 3. Monster log table
CREATE TABLE monster_log (
                             id INT NOT NULL AUTO_INCREMENT,
                             session_id INT NOT NULL,
                             defeated TINYINT(1) NOT NULL DEFAULT 0,
                             PRIMARY KEY (id),
                             FOREIGN KEY (session_id) REFERENCES player(id) -- temporarily refer to player, fix later
);

-- 4. Level log table
CREATE TABLE level_log (
                           id INT NOT NULL AUTO_INCREMENT,
                           session_id INT NOT NULL,
                           room_number INT NOT NULL,
                           level_type VARCHAR(50) NOT NULL,
                           completed TINYINT(1) NOT NULL DEFAULT 0,
                           PRIMARY KEY (id),
                           FOREIGN KEY (session_id) REFERENCES player(id) -- temporarily refer to player, fix later
);

-- 5. Session table (now level_log and monster_log exist)
CREATE TABLE session (
                         id INT NOT NULL AUTO_INCREMENT,
                         player_id INT NOT NULL,
                         current_level_log_id INT DEFAULT NULL,
                         current_monster_log_id INT DEFAULT NULL,
                         score INT NOT NULL DEFAULT 0,
                         monster_encounters INT NOT NULL DEFAULT 0,
                         gameover TINYINT(1) NOT NULL DEFAULT 0,
                         PRIMARY KEY (id),
                         FOREIGN KEY (player_id) REFERENCES player(id),
                         FOREIGN KEY (current_level_log_id) REFERENCES level_log(id),
                         FOREIGN KEY (current_monster_log_id) REFERENCES monster_log(id)
);

-- 6. Fix foreign key references now that session table exists
ALTER TABLE level_log DROP FOREIGN KEY level_log_ibfk_1;
ALTER TABLE level_log ADD FOREIGN KEY (session_id) REFERENCES session(id);

ALTER TABLE monster_log DROP FOREIGN KEY monster_log_ibfk_1;
ALTER TABLE monster_log ADD FOREIGN KEY (session_id) REFERENCES session(id);

-- 7. Question log
CREATE TABLE question_log (
                              id INT NOT NULL AUTO_INCREMENT,
                              session_id INT NOT NULL,
                              level_log_id INT NOT NULL,
                              question_id INT NOT NULL,
                              completed TINYINT(1) NOT NULL DEFAULT 0,
                              PRIMARY KEY (id),
                              FOREIGN KEY (session_id) REFERENCES session(id),
                              FOREIGN KEY (level_log_id) REFERENCES level_log(id),
                              FOREIGN KEY (question_id) REFERENCES question(id)
);

-- 8. Monster log questions
CREATE TABLE monster_log_questions (
                                       id INT NOT NULL AUTO_INCREMENT,
                                       monster_log_id INT NOT NULL,
                                       question_id INT NOT NULL,
                                       PRIMARY KEY (id),
                                       FOREIGN KEY (monster_log_id) REFERENCES monster_log(id),
                                       FOREIGN KEY (question_id) REFERENCES question(id)
);

-- 9. Items
CREATE TABLE item (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(100) NOT NULL,
                      level_log_id INT NOT NULL,
                      used TINYINT(1) NOT NULL DEFAULT 0,
                      player_id INT DEFAULT NULL,
                      session_id INT DEFAULT NULL,
                      PRIMARY KEY (id),
                      FOREIGN KEY (level_log_id) REFERENCES level_log(id) ON DELETE CASCADE,
                      FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE SET NULL,
                      FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE SET NULL
);

-- Players
INSERT INTO player (name) VALUES
                              ('Alice'),
                              ('Bob'),
                              ('Charlie');

-- Questions
INSERT INTO question (text, correct_answer, hint) VALUES
                                                      ('What is the capital of France?', 'Paris', 'It’s also the city of love.'),
                                                      ('2 + 2 = ?', '4', 'Basic arithmetic.'),
                                                      ('What color is the sky?', 'Blue', 'Look up on a sunny day.'),
                                                      ('Who wrote "Hamlet"?', 'Shakespeare', 'English playwright.'),
                                                      ('What is the boiling point of water in Celsius?', '100', 'Standard atmospheric pressure.'),
                                                      ('What is the square root of 64?', '8', 'It’s a whole number.'),
                                                      ('Which is correct? A, B, C, D?', 'A'),
                                                      ('What is the meaning of life, the universe and everything?', '42'),
                                                      ('What has wings but can not fly?', 'Penguin');

-- Sessions (one per player)
INSERT INTO session (player_id, score, monster_encounters, gameover)
VALUES
    (1, 0, 0, 0),
    (2, 0, 0, 0),
    (3, 0, 0, 0);

-- Level logs (6 rooms per session for player 1)
INSERT INTO level_log (session_id, room_number, level_type, completed)
VALUES
    (1, 1, 'trivia', 0),
    (1, 2, 'trivia', 0),
    (1, 3, 'puzzle', 0),
    (1, 4, 'riddle', 0),
    (1, 5, 'trivia', 0),
    (1, 6, 'boss', 0);

-- Question logs (link each level with a question)
-- Assume level_log IDs from 1 to 6
INSERT INTO question_log (session_id, level_log_id, question_id, completed)
VALUES
    (1, 1, 1, 0),
    (1, 2, 2, 0),
    (1, 3, 3, 0),
    (1, 4, 4, 0),
    (1, 5, 5, 0),
    (1, 6, 6, 0);

INSERT INTO monster_log (session_id, defeated)
VALUES (1, 0);

INSERT INTO monster_log_questions (monster_log_id, question_id)
VALUES
    (1, 1),
    (1, 2);

