package version03.NameServerTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TalkServer {
    public static void main(String[] args) throws IOException {

        ServerSocket welcomeSocket = new ServerSocket(12080);
        System.out.println("Venter på ack");

        Socket connectionSocket = welcomeSocket.accept();

        System.out.println("Forbundet");

        RecieverTråd reciever = new RecieverTråd(connectionSocket);
        SenderTråd sender = new SenderTråd(connectionSocket);

        reciever.start();
        sender.start();

        try {
            reciever.join();
            sender.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
