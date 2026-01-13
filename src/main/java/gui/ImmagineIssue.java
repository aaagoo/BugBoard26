package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;
import gui.util.RoundedPanel;

public class ImmagineIssue extends JFrame {
    private JPanel mainPanel;
    private JButton indietroButton;
    private JLabel immagineLabel;
    private JPanel botPanel;
    private JPanel imagePanel;

    public ImmagineIssue(String immagineUrl) {
        super();
        setContentPane(mainPanel);
        setTitle("Immagine Issue");
        setSize(1000, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        imagePanel.setBorder(new RoundedPanel("pannello"));
        botPanel.setBorder(new RoundedPanel("pannello"));

        caricaImmagine(immagineUrl);
        setVisible(true);

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void caricaImmagine(String immagineBase64) {
        try {
            if (immagineBase64 == null || immagineBase64.isEmpty()) {
                immagineLabel.setText("Nessuna immagine disponibile");
                return;
            }

            // Rimuovi il prefisso "data:image/...;base64," se presente
            String base64Data = immagineBase64;
            if (immagineBase64.contains(",")) {
                base64Data = immagineBase64.split(",")[1];
            }

            // Decodifica Base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            ImageIcon imageIcon = new ImageIcon(imageBytes);

            // Scala l'immagine mantenendo le proporzioni
            Image image = imageIcon.getImage();
            int originalWidth = image.getWidth(null);
            int originalHeight = image.getHeight(null);

            // Dimensioni massime
            int maxWidth = 900;
            int maxHeight = 550;

            // Calcola le nuove dimensioni mantenendo le proporzioni
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);

            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            immagineLabel.setIcon(new ImageIcon(scaledImage));
            immagineLabel.setText("");
        } catch (Exception e) {
            immagineLabel.setText("Impossibile caricare l'immagine: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
