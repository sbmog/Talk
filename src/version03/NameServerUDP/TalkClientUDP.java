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

        // 1. Registrér dig selv
        System.out.print("Indtast dit kaldenavn: ");
        String kaldenavn = console.readLine();

        String registrerCmd = "REGISTRER " + kaldenavn;
        sendUDPMessage(udpSocket, registrerCmd, "localhost", NAME_SERVER_PORT);
        System.out.println("Du er registreret på navneserveren som: " + kaldenavn);

        // 2. Spørg om en anden bruger
        System.out.print("Hvem vil du forbinde til (kaldenavn)? ");
        String modtagerNavn = console.readLine();

        String connectCmd = "CONNECT " + modtagerNavn;
        String response = sendUDPMessage(udpSocket, connectCmd, "localhost", NAME_SERVER_PORT);

        if (response.startsWith("CONNECT_IP")) {
            String[] parts = response.split(" ");
            String[] ipAndPort = parts[1].split(":");
            String ip = ipAndPort[0];
            int port = Integer.parseInt(ipAndPort[1]);

            System.out.println("Forbinder til " + modtagerNavn + " på " + ip + ":" + port);

            // 3. Opret TCP-chat med den anden klient
            Socket chatSocket = new Socket(ip, port);
            System.out.println("Forbundet til klient. Start chat:");

            RecieverTråd reciever = new RecieverTråd(chatSocket);
            SenderTråd sender = new SenderTråd(chatSocket);

            reciever.start();
            sender.start();

            reciever.join();
            sender.join();

        } else {
            System.out.println("Kunne ikke finde klienten: " + response);
        }

        udpSocket.close();
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
