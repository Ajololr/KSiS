package Client.LoginWindow;

import Client.Client;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LoginWindow extends Frame implements WindowListener {
    public Label nameLbl;
    public TextField nameField;
    public Button joinButton;

    public LoginWindow() {
        addWindowListener(this);
        setLayout(new FlowLayout(FlowLayout.CENTER));

        nameLbl = new Label("Your name:");
        joinButton = new Button("Join chat");
        nameField = new TextField(10);

        joinButton.addActionListener(actionEvent -> {
            new Client(nameField.getText());
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        nameField.addTextListener(action -> {
            joinButton.setEnabled(nameField.getText().length() > 0);
        });

        nameField.addActionListener(actionEvent -> {
            new Client(nameField.getText());
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        add(nameLbl);
        add(nameField);
        add(joinButton);

        this.setSize(300, 300);
        this.setVisible(true);
        this.setTitle("Lab-2");
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        dispose();
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
