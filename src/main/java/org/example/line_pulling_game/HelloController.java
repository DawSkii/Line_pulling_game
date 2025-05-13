package org.example.line_pulling_game;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Button;

import java.awt.print.PrinterGraphics;
import java.io.PrintWriter;
import java.net.Socket;

public class HelloController {
    @FXML
    private ChoiceBox teamSelect;
    @FXML
    private Button increaseProgressBar;
    @FXML
    private ProgressBar progressBar = new ProgressBar(0.5f);

    private final int serverPort = 4444;
    private final String serverIP = "localhost";
    private PrintWriter out;
    private Socket socket;

    @FXML
    void chooseTeam() {
        String team = teamSelect.getValue().toString().split(" ")[0];
        out.println("PULL " + team);
    }
    @FXML
    protected void onButtonClick() {
        float progress = 0.1f;
        progressBar.setProgress(progressBar.getProgress() + progress);
    }
}