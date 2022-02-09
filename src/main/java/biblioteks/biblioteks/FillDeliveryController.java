package biblioteks.biblioteks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.FloatStringConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasa obsługująca uzupełnienie danej dostawy
 * o kolejne książki.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku fill_delivery-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class FillDeliveryController {

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Lista dostępnych książek.
     */
    @FXML
    private ComboBox<?> book_list;

    /**
     * Lista dostępnych dostaw.
     */
    @FXML
    private ComboBox<?> delivery_list;

    /**
     * Ilość szutk danej ksiązki w dostawie.
     */
    @FXML
    private TextField amount;

    /**
     * Cena za sztukę danej książki.
     */
    @FXML
    private TextField cost;

    /**
     * Metoda dodaje obsługę wciśnięcia przycisku Dodaj.
     * Metoda łączy się z bazą danych, po czym sprawdza poprawność
     * wprowadzonych danych. Jeżeli wszystko zostało poprawnie uzupełnione,
     * dane zostają wysłane do bazy.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) {
        try {
            String sql_str = "insert into dostawa_ksiazka (id_dostawa, id_ksiazka, ilosc, cena) values (?, ?, ?, ?)";
            Connection conn = PGSQL.makeConnection();
            String text = "";
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                if (delivery_list.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano dostawy.\n";
                } else {
                    String temp = delivery_list.getSelectionModel().getSelectedItem().toString();
                    temp = temp.substring(0, temp.indexOf(':'));
                    statement.setInt(1, Integer.parseInt(temp));
                }
                if (book_list.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano książki.\n";
                } else {
                    String temp = book_list.getSelectionModel().getSelectedItem().toString();
                    temp = temp.substring(0, temp.indexOf(':'));
                    statement.setInt(2, Integer.parseInt(temp));
                }
                if (amount.getText().equals("")) {
                    text += "Nie podano ilości.\n";
                } else {
                    try {
                        if (amount.getText().charAt(0) == '-') {
                            text += "Podano nieprawidłową ilość.\n";
                        } else {
                            statement.setInt(3, Integer.parseInt(amount.getText()));
                        }
                    } catch (Exception e) {
                        text += "Podano nieprawidłową ilość.\n";
                    }
                }
                if (cost.getText().equals("")) {
                    text += "Nie podano ceny.\n";
                } else {
                    try {
                        if (cost.getText().charAt(0) == '-') {
                            text += "Podano nieprawidłową cenę.\n";
                        } else {
                            statement.setFloat(4, new FloatStringConverter().fromString(cost.getText()));

                        }
                    } catch (Exception e) {
                        text += "Podano nieprawidłową cenę.\n";
                    }
                }
                if (text.equals("")) {
                    statement.executeUpdate();
                    alert_text.setText("Pomyślnie uzupełniono zamówienie");
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
     * Metoda dodaje obsługę wyświetlenia listy dostępnych książek.
     * Metoda łączy się z bazą danych, po czym uzupełnia listę książek
     * o wszystkie istniejące obiekty tego typu w bazie danych.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onBookListSelected(MouseEvent event) {
        try {
            String sql_str = "select * from wybor_ksiazek";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                ResultSet res = statement.executeQuery();
                int id;
                String title;
                String surn;
                ObservableList books = FXCollections.observableArrayList();
                while (res.next()) {
                    id = res.getInt("id_ksiazka");
                    title = res.getString("tytul");
                    surn = res.getString("nazwisko");
                    books.add(id + ": " + title + " (" + surn + ")");
                }
                statement.close();
                book_list.setItems(books);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda dodaje obsługę wyświetlenia listy dostępnych dostaw.
     * Metoda łączy się z bazą danych, po czym uzupełnia listę dostaw
     * o wszystkie istniejące obiekty tego typu w bazie danych.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onDeliveryListSelected(MouseEvent event) {
        try {
            String sql_str = "select id_dostawa, hurt_email, data from dostawa order by id_dostawa";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                ResultSet res = statement.executeQuery();
                int id;
                String mail;
                String data;
                ObservableList names = FXCollections.observableArrayList();
                while (res.next()) {
                    id = res.getInt("id_dostawa");
                    mail = res.getString("hurt_email");
                    data = res.getDate("data").toString();
                    names.add(id + ": " + mail + " (" + data + ")");
                }
                statement.close();
                delivery_list.setItems(names);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda dodaje obsługę przycisku dodającego nową książkę.
     * Metoda otwiera nowe okno w którym pracownik może dodać
     * nową książkę do bazy danych (add_book-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onNewBookPressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("add_book-view.fxml")), "BiblioteKS - nowa książka");
    }

    /**
     * Metoda dodaje obsługę przycisku dodającego nową dostawę.
     * Metoda otwiera nowe okno w którym pracownik może dodać
     * nową dostawę do bazy danych (add_delivery-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onNewDeliveryPressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("add_delivery-view.fxml")), "BiblioteKS - nowa dostawa");
    }
}
