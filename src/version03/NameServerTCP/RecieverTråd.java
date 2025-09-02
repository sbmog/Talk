package version03.NameServerTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RecieverTråd extends Thread {
    private final Socket connectionSocket;

    public RecieverTråd(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        try (BufferedReader inFromOther = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))) {
            String msg;
            while ((msg = inFromOther.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Forbindelsen er lukket" );
        }
    }
}
