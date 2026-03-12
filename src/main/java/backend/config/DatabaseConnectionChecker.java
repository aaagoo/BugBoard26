package backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionChecker implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Verifica connessione database in corso...");
        try {
            jdbcTemplate.execute("SELECT 1");
            System.out.println("Connessione database OK.");
        } catch (Exception e) {
            System.err.println("Connessione database FALLITA: " + e.getMessage());
            // Rilanciamo l'eccezione per bloccare l'avvio e permettere al Main di gestirlo
            throw new RuntimeException("Impossibile connettersi al database. Verifica la connessione internet.", e);
        }
    }
}
