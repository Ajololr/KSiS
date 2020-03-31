package Client.ChatWindow;

import Client.ClientSocket.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

public class ChatWindow extends Frame implements WindowListener {
    public LinkedList<LinkedList<String>> membersList = new LinkedList<>();
    public Button sendButton;
    public List chatsList;
    public TextField msgField;
    public TextArea chatArea;
    private ClientSocket client;
    private int currentChatIndex;

    public ChatWindow(ClientSocket client) {
        addWindowListener(this);
        this.client = client;
        membersList.add(new LinkedList<>());
        setLayout(new FlowLayout(FlowLayout.LEFT));

        sendButton = new Button("Send");

        chatsList = new List(15, false);
        chatsList.add("Global chat");
        chatsList.select(0);
        currentChatIndex = 0;

        msgField = new TextField(49);

        chatArea = new TextArea("", 15, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        chatArea.setEditable(false);

        chatsList.addActionListener(actionEvent -> {
            currentChatIndex = (int)chatsList.getSelectedIndex();
            chatArea.setText("");
            for (String text : membersList.get(currentChatIndex)) {
                chatArea.append(text);
            }
        });

        sendButton.addActionListener(actionEvent -> {
            if (msgField.getText().trim() != "") {
                client.send(msgField.getText().trim(), currentChatIndex);
                msgField.setText("");
            }
        });

        msgField.addActionListener(actionEvent -> {
            if (msgField.getText().trim() != "") {
                client.send(msgField.getText().trim(), currentChatIndex);
                msgField.setText("");
            }
        });

        add(chatArea);
        add(chatsList);
        add(msgField);
        add(sendButton);

        this.setSize(500,310);
        this.setVisible(true);
        this.setTitle("Lab-2");
    }

    public void addMsg(String msg, int index) {
        membersList.get(index).add(msg);
        if (currentChatIndex == index) {
            chatArea.append(msg);
        }
    }

    public void deleteChat(int index) {
        if (index == currentChatIndex) {
            currentChatIndex = 0;
            chatsList.select(0);
            chatArea.setText("");
            for (String text : membersList.get(currentChatIndex)) {
                chatArea.append(text);
            }
        }
        chatsList.remove(index);
        membersList.remove(index);
    }

    public void addChat(String nickname) {
        chatsList.add(nickname);
        membersList.add(new LinkedList<>());
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) { }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        client.downService();
        dispose();
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) { }

    @Override
    public void windowIconified(WindowEvent windowEvent) { }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) { }

    @Override
    public void windowActivated(WindowEvent windowEvent) { }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) { }
}
