package version03.NameServerTCP;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandler extends Thread {
    private Socket socket;
    private Map<String, String> clients;

    public ClientHandler(Socket socket, Map<String, String> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {


            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(" ");


                if (parts[0].equalsIgnoreCase("REGISTRER") && parts.length == 2) {
                    String navn = parts[1];
                    String ipPort = socket.getInetAddress().getHostAddress() + ":15000";
                    clients.put(navn, ipPort);

                    out.writeBytes("Velkommen " + navn + "! Kommandoer: LISTE, CONNECT <name>\n");

                } else if (parts[0].equalsIgnoreCase("LISTE")) {
                    for (Map.Entry<String, String> entry : clients.entrySet()) {
                        out.writeBytes(entry.getKey() + " -> " + entry.getValue() + "\n");
                    }
                    out.writeBytes("END_OF_LIST\n");

                } else if (parts[0].equalsIgnoreCase("CONNECT") && parts.length == 2) {
                    String target = parts[1];
                    String ipPort = clients.get(target);
                    if (ipPort != null) {
                        out.writeBytes("CONNECT_IP " + ipPort + "\n");
                    } else {
                        out.writeBytes("Ingen klient med dette navn: " + target + "\n");
                    }

                } else {
                    out.writeBytes("Forkert kommando\n");
                }
            }

        } catch (IOException e) {
            System.out.println("Klient forbindelse lukket" );
        }
    }
}
