package version03.NameServerUDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class NameServerUDP {
    // Map til at holde kaldenavne (navn -> "ip:port") for registrerede klienter
    private static final Map<String, String> registeredClients = new HashMap<>();

    public static void main(String[] args) throws Exception {
        int port = 13000;   // Porten navneserveren lytter på for UDP-pakker
        DatagramSocket socket = new DatagramSocket(port);  // Opretter UDP socket på port 13000
        System.out.println("UDP Navneserver startet på port " + port);

        byte[] buffer = new byte[256];  // Buffer til indkommende pakker (max 256 bytes)

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);   // Blokerer og venter på at modtage en UDP-pakke

            // Læser indholdet af pakken som en String
            String input = new String(packet.getData(), 0, packet.getLength()).trim();
            // Henter IP-adressen på afsenderen direkte fra pakken
            String senderIp = packet.getAddress().getHostAddress();

            // Hardcoded chat-port, som klienterne lytter på for samtaler
            final String chatPort = "12080";

            System.out.println("Modtaget: '" + input + "' fra " + senderIp);

            // Split input kommandoen i ord (fx "REGISTRER kaldenavn")
            String[] parts = input.split(" ");
            String response;  // Her gemmes svaret som sendes tilbage til klienten

            // Håndterer REGISTRER-kommando: REGISTRER <kaldenavn>
            if (parts[0].equalsIgnoreCase("REGISTRER") && parts.length == 2) {
                String navn = parts[1].toLowerCase();  // Kaldenavn til lowercase for ensartethed
                // Registrerer klientens IP + den kendte chat-port
                String value = senderIp + ":" + chatPort;
                registeredClients.put(navn, value);   // Gemmer i map
                response = "Registreret: " + navn + " -> " + value;

                // Håndterer CONNECT-kommando: CONNECT <kaldenavn>
            } else if (parts[0].equalsIgnoreCase("CONNECT") && parts.length == 2) {
                String navn = parts[1].toLowerCase();
                String value = registeredClients.get(navn);  // Finder IP:port på kaldenavnet
                if (value != null) {
                    response = "CONNECT_IP " + value;  // Returnerer IP:port hvis fundet
                } else {
                    response = "Ingen klient med navn: " + navn;  // Fejl hvis ikke fundet
                }

                // Håndterer LIST-kommandoen: returnerer alle registrerede klienter
            } else if (parts[0].equalsIgnoreCase("LIST")) {
                StringBuilder listBuilder = new StringBuilder();
                for (Map.Entry<String, String> entry : registeredClients.entrySet()) {
                    listBuilder.append(entry.getKey())    // Kaldenavn
                            .append(" -> ")
                            .append(entry.getValue())  // IP:port
                            .append("\n");
                }
                listBuilder.append("END_OF_LIST");   // Marker slutningen på listen
                response = listBuilder.toString();

                // Hvis ukendt kommando modtages
            } else {
                response = "Ugyldig kommando. Brug REGISTRER <navn>, CONNECT <navn> eller LIST";
            }

            // Konverter svar-streng til byte-array til UDP-pakke
            byte[] responseBytes = response.getBytes();

            // Forbered UDP-pakken til at sende svaret tilbage til afsenderen
            DatagramPacket reply = new DatagramPacket(
                    responseBytes,
                    responseBytes.length,
                    packet.getAddress(),    // Sender IP-adresse
                    packet.getPort()        // Sender port (det UDP-portnummer afsender bruger)
            );

            // Sender svaret til klienten
            socket.send(reply);
        }
    }
}
