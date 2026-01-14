package gui.util;

import javax.swing.*;

public abstract class BaseFrame extends JFrame {

    private LoadingGlassPane loadingPane;

    public BaseFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Inizializza il GlassPane per il caricamento
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
            System.exit(0);
        }
    }
}
