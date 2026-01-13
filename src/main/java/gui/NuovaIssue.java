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
    private String immagineBase64 = null;

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
                        "Immagini", "jpg", "jpeg", "png", "gif"));

                int result = fileChooser.showOpenDialog(NuovaIssue.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = fileChooser.getSelectedFile();
                        byte[] fileContent = java.nio.file.Files.readAllBytes(file.toPath());
                        immagineBase64 = java.util.Base64.getEncoder().encodeToString(fileContent);
                        JOptionPane.showMessageDialog(null, "Immagine allegata: " + file.getName());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Errore nell'allegare l'immagine: " + ex.getMessage());
                    }
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

                if (tipo == null) {
                    JOptionPane.showMessageDialog(null, "Seleziona un tipo");
                    return;
                }

                if (priorita == null) {
                    JOptionPane.showMessageDialog(null, "Seleziona una priorità");
                    return;
                }

                String messaggio = Controller.getInstance().creaIssue(titolo, descrizione, priorita, tipo, null, immagineBase64);
                JOptionPane.showMessageDialog(null, messaggio);

                if (messaggio.contains("successo")) {
                    new HomeUtente();
                    dispose();
                }
            }
        });

    }

}
