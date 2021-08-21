CREATE TABLE post
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);
CREATE TABLE candidates
(
    id  SERIAL PRIMARY KEY,
    name TEXT
);
CREATE TABLE users
(
    id  SERIAL PRIMARY KEY,
    name    TEXT NOT NULL UNIQUE,
    email   TEXT NOT NULL UNIQUE,
    password    TEXT
);