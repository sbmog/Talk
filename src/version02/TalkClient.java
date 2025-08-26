package version02;

import java.io.IOException;
import java.net.Socket;

public class TalkClient {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("LocalHost", 12080);

        RecieverTråd reciever = new RecieverTråd(socket);
        SenderTråd sender = new SenderTråd(socket);

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

