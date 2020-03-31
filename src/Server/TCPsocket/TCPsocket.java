package Server.TCPsocket;

import java.io.*;
import java.net.*;
import Server.Server;

public class TCPsocket extends Thread {
    private Socket socket;
    private BufferedReader inputStream;
    private BufferedWriter outputStream;

    public TCPsocket(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        for (String msg : Server.msgList) {
            this.send(msg);
        }
        for (String member : Server.membersList) {
            this.send("1 0 0 " + member);
        }
        start();
    }

    private void deleteMember(int sender, int receiver, String text) {
        String msg = "2 " + sender + " " + receiver + " " + text;
        for (TCPsocket clientSocket : Server.serverList) {
            clientSocket.send(msg);
        }
        this.downService();
    }

    private void addMember(String member, String msg) {
        Server.membersList.add(member);
        for (TCPsocket clientSocket : Server.serverList) {
            clientSocket.send(msg);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = inputStream.readLine();
                String[] data = msg.split(" ", 4);
                int command = Integer.parseInt(data[0]);
                int sender = Server.serverList.indexOf(this) + 1;
                int receiver = Integer.parseInt(data[2]);
                String text = data[3];
                switch (command) {
                    case 0:
                        send(sender, receiver, text, msg);
                        break;
                    case 1:
                        addMember(text, msg);
                        break;
                    case 2:
                        deleteMember(sender, receiver, text);
                        break;
                    default:
                        System.out.println("Unrecognized command" + command);
                }
            }
        } catch (IOException | NullPointerException e) {
            this.downService();
        }
    }

    private void send(int sender, int receiver, String text, String msg) {
        if (receiver == 0) {
            Server.msgList.add(msg);
            for (TCPsocket clientSocket : Server.serverList) {
                clientSocket.send(msg);
            }
        } else {
            System.out.println("Server sends " + text + " from " + sender + " to " + receiver);
            msg = "0 " + sender + " " + receiver + " " + text;
            Server.serverList.get(receiver - 1).send(msg);
            if (sender != receiver) {
                msg = "0 " + receiver + " " + sender + " " + text;
                Server.serverList.get(sender - 1).send(msg);
            }
        }
    }

    private void send(String msg) {
        try {
            outputStream.write(msg + "\n");
            outputStream.flush();
        } catch (IOException ignored) {}
    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                System.out.println("TCP socket is being turned off...");
                socket.close();
                inputStream.close();
                outputStream.close();
                this.interrupt();
                int index = Server.serverList.indexOf(this);
                Server.serverList.remove(this);
                Server.membersList.remove(index);
            }
        } catch (IOException ignored) {}
    }
}
