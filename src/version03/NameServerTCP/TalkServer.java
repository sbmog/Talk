package version03.NameServerTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TalkServer {
    private int port;
    private Map<String, String> klienter = Collections.synchronizedMap(new HashMap<>());

    public TalkServer(int port) {
        this.port = port;
    }

    public void start() {
      klienter.put("TRISTAN", "127,0,0,1");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TalkServer kører på port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Klient forbundet: " + socket.getInetAddress());

                // Start tråde for klienten
                new ClientHandler(socket, klienter).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TalkServer(12080).start();
    }
}