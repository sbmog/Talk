package version03.NameServerUDP;

import java.io.*;
import java.net.*;

public class TalkClientUDP {
    public static void main(String[] args) throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        int dnsPort = 13000;
        int chatPort = 12080;

        System.out.print("Indtast kaldenavn (fx 'local' eller 'sidse'): ");
        String kaldenavn = console.readLine().toLowerCase();

        // Send DNS forespørgsel via UDP
        DatagramSocket dnsSocket = new DatagramSocket();
        InetAddress dnsServer = InetAddress.getByName("localhost");

        byte[] sendData = kaldenavn.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dnsServer, dnsPort);
        dnsSocket.send(sendPacket);

        // Modtag svar
        byte[] recvBuf = new byte[256];
        DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
        dnsSocket.receive(recvPacket);
        String ip = new String(recvPacket.getData(), 0, recvPacket.getLength());

        dnsSocket.close();

        if (ip.equals("NOT FOUND")) {
            System.out.println("Kaldenavn ikke fundet på navneserveren.");
            return;
        }

        System.out.println("DNS resolved " + kaldenavn + " til " + ip);

        // Opret TCP forbindelse til TalkServer
        try (Socket socket = new Socket(ip, chatPort)) {
            System.out.println("Forbundet til TalkServer på " + ip + ":" + chatPort);

            // Start tråde til at sende og modtage beskeder
            Thread reciever = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Modtaget: " + line);
                    }
                } catch (IOException e) {
                    System.out.println("Modtagelse afbrudt.");
                }
            });

            Thread sender = new Thread(() -> {
                try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
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

            reciever.join();
            sender.join();

        } catch (Exception e) {
            System.out.println("Kan ikke forbinde til TalkServer: " + e.getMessage());
        }
    }
}
