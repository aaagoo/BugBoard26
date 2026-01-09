package backend.rest;

import backend.service.AccountService;
import modello.Utente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthRestController {
    private final AccountService accountService;

    public AuthRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Utente utente = accountService.login(username, password);

        if (utente != null) {
            String token = accountService.generateToken(utente);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("utente", utente);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Map.of("messaggio", "Credenziali non valide"));
    }
}
