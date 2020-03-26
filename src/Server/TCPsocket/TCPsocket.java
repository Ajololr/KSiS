package Server.TCPsocket;

import java.io.*;
import java.net.*;
import Server.Server;

public class TCPsocket extends Thread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public TCPsocket(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        for (String msg : Server.msgList) {
            this.send(msg);
        }
        start();
    }

    @Override
    public void run() {
        String word;
        try {
            while (true) {
                word = in.readLine();
                if(word.equals("exit")) {
                    this.downService();
                    break;
                }
                Server.msgList.add(word);
                for (TCPsocket clientSocket : Server.serverList) {
                    clientSocket.send(word);
                }
            }

        } catch (IOException | NullPointerException e) {
            this.downService();
        }
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (TCPsocket clientSocket : Server.serverList) {
                    if(clientSocket.equals(this)) clientSocket.interrupt();
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}
