module biblioteks.biblioteks {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens biblioteks.biblioteks to javafx.fxml;
    exports biblioteks.biblioteks;
}