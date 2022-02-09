package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Klasa obsługująca okno dodania wydawnictwa.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku add_publisher-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class AddPublisherController {

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Nazwa wydawnictwa.
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
            alert_text.setText("Nie podano wydawnictwa.");
        } else {
            try {
                String sql_str = "insert into wydawnictwo (nazwa_wydawnictwo) values (?)";
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    statement.setString(1, name.getText());
                    statement.executeUpdate();
                    statement.close();
                    alert_text.setText("Dodano nowe wydawnictwo.");
                } else {
                    alert_text.setText("Nie można połączyć się z bazą.");
                }
            } catch (SQLException e) {
                alert_text.setText("Podane wydawnictwo jest już w bazie.");
            }
        }
    }

}
