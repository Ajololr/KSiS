package Client;

import Client.ChatWindow.ChatWindow;
import Client.LoginWindow.LoginWindow;
import Server.Server;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;
    public String nickname;
    private ChatWindow app;

    private class ReadMsg extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    String[] data = in.readLine().split(" ", 4);
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

    public void WriteMsg(String text, byte receiver) {
        Date time;
        String dtime;
        SimpleDateFormat dt1;
        try {
            time = new Date();
            dt1 = new SimpleDateFormat("HH:mm:ss");
            dtime = dt1.format(time);
            out.write("0 0 " + receiver + " (" + dtime + ") " + nickname + ": " + text + "\n");
            out.flush();
        } catch (IOException e) {
            downService();
        }
    }

    public void addUser(String nickname) {
        try {
            System.out.println("Client sends his name: " + nickname);
            out.write("1 0 0 " + nickname + "\n");
            out.flush();
        } catch (IOException e) {
            downService();
        }
    }

    public void downService() {
        try {
            if (!clientSocket.isClosed()) {
                System.out.println("Client is being turned off...");
                out.write("2 0 0 0 \n");
                out.flush();
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    public Client(String nickname) {
        this.nickname = nickname;
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
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                app = new ChatWindow(this);
                new ReadMsg().start();
                this.addUser(nickname);
            } catch (IOException ex) {
                downService();
            }
        } catch (Exception ex) {
            System.err.println("Error happened.");
        }
    }

    public static void main(String[] args) {
        new LoginWindow();
    }
}