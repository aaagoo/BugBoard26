package gui;

import javax.swing.*;

public class Home extends JFrame {

    private JPanel mainpanel;

    public Home() {
        setContentPane(mainpanel);
        setTitle("Login");
        setSize(1000,500);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
