package biblioteks.biblioteks;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasa posiadająca elementy oraz metody typu
 * static, wykorzystywane przez wiele kontrolerów.
 */
public class Functions {

    /**
     * String mówiący o tym, co ma zostać
     * wyświetlone w tabeli elementów
     * (list-view.fxml).
     */
    static String view;

    /**
     * Zmienna typu static przechowująca
     * indeks elementów.
     */
    static int selected_id = 0;

    /**
     * Zmienna typu static przechowująca adres
     * e-mail zalogowanego użytkownika. Działa
     * prawie jak sesja użytkownika.
     */
    static String selected_email = "";

    /**
     * Metoda tworzy nowe okno dla podanych paramentów.
     * @param parent Przetworzony plik .fxml, który zostanie wyświetlony.
     * @param window_name Tytuł nowego okna.
     */
    static void createWindow(Parent parent, String window_name) {
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(window_name);
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * Metoda wykonuje odczyt wierszy oraz kolumn z podanego zapytania.
     * Następnie uzupełnia podaną tabelę otrzymanymi wartościami.
     * W razie niepowodzenia uzupełnia etykietę alert_text.
     * @param sql_str Zapytanie wysyłane do bazy.
     * @param table Tabela do której zostaną wpisane wartości.
     * @param alert_text Etykieta przechowująca informację o niepowodzeniu w wykonaniu operacji.
     */
    static void fetchTable(String sql_str, TableView<ObservableList> table, Label alert_text) {
        table.getSelectionModel();
        try {
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                table.getColumns().clear();
                PreparedStatement statement = conn.prepareStatement(sql_str);
                ResultSet res = statement.executeQuery();
                for (int i = 0; i < res.getMetaData().getColumnCount(); i++) {
                    final int temp = i;
                    TableColumn col = new TableColumn(res.getMetaData().getColumnName(i + 1));
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
                ObservableList<ObservableList> data = FXCollections.observableArrayList();
                while (res.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
                        row.add(res.getString(i));
                    }
                    data.add(row);
                }
                table.setItems(data);
                statement.close();
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda wylogowująca użytkownika. Najpierw zwracana jest
     * informacja o wylogowaniu, po czym następuje przeniesienie
     * użytkownika do widoku początkowego. Na koniec zostaje
     * wyczyszczony koszyk.
     */
    static void logout() throws IOException {
        Alert logout = new Alert(Alert.AlertType.INFORMATION);
        logout.setTitle("Wylogowano");
        logout.setHeaderText("Wylogowano użytkownika");
        logout.setContentText("Po zatwierdzeniu tego okna zostaniesz przekierowany do strony głównej");
        logout.showAndWait();
        CartController.setEmail("");
        CartController.resetCart();
        new MainApplication().changeScene("main-view.fxml");
    }

    /**
     * Metoda uzupełniająca wprowadzony element typu ComboBox
     * o autorów.
     * @param author_list ComboBox, który zostanie uzupełniony.
     * @param alert_text Etykieta przechowująca informację o niepowodzeniu w wykonaniu operacji.
     */
    static void listAuthors(ComboBox<?> author_list, Label alert_text) {
        try {
            String temp_sql = "select id_autor, imie, nazwisko from autor order by id_autor";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql);
                ResultSet res = statement.executeQuery();
                String name;
                String surn;
                int id;
                ObservableList authors = FXCollections.observableArrayList();
                while (res.next()) {
                    name = res.getString("imie");
                    surn = res.getString("nazwisko");
                    id = res.getInt("id_autor");
                    authors.add(id + ": " + name + " " + surn);
                }
                statement.close();
                author_list.setItems(authors);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda uzupełniająca wprowadzony element typu ComboBox
     * o książki z danego zamówienia.
     * @param book_list ComboBox, który zostanie uzupełniony.
     * @param alert_text Etykieta przechowująca informację o niepowodzeniu w wykonaniu operacji.
     * @param order_id Indeks zamówienia.
     */
    static void listBooks(ComboBox<?> book_list, Label alert_text, int order_id) {
        try {
            String temp_sql = "select zk.id_ksiazka, k.tytul, zk.data_zwrotu from zamowienie_ksiazka zk join ksiazka k on k.id_ksiazka=zk.id_ksiazka where id_zamowienie=" + order_id;
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql);
                ResultSet res = statement.executeQuery();
                ObservableList states = FXCollections.observableArrayList();
                while (res.next()) {
                    states.add(res.getString("id_ksiazka") + ": " + res.getString("tytul") + " (" + res.getDate("data_zwrotu") + ")");
                }
                statement.close();
                book_list.setItems(states);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda uzupełniająca wprowadzony element typu ComboBox
     * o stany zamówienia
     * @param order_state ComboBox, który zostanie uzupełniony.
     * @param alert_text Etykieta przechowująca informację o niepowodzeniu w wykonaniu operacji.
     */
    static void listOrderStates(ComboBox<?> order_state, Label alert_text) {
        try {
            String temp_sql = "select * from status";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql);
                ResultSet res = statement.executeQuery();
                ObservableList states = FXCollections.observableArrayList();
                while (res.next()) {
                    states.add(res.getString("nazwa_status"));
                }
                statement.close();
                order_state.setItems(states);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda uzupełniająca wprowadzony element typu ComboBox
     * o typy płatności.
     * @param payment_state ComboBox, który zostanie uzupełniony.
     * @param alert_text Etykieta przechowująca informację o niepowodzeniu w wykonaniu operacji.
     */
    static void listPayments(ComboBox<?> payment_state, Label alert_text) {
        try {
            String temp_sql = "select * from platnosc";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql);
                ResultSet res = statement.executeQuery();
                ObservableList types = FXCollections.observableArrayList();
                while (res.next()) {
                    types.add(res.getString("typ_platnosci"));
                }
                statement.close();
                payment_state.setItems(types);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda uzupełniająca wprowadzony element typu ComboBox
     * o gatunki książek.
     * @param genre_list ComboBox, który zostanie uzupełniony.
     * @param alert_text Etykieta przechowująca informację o niepowodzeniu w wykonaniu operacji.
     */
    static void listGenres(ComboBox<?> genre_list, Label alert_text) {
        try {
            String temp_sql = "select * from gatunek";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql);
                ResultSet res = statement.executeQuery();
                ObservableList genres = FXCollections.observableArrayList();
                while (res.next()) {
                    genres.add(res.getString("nazwa_gatunek"));
                }
                statement.close();
                genre_list.setItems(genres);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda uzupełniająca wprowadzony element typu ComboBox
     * o wydawnictwa w bazie danych.
     * @param publisher_list ComboBox, który zostanie uzupełniony.
     * @param alert_text Etykieta przechowująca informację o niepowodzeniu w wykonaniu operacji.
     */
    static void listPublishers(ComboBox<?> publisher_list, Label alert_text) {
        try {
            String temp_sql = "select * from wydawnictwo";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql);
                ResultSet res = statement.executeQuery();
                ObservableList publishers = FXCollections.observableArrayList();
                while (res.next()) {
                    publishers.add(res.getString("nazwa_wydawnictwo"));
                }
                statement.close();
                publisher_list.setItems(publishers);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }

    /**
     * Metoda uzupełniająca wprowadzony element typu ComboBox
     * o ceny i długości wypożyczenia danej książki.
     * @param price_list ComboBox, który zostanie uzupełniony.
     * @param alert_text Etykieta przechowująca informację o niepowodzeniu w wykonaniu operacji.
     * @param id_ksiazka Indeks książki dla której zostaną wybrane ceny i długości wypożyczenia.
     */
    static void listPrices(ComboBox<?> price_list, Label alert_text, int id_ksiazka) {
        try {
            String temp_sql = "select * from czas_wynajmu where id_ksiazka=" + id_ksiazka;
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(temp_sql);
                ResultSet res = statement.executeQuery();
                ObservableList prices = FXCollections.observableArrayList();
                while (res.next()) {
                    prices.add(res.getString("cena") + " (" + res.getString("dlugosc_najmu") + " dni)");
                }
                statement.close();
                price_list.setItems(prices);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            alert_text.setText(e.getMessage());
        }
    }
}
