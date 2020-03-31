package Client.ClientSocket;

import Client.ChatWindow.ChatWindow;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientSocket {
    private static Socket clientSocket;
    private static BufferedReader inputStream;
    private static BufferedWriter outputStream;
    public String nickname;
    private ChatWindow app;

    private class Reader extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    String[] data = inputStream.readLine().split(" ", 4);
                    int command = Integer.parseInt(data[0]);
                    int sender = Integer.parseInt(data[1]);
                    int receiver = Integer.parseInt(data[2]);
                    String text = data[3];
                    switch (command) {
                        case 0:
                            if (receiver != 0) {
                                app.addMsg(text + "\n", sender);
                            } else {
                                app.addMsg(text + "\n", 0);
                            }
                            break;
                        case 1:
                            app.addChat(text);
                            break;
                        case 2:
                            app.deleteChat(sender);
                            break;
                        default:
                            System.err.println("Unrecognized command!");
                    }
                }
            } catch (IOException e) {
                downService();
            }
        }
    }

    public void send(String text, int receiver) {
        Date time;
        String dtime;
        SimpleDateFormat dt1;
        try {
            time = new Date();
            dt1 = new SimpleDateFormat("HH:mm:ss");
            dtime = dt1.format(time);
            outputStream.write("0 0 " + receiver + " (" + dtime + ") " + nickname + ": " + text + "\n");
            outputStream.flush();
        } catch (IOException e) {
            downService();
        }
    }

    public void addUser(String nickname) {
        try {
            System.out.println("Client sends his name: " + nickname);
            outputStream.write("1 0 0 " + nickname + "\n");
            outputStream.flush();
        } catch (IOException e) {
            downService();
        }
    }

    public void downService() {
        try {
            if (!clientSocket.isClosed()) {
                System.out.println("Client is being turned off...");
                outputStream.write("2 0 0 0 \n");
                outputStream.flush();
                clientSocket.close();
                outputStream.close();
                outputStream.close();
            }
        } catch (IOException ignored) {}
    }

    private void connectToServer() {
        byte[] buf = new byte[4];
        try {
            System.out.println("Client wants to connect.");
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), 2000);
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.send(datagramPacket);
            datagramSocket.receive(datagramPacket);
            datagramSocket.close();

            InetAddress address = datagramPacket.getAddress();
            ByteBuffer byteBuffer = ByteBuffer.wrap(datagramPacket.getData());
            int port = byteBuffer.getInt();
            try {
                System.out.println("Client connects to " + address.toString() + ":" + port);
                clientSocket = new Socket(address, port);
            } catch (IOException ex) {
                System.err.println("Socket failed.");
            }
        } catch (IOException ex) {
            System.out.println("Client failed to send/receive.");
        }
    }

    public ClientSocket(String nickname) {
        this.nickname = nickname;
        connectToServer();
        try {
            inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputStream = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            app = new ChatWindow(this);
            new Reader().start();
            this.addUser(nickname);
        } catch (IOException ex) {
            System.out.println("Failed to initialize chat application.");
            downService();
        }
    }
}