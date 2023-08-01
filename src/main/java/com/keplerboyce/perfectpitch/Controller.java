package com.keplerboyce.perfectpitch;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;


public class Controller {
    @FXML private Label results;
    @FXML private Label rate;
    @FXML private Button repeat;
    @FXML private Button reveal;
    @FXML private HBox topRow;
    @FXML private HBox bottomRow;

    private int correct = 0;
    private int total = 0;
    private boolean guessed = false;
    private String currentNote;

    private final String[] topRowTexts = {"C", "D", "E", "F", "G", "A", "B"};
    private final String[] bottomRowTexts = {"C#/Db", "D#/Eb", "F#/Gb", "G#/Ab", "A#/Bb"};
    private final HashMap<String, Button> buttons = new HashMap<>();

    public void initialize() {
        randomizeNote();
        updateResults();
        addButtonsToBox(topRowTexts, topRow);
        addButtonsToBox(bottomRowTexts, bottomRow);
    }

    private void addButtonsToBox(String[] texts, HBox box) {
        for (String text : texts) {
            Button button = new Button(text);
            button.setOnAction(e -> noteClick(text));
            button.setId(text);
            box.getChildren().add(button);
            buttons.put(text, button);
        }
    }

    private void noteClick(String note) {
        handleGuess(note);
        updateResults();
    }

    private void randomizeNote() {
        String[] allNotes = Stream.concat(Arrays.stream(topRowTexts), Arrays.stream(bottomRowTexts))
            .toArray(String[]::new);
        currentNote = allNotes[ThreadLocalRandom.current().nextInt(allNotes.length)];
    }

    private void updateResults() {
        results.setText(String.format("%d/%d %s", correct, total, currentNote));
    }

    private void handleGuess(String note) {
        if (!guessed) {
            total++;
        }
        if (note.equals(currentNote)) {
            if (!guessed) {
                correct++;
            }
            guessed = false;
            randomizeNote();
            for (Button button : buttons.values()) {
                button.getStyleClass().remove("bg-gray");
                button.getStyleClass().remove("text-gray");
            }
        } else {
            guessed = true;
            Button button = buttons.get(note);
            button.getStyleClass().add("bg-gray");
            button.getStyleClass().add("text-gray");
        }
    }
}
