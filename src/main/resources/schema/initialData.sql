/* add rows */

INSERT INTO users (username, password)
VALUES ('One'   , '1'),
       ('Two'   , '12'),
       ('Three' , '123'),
       ('Four'  , '1234'),
       ('Five'  , '12345');

INSERT INTO cart (uid)
SELECT uid FROM users;

INSERT INTO product (name, image, description, price, quantity, brand, category)
VALUES ('Samsung Galaxy S23 Ultra', 'src/main/resources/assets/Samsung_Galaxy_S23_Ultra.jpeg', 'Galaxy S23 Ultra owns the latest processor, which is the Qualcomm SM8550-AC Snapdragon 8 Gen 2 (4 nm) processor. It is capable of multi-tasking, streaming videos, running even the most demanding games with ease. The phone comes with various memory variations, up to 1TB and 12GB RAM (Please check specification table).',
        1065.0, 35, 'Samsung', 'smartphone'),
       ('Xiaomi Poco X5 Pro', 'src/main/resources/assets/Xiaomi_Poco_X5_Pro.jpeg', 'Snapdragon® 778G processor 120Hz FHD+ AMOLED DotDisplay 108MP pro-grade main camera 67W turbo charging',
        336.94, 17, 'Xiaomi', 'smartphone'),
       ('Nokia 105', 'src/main/resources/assets/Nokia_105.jpeg', 'The Nokia 105 comes with a classic, ergonomic design to fit your hands better. In addition, the 1.8" QVGA screen offers better viewing, while the keyboard with separate keys makes it easier to write messages and make calls.',
        31.0, 62, 'Nokia', 'cellphone'),
       ('Xiaomi Redmi Note 11S', 'src/main/resources/assets/Xiaomi_Redmi_Note_11S.jpeg', 'The smartphone is equipped with an AI quad camera system, with a powerful 108MP main camera for crystal clear shots even at high magnification. The camera system is completed by the 8MP ultra-wide-angle camera, the 2MP macro camera, the 2MP depth camera and the 16MP selfie camera.',
        201.4, 84, 'Xiaomi', 'smartphone'),
       ('Unihertz Tank', 'src/main/resources/assets/Unihertz_Tank.jpeg', 'The Unihertz Tank is a cutting-edge rugged 4G smartphone that can withstand harsh environments while still retaining the incredible technology and features built into modern smartphones.',
        309.99, 11, 'Unihertz', 'smartphone'),
       ('L8STAR BM10 Mini', 'src/main/resources/assets/L8STAR_BM10_Mini.jpeg', 'A mobile with a mini and compact design, smaller than a lighter!

Equipped with a large 0.66-inch OLED screen, the L8STAR BM10 offers you the ultimate visual experience. It uses the MTK6261D processor, which makes you listen to music smoothly. Besides, dual card dual standby design combined with low radiation, it is healthy and convenient for your life. Large 350mAh capacity battery offers you a very great experience.',
        11.71, 27, 'L8STAR', 'cellphone'),
       ('Ulefone Armor Mini 2', 'src/main/resources/assets/Ulefone_Armor_Mini_2.jpeg', 'Robust and waterproof (IP68) mobile phone, with an imposing design and high-quality frame, resistant to hard use. It is equipped with a battery with a capacity of 2100mAh which offers a long autonomy and a lens.',
        40.0, 35, 'Ulefone', 'cellphone');
