package main;

import backend.BackendApplication;
import gui.util.SplashScreen;
import gui.util.StyleManager;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        StyleManager.setupTheme();

        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.start();
            BackendApplication.splash = splash;
            System.out.println("Splash Screen avviata da Main.");
        });

        try {
            SpringApplicationBuilder builder = new SpringApplicationBuilder(BackendApplication.class);
            builder.headless(false);
            context = builder.run(args);
        } catch (Exception e) {
            System.err.println("Errore critico all'avvio: " + e.getMessage());
            
            if (BackendApplication.splash != null) {
                BackendApplication.splash.stop();
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Impossibile avviare l'applicazione.\n\n" +
                        "Verifica la tua connessione internet.\n" +
                        "Errore: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()),
                        "Errore di Connessione",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }
    }
}
