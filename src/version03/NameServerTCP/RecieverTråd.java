package version03.NameServerTCP;
import java.io.*;
import java.net.Socket;

public class RecieverTråd extends Thread {
    private Socket socket;

    public RecieverTråd(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}