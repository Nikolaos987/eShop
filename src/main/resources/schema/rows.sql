/* add rows */

INSERT INTO customer (username, password)
VALUES ('One'   , '1'),
       ('Two'   , '12'),
       ('Three' , '123'),
       ('Four'  , '1234'),
       ('Five'  , '12345'),
       ('Six'   , '123456'),
       ('Seven' , '1234567'),
       ('Eight' , '12345678'),
       ('Nine'  , '123456789'),
       ('Ten'   , '1234567890');

INSERT INTO product (name, description, price, quantity, brand, category)
VALUES ('Samsung Galaxy S23 Ultra', 'Galaxy S23 Ultra owns the latest processor, which is the Qualcomm SM8550-AC Snapdragon 8 Gen 2 (4 nm) processor. It is capable of multi-tasking, streaming videos, running even the most demanding games with ease. The phone comes with various memory variations, up to 1TB and 12GB RAM (Please check specification table).',
        1065.0, 5, 'Samsung', 'smartphone'),
       ('Xiaomi Poco X5 Pro', 'Snapdragon® 778G processor 120Hz FHD+ AMOLED DotDisplay 108MP pro-grade main camera 67W turbo charging',
        336.94, 7, 'Xiaomi', 'smartphone'),
       ('Nokia 105', 'The Nokia 105 comes with a classic, ergonomic design to fit your hands better. In addition, the 1.8" QVGA screen offers better viewing, while the keyboard with separate keys makes it easier to write messages and make calls.',
        31.0, 2, 'Nokia', 'mobile phone');