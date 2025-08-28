package version03.NameServerTCP;

import java.io.*;
import java.net.Socket;

public class TalkClient {
    public static void main(String[] args) throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Indtast server-navn: ");
        String serverNavn = console.readLine();
        int port = 13000;

        Socket dnsSocket = new Socket("LocalHost", port);

        DataOutputStream dnsOut = new DataOutputStream(dnsSocket.getOutputStream());
        BufferedReader dnsIn = new BufferedReader(new InputStreamReader(dnsSocket.getInputStream()));

        dnsOut.writeBytes(serverNavn + '\n');
        String ip = dnsIn.readLine();
        dnsSocket.close();

        System.out.println("DNS resolved " + serverNavn + " til " + ip);

        port = 12080;
        Socket socket = new Socket(ip, port);

        System.out.println("Forbundet til anden client. ");

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

