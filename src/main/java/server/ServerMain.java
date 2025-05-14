package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {

    private static final int portNumber = 4444;
    static float score = 0.5f;
    private static final float WIN_LEFT = 0f;
    private static final float WIN_RIGHT = 1f;
    private static final List<ClientHandler> clients = new ArrayList<>();


    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(portNumber)){
            System.out.println("Server został uruchomiony...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
        }

    }

    static synchronized void updateScore(String team) {
        float change = 0.1f;
        if (team.equals("LEFT")) score = Math.min(WIN_RIGHT, score + change);
        else if (team.equals("RIGHT")) score = Math.max(WIN_LEFT, score - change);
        broadcastScore();
        if (score <= WIN_LEFT || score >= WIN_RIGHT) {
            System.out.println("Wygrana " + team);
            sendWinner(team);
            score = 0.5f;
        }
    }

    static synchronized void broadcastScore(){
        for (ClientHandler client : clients) {
            client.sendScore(score);
        }
    }
    static synchronized void sendWinner(String team){
        for (ClientHandler client : clients) {
            client.sendWinner(team);
        }
    }
}
