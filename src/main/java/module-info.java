module org.example.genetic {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.example.genetic to javafx.fxml;
    exports org.example.genetic;
}