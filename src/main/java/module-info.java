module org.openjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    // javax
    requires java.desktop;

    opens org.openjfx to javafx.fxml;
    exports org.openjfx;
}
