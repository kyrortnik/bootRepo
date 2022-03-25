CREATE TABLE  IF NOT EXISTS certificates(
id SERIAL NOT NULL,
name CHARACTER VARYING(255) NOT NULL,
description CHARACTER VARYING(255),
price NUMERIC(20,2),
duration INTEGER,
create_date TIMESTAMP(6) WITHOUT TIME ZONE,
last_update_date TIMESTAMP(6) WITHOUT TIME ZONE,
PRIMARY KEY (id), UNIQUE (name));


CREATE TABLE  IF NOT EXISTS tags (
id SERIAL NOT NULL,
name CHARACTER VARYING(50) NOT NULL,
PRIMARY KEY (id), UNIQUE (name));


CREATE TABLE  IF NOT EXISTS users (
id SERIAL NOT NULL,
first_name CHARACTER VARYING(255),
second_name CHARACTER VARYING(255), PRIMARY KEY (id));


CREATE TABLE  IF NOT EXISTS orders (
id SERIAL NOT NULL,
order_date TIMESTAMP(6) WITHOUT TIME ZONE,
order_cost NUMERIC(20,2),
user_id INTEGER NOT NULL,
gift_certificate_id INTEGER NOT NULL,
PRIMARY KEY (id),
CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users(id),
CONSTRAINT fk_gift_certificate FOREIGN KEY (gift_certificate_id) REFERENCES certificates(id));


CREATE TABLE  IF NOT EXISTS certificates_tags (
certificate_id BIGINT NOT NULL,
tag_id BIGINT NOT NULL,
PRIMARY KEY (certificate_id, tag_id),
CONSTRAINT fk_certificates FOREIGN KEY (certificate_id) REFERENCES certificates(id),
CONSTRAINT fk_tags FOREIGN KEY (tag_id) REFERENCES tags(id));


CREATE TABLE IF NOT EXISTS revinfo (
rev SERIAL NOT NULL,
revtstmp BIGINT,
PRIMARY KEY (rev));


CREATE TABLE  IF NOT EXISTS orders_aud (
id INTEGER NOT NULL,
rev INTEGER NOT NULL,
revtype SMALLINT,
order_cost DOUBLE PRECISION,
order_date TIMESTAMP(6) WITHOUT TIME ZONE,
gift_certificate_id BIGINT,
user_id BIGINT,
PRIMARY KEY (id, rev),
CONSTRAINT fke27yw2ep0cm2h5hin5fipu55h FOREIGN KEY (rev) REFERENCES revinfo(rev));


CREATE TABLE  IF NOT EXISTS tags_aud (
id INTEGER NOT NULL,
rev INTEGER NOT NULL,
revtype SMALLINT,
name CHARACTER VARYING(255),
PRIMARY KEY (id, rev),
CONSTRAINT fk80n0rnnao71nirkhxe2dqowkp FOREIGN KEY (rev) REFERENCES revinfo(rev));


CREATE TABLE  IF NOT EXISTS users_aud (
id INTEGER NOT NULL,
rev INTEGER NOT NULL,
revtype SMALLINT,
first_name CHARACTER VARYING(255),
second_name CHARACTER VARYING(255),
PRIMARY KEY (id, rev),
CONSTRAINT fkl1mf0jaesfny93lglupp17d9u FOREIGN KEY (rev) REFERENCES revinfo(rev));


CREATE TABLE  IF NOT EXISTS certificates_aud (
id INTEGER NOT NULL,
rev INTEGER NOT NULL,
revtype SMALLINT,
create_date TIMESTAMP(6) WITHOUT TIME ZONE,
description CHARACTER VARYING(255),
duration BIGINT,
last_update_date TIMESTAMP(6) WITHOUT TIME ZONE,
name CHARACTER VARYING(255), price BIGINT,
PRIMARY KEY (id, rev),
CONSTRAINT fkhkf1i50y3qmebf0u9a78nvjhv FOREIGN KEY (rev) REFERENCES revinfo(rev));


CREATE TABLE  IF NOT EXISTS certificates_tags_aud (
rev INTEGER NOT NULL,
certificate_id BIGINT NOT NULL,
tag_id BIGINT NOT NULL, revtype SMALLINT,
PRIMARY KEY (rev, certificate_id, tag_id),
CONSTRAINT fkcn55lojfj1opraxg1kaet6qv8 FOREIGN KEY (rev) REFERENCES revinfo(rev));

