package Server.UDPsocket;

import java.net.*;

public class UDPsocket extends Thread {
    private DatagramSocket datagramSocket;
    private byte[] buf = new byte[4];
    private int serverPort;

    public UDPsocket(int port) {
        serverPort = port;
        start();
    }

    public void run()
    {
        try
        {
            datagramSocket = new DatagramSocket(2000);

            System.out.println("Server is listening!");
            while(true)
            {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                System.out.println(serverPort);
                buf[0] = (byte)((serverPort >> 24) & 0xff);
                buf[1] = (byte)((serverPort >> 16) & 0xff);
                buf[2] = (byte)((serverPort >> 8) & 0xff);
                buf[3] = (byte)((serverPort >> 0) & 0xff);
                System.out.println("Server sends " + address.toString() + ":" + port + " port ");
                packet = new DatagramPacket(buf, buf.length, address, port);
                datagramSocket.send(packet);
            }
        }
        catch(SocketException se)
        {
            System.out.print("!Server socket could not be opened\n");
        }
        catch(Exception ex) {
        }
        finally {
            datagramSocket.close();
        }
    }
}
