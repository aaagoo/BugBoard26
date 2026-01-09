package backend;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import gui.Login;
import javax.swing.SwingUtilities;

@SpringBootApplication
@ComponentScan(basePackages = {"backend", "modello"})
@EntityScan(basePackages = "backend.entity")
public class BackendApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BackendApplication.class)
                .headless(false)
                .run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void avviaFrontend() {
        System.out.println("Backend avviato. Apertura interfaccia grafica...");

        SwingUtilities.invokeLater(() -> {
            new Login();
        });
    }
}
