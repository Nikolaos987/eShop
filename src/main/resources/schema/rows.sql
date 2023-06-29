/* add rows */

INSERT INTO users (username, password)
VALUES ('One'   , '1'),
       ('Two'   , '12'),
       ('Three' , '123');

INSERT INTO cart (uid)
SELECT uid FROM users;

INSERT INTO product (name, description, price, quantity, brand, category)
VALUES ('Samsung Galaxy S23 Ultra', 'Galaxy S23 Ultra owns the latest processor, which is the Qualcomm SM8550-AC Snapdragon 8 Gen 2 (4 nm) processor. It is capable of multi-tasking, streaming videos, running even the most demanding games with ease. The phone comes with various memory variations, up to 1TB and 12GB RAM (Please check specification table).',
        1065.0, 5, 'Samsung', 'smartphone'),
       ('Xiaomi Poco X5 Pro', 'Snapdragon® 778G processor 120Hz FHD+ AMOLED DotDisplay 108MP pro-grade main camera 67W turbo charging',
        336.94, 7, 'Xiaomi', 'smartphone'),
       ('Nokia 105', 'The Nokia 105 comes with a classic, ergonomic design to fit your hands better. In addition, the 1.8" QVGA screen offers better viewing, while the keyboard with separate keys makes it easier to write messages and make calls.',
        31.0, 2, 'Nokia', 'cellphone'),
       ('Xiaomi Redmi Note 11S', 'The smartphone is equipped with an AI quad camera system, with a powerful 108MP main camera for crystal clear shots even at high magnification. The camera system is completed by the 8MP ultra-wide-angle camera, the 2MP macro camera, the 2MP depth camera and the 16MP selfie camera.',
        201.4, 4, 'Xiaomi', 'smartphone'),
       ('Unihertz Tank', 'The Unihertz Tank is a cutting-edge rugged 4G smartphone that can withstand harsh environments while still retaining the incredible technology and features built into modern smartphones.',
        309.99, 1, 'Unihertz', 'smartphone'),
       ('L8STAR BM10 Mini', 'A mobile with a mini and compact design, smaller than a lighter!

Equipped with a large 0.66-inch OLED screen, the L8STAR BM10 offers you the ultimate visual experience. It uses the MTK6261D processor, which makes you listen to music smoothly. Besides, dual card dual standby design combined with low radiation, it is healthy and convenient for your life. Large 350mAh capacity battery offers you a very great experience.',
        11.71, 17, 'L8STAR', 'cellphone'),
       ('Ulefone Armor Mini 2', 'Robust and waterproof (IP68) mobile phone, with an imposing design and high-quality frame, resistant to hard use. It is equipped with a battery with a capacity of 2100mAh which offers a long autonomy and a lens.',
        40.0, 3, 'Ulefone', 'cellphone');

