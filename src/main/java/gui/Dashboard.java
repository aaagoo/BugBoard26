package gui;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
import gui.util.Utility;
import modello.Account;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Dashboard extends BaseFrame {
    private JPanel botPanel;
    private JButton indietroButton;
    private JPanel midPanel;
    private JPanel dashboardPanel;
    private JTable dashboardTable;
    private JPanel topPanel;
    private JPanel infoutentePanel;
    private JPanel mainPanel;
    private JLabel userpngLabel;
    private JLabel ruoloLabel;
    private JLabel benvenutoLabel;
    private JButton eliminaButton;
    private JButton modificaButton;
    private JButton filtraButton;
    private JPanel filtroPanel;
    private JButton resetButton;
    private JScrollPane dashboardScroll;

    private Controller controller;
    private TableRowSorter<javax.swing.table.TableModel> sorter;

    public Dashboard() {
        super();
        controller = Controller.getInstance();

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
        filtroPanel.setBorder(new RoundedPanel("pannello"));

        Account utente = Controller.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
        }

        StyleManager.styleTable(dashboardTable, dashboardScroll);

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
                            new VIsualizzaIssue(issueId, VIsualizzaIssue.Provenienza.DASHBOARD);
                            dispose();
                        }
                    }
                }
            }
        });

        caricaDatiAsincrono();

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUtente();
                dispose();
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EliminaIssue(EliminaIssue.Provenienza.DASHBOARD);
                dispose();
            }
        });

        if (modificaButton != null) {
            modificaButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ModificaIssueSeleziona(ModificaIssueSeleziona.Provenienza.DASHBOARD);
                    dispose();
                }
            });
        }

        if (filtraButton != null) {
            filtraButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new FIltraIssue(Dashboard.this, filtri -> applicaFiltriTabella(filtri));
                }
            });
        }

        if (resetButton != null) {
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (sorter != null) {
                        sorter.setRowFilter(null);
                    }
                }
            });
        }
    }

    private void caricaDatiAsincrono() {
        showLoading();

        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return controller.getAllIssues();
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
                    JOptionPane.showMessageDialog(Dashboard.this, 
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
