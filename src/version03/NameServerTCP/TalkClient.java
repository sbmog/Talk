package version03.NameServerTCP;

import java.io.*;
import java.net.Socket;

public class TalkClient {
    public static void main(String[] args) throws IOException {

        // Forbind til NameServer
        Socket nameServerSocket = new Socket("localhost", 13000);
        BufferedReader inFromNameServer = new BufferedReader(new InputStreamReader(nameServerSocket.getInputStream()));
        DataOutputStream outToNameServer = new DataOutputStream(nameServerSocket.getOutputStream());
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter your name to register: ");
        String name = userInput.readLine();
        outToNameServer.writeBytes("REGISTRER " + name + "\n");

        System.out.println("You can now type LIST or CONNECT <name>");

        // Lyt på NameServer indtil vi får et CONNECT_IP
        while (true) {
            String command = userInput.readLine();
            if (command == null || command.isEmpty()) continue;

            if (command.equalsIgnoreCase("LIST") || command.toUpperCase().startsWith("CONNECT")) {
                outToNameServer.writeBytes(command + "\n");

                // læs svar fra NameServer
                String response;
                while ((response = inFromNameServer.readLine()) != null) {
                    System.out.println("[NameServer]: " + response);

                    if (response.equals("END_OF_LIST")) {
                        break;
                    }

                    if (response.startsWith("CONNECT_IP")) {
                        // Vi har fået info om hvem vi skal forbinde til
                        String ipPort = response.substring("CONNECT_IP".length()).trim();
                        String[] parts = ipPort.split(":");
                        String ip = parts[0];
                        int port = Integer.parseInt(parts[1]);

                        System.out.println("Connecting to TalkServer at " + ip + ":" + port);

                        // Luk forbindelse til NameServer
                        nameServerSocket.close();

                        // Opret ny forbindelse til TalkServer
                        Socket talkSocket = new Socket(ip, port);

                        // Start chat-tråde
                        new SenderTråd(talkSocket).start();
                        new RecieverTråd(talkSocket).start();

                        System.out.println("You are now in chat. Type messages to send.");
                        return; // afslut main, chat kører i tråde
                    }
                }

            } else {
                System.out.println("Invalid command. Use LIST or CONNECT <name>");
            }
        }
    }
}
