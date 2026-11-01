package serverPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server_4_3 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(1234);
        List<InetSocketAddress> clients = new ArrayList<>();

        System.out.println("Lancement du serveur");
        System.out.println("Ecrivez un message pour le diffuser aux clients.");

        Thread receivingThread = new Thread(new Runnable() {
            public void run() {
                receiveMessages(socket, clients);
            }
        });

        Thread sendingThread = new Thread(new Runnable() {
            public void run() {
                sendMessages(socket, clients);
            }
        });

        receivingThread.start();
        sendingThread.start();
    }

    public static void receiveMessages(
            DatagramSocket socket,
            List<InetSocketAddress> clients
    ) {
        try {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                InetSocketAddress client = new InetSocketAddress(
                        packet.getAddress(),
                        packet.getPort()
                );

                synchronized (clients) {
                    if (!clients.contains(client)) {
                        clients.add(client);
                    }
                }

                String message = new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                if (message.equalsIgnoreCase("/join")) {
                    continue;
                }

                if (message.equalsIgnoreCase("/quit")) {
                    synchronized (clients) {
                        clients.remove(client);
                    }
                    continue;
                }

                String displayedMessage = "Client "
                        + client.getAddress().getHostAddress()
                        + ":"
                        + client.getPort()
                        + " : "
                        + message;
                System.out.println(displayedMessage);

                byte[] data = displayedMessage.getBytes(StandardCharsets.UTF_8);
                synchronized (clients) {
                    for (InetSocketAddress destination : clients) {
                        socket.send(new DatagramPacket(
                                data,
                                data.length,
                                destination.getAddress(),
                                destination.getPort()
                        ));
                    }
                }
            }
        } catch (Exception exception) {
            System.err.println("Erreur de reception : " + exception.getMessage());
        }
    }

    public static void sendMessages(
            DatagramSocket socket,
            List<InetSocketAddress> clients
    ) {
        try {
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                String message = scanner.nextLine().trim();

                if (!message.isEmpty()) {
                    byte[] data = ("Serveur : " + message)
                            .getBytes(StandardCharsets.UTF_8);

                    synchronized (clients) {
                        for (InetSocketAddress destination : clients) {
                            socket.send(new DatagramPacket(
                                    data,
                                    data.length,
                                    destination.getAddress(),
                                    destination.getPort()
                            ));
                        }
                    }
                }
            }
        } catch (Exception exception) {
            System.err.println("Erreur d'envoi : " + exception.getMessage());
        }
    }
}
