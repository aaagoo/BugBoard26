package gui;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.Utility;
import modello.Account;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
    private File fileSelezionato = null;

    public NuovaIssue() {
        super();
        setContentPane(mainPanel);
        setTitle("Creazione Issue");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        infoutentePanel.setBorder(new RoundedPanel("pannello"));
        botPanel.setBorder(new RoundedPanel("pannello"));
        midPanel.setBorder(new RoundedPanel("pannello"));
        imagePanel.setBorder(new RoundedPanel("finestra"));
        checkPanel.setBorder(new RoundedPanel("finestra"));
        infoPanel.setBorder(new RoundedPanel("finestra"));

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
                    fileSelezionato = fileChooser.getSelectedFile();
                    JOptionPane.showMessageDialog(null, "Immagine selezionata: " + fileSelezionato.getName());
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

                // Avvia il caricamento
                showLoading();

                // Esegui l'operazione in background
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        String immagineUrl = null;
                        if (fileSelezionato != null) {
                            // Upload dell'immagine tramite Backend
                            immagineUrl = Controller.getInstance().uploadImmagine(fileSelezionato);
                            if (immagineUrl == null) {
                                throw new Exception("Errore durante l'upload dell'immagine.");
                            }
                        }

                        return Controller.getInstance().creaIssue(titolo, descrizione, priorita, tipo, null, immagineUrl);
                    }

                    @Override
                    protected void done() {
                        // Nascondi il caricamento
                        hideLoading();
                        try {
                            String messaggio = get(); // Ottieni il risultato o l'eccezione
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

}
