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
second_name CHARACTER VARYING(255),
username CHARACTER VARYING(255),
password CHARACTER VARYING(255),
 PRIMARY KEY (id), UNIQUE (username));


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

--CREATE TABLE IF NOT EXISTS auth_user_group (
--auth_user_group_id  SERIAL NOT NULL,
--username CHARACTER VARYING (128) NOT NULL UNIQUE,
--auth_group CHARACTER VARYING (128) NOT NULL UNIQUE,
--CONSTRAINT users_auth_user_group_fk FOREIGN KEY(username) REFERENCES users(username),
--PRIMARY KEY (auth_user_group_id)
--);

CREATE TABLE IF NOT EXISTS roles (
id SERIAL PRIMARY KEY,
name CHARACTER VARYING(100),
description CHARACTER VARYING(100)
);

CREATE TABLE IF NOT EXISTS users_roles (
user_id BIGINT NOT NULL,
role_id BIGINT NOT NULL,
CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id)
)


