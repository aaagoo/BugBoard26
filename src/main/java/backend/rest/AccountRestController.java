package backend.rest;

import backend.service.AccountService;
import modello.Ruolo;
import modello.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountRestController {
    
    private static final String KEY_MESSAGGIO = "messaggio";
    
    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Object> creaAccount(@RequestBody Map<String, String> body) {
        String messaggio = accountService.creaAccount(
                body.get("nomeUtente"),
                body.get("password"),
                body.get("nome"),
                body.get("cognome"),
                body.get("email"),
                Ruolo.valueOf(body.get("ruolo")),
                body.get("avatar")
        );
        return ResponseEntity.ok(Map.of(KEY_MESSAGGIO, messaggio));
    }

    @GetMapping
    public ResponseEntity<Object> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{username}")
    public ResponseEntity<Object> getUtente(@PathVariable String username) {
        Account utente = accountService.getUtente(username);
        return utente != null ? ResponseEntity.ok(utente) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{username}")
    public ResponseEntity<Object> modificaAccount(@PathVariable String username, @RequestBody Map<String, String> body) {
        String messaggio = accountService.modificaAccount(
                username,
                body.getOrDefault("password", ""),
                body.getOrDefault("nome", ""),
                body.getOrDefault("cognome", ""),
                body.getOrDefault("email", ""),
                body.getOrDefault("avatar", "")
        );
        return ResponseEntity.ok(Map.of(KEY_MESSAGGIO, messaggio));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Object> eliminaAccount(@PathVariable String username) {
        String messaggio = accountService.eliminaAccount(username);
        return ResponseEntity.ok(Map.of(KEY_MESSAGGIO, messaggio));
    }
}
