package version03.NameServerTCP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public class ClientHandler extends Thread {
    private Socket socket;
    private Map<String, String> clients;        //KEY: navn, VALUE: IP.

    public ClientHandler(Socket socket, Map<String, String> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            out.writeBytes("Velkommen! Brug REGISTRER kaldenavn - ip eller LIST\n");

            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(" ");

                if (parts[0].equalsIgnoreCase("REGISTRER") && parts.length == 3) {
                    clients.put(parts[1], parts[2]);         //Rigtig formateret, og tilføjet til map.
                    out.writeBytes("Registreret: " + parts[1] + " -> " + parts[2] + "\n");

                } else if (parts[0].equalsIgnoreCase("LIST")) {
                    synchronized (clients) {         //kun en tråd kan køre ad gangen.
                        for (Map.Entry<String, String> entry : clients.entrySet()) {
                            out.writeBytes(entry.getKey() + " -> " + entry.getValue() + "\n");
                        }
                    }
                    out.writeBytes("END_OF_LIST\n");
                } else {
                    out.writeBytes("Ugyldig kommando. Brug REGISTER eller LIST\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Klient afbrudt: " + e.getMessage());
        }
    }
}