package version03.NameServerUDP;

import java.io.*;
import java.net.*;

public class TalkClientUDP {
    private static final int NAME_SERVER_PORT = 13000;
    private static final int DEFAULT_CHAT_PORT = 12080;
    private static final int BUFFER_SIZE = 256;

    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket udpSocket = new DatagramSocket();

        boolean running = true;
        boolean isRegistered = false;
        String kaldenavn = null;

        while (running) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. REGISTRER kaldenavn");
            System.out.println("2. LIST (vis registrerede klienter)");
            System.out.println("3. CONNECT til anden klient");
            System.out.println("4. Afslut");
            System.out.print("Vælg (1-4): ");

            String valg = console.readLine().trim();

            switch (valg) {
                case "1":  // REGISTRER
                    System.out.print("Indtast dit kaldenavn: ");
                    kaldenavn = console.readLine().trim();
                    if (!kaldenavn.isEmpty()) {
                        String registrerCmd = "REGISTRER " + kaldenavn;
                        String regResponse = sendUDPMessage(udpSocket, registrerCmd, "localhost", NAME_SERVER_PORT);
                        System.out.println(regResponse);
                        isRegistered = true;
                    } else {
                        System.out.println("Kaldenavn kan ikke være tomt.");
                    }
                    break;

                case "2":  // LIST
                    String listResponse = sendUDPMessage(udpSocket, "LIST", "localhost", NAME_SERVER_PORT);
                    System.out.println("\nRegistrerede klienter:\n" + listResponse);
                    break;

                case "3":  // CONNECT
                    if (!isRegistered) {
                        System.out.println("Du skal først registrere dig, før du kan connecte.");
                        break;
                    }

                    System.out.print("Hvem vil du forbinde til (kaldenavn)? ");
                    String modtagerNavn = console.readLine().trim();

                    String connectCmd = "CONNECT " + modtagerNavn;
                    String response = sendUDPMessage(udpSocket, connectCmd, "localhost", NAME_SERVER_PORT);

                    if (response.startsWith("CONNECT_IP")) {
                        String[] parts = response.split(" ");
                        String[] ipAndPort = parts[1].split(":");
                        String ip = ipAndPort[0];
                        int port = Integer.parseInt(ipAndPort[1]);

                        System.out.println("Forbinder til " + modtagerNavn + " på " + ip + ":" + port);

                        // Start TCP-chat med anden klient
                        Socket chatSocket = new Socket(ip, port);
                        System.out.println("Forbundet til klient. Start chat:");

                        RecieverTråd reciever = new RecieverTråd(chatSocket);
                        SenderTråd sender = new SenderTråd(chatSocket);

                        reciever.start();
                        sender.start();

                        reciever.join();
                        sender.join();
                        chatSocket.close();
                    } else {
                        System.out.println("Kunne ikke finde klienten: " + response);
                    }
                    break;

                case "4":  // AFSLUT
                    System.out.println("Lukker klienten...");
                    running = false;
                    break;

                default:
                    System.out.println("Ugyldigt valg. Indtast et tal fra 1 til 4.");
            }
        }

        udpSocket.close();
        System.out.println("Klienten er lukket.");
    }

    private static String sendUDPMessage(DatagramSocket socket, String message, String host, int port) throws IOException {
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName(host);

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);

        byte[] responseBuffer = new byte[BUFFER_SIZE];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
        socket.receive(responsePacket);

        return new String(responsePacket.getData(), 0, responsePacket.getLength()).trim();
    }
}
