package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

/**
 * Klasa kontrolująca widok modyfikacji zamówienia oraz
 * konwersji rezerwacji w zamówienie.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku mod_order-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class ModOrderController {

    /**
     * Lista książek
     */
    @FXML
    private ComboBox<String> book_list;

    /**
     * Data zwrotu danej książki.
     */
    @FXML
    private DatePicker delivery_date;

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Adres e-mail użytkownika składającego zamówienie.
     */
    @FXML
    private Label email;

    /**
     * Data złożenia zamówienia.
     */
    @FXML
    private DatePicker order_date;

    /**
     * Status zamówienia.
     */
    @FXML
    private ComboBox<String> order_state;

    /**
     * Status płatności.
     */
    @FXML
    private ComboBox<String> payment_state;

    /**
     * Data wygaśnięcia rezerwacji.
     */
    @FXML
    private DatePicker expiration_date;

    /**
     * Zapytanie wysyłane do bazy danych.
     */
    private String sql_str = "";

    /**
     * Indeks wybranego elementu.
     *
     * @see Functions.selected_id
     */
    private int order_id;

    /**
     * Metoda obługująca przycisk aktualizacji.
     * Dla zamówienia dodaje do bazy nową pozycję.
     * Dla rezeracji wykonuje transformację
     * rezerwacji do zamówienia.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onModifyPressed(ActionEvent event) {
        try {
            Connection conn = PGSQL.makeConnection();
            String text = "";
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str, Statement.RETURN_GENERATED_KEYS);
                if (payment_state.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano typu płatności.\n";
                } else {
                    statement.setString(1, payment_state.getSelectionModel().getSelectedItem());
                }
                if (order_state.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano statusu zamówienia.\n";
                } else {
                    statement.setString(2, order_state.getSelectionModel().getSelectedItem());
                }
                if (BookOrderController.book_convert) {
                    statement.setString(3, email.getText());
                    statement.setDate(4, new Date(System.currentTimeMillis()));
                }
                if (text.equals("")) {
                    statement.executeUpdate();
                    alert_text.setText("Pomyślnie wykonano operację.");
                    if (BookOrderController.book_convert) {
                        ResultSet rs = statement.getGeneratedKeys();
                        int id = 0;
                        while (rs.next()) {
                            id = rs.getInt(1);
                        }
                        sql_str = "select przeniesRezerwacje(" + id + ", " + order_id + ")";
                        statement = conn.prepareStatement(sql_str);
                        statement.executeQuery();
                        Stage stage = (Stage) alert_text.getScene().getWindow();
                        stage.close();
                    }
                } else {
                    alert_text.setText(text);
                }
                if (book_list.getSelectionModel().getSelectedItem() != null) {
                    statement = conn.prepareStatement("update zamowienie_ksiazka set data_zwrotu=? where id_zamowienie=? and id_ksiazka=?");
                    statement.setDate(1, Date.valueOf(delivery_date.getValue().toString()));
                    statement.setInt(2, order_id);
                    statement.setInt(3, Integer.parseInt(book_list.getSelectionModel().getSelectedItem().substring(0, book_list.getSelectionModel().getSelectedItem().indexOf(":"))));
                    statement.executeUpdate();
                    alert_text.setText("Pomyślnie wykonano wszystkie operacje.");
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
     * Metoda obsługująca wybór książki. Pozwala na zmianę
     * terminu zwrotu książki.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onBookSelected(ActionEvent event) {
        if (book_list.getSelectionModel().getSelectedItem() != null) {
            delivery_date.setVisible(true);
            delivery_date.setDisable(false);
            String temp = book_list.getSelectionModel().getSelectedItem();
            delivery_date.setValue(LocalDate.parse(temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"))));
        }
    }

    /**
     * Metoda obsługująca wyświetlenie listy książek w zamówieniu.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onBookListSelected(MouseEvent event) {
        Functions.listBooks(book_list, alert_text, order_id);
    }

    /**
     * Metoda obsługująca wyświetlenie listy statusów zamówienia.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onOrderStateSelected(MouseEvent event) {
        Functions.listOrderStates(order_state, alert_text);
    }

    /**
     * Metoda obsługująca wyświetlenie listy kosztów wypożyczenia książki.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onPaymentSelected(MouseEvent event) {
        Functions.listPayments(payment_state, alert_text);
    }

    /**
     * Metoda wywoływana podczas aktywacji kontrolera.
     * Metoda uzupełnia pola o wartości danego zamówienia/rezerwacji
     * oraz przygotowuje zapytanie które zostanie wykonane po
     * wciśnięciu przycisku aktualizacji.
     */
    public void initialize() {
        if (Functions.selected_id != 0) {
            try {
                if (BookOrderController.book_convert) {
                    sql_str = "select * from rezerwacja where id_rezerwacja=" + Functions.selected_id;
                } else {
                    sql_str = "select * from zamowienie where id_zamowienie=" + Functions.selected_id;
                }
                order_id = Functions.selected_id;
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        if (BookOrderController.book_convert) {
                            email.setText(res.getString("email"));
                            book_list.setVisible(false);
                            expiration_date.setVisible(true);
                            expiration_date.setValue(res.getDate("data_wygasniecia").toLocalDate());
                            order_date.setValue(res.getDate("data_rezerwacji").toLocalDate());
                        } else {
                            email.setText(res.getString("email"));
                            payment_state.setValue(res.getString("typ_platnosci"));
                            order_state.setValue(res.getString("nazwa_status"));
                            book_list.setVisible(true);
                            expiration_date.setVisible(false);
                            order_date.setValue(res.getDate("data_zamowienia").toLocalDate());
                        }
                    }
                    statement.close();
                    if (BookOrderController.book_convert) {
                        sql_str = "insert into zamowienie (typ_platnosci, nazwa_status, email, data_zamowienia) values (?, ?, ?, ?)";
                    } else {
                        sql_str = "update zamowienie set typ_platnosci=?, nazwa_status=? where id_zamowienie=" + Functions.selected_id;
                    }
                } else {
                    alert_text.setText("Nie można połączyć się z bazą.");
                }
            } catch (SQLException e) {
                alert_text.setText(e.getMessage());
            }
            Functions.selected_id = 0;
        }
    }
}