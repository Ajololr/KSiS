package Client;

import Client.ChatWindow.ChatWindow;
import Client.LoginWindow.LoginWindow;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;
    private String nickname;
    private Date time;
    private String dtime;
    private SimpleDateFormat dt1;
    private ChatWindow app;

    private class ReadMsg extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine();
                    if (str.equals("exit")) {
                        downService();
                        break;
                    }
                    app.displayMsg(str + "\n");
                }
            } catch (IOException e) {
                downService();
            }
        }
    }

    public void WriteMsg(String userWord) {
        try {
            time = new Date();
            dt1 = new SimpleDateFormat("HH:mm:ss");
            dtime = dt1.format(time);
            if (userWord.equals("exit")) {
                out.write("exit" + "\n");
                downService();
            } else {
                out.write("(" + dtime + ") " + nickname + ": " + userWord + "\n");
            }
            out.flush();
        } catch (IOException e) {
            downService();
        }
    }

    private void downService() {
        System.out.println("Client is being turned off...");
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    public Client(String nickname) {
        this.nickname = nickname;
        byte buf[] = new byte[4];
        try {
            System.out.println("Client wants to connect!");
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
                app = new ChatWindow(this);
            } catch (IOException ex) {
                System.err.println("Socket failed");
            }
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                new ReadMsg().start();
            } catch (IOException ex) {
                downService();
            }
        } catch (Exception ex) {
        }
    }

    public static void main(String[] args) {
        new LoginWindow();
    }
}