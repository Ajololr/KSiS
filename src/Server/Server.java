package Server;

import Server.TCPsocket.TCPsocket;
import Server.UDPsocket.UDPsocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    private static UDPsocket connectionListener;
    public static LinkedList<TCPsocket> serverList = new LinkedList<>();

    public static void main(String[] args) {
        connectionListener = new UDPsocket();

        try {
            ServerSocket server = new ServerSocket();
            try {
                while (true) {
                    Socket socket = server.accept();
                    try {
                        serverList.add(new TCPsocket(socket));
                    } catch (IOException e) {
                        socket.close();
                    }
                }
            } finally {
                server.close();
            }
        } catch (IOException ex) {
        }
    }
}