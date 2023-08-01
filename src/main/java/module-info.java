module com.keplerboyce.perfectpitch {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.keplerboyce.perfectpitch to javafx.fxml;
    exports com.keplerboyce.perfectpitch;
}