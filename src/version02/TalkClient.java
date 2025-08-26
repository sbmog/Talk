package version02;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TalkClient {
    public static void main(String[] args) throws IOException {

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = new Socket("LocalHost", 12080);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while (true) {
            // clientSocket.isConnected();

            String sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence + '\n');

            String fromServer = inFromServer.readLine();
            System.out.println("From other client: " + fromServer);
        }

//        clientSocket.close();

    }
}

