package com.keplerboyce.perfectpitch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class Main {
    public static class App extends Application {
        @Override
        public void start(Stage stage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 480, 360);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
            stage.setTitle("Perfect Pitch Trainer");
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(App.class);
    }
}
