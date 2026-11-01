package clientPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client_4_3 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress server = InetAddress.getByName("localhost");

        byte[] join = "/join".getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(join, join.length, server, 1234));

        Thread sendingThread = new Thread(new Runnable() {
            public void run() {
                sendMessages(socket, server);
            }
        });

        Thread receivingThread = new Thread(new Runnable() {
            public void run() {
                receiveMessages(socket);
            }
        });

        sendingThread.start();
        receivingThread.start();
    }

    public static void sendMessages(
            DatagramSocket socket,
            InetAddress server
    ) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Connecte au serveur. Ecrivez vos messages :");

            while (scanner.hasNextLine()) {
                String message = scanner.nextLine().trim();

                if (!message.isEmpty()) {
                    byte[] data = message.getBytes(StandardCharsets.UTF_8);
                    socket.send(new DatagramPacket(
                            data,
                            data.length,
                            server,
                            1234
                    ));
                }

                if (message.equalsIgnoreCase("/quit")) {
                    socket.close();
                    break;
                }
            }
        } catch (Exception exception) {
            if (!socket.isClosed()) {
                System.err.println("Erreur d'envoi : " + exception.getMessage());
            }
        }
    }

    public static void receiveMessages(DatagramSocket socket) {
        try {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                System.out.println(new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8
                ));
            }
        } catch (Exception exception) {
            if (!socket.isClosed()) {
                System.err.println("Erreur de reception : " + exception.getMessage());
            }
        }
    }
}
