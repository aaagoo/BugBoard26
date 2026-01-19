package gui;

import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.Utility;
import java.util.Map;
import controller.Controller;
import modello.Account;
import modello.Ruolo;
import sessione.SessioneManager;

import javax.swing.*;
import java.net.URL;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class VIsualizzaIssue extends BaseFrame {

    public enum Provenienza {
        HOME,
        DASHBOARD
    }

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
    private Provenienza provenienza;

    public VIsualizzaIssue(Long issueId) {
        this(issueId, Provenienza.HOME);
    }

    public VIsualizzaIssue(Long issueId, Provenienza provenienza) {
        super();
        this.issueId = issueId;
        this.provenienza = provenienza;
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        caricaIssueAsincrono();

        botPanel.setBorder(new RoundedPanel("pannello"));
        midPanel.setBorder(new RoundedPanel("pannello"));
        imagePanel.setBorder(new RoundedPanel("finestra"));
        checkPanel.setBorder(new RoundedPanel("finestra"));
        infoPanel.setBorder(new RoundedPanel("finestra"));

        indietroButton.addActionListener(e -> {
            if (this.provenienza == Provenienza.DASHBOARD) {
                new Dashboard();
            } else {
                Account utente = SessioneManager.getInstance().getUtenteCorrente();
                if (utente != null && utente.getRuolo() == Ruolo.AMMINISTRATORE) {
                    new HomeAmm();
                } else {
                    new HomeUtente();
                }
            }
            dispose();
        });

        immagineButton.addActionListener(e -> {
            apriImmagineAsincrono();
        });

        salvaImmagineButton.addActionListener(e -> {
            salvaImmagineAsincrono();
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
                risolviIssueAsincrono();
            }
        });
    }

    private void apriImmagineAsincrono() {
        showLoading();
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                Map<String, Object> issue = Controller.getInstance().getIssueById(issueId);
                String immagineUrl = (String) issue.get("immagineurl");

                if (immagineUrl != null && !immagineUrl.isEmpty()) {
                    return Controller.getInstance().getProxyImageUrl(immagineUrl);
                }
                return null;
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    String proxyUrl = get();
                    if (proxyUrl != null) {
                        new ImmagineIssue(proxyUrl);
                    } else {
                        JOptionPane.showMessageDialog(VIsualizzaIssue.this, "Nessuna immagine disponibile per questa issue.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VIsualizzaIssue.this, "Errore nel caricamento dell'immagine: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void salvaImmagineAsincrono() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salva Immagine");
        fileChooser.setSelectedFile(new java.io.File("issue_" + issueId + ".png"));
        javax.swing.filechooser.FileNameExtensionFilter filter =
                new javax.swing.filechooser.FileNameExtensionFilter("Immagini (*.png, *.jpg)", "png", "jpg", "jpeg");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().contains(".")) {
            fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".png");
        }
        final java.io.File finalFile = fileToSave;

        showLoading();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Map<String, Object> issue = Controller.getInstance().getIssueById(issueId);
                String immagineUrl = (String) issue.get("immagineurl");

                if (immagineUrl == null || immagineUrl.isEmpty()) {
                    throw new Exception("Nessuna immagine disponibile.");
                }

                String downloadUrl = Controller.getInstance().getProxyImageUrl(immagineUrl);
                try (InputStream in = new URL(downloadUrl).openStream()) {
                    Files.copy(in, finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                return null;
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    get(); 
                    JOptionPane.showMessageDialog(VIsualizzaIssue.this, "Immagine salvata con successo in:\n" + finalFile.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VIsualizzaIssue.this, "Errore nel salvataggio: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void caricaIssueAsincrono() {
        showLoading();
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
                    aggiornaUI(issue);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(VIsualizzaIssue.this, "Errore caricamento issue: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void risolviIssueAsincrono() {
        showLoading();
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return Controller.getInstance().risolviIssue(issueId);
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    String risultato = get();
                    if (risultato.contains("successo")) {
                        JOptionPane.showMessageDialog(VIsualizzaIssue.this, risultato, "Successo", JOptionPane.INFORMATION_MESSAGE);
                        caricaIssueAsincrono(); 
                    } else {
                        JOptionPane.showMessageDialog(VIsualizzaIssue.this, risultato, "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(VIsualizzaIssue.this, "Errore risoluzione: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void aggiornaUI(Map<String, Object> issue) {
        idLabel.setText("ID: " + issue.get("id"));
        titoloField.setText((String) issue.get("titolo"));
        descrizioneArea.setText((String) issue.get("descrizione"));
        prioritaLabel.setText("Priorità: " + issue.get("priorita"));
        tipoLabel.setText("Tipo: " + issue.get("tipo"));
        risoltoLabel.setText("Risolto: " + ((Boolean) issue.get("risolto") ? "Sì" : "No"));
        creatoreLabel.setText("Creatore: " + issue.get("creatoreusername"));

        dataCreazioneLabel.setText("Data Creazione: " + Utility.formattaData(issue.get("datacreazione")));
        dataRisoluzioneLabel.setText("Data Risoluzione: " + Utility.formattaData(issue.get("datarisoluzione")));

        titoloField.setEditable(false);
        descrizioneArea.setEditable(false);

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

        Account utenteCorrente = SessioneManager.getInstance().getUtenteCorrente();
        if (utenteCorrente != null && assegnatario != null && 
            utenteCorrente.getNomeUtente().equals(assegnatario)) {
            modificaButton.setEnabled(true);
            modificaButton.setToolTipText("Segna come risolta");
        } else {
            modificaButton.setEnabled(false);
            modificaButton.setToolTipText("Solo l'assegnatario può modificare lo stato");
        }

        Boolean risolto = (Boolean) issue.get("risolto");
        if (Boolean.TRUE.equals(risolto)) {
            modificaButton.setEnabled(false);
            modificaButton.setText("Già Risolta");
        }
    }
}
