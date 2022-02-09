package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Klasa kontrolująca główny widok aplikacji.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku main-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class MainController {

    /**
     * Metoda przekierowująca na widok logowania użytkownika.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onMenuLeftButtonPressed(ActionEvent event) throws IOException {
        new MainApplication().changeScene("login-view.fxml");
    }

    /**
     * Metoda przekierowującca na widok rejestracji użytkownika.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onMenuRightButtonPressed(ActionEvent event) throws IOException {
        new MainApplication().changeScene("register-view.fxml");
    }

}
