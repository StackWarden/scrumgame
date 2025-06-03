-- -------------------------------------------------------------
-- TablePlus 6.4.8(608)
--
-- https://tableplus.com/
--
-- Database: scrumgame
-- Generation Time: 2025-05-15 11:12:57.2070
-- -------------------------------------------------------------


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*use scrumgame;*/
DROP TABLE IF EXISTS `level_log`;
CREATE TABLE `level_log` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `session_id` int NOT NULL,
                             `question_id` int NOT NULL,
                             `completed` tinyint(1) NOT NULL DEFAULT '0',
                             PRIMARY KEY (`id`),
                             KEY `question_id` (`question_id`),
                             KEY `session_id` (`session_id`),
                             CONSTRAINT `level_log_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
                             CONSTRAINT `level_log_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `monster_log`;
CREATE TABLE `monster_log` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `session_id` int NOT NULL,
                               `defeated` tinyint(1) NOT NULL DEFAULT '0',
                               PRIMARY KEY (`id`),
                               KEY `session_id` (`session_id`),
                               CONSTRAINT `monster_log_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                            `name` VARCHAR(100) NOT NULL,
                            `level_log_id` INT NOT NULL,
                            `used` TINYINT(1) NOT NULL DEFAULT 0,
                            `player_id` INT DEFAULT NULL,
                            `session_id` INT DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `level_log_id` (`level_log_id`),
                            KEY `player_id` (`player_id`),
                            CONSTRAINT `item_ibfk_1` FOREIGN KEY (`level_log_id`) REFERENCES `level_log` (`id`) ON DELETE CASCADE,
                            CONSTRAINT `item_ibfk_2` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE SET NULL,
                            CONSTRAINT `item_ibfk_3` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `monster_log_questions`;
CREATE TABLE `monster_log_questions` (
                                         `id` int NOT NULL AUTO_INCREMENT,
                                         `monster_log_id` int NOT NULL,
                                         `question_id` int NOT NULL,
                                         PRIMARY KEY (`id`),
                                         KEY `monster_log_id` (`monster_log_id`),
                                         KEY `question_id` (`question_id`),
                                         CONSTRAINT `monster_log_questions_ibfk_1` FOREIGN KEY (`monster_log_id`) REFERENCES `monster_log` (`id`),
                                         CONSTRAINT `monster_log_questions_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `name` varchar(255) NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `text` text NOT NULL,
                            `correct_answer` varchar(255) NOT NULL,
                            `hint` varchar(255),
                            `type` varchar(255),
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `session`;
CREATE TABLE `session` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `player_id` int NOT NULL,
                           `current_level_log_id` int DEFAULT NULL,
                           `current_monster_log_id` int DEFAULT NULL,
                           `score` int NOT NULL DEFAULT '0',
                           `monster_encounters` int NOT NULL DEFAULT '0',
                           `gameover` tinyint(1) NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`),
                           KEY `player_id` (`player_id`),
                           KEY `current_level_log_id` (`current_level_log_id`),
                           KEY `current_monster_log_id` (`current_monster_log_id`),
                           CONSTRAINT `session_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`),
                           CONSTRAINT `session_ibfk_2` FOREIGN KEY (`current_level_log_id`) REFERENCES `level_log` (`id`),
                           CONSTRAINT `session_ibfk_3` FOREIGN KEY (`current_monster_log_id`) REFERENCES `monster_log` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- ... [same DROP/CREATE statements as before] ...

-- Seed: players
INSERT INTO `player` (`name`) VALUES
                                  ('Alice'),
                                  ('Bob'),
                                  ('Charlie');

-- Seed: questions
INSERT INTO `question` (`text`, `correct_answer`, `type`) VALUES
                                                      ('What is the capital of France?', 'Paris', 'Open'),
                                                      ('2 + 2 = ?', '4', 'Open'),
                                                      ('What color is the sky?', 'Blue', 'Open'),
                                                      ('Which is correct? A, B, C, D?', 'A', 'Multiple Choice'),
                                                      ('What is the meaning of life, the universe and everything?', '42', 'Open');
                                                      -- ('', '', 'Riddle')


-- Seed: sessions
INSERT INTO `session` (`player_id`, `score`, `monster_encounters`, `gameover`)
VALUES
    (1, 0, 0, 0),
    (2, 0, 0, 0);

-- Seed: level_log
INSERT INTO `level_log` (`session_id`, `question_id`, `completed`)
VALUES
    (1, 1, 0),
    (1, 2, 1),
    (2, 3, 0);

-- Seed: monster_log
INSERT INTO `monster_log` (`session_id`, `defeated`)
VALUES
    (1, 1),
    (2, 0);

-- Seed: monster_log_questions
INSERT INTO `monster_log_questions` (`monster_log_id`, `question_id`)
VALUES
    (1, 1),
    (2, 2);
