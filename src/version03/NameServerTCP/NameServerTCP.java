package version03.NameServerTCP;

import version03.ClientInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

public class NameServerTCP {
    private static final List<ClientInfo> clientList = new java.util.ArrayList<>();
    private static final int PORT = 12080;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("NavneServer (TCP) kører på port " + PORT);



    }
}
