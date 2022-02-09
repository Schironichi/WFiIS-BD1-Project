package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * Klasa kontrolująca widok pracownika.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku worker-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class WorkerController {

    /**
     * Metoda tworząca nowe okno z listą autorów książek.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onAuthorsPressed(ActionEvent event) throws IOException {
        Functions.view = "Author";
        Functions.createWindow(FXMLLoader.load(getClass().getResource("list-view.fxml")), "BiblioteKS - lista autorów");
    }

    /**
     * Metoda tworząca nowe okno z listą książek.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onBooksPressed(ActionEvent event) throws IOException {
        Functions.view = "Book";
        Functions.createWindow(FXMLLoader.load(getClass().getResource("list-view.fxml")), "BiblioteKS - lista książek");
    }

    /**
     * Metoda tworząca nowe okno z listą dostaw.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onDeliveriesPressed(ActionEvent event) throws IOException {
        Functions.view = "Delivery";
        Functions.createWindow(FXMLLoader.load(getClass().getResource("list-view.fxml")), "BiblioteKS - lista dostaw");
    }

    /**
     * Metoda tworząca nowe okno z opcją uzupełnienia dostawy
     * o kolejne książki.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onFillDeliveryPressed(ActionEvent event) throws IOException {
        Functions.view = "Del_Books";
        Functions.createWindow(FXMLLoader.load(getClass().getResource("list-view.fxml")), "BiblioteKS - uzupełnienie dostawy");
    }

    /**
     * Metoda wywołująca wylogowanie użytkownika.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onLogoutButtonPressed(ActionEvent event) throws IOException {
        Functions.logout();
    }

    /**
     * Metoda tworząca nowe okno z listą hurtownii.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onWarehousesPressed(ActionEvent event) throws IOException {
        Functions.view = "Warehouse";
        Functions.createWindow(FXMLLoader.load(getClass().getResource("list-view.fxml")), "BiblioteKS - lista hurtowni");
    }

    /**
     * Metoda otwierająca tryb użytkownika. Dzięki temu
     * pracownik może wypożyczyć książkę tak, jak zwykły
     * użytkownik.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onUserViewPressed(ActionEvent event) throws IOException {
        new MainApplication().changeScene("logged-view.fxml");
    }

    /**
     * Metoda tworząca nowe okno z opcją uzupełnienia
     * cen oraz długości wypożyczenia danej książki.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onBooksPricesPressed(ActionEvent event) throws IOException {
        Functions.view = "Book-Price";
        Functions.createWindow(FXMLLoader.load(getClass().getResource("list-view.fxml")), "BiblioteKS - ceny książek");
    }

    /**
     * Metoda tworząca nowe okno z listą rezerwacji
     * oraz zamówień.
     *
     * @param event Zdarzenie wywołujące procedurę.
     */
    @FXML
    void onBookOrderSelected(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("book_order-view.fxml")), "BiblioteKS - ceny książek");
    }
}
