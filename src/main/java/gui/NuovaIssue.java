package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NuovaIssue extends JFrame {


    private JPanel mainpanel;
    private JPanel topPanel;
    private JPanel infoutentePanel;
    private JLabel benvenuto;
    private JLabel userpngLabel;
    private JButton areaPersonaleButton;
    private JPanel botPanel;
    private JButton confermaButton;
    private JButton annullaButton;
    private JPanel midPanel;
    private JCheckBox criticalCheckBox;
    private JCheckBox lowCheckBox;
    private JCheckBox highCheckBox;
    private JCheckBox mediumCheckBox;

    public NuovaIssue() {
        setContentPane(mainpanel);
        setTitle("Creazione Issue");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);


        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Home();
                dispose();
            }
        });
    }

}
