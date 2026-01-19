package gui;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
import gui.util.Utility;
import modello.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class Notifiche extends BaseFrame {

    private JPanel midPanel;
    private JPanel dashboardPanel;
    private JScrollPane dashboardScroll;
    private JTable dashboardTable;
    private JPanel botPanel;
    private JButton indietroButton;
    private JButton segnaLettaButton;
    private JPanel topPanel;
    private JPanel infoutentePanel;
    private JLabel ruoloLabel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JPanel mainPanel;


    public Notifiche(){
        super();
        setContentPane(mainPanel);
        setTitle("Notifiche");
        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        midPanel.setBorder(new RoundedPanel("pannello"));
        infoutentePanel.setBorder(new RoundedPanel("pannello"));
        botPanel.setBorder(new RoundedPanel("pannello"));
        dashboardPanel.setBorder(new RoundedPanel("finestra"));

        Account utente = Controller.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
        }

        StyleManager.styleTable(dashboardTable, dashboardScroll);

        caricaNotificheAsincrono();

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUtente();
                dispose();
            }
        });

        segnaLettaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = dashboardTable.getSelectedRow();
                if (row != -1) {
                    String statoLetta = (String) dashboardTable.getValueAt(row, 3);
                    if ("Sì".equalsIgnoreCase(statoLetta)) {
                        JOptionPane.showMessageDialog(Notifiche.this, "Questa notifica è già stata letta.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    Long id = Long.parseLong(dashboardTable.getValueAt(row, 0).toString());
                    segnaLettaAsincrono(id);
                } else {
                    JOptionPane.showMessageDialog(Notifiche.this, "Seleziona una notifica.");
                }
            }
        });
    }

    private void caricaNotificheAsincrono() {
        showLoading();
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return Controller.getInstance().getNotifiche();
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    List<Map<String, Object>> notifiche = get();
                    popolaTabellaNotifiche(notifiche);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void popolaTabellaNotifiche(List<Map<String, Object>> notifiche) {
        String[] colonne = {"ID", "Messaggio", "Data", "Letta"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Map<String, Object> n : notifiche) {
            model.addRow(new Object[]{
                    n.get("id"),
                    n.get("messaggio"),
                    Utility.formattaData(n.get("datacreazione")), 
                    (Boolean) n.get("letta") ? "Sì" : "No"
            });
        }
        dashboardTable.setModel(model);

        Utility.impostaLarghezzeColonne(dashboardTable, 50, 600, 150, 50);
        
        dashboardTable.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("No".equals(value)) {
                    c.setFont(c.getFont().deriveFont(java.awt.Font.BOLD));
                    c.setForeground(java.awt.Color.RED);
                } else {
                    c.setForeground(java.awt.Color.BLACK);
                }
                return c;
            }
        });
    }

    private void segnaLettaAsincrono(Long id) {
        showLoading();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Controller.getInstance().segnaNotificaLetta(id);
                return null;
            }

            @Override
            protected void done() {
                hideLoading();
                caricaNotificheAsincrono();
            }
        };
        worker.execute();
    }
}
