SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema movies
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `movies` ;

-- -----------------------------------------------------
-- Schema movies
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `movies` DEFAULT CHARACTER SET utf8 ;
USE `movies` ;

-- -----------------------------------------------------
-- Table `movies`.`person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `movies`.`person` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(64) NOT NULL,
  `lastName` VARCHAR(64) NOT NULL,
  `birthDate` DATE NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `movies`.`movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `movies`.`movie` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `genre` VARCHAR(45) NOT NULL,
  `releaseYear` YEAR NOT NULL,
  `director_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_movie_person_idx` (`director_id` ASC) VISIBLE,
  CONSTRAINT `fk_movie_person`
    FOREIGN KEY (`director_id`)
    REFERENCES `movies`.`person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `movies`.`review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `movies`.`review` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(255) NOT NULL,
  `rating` FLOAT NOT NULL,
  `dateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `movie_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_review_movie1_idx` (`movie_id` ASC) VISIBLE,
  CONSTRAINT `fk_review_movie1`
    FOREIGN KEY (`movie_id`)
    REFERENCES `movies`.`movie` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
CONSTRAINT `check_rating_range`
    CHECK (`rating` >= 1.0 AND `rating` <= 10.0)
)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `movies`.`movieActors`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `movies`.`actor_ids` (
  `actor_id` INT NOT NULL,
  `movie_id` INT NOT NULL,
  PRIMARY KEY (`actor_id`, `movie_id`),
  INDEX `fk_person_has_movie_movie1_idx` (`movie_id` ASC) VISIBLE,
  INDEX `fk_person_has_movie_person1_idx` (`actor_id` ASC) VISIBLE,
  CONSTRAINT `fk_person_has_movie_person1`
    FOREIGN KEY (`actor_id`)
    REFERENCES `movies`.`person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_person_has_movie_movie1`
    FOREIGN KEY (`movie_id`)
    REFERENCES `movies`.`movie` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;