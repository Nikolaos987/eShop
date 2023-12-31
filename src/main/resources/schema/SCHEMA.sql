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


/* create table product */
DROP TABLE IF EXISTS product CASCADE;
CREATE TABLE product (
    pid uuid /*DEFAULT uuid_generate_v4()*/,  /* gen_random_uuid () */
    name varchar(500) NOT NULL,
--     image varchar(250),
    description VARCHAR(50000),
    price float8 NOT NULL,
    quantity int NOT NULL,
    brand varchar(2500) NOT NULL,
    category varchar(2500) NOT NULL,
    PRIMARY KEY (pid)
);


/* create table cart */
DROP TABLE IF EXISTS cart CASCADE;
CREATE TABLE cart (
    cid uuid DEFAULT uuid_generate_v4(),
    uid uuid NOT NULL,
    dateCreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cid),
    FOREIGN KEY (uid) REFERENCES users(uid)
);


/* create table cartItem*/
DROP TABLE IF EXISTS cartItem CASCADE;
CREATE TABLE cartItem (
    itemid uuid DEFAULT uuid_generate_v4(),
    cid uuid,
    pid uuid,
    quantity int,
    PRIMARY KEY (itemid),
    FOREIGN KEY (cid) REFERENCES cart(cid),
    FOREIGN KEY (pid) REFERENCES product(pid)
);

/* create table related_products */
DROP TABLE IF EXISTS related_products CASCADE;
CREATE TABLE related_products (
    id uuid DEFAULT uuid_generate_v4(),
    r_pid uuid,
    to_pid uuid /*DEFAULT uuid_generate_v4()*/,  /* gen_random_uuid () */
    PRIMARY KEY (id),
    FOREIGN KEY (r_pid) REFERENCES product(pid),
    FOREIGN KEY (to_pid) REFERENCES product(pid)
);


CREATE INDEX find_user ON users(username);
CREATE INDEX find_product ON product(name);
