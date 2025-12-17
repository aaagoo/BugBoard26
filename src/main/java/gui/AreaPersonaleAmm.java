package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AreaPersonaleAmm extends JFrame {
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel infoPanel;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel datiouterPanel;
    private JPanel datiPanel;
    private JLabel nomeLabel;
    private JLabel cognomeLabel;
    private JLabel usernameLabel;
    private JButton modificaAvatarButton;
    private JPanel rightPanel;
    private JPanel tablePanel;
    private JTable utentiTable;
    private JPanel buttonPanel;
    private JButton creaButton;
    private JButton indietroButton;
    private JButton eliminaButton;
    private JLabel emailLabel;
    private JButton modificaButton;


    public AreaPersonaleAmm() {
        setContentPane(mainPanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeAmm();
                dispose();
            }
        });
    }
}
