package gui.swing;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
import modello.Account;
import controller.Controller;
import gui.util.Utility;
import sessione.SessioneManager;

public class HomeUtente extends BaseFrame {
    private JPanel mainPanel;
    private JPanel infoutentePanel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JButton areaPersonaleButton;
    private JPanel operationsPanel;
    private JButton nuovaIssueButton;
    private JButton dashboardButton;
    private JTable dashboardTable;
    private JButton disconnettitiButton;
    private JPanel topPanel;
    private JPanel midPanel;
    private JPanel botPanel;
    private JLabel ruoloLabel;
    private JPanel dashboardPanel;
    private JScrollPane dashboardScroll;
    private JButton notificheButton;
    private JPanel filtroPanel;
    private JButton filtraButton;
    private JButton resetButton;

    private TableRowSorter<javax.swing.table.TableModel> sorter;

    public HomeUtente() {
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

        Account utente = Controller.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
        }

        StyleManager.styleTable(dashboardTable, dashboardScroll);

        dashboardTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    int row = dashboardTable.getSelectedRow();
                    if (row != -1) {
                        int modelRow = dashboardTable.convertRowIndexToModel(row);
                        Object idObj = dashboardTable.getModel().getValueAt(modelRow, 0);
                        Long issueId = Long.parseLong(idObj.toString());
                        new VisualizzaIssue(issueId);
                        dispose();
                    }
                }
            }
        });

        caricaDatiAsincrono();

        nuovaIssueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NuovaIssue();
                dispose();
            }
        });

        disconnettitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SessioneManager.getInstance().logout();
                new Login();
                dispose();
            }
        });

        areaPersonaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AreaPersonale();
                dispose();
            }
        });

        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dashboard();
                dispose();
            }
        });

        notificheButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Notifiche();
                dispose();
            }
        });

        filtraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FIltraIssue(HomeUtente.this, filtri -> applicaFiltriTabella(filtri), true);
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
        controllaNotifiche();
    }

    private void caricaDatiAsincrono() {
        showLoading();
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                Account utente = Controller.getInstance().getUtenteCorrente();
                if (utente != null) {
                    return Controller.getInstance().getIssueAssegnate(utente.getNomeUtente());
                }
                return new ArrayList<>();
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    List<Map<String, Object>> dati = get();
                    
                    String[] colonne = {"ID", "Titolo", "Priorità", "Tipo", "Creatore", "Data Creazione", "Risolto"};
                    javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(colonne, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };

                    for (Map<String, Object> riga : dati) {
                        model.addRow(new Object[]{
                                riga.get("id"),
                                riga.get("titolo"),
                                riga.get("priorita"),
                                riga.get("tipo"),
                                riga.get("creatoreusername"),
                                Utility.formattaData(riga.get("datacreazione")),
                                (Boolean) riga.get("risolto") ? "Sì" : "No"
                        });
                    }
                    dashboardTable.setModel(model);
                    
                    Utility.impostaColorazioneRisolto(dashboardTable);
                    Utility.impostaLarghezzeColonne(dashboardTable, 17, 70, 20, 40, 40, 30, 10);
                    
                    sorter = new TableRowSorter<>(dashboardTable.getModel());
                    dashboardTable.setRowSorter(sorter);

                } catch (Exception e) {
                    e.printStackTrace();
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
        
        if (filtri.containsKey("data")) {
            filters.add(RowFilter.regexFilter(filtri.get("data").toString(), 5));
        }
        
        if (filtri.containsKey("risolto")) {
            String val = filtri.get("risolto").toString();
            filters.add(RowFilter.regexFilter("^" + val + "$", 6));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void controllaNotifiche() {
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return Controller.getInstance().contaNotificheNonLette();
            }

            @Override
            protected void done() {
                try {
                    int count = get();
                    if (count > 0) {
                        if (notificheButton != null) {
                            notificheButton.setText("Notifiche (" + count + ")");
                            notificheButton.setBackground(new Color(172, 71, 53));
                        }

                        if (!SessioneManager.getInstance().isNotificheGiaControllate()) {
                            JOptionPane.showMessageDialog(HomeUtente.this, 
                                    "Hai " + count + " nuove notifiche.", 
                                    "Nuove Notifiche", 
                                    JOptionPane.INFORMATION_MESSAGE);
                            SessioneManager.getInstance().setNotificheGiaControllate(true);
                        }
                        
                    } else {
                        if (notificheButton != null) {
                            notificheButton.setText("Notifiche");
                            notificheButton.setBackground(new Color(54, 172, 150));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
