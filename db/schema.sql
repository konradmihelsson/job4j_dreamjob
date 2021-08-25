CREATE TABLE cities
(
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);
CREATE TABLE post
(
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT now()
);
CREATE TABLE candidates
(
    id      SERIAL PRIMARY KEY,
    name    TEXT NOT NULL,
    city_id INT NOT NULL references cities (id),
    created TIMESTAMP NOT NULL DEFAULT now()
);
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    name     TEXT NOT NULL UNIQUE,
    email    TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);
INSERT INTO cities (name)
VALUES ('Санкт-Петербург'),
       ('Москва'),
       ('Казань'),
       ('Нижний Новгород'),
       ('Новосибирск'),
       ('Волгоград'),
       ('Владивосток'),
       ('Калининград'),
       ('Тула'),
       ('Ставрополь'),
       ('Краснодар'),
       ('Брянск'),
       ('Псков');
