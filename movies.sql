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
  `id` INT NOT NULL,
  `firstName` VARCHAR(64) NOT NULL,
  `lastName` VARCHAR(64) NOT NULL,
  `birthDate` DATE NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `movies`.`movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `movies`.`movie` (
  `id` INT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `genre` VARCHAR(45) NOT NULL,
  `releaseYear` YEAR NOT NULL,
  `directorId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_movie_person_idx` (`directorId` ASC) VISIBLE,
  CONSTRAINT `fk_movie_person`
    FOREIGN KEY (`directorId`)
    REFERENCES `movies`.`person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `movies`.`review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `movies`.`review` (
  `id` INT NOT NULL,
  `text` VARCHAR(255) NOT NULL,
  `rating` FLOAT NOT NULL,
  `dateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `movieId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_review_movie1_idx` (`movieId` ASC) VISIBLE,
  CONSTRAINT `fk_review_movie1`
    FOREIGN KEY (`movieId`)
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
CREATE TABLE IF NOT EXISTS `movies`.`movieActors` (
  `actorId` INT NOT NULL,
  `movieId` INT NOT NULL,
  PRIMARY KEY (`actorId`, `movieId`),
  INDEX `fk_person_has_movie_movie1_idx` (`movieId` ASC) VISIBLE,
  INDEX `fk_person_has_movie_person1_idx` (`actorId` ASC) VISIBLE,
  CONSTRAINT `fk_person_has_movie_person1`
    FOREIGN KEY (`actorId`)
    REFERENCES `movies`.`person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_person_has_movie_movie1`
    FOREIGN KEY (`movieId`)
    REFERENCES `movies`.`movie` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;