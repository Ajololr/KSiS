package server.UDPsocket;

import java.net.*;

public class UDPsocket extends Thread {
    private DatagramSocket datagramSocket;
    private byte[] buf = new byte[256];

    public UDPsocket() {
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
