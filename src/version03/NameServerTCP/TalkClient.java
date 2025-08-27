package version03.NameServerTCP;

import java.io.*;
import java.net.Socket;

public class TalkClient {
    private String nickname;
    private String ip; // hardkodet IP
    private String serverHost;
    private int serverPort;

    public TalkClient(String nickname, String ip, String serverHost, int serverPort) {
        this.nickname = nickname;
        this.ip = ip;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void start() throws IOException {
        Socket socket = new Socket(serverHost, serverPort);
        System.out.println("Forbundet til navneserver: " + serverHost);

        // Start tråde til at sende og modtage beskeder
        RecieverTråd reciever = new RecieverTråd(socket);
        SenderTråd sender = new SenderTråd(socket);

        sender.start();
        reciever.start();

        try {
            sender.join();
            reciever.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        TalkClient client = new TalkClient("Alice", "localhost", "localhost", 12080);
        client.start();
    }
}
