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

            System.out.print("Indtast navn for at registrere: ");
            String navn = userInput.readLine();
            outToNameServer.writeBytes("REGISTRER " + navn + "\n");

            String welcome = inFromNameServer.readLine();
            System.out.println("[NameServer]: " + welcome);


            // Lyt på NameServer indtil vi får et CONNECT_IP
            while (true) {
                String command = userInput.readLine();
                if (command == null || command.isEmpty()) continue;

                if (command.equalsIgnoreCase("LISTE") || command.toUpperCase().startsWith("CONNECT")) {
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

                            System.out.println("Forbinder til TalkServer med " + ip + ":" + port);

                            // Luk forbindelse til NameServer
                            nameServerSocket.close();
                            // Opret ny forbindelse til TalkServer
                            Socket talkSocket = new Socket(ip, port);

                            // Start chat-tråde
                            new SenderTråd(talkSocket).start();
                            new RecieverTråd(talkSocket).start();

                            System.out.println("Du er nu i chatrummet. Indtast besked og send.");
                            return;
                        }
                    }
                } else {
                    System.out.println("Forkert kommando: Brug LISTE or CONNECT <navn>");
                }
            }
        }
    }
