package com.keplerboyce.perfectpitch;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class Controller {
    @FXML
    private Label notePressed;

    @FXML
    private Button C;
    @FXML
    private Button D;

    public void initialize() {
        one.setOnAction(e -> numChange(1));
        two.setOnAction(e -> numChange(2));
    }

    protected void noteClick(String note) {
        notePressed.setText(note);
    }
}
