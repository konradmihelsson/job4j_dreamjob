CREATE TABLE cities
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);
CREATE TABLE IF NOT EXISTS post
(
    id      SERIAL PRIMARY KEY,
    name    TEXT,
    created TIMESTAMP DEFAULT now()
);
CREATE TABLE IF NOT EXISTS candidates
(
    id      SERIAL PRIMARY KEY,
    name    TEXT,
    city_id INT references cities (id),
    created TIMESTAMP DEFAULT now()
);
CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    name     TEXT,
    email    TEXT,
    password TEXT
);
INSERT INTO cities (name)
SELECT * FROM (SELECT 'Санкт-Петербург') AS tmp
WHERE NOT EXISTS
    (SELECT name FROM cities WHERE name = 'Санкт-Петербург') LIMIT 1;
