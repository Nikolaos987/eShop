CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- CREATE TYPE valid_categories AS ENUM ('smartphone', 'mobile phone');

/* create table users */
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    uid uuid DEFAULT uuid_generate_v4(),  /* gen_random_uuid () */
    username varchar(20) NOT NULL,
    password varchar(20) NOT NULL,
    PRIMARY KEY (uid)
);


/* create table product*/
DROP TABLE IF EXISTS product CASCADE;
CREATE TABLE product (
    pid uuid DEFAULT uuid_generate_v4(),  /* gen_random_uuid () */
    name varchar(50) NOT NULL,
    description VARCHAR(500),
    price float8 NOT NULL,
    quantity int /*CHECK (product.quantity>=0)*/ NOT NULL,
    brand varchar(25) NOT NULL,
    category valid_categories NOT NULL,
    PRIMARY KEY (pid)
);


DROP TABLE IF EXISTS cart CASCADE;
CREATE TABLE cart (
    cid uuid DEFAULT uuid_generate_v4(),
    uid uuid NOT NULL,
    dateCreated date NOT NULL,
    PRIMARY KEY (cid),
    FOREIGN KEY (uid) REFERENCES users(uid)
);


DROP TABLE IF EXISTS cartItem CASCADE;
CREATE TABLE cartItem (
    cid uuid DEFAULT uuid_generate_v4(),
    pid uuid NOT NULL,
    quantity int NOT NULL,
    PRIMARY KEY (cid),
    FOREIGN KEY (pid) REFERENCES product(pid)
);


CREATE INDEX find_user ON users(username);
CREATE INDEX find_product ON product(name);
