-- Tworzenie tabel --
CREATE TABLE platnosc
(
    typ_platnosci VARCHAR PRIMARY KEY
);
CREATE TABLE hurtownia
(
    hurt_email VARCHAR PRIMARY KEY,
    nazwa      VARCHAR NOT NULL,
    adres      VARCHAR NOT NULL,
    telefon    VARCHAR NOT NULL
);
CREATE TABLE dostawa
(
    id_dostawa        SERIAL PRIMARY KEY,
    hurt_email        VARCHAR NOT NULL,
    typ_platnosci     VARCHAR NOT NULL,
    koszt             NUMERIC NOT NULL,
    data              DATE    NOT NULL,
    data_dostarczenia DATE
);
CREATE TABLE autor
(
    id_autor       SERIAL PRIMARY KEY,
    imie           VARCHAR NOT NULL,
    nazwisko       VARCHAR,
    data_urodzenia DATE
);
CREATE TABLE ksiazka
(
    id_ksiazka        SERIAL PRIMARY KEY,
    id_autor          INTEGER NOT NULL,
    tytul             VARCHAR NOT NULL,
    nazwa_gatunek     VARCHAR NOT NULL,
    opis              VARCHAR,
    nazwa_wydawnictwo VARCHAR NOT NULL,
    rok_wydania       INTEGER,
    wypozyczona_ilosc INTEGER NOT NULL
);
CREATE TABLE gatunek
(
    nazwa_gatunek VARCHAR PRIMARY KEY
);
CREATE TABLE wydawnictwo
(
    nazwa_wydawnictwo VARCHAR PRIMARY KEY
);
CREATE TABLE czas_wynajmu
(
    id_wynajmu    SERIAL PRIMARY KEY,
    id_ksiazka    INTEGER NOT NULL,
    dlugosc_najmu INTEGER NOT NULL,
    cena          NUMERIC NOT NULL
);
CREATE TABLE dostawa_ksiazka
(
    id_dostawa INTEGER NOT NULL,
    id_ksiazka INTEGER NOT NULL,
    ilosc      INTEGER NOT NULL,
    cena       NUMERIC NOT NULL
);
CREATE TABLE uzytkownik
(
    email    VARCHAR PRIMARY KEY,
    imie     VARCHAR NOT NULL,
    nazwisko VARCHAR,
    haslo    VARCHAR NOT NULL,
    telefon  VARCHAR NOT NULL,
    typ      INTEGER NOT NULL
);
CREATE TABLE rezerwacja
(
    id_rezerwacja    SERIAL PRIMARY KEY,
    email            VARCHAR NOT NULL,
    data_rezerwacji  DATE    NOT NULL,
    data_wygasniecia DATE    NOT NULL
);
CREATE TABLE rezerwacja_ksiazka
(
    id_ksiazka        INTEGER NOT NULL,
    id_rezerwacja     INTEGER NOT NULL,
    czas_wypozyczenia INTEGER NOT NULL,
    rezerwacja_ilosc  INTEGER NOT NULL
);
CREATE TABLE opinia
(
    id_opinia  SERIAL PRIMARY KEY,
    email      VARCHAR  NOT NULL,
    id_ksiazka INTEGER  NOT NULL,
    naglowek   VARCHAR  NOT NULL,
    ocena      NUMERIC NOT NULL,
    tresc      VARCHAR
);
CREATE TABLE status
(
    nazwa_status VARCHAR PRIMARY KEY
);
CREATE TABLE zamowienie
(
    id_zamowienie   SERIAL PRIMARY KEY,
    email           VARCHAR NOT NULL,
    typ_platnosci   VARCHAR NOT NULL,
    data_zamowienia DATE    NOT NULL,
    nazwa_status    VARCHAR NOT NULL
);
CREATE TABLE zamowienie_ksiazka
(
    id_ksiazka       INTEGER NOT NULL,
    id_zamowienie    INTEGER NOT NULL,
    data_zwrotu      DATE    NOT NULL,
    zamowienie_ilosc INTEGER NOT NULL
);
--
ALTER TABLE dostawa_ksiazka
    ADD PRIMARY KEY (id_dostawa, id_ksiazka);
ALTER TABLE rezerwacja_ksiazka
    ADD PRIMARY KEY (id_ksiazka, id_rezerwacja);
ALTER TABLE zamowienie_ksiazka
    ADD PRIMARY KEY (id_ksiazka, id_zamowienie);
-- Modyfikacja tabel - referencje kluczy obcych --
ALTER TABLE zamowienie
    ADD FOREIGN KEY (typ_platnosci) REFERENCES platnosc (typ_platnosci);
ALTER TABLE zamowienie
    ADD FOREIGN KEY (nazwa_status) REFERENCES status (nazwa_status);
ALTER TABLE dostawa
    ADD FOREIGN KEY (typ_platnosci) REFERENCES platnosc (typ_platnosci);
ALTER TABLE dostawa
    ADD FOREIGN KEY (hurt_email) REFERENCES hurtownia (hurt_email);
ALTER TABLE dostawa_ksiazka
    ADD FOREIGN KEY (id_dostawa) REFERENCES dostawa (id_dostawa);
ALTER TABLE ksiazka
    ADD FOREIGN KEY (id_autor) REFERENCES autor (id_autor);
ALTER TABLE ksiazka
    ADD FOREIGN KEY (nazwa_gatunek) REFERENCES gatunek (nazwa_gatunek);
ALTER TABLE ksiazka
    ADD FOREIGN KEY (nazwa_wydawnictwo) REFERENCES wydawnictwo (nazwa_wydawnictwo);
ALTER TABLE opinia
    ADD FOREIGN KEY (id_ksiazka) REFERENCES ksiazka (id_ksiazka);
ALTER TABLE zamowienie_ksiazka
    ADD FOREIGN KEY (id_ksiazka) REFERENCES ksiazka (id_ksiazka);
ALTER TABLE rezerwacja_ksiazka
    ADD FOREIGN KEY (id_ksiazka) REFERENCES ksiazka (id_ksiazka);
ALTER TABLE czas_wynajmu
    ADD FOREIGN KEY (id_ksiazka) REFERENCES ksiazka (id_ksiazka);
ALTER TABLE zamowienie
    ADD FOREIGN KEY (email) REFERENCES uzytkownik (email);
ALTER TABLE opinia
    ADD FOREIGN KEY (email) REFERENCES uzytkownik (email);
ALTER TABLE rezerwacja
    ADD FOREIGN KEY (email) REFERENCES uzytkownik (email);
ALTER TABLE rezerwacja_ksiazka
    ADD FOREIGN KEY (id_rezerwacja) REFERENCES rezerwacja (id_rezerwacja);
ALTER TABLE zamowienie_ksiazka
    ADD FOREIGN KEY (id_zamowienie) REFERENCES zamowienie (id_zamowienie);