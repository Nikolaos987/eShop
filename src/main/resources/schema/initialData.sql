/* add rows */

INSERT INTO users (username, password)
VALUES ('One'   , '1111'),
       ('Two'   , '2222'),
       ('Three' , '3333'),
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
        40.0, 35, 'Ulefone', 'cellphone'),
       ('Apple MacBook Air', 'src/main/resources/assets/Apple_MacBook_Air.jpeg', 'Υψηλές επιδόσεις χάρις τον 6πύρηνο επεξεργαστή Intel® Core™ i5 5350U 1.80 Ghz με τεχνολογία Turbo Boost χρονίζεται στα 2.90 Ghz  το σύστημα σου αλλάζει εποχή στην ταχύτητα. Τρέχει τα κυριολεκτικά τα πάντα, απροβλημάτιστα!',
        771.2, 52, 'Apple', 'laptop'),
       ('Lenovo IdeaPad 3', 'src/main/resources/assets/Lenovo_IdeaPad_3.jpeg', 'Το Lenovo IdeaPad 3 απευθύνεται σε φοιτητές και οικιακούς χρήστες που αναζητούν έναν αξιόπιστο φορητό υπολογιστή για καθημερινή χρήση. Με τον επεξεργαστή i3-1215U και την οθόνη FHD, μπορεί να χειριστεί καθημερινές εργασίες όπως τον περιηγητή ιστού, το Office, τα email, τα κοινωνικά δίκτυα και πολλά άλλα.',
        428.05, 87, 'Lenovo', 'laptop'),
       ('Lenovo IdeaPad Flex 5', 'src/main/resources/assets/Lenovo_IdeaPad_Flex_5.jpeg', 'Το Lenovo IdeaPad Flex 5 14IAU7 είναι ένα laptop με οθόνη 14 ιντσών IPS touchscreen και ανάλυση Full HD, που σημαίνει ότι προσφέρει εξαιρετική ποιότητα εικόνας. Είναι εξοπλισμένο με έναν ισχυρό επεξεργαστή i7-1255U και 16GB RAM, τα οποία επιτρέπουν την άνετη εκτέλεση πολλών εργασιών ταυτόχρονα. Έχει επίσης μια μεγάλη μονάδα αποθήκευσης 512GB SSD, η οποία προσφέρει γρήγορους χρόνους φόρτωσης και εκκίνησης του συστήματος, καθώς και αρκετό αποθηκευτικό χώρο για τα αρχεία σας.',
        998, 47, 'Lenovo', 'laptop'),
       ('HP Victus 15-fa0013nv', 'src/main/resources/assets/HP_Victus_15-fa0013nv.jpeg', 'Το HP Victus 15-fa0013nv απευθύνεται σε απαιτητικούς χρήστες που αναζητούν ένα φορητό υπολογιστή για gaming και εργασίες πολυμέσων. Πρόκειται για ένα κομψό μηχάνημα βάρους 2.29 κιλών.',
        1245, 97, 'HP', 'laptop'),
       ('Dell G16 7630', 'src/main/resources/assets/Dell_G16_7630.jpeg', 'Το Dell G16 7630 είναι ένα Laptop που προορίζεται για Gaming με οθόνη 16".

• Μοντέλο 2023
• Οθόνη 16" 2560x1600
• Core i9 13900HX-2.2GHz/Raptor Lake (13th Gen)
• Ethernet, HDMI, Thunderbolt 4, USB 3.2
• Βάρος 2.73Kg',
        2714.89, 34, 'Dell', 'laptop'),

       ('Apple Watch SE 2022', 'src/main/resources/assets/Apple_Watch_SE_2022.jpeg', 'Το Apple Watch SE είναι σχεδιασμένο για αυτούς που θέλουν ένα πιο οικονομικό και προσιτό έξυπνο ρολόι από την σειρά της Apple Watch. Απευθύνεται σε αυτούς που επιθυμούν να παρακολουθούν την υγεία τους, να λαμβάνουν ειδοποιήσεις και ενημερώσεις, να ακούν μουσική και να εκτελούν βασικές λειτουργίες όπως απάντηση σε τηλεφωνήματα και αποστολή μηνυμάτων.',
        364, 21, 'Apple', 'watch'),
       ('Samsung Galaxy Watch 6', 'src/main/resources/assets/Samsung_Galaxy_Watch6.jpeg', 'Το smartwatch που σας γνωρίζει καλύτερα επέστρεψε με μια εξατομικευμένη εμπειρία ευεξίας. Ξεκινήστε την ημέρα σας με έναν απολαυστικό ύπνο χάρη στην Παρακολούθηση ύπνου που είναι τώρα διαθέσιμη στο Galaxy Watch6.',
        664.28, 1, 'Samsung', 'watch');

