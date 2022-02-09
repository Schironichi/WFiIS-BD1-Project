package biblioteks.biblioteks;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasa kontrolująca widok profilu użytkownika.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku profile-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class ProfileController {

    /**
     * Tabela zawierąca wszystkie rezerwacje.
     */
    @FXML
    private TableView<ObservableList> bokings_table;

    /**
     * Adres e-mail użytkownika.
     */
    @FXML
    private Label mail;

    /**
     * Imię (oraz nazwisko) użytkownika.
     */
    @FXML
    private Label name;

    /**
     * Tabela zawierąca wszystkie zamówienia.
     */
    @FXML
    private TableView<ObservableList> orders_table;

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Numer telefonu użytkownika.
     */
    @FXML
    private Label phone;

    /**
     * Metoda inicjalizująca kontroler. Jeżeli użytkownik jest
     * zalogowany, jego najważniejsze dane zostaną wypisane
     * w oknie oraz listy rezerwacji i zamówień zostaną uzupełnione.
     *
     * @see CartController
     */
    public void initialize() {
        if (!CartController.getEmail().equals("")) {
            try {
                String sql_str = "select * from uzytkownik where email='" + CartController.getEmail() + "'";
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        mail.setText(res.getString("email"));
                        if (res.getString("nazwisko") == null || res.getString("nazwisko").equals("")) {
                            name.setText(res.getString("imie"));
                        } else {
                            name.setText(res.getString("imie") + " " + res.getString("nazwisko"));
                        }
                        phone.setText(res.getString("telefon"));
                    }
                    statement.close();
                } else {
                    alert_text.setText("Nie można połączyć się z bazą.");
                }
            } catch (SQLException e) {
                alert_text.setText(e.getMessage());
            }
            Functions.fetchTable("select * from funkcjaRezerwacji('" + CartController.getEmail() + "')", bokings_table, alert_text);
            Functions.fetchTable("select * from funkcjaZamowien('" + CartController.getEmail() + "')", orders_table, alert_text);
        }
    }

}
