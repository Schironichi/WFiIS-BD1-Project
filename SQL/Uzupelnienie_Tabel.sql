insert into platnosc
values ('Karta'),
       ('Gotówka'),
       ('Online'),
       ('Niezapłacone');

insert into uzytkownik (email, imie, haslo, telefon, typ)
values ('admin', 'Administrator', 'admin', '000000000', 1);
insert into uzytkownik (email, imie, nazwisko, haslo, telefon, typ)
values ('test1@o2.pl', 'Czytelnik 1', 'Czytelny', 'test', '123456789', 0),
       ('test2@o2.pl', 'Czytelnik 2', '', 'test', '234567890', 0),
       ('test3@o2.pl', 'Czytelnik 3', '', 'test', '345678901', 0),
       ('test4@o2.pl', 'Czytelnik 4', 'Czytelny', 'test', '456789012', 0),
       ('test5@o2.pl', 'Czytelnik 5', 'Czytelny', 'test', '567890123', 0);

insert into hurtownia (hurt_email, nazwa, adres, telefon)
values ('hurt1@wp.pl', 'AdamHURT', 'Hurtowa 1 Kunów', '123123123'),
       ('hurt2@wp.pl', 'BartHURT', 'Hurtownicza 3 Kielce', '234234234'),
       ('hurt3@wp.pl', 'CelaHURT', 'Robocza 5 Kraków', '345345345'),
       ('hurt4@wp.pl', 'DawidHURT', 'Prawcownicza 4 Gdańsk', '456456456'),
       ('hurt5@wp.pl', 'ElaHURT', 'Hurtowa 8 Opole', '567567567');

insert into status
values ('Złożone'),
       ('Wypożyczone'),
       ('Oddane'),
       ('Opóźnione');

insert into autor (imie, nazwisko, data_urodzenia)
values ('Remigusz', 'Mróz', '1987-01-15'),
       ('Holly', 'Black', '1971-11-20'),
       ('Sarah J.', 'Mass', '1986-03-05'),
       ('John', 'Flanagan', '1944-05-22'),
       ('Alice', 'Oseman', '1994-10-16');

insert into gatunek
values ('młodzieżowa'),
       ('fantasy'),
       ('science-fiction'),
       ('thriller');

insert into wydawnictwo
values ('Jaguar'),
       ('Filia'),
       ('Uroboros');

insert into dostawa (hurt_email, typ_platnosci, koszt, data, data_dostarczenia)
values ('hurt2@wp.pl', 'Gotówka', '49.99', '2022-01-01', '2022-01-22'),
       ('hurt2@wp.pl', 'Gotówka', '99.99', '2022-01-05', '2022-01-18'),
       ('hurt5@wp.pl', 'Karta', '69.99', '2022-01-11', '2022-01-26');

insert into dostawa (hurt_email, typ_platnosci, koszt, data)
values ('hurt1@wp.pl', 'Niezapłacone', '54.99', '2022-01-30');

insert into ksiazka (id_autor, tytul, nazwa_gatunek, opis, nazwa_wydawnictwo, rok_wydania, wypozyczona_ilosc)
values (1, 'Projekt Riese', 'thriller', 'Nic nie jest ostateczne. Nawet śmierć.', 'Filia', '2022', 0),
       (2, 'Okrutny książę. Tom 1', 'science-fiction', 'Pierwszy tom trylogii. Historia siedemnastoletniej dziewczyny, która została porwana do Elysium, świata elfów, i będzie musiała poradzić sobie w tej niebezpiecznej krainie.', 'Jaguar', '2018', 0),
       (3, 'Dwór cierni i róż. Tom 1', 'fantasy', 'Autorka bestsellerowej serii "Szklany tron" powraca z porywającą opowieścią o miłości, która jest w stanie pokonać nienawiść i uprzedzenia!', 'Uroboros', '2016', 0),
       (3, 'Dwór mgieł i furii. Dwór cierni i róż. Tom 2', 'fantasy', 'Między światłem a ciemnością rozgrywa się walka, w której stawką są losy całego świata. A w magicznym świecie Fae przyjaciele potrafią być bardziej niebezpieczni niż wrogowie…', 'Uroboros', '2017', 0),
       (4, 'Ruiny Gorlanu. Zwiadowcy. Tom 1', 'fantasy', 'Pierwszy tom jednej z najpoczytniejszych serii fantasy na świecie.', 'Jaguar', '2021', 0),
       (4, 'Płonący most. Zwiadowcy. Tom 2', 'fantasy', 'Drugi tom serii o przygodach Willa i jego przyjaciół.', 'Jaguar', '2021', 0),
       (4, 'Ziemia skuta lodem. Zwiadowcy. Tom 3', 'fantasy', 'Trzeci tom bestsellerowej sagi o Korpusie Zwiadowców.', 'Jaguar', '2021', 0),
       (5, 'Heartstopper. Tom 1', 'młodzieżowa', 'Powieść graficzna LGBTQ+ o życiu, miłości i wszystkim, co dzieje się pomiędzy – gratka dla fanów filmu Twój Simon i książek Holly Bourne.', 'Jaguar', '2021', 0);

insert into czas_wynajmu (id_ksiazka, dlugosc_najmu, cena)
values (1, 30, 9.99),
       (1, 60, 17.49),
       (2, 30, 8.99),
       (2, 60, 16.99),
       (3, 30, 10.99),
       (3, 60, 18.49),
       (4, 30, 11.99),
       (4, 60, 19.99),
       (5, 30, 7.99),
       (5, 60, 11.99),
       (6, 30, 8.99),
       (6, 60, 15.99),
       (7, 30, 11.99),
       (7, 60, 21.99),
       (8, 30, 16.99),
       (8, 60, 24.99);

insert into dostawa_ksiazka (id_dostawa, id_ksiazka, ilosc, cena)
values (1, 5, 5, 6.99),
       (1, 6, 6, 5.99),
       (1, 7, 7, 7.99),
       (1, 2, 8, 9.99),
       (2, 1, 9, 11.99),
       (2, 3, 10, 10.99),
       (2, 4, 11, 4.99),
       (3, 2, 12, 7.99);

insert into opinia (email, id_ksiazka, naglowek, ocena, tresc)
values ('test1@o2.pl', 1, 'Super', 4.5, 'Bardzo mi się podoba.'),
       ('test2@o2.pl', 1, 'Mega', 5.0, 'Moja ulubiona książka'),
       ('test3@o2.pl', 2, 'Średnia', 3.0, ''),
       ('test4@o2.pl', 4, 'Okej', 3.5, 'Może być'),
       ('test5@o2.pl', 6, 'Ujdzie', 3.0, 'Mogła być lepsza'),
       ('test1@o2.pl', 7, 'Fajna', 4.0, '');