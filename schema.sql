DROP TABLE IF EXISTS oop_ticket;
DROP TABLE IF EXISTS oop_customer;
DROP TABLE IF EXISTS oop_show;
DROP TABLE IF EXISTS oop_movie_theatre;
DROP TABLE IF EXISTS oop_movie_role;
DROP TABLE IF EXISTS oop_movie;
DROP TABLE IF EXISTS oop_role;
DROP TABLE IF EXISTS oop_person;

CREATE TABLE oop_person
(
    id         BIGSERIAL PRIMARY KEY,

    firstname  VARCHAR(255) NOT NULL,
    lastname   VARCHAR(255) NOT NULL,
    photo_url  VARCHAR(255) NOT NULL,
    birthday   TIMESTAMP    NOT NULL,

    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE oop_role
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
INSERT INTO oop_role(name)
VALUES ('actor');
INSERT INTO oop_role(name)
VALUES ('director');

CREATE TABLE oop_movie
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL
);

CREATE TABLE oop_movie_role
(
    person_id BIGINT REFERENCES oop_person (id),
    role_id   BIGINT REFERENCES oop_role (id),
    movie_id  BIGINT REFERENCES oop_movie (id)
);

CREATE TABLE oop_movie_theatre
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE oop_show
(
    id               BIGSERIAL PRIMARY KEY,

    movie_theatre_id BIGINT REFERENCES oop_movie_theatre (id),
    movie_id         BIGINT REFERENCES oop_movie (id),

    started_at       TIMESTAMP NOT NULL,
    duration         INT       NOT NULL,

    theatre_hall     SMALLINT  NOT NULL,
    tickets_qty      SMALLINT  NOT NULL
);

CREATE TABLE oop_customer
(
    id        BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname  VARCHAR(255) NOT NULL
);

CREATE TABLE oop_ticket
(
    customer_id BIGINT REFERENCES oop_customer (id),
    show_id     BIGINT REFERENCES oop_show (id)
);