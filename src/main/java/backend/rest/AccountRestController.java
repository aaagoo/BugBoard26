package backend.rest;

import backend.service.AccountService;
import modello.Ruolo;
import modello.Utente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountRestController {
    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> creaAccount(@RequestBody Map<String, String> body) {
        try {
            String messaggio = accountService.creaAccount(
                    body.get("nomeUtente"),
                    body.get("password"),
                    body.get("nome"),
                    body.get("cognome"),
                    body.get("email"),
                    Ruolo.valueOf(body.get("ruolo")),
                    body.getOrDefault("avatar", "user.png")
            );
            return ResponseEntity.ok(Map.of("messaggio", messaggio));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(Map.of("messaggio", "Errore: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        try {
            return ResponseEntity.ok(accountService.getAllAccounts());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(Map.of("messaggio", "Errore: " + e.getMessage()));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUtente(@PathVariable String username) {
        try {
            Utente utente = accountService.getUtente(username);
            return utente != null ? ResponseEntity.ok(utente) : ResponseEntity.notFound().build();
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(Map.of("messaggio", "Errore: " + e.getMessage()));
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> modificaAccount(@PathVariable String username, @RequestBody Map<String, String> body) {
        try {
            String messaggio = accountService.modificaAccount(
                    username,
                    body.getOrDefault("password", ""),
                    body.getOrDefault("nome", ""),
                    body.getOrDefault("cognome", ""),
                    body.getOrDefault("email", ""),
                    body.getOrDefault("avatar", "")
            );
            return ResponseEntity.ok(Map.of("messaggio", messaggio));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(Map.of("messaggio", "Errore: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> eliminaAccount(@PathVariable String username) {
        try {
            String messaggio = accountService.eliminaAccount(username);
            return ResponseEntity.ok(Map.of("messaggio", messaggio));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(Map.of("messaggio", "Errore: " + e.getMessage()));
        }
    }
}
