/* add rows */

INSERT INTO users (username, password)
VALUES ('One'   , '1111'),
       ('Two'   , '2222'),
       ('Three' , '3333'),
       ('Four'  , '1234'),
       ('Five'  , '12345');

INSERT INTO cart (uid)
SELECT uid FROM users;

INSERT INTO product (pid, name, description, price, quantity, brand, category)
VALUES ('632b9f06-0262-4afd-860f-2469be41e244', 'Samsung Galaxy S23 Ultra', 'Galaxy S23 Ultra owns the latest processor, which is the Qualcomm SM8550-AC Snapdragon 8 Gen 2 (4 nm) processor. It is capable of multi-tasking, streaming videos, running even the most demanding games with ease. The phone comes with various memory variations, up to 1TB and 12GB RAM (Please check specification table).',
        1065.0, 35, 'Samsung', 'smartphone'),
       ('56ecd554-3b4a-47c7-a74d-3593261ccd00', 'Xiaomi Poco X5 Pro', 'Snapdragon® 778G processor 120Hz FHD+ AMOLED DotDisplay 108MP pro-grade main camera 67W turbo charging',
        336.94, 17, 'Xiaomi', 'smartphone'),
       ('06c79864-a520-4751-b280-389f05744079', 'Nokia 105', 'The Nokia 105 comes with a classic, ergonomic design to fit your hands better. In addition, the 1.8" QVGA screen offers better viewing, while the keyboard with separate keys makes it easier to write messages and make calls.',
        31.0, 62, 'Nokia', 'cellphone'),
       ('ab1a8d6d-4bc8-4472-893f-773a7af0f633', 'Xiaomi Redmi Note 11S', 'The smartphone is equipped with an AI quad camera system, with a powerful 108MP main camera for crystal clear shots even at high magnification. The camera system is completed by the 8MP ultra-wide-angle camera, the 2MP macro camera, the 2MP depth camera and the 16MP selfie camera.',
        201.4, 84, 'Xiaomi', 'smartphone'),
       ('977978e0-961c-41fb-aab7-a9ef9bb9da16', 'Unihertz Tank', 'The Unihertz Tank is a cutting-edge rugged 4G smartphone that can withstand harsh environments while still retaining the incredible technology and features built into modern smartphones.',
        309.99, 11, 'Unihertz', 'smartphone'),
       ('369fa38f-7fa2-450e-85cb-fd53a7358d7a', 'L8STAR BM10 Mini', 'A mobile with a mini and compact design, smaller than a lighter!

Equipped with a large 0.66-inch OLED screen, the L8STAR BM10 offers you the ultimate visual experience. It uses the MTK6261D processor, which makes you listen to music smoothly. Besides, dual card dual standby design combined with low radiation, it is healthy and convenient for your life. Large 350mAh capacity battery offers you a very great experience.',
        11.71, 27, 'L8STAR', 'cellphone'),
       ('212343b2-3869-4707-99ca-5eb3a5a25bcf', 'Ulefone Armor Mini 2', 'Robust and waterproof (IP68) mobile phone, with an imposing design and high-quality frame, resistant to hard use. It is equipped with a battery with a capacity of 2100mAh which offers a long autonomy and a lens.',
        40.0, 35, 'Ulefone', 'cellphone'),
       ('a9a73be2-c166-43d0-8651-7b10b206f6d6', 'Apple MacBook Air', 'Υψηλές επιδόσεις χάρις τον 6πύρηνο επεξεργαστή Intel® Core™ i5 5350U 1.80 Ghz με τεχνολογία Turbo Boost χρονίζεται στα 2.90 Ghz  το σύστημα σου αλλάζει εποχή στην ταχύτητα. Τρέχει τα κυριολεκτικά τα πάντα, απροβλημάτιστα!',
        771.2, 52, 'Apple', 'laptop'),
       ('a5979c00-f262-4d2f-a14d-60b6dbeb1310', 'Lenovo IdeaPad 3', 'Το Lenovo IdeaPad 3 απευθύνεται σε φοιτητές και οικιακούς χρήστες που αναζητούν έναν αξιόπιστο φορητό υπολογιστή για καθημερινή χρήση. Με τον επεξεργαστή i3-1215U και την οθόνη FHD, μπορεί να χειριστεί καθημερινές εργασίες όπως τον περιηγητή ιστού, το Office, τα email, τα κοινωνικά δίκτυα και πολλά άλλα.',
        428.05, 87, 'Lenovo', 'laptop'),
       ('1b91744f-5e17-4763-9109-f9c676b9d55e', 'Lenovo IdeaPad Flex 5', 'Το Lenovo IdeaPad Flex 5 14IAU7 είναι ένα laptop με οθόνη 14 ιντσών IPS touchscreen και ανάλυση Full HD, που σημαίνει ότι προσφέρει εξαιρετική ποιότητα εικόνας. Είναι εξοπλισμένο με έναν ισχυρό επεξεργαστή i7-1255U και 16GB RAM, τα οποία επιτρέπουν την άνετη εκτέλεση πολλών εργασιών ταυτόχρονα. Έχει επίσης μια μεγάλη μονάδα αποθήκευσης 512GB SSD, η οποία προσφέρει γρήγορους χρόνους φόρτωσης και εκκίνησης του συστήματος, καθώς και αρκετό αποθηκευτικό χώρο για τα αρχεία σας.',
        998, 47, 'Lenovo', 'laptop'),
       ('3dc80070-2032-400b-8585-2ef5e89be0d7', 'HP Victus 15-fa0013nv', 'Το HP Victus 15-fa0013nv απευθύνεται σε απαιτητικούς χρήστες που αναζητούν ένα φορητό υπολογιστή για gaming και εργασίες πολυμέσων. Πρόκειται για ένα κομψό μηχάνημα βάρους 2.29 κιλών.',
        1245, 97, 'HP', 'laptop'),
       ('0be075be-89c3-4846-b7a8-5f85f0d24a77', 'Dell G16 7630', 'Το Dell G16 7630 είναι ένα Laptop που προορίζεται για Gaming με οθόνη 16".

• Μοντέλο 2023
• Οθόνη 16" 2560x1600
• Core i9 13900HX-2.2GHz/Raptor Lake (13th Gen)
• Ethernet, HDMI, Thunderbolt 4, USB 3.2
• Βάρος 2.73Kg',
        2714.89, 34, 'Dell', 'laptop'),

       ('df316f1f-bdd6-45c9-b03b-e3912db9b8b5', 'Apple Watch SE 2022', 'Το Apple Watch SE είναι σχεδιασμένο για αυτούς που θέλουν ένα πιο οικονομικό και προσιτό έξυπνο ρολόι από την σειρά της Apple Watch. Απευθύνεται σε αυτούς που επιθυμούν να παρακολουθούν την υγεία τους, να λαμβάνουν ειδοποιήσεις και ενημερώσεις, να ακούν μουσική και να εκτελούν βασικές λειτουργίες όπως απάντηση σε τηλεφωνήματα και αποστολή μηνυμάτων.',
        364, 21, 'Apple', 'watch'),
       ('f01a8462-1211-402c-bfe9-8c8799997013', 'Samsung Galaxy Watch 6', 'Το smartwatch που σας γνωρίζει καλύτερα επέστρεψε με μια εξατομικευμένη εμπειρία ευεξίας. Ξεκινήστε την ημέρα σας με έναν απολαυστικό ύπνο χάρη στην Παρακολούθηση ύπνου που είναι τώρα διαθέσιμη στο Galaxy Watch6.',
        664.28, 1, 'Samsung', 'watch');

