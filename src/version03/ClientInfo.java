package version03;

public class ClientInfo {
    String name;
    String ip;
    int port;

    ClientInfo(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }


    @Override
    public String toString() {
        return "ClientInfo{" +
                "Kaldenavn: '" + name + '\'' +
                ", IP: '" + ip + '\'' +
                ", port: " + port +
                '}';
    }
}
