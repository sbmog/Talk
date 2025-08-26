package version01;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TalkServer {
    public static void main(String[] args) throws IOException {

        ServerSocket welcomeSocket = new ServerSocket(12080);
        System.out.println("Venter på ack");

        Socket connectionSocket = welcomeSocket.accept();

        BufferedReader toClient = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        System.out.println("Forbundet");

        while (true) {
            String clientSentence = fromClient.readLine();
            System.out.println("From original client: " + clientSentence);

            String toClientSentence = toClient.readLine();

            outToClient.writeBytes(toClientSentence+'\n');
        }

    }
}
