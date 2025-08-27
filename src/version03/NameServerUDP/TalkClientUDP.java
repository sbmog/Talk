package version03.NameServerUDP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

public class TalkClientUDP {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddr = InetAddress.getByName("localhost");
        int serverPort = 12080;


        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        byte[] buffer = new byte[1024];
        String line;
        while ((line = input.readLine()) != null) {
            byte[] data = line.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, serverPort);
            socket.send(packet);

            // modtag svar
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            String respMsg = new String(response.getData(), 0, response.getLength());
            System.out.println(respMsg);
        }
    }
}