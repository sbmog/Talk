package version03.NameServerTCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NameServer {
    private static final Map<String, String> dnsNavne = new HashMap<>();
    private static int port = 13000;

    public static void main(String[] args) throws IOException {
        dnsNavne.put("LOCAL".toLowerCase(), "localhost");
        dnsNavne.put("Sidse".toLowerCase(), "10.10.131.181");

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("DNS server started på port: " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            String hostName = in.readLine();
            System.out.println("DNS lookup for: " + hostName);

            String ip = dnsNavne.getOrDefault(hostName.toLowerCase(), "NOT FOUND");
            out.writeBytes(ip + '\n');


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
