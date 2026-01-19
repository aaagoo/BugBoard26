package main;

import backend.BackendApplication;
import gui.util.SplashScreen;
import gui.util.StyleManager;
import org.springframework.boot.builder.SpringApplicationBuilder;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        StyleManager.setupTheme();

        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.start();
            BackendApplication.splash = splash;
            System.out.println("Splash Screen avviata da Main.");
        });

        new SpringApplicationBuilder(BackendApplication.class)
                .headless(false)
                .run(args);
    }
}
