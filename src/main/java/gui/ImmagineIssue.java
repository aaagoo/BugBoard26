package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.imageio.ImageIO;
import gui.util.RoundedPanel;
import gui.util.Utility;

public class ImmagineIssue extends JDialog {
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

        Image icon = Utility.getIconaApplicazione();
        if (icon != null) {
            setIconImage(icon);
        }

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

    private void caricaImmagine(String immagineUrl) {
        try {
            if (immagineUrl == null || immagineUrl.isEmpty()) {
                immagineLabel.setText("Nessuna immagine disponibile");
                return;
            }

            URL url = new URL(immagineUrl);
            Image image = ImageIO.read(url);

            if (image == null) {
                immagineLabel.setText("Impossibile leggere l'immagine dall'URL");
                return;
            }

            int originalWidth = image.getWidth(null);
            int originalHeight = image.getHeight(null);

            int maxWidth = 900;
            int maxHeight = 550;

            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            if (ratio > 1) {
                ratio = 1;
            }

            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);

            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            immagineLabel.setIcon(new ImageIcon(scaledImage));
            immagineLabel.setText("");
        } catch (Exception e) {
            immagineLabel.setText("Errore caricamento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
