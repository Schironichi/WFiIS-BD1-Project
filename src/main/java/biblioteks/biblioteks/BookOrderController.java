package biblioteks.biblioteks;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Klasa obsługująca okno zamówień oraz rezerwacji użytkowników.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku book_order-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class BookOrderController {

    /**
     * Informacja o tym, czy wykonywana jest konwersja rezerwacji w zamówienie.
     */
    static boolean book_convert;

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Tabela wyświetlana na ekranie, zawierająca w sobie
     * listę rezerwacji złożonych przez użytkowników.
     */
    @FXML
    private TableView<ObservableList> bookings;

    /**
     * Tabela wyświetlana na ekranie, zawierająca w sobie
     * listę zamówień złożonych przez użytkowników.
     */
    @FXML
    private TableView<ObservableList> orders;

    /**
     * Nazwa pliku .fxml, który jest oknem dodania/modyfikowania
     * danego obiektu.
     */
    private String add_fxml;

    /**
     * Zapytanie do bazy danych zwracające listę zamówień.
     */
    private String orders_sql;

    /**
     * Zapytanie do bazy danych zwracające listę rezerwacji.
     */
    private String bookings_sql;

    /**
     * Tytuł wyskakującego okna.
     */
    private String win_name;

    /**
     * Zapytanie do bazy danych usuwające dany element.
     */
    private String del_sql;

    /**
     * Metoda dodaje obsługę przycisku zmiany zamówienia.
     * Jeżeli wybrano pozycję z tabeli zamówień, metoda
     * wyświetla okno modyfikacji zamówienia.
     * Po dokonaniu zmian, metoda odświeża listę zamówień.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onChangeOrderPressed(ActionEvent event) throws IOException {
        if (orders.getSelectionModel().getSelectedItem() != null) {
            book_convert = false;
            Functions.selected_id = Integer.parseInt(orders.getSelectionModel().getSelectedItem().get(0).toString());
            Functions.createWindow(FXMLLoader.load(getClass().getResource(add_fxml)), win_name);
            Functions.fetchTable(orders_sql, orders, alert_text);
        }
    }

    /**
     * Metoda dodaje obsługę przycisku usunięcia rezerwacji.
     * Jeżeli wybrano pozycję z tabeli rezerwacji, metoda
     * podejmuje próbę usunięcia jej z bazy danych.
     * Po dokonaniu zmian, metoda odświeża listę rezerwacji.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onDeleteBookPressed(ActionEvent event) {
        if (bookings.getSelectionModel().getSelectedItem() != null) {
            try {
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    PreparedStatement statement;
                    statement = conn.prepareStatement(del_sql + bookings.getSelectionModel().getSelectedItem().get(0));
                    statement.executeUpdate();
                    statement.close();
                    alert_text.setText("Pomyślnie usunięto wybrany element.");
                } else {
                    alert_text.setText("Nie można połączyć się z bazą.");
                }
            } catch (SQLException e) {
                if (e.getSQLState().equals("23503")) {
                    alert_text.setText("Istnieją obiekty zależne od tego elementu.");
                } else {
                    alert_text.setText(e.getMessage());
                }
            }
        } else {
            alert_text.setText("Nie wybrano elementu z listy!");
        }
        Functions.fetchTable(bookings_sql, bookings, alert_text);
    }

    /**
     * Metoda dodaje obsługę przycisku przekształcenia rezerwacji w zamówienie.
     * Jeżeli wybrano pozycję z tabeli rezerwacji, metoda
     * otwiera okno konwersji rezerwacji w zamówienie.
     * Po dokonaniu zmian, metoda odświeża listę rezerwacji oraz zamówień,
     * po czym usuwa daną rezerwację.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onIntoOrderPressed(ActionEvent event) throws IOException {
        if (bookings.getSelectionModel().getSelectedItem() != null) {
            int temp = bookings.getSelectionModel().getSelectedIndex();
            book_convert = true;
            Functions.selected_id = Integer.parseInt(bookings.getSelectionModel().getSelectedItem().get(0).toString());
            Functions.createWindow(FXMLLoader.load(getClass().getResource(add_fxml)), win_name);
            Functions.fetchTable(orders_sql, orders, alert_text);
            Functions.fetchTable(bookings_sql, bookings, alert_text);
            bookings.getSelectionModel().select(temp);
            onDeleteBookPressed(event);
        }
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * Metoda przygotowuje zapytania do bazdy danych,
     * wyświetlania nowego okna oraz uzupełnia tabele
     * rezerwacji i zamówień.
     */
    public void initialize() {
        orders_sql = "select * from lista_zamowien";
        bookings_sql = "select * from lista_rezerwacji";
        add_fxml = "mod_order-view.fxml";
        win_name = "BiblioteKS - zamówienie";
        del_sql = "delete from rezerwacja where id_rezerwacja=";
        Functions.fetchTable(bookings_sql, bookings, alert_text);
        Functions.fetchTable(orders_sql, orders, alert_text);
    }
}