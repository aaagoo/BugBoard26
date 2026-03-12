package gui.util;

import main.Main;
import javax.swing.*;
import java.awt.Image;

public abstract class BaseFrame extends JFrame {

    private LoadingGlassPane loadingPane;

    public BaseFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        Image icon = Utility.getIconaApplicazione();
        if (icon != null) {
            setIconImage(icon);
        }

        loadingPane = new LoadingGlassPane();
        setGlassPane(loadingPane);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                chiudiApplicazione();
            }
        });
    }

    public void showLoading() {
        SwingUtilities.invokeLater(() -> loadingPane.start());
    }

    public void hideLoading() {
        SwingUtilities.invokeLater(() -> loadingPane.stop());
    }

    private void chiudiApplicazione() {
        int conferma = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler chiudere l'applicazione?",
                "Conferma chiusura",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (conferma == JOptionPane.YES_OPTION) {
            System.out.println("Chiusura applicazione completa...");
            
            // Chiudiamo Spring Boot
            if (Main.context != null) {
                Main.context.close();
            }

            System.exit(0);
        }
    }
}
