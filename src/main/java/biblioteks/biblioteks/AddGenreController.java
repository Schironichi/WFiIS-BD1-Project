package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Klasa obsługująca okno dodania gatunku książek.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku add_genre-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class AddGenreController {

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Nowy gatunek książek.
     */
    @FXML
    private TextField name;

    /**
     * Metoda dodająca obsługę wciśnięcia przycisku Dodaj.
     * Metoda sprawdza poprawność danych, po czym wysyła zapytanie do bazy
     * danych i w zależności od statusu operacji, wyświetla w widoku
     * odpowiednią informację.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) {
        if (name.getText() == null || name.getText().equals("")) {
            alert_text.setText("Nie podano gatunku.");
        } else {
            try {
                String sql_str = "insert into gatunek (nazwa_gatunek) values (?)";
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    statement.setString(1, name.getText().toLowerCase());
                    statement.executeUpdate();
                    statement.close();
                    alert_text.setText("Dodano nowy gatunek.");
                } else {
                    alert_text.setText("Nie można połączyć się z bazą.");
                }
            } catch (SQLException e) {
                alert_text.setText("Podany gatunek jest już w bazie.");
            }
        }
    }
}