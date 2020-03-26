package Client;

import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;
    private String nickname;
    private Date time;
    private String dtime;
    private SimpleDateFormat dt1;

    private void pressNickname() {
        System.out.print("Press your nick: ");
        try {
            nickname = reader.readLine();
            out.write("Hello " + nickname + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }

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
                    System.out.println(str);
                }
            } catch (IOException e) {
                downService();
            }
        }
    }

    public class WriteMsg extends Thread {
        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    time = new Date();
                    dt1 = new SimpleDateFormat("HH:mm:ss");
                    dtime = dt1.format(time);
                    userWord = reader.readLine();
                    if (userWord.equals("exit")) {
                        out.write("exit" + "\n");
                        downService();
                        break;
                    } else {
                        out.write("(" + dtime + ") " + nickname + ": " + userWord + "\n"); // отправляем на сервер
                    }
                    out.flush();
                } catch (IOException e) {
                    downService();
                }

            }
        }
    }

    private void downService() {
        System.out.println("Client is being turned off...");
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
                reader.close();
            }
        } catch (IOException ignored) {}
    }

    public Client() {
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
            } catch (IOException ex) {
                System.err.println("Socket failed");
            }
            try {
                reader = new BufferedReader(new InputStreamReader(System.in));
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                pressNickname();
                new ReadMsg().start();
                new WriteMsg().start();
            } catch (IOException ex) {
                downService();
            }
        } catch (Exception ex) {
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}