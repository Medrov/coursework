package Util;

import javax.swing.*;
import java.awt.*;

public class Preloader extends JDialog {
    private JLabel label;

    public Preloader(JFrame parent) {
        super(parent, "Loading", true);
        setLayout(new BorderLayout());
        label = new JLabel("Please wait...", JLabel.CENTER);
        add(label, BorderLayout.CENTER);
        setSize(300, 100);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }

    public void setMessage(String message) {
        label.setText(message);
    }
}
