package biblioteks.biblioteks;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasa obsługująca okno szczegółów o danej książce.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku book_details-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class BookDetailsController {

    @FXML
    private Label selected_amount;

    /**
     * Przycisk wysyłający zapytanie do bazy danych.
     */
    @FXML
    private Button add_btn;

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Etykieta pokazująca, ile sztuk danej książki jest dostępne.
     */
    @FXML
    private Label amount;

    /**
     * Etykieta pokazująca imię i nazwisko autora książki.
     */
    @FXML
    private Label author;

    /**
     * Etykieta pokazująca opis książki.
     */
    @FXML
    private Label desc;

    /**
     * Etykieta pokazująca gatunek książki.
     */
    @FXML
    private Label genre;

    /**
     * Lista opcji wypożyczenia książki - cena (czas w dniach).
     */
    @FXML
    private ComboBox<?> price_list;

    /**
     * Etykieta pokazująca wydawnictwo danej książki.
     */
    @FXML
    private Label publisher;

    /**
     * Etykieta pokazująca tytuł książki.
     */
    @FXML
    private Label title;

    /**
     * Etykieta pokazująca rok wydania książki.
     */
    @FXML
    private Label year;

    /**
     * Slider, który służy do wybrania ilości sztuk książek do wypożyczenia.
     */
    @FXML
    private Slider amount_sel;

    /**
     * Obszar tekstowy w którym można dodać szczegóły opinii o książce.
     */
    @FXML
    private TextArea rating_desc;

    /**
     * Slider, który służy do wybrania oceny książki w skali 0 - 5.
     */
    @FXML
    private Slider rating_slider;

    /**
     * Tabela pokazująca wszystkie opinie użytkowników o danej książce.
     */
    @FXML
    private TableView<ObservableList> rating_table;

    /**
     * Tytuł opinii.
     */
    @FXML
    private TextField rating_title;

    /**
     * Wybrana przez użytkownika wartość oceny książki.
     */
    @FXML
    private Label rating_val;

    /**
     * Indeks książki, której szczegóły są wyświetlane.
     */
    private int sel = 0;

    /**
     * Cena wybrana z listy czasu wypożyczenia.
     */
    private float cost = 0.0f;

    /**
     * Czas trwania wypożyczenia (w dniach).
     */
    private int duration = 0;

    /**
     * Metoda dodaje obsługę wciśnięcia przycisku Dodaj do Koszyka.
     * Metoda wysyła do koszyka daną książkę i odpowiadające jej wartości,
     * po czym zamyka szczegóły danej książki.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) {
        CartController.addItem(sel, (int) amount_sel.getValue(), cost, duration);
        Alert added = new Alert(Alert.AlertType.INFORMATION);
        added.setTitle("Dodano");
        added.setHeaderText("Dodano książkę");
        added.setContentText("Pomyślnie dodano książkę do koszyka.");
        added.showAndWait();
        Stage stage = (Stage) add_btn.getScene().getWindow();
        stage.close();
    }

    /**
     * Metoda obsługująca wyświetlenie listy opcji wypożyczenia danej książki.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onPriceListSelected(MouseEvent event) {
        Functions.listPrices(price_list, alert_text, sel);
    }

    /**
     * Metoda wywoływana po wybraniu czasu wypożyczenia.
     * Jeżeli użytkownik pomyślnie wybrał czas wypożyczenia danej książki,
     * wówczas dostaje możliwość dodania książki do koszyka.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onPriceSelected(ActionEvent event) {
        if (price_list.getSelectionModel().getSelectedItem() != null) {
            add_btn.setDisable(false);
            String temp = price_list.getSelectionModel().getSelectedItem().toString();
            cost = Float.parseFloat(temp.substring(0, temp.indexOf(" ")));
            duration = Integer.parseInt(temp.substring(temp.indexOf("(") + 1, temp.indexOf(" d")));
        }
    }

    /**
     * Metoda aktualizująca wyświetlaną ilość książek, wybraną przez użytkownika.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void updateValue(MouseEvent event) {
        selected_amount.setText("" + (int) amount_sel.getValue());
    }

    /**
     * Metoda dodaje obsługę wysłania opinii o książce.
     * Metoda wysyła do bazy danych opinię uzupełnioną przez użytkownika.
     * Jeżeli opinia nie została dobrze uzupełniona, wówczas wewnątrz
     * okna pojawi się odpowiednia informacja.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddReviewPressed(ActionEvent event) {
        try {
            String sql_str = "insert into opinia (id_ksiazka, naglowek, ocena, tresc, email) values (?, ?, ?, ?, ?)";
            Connection conn = PGSQL.makeConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql_str);
                statement.setInt(1, sel);
                statement.setString(2, rating_title.getText());
                statement.setDouble(3, rating_slider.getValue());
                statement.setString(4, rating_desc.getText());
                statement.setString(5, CartController.getEmail());
                statement.executeUpdate();
                statement.close();
                Functions.fetchTable("select * from funkcja_opinii(" + sel + ")", rating_table, alert_text);
            } else {
                alert_text.setText("Nie można połączyć się z bazą.");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("P0001")) {
                String str = e.getMessage().replace("ERROR: ", "").replace("\\n", "\n");
                str = str.substring(0, str.lastIndexOf("\n\n"));
                alert_text.setText(str);
            } else {
                alert_text.setText(e.getMessage());
            }

        }
    }

    /**
     * Metoda aktualizująca wyświetlaną wartość oceny w opinii, wybraną przez użytkownika.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onRatingSelected(MouseEvent event) {
        rating_val.setText("" + rating_slider.getValue());
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * Metoda uzupełnia poszczególne informacje o wybranej książce
     * oraz sprawdza, czy użytkownik może wypożyczyć książkę. Jest to
     * zrealizowane poprzez sprawdzenie ilości dostępnych książek.
     */
    public void initialize() {
        if (Functions.selected_id != 0) {
            try {
                sel = Functions.selected_id;
                Functions.fetchTable("select * from funkcja_opinii(" + sel + ")", rating_table, alert_text);
                String sql_str = "select * from lista_ksiazek_uzytkownik where \"ID\"=" + sel;
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql_str);
                    ResultSet res = statement.executeQuery();
                    while (res.next()) {
                        title.setText(res.getString("Tytuł"));
                        author.setText(res.getString("Autor"));
                        genre.setText(res.getString("Gatunek"));
                        publisher.setText(res.getString("Wydawnictwo"));
                        year.setText(res.getString("Rok wydania"));
                        if (!res.getString("Opis").equals(""))
                            desc.setText(res.getString("Opis"));
                        amount.setText(res.getString("Dostępna ilość"));
                        int count = res.getInt("Dostępna ilość");
                        if (count != 0) {
                            amount_sel.setMax(count);
                            selected_amount.setText("" + (int) amount_sel.getValue());
                            amount_sel.setDisable(false);
                        } else {
                            price_list.setDisable(true);
                        }
                    }
                    statement.close();
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
