package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.Controller;
import gui.util.TabellaUtility;
import gui.util.RoundedPanel;
import javax.swing.table.TableColumn;

public class GestisciUtenti extends JFrame {

    private JPanel mainPanel;
    private JPanel operationsPanel;
    private JButton modificaAccountButton;
    private JButton creaAccountButton;
    private JButton indietroButton;
    private JPanel tablePanel;
    private JPanel utentiPanel;
    private JTable utentiTable;
    private JPanel amministratoriPanel;
    private JTable amministratoriTable;
    private JButton eliminaButton;

    private Controller controller;

    public GestisciUtenti() {
        setContentPane(mainPanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        tablePanel.setBorder(new RoundedPanel("pannello"));
        utentiPanel.setBorder(new RoundedPanel("finestra"));
        amministratoriPanel.setBorder(new RoundedPanel("finestra"));
        operationsPanel.setBorder(new RoundedPanel("pannello"));

        controller = Controller.getInstance();
        TabellaUtility.caricaDatiUtenti(utentiTable, amministratoriTable, controller);

        TableColumn column1 = utentiTable.getColumnModel().getColumn(3);
        column1.setPreferredWidth(200);
        TableColumn column2 = amministratoriTable.getColumnModel().getColumn(3);
        column2.setPreferredWidth(200);

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeAmm();
                dispose();
            }
        });

        creaAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreaAccount();
                dispose();
            }
        });

        modificaAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ModificaAccount();
                dispose();
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EliminaAccount();
                dispose();
            }
        });
    }
}
