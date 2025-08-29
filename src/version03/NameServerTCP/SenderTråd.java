package version03.NameServerTCP;

import java.io.*;
import java.net.Socket;

public class SenderTråd extends Thread {
    private final Socket connectionSocket;

    public SenderTråd(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        try (BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
             DataOutputStream outToOther = new DataOutputStream(connectionSocket.getOutputStream())) {

            String sentence;
            while ((sentence = inFromUser.readLine()) != null) {
                outToOther.writeBytes(sentence + '\n');
            }
        } catch (IOException e) {
            System.out.println("Connection closed");
        }
    }
}
