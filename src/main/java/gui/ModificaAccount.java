package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModificaAccount extends JFrame {

    private JPanel mainPanel;
    private JPanel tablePanel;
    private JPanel utentiPanel;
    private JTable utentiTable;
    private JPanel amministratoriPanel;
    private JTable amministratoriTable;
    private JPanel operationsPanel;
    private JTextField nomeutenteField;
    private JPanel buttonsPanel;
    private JButton annullaButton;
    private JButton eliminaButton;
    private JComboBox comboBox1;

    public ModificaAccount() {
        setContentPane(mainPanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestisciUtenti();
                dispose();
            }
        });
    }
}
