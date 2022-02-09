package biblioteks.biblioteks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.FloatStringConverter;

import java.io.IOException;
import java.sql.*;

/**
 * Klasa obsługująca okno dodania/modyfikacji dostawy.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku add_delivery-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class AddDeliveryController {

    /**
     * Przycisk wysyłający zapytanie do bazy danych.
     */
    @FXML
    private Button add_btn;

    /**
     * Opis danego okna, znajdujący się w górnej części widoku.
     */
    @FXML
    private Label window_name;

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Koszta zewnętrzne, powiązane z dostawą (na przykład koszt wysyłki).
     */
    @FXML
    private TextField cost;

    /**
     * Data otrzymania dostawy.
     */
    @FXML
    private DatePicker delivery_date;

    /**
     * Data zgłoszenia.
     */
    @FXML
    private DatePicker order_date;

    /**
     * Lista stanów płatności.
     */
    @FXML
    private ComboBox<String> payment_state;

    /**
     * Lista hurtowni.
     */
    @FXML
    private ComboBox<String> warehouse_list;

    /**
     * Zapytanie wysyłane do bazy danych.
     */
    private String sql_str = "";

    /**
     * Metoda dodająca obsługę wciśnięcia przycisku Dodaj/Zmień.
     * Najpierw odbywa się sprawdzenie poprawności wprowadznych danych.
     * Następnie zostaje wysłane zapytanie do bazy danych. W zależności
     * od statusu operacji, wyświetlana zostaje odpowiednia informacja.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) throws IOException {
        try {
            if (sql_str.equals("")) {
                sql_str = "insert into dostawa (hurt_email, typ_platnosci, koszt, data, data_dostarczenia) values (?, ?, ?, ?, ?)";
            }
            Connection conn = PGSQL.makeConnection();
            String text = "";
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                if (warehouse_list.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano adresu e-mail hurtowni.\n";
                } else {
                    String temp = warehouse_list.getSelectionModel().getSelectedItem();
                    temp = temp.substring(temp.indexOf('(') + 1, temp.indexOf(')'));
                    statement.setString(1, temp);
                }
                if (payment_state.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano płatności.\n";
                } else {
                    statement.setString(2, payment_state.getSelectionModel().getSelectedItem());
                }
                if (cost.getText().equals("")) {
                    text += "Nie podano kosztu.\n";
                } else {
                    try {
                        if (cost.getText().charAt(0) == '-') {
                            text += "Podano nieprawidłową kwotę.\n";
                        } else {
                            statement.setFloat(3, new FloatStringConverter().fromString(cost.getText()));

                        }
                    } catch (Exception e) {
                        text += "Podano nieprawidłową kwotę.\n";
                    }
                }
                if (order_date.getValue() == null) {
                    text += "Nie podano daty zamówienia.\n";
                } else {
                    statement.setDate(4, Date.valueOf(order_date.getValue().toString()));
                }
                Date temp_date;
                if (delivery_date.getValue() != null) {
                    temp_date = Date.valueOf(order_date.getValue().toString());
                } else {
                    temp_date = null;
                }
                statement.setDate(5, temp_date);
                if (text.equals("")) {
                    statement.executeUpdate();
                    alert_text.setText("Pomyślnie wykonano operację.");
                } else {
                    alert_text.setText(text);
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
     * Metoda dodaje obsługę przycisku dodającego nową hurtownię.
     * Metoda otwiera nowe okno w którym pracownik może dodać
     * nową hurtownię do bazy danych (add_warehouse-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onNewWarehousePressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("add_warehouse-view.fxml")), "BiblioteKS - nowa hurtownia");
    }

    /**
     * Metoda obsługująca wyświetlenie opcji płatności.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onPaymentSelected(MouseEvent event) {
        Functions.listPayments(payment_state, alert_text);
    }

    /**
     * Metoda obsługująca wyświetlenie listy hurtowni w bazie danych.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onWarehouseListSelected(MouseEvent event) {
        try {
            String temp_sql = "select hurt_email, nazwa from hurtownia";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql);
                ResultSet res = statement.executeQuery();
                String mail;
                String name;
                ObservableList names = FXCollections.observableArrayList();
                while (res.next()) {
                    mail = res.getString("hurt_email");
                    name = res.getString("nazwa");
                    names.add(name + " (" + mail + ")");
                }
                statement.close();
                warehouse_list.setItems(names);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * Dodaje ona obsługę modyfikacji danej dostawy.
     * Jeżeli użytkownik wybrał opcję modyfikacji dostawy,
     * wówczas wszystkie pola zostają uzupełnione o odpowiednie
     * wartości.
     */
    public void initialize() {
        if (Functions.selected_id != 0) {
            try {
                sql_str = "select * from dostawa where id_dostawa=" + Functions.selected_id;
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    add_btn.setText("Zmień");
                    window_name.setText("ZMIEŃ DOSTAWĘ");
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        cost.setText(res.getString("koszt"));
                        payment_state.setValue(res.getString("typ_platnosci"));
                        order_date.setValue(res.getDate("data").toLocalDate());
                        if (res.getString("data_dostarczenia") != null) {
                            delivery_date.setValue(res.getDate("data_dostarczenia").toLocalDate());
                        }
                    }
                    statement.close();
                    sql_str = "update dostawa set hurt_email=?, typ_platnosci=?, koszt=?, data=?, data_dostarczenia=? where id_dostawa=" + Functions.selected_id;
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

