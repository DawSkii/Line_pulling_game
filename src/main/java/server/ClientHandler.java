package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static server.ServerMain.score;
import static server.ServerMain.updateScore;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;


    ClientHandler(Socket socket){
        this.socket = socket;
    }


    @Override
    public void run() {
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            sendScore(score);

            String line;
            while((line = in.readLine()) != null){
                if(line.startsWith("PULL")){
                    String team = line.split(" ")[1];
                    updateScore(team);
                }
            }
        }catch (IOException e){
            System.out.println("Błąd klienta: " + e.getMessage());
        }
    }


    void sendScore(float score){
        out.println("PROGRESS " + score);
    }
    void sendWinner(String team){out.println("WINNER " + team);}
}
