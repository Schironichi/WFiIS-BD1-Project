package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Klasa kontrolująca widok okna płatności.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku payment-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class PaymentController {
    /**
     * Zmienna reprezentująca informację o statusie płatności.
     */
    static private String state = "";

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Metoda zwracająca stan płatności.
     *
     * @return Stan płatności w formie String.
     */
    static String getState() {
        return state;
    }

    /**
     * Metoda wywołana po wciśnięciu przycisku płatności w bibliotece.
     * Metoda zamyka okno płatności i nadaje odpowiedni status płatności.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onLocalPayment(ActionEvent event) {
        state = "Niezapłacone";
        Stage stage = (Stage) alert_text.getScene().getWindow();
        stage.close();
    }

    /**
     * Metoda wywołana po wciśnięciu przycisku płatności online.
     * Metoda wyświetla okno "płatności", po czym ustawia
     * odpowiednią informację o statusie płatności.
     * Po wykonaniu płatności zamyka okno.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onOnlinePayment(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Płatność");
        alert.setHeaderText("Płatność Online");
        alert.setContentText("Dokonano płatność Online. Życzymy miłej lektury!");
        alert.showAndWait();
        state = "Online";
        Stage stage = (Stage) alert_text.getScene().getWindow();
        stage.close();
    }

    /**
     * Metoda wywoływana podczas aktywacji kontrolera.
     * Metoda pokazuje kwotę, którą będzie musiał zapłacić dany użytkownik
     * za swoje zamówienie.
     */
    public void initialize() {
        alert_text.setText(BigDecimal.valueOf(CartController.getSum()).setScale(2, RoundingMode.HALF_UP) + " zł");
    }
}
