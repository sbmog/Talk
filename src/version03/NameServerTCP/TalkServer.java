package version03.NameServerTCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TalkServer {

    // Alle klienters output streams
    private static final Set<DataOutputStream> clientOutputs =
            Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12080);
        System.out.println("TalkServer running on port 12080...");

        // Start tråd så serveren selv kan skrive i chatten
        new Thread(TalkServer::serverInputLoop).start();

        // Accepter klienter i en uendelig løkke
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            clientOutputs.add(out);

            // Start en tråd til at håndtere denne klient
            new Thread(() -> handleClient(clientSocket, out)).start();
        }
    }

    private static void handleClient(Socket socket, DataOutputStream out) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while ((msg = in.readLine()) != null) {
                // Log på serveren
                System.out.println("[CLIENT " + socket.getInetAddress() + "]: " + msg);
                // Send til alle andre klienter
                broadcast(msg, out);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            clientOutputs.remove(out);
        }
    }

    // Serveren kan selv skrive beskeder
    private static void serverInputLoop() {
        try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = stdin.readLine()) != null) {
                // Udskriv lokalt
                System.out.println("[SERVER]: " + line);
                // Send til alle klienter
                broadcast("[SERVER]: " + line, null);
            }
        } catch (IOException e) {
            System.out.println("Server input stopped.");
        }
    }

    // Send besked til alle klienter undtagen senderOut (hvis != null)
    private static void broadcast(String message, DataOutputStream senderOut) {
        synchronized (clientOutputs) {
            for (DataOutputStream out : clientOutputs) {
                try {
                    if (out != senderOut) {
                        out.writeBytes(message + "\n");
                    }
                } catch (IOException ignored) {}
            }
        }
    }
}
