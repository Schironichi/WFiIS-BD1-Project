package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;

/**
 * Klasa obsługująca okno dodania/modyfikacji autora.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku add_author-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class AddAuthorController {

    /**
     * Przycisk wysyłający zapytanie do bazy danych.
     */
    @FXML
    private Button add_btn;

    /**
     * Opis danego okna, znajdujący się w górnej części widoku.
     */
    @FXML
    private Label window_name;

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Data urodzenia autora.
     */
    @FXML
    private DatePicker birth;

    /**
     * Imię autora.
     */
    @FXML
    private TextField name;

    /**
     * Nazwisko autora.
     */
    @FXML
    private TextField surn;

    /**
     * Zapytanie wysyłane do bazy danych.
     */
    private String sql_str = "";

    /**
     * Metoda dodająca obsługę wciśnięcia przycisku Dodaj/Zmień.
     * Metoda sprawdza poprawność danych, po czym wysyła zapytanie do bazy
     * danych i w zależności od statusu operacji, wyświetla w widoku
     * odpowiednią informację.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) throws IOException {
        try {
            if (sql_str.equals("")) {
                sql_str = "insert into autor (imie, nazwisko, data_urodzenia) values (?, ?, ?)";
            }
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                statement.setString(1, name.getText());
                statement.setString(2, surn.getText());
                Date temp_date;
                if (birth.getValue() != null) {
                    temp_date = Date.valueOf(birth.getValue().toString());
                } else {
                    temp_date = null;
                }
                statement.setDate(3, temp_date);
                statement.executeUpdate();
                statement.close();
                alert_text.setText("Pomyślnie wykonano operację.");
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * Dodaje ona obsługę modyfikacji danego autora.
     * Jeżeli użytkownik wybrał opcję modyfikacji autora,
     * wówczas wszystkie pola zostają uzupełnione o odpowiednie
     * wartości.
     */
    public void initialize() {
        if (Functions.selected_id != 0) {
            try {
                sql_str = "select * from autor where id_autor=" + Functions.selected_id;
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    add_btn.setText("Zmień");
                    window_name.setText("ZMIEŃ AUTORA");
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        name.setText(res.getString("imie"));
                        surn.setText(res.getString("nazwisko"));
                        if (res.getDate("data_urodzenia") != null)
                            birth.setValue(res.getDate("data_urodzenia").toLocalDate());
                    }
                    statement.close();
                    sql_str = "update autor set imie =?, nazwisko=?, data_urodzenia=? where id_autor=" + Functions.selected_id;
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