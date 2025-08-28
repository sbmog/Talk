package version03.NameServerUDP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TalkServerUDP {
    public static void main(String[] args) throws IOException {
        int port = 12080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("TalkServer klar på port " + port);

        try (Socket connectionSocket = serverSocket.accept()) {
            System.out.println("Klient forbundet: " + connectionSocket.getInetAddress());

            Thread reciever = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Klient siger: " + line);
                    }
                } catch (IOException e) {
                    System.out.println("Modtagelse afbrudt.");
                }
            });

            Thread sender = new Thread(() -> {
                try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                     PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true)) {
                    String line;
                    while ((line = userInput.readLine()) != null) {
                        out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Afsendelse afbrudt.");
                }
            });

            reciever.start();
            sender.start();

            try {
                reciever.join();
                sender.join();
            } catch (InterruptedException e) {
                System.out.println("Tråde blev afbrudt");
            }
        }
    }
}
