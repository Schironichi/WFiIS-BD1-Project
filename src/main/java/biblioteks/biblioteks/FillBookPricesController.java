package biblioteks.biblioteks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.FloatStringConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasa obsługująca uzupełnienie opcji wypożyczenia danej książki.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku fill_book_prices-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class FillBookPricesController {

    /**
     * Przycisk wysyłający zapytanie do bazy danych.
     */
    @FXML
    private Button add_btn;

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Lista książek dostępnych w bazie danych.
     */
    @FXML
    private ComboBox<?> book_list;

    /**
     * Długość wypożyczenia książki (w dniach)
     */
    @FXML
    private TextField lease;

    /**
     * Cena za daną opcję wypożyczenia.
     */
    @FXML
    private TextField price;

    /**
     * Opis danego okna, znajdujący się w górnej części widoku.
     */
    @FXML
    private Label window_name;

    /**
     * Zapytanie wysyłane do bazy danych.
     */
    private String sql_str = "";

    /**
     * Metoda dodaje obsługę wciśnięcia przycisku Dodaj.
     * Metoda łączy się z bazą danych, po czym sprawdza poprawność
     * wprowadzonych danych. Jeżeli wszystko zostało poprawnie uzupełnione,
     * dane zostają wysłane do bazy.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) {
        try {
            if(sql_str.equals("")) {
                sql_str = "insert into czas_wynajmu (id_ksiazka, dlugosc_najmu, cena) values (?, ?, ?)";
            }
            Connection conn = PGSQL.makeConnection();
            String text = "";
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                if (book_list.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano książki.\n";
                } else {
                    String temp = book_list.getSelectionModel().getSelectedItem().toString();
                    temp = temp.substring(0, temp.indexOf(':'));
                    statement.setInt(1, Integer.parseInt(temp));
                }
                if (lease.getText().equals("")) {
                    text += "Nie podano czasu najmu.\n";
                } else {
                    try {
                        if (lease.getText().charAt(0) == '-') {
                            text += "Podano nieprawidłową ilość dni.\n";
                        } else {
                            statement.setInt(2, Integer.parseInt(lease.getText()));
                        }
                    } catch (Exception e) {
                        text += "Podano nieprawidłową ilość dni.\n";
                    }
                }
                if (price.getText().equals("")) {
                    text += "Nie podano kosztu.\n";
                } else {
                    try {
                        if (price.getText().charAt(0) == '-') {
                            text += "Podano nieprawidłową kwotę.\n";
                        } else {
                            statement.setFloat(3, new FloatStringConverter().fromString(price.getText()));
                        }
                    } catch (Exception e) {
                        text += "Podano nieprawidłową kwotę.\n";
                    }
                }
                if (text.equals("")) {
                    statement.executeUpdate();
                    alert_text.setText("Pomyślnie uzupełniono zamówienie");
                } else {
                    alert_text.setText(text);
                }
                statement.close();
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda dodaje obsługę wyświetlenia listy dostępnych książek.
     * Metoda łączy się z bazą danych, po czym uzupełnia listę książek
     * o wszystkie istniejące obiekty tego typu w bazie danych.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onBookListSelected(MouseEvent event) {
        try {
            String temp_sql_str = "select k.id_ksiazka, k.tytul, a.nazwisko from ksiazka k join autor a on k.id_autor = a.id_autor group by k.id_ksiazka, a.nazwisko order by k.id_ksiazka";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql_str);
                ResultSet res = statement.executeQuery();
                int id;
                String title;
                String surn;
                ObservableList books = FXCollections.observableArrayList();
                while (res.next()) {
                    id = res.getInt("id_ksiazka");
                    title = res.getString("tytul");
                    surn = res.getString("nazwisko");
                    books.add(id + ": " + title + " (" + surn + ")");
                }
                statement.close();
                book_list.setItems(books);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda dodaje obsługę przycisku dodającego nową książkę.
     * Metoda otwiera nowe okno w którym pracownik może dodać
     * nową książkę do bazy danych (add_book-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onNewBookPressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("add_book-view.fxml")), "BiblioteKS - nowa książka");
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * Dzięki tej metodzie, w przypadku gdy pracownik
     * wybrał opcję modyfikowania ceny wypożyczenia książki,
     * będzie mógł to zrobić. W takiej sytuacji do bazy
     * danych zostanie wysłane zapytanie UPDATE.
     */
    public void initialize() {
        if (Functions.selected_id != 0) {
            try {
                sql_str = "select * from czas_wynajmu where id_wynajmu=" + Functions.selected_id;
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    add_btn.setText("Zmień");
                    window_name.setText("ZMIEŃ CENĘ KSIĄŻKI");
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        lease.setText(res.getString("dlugosc_najmu"));
                        price.setText(res.getString("cena"));
                    }
                    statement.close();
                    sql_str = "update czas_wynajmu set id_ksiazka=?, dlugosc_najmu=?, cena=? where id_wynajmu=" + Functions.selected_id;
                } else {
                    alert_text.setText("Nie można połączyć się z bazą.");
                }
            } catch (SQLException e) {
                alert_text.setText(e.getMessage());
            }
            Functions.selected_id = 0;
        }
    }
}
