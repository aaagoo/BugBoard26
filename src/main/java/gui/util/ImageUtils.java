package gui.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static Image getIconaApplicazione() {
        try {
            java.net.URL url = ImageUtils.class.getResource("/images/icona_bugboard.png");
            if (url != null) {
                return new ImageIcon(url).getImage();
            }
        } catch (Exception e) {
            System.err.println("Impossibile caricare l'icona dell'applicazione: " + e.getMessage());
        }
        return null;
    }

    public static Icon getSVGIcon(String name, int width, int height) {
        try {
            FlatSVGIcon icon = new FlatSVGIcon("icons/" + name);
            return icon.derive(width, height);
        } catch (Exception e) {
            System.err.println("Errore caricamento SVG: " + name + " - " + e.getMessage());
            return null;
        }
    }

    public static void caricaAvatar(JLabel label, String avatarName, int width, int height) {
        if (avatarName == null || avatarName.isEmpty()) {
            avatarName = "user.png";
        }

        String resourcePath = "/images/profileIcons/" + avatarName;
        var url = ImageUtils.class.getResource(resourcePath);

        if (url == null) {
            label.setIcon(null);
            label.setText("No Img");
            return;
        }

        try {
            ImageIcon imageIcon = new ImageIcon(url);
            Image image = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(image));
            label.setText("");
        } catch (Exception e) {
            System.err.println("❌ Errore caricamento avatar: " + e.getMessage());
            e.printStackTrace();
            label.setIcon(null);
            label.setText("Error");
        }
    }

    public static void caricaImmagine(JLabel label, String resourcePath, int width, int height) {
        try {
            ImageIcon imageIcon = new ImageIcon(ImageUtils.class.getClassLoader().getResource(resourcePath));
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getAvatarFiles() {
        List<String> avatarNames = new ArrayList<>();
        String resourcePath = "/images/profileIcons/avatars.list";
        
        try (InputStream is = ImageUtils.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    avatarNames.add(line.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Errore lettura avatars.list: " + e.getMessage());
            return new String[]{"user.png"}; 
        }
        
        return avatarNames.toArray(new String[0]);
    }

    public static String scegliAvatar(JFrame parent, String avatarCorrente) {
        String[] avatarFiles = getAvatarFiles();

        if (avatarFiles.length == 0) {
            JOptionPane.showMessageDialog(parent, "Nessun avatar trovato");
            return null;
        }

        return (String) JOptionPane.showInputDialog(
                parent,
                "Seleziona un avatar:",
                "Scegli Avatar",
                JOptionPane.QUESTION_MESSAGE,
                null,
                avatarFiles,
                avatarCorrente
        );
    }
}
