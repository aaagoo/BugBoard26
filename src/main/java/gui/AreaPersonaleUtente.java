package gui;

import gui.util.RoundedPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AreaPersonaleUtente extends JFrame {
    private JPanel mainPanel;
    private JButton indietroButton;
    private JPanel utentePanel;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel datiPanel;
    private JLabel nomeLabel;
    private JLabel cognomeLabel;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JPanel buttonPanel;
    private JButton modificaButton;


    public AreaPersonaleUtente() {
        setContentPane(mainPanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        utentePanel.setBorder(new RoundedPanel("pannello"));
        buttonPanel.setBorder(new RoundedPanel("pannello"));
        datiPanel.setBorder(new RoundedPanel("finestra"));
        imagePanel.setBorder(new RoundedPanel("finestra"));


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUtente();
                dispose();
            }
        });

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ModificaUtente();
                dispose();
            }
        });
    }
}
