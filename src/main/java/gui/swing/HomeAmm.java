package gui.swing;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
import gui.util.Utility;
import modello.Account;
import sessione.SessioneManager;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HomeAmm extends BaseFrame {

    private JPanel infoutentePanel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JLabel ruoloLabel;
    private JPanel midPanel;
    private JPanel operationsPanel;
    private JButton gestisciUtentiButton;
    private JPanel dashboardPanel;
    private JTable dashboardTable;
    private JPanel botPanel;
    private JButton disconnettitiButton;
    private JPanel mainPanel;
    private JPanel topPanel;
    private JButton modificaButton;
    private JButton eliminaButton;
    private JScrollPane dashboardScroll;
    private JPanel filtroPanel;
    private JButton filtraButton;
    private JButton resetButton;

    private TableRowSorter<javax.swing.table.TableModel> sorter;

    public HomeAmm() {
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
        operationsPanel.setBorder(new RoundedPanel("finestra"));
        dashboardPanel.setBorder(new RoundedPanel("finestra"));
        filtroPanel.setBorder(new RoundedPanel("pannello"));

        StyleManager.styleTable(dashboardTable, dashboardScroll);

        dashboardScroll.getVerticalScrollBar().setUnitIncrement(8);

        Account utente = SessioneManager.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
        }

        dashboardTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = dashboardTable.getSelectedRow();
                    if (row != -1) {
                        int modelRow = dashboardTable.convertRowIndexToModel(row);
                        Object idObj = dashboardTable.getModel().getValueAt(modelRow, 0);
                        
                        Long issueId = null;
                        if (idObj instanceof Number) {
                            issueId = ((Number) idObj).longValue();
                        }
                        
                        if (issueId != null) {
                            new AzioniIssue(HomeAmm.this, issueId, AzioniIssue.Provenienza.HOME, () -> caricaDatiAsincrono());
                        }
                    }
                }
            }
        });

        caricaDatiAsincrono();

        disconnettitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login();
                dispose();
            }
        });

        gestisciUtentiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestisciUtenti();
                dispose();
            }
        });

        filtraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FIltraIssue(HomeAmm.this, filtri -> applicaFiltriTabella(filtri));
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sorter != null) {
                    sorter.setRowFilter(null);
                }
            }
        });
    }

    private void caricaDatiAsincrono() {
        showLoading();

        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return Controller.getInstance().getAllIssues();
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    List<Map<String, Object>> dati = get();

                    Utility.popolaTabellaIssue(dashboardTable, dati);
                    Utility.impostaColorazioneRisolto(dashboardTable);
                    Utility.impostaLarghezzeColonne(dashboardTable, 15, 100, 20, 40, 60, 60, 60, 20);

                    sorter = new TableRowSorter<>(dashboardTable.getModel());
                    dashboardTable.setRowSorter(sorter);
                    
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(HomeAmm.this, 
                            "Errore nel caricamento dei dati: " + ex.getMessage(), 
                            "Errore", 
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void applicaFiltriTabella(Map<String, Object> filtri) {
        if (sorter == null) return;

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (filtri.containsKey("id")) {
            filters.add(RowFilter.regexFilter("^" + filtri.get("id") + "$", 0));
        }
        if (filtri.containsKey("parole")) {
            filters.add(RowFilter.regexFilter("(?i)" + filtri.get("parole"), 1)); 
        }
        if (filtri.containsKey("priorita")) {
            filters.add(RowFilter.regexFilter(filtri.get("priorita").toString(), 2));
        }
        if (filtri.containsKey("tipo")) {
            filters.add(RowFilter.regexFilter(filtri.get("tipo").toString(), 3));
        }
        if (filtri.containsKey("creatore")) {
            filters.add(RowFilter.regexFilter(filtri.get("creatore").toString(), 4));
        }
        if (filtri.containsKey("assegnatario")) {
            filters.add(RowFilter.regexFilter(filtri.get("assegnatario").toString(), 5));
        }
        if (filtri.containsKey("data")) {
            filters.add(RowFilter.regexFilter(filtri.get("data").toString(), 6));
        }
        if (filtri.containsKey("risolto")) {
            String val = filtri.get("risolto").toString();
            filters.add(RowFilter.regexFilter("^" + val + "$", 7));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }
}
