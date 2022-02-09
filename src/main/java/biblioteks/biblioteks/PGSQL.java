package biblioteks.biblioteks;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Klasa zawiera metodę niezbędną do połączenia się z bazą danych PostgreSQL.
 */
public class PGSQL {
    /**
     * Zmienna typu Connection, która symbolizuje połączenie z bazą danych.
     */
    private static Connection connection;

    /**
     * Metoda łącząca się z bazą danych PostgreSQL na stronie <a href='https://www.elephantsql.com/'>ElephantSQL</a>.
     * W przypadku nieudanego połączenia z bazą danych, metoda pokazuje okno informujące o tym.
     *
     * @return Connection pozwalające na wykonywanie zapytań do bazy.
     */
    static Connection makeConnection() throws SQLException {
        if (connection == null) {
            try {
                String url = "";
                String email = "";
                String pass = "";
                connection = DriverManager.getConnection(url, email, pass);
                return connection;
            } catch (SQLException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Błąd");
                error.setHeaderText("Nie udało się połączyć z bazą danych!");
                error.setContentText(e.getMessage() + "\nProszę spróbować później.");
                error.showAndWait();
                return null;
            }
        } else {
            return connection;
        }
    }
}
