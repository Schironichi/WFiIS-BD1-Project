create or replace function sprawdzDaneUzytkownika()
    returns trigger
    language plpgsql
as
$$
declare
    tekst varchar := '';
begin
    if (new.email = '') is not false then
        tekst := tekst || 'Nie podano adresu e-mail.\n';
    end if;
    if new.email not like '%@%.%' and not (new.email = '') is not false then
        tekst := tekst || 'Niepoprawny adres e-mail.\n';
    end if;
    if char_length(new.haslo) < 8 then
        tekst := tekst || 'Hasło musi mieć przynajmniej 8 znaków.\n';
    end if;
    if (new.imie = '') is not false then
        tekst := tekst || 'Nie podano imienia.\n';
    end if;
    if (new.telefon = '') is not false then
        tekst := tekst || 'Nie podano numeru telefonu.\n';
    end if;
    if not (tekst = '') is not false then
        raise exception '%', tekst;
    end if;
    return new;
end;
$$;

create trigger daneUzytkownika
    before insert or update
    on uzytkownik
    for each row
execute procedure sprawdzDaneUzytkownika();

create or replace function sprawdzDaneHurtowni()
    returns trigger
    language plpgsql
as
$$
declare
    tekst varchar := '';
begin
    if (new.hurt_email = '') is not false then
        tekst := tekst || 'Nie podano adresu e-mail.\n';
    end if;
    if new.hurt_email not like '%@%.%' and not (new.hurt_email = '') is not false then
        tekst := tekst || 'Niepoprawny adres e-mail.\n';
    end if;
    if (new.nazwa = '') is not false then
        tekst := tekst || 'Nie podano nazwy hurtowni.\n';
    end if;
    if (new.telefon = '') is not false then
        tekst := tekst || 'Nie podano numeru telefonu.\n';
    end if;
    if (new.adres = '') is not false then
        tekst := tekst || 'Nie podano adresu.\n';
    end if;
    if not (tekst = '') is not false then
        raise exception '%', tekst;
    end if;
    return new;
end;
$$;

create trigger daneHurtowni
    before insert or update
    on hurtownia
    for each row
execute procedure sprawdzDaneHurtowni();

create or replace function oblicz(ilosc integer, cena numeric)
    returns numeric
    language plpgsql
as
$$
begin
    return ilosc * cena;
end;
$$;

create or replace function funkcja_opinii(int)
    returns setof lista_opinii as
$$
select u.imie     as "Autor",
       o.naglowek as "Tytuł",
       o.ocena    as "Ocena",
       o.tresc    as "Szczegóły"
from opinia o
         join uzytkownik u on u.email = o.email
where o.id_ksiazka = $1
$$
    language sql;

create or replace function sprawdzDaneOpinii()
    returns trigger
    language plpgsql
as
$$
declare
    tekst varchar := '';
begin
    if new.id_ksiazka is null then
        tekst := tekst || 'Nie wybrano książki.\n';
    end if;
    if (new.naglowek = '') is not false then
        tekst := tekst || 'Nie podano nagłówka.\n';
    end if;
    if (new.email = '') is not false then
        tekst := tekst || 'Sesja wygasła. Proszę się zalogować.\n';
    end if;
    if not (tekst = '') is not false then
        raise exception '%', tekst;
    end if;
    return new;
end;
$$;

create trigger daneOpinii
    before insert or update
    on opinia
    for each row
execute procedure sprawdzDaneOpinii();

create or replace function rezerwujFunkcja()
    returns trigger as
$$
begin
    update ksiazka set wypozyczona_ilosc = wypozyczona_ilosc + new.rezerwacja_ilosc where id_ksiazka = new.id_ksiazka;
    return new;
end;
$$
    language plpgsql;

create trigger rezerwuj_ksiazki
    after insert or update
    on rezerwacja_ksiazka
    for each row
execute procedure rezerwujFunkcja();

create or replace function sprawdzIloscRezerwacji()
    returns trigger as
$$
declare
    var bigint := liczba_ksiazek(new.rezerwacja_ilosc, new.id_ksiazka);
begin
    if (var < 0) then
        raise exception 'Wybrano za dużo książek!';
    end if;
    return new;
end;
$$
    language plpgsql;

create or replace function liczba_ksiazek(int, int)
    returns bigint
    language sql as
$$
select sum(dk.ilosc) - k.wypozyczona_ilosc - $1
from ksiazka k
         join autor a on k.id_autor = a.id_autor
         join dostawa_ksiazka dk on k.id_ksiazka = dk.id_ksiazka
