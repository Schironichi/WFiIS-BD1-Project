package biblioteks.biblioteks;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Klasa obsługująca koszyk użytkownika.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku cart-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class CartController {

    /**
     * Lista produktów wybranych przez użytkownika.
     */
    static private final ArrayList<ArrayList<Float>> list = new ArrayList<>();

    /**
     * Adres e-mail zalogowanego użytkownika.
     */
    static private String email = "";

    /**
     * Kwota do zapłaty przez użytkownika.
     */
    static private float sum;

    /**
     * Etykieta z kwotą do zapłaty.
     */
    @FXML
    private Label price;

    /**
     * Przycisk usuwający dany produkt z listy.
     */
    @FXML
    private Button del_btn;

    /**
     * Tabela produktów, która jest wyświetlana w widoku.
     */
    @FXML
    private TableView<ObservableList> table;

    /**
     * Metoda zwracająca wartość kwoty do zapłaty.
     * @return Wartość kwoty do zapłaty.
     */
    static float getSum() {
        return sum;
    }

    /**
     * Obsługa dodania wartości do listy produktów.
     * @param id Indeks książki.
     * @param amount Ilość sztuk danej książki.
     * @param price Cena za sztukę danej książki.
     * @param duration Czas wypożyczenia (w dniach).
     */
    static void addItem(int id, int amount, float price, int duration) {
        ArrayList<Float> elem = new ArrayList<Float>();
        elem.add((float) id);
        elem.add((float) amount);
        elem.add(price);
        elem.add((float) duration);
        list.add(elem);
    }

    /**
     * Metoda zwracająca adres e-mail zalogowanego użytkownika.
     * @return Adres e-mail zalogowanego użytkownika.
     */
    static String getEmail() {
        return email;
    }

    /**
     * Metoda ustawiająca nowy adres e-mail zalogowanego użytkownika.
     * @param new_email Nowy adres e-mail zalogowanego użytkownika.
     */
    static void setEmail(String new_email) {
        email = new_email;
    }

    /**
     * Metoda pozbywająca się wszystkich produktów z koszyka.
     */
    static void resetCart() {
        for (ArrayList<Float> row : list) {
            row.clear();
        }
        list.clear();
    }

    /**
     * Metoda dodaje obsługę przycisku dokonania rezerwacji.
     * Metoda sprawdza, czy koszyk jest pusty. Jeżeli nie jest,
     * dokonane jest sprawdzenie, czy użytkownik jest zalogowany.
     * Jeżeli to również zostanie spełnione, metoda wysyła do bazy
     * danych rezerwację złożoną przez użytkownika.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onBookPressed(ActionEvent event) {
        int id = 0;
        try {
            String sql_str = "insert into rezerwacja (email, data_rezerwacji, data_wygasniecia) values (?, ?, ?)";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                if (list.size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Koszyk jest pusty");
                    alert.setContentText("Nie można złożyć rezerwacji dla pustego koszyka.");
                    alert.showAndWait();
                    Stage stg = (Stage) del_btn.getScene().getWindow();
                    stg.close();
                    return;
                } else {
                    PreparedStatement statement = conn.prepareStatement(sql_str, Statement.RETURN_GENERATED_KEYS);
                    if (CartController.getEmail().equals("")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd");
                        alert.setHeaderText("Użytkownik niezalogowany");
                        alert.setContentText("Proszę się zalogować przed wykonaniem tej operacji.");
                        alert.showAndWait();
                        Stage stg = (Stage) del_btn.getScene().getWindow();
                        stg.close();
                        return;
                    } else {
                        statement.setString(1, CartController.getEmail());
                    }
                    statement.setDate(2, new Date(System.currentTimeMillis()));
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(new Date(System.currentTimeMillis()));
                    c.add(GregorianCalendar.DATE, 7);
                    statement.setDate(3, new Date(c.getTimeInMillis()));
                    statement.executeUpdate();
                    ResultSet rs = statement.getGeneratedKeys();
                    id = 0;
                    while (rs.next()) {
                        id = rs.getInt(1);
                    }
                    sql_str = "insert into rezerwacja_ksiazka (id_ksiazka, id_rezerwacja, rezerwacja_ilosc, czas_wypozyczenia) values (?, ?, ?, ?)";
                    statement = conn.prepareStatement(sql_str);
                    for (ArrayList<Float> data_row : list) {
                        statement.setInt(1, data_row.get(0).intValue());
                        statement.setInt(2, id);
                        statement.setInt(3, data_row.get(1).intValue());
                        statement.setInt(4, data_row.get(3).intValue());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                    statement.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sukces");
                    alert.setHeaderText("Dodano rezerwację");
                    alert.setContentText("Pomyślnie złożono rezerwację. Książki będą do odebrania w ciągu 7 dni od złożenia rezerwacji.");
                    alert.showAndWait();
                    resetCart();
                    Stage stage = (Stage) del_btn.getScene().getWindow();
                    stage.close();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z połączeniem");
                alert.setContentText("Nie można połączyć się z bazą. Proszę spróbować później");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            if (id != 0) {
                try {
                    String sql_str = "delete from rezerwacja where id_rezerwacja=" + id;
                    Connection conn = PGSQL.makeConnection();
                    if (conn != null) {
                        PreparedStatement statement = conn.prepareStatement(sql_str);
                        statement.executeUpdate();
                        statement.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Problem z wysłaniem danych");
            if (e.getSQLState().equals("P0001")) {
                alert.setContentText(e.getMessage().substring(e.getMessage().indexOf("ERROR:"), e.getMessage().indexOf("  ")));
            } else {
                alert.setContentText(e.getMessage());
            }
            alert.showAndWait();
        }
    }

    /**
     * Metoda obsługująca usunięcie danego produktu z listy.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onDeletePressed(ActionEvent event) {
        if (table.getSelectionModel().getSelectedItem() != null) {
            list.remove(table.getSelectionModel().getSelectedIndex());
            fetchList();
            fetchSum();
        }
    }

    /**
     * Metoda dodaje obsługę przycisku dokonania zamówienia. Na
     * początku zostaje wyświetlone okno płatności (payment-view.fxml).
     * Jeżeli płatność się powiedzie, wówczas sprawdzane jest czy
     * koszyk jest pusty. Jeżeli nie jest,
     * dokonane jest sprawdzenie, czy użytkownik jest zalogowany.
     * Jeżeli to również zostanie spełnione, metoda wysyła do bazy
     * danych zamówienie złożone przez użytkownika.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onOrderPressed(ActionEvent event) throws IOException {
        int id = 0;
        Functions.createWindow(FXMLLoader.load(getClass().getResource("payment-view.fxml")), "BiblioteKS - płatność");
        if (PaymentController.getState().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Nie dokonano płatności");
            alert.setContentText("Proszę wybrać formę płatności");
            alert.showAndWait();
        } else {
            try {
                String sql_str = "insert into zamowienie (email, typ_platnosci, data_zamowienia, nazwa_status) values (?, ?, ?, ?)";
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    if (list.size() == 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd");
                        alert.setHeaderText("Koszyk jest pusty");
                        alert.setContentText("Nie można złożyć zamówienia dla pustego koszyka.");
                        alert.showAndWait();
                        Stage stg = (Stage) del_btn.getScene().getWindow();
                        stg.close();
                    } else {
                        PreparedStatement statement = conn.prepareStatement(sql_str, Statement.RETURN_GENERATED_KEYS);
                        if (CartController.getEmail().equals("")) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Błąd");
                            alert.setHeaderText("Użytkownik niezalogowany");
                            alert.setContentText("Proszę się zalogować przed wykonaniem tej operacji.");
                            alert.showAndWait();
                            Stage stg = (Stage) del_btn.getScene().getWindow();
                            stg.close();
                            return;
                        } else {
                            statement.setString(1, CartController.getEmail());
                        }
                        statement.setString(2, PaymentController.getState());
                        statement.setDate(3, new Date(System.currentTimeMillis()));
                        statement.setString(4, "Złożone");
                        statement.executeUpdate();
                        ResultSet rs = statement.getGeneratedKeys();
                        id = 0;
                        while (rs.next()) {
                            id = rs.getInt(1);
                        }
                        sql_str = "insert into zamowienie_ksiazka (id_ksiazka, id_zamowienie, zamowienie_ilosc, data_zwrotu) values (?, ?, ?, ?)";
                        statement = conn.prepareStatement(sql_str);
                        for (ArrayList<Float> data_row : list) {
                            statement.setInt(1, data_row.get(0).intValue());
                            statement.setInt(2, id);
                            statement.setInt(3, data_row.get(1).intValue());
                            GregorianCalendar c = new GregorianCalendar();
                            c.setTime(new Date(System.currentTimeMillis()));
                            c.add(GregorianCalendar.DATE, data_row.get(3).intValue());
                            statement.setDate(4, new Date(c.getTimeInMillis()));
                            statement.addBatch();
                        }
                        statement.executeBatch();
                        statement.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Sukces");
                        alert.setHeaderText("Dodano zamówienie");
                        alert.setContentText("Pomyślnie dodano zamówienie. Proszę odebrać książki w bibliotece.");
                        alert.showAndWait();
                        resetCart();
                        Stage stage = (Stage) del_btn.getScene().getWindow();
                        stage.close();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Problem z połączeniem");
                    alert.setContentText("Nie można połączyć się z bazą. Proszę spróbować później");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                if (id != 0) {
                    try {
                        String sql_str = "delete from zamowienie where id_zamowienie=" + id;
                        Connection conn = PGSQL.makeConnection();
                        if (conn != null) {
                            PreparedStatement statement = conn.prepareStatement(sql_str);
                            statement.executeUpdate();
                            statement.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z wysłaniem danych");
                if (e.getSQLState().equals("P0001")) {
                    alert.setContentText(e.getMessage().substring(e.getMessage().indexOf("ERROR:"), e.getMessage().indexOf("  ")));
                } else {
                    alert.setContentText(e.getMessage());
                }
                alert.showAndWait();
            }
        }
    }

    /**
     * Metoda odświeżająca wartość kwoty do zapłaty.
     */
    private void fetchSum() {
        sum = 0.f;
        for (ArrayList<Float> row : list) {
            sum += row.get(1) * row.get(2);
        }
        price.setText(new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP) + "zł");
    }

    /**
     * Metoda odświeżająca wyświetlaną tabelę książek w koszyku.
     */
    private void fetchList() {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        for (ArrayList<Float> data_row : list) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (Float elem : data_row) {
                row.add(elem.toString());
            }
            data.add(row);
        }
        table.getItems().setAll(data);
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * Metoda uzupełnia tabelę książek o odpowiednie kolumny,
     * po czym uzupełnia wartość kwoty do zapłaty oraz elementy
     * w wyświetlanej tabeli książek.
     */
    public void initialize() {
        String[] nazwy = {"ID Książki", "Ilość Książek", "Cena (szt.)", "Długość wypożyczenia (dni)"};
        for (int i = 0; i < nazwy.length; i++) {
            final int temp = i;
            TableColumn col = new TableColumn(nazwy[i]);
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> cellDataFeatures) {
                    if (cellDataFeatures.getValue().get(temp) == null) {
                        return new SimpleStringProperty("");
                    } else {
                        return new SimpleStringProperty(cellDataFeatures.getValue().get(temp).toString());
                    }
                }
            });
            table.getColumns().addAll(col);
        }
        fetchList();
        fetchSum();
    }
}

