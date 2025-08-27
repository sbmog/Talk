package version03.NameServerUDP;

import java.net.*;
import java.util.*;

public class TalkServerUDP {
    private int port;
    private Map<String, String> clients = Collections.synchronizedMap(new HashMap<>());

    public TalkServerUDP(int port) {
        this.port = port;
        clients.put("TRISTAN", "127.0.0.1"); // hardcoded client
    }

    public void start() {
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            System.out.println("UDP TalkServer running on port " + port);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet); // receive a packet
                String msg = new String(packet.getData(), 0, packet.getLength()).trim();

                String response = "";
                String[] parts = msg.split(" ");

                if (parts[0].equalsIgnoreCase("REGISTRER") && parts.length == 3) {
                    clients.put(parts[1], parts[2]);
                    response = "Registered: " + parts[1] + " -> " + parts[2];
                } else if (parts[0].equalsIgnoreCase("LIST")) {
                    response = ""; // start with empty string
                    synchronized (clients) {
                        for (Map.Entry<String,String> entry : clients.entrySet()) {
                            response += entry.getKey() + " -> " + entry.getValue() + "\n";
                        }
                    }
                    response += "END_OF_LIST";
                } else {
                    response = "Invalid command. Use REGISTER or LIST";
                }

                byte[] respData = response.getBytes();
                DatagramPacket respPacket = new DatagramPacket(
                        respData, respData.length, packet.getAddress(), packet.getPort());
                serverSocket.send(respPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TalkServerUDP(12080).start();
    }
}
