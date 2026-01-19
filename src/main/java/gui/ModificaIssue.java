package gui;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
import gui.util.Utility;
import modello.Account;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

public class ModificaIssue extends BaseFrame {

    public enum Provenienza {
        HOME,
        DASHBOARD
    }

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel infoutentePanel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JPanel botPanel;
    private JButton confermaButton;
    private JButton annullaButton;
    private JPanel midPanel;
    private JCheckBox criticalCheckBox;
    private JCheckBox lowCheckBox;
    private JCheckBox highCheckBox;
    private JCheckBox mediumCheckBox;
    private JTextField titoloField;
    private JPanel info2Panel;
    private JPanel infoPanel;
    private JPanel checkPanel;
    private JPanel imagePanel;
    private JLabel ruoloLabel;
    private JButton allegaButton;
    private JTextArea descrizioneArea;
    private JCheckBox questionCheckBox;
    private JCheckBox bugCheckBox;
    private JCheckBox documentationCheckBox;
    private JCheckBox featureCheckBox;
    private JPanel assegnaPanel;
    private JCheckBox automaticoCheckBox;
    private JCheckBox manualeCheckBox;
    private JComboBox<String> assigneeComboBox;
    private JButton rimuoviAllegatoButton;
    private JLabel statoImmagineLabel;
    private JButton visualizzaButton;
    private JScrollPane descrizioneScrollPane;
    private File fileSelezionato = null;
    private String immagineUrlCorrente = null;
    private Long issueId;
    private Provenienza provenienza;

    public ModificaIssue(Long issueId, Provenienza provenienza) {
        super();
        this.issueId = issueId;
        this.provenienza = provenienza;
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        infoutentePanel.setBorder(new RoundedPanel("pannello"));
        botPanel.setBorder(new RoundedPanel("pannello"));
        midPanel.setBorder(new RoundedPanel("pannello"));
        imagePanel.setBorder(new RoundedPanel("finestra"));
        checkPanel.setBorder(new RoundedPanel("finestra"));
        infoPanel.setBorder(new RoundedPanel("finestra"));
        assegnaPanel.setBorder(new RoundedPanel("finestra"));

        descrizioneArea.setBorder(null);
        descrizioneScrollPane.putClientProperty("FlatLaf.style", "arc: 15");

        Account utente = Controller.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
        }

        ButtonGroup prioritaGroup = new ButtonGroup();
        prioritaGroup.add(lowCheckBox);
        prioritaGroup.add(mediumCheckBox);
        prioritaGroup.add(highCheckBox);
        prioritaGroup.add(criticalCheckBox);

        ButtonGroup tipoGroup = new ButtonGroup();
        tipoGroup.add(questionCheckBox);
        tipoGroup.add(bugCheckBox);
        tipoGroup.add(documentationCheckBox);
        tipoGroup.add(featureCheckBox);

        ButtonGroup assegnazioneGroup = new ButtonGroup();
        assegnazioneGroup.add(automaticoCheckBox);
        assegnazioneGroup.add(manualeCheckBox);
        
        manualeCheckBox.setSelected(true);
        assigneeComboBox.setEnabled(true);

        popolaComboBoxUtentiAndCaricaDati();

