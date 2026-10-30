package clientPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client_4_3 {
    private static final int SERVER_PORT = 1234;
    private static final int BUFFER_SIZE = 1024;
    private static final String JOIN_MESSAGE = "/join";
    private static final String QUIT_MESSAGE = "/quit";

    public static void main(String[] argv) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");

        sendMessage(socket, serverAddress, JOIN_MESSAGE);

        Thread sendingThread = new Thread(
                () -> sendMessages(socket, serverAddress),
                "client-sending"
        );
        Thread receivingThread = new Thread(
                () -> receiveMessages(socket),
                "client-receiving"
        );

        receivingThread.start();
        sendingThread.start();
    }

    private static void sendMessages(
            DatagramSocket socket,
            InetAddress serverAddress
    ) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Connecte au serveur. Ecrivez vos messages :");

            while (!socket.isClosed() && scanner.hasNextLine()) {
                String message = scanner.nextLine().trim();
                if (message.isEmpty()) {
                    continue;
                }

                sendMessage(socket, serverAddress, message);

                if (message.equalsIgnoreCase(QUIT_MESSAGE)) {
                    socket.close();
                }
            }
        } catch (Exception exception) {
            if (!socket.isClosed()) {
                System.err.println("Erreur d'envoi : " + exception.getMessage());
            }
        }
    }

    private static void sendMessage(
            DatagramSocket socket,
            InetAddress serverAddress,
            String message
    ) throws Exception {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(
                data,
                data.length,
                serverAddress,
                SERVER_PORT
        );
        socket.send(packet);
    }

    private static void receiveMessages(DatagramSocket socket) {
        try {
            while (!socket.isClosed()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );
                System.out.println(message);
            }
        } catch (Exception exception) {
            if (!socket.isClosed()) {
                System.err.println("Erreur de reception : " + exception.getMessage());
            }
        }
    }
}
