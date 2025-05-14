package org.example.line_pulling_game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HelloController {
    @FXML
    private ChoiceBox<String> teamSelect;
    @FXML
    private Button increaseProgressBar;
    @FXML
    private ProgressBar progressBar;

    private PrintWriter out;
    private Socket socket;
    private String team;
    private BufferedReader in;
    private boolean isConnected = false;
    private Thread serverListener;

    @FXML
    public void initialize() {
        final int serverPort = 4444;
        final String serverIP = "localhost";

        progressBar.setProgress(0.5f);

        teamSelect.getItems().addAll("Team LEFT", "Team RIGHT");

        try {
            socket = new Socket(serverIP, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isConnected = true;
            serverListener = new Thread(this::listenToServer);
            serverListener.setDaemon(true);
            serverListener.start();

            System.out.println("Połączono z serwerem");
        } catch (Exception e) {
            System.err.println("Nie połączono do servera: " + e.getMessage());
            showError("Błąd połączenia", "Nie można połączyć się z serwerem. " + e.getMessage());
            isConnected = false;
        }
    }

    @FXML
    void chooseTeam() {
        if (teamSelect.getValue() == null) return;
        team = teamSelect.getValue().split(" ")[1];
    }

    @FXML
    protected void onButtonClick() {
        if (!isConnected) {
            showError("Brak połączenia", "Brak połączenia z serwerem!");
            return;
        }

        if (team == null) {
            showError("Wybierz drużynę", "Musisz najpierw wybrać drużynę!");
            return;
        }

        out.println("PULL " + team);

    }

    private void listenToServer() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("PROGRESS")) {
                    final float progress = Float.parseFloat(serverMessage.split(" ")[1]);
                    Platform.runLater(() -> updateProgressBar(progress));
                }else if(serverMessage.startsWith("WINNER")){
                    final String winner = serverMessage.split(" ")[1];
                    Platform.runLater(() -> showWinner(winner));
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas odbierania danych z serwera: " + e.getMessage());
        }
    }

    private void updateProgressBar(double progress) {
        progressBar.setProgress(progress);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showWinner(String winner){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wygrana");
        alert.setContentText("Wygrana " + winner);
        alert.showAndWait();
    }

    public void onStop() {
        if (serverListener != null) {
            serverListener.interrupt();
        }

        if (socket != null && out != null) {
            try {
                socket.close();
                if (in != null) in.close();
                out.close();
                System.out.println("Zamknięto połączenie z serwerem");
            } catch (Exception e) {
                System.err.println("Błąd zamknięcia połączenia: " + e.getMessage());
            }
        }
    }
}