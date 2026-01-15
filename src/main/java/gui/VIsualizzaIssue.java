package gui;

import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.Utility;
import java.util.Map;
import controller.Controller;

import javax.swing.*;
import java.net.URL;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class VIsualizzaIssue extends BaseFrame {

    private JPanel midPanel;
    private JPanel infoPanel;
    private JTextArea descrizioneArea;
    private JTextField titoloField;
    private JPanel info2Panel;
    private JPanel checkPanel;
    private JPanel imagePanel;
    private JButton immagineButton;
    private JPanel mainPanel;
    private JPanel botPanel;
    private JButton indietroButton;
    private JButton modificaButton;
    private JLabel prioritaLabel;
    private JLabel tipoLabel;
    private JLabel risoltoLabel;
    private JLabel creatoreLabel;
    private JLabel dataCreazioneLabel;
    private JLabel dataRisoluzioneLabel;
    private JPanel topPanel;
    private JLabel idLabel;
    private JButton salvaImmagineButton;
    private JLabel statoImmagineLabel;
    private JLabel assegnataALabel;
    private Long issueId;

    public VIsualizzaIssue(Long issueId) {
        super();
        this.issueId = issueId;
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        caricaIssue();

        botPanel.setBorder(new RoundedPanel("pannello"));
        midPanel.setBorder(new RoundedPanel("pannello"));
        imagePanel.setBorder(new RoundedPanel("finestra"));
        checkPanel.setBorder(new RoundedPanel("finestra"));
        infoPanel.setBorder(new RoundedPanel("finestra"));

        indietroButton.addActionListener(e -> {
            new HomeUtente();
            dispose();
        });

        immagineButton.addActionListener(e -> {
            try {
                Map<String, Object> issue = Controller.getInstance().getIssueById(issueId);
                String immagineUrl = (String) issue.get("immagineurl");

                if (immagineUrl != null && !immagineUrl.isEmpty()) {
                    String proxyUrl = Controller.getInstance().getProxyImageUrl(immagineUrl);
                    new ImmagineIssue(proxyUrl);
                } else {
                    JOptionPane.showMessageDialog(this, "Nessuna immagine disponibile per questa issue.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore nel caricamento dell'immagine: " + ex.getMessage());
            }
        });

        salvaImmagineButton.addActionListener(e -> {
            try {
                Map<String, Object> issue = Controller.getInstance().getIssueById(issueId);
                String immagineUrl = (String) issue.get("immagineurl");

                if (immagineUrl == null || immagineUrl.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nessuna immagine disponibile per questa issue.");
                    return;
                }

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Salva Immagine");
                fileChooser.setSelectedFile(new java.io.File("issue_" + issueId + ".png"));

                javax.swing.filechooser.FileNameExtensionFilter filter =
                        new javax.swing.filechooser.FileNameExtensionFilter("Immagini (*.png, *.jpg)", "png", "jpg", "jpeg");
                fileChooser.setFileFilter(filter);

                int userSelection = fileChooser.showSaveDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    java.io.File fileToSave = fileChooser.getSelectedFile();

                    if (!fileToSave.getName().contains(".")) {
                        fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".png");
                    }

                    String downloadUrl = Controller.getInstance().getProxyImageUrl(immagineUrl);
                    
                    try (InputStream in = new URL(downloadUrl).openStream()) {
                        Files.copy(in, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }

                    JOptionPane.showMessageDialog(this, "Immagine salvata con successo in:\n" + fileToSave.getAbsolutePath());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore nel salvataggio dell'immagine: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        modificaButton.addActionListener(e -> {
            int conferma = JOptionPane.showConfirmDialog(
                    this,
                    "Vuoi contrassegnare questa issue come risolta?",
                    "Conferma risoluzione",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (conferma == JOptionPane.YES_OPTION) {
                String risultato = Controller.getInstance().risolviIssue(issueId);

                if (risultato.contains("successo")) {
                    JOptionPane.showMessageDialog(
                            this,
                            risultato,
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    caricaIssue();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            risultato,
                            "Errore",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    private void caricaIssue() {
        Utility.caricaDatiIssueById(issueId, idLabel, titoloField, descrizioneArea,
                prioritaLabel, tipoLabel, risoltoLabel, creatoreLabel,
                dataCreazioneLabel, dataRisoluzioneLabel, this);

        try {
            Map<String, Object> issue = Controller.getInstance().getIssueById(issueId);

            String immagineUrl = (String) issue.get("immagineurl");
            if (immagineUrl != null && !immagineUrl.isEmpty()) {
                statoImmagineLabel.setText("Stato: Allegata");
                immagineButton.setEnabled(true);
                salvaImmagineButton.setEnabled(true);
            } else {
                statoImmagineLabel.setText("Stato: Non Allegata");
                immagineButton.setEnabled(false);
                salvaImmagineButton.setEnabled(false);
            }

            String assegnatario = (String) issue.get("assegnatariousername");
            if (assegnatario != null && !assegnatario.isEmpty()) {
                assegnataALabel.setText("Assegnata a: " + assegnatario);
            } else {
                assegnataALabel.setText("Assegnata a: Nessuno");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
