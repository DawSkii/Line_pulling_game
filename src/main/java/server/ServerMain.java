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


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server started");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    static synchronized void updateScore(String team) {
        if (team.equals("LEFT")) score = Math.max(WIN_LEFT, score - 1);
        else if (team.equals("RIGHT")) score = Math.min(WIN_RIGHT, score + 1);
    }

    static synchronized void broadcastScore(){
        for (ClientHandler client : clients) {
            client.sendScore(score);
        }
    }
}
