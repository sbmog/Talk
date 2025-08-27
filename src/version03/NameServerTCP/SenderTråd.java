    package version03.NameServerTCP;
    import java.io.*;
    import java.net.Socket;

    public class SenderTråd extends Thread {
        private Socket socket;

        public SenderTråd(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                String line;
                while ((line = userInput.readLine()) != null) {
                    out.writeBytes(line + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }