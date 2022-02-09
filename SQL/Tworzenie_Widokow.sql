create view lista_hurtownii as
select d.hurt_email        as "E-mail",
       h.nazwa             as "Nazwa",
       h.adres             as "Adres",
       h.telefon           as "Numer telefonu",
       count(d.hurt_email) as "Ilość dostaw"
from dostawa d
         join hurtownia h on d.hurt_email = h.hurt_email
group by d.hurt_email, h.nazwa, h.adres, h.telefon;

create view lista_dostaw as
select dk.id_dostawa                       as "ID",
       d.hurt_email                        as "E-mail hurtownii",
       d.typ_platnosci                     as "Status płatności",
       d.koszt                             as "Koszt dostawy",
       sum(dk.ilosc)                       as "Ilość książek",
       sum((dk.ilosc * dk.cena) + d.koszt) as "Koszt całkowity",
       d.data                              as "Data zamówienia",
       d.data_dostarczenia                 as "Data dostarczenia"
from dostawa_ksiazka dk
         join dostawa d on dk.id_dostawa = d.id_dostawa
group by dk.id_dostawa, d.hurt_email, d.typ_platnosci, d.koszt, d.data, d.data_dostarczenia;

create view lista_autorow as
select a.id_autor          as "ID",
       a.imie              as "Imię",
       a.nazwisko          as "Nazwisko",
       a.data_urodzenia    as "Data urodzenia",
       count(k.id_ksiazka) as "Ilość różnych książek w systemie"
from autor a
         join ksiazka k on a.id_autor = k.id_autor
group by a.id_autor
order by a.id_autor;

create view lista_dk as
select dk.id_dostawa as "ID",
       k.tytul       as "Tytuł książki",
       dk.ilosc      as "Ilość książek",
       dk.cena       as "Cena za sztukę"
from dostawa_ksiazka dk
         join ksiazka k on k.id_ksiazka = dk.id_ksiazka
order by dk.id_dostawa;

create view lista_cen_ksiazki as
select cw.id_wynajmu    as "ID",
       k.tytul          as "Tytuł książki",
       cw.cena          as "Cena za wypożyczenie",
       cw.dlugosc_najmu as "Długość wypożyczenia (dni)"
from czas_wynajmu cw
         join ksiazka k on k.id_ksiazka = cw.id_ksiazka
order by cw.id_ksiazka, cw.id_wynajmu;

create view lista_ksiazek_pracownik as
select k.id_ksiazka                  as "ID",
       (a.imie || ' ' || a.nazwisko) as "Autor",
       k.tytul                       as "Tytuł",
       k.nazwa_gatunek               as "Gatunek",
       k.nazwa_wydawnictwo           as "Wydawnictwo",
       k.rok_wydania                 as "Rok wydania",
       k.wypozyczona_ilosc           as "Wypożyczona ilość",
       sum(dk.ilosc)                 as "Całkowita ilość"
from ksiazka k
         join autor a on k.id_autor = a.id_autor
         join dostawa_ksiazka dk on k.id_ksiazka = dk.id_ksiazka
group by k.id_ksiazka, a.imie, a.nazwisko
order by k.id_ksiazka;

create view wybor_ksiazek as
select k.id_ksiazka,
       k.tytul,
       a.nazwisko
from ksiazka k
         join autor a on k.id_autor = a.id_autor
group by k.id_ksiazka, a.nazwisko
order by k.id_ksiazka;

create view lista_ksiazek_uzytkownik as
SELECT k.id_ksiazka                                    AS "ID",
       (a.imie::text || ' '::text) || a.nazwisko::text AS "Autor",
       k.tytul                                         AS "Tytuł",
       k.nazwa_gatunek                                 AS "Gatunek",
       k.nazwa_wydawnictwo                             AS "Wydawnictwo",
       k.rok_wydania                                   as "Rok wydania",
       k.opis                                          as "Opis",
       sum(dk.ilosc) - k.wypozyczona_ilosc             AS "Dostępna ilość"
FROM ksiazka k
         JOIN autor a ON k.id_autor = a.id_autor
         JOIN dostawa_ksiazka dk ON k.id_ksiazka = dk.id_ksiazka
GROUP BY k.id_ksiazka, a.imie, a.nazwisko
ORDER BY k.id_ksiazka;

create view lista_opinii as
select u.imie     as "Autor",
       o.naglowek as "Tytuł",
       o.ocena    as "Ocena",
       o.tresc    as "Szczegóły"
from opinia o
         join uzytkownik u on u.email = o.email
order by o.ocena desc;

create view lista_zamowien as
select id_zamowienie   as "ID",
       email           as "E-mail",
       typ_platnosci   as "Płatność",
       data_zamowienia as "Data zamówienia",
       nazwa_status    as "Status zamówienia"
from zamowienie
order by id_zamowienie;

create view lista_rezerwacji as
select id_rezerwacja    as "ID",
       email            as "E-mail",
       data_rezerwacji  as "Data rezerwacji",
       data_wygasniecia as "Data wygaśnięcia"
from rezerwacja
order by id_rezerwacja;