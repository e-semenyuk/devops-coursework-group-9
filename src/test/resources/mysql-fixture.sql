CREATE TABLE country (
    Code CHAR(3) PRIMARY KEY,
    Name VARCHAR(52) NOT NULL,
    Continent VARCHAR(30) NOT NULL,
    Region VARCHAR(26) NOT NULL,
    Population BIGINT NOT NULL,
    Capital INT
);

CREATE TABLE city (
    ID INT PRIMARY KEY,
    Name VARCHAR(35) NOT NULL,
    CountryCode CHAR(3) NOT NULL,
    District VARCHAR(20) NOT NULL,
    Population INT NOT NULL
);

INSERT INTO country (Code, Name, Continent, Region, Population, Capital)
VALUES ('GBR', 'United Kingdom', 'Europe', 'British Islands', 68000000, 1);

INSERT INTO city (ID, Name, CountryCode, District, Population)
VALUES (1, 'London', 'GBR', 'England', 9000000);
