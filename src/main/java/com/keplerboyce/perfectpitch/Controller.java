package com.keplerboyce.perfectpitch;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class Controller {
    @FXML private Label results;
    @FXML private Label rate;
    @FXML private Button reset;
    @FXML private Button repeat;
    @FXML private Button reveal;
    @FXML private HBox topRow;
    @FXML private HBox bottomRow;

    private int total = 0;
    private int correct = 0;
    private boolean guessed = false;
    private boolean gotCorrect = false;
    private boolean started = false;
    private boolean revealed = false;
    private String currentNote;

    private static final int NOTE_DURATION_MS = 500;
    private static final int SAMPLE_RATE = 16 * 1024;
    private static final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);

    private final String[] topRowTexts = {"C", "D", "E", "F", "G", "A", "B"};
    private final String[] bottomRowTexts = {"C#/Db", "D#/Eb", "F#/Gb", "G#/Ab", "A#/Bb"};
    private final HashMap<String, Button> buttons = new HashMap<>();

    public void initialize() {
        randomizeNote();
        updateResults();
        addButtonsToBox(topRowTexts, topRow);
        addButtonsToBox(bottomRowTexts, bottomRow);
        reset.setOnAction(e -> handleReset());
        repeat.setOnAction(e -> handleRepeat());
        reveal.setOnAction(e -> handleReveal());
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
        if (!started || revealed) {
            return;
        }
        handleGuess(note);
        updateResults();
    }

    private void randomizeNote() {
        String[] allNotes = Stream.concat(Arrays.stream(topRowTexts), Arrays.stream(bottomRowTexts))
            .toArray(String[]::new);
        currentNote = allNotes[ThreadLocalRandom.current().nextInt(allNotes.length)];
    }

    private void updateResults() {
        results.setText(String.format("%d/%d", correct, total));
        rate.setText(String.format("%.1f%%", Double.isNaN(100.0 * correct/total) ?  0.0 : 100.0 * correct/total));
    }

    private void handleGuess(String note) {
        if (!guessed) {
            total++;
            results.setText(String.format("%d/%d", correct, total));
        }
        if (note.equals(currentNote)) {
            if (!guessed) {
                correct++;
            }
            gotCorrect = true;
            reveal.setText("Next");
            Button button = buttons.get(note);
            button.getStyleClass().add("bg-green");
        } else if (!gotCorrect) {
            Button button = buttons.get(note);
            button.getStyleClass().add("bg-gray");
            button.getStyleClass().add("text-gray");
        }
        guessed = true;
    }

    private void handleReset() {
        total = 0;
        correct = 0;
        guessed = false;
        gotCorrect = false;
        started = false;
        revealed = false;
        repeat.setText("Play Note");
        reveal.setText("Reveal");
        updateResults();
        clearColors();
        randomizeNote();
    }

    private void handleRepeat() {
        started = true;
        repeat.setText("Repeat Note");
        playCurrentNote();
    }

    private void handleReveal() {
        if (!started) {
            return;
        }
        if (!gotCorrect && !revealed) {
            if (!guessed) {
                total++;
            }
            revealed = true;
            updateResults();
            results.setText(String.format("%d/%d %s", correct, total, currentNote));
            reveal.setText("Next");
            return;
        }
        reveal.setText("Reveal");
        guessed = false;
        gotCorrect = false;
        revealed = false;
        updateResults();
        clearColors();
        randomizeNote();
        playCurrentNote();
    }

    private void clearColors() {
        for (Button button : buttons.values()) {
            button.getStyleClass().remove("bg-green");
            button.getStyleClass().remove("bg-gray");
            button.getStyleClass().remove("text-gray");
        };
    }

    private void playCurrentNote() {
        Map<String, Double> noteFreqs = Map.ofEntries(
            Map.entry("C", 261.63),
            Map.entry("C#/Db", 277.18),
            Map.entry("D", 293.66),
            Map.entry("D#/Eb", 311.13),
            Map.entry("E", 329.62),
            Map.entry("F", 349.23),
            Map.entry("F#/Gb", 369.99),
            Map.entry("G", 392.00),
            Map.entry("G#/Ab", 415.30),
            Map.entry("A", 440.00),
            Map.entry("A#/Bb", 466.16),
            Map.entry("B", 493.88)
        );
        new Thread(() -> playFrequency(noteFreqs.get(currentNote))).start();
    }

    private void playFrequency(double freq) {
        SourceDataLine line;
        try {
            line = AudioSystem.getSourceDataLine(audioFormat);
            line.open(audioFormat, SAMPLE_RATE);
            line.start();
            byte[] toneBuffer = createSinWaveBuffer(freq);
            line.write(toneBuffer, 0, toneBuffer.length);
            line.drain();
            line.close();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] createSinWaveBuffer(double freq) {
        int samples = (int) ((NOTE_DURATION_MS * SAMPLE_RATE) / 1000);
        byte[] output = new byte[samples];
        double period = (double) SAMPLE_RATE / freq;
        for (int i = 0; i < output.length; i++) {
            double angle = 2.0 * Math.PI * i / period;
            output[i] = (byte) (Math.sin(angle) * 127f);
        }
        return output;
    }
}
