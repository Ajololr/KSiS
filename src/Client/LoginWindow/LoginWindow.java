package Client.LoginWindow;

import Client.Client;
import java.awt.*;

public class LoginWindow extends Frame {
    public Label nameLbl;
    public TextField nameField;
    public Button joinButton;

    public LoginWindow() {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        nameLbl = new Label("Your name:");
        joinButton = new Button("Join chat");
        nameField = new TextField(10);

        joinButton.addActionListener(actionEvent -> {
            new Client(nameField.getText());
        });

        nameField.addTextListener(action -> {
            joinButton.setEnabled(nameField.getText().length() > 0);
        });

        nameField.addActionListener(actionEvent -> {
            new Client(nameField.getText());
        });

        add(nameLbl);
        add(nameField);
        add(joinButton);

        this.setSize(300, 300);
        this.setVisible(true);
        this.setTitle("Lab-2");
    }
}
