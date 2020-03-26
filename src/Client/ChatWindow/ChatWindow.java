package Client.ChatWindow;

import Client.Client;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ChatWindow extends Frame implements WindowListener {
    public Label membersLbl;
    public Label msgLbl;
    public Button sendButton;
    public List chatsList;
    public TextField msgField;
    public TextArea chatArea;
    private Client client;

    public ChatWindow(Client client) {
        this.client = client;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        membersLbl = new Label("Members:");
        msgLbl = new Label("Message:");

        sendButton = new Button("Send");

        chatsList = new List(15, false);
        chatsList.add("Global chat");
        chatsList.select(0);

        msgField = new TextField(49);

        chatArea = new TextArea("", 15, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        chatArea.setEditable(false);

        chatsList.addActionListener(actionEvent -> {
            System.out.println(actionEvent);
        });

        sendButton.addActionListener(actionEvent -> {
            if (msgField.getText().trim() != "") {
                client.WriteMsg(msgField.getText().trim());
                msgField.setText("");
            }
        });

        msgField.addActionListener(actionEvent -> {
            if (msgField.getText().trim() != "") {
                client.WriteMsg(msgField.getText().trim());
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

    public void displayMsg(String msg) {
        chatArea.append(msg);
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }
}
