package version03.NameServerUDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class NameServerUDP {
    private static final Map<String, String> dnsNavne = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // Hardcoded navne og IP'er
        dnsNavne.put("local", "127.0.0.1");
        dnsNavne.put("sidse", "10.10.131.181");

        int port = 13000;
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("Navneserver startet på port " + port);

        byte[] buf = new byte[256];

        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String receivedName = new String(packet.getData(), 0, packet.getLength()).toLowerCase();
            System.out.println("Forespørgsel på navn: " + receivedName);

            String ip = dnsNavne.getOrDefault(receivedName, "NOT FOUND");

            byte[] sendData = ip.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, packet.getAddress(), packet.getPort());

            socket.send(sendPacket);
            System.out.println("Sendt IP: " + ip);
        }
    }
}