        ActionListener assegnazioneListener = e -> {
            assigneeComboBox.setEnabled(manualeCheckBox.isSelected());
        };
        automaticoCheckBox.addActionListener(assegnazioneListener);
        manualeCheckBox.addActionListener(assegnazioneListener);

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tornaIndietro();
            }
        });

        allegaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Immagini (JPG, PNG)", "jpg", "jpeg", "png"));

                int result = fileChooser.showOpenDialog(ModificaIssue.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    fileSelezionato = fileChooser.getSelectedFile();
                    aggiornaStatoImmagine();
                    JOptionPane.showMessageDialog(null, "Immagine selezionata: " + fileSelezionato.getName());
                }
            }
        });

        rimuoviAllegatoButton.addActionListener(e -> {
            fileSelezionato = null;
            immagineUrlCorrente = null;
            aggiornaStatoImmagine();
            JOptionPane.showMessageDialog(null, "Allegato rimosso.");
        });

        visualizzaButton.addActionListener(e -> {
            try {
                if (fileSelezionato != null) {
                    String localUrl = fileSelezionato.toURI().toURL().toString();
                    new ImmagineIssue(localUrl);
                } else if (immagineUrlCorrente != null && !immagineUrlCorrente.isEmpty()) {
                    String proxyUrl = Controller.getInstance().getProxyImageUrl(immagineUrlCorrente);
                    new ImmagineIssue(proxyUrl);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore visualizzazione: " + ex.getMessage());
            }
        });

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvaModifiche();
            }
        });
    }

    private void tornaIndietro() {
        if (provenienza == Provenienza.DASHBOARD) {
            new Dashboard();
        } else {
            Account utente = Controller.getInstance().getUtenteCorrente();
            Utility.redirectByRole(utente);
        }
        dispose();
    }

    private void popolaComboBoxUtentiAndCaricaDati() {
        showLoading();
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return Controller.getInstance().getUtenti();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> utenti = get();
                    assigneeComboBox.removeAllItems();
                    for (Map<String, Object> utente : utenti) {
                        assigneeComboBox.addItem((String) utente.get("nomeutente"));
                    }

                    caricaDatiIssue();
                    
                } catch (Exception e) {
                    hideLoading();
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ModificaIssue.this, "Errore caricamento utenti: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void caricaDatiIssue() {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                return Controller.getInstance().getIssueById(issueId);
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    Map<String, Object> issue = get();
                    titoloField.setText((String) issue.get("titolo"));
                    descrizioneArea.setText((String) issue.get("descrizione"));
                    
                    String priorita = (String) issue.get("priorita");
                    if ("LOW".equals(priorita)) lowCheckBox.setSelected(true);
                    else if ("MEDIUM".equals(priorita)) mediumCheckBox.setSelected(true);
                    else if ("HIGH".equals(priorita)) highCheckBox.setSelected(true);
                    else if ("CRITICAL".equals(priorita)) criticalCheckBox.setSelected(true);

                    String tipo = (String) issue.get("tipo");
                    if ("QUESTION".equals(tipo)) questionCheckBox.setSelected(true);
                    else if ("BUG".equals(tipo)) bugCheckBox.setSelected(true);
                    else if ("DOCUMENTATION".equals(tipo)) documentationCheckBox.setSelected(true);
                    else if ("FEATURE".equals(tipo)) featureCheckBox.setSelected(true);

                    immagineUrlCorrente = (String) issue.get("immagineurl");
                    aggiornaStatoImmagine();

                    String assegnatario = (String) issue.get("assegnatariousername");
                    if (assegnatario != null) {
                        manualeCheckBox.setSelected(true);
                        assigneeComboBox.setEnabled(true);
                        assigneeComboBox.setSelectedItem(assegnatario);
                    } else {
                        automaticoCheckBox.setSelected(true);
                        assigneeComboBox.setEnabled(false);
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ModificaIssue.this, "Errore caricamento dati issue: " + e.getMessage());
                    tornaIndietro();
                }
            }
        };
        worker.execute();
    }

    private void salvaModifiche() {
        String titolo = titoloField.getText().trim();
        String descrizione = descrizioneArea.getText().trim();

        if (titolo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inserisci un titolo");
            return;
        }

        String priorita = null;
        if (lowCheckBox.isSelected()) priorita = "LOW";
        else if (mediumCheckBox.isSelected()) priorita = "MEDIUM";
        else if (highCheckBox.isSelected()) priorita = "HIGH";
        else if (criticalCheckBox.isSelected()) priorita = "CRITICAL";

        String tipo = null;
        if (questionCheckBox.isSelected()) tipo = "QUESTION";
        else if (bugCheckBox.isSelected()) tipo = "BUG";
        else if (documentationCheckBox.isSelected()) tipo = "DOCUMENTATION";
        else if (featureCheckBox.isSelected()) tipo = "FEATURE";

        if (tipo == null || priorita == null) {
            JOptionPane.showMessageDialog(null, "Seleziona tipo e priorità");
            return;
        }

        String assegnatario = null;
        if (manualeCheckBox.isSelected()) {
            assegnatario = (String) assigneeComboBox.getSelectedItem();
        }
        
        final String assegnatarioFinale = assegnatario;
        final String prioritaFinale = priorita;
        final String tipoFinale = tipo;

        showLoading();

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String immagineUrl = immagineUrlCorrente;
                if (fileSelezionato != null) {
                    immagineUrl = Controller.getInstance().uploadImmagine(fileSelezionato);
                    if (immagineUrl == null) throw new Exception("Errore upload immagine.");
                }

                return Controller.getInstance().modificaIssue(issueId, titolo, descrizione, prioritaFinale, tipoFinale, assegnatarioFinale, immagineUrl);
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    String messaggio = get();
                    JOptionPane.showMessageDialog(ModificaIssue.this, messaggio);
                    if (messaggio.contains("successo")) {
                        tornaIndietro();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ModificaIssue.this, "Errore: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void aggiornaStatoImmagine() {
        boolean haImmagine = (fileSelezionato != null) || (immagineUrlCorrente != null && !immagineUrlCorrente.isEmpty());
        
        if (haImmagine) {
            statoImmagineLabel.setText("Stato: Allegata");
            allegaButton.setEnabled(false);
            if (rimuoviAllegatoButton != null) rimuoviAllegatoButton.setEnabled(true);
            if (visualizzaButton != null) visualizzaButton.setEnabled(true);
        } else {
            statoImmagineLabel.setText("Stato: Non Allegata");
            allegaButton.setEnabled(true);
            if (rimuoviAllegatoButton != null) rimuoviAllegatoButton.setEnabled(false);
            if (visualizzaButton != null) visualizzaButton.setEnabled(false);
        }
    }
}
