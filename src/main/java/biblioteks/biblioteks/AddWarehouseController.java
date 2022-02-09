package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasa obsługująca okno dodania/modyfikacji hurtowni.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku add_warehouse-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class AddWarehouseController {

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
     * Fizyczny adres hurtowni.
     */
    @FXML
    private TextField address;

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Adres e-mail hurtowni.
     */
    @FXML
    private TextField email;

    /**
     * Nazwa hurtowni.
     */
    @FXML
    private TextField title;

    /**
     * Numer telefonu hurtowni.
     */
    @FXML
    private TextField phone;

    /**
     * Zapytanie wysyłane do bazy danych.
     */
    private String sql_str = "";

    /**
     * Metoda dodająca obsługę wciśnięcia przycisku Dodaj/Zmień.
     * Metoda wysyła zapytanie do bazy danych i w zależności
     * od statusu operacji, wyświetla w widoku odpowiednią informację.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) throws IOException {
        try {
            if (sql_str.equals("")) {
                sql_str = "insert into hurtownia (hurt_email, nazwa, telefon, adres) values (?, ?, ?, ?)";
            }
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                statement.setString(1, email.getText());
                statement.setString(2, title.getText());
                statement.setString(3, phone.getText());
                statement.setString(4, address.getText());
                statement.executeUpdate();
                statement.close();
                alert_text.setText("Pomyślnie wykonano operację.");
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                alert_text.setText("Podany adres e-mail jest już zajęty.");
            } else {
                String str = e.getMessage().replace("ERROR: ", "").replace("\\n", "\n");
                str = str.substring(0, str.lastIndexOf("\n\n"));
                alert_text.setText(str);
            }
        }
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * Dodaje ona obsługę modyfikacji danej hurtowni.
     * Jeżeli użytkownik wybrał opcję modyfikacji hurtowni,
     * wówczas wszystkie pola zostają uzupełnione o odpowiednie
     * wartości.
     */
    public void initialize() {
        if (!Functions.selected_email.equals("")) {
            try {
                sql_str = "select * from hurtownia where hurt_email='" + Functions.selected_email + "'";
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    add_btn.setText("Zmień");
                    window_name.setText("ZMIEŃ HURTOWNIĘ");
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        email.setText(res.getString("hurt_email"));
                        title.setText(res.getString("nazwa"));
                        phone.setText(res.getString("telefon"));
                        address.setText(res.getString("adres"));
                    }
                    statement.close();
                    sql_str = "update hurtownia set hurt_email=?, nazwa=?, telefon=?, adres=? where hurt_email='" + Functions.selected_email + "'";
                } else {
                    alert_text.setText("Nie można połączyć się z bazą.");
                }
            } catch (SQLException e) {
                alert_text.setText(e.getMessage());
            }
            Functions.selected_email = "";
        }
    }
}
