package Client.ChatWindow;

import Client.ClientSocket.*;
import Client.FileStorageManager.UniqueFile;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Random;


public class ChatWindow extends Frame implements WindowListener {
    public LinkedList<ChatMessages> chatsMessagesList = new LinkedList<>();
    public Button sendButton;
    public Button addFileButton;
    public List chatsList;
    public TextField msgField;
    public TextArea chatArea;
    private ClientSocket client;
    private Container newChatArea;
    private int currentChatIndex;

    private class FileMessage {
        public Container controlPane;
        public Label fileNameLabel;
    }

    private class ChatMessage {
        public Container controlPane;
        public Button downloadButton;
        public Label mes;
        public String message;
        public FileMessage[] filesArray;
    }

    private Panel createFileControlElement(File file) {
        Panel controlPane;
        Button deleteButton;
        Label fileNameLabel;
        controlPane = new Panel();
        int rgbColor = (int)(Math.random() * 0xFFFFFF);
        controlPane.setBackground(new Color(rgbColor));
        controlPane.setLayout(new FlowLayout());
        deleteButton = new Button("Delete file");
        deleteButton.addActionListener(actionEvent -> {
            remove(controlPane);
            updateLayout();
        });
        fileNameLabel = new Label(file.getName());
        fileNameLabel.setForeground(new Color(rgbColor ^ 0xFFFFFF));
        controlPane.add(fileNameLabel);
        controlPane.add(deleteButton);
        controlPane.setVisible(true);
        return controlPane;
    }

    private class ChatMessages {
        public String chatName;
        public LinkedList<String> messagesList;
        public int unreadMessagesCount;

        private ChatMessages(String chatName) {
            this.chatName = chatName;
            messagesList = new LinkedList();
            unreadMessagesCount = 0;
        }
    }

    private void updateLayout() {
        this.setVisible(false);
        this.setVisible(true);
    }

    public ChatWindow(ClientSocket client) {
        addWindowListener(this);
        this.client = client;
        chatsMessagesList.add(new ChatMessages("Global chat"));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        sendButton = new Button("Send");
        addFileButton = new Button("Add file");

        chatsList = new List(15, false);
        chatsList.add("Global chat");
        chatsList.select(0);
        currentChatIndex = 0;

        msgField = new TextField(49);

        chatArea = new TextArea("", 15, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        chatArea.setEditable(false);

        chatsList.addActionListener(actionEvent -> {
            currentChatIndex = chatsList.getSelectedIndex();
            chatsList.remove(currentChatIndex);
            chatsList.add(chatsMessagesList.get(currentChatIndex).chatName, currentChatIndex);
            chatsList.select(currentChatIndex);
            chatsMessagesList.get(currentChatIndex).unreadMessagesCount = 0;
            chatArea.setText("");
            for (String text : chatsMessagesList.get(currentChatIndex).messagesList) {
                chatArea.append(text);
            }
        });

        sendButton.addActionListener(actionEvent -> {
            if (!msgField.getText().trim().equals("")) {
                client.send(msgField.getText().trim(), currentChatIndex);
                msgField.setText("");
            }
        });

        addFileButton.addActionListener(actionEvent -> {
            FileDialog fileDialog = new FileDialog((Frame) null);
            fileDialog.setVisible(true);
            String filename = fileDialog.getFile();
            String directory = fileDialog.getDirectory();
            if (filename == null)
                System.out.println("You cancelled the choice");
            else {
                System.out.println("You chose " + directory + filename);
                File file = new File(directory + filename);
                add(createFileControlElement(file));
                updateLayout();
            }
        });

        msgField.addActionListener(actionEvent -> {
            if (!msgField.getText().trim().equals("")) {
                client.send(msgField.getText().trim(), currentChatIndex);
                msgField.setText("");
            }
        });

        add(chatArea);
        add(chatsList);
        add(msgField);
        add(sendButton);
        add(addFileButton);

        this.setSize(500,400);
        this.setVisible(true);
        this.setTitle(client.nickname);
    }

    public void addMsg(String msg, int index) {
        chatsMessagesList.get(index).messagesList.add(msg);
        if (currentChatIndex == index) {
            chatArea.append(msg);
        } else {
            chatsList.remove(index);
            chatsList.add(chatsMessagesList.get(index).chatName + " (" + (++chatsMessagesList.get(index).unreadMessagesCount) + ")", index);
        }
    }

    public void deleteChat(int index) {
        if (index == currentChatIndex) {
            currentChatIndex = 0;
            chatsList.select(0);
            chatArea.setText("");
            for (String text : chatsMessagesList.get(currentChatIndex).messagesList) {
                chatArea.append(text);
            }
        }
        chatsList.remove(index);
        chatsMessagesList.remove(index);
    }

    public void addChat(String nickname) {
        chatsList.add(nickname);
        chatsMessagesList.add(new ChatMessages(nickname));
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