where k.id_ksiazka = $2
group by k.wypozyczona_ilosc
$$;

create trigger sprawdz_ilosc_rezerwacji
    before insert or update
    on rezerwacja_ksiazka
    for each row
execute procedure sprawdzIloscRezerwacji();

create or replace function zamowFunkcja()
    returns trigger as
$$
begin
    update ksiazka set wypozyczona_ilosc = wypozyczona_ilosc + new.zamowienie_ilosc where id_ksiazka = new.id_ksiazka;
    return new;
end;
$$
    language plpgsql;

create trigger zamow_ksiazki
    after insert
    on zamowienie_ksiazka
    for each row
execute procedure zamowFunkcja();

create or replace function usunRezerwacje()
    returns trigger
    language 'plpgsql' as
$$
begin
    delete from rezerwacja_ksiazka where id_rezerwacja = old.id_rezerwacja;
    return old;
end;
$$;

create or replace function usunRezerwacjaKsiazka()
    returns trigger
    language 'plpgsql' as
$$
begin
    update ksiazka
    set wypozyczona_ilosc = wypozyczona_ilosc - old.rezerwacja_ilosc
    where id_ksiazka = old.id_ksiazka;
    return old;
end;
$$;

create trigger usun_rezerwacje_ksiazke
    before delete
    on rezerwacja_ksiazka
    for each row
execute procedure usunRezerwacjaKsiazka();

create trigger usun_rezerwacje
    before delete
    on rezerwacja
    for each row
execute procedure usunRezerwacje();

create or replace function zwrocKsiazki()
    returns trigger
    language 'plpgsql' as
$$
begin
    if new.nazwa_status = 'Oddane' and old.nazwa_status <> 'Oddane' then
        update ksiazka
        set wypozyczona_ilosc = wypozyczona_ilosc - temp.zamowienie_ilosc
        from (
                 select k.id_ksiazka, zk.zamowienie_ilosc
                 from ksiazka k
                          join zamowienie_ksiazka zk on k.id_ksiazka = zk.id_ksiazka
                 where zk.id_zamowienie = new.id_zamowienie
             ) as temp
        where ksiazka.id_ksiazka = temp.id_ksiazka;
    else
        if new.nazwa_status <> 'Oddane' and old.nazwa_status = 'Oddane' then
            update ksiazka
            set wypozyczona_ilosc = wypozyczona_ilosc + temp.zamowienie_ilosc
            from (
                     select k.id_ksiazka, zk.zamowienie_ilosc
                     from ksiazka k
                              join zamowienie_ksiazka zk on k.id_ksiazka = zk.id_ksiazka
                     where zk.id_zamowienie = new.id_zamowienie
                 ) as temp
            where ksiazka.id_ksiazka = temp.id_ksiazka;
        end if;
    end if;
    return new;
end;
$$;

create trigger zwroc_ksiazki
    before update
    on zamowienie
    for each row
execute procedure zwrocKsiazki();

create or replace function przeniesRezerwacje(int, int)
    returns bool
    language 'plpgsql' as
$$
begin
    insert into zamowienie_ksiazka(id_ksiazka, id_zamowienie, data_zwrotu, zamowienie_ilosc)
    select id_ksiazka, $1, current_date + czas_wypozyczenia, rezerwacja_ilosc
    from rezerwacja_ksiazka
    where id_rezerwacja = $2;
    delete from rezerwacja_ksiazka where id_rezerwacja = $2;
    return true;
end;
$$;

create or replace function funkcjaRezerwacji(varchar)
    returns setof lista_rezerwacji as
$$
select id_rezerwacja    as "ID",
       email            as "E-mail",
       data_rezerwacji  as "Data rezerwacji",
       data_wygasniecia as "Data wygaśnięcia"
from rezerwacja
where email = $1
order by id_rezerwacja;
$$
    language sql;

create or replace function funkcjaZamowien(varchar)
    returns setof lista_zamowien as
$$
select id_zamowienie   as "ID",
       email           as "E-mail",
       typ_platnosci   as "Płatność",
       data_zamowienia as "Data zamówienia",
       nazwa_status    as "Status zamówienia"
from zamowienie
where email = $1
order by id_zamowienie;
$$
    language sql;

create or replace function funkcja_opinii(int)
returns setof lista_opinii as
    $$
    select u.imie     as "Autor",
       o.naglowek as "Tytuł",
       o.ocena    as "Ocena",
       o.tresc    as "Szczegóły"
from opinia o
         join uzytkownik u on u.email = o.email
where o.id_ksiazka = $1
    $$
language sql;