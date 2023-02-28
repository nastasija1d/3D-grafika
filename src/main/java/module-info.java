module com.example.rgdz2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rgdz2 to javafx.fxml;
    exports com.example.rgdz2;
}