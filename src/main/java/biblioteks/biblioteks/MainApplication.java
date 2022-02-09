package biblioteks.biblioteks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Główna klasa programu, tworząca podstawowe okno programu
 * oraz posiadająca metody do zmieniania tego, co widać
 * w danym momencie w aplikacji.
 */
public class MainApplication extends Application {
    private static Stage stg;

    /**
     * Metoda main, wywoływana podczas uruchomienia aplikacji.
     * Funkcja launch() uruchamia okno JavaFX.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Metoda start tworzy okno o rozmiarze 600x400 pikseli,
     * pokazuje wewnątrz niego główny widok main-view.fxml
     * oraz nadaje tytuł okna - BiblioteKS.
     *
     * @param stage Element typu Stage, niezbędny do wyświetlania.
     */
    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("BiblioteKS");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metoda changeScene zmienia scenę, czyli pokazuje nowy widok wewnątrz
     * aplikacji.
     *
     * @param fxml Nowy widok, który zostanie wyświetlony w apliakcji.
     */
    public void changeScene(String fxml) throws IOException {
        Parent new_scene = FXMLLoader.load(getClass().getResource(fxml));
        stg.getScene().setRoot(new_scene);
    }
}