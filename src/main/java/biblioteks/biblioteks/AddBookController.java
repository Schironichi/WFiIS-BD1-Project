package biblioteks.biblioteks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddBookController {

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
     * Lista autorów.
     */
    @FXML
    private ComboBox<String> author_list;

    /**
     * Opis szczegółowy książki.
     */
    @FXML
    private TextArea desc;

    /**
     * Lista gatunków książek.
     */
    @FXML
    private ComboBox<String> genre_list;

    /**
     * Lista wydawnictw.
     */
    @FXML
    private ComboBox<String> publisher_list;

    /**
     * Tytuł książki.
     */
    @FXML
    private TextField title;

    /**
     * Rok wydania książki.
     */
    @FXML
    private TextField year;

    /**
     * Zapytanie wysyłane do bazy danych.
     */
    private String sql_str = "";

    /**
     * Metoda dodająca obsługę wciśnięcia przycisku Dodaj/Zmień.
     * Metoda sprawdza poprawność danych, po czym wysyła zapytanie do bazy
     * danych i w zależności od statusu operacji, wyświetla w widoku
     * odpowiednią informację.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) throws IOException {
        try {
            if (sql_str.equals("")) {
                sql_str = "insert into ksiazka (id_autor, tytul, opis, nazwa_gatunek, nazwa_wydawnictwo, rok_wydania, wypozyczona_ilosc) values (?, ?, ?, ?, ?, ?, 0)";
            }
            Connection conn = PGSQL.makeConnection();
            String text = "";
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                if (author_list.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano autora.\n";
                } else {
                    String temp = author_list.getSelectionModel().getSelectedItem();
                    temp = temp.substring(0, temp.indexOf(':'));
                    statement.setInt(1, Integer.parseInt(temp));
                }
                if (title.getText().equals("")) {
                    text += "Nie podano tytułu.\n";
                } else {
                    statement.setString(2, title.getText());
                }
                statement.setString(3, desc.getText());
                if (genre_list.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano gatunku.\n";
                } else {
                    String temp = genre_list.getSelectionModel().getSelectedItem();
                    statement.setString(4, temp);
                }
                if (publisher_list.getSelectionModel().getSelectedItem() == null) {
                    text += "Nie wybrano wydawnictwa.\n";
                } else {
                    String temp = publisher_list.getSelectionModel().getSelectedItem();
                    statement.setString(5, temp);
                }
                if (!year.getText().equals("")) {
                    try {
                        statement.setInt(6, Integer.parseInt(year.getText()));
                    } catch (Exception e) {
                        text += "Podano nieprawidłowy rok.\n";
                    }
                } else {
                    text += "Nie podano roku wydania.\n";
                }
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
     * Metoda obsługująca wyświetlenie listy autorów w bazie danych.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAuthorListSelected(MouseEvent event) {
        Functions.listAuthors(author_list, alert_text);
    }

    /**
     * Metoda obsługująca wyświetlenie listy gatunków w bazie danych.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onGenreListSelected(MouseEvent event) {
        Functions.listGenres(genre_list, alert_text);
    }

    /**
     * Metoda dodaje obsługę przycisku dodającego nowego autora.
     * Metoda otwiera nowe okno w którym pracownik może dodać
     * nowego autora do bazy danych (add_author-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onNewAuthorPressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("add_author-view.fxml")), "BiblioteKS - nowy autor");
    }

    /**
     * Metoda dodaje obsługę przycisku dodającego nowy gatunek.
     * Metoda otwiera nowe okno w którym pracownik może dodać
     * nowy gatunek do bazy danych (add_genre-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onNewGenrePressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("add_genre-view.fxml")), "BiblioteKS - nowy gatunek");
    }

    /**
     * Metoda dodaje obsługę przycisku dodającego nowe wydawnictwo.
     * Metoda otwiera nowe okno w którym pracownik może dodać
     * nowe wydawnictwo do bazy danych (add_publisher-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onNewPublisherPressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("add_publisher-view.fxml")), "BiblioteKS - nowe wydawnictwo");
    }

    /**
     * Metoda obsługująca wyświetlenie listy wydawnictw w bazie danych.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onPublisherListSelected(MouseEvent event) {
        Functions.listPublishers(publisher_list, alert_text);
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * Dodaje ona obsługę modyfikacji danej książki.
     * Jeżeli użytkownik wybrał opcję modyfikacji książki,
     * wówczas wszystkie pola zostają uzupełnione o odpowiednie
     * wartości.
     */
    public void initialize() {
        if (Functions.selected_id != 0) {
            try {
                sql_str = "select * from ksiazka where id_ksiazka=" + Functions.selected_id;
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    add_btn.setText("Zmień");
                    window_name.setText("ZMIEŃ KSIĄŻKĘ");
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        title.setText(res.getString("tytul"));
                        if (!res.getString("opis").equals("")) {
                            desc.setText(res.getString("opis"));
                        }
                        genre_list.setValue(res.getString("nazwa_gatunek"));
                        publisher_list.setValue(res.getString("nazwa_wydawnictwo"));
                        if (!res.getString("rok_wydania").equals("")) {
                            year.setText(res.getString("rok_wydania"));
                        }
                    }
                    statement.close();
                    sql_str = "update ksiazka set id_autor=?, tytul=?, opis=?, nazwa_gatunek=?, nazwa_wydawnictwo=?, rok_wydania=? where id_ksiazka=" + Functions.selected_id;
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
