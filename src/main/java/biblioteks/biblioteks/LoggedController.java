package biblioteks.biblioteks;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

/**
 * Klasa kontrolująca widok zalogowanego czytelnika.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku logged-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class LoggedController {

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Po wciśnięciu tego przycisku następuje
     * filtracja elementów w tabeli.
     */
    @FXML
    private Button search_btn;

    /**
     * Lista opcji filtracji.
     */
    @FXML
    private ComboBox<String> search_selection;

    /**
     * Lista elementów dostępnych dla wybranej
     * opcji filtracji.
     */
    @FXML
    private ComboBox<?> selection;

    /**
     * Element pozwalający na wpisanie tytułu książki
     * do filtru.
     */
    @FXML
    private TextField selection_title;

    /**
     * Tabela w której wyświetlane są wyniki.
     */
    @FXML
    private TableView<ObservableList> table;

    /**
     * Metoda dodaje obsługę przycisku "Koszyk".
     * Po wciśnięciu przycisku zostanie utworzone nowe
     * okno z widokiem na koszyk użytkownika (cart-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onMenuLeftButtonPressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("cart-view.fxml")), "BiblioteKS - koszyk");
    }

    /**
     * Metoda dodaje obsługę przycisku "Wyloguj".
     * Po wciśnięciu przycisku użytkownik zostanie wylogowany
     * oraz przekierowany do ekranu początkowego.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onMenuRightButtonPressed(ActionEvent event) throws IOException {
        Functions.logout();
    }

    /**
     * Metoda obsługująca wciśnięcie przycisku "Szukaj".
     * Po wciśnięciu przycisku następuje wykonanie
     * filtracji zgodnie z wybranymi elementami przez
     * użytkownika.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onSearchPressed(ActionEvent event) {
        String column = "";
        String name = search_selection.getSelectionModel().getSelectedItem();
        switch (name) {
            case "Tytuł":
                if (!selection_title.getText().equals("")) {
                    column = " where \"Tytuł\"='" + selection_title.getText() + "'";
                }
                break;
            case "Autor":
                String temp = selection.getSelectionModel().getSelectedItem().toString();
                temp = temp.substring(0, temp.indexOf(':'));
                column = " where\"" + name + "\"=" + temp;
                break;
            default:
                if (selection.getSelectionModel().getSelectedItem() != null) {
                    column = " where\"" + name + "\"='" + selection.getSelectionModel().getSelectedItem() + "'";
                }
                break;
        }
        Functions.fetchTable("select * from lista_ksiazek_uzytkownik" + column, table, alert_text);
    }

    /**
     * Metoda dodaje obsługę przycisku "Profil".
     * Po wciśnięciu przycisku użytkownik zostanie przekierowany
     * do swojego profilu (profile-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onProfilePressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource("profile-view.fxml")), "BiblioteKS - profil");
    }

    /**
     * Metoda wywoływana po dokonaniu wyboru w search_selection.
     * W zależności od wybranej pozycji z listy, metoda nadaje
     * widoczność oraz opcję edytowania odpowiednim elementom.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
     void onSearchSelectionPressed(ActionEvent event) {
        if (search_selection.getSelectionModel().getSelectedItem() != null) {
            if (search_selection.getSelectionModel().getSelectedItem().equals("Tytuł")) {
                selection.setVisible(false);
                selection.setDisable(true);
                selection_title.setVisible(true);
                selection_title.setDisable(false);
                search_btn.setDisable(false);
            } else {
                selection.getSelectionModel().clearSelection();
                search_btn.setDisable(true);
                selection_title.setVisible(false);
                selection_title.setDisable(true);
                selection.setVisible(true);
                selection.setDisable(false);
            }
        }
    }

    /**
     * Metoda pobiera listę elementów do selection w zależności
     * od tego, co zostało wybrane w search_selection.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onSelectionPressed(MouseEvent event) {
        switch (search_selection.getSelectionModel().getSelectedItem()) {
            case "Autor":
                Functions.listAuthors(selection, alert_text);
                break;
            case "Gatunek":
                Functions.listGenres(selection, alert_text);
                break;
            case "Wydawnictwo":
                Functions.listPublishers(selection, alert_text);
                break;
        }
        search_btn.setDisable(false);
    }

    /**
     * Metoda zajmuje się obsługą wybrania książki z listy.
     * Po kliknięciu w daną książkę, metoda otwiera nowe okno
     * w którym widoczne są informacje szczegółowe o danej
     * książce (book_details-view.fxml).
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onTableSelection(MouseEvent event) throws IOException {
        if (table.getSelectionModel().getSelectedItem() != null) {
            Functions.selected_id = Integer.parseInt(table.getSelectionModel().getSelectedItem().get(0).toString());
            Functions.createWindow(FXMLLoader.load(getClass().getResource("book_details-view.fxml")), "BiblioteKS - szczegóły książki");
        }
    }

    /**
     * Metoda wykoywana podczas aktywacji kontrolera.
     * Uzupełnia tabelę o książki z bazy danych.
     */
    public void initialize() {
        search_selection.getItems().addAll("Autor",
                "Tytuł",
                "Gatunek",
                "Wydawnictwo");
        Functions.fetchTable("select * from lista_ksiazek_uzytkownik", table, alert_text);
    }

}

