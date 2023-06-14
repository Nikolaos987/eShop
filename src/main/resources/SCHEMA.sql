/* create table */

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    username varchar(20) NOT NULL,
    password varchar(20) NOT NULL,
    PRIMARY KEY (username)
);




/* add rows */

INSERT INTO users
VALUES ('One', '1'),
       ('Two', '12'),
       ('Three', '123'),
       ('Four', '1234'),
       ('Five', '12345');