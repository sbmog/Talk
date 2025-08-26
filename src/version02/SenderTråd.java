package version02;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SenderTråd extends Thread {
    Socket connectionSocket;

    public SenderTråd(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        try (BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
             DataOutputStream outToOther = new DataOutputStream(connectionSocket.getOutputStream());
        ) {
            String sentence;
            while ((sentence = inFromUser.readLine()) != null) {
                outToOther.writeBytes(sentence + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
