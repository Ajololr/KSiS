package Client.ChatWindow;

import Client.ClientSocket.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
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

    public class ListPanel extends JPanel
    {
        private static final long serialVersionUID = 1L;
        private JPanel fillerPanel;
        private ArrayList<JPanel> panels;

        public ListPanel(java.util.List<JPanel> panels, int height)
        {
            this(panels, height, new Insets(2, 0, 2, 0));
        }

        public ListPanel(java.util.List<JPanel> panels, int height, Insets insets)
        {
            this();
            for (JPanel panel : panels)
                addPanel(panel, height, insets);
        }

        public ListPanel()
        {
            super();
            this.fillerPanel = new JPanel();
            this.fillerPanel.setMinimumSize(new Dimension(0, 0));
            this.panels = new ArrayList<JPanel>();
            setLayout(new GridBagLayout());
        }

        public void addPanel(JPanel p, int height)
        {
            addPanel(p, height, new Insets(2, 0, 2, 0));
        }

        public void addPanel(JPanel p, int height, Insets insets)
        {
            super.remove(fillerPanel);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = getComponentCount();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.PAGE_START;
            gbc.ipady = height;
            gbc.insets = insets;
            gbc.weightx = 1.0;
            panels.add(p);
            add(p, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = getComponentCount();
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.weighty = 1.0;
            add(fillerPanel, gbc);
            revalidate();
            invalidate();
            repaint();
        }

        public void removePanel(JPanel p)
        {
            removePanel(panels.indexOf(p));
        }

        public void removePanel(int i)
        {
            super.remove(i);
            panels.remove(i);
            revalidate();
            invalidate();
            repaint();
        }

        public ArrayList<JPanel> getPanels()
        {
            return this.panels;
        }

        public JPanel getRandomJPanel()
        {
            JPanel panel = new JPanel();
            panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
            panel.add(new JLabel("This is a randomly sized JPanel"));
            panel.setBackground(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            return panel;
        }
    }

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

    private JPanel createPanel() {
        JPanel controlPane;
        Button deleteButton;
        Label fileNameLabel;
        controlPane = new JPanel();
        int rgbColor = (int)(Math.random() * 0xFFFFFF);
        controlPane.setBackground(new Color(rgbColor));
        controlPane.setLayout(new FlowLayout());
        deleteButton = new Button("Delete file");
        deleteButton.addActionListener(actionEvent -> {
            remove(controlPane);
            updateLayout();
        });
        fileNameLabel = new Label("test");
        fileNameLabel.setForeground(new Color(rgbColor ^ 0xFFFFFF));
        controlPane.add(fileNameLabel);
        controlPane.add(deleteButton);
        controlPane.setSize(250,50);
        controlPane.setVisible(false);
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

//        JList<JPanel> panelJList = new JList<JPanel>();
//        panelJList.add(createPanel());
//        panelJList.add(createPanel());
//        panelJList.setVisible(true);

//        JPanel jpAcc = new JPanel();
//        jpAcc.setSize(300, 300);
//        JScrollPane scrollPane = new JScrollPane(panelJList);
//        jpAcc.add(scrollPane, BorderLayout.CENTER);
//        jpAcc.setVisible(true);
//        pack();
//        JScrollPane jScrollPane = new JScrollPane(panelJList);
//        jScrollPane.setLayout(new ScrollPaneLayout());

        final ListPanel listPanel = new ListPanel();
        for (int i = 1; i <= 10; i++)
            listPanel.addPanel(listPanel.getRandomJPanel(), new Random().nextInt(50) + 50);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(listPanel);

        add(chatArea);
        add(chatsList);
        add(msgField);
        add(sendButton);
        add(addFileButton);
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 300));
        panel.setBackground(Color.CYAN);
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane);
        add(panel);

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
