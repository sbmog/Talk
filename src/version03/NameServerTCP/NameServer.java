    package version03.NameServerTCP;

    import java.io.*;
    import java.net.ServerSocket;
    import java.net.Socket;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.Map;


        public class NameServer {
            private static Map<String, String> clients = Collections.synchronizedMap(new HashMap<>());
            private static int port = 13000;

            public static void main(String[] args) throws IOException {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("NameServer startet på port " + port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    new ClientHandler(socket, clients).start();
                }
            }
        }