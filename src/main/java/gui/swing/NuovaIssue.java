package gui.swing;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.Utility;
import modello.Account;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NuovaIssue extends BaseFrame {


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
    
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB

    public NuovaIssue() {
        super();
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
        automaticoCheckBox.setSelected(true);
        assigneeComboBox.setEnabled(false);

        popolaComboBoxUtenti();

        ActionListener assegnazioneListener = e -> {
            assigneeComboBox.setEnabled(manualeCheckBox.isSelected());
        };
        automaticoCheckBox.addActionListener(assegnazioneListener);
        manualeCheckBox.addActionListener(assegnazioneListener);

        aggiornaStatoImmagine();

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUtente();
                dispose();
            }
        });

        allegaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Immagini (JPG, PNG)", "jpg", "jpeg", "png"));

                int result = fileChooser.showOpenDialog(NuovaIssue.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    
                    if (file.length() > MAX_FILE_SIZE) {
                        JOptionPane.showMessageDialog(NuovaIssue.this, 
                                "Il file è troppo grande! Dimensione massima: 2 MB.", 
                                "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    fileSelezionato = file;
                    aggiornaStatoImmagine();
                    JOptionPane.showMessageDialog(null, "Immagine selezionata: " + fileSelezionato.getName());
                }
            }
        });

        rimuoviAllegatoButton.addActionListener(e -> {
            fileSelezionato = null;
            aggiornaStatoImmagine();
            JOptionPane.showMessageDialog(null, "Allegato rimosso.");
        });

        visualizzaButton.addActionListener(e -> {
            if (fileSelezionato != null) {
                try {
                    String localUrl = fileSelezionato.toURI().toURL().toString();
                    new ImmagineIssue(localUrl);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore visualizzazione: " + ex.getMessage());
                }
            }
        });

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titolo = titoloField.getText().trim();
                String descrizione = descrizioneArea.getText().trim();

                if (titolo.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Inserisci un titolo");
                    return;
                }

                String prioritaTemp = null;
                if (lowCheckBox.isSelected()) prioritaTemp = "LOW";
                else if (mediumCheckBox.isSelected()) prioritaTemp = "MEDIUM";
                else if (highCheckBox.isSelected()) prioritaTemp = "HIGH";
                else if (criticalCheckBox.isSelected()) prioritaTemp = "CRITICAL";
                final String priorita = prioritaTemp;

                String tipoTemp = null;
                if (questionCheckBox.isSelected()) tipoTemp = "QUESTION";
                else if (bugCheckBox.isSelected()) tipoTemp = "BUG";
                else if (documentationCheckBox.isSelected()) tipoTemp = "DOCUMENTATION";
                else if (featureCheckBox.isSelected()) tipoTemp = "FEATURE";
                final String tipo = tipoTemp;

                if (tipo == null) {
                    JOptionPane.showMessageDialog(null, "Seleziona un tipo");
                    return;
                }

                if (priorita == null) {
                    JOptionPane.showMessageDialog(null, "Seleziona una priorità");
                    return;
                }

                String assegnatarioTemp = null;
                if (manualeCheckBox.isSelected()) {
                    assegnatarioTemp = (String) assigneeComboBox.getSelectedItem();
                    if (assegnatarioTemp == null || assegnatarioTemp.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Seleziona un utente per l'assegnazione manuale");
                        return;
                    }
                }
                final String assegnatario = assegnatarioTemp;

                showLoading();

                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        String immagineUrl = null;
                        if (fileSelezionato != null) {
                            immagineUrl = Controller.getInstance().uploadImmagine(fileSelezionato);
                            if (immagineUrl == null) {
                                throw new Exception("Errore durante l'upload dell'immagine.");
                            }
                        }

                        return Controller.getInstance().creaIssue(titolo, descrizione, priorita, tipo, assegnatario, immagineUrl);
                    }

                    @Override
                    protected void done() {
                        hideLoading();
                        try {
                            String messaggio = get();
                            JOptionPane.showMessageDialog(NuovaIssue.this, messaggio);

                            if (messaggio.contains("successo")) {
                                new HomeUtente();
                                dispose();
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            JOptionPane.showMessageDialog(NuovaIssue.this, "Errore: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();
            }
        });
    }

    private void aggiornaStatoImmagine() {
        if (fileSelezionato != null) {
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

    private void popolaComboBoxUtenti() {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
