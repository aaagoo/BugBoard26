package gui.swing;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
import gui.util.Utility;
import modello.Account;
import modello.Notifica;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Notifiche extends BaseFrame {

    private JPanel midPanel;
    private JPanel dashboardPanel;
    private JScrollPane dashboardScroll;
    private JTable dashboardTable;
    private JPanel botPanel;
    private JButton indietroButton;
    private JPanel topPanel;
    private JPanel infoutentePanel;
    private JLabel ruoloLabel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JPanel mainPanel;


    public Notifiche(){
        super();
        setContentPane(mainPanel);
        setTitle("BugBoard26");
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

        dashboardTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = dashboardTable.getSelectedRow();
                    if (row != -1) {
                        String statoLetta = (String) dashboardTable.getValueAt(row, 3);
                        if ("Sì".equalsIgnoreCase(statoLetta)) {
                            String messaggio = (String) dashboardTable.getValueAt(row, 1);
                            JOptionPane.showMessageDialog(Notifiche.this, messaggio, "Dettaglio Notifica", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        Long id = Long.parseLong(dashboardTable.getValueAt(row, 0).toString());
                        segnaLettaAsincrono(id);
                    }
                }
            }
        });
    }

    private void caricaNotificheAsincrono() {
        showLoading();
        SwingWorker<List<Notifica>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Notifica> doInBackground() throws Exception {
                return Controller.getInstance().getNotifiche();
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    List<Notifica> notifiche = get();
                    popolaTabellaNotifiche(notifiche);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void popolaTabellaNotifiche(List<Notifica> notifiche) {
        String[] colonne = {"ID", "Messaggio", "Data", "Letta"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Notifica n : notifiche) {
            model.addRow(new Object[]{
                    n.getId(),
                    n.getMessaggio(),
                    Utility.formattaData(n.getDataCreazione()), 
                    n.isLetta() ? "Sì" : "No"
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
                    c.setForeground(new Color(172,71,53));
                } else {
                    c.setForeground(new Color(33, 37, 43));
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
