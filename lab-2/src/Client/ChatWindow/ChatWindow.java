package Client.ChatWindow;

import Client.ClientSocket.*;
import Client.FileStorageManager.FileStorageManager;
import Client.FileStorageManager.UniqueFile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatWindow extends Frame implements WindowListener {
    public LinkedList<ChatMessages> chatsMessagesList = new LinkedList<>();
    public LinkedList<UniqueFile> filesList = new LinkedList<>();
    private LinkedList<Panel> filesPanelsList = new LinkedList<>();
    private final FileStorageManager fileStorageManager = new FileStorageManager();
    private final String[] forbiddenExtensions = {".exe", ".jar"};
    public Button sendButton;
    public Button addFileButton;
    public List chatsList;
    public TextField msgField;
    private ClientSocket client;
    private JScrollPane scrollPane = new JScrollPane();
    final ListPanel chatArea = new ListPanel();
    private int currentChatIndex;

    private class Message {
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
                        fileStorageManager.getFileFromStorage(uniqueFile);
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

    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void clearFilesForSending() {
        for (Panel filePanel: filesPanelsList) {
            this.remove(filePanel);
        }
        filesList.clear();
        filesPanelsList.clear();
    }

    private boolean isValidFile(String fileName) {
        LinkedList<UniqueFile> result = new LinkedList<>();
        for (String extension : forbiddenExtensions) {
            Pattern p = Pattern.compile(extension + "$");
            Matcher m = p.matcher(fileName);
            if (m.find()) {
                return false;
            }
        }
        return true;
    }

    private String correctFileName(String fileName) {
        Pattern pattern = Pattern.compile(" ");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.replaceAll("_");
    }

    private Panel createFileControlElement(UniqueFile file) {
        Panel controlPane;
        Button deleteButton;
        Label fileNameLabel;
        controlPane = new Panel();
        int rgbColor = (int)(Math.random() * 0xFFFFFF);
        controlPane.setBackground(new Color(rgbColor));
        controlPane.setLayout(new FlowLayout());
        deleteButton = new Button("Delete file");
        deleteButton.addActionListener(actionEvent -> {
            try {
                fileStorageManager.deleteFileFromStorage(filesList.get(filesList.indexOf(file)), sendButton);
            } catch (Exception ex) {
                System.out.println("File has already been deleted.");
            }
            filesPanelsList.remove(controlPane);
            filesList.remove(file);
            remove(controlPane);
            updateLayout();
        });
        fileNameLabel = new Label(file.getOriginalName());
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
        Pattern p = Pattern.compile("[A-z.\\-_А-я0-9]+,[-A-z.0-9]+");
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

    public void sendHandler() {
        if (!msgField.getText().trim().equals("")) {
            client.send(msgField.getText().trim(), parseFilesList(filesList), currentChatIndex);
            clearFilesForSending();
            msgField.setText("");
        }
    }

    public ChatWindow(ClientSocket client) {
        addWindowListener(this);
        this.client = client;
        chatsMessagesList.add(new ChatMessages("Global chat"));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        chatsList = new List(21, false);
        chatsList.add("Global chat");
        chatsList.select(0);
        chatsList.addActionListener(actionEvent -> {
            currentChatIndex = chatsList.getSelectedIndex();
            chatsList.remove(currentChatIndex);
            chatsList.add(chatsMessagesList.get(currentChatIndex).chatName, currentChatIndex);
            chatsList.select(currentChatIndex);
            chatsMessagesList.get(currentChatIndex).unreadMessagesCount = 0;
            chatArea.removeAll();
            updateLayout();
            for (Message msg : chatsMessagesList.get(currentChatIndex).messagesList) {
                chatArea.addPanel(msg.getMessagePanel(), 100);
            }
        });
        currentChatIndex = 0;

        msgField = new TextField(49);
        msgField.addActionListener(actionEvent -> {
            sendHandler();
        });

        sendButton = new Button("Send");
        sendButton.addActionListener(actionEvent -> {
            sendHandler();
        });

        addFileButton = new Button("Add file");
        addFileButton.addActionListener(actionEvent -> {
            FileDialog fileDialog = new FileDialog((Frame) null);
            fileDialog.setVisible(true);
            String filename = fileDialog.getFile();
            String directory = fileDialog.getDirectory();
            if (filename != null) {
                if (!isValidFile(filename)) {
                    showErrorMessage("Forbidden extension", "File " + filename +  " with such extension is not allowed.");
                } else  {
                    File file = new File(directory + filename);
                    UniqueFile newFileEntry = new UniqueFile(correctFileName(filename));
                    filesList.add(newFileEntry);
                    try {
                        fileStorageManager.putFileToStorage(file, newFileEntry, sendButton);
                        Panel filePanel = createFileControlElement(newFileEntry);
                        filesPanelsList.add(filePanel);
                        add(filePanel);
                        updateLayout();
                    } catch (Exception ex) {
                        System.out.println("Error");
                    }
                }
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
