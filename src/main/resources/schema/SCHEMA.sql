CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE valid_categories AS ENUM ('smartphone', 'mobile phone');

/* create table users */
DROP TABLE IF EXISTS customer CASCADE;
CREATE TABLE customer (
    userId uuid DEFAULT uuid_generate_v4(),  /* gen_random_uuid () */
    username varchar(20) NOT NULL,
    password varchar(20) NOT NULL,
    PRIMARY KEY (userId)
);


/* create table product*/
DROP TABLE IF EXISTS product CASCADE;
CREATE TABLE product (
    productId uuid DEFAULT uuid_generate_v4(),  /* gen_random_uuid () */
    name varchar(50) NOT NULL,
    description VARCHAR(500),
    price float8 NOT NULL,
    quantity int CHECK (product.quantity>=0) NOT NULL,
    brand varchar(25) NOT NULL,
    category valid_categories NOT NULL,
    PRIMARY KEY (productId)
);


DROP TABLE IF EXISTS favourites CASCADE;
CREATE TABLE favourites (
    fid uuid DEFAULT uuid_generate_v4(),
    pid uuid NOT NULL,
    uid uuid NOT NULL,
    CONSTRAINT PK_Favourite PRIMARY KEY (fid),
    FOREIGN KEY (pid) REFERENCES product(productId) ON DELETE CASCADE,
    FOREIGN KEY (uid) REFERENCES customer(userId) ON DELETE CASCADE
);


DROP TABLE IF EXISTS cart CASCADE;
CREATE TABLE cart (
    cid uuid DEFAULT uuid_generate_v4(),
    pid uuid NOT NULL,
--     uid uuid NOT NULL,
    name varchar(50),
    price float8 NOT NULL,
    quantity int CHECK (cart.quantity>=0),
    CONSTRAINT PK_Cart PRIMARY KEY (cid),
    FOREIGN KEY (pid) REFERENCES product(productId) ON DELETE CASCADE
--     FOREIGN KEY (uid) REFERENCES customer(userId) ON DELETE CASCADE
);


CREATE INDEX find_user ON customer(username);
CREATE INDEX find_product ON product(name);
