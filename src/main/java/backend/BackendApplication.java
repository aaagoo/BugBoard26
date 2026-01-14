package backend;

import gui.util.SplashScreen;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import gui.Login;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

@SpringBootApplication
@ComponentScan(basePackages = {"backend", "modello"})
public class BackendApplication {

    public static volatile SplashScreen splash;

    @EventListener(ApplicationReadyEvent.class)
    public void avviaInterfaccia() {
        System.out.println("Spring Boot pronto. Chiudo Splash e apro Login.");
        
        SwingUtilities.invokeLater(() -> {
            if (splash != null) {
                splash.stop();
                splash = null; 
                System.out.println("Splash Screen chiusa.");
            }

            Timer delayTimer = new Timer(500, e -> {
                Login login = new Login();
                login.toFront();
                login.requestFocus();
                ((Timer)e.getSource()).stop();
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        });
    }
}
