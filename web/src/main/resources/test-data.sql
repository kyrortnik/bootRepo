INSERT INTO certificates VALUES (DEFAULT,'sea weekend','weekend at the sea',1500, 90,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, 'sky diving','extreme ',500, 30,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, 'rope jumping','extreme leisure',400, 60,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, 'cruise','leisure',400, 60,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, '123','none',400, 60,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, 'trip','abroad',400, 60,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, 'long walk','sunset',400, 60,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, 'some name','none',400, 60,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, 'riding','horse',400, 60,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, 'drift','tokyo',400, 60,current_timestamp,current_timestamp);
INSERT INTO certificates VALUES (DEFAULT, '777','none',400, 60,current_timestamp,current_timestamp);

INSERT INTO tags VALUES (DEFAULT,'sky');
INSERT INTO tags VALUES (DEFAULT,'sea');
INSERT INTO tags VALUES (DEFAULT,'weekend');
INSERT INTO tags VALUES (DEFAULT,'extreme');
INSERT INTO tags VALUES (DEFAULT,'abroad');

INSERT INTO certificates_tags VALUES (1,1);
INSERT INTO certificates_tags VALUES (1,2);
INSERT INTO certificates_tags VALUES (2,1);
INSERT INTO certificates_tags VALUES (2,3);
INSERT INTO certificates_tags VALUES (3,4);
INSERT INTO certificates_tags VALUES (3,5);
--admin
INSERT INTO users VALUES (DEFAULT,'Administrator','Head','admin','$2a$12$xs3gTm9RBIMD4bCKfWIJ7eKhfcRQHW.PYoycsGOUv2lcgSk3OEmUC');
--userPassword
INSERT INTO users VALUES (DEFAULT,'John','user','userLogin','$2a$12$bopJ5gJXc9ax5ZIDEI/bsuK2H2j3JbvLmMmXab5PGhsqsSIbfaRoW');
--guestPassword
INSERT INTO users VALUES (DEFAULT,'Jane','guest','guestLogin','$2a$12$f.dcdT4icxEFJOjakpAwL.JENNBPfvHiukM4sCzPJOXfQvYt9.3Uy');


INSERT INTO roles VALUES (DEFAULT,'ROLE_ADMIN','Administrator');
INSERT INTO roles VALUES (DEFAULT,'ROLE_USER','Application user');
INSERT INTO roles VALUES (DEFAULT,'ROLE_GUEST','Application guest');

INSERT INTO users_roles VALUES (1,1);
INSERT INTO users_roles VALUES (2,2);
INSERT INTO users_roles VALUES (3,3);

