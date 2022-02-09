package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Klasa kontrolująca widok rejestracji użytkownika.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku register-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class RegisterController {
    /**
     * Element typu Label, który przechowuje infomacje w przypadku
     * nieudanej operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Pole uzupełniane przez użytkownika: Adres e-mail
     */
    @FXML
    private TextField email;

    /**
     * Pole uzupełniane przez użytkownika: Imię
     */
    @FXML
    private TextField name;

    /**
     * Pole uzupełniane przez użytkownika: Hasło
     */
    @FXML
    private PasswordField pass;

    /**
     * Pole uzupełniane przez użytkownika: Numer telefonu
     */
    @FXML
    private TextField phone;

    /**
     * Pole uzupełniane przez użytkownika: Nazwisko
     */
    @FXML
    private TextField surn;

    /**
     * Metoda przekierowująca na widok logowania użytkownika.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onExistingPressed(ActionEvent event) throws IOException {
        new MainApplication().changeScene("login-view.fxml");
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
     * Metoda wywołana po wciśnięciu przycisku rejestracji. Próbuje ona wysłać
     * do bazy danych nowego użytkownika o podanych przez niego danych.
     * Baza danych posiada trigger, który sprawdza czy wprowadzone dane nie są
     * puste, oraz dokonuje walidacji danych. Informacja zwrotna zostaje
     * wyświetlona wewnątrz elementu alert_text.
     * W przypadku pomyślnego dodania użytkownika do bazy, program wyświetla
     * okno z informacją o tym, po czym przekierowuje uzytkownika do
     * widoku logowania.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onRegisterPressed(ActionEvent event) throws IOException {
        try {
            String sql_str = "insert into uzytkownik (email, haslo, telefon, imie, nazwisko, typ) values (?, ?, ?, ?, ?, 0)";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                statement.setString(1, email.getText());
                statement.setString(2, pass.getText());
                statement.setString(3, phone.getText());
                statement.setString(4, name.getText());
                statement.setString(5, surn.getText());
                statement.executeUpdate();
                statement.close();
                Alert register = new Alert(Alert.AlertType.INFORMATION);
                register.setTitle("Udana rejestracja");
                register.setHeaderText("Zarejestrowano nowego użytkownika");
                register.setContentText("Po zatwierdzeniu tego okna zostaniesz przekierowany do strony z logowaniem");
                register.showAndWait();
                new MainApplication().changeScene("login-view.fxml");
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
}
