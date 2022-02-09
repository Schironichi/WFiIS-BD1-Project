package biblioteks.biblioteks;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Klasa kontrolująca widok listy wybranych elementów.
 * Struktura klasy wygenerowana przez program Scene Builder
 * dla pliku list-view.fxml, rozwinięta o własną funkcjonalność.
 */
public class ListController {

    /**
     * Etykieta zawierająca informacje odnośnie
     * przeprowadzanych operacji.
     */
    @FXML
    private Label alert_text;

    /**
     * Przycisk do usuwania obiektów.
     */
    @FXML
    private Button del_btn;

    /**
     * Przycisk do modyfikowania obiektów.
     */
    @FXML
    private Button modify_btn;

    /**
     * Tabela w której znajdują się elementy.
     */
    @FXML
    private TableView<ObservableList> table;

    /**
     * Nazwa pliku .fxml, który zostanie wyświetlony
     * przy opcji dodania/modyfikacji elementu.
     */
    private String add_fxml;

    /**
     * Zapytanie wysyłane do bazy danych.
     */
    private String table_sql;

    /**
     * Nazwa otwieranego przez kontroler okna.
     */
    private String win_name;

    /**
     * Zapytanie wysyłane do bazy danych
     * w celu usunięcia elementu.
     */
    private String del_sql;

    /**
     * Zmienna informaująca o tym, czy indeks jest
     * typu String, czy nie.
     */
    private boolean is_string = false;

    /**
     * Obsługa przycisku dodawania elementu.
     * Po zamknięciu nowgo okna, lista elementów
     * zostaje zaktualizowana.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onAddPressed(ActionEvent event) throws IOException {
        Functions.createWindow(FXMLLoader.load(getClass().getResource(add_fxml)), win_name);
        Functions.fetchTable(table_sql, table, alert_text);
    }

    /**
     * Obsługa przycisku usuwania elementu.
     * Po wykonaniu operacji, lista elementów
     * zostaje zaktualizowana.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onDeletePressed(ActionEvent event) {
        if (table.getSelectionModel().getSelectedItem() != null) {
            try {
                Connection conn = PGSQL.makeConnection();
                if (conn != null) {
                    PreparedStatement statement;
                    if (is_string) {
                        statement = conn.prepareStatement(del_sql + table.getSelectionModel().getSelectedItem().get(0) + "'");
                    } else if (Functions.view.equals("Del_Books")) {
                        statement = conn.prepareStatement(del_sql + table.getSelectionModel().getSelectedItem().get(0) + " and id_ksiazka=" + table.getSelectionModel().getSelectedItem().get(1));
                    } else {
                        statement = conn.prepareStatement(del_sql + table.getSelectionModel().getSelectedItem().get(0));
                    }
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
        Functions.fetchTable(table_sql, table, alert_text);
    }

    /**
     * Obsługa przycisku modyfikowania elementu.
     * Po zamknięciu nowgo okna, lista elementów
     * zostaje zaktualizowana.
     * @param event Zdarzenie wywołujące metodę.
     */
    @FXML
    void onModifyPressed(ActionEvent event) throws IOException {
        if (table.getSelectionModel().getSelectedItem() != null) {
            if (is_string) {
                Functions.selected_email = table.getSelectionModel().getSelectedItem().get(0).toString();
            } else {
                Functions.selected_id = Integer.parseInt(table.getSelectionModel().getSelectedItem().get(0).toString());
            }
            onAddPressed(event);
        } else {
            alert_text.setText("Nie wybrano elementu z listy!");
        }
        Functions.fetchTable(table_sql, table, alert_text);
    }

    /**
     * Metoda wywoływana po aktywacji kontrolera.
     * W zależności od tego, co ma zostać wyświetlone,
     * metoda odpowiednio ustawia parametry klasy.
     * Na koniec odświeża tabelę z obiektami.
     */
    public void initialize() {
        switch (Functions.view) {
            case "Book":
                table_sql = "select * from lista_ksiazek_pracownik";
                add_fxml = "add_book-view.fxml";
                win_name = "BiblioteKS - książka";
                del_sql = "delete from ksiazka where id_ksiazka=";
                break;
            case "Delivery":
                table_sql = "select * from lista_dostaw";
                add_fxml = "add_delivery-view.fxml";
                win_name = "BiblioteKS - dostawa";
                del_sql = "delete from dostawa where id_dostawa=";
                break;
            case "Warehouse":
                table_sql = "select * from lista_hurtownii";
                add_fxml = "add_warehouse-view.fxml";
                win_name = "BiblioteKS - hurtownia";
                is_string = true;
                del_sql = "delete from hurtownia where hurt_email='";
                break;
            case "Author":
                table_sql = "select * from lista_autorow";
                add_fxml = "add_author-view.fxml";
                win_name = "BiblioteKS - autor";
                del_sql = "delete from autor where id_autor=";
                break;
            case "Del_Books":
                table_sql = "select * from lista_dk";
                modify_btn.setVisible(false);
                add_fxml = "fill_delivery-view.fxml";
                win_name = "BiblioteKS - uzupełnienie dostawy";
                del_sql = "delete from dostawa_ksiazka where id_dostawa=";
                break;
            case "Book-Price":
                table_sql = "select * from lista_cen_ksiazki";
                add_fxml = "fill_book_prices-view.fxml";
                win_name = "BiblioteKS - ceny najmu książek";
                del_sql = "delete from czas_wynajmu where id_wynajmu=";
                break;
            default:
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Podany widok nie istnieje");
                alert.setContentText("Po zatwierdzeniu tego okna zostaniesz przekierowany do strony głównej");
                alert.showAndWait();
                Stage stg = (Stage) del_btn.getScene().getWindow();
                stg.close();
                return;
        }
        Functions.fetchTable(table_sql, table, alert_text);
    }
}