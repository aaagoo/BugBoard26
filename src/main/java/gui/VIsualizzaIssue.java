package gui;

import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.Utility;
import java.util.Map;
import controller.Controller;

import javax.swing.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private Long issueId;
    
    // URL del backend (dovrebbe essere preso da una configurazione centralizzata, ma per ora lo metto qui come nel Controller)
    private static final String BACKEND_URL = "http://localhost:8080";

    public VIsualizzaIssue(Long issueId) {
        super();
        this.issueId = issueId;
        setContentPane(mainPanel);
        setTitle("Visualizza Issue");
        setSize(1200, 800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
                    // Se è un URL http, passa attraverso il proxy del backend
                    if (immagineUrl.startsWith("http")) {
                        String proxyUrl = BACKEND_URL + "/api/issues/proxy-immagine?url=" + 
                                          URLEncoder.encode(immagineUrl, StandardCharsets.UTF_8);
                        new ImmagineIssue(proxyUrl);
                    } else {
                        // Vecchio formato Base64 (se presente)
                        new ImmagineIssue(immagineUrl); // ImmagineIssue dovrà gestire il fallimento se non è un URL valido
                    }
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

                    // Scarica e salva l'immagine
                    String downloadUrl = immagineUrl;
                    if (immagineUrl.startsWith("http")) {
                        // Usa il proxy anche per il download
                        downloadUrl = BACKEND_URL + "/api/issues/proxy-immagine?url=" + 
                                      URLEncoder.encode(immagineUrl, StandardCharsets.UTF_8);
                        
                        try (InputStream in = new URL(downloadUrl).openStream()) {
                            Files.copy(in, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } else {
                        // Gestione Base64 legacy
                        String base64Data = immagineUrl;
                        if (immagineUrl.contains(",")) {
                            base64Data = immagineUrl.split(",")[1];
                        }
                        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
                        Files.write(fileToSave.toPath(), imageBytes);
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
    }
}
