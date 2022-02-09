package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasa kontrolująca widok logowania użytkownika.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku login-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class LoginController {

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Pole uzupełniane przez użytkownika: Adres e-mail
     */
    @FXML
    private TextField email;

    /**
     * Pole uzupełniane przez użytkownika: Hasło
     */
    @FXML
    private PasswordField pass;

    /**
     * Metoda wywołana po wciśnięciu przycisku logowania. Próbuje ona wczytać
     * z bazy danych użytkownika o podanych przez niego danych. Informacja zwrotna,
     * przy nieudanej próbie, zostaje wyświetlona wewnątrz elementu alert_text.
     * W przypadku znalezienia takiego użytkownika, program przekierowuje uzytkownika
     * do wyszukiwarki bibliotekowej. Oprócz tego, metoda sprawdza, jakie uprawnienia
     * ma dany użytkownik.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onLoginPressed(ActionEvent event) throws IOException {
        try {
            String sql_str = "select imie, typ from uzytkownik where email='" + email.getText() + "' and haslo='" + pass.getText() + "'";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                ResultSet res = statement.executeQuery();
                if (res.next()) {
                    CartController.setEmail(email.getText());
                    switch (res.getInt("typ")) {
                        case 0:
                            new MainApplication().changeScene("logged-view.fxml");
                            break;
                        case 1:
                            new MainApplication().changeScene("worker-view.fxml");
                            break;
                        default:
                            break;
                    }
                } else {
                    alert_text.setText("Podano nieprawidłowe dane.");
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
     * Metoda przekierowująca na główny widok aplikacji.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onMainPagePressed(ActionEvent event) throws IOException {
        new MainApplication().changeScene("main-view.fxml");
    }

    /**
     * Metoda przekierowująca na widok rejestracji użytkownika.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onNewbiePressed(ActionEvent event) throws IOException {
        new MainApplication().changeScene("register-view.fxml");
    }

}