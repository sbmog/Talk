package version03.NameServerTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ServerListener implements Runnable {
    private BufferedReader inFromServer;

    public ServerListener(BufferedReader inFromServer) {
        this.inFromServer = inFromServer;
    }

    @Override
    public void run() {
        try {
            String response;
            while ((response = inFromServer.readLine()) != null) {
                System.out.println("[SERVER]: " + response);

                if (response.startsWith("CONNECT_IP")) {
                    String ipPort = response.substring("CONNECT_IP".length()).trim();
                    String[] parts = ipPort.split(":");
                    String ip = parts[0];
                    int port = Integer.parseInt(parts[1]);

                    //ny socket til talkserver
                    Socket talkSocket = new Socket(ip, port);

                    //start chat threads på talkserver
                    new SenderTråd(talkSocket).start();
                    new RecieverTråd(talkSocket).start();
                }
            }
        } catch (IOException e) {
            System.out.println("Lost connection to server.");
        }
    }
}
