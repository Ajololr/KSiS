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
        for (String member : Server.membersList) {
            this.send("1 0 0 " + member);
        }
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = in.readLine();
                System.out.println(msg);
                String[] data = msg.split(" ", 4);
                int command = Integer.parseInt(data[0]);
                int sender = Server.serverList.indexOf(this) + 1;
                int receiver = Integer.parseInt(data[2]);
                String text = data[3];
                switch (command) {
                    case 0:
                        if (receiver == 0) {
                            Server.msgList.add(msg);
                            for (TCPsocket clientSocket : Server.serverList) {
                                clientSocket.send(msg);
                            }
                        } else {
                            System.out.println("Server sends " + text + " from " + sender + " to " + receiver);
                            msg = command + " " + sender + " " + receiver + " " + text;
                            Server.serverList.get(receiver - 1).send(msg);
                            if (sender != receiver) {
                                msg = command + " " + receiver + " " + sender + " " + text;
                                Server.serverList.get(sender - 1).send(msg);
                            }
                        }
                        break;
                    case 1:
                        Server.membersList.add(text);
                        for (TCPsocket clientSocket : Server.serverList) {
                            clientSocket.send(msg);
                        }
                        break;
                    case 2:
                        msg = command + " " + sender + " " + receiver + " " + text;
                        for (TCPsocket clientSocket : Server.serverList) {
                            clientSocket.send(msg);
                        }
                        this.downService();
                        break;
                    default:
                        System.out.println("Unrecognized command" + command);
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
                System.out.println("TCP socket is being turned off...");
                socket.close();
                in.close();
                out.close();
                for (TCPsocket clientSocket : Server.serverList) {
                    if(clientSocket.equals(this)) clientSocket.interrupt();
                    int index = Server.serverList.indexOf(this);
                    Server.serverList.remove(this);
                    Server.membersList.remove(index);
                }
            }
        } catch (IOException ignored) {}
    }
}
