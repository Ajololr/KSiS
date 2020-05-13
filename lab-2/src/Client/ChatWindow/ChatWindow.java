package Client.ChatWindow;

import Client.ClientSocket.*;
import Client.FileStorageManager.FileStorageManager;
import Client.FileStorageManager.UniqueFile;
import javafx.scene.layout.GridPane;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChatWindow extends Frame implements WindowListener {
    public LinkedList<ChatMessages> chatsMessagesList = new LinkedList<>();
    public LinkedList<UniqueFile> filesList = new LinkedList<>();
    public Button sendButton;
    public Button addFileButton;
    public List chatsList;
    public TextField msgField;
    private ClientSocket client;
    private JScrollPane scrollPane = new JScrollPane();
    final ListPanel chatArea = new ListPanel();
    private int currentChatIndex;

    private static class Message {
        public String message;
        public LinkedList<UniqueFile> filesList;

        public JPanel getMessagePanel() {
            JPanel controlPane = new JPanel();
            controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.Y_AXIS));
            JTextArea textArea = new JTextArea(1, 30);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.append(message);
            controlPane.add(textArea);
            if (!filesList.isEmpty()) {
                for (UniqueFile uniqueFile: filesList) {
                    JPanel buttonPanel = new JPanel();
                    Label fileNameLabel  = new Label(uniqueFile.getOriginalName());
                    Button downloadButton = new Button("Download");
                    downloadButton.addActionListener(actionEvent -> {
                    });
                    int rgbColor = (int)(Math.random() * 0xFFFFFF);
                    buttonPanel.setBackground(new Color(rgbColor));
                    fileNameLabel.setForeground(new Color(rgbColor ^ 0xFFFFFF));
                    buttonPanel.add(fileNameLabel);
                    buttonPanel.add(downloadButton);
                    controlPane.add(buttonPanel);
                }
            }
            return controlPane;
        }

        public Message(String msg, LinkedList<UniqueFile> filesList) {
            message = msg;
            if (filesList == null) {
                this.filesList = new LinkedList<>();
            } else  {
                this.filesList = filesList;
            }
        }
    }

    private static class ChatMessages {
        public String chatName;
        public LinkedList<Message> messagesList;
        public int unreadMessagesCount;

        private ChatMessages(String chatName) {
            this.chatName = chatName;
            messagesList = new LinkedList<>();
            unreadMessagesCount = 0;
        }
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

    private void updateLayout() {
        revalidate();
        invalidate();
        repaint();
    }

    public void addMsg(String text, LinkedList<UniqueFile> files, int index) {
        Message message = new Message(text, files);
        chatsMessagesList.get(index).messagesList.add(message);
        if (currentChatIndex == index) {
            chatArea.addPanel(message.getMessagePanel(), 100);
        } else {
            chatsList.remove(index);
            chatsList.add(chatsMessagesList.get(index).chatName + " (" + (++chatsMessagesList.get(index).unreadMessagesCount) + ")", index);
        }
    }

    public void deleteChat(int index) {
        if (index == currentChatIndex) {
            currentChatIndex = 0;
            chatsList.select(0);
            scrollPane.removeAll();
            for (Message msg : chatsMessagesList.get(currentChatIndex).messagesList) {
                chatArea.addPanel(msg.getMessagePanel(), 100);
            }
        }
        chatsList.remove(index);
        chatsMessagesList.remove(index);
    }

    public void addChat(String nickname) {
        chatsList.add(nickname);
        chatsMessagesList.add(new ChatMessages(nickname));
    }

    public LinkedList<UniqueFile> parseFilesString(String filesArray) {
        if (filesArray.isEmpty()) {
            return null;
        }
        LinkedList<UniqueFile> result = new LinkedList<>();
        Pattern p = Pattern.compile("[A-z.0-9]+,[-A-z.0-9]+");
        Matcher m = p.matcher(filesArray);
        while (m.find()) {
            String[] data = m.group().split(",");
            result.add(new UniqueFile(data[0], Integer.parseInt(data[1])));
        }
        return result;
    }

    public String parseFilesList(LinkedList<UniqueFile> uniqueFiles) {
        String result = "[";
        for (UniqueFile uniqueFile: uniqueFiles) {
            result += "{" + uniqueFile.getOriginalName() + "," + uniqueFile.getID() + "}";
    }
        result += "]";
        System.out.println(result);
        return result;
    }

    public ChatWindow(ClientSocket client) {
        addWindowListener(this);
        this.client = client;
        chatsMessagesList.add(new ChatMessages("Global chat"));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        sendButton = new Button("Send");
        addFileButton = new Button("Add file");

        chatsList = new List(21, false);
        chatsList.add("Global chat");
        chatsList.select(0);
        currentChatIndex = 0;

        msgField = new TextField(49);

        chatsList.addActionListener(actionEvent -> {
            currentChatIndex = chatsList.getSelectedIndex();
            chatsList.remove(currentChatIndex);
            chatsList.add(chatsMessagesList.get(currentChatIndex).chatName, currentChatIndex);
            chatsList.select(currentChatIndex);
            chatsMessagesList.get(currentChatIndex).unreadMessagesCount = 0;
            scrollPane.removeAll();
            for (Message msg : chatsMessagesList.get(currentChatIndex).messagesList) {
                chatArea.addPanel(msg.getMessagePanel(), 100);
            }
        });

        sendButton.addActionListener(actionEvent -> {
            if (!msgField.getText().trim().equals("")) {
                client.send(msgField.getText().trim(), parseFilesList(filesList), currentChatIndex);
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
                filesList.add(new UniqueFile(file.getName(), 25));
                FileStorageManager fileStorageManager = new FileStorageManager();
                fileStorageManager.putFileToStorage(file);
                updateLayout();
            }
        });

        msgField.addActionListener(actionEvent -> {
            if (!msgField.getText().trim().equals("")) {
                client.send(msgField.getText().trim(), parseFilesList(filesList), currentChatIndex);
                msgField.setText("");
            }
        });

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(chatArea);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(340, 350));
        panel.setBackground(Color.CYAN);
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane);

        add(panel);
        add(chatsList);
        add(msgField);
        add(sendButton);
        add(addFileButton);

        this.setSize(500,500);
        updateLayout();
        this.setVisible(true);
        this.setTitle(client.nickname);
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
