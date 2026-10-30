package serverPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server_4_3 {
    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 1024;
    private static final String JOIN_MESSAGE = "/join";
    private static final String QUIT_MESSAGE = "/quit";

    public static void main(String[] argv) throws Exception {
        DatagramSocket socket = new DatagramSocket(PORT);
        Set<Client> clients = ConcurrentHashMap.newKeySet();

        System.out.println("Lancement du serveur");
        System.out.println("Ecrivez un message pour le diffuser aux clients.");

        Thread receivingThread = new Thread(
                () -> receiveMessages(socket, clients),
                "server-receiving"
        );
        Thread sendingThread = new Thread(
                () -> sendMessages(socket, clients),
                "server-sending"
        );

        receivingThread.start();
        sendingThread.start();
    }

    private static void receiveMessages(
            DatagramSocket socket,
            Set<Client> clients
    ) {
        try {
            while (!socket.isClosed()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                Client client = new Client(packet.getAddress(), packet.getPort());
                clients.add(client);

                String message = new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                if (message.equalsIgnoreCase(JOIN_MESSAGE)) {
                    continue;
                }

                if (message.equalsIgnoreCase(QUIT_MESSAGE)) {
                    clients.remove(client);
                    continue;
                }

                String broadcast = "Client " + client + " : " + message;
                System.out.println(broadcast);
                broadcast(socket, clients, broadcast);
            }
        } catch (Exception exception) {
            if (!socket.isClosed()) {
                System.err.println("Erreur de reception : " + exception.getMessage());
            }
        }
    }

    private static void sendMessages(
            DatagramSocket socket,
            Set<Client> clients
    ) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (!socket.isClosed() && scanner.hasNextLine()) {
                String message = scanner.nextLine().trim();
                if (!message.isEmpty()) {
                    String broadcast = "Serveur : " + message;
                    broadcast(socket, clients, broadcast);
                }
            }
        } catch (Exception exception) {
            if (!socket.isClosed()) {
                System.err.println("Erreur d'envoi : " + exception.getMessage());
            }
        }
    }

    private static void broadcast(
            DatagramSocket socket,
            Set<Client> clients,
            String message
    ) throws Exception {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        for (Client client : clients) {
            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    client.address,
                    client.port
            );
            socket.send(packet);
        }
    }

    private record Client(InetAddress address, int port) {
        @Override
        public String toString() {
            return address.getHostAddress() + ":" + port;
        }
    }
}
