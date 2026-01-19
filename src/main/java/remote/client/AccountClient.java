package remote.client;

import com.fasterxml.jackson.databind.JsonNode;
import modello.Account;
import modello.Ruolo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountClient extends BaseApiClient {

    public AccountClient(String baseUrl) {
        super(baseUrl);
    }

    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) {
        try {
            Map<String, Object> body = Map.of(
                    "nomeUtente", nomeUtente,
                    "password", password,
                    "nome", nome,
                    "cognome", cognome,
                    "email", email,
                    "ruolo", ruolo.name(),
                    "avatar", avatar
            );
            String response = post("/api/accounts", body);
            JsonNode root = mapper.readTree(response);
            return root.path("messaggio").asText("Account creato");
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public List<Map<String, Object>> getAllAccounts() {
        try {
            String response = get("/api/accounts");
            return mapper.readValue(response, List.class);
        } catch (Exception e) {
            System.out.println("[AccountClient] getAllAccounts fallito: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Account getUtente(String nomeUtente) {
        try {
            String response = get("/api/accounts/" + nomeUtente);
            JsonNode node = mapper.readTree(response);
            return new Account(
                    node.path("nomeUtente").asText(),
                    node.path("password").asText(),
                    node.path("nome").asText(),
                    node.path("cognome").asText(),
                    node.path("email").asText(),
                    Ruolo.valueOf(node.path("ruolo").asText()),
                    node.path("avatar").asText(),
                    node.path("issueAssegnate").asInt()
            );
        } catch (Exception e) {
            System.out.println("[AccountClient] getUtente fallito: " + e.getMessage());
            return null;
        }
    }

    public String modificaAccount(String nomeUtente, String password, String nome, String cognome, String email, String avatar) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("nomeUtente", nomeUtente);
            if (!password.isEmpty()) body.put("password", password);
            if (!nome.isEmpty()) body.put("nome", nome);
            if (!cognome.isEmpty()) body.put("cognome", cognome);
            if (!email.isEmpty()) body.put("email", email);
            if (!avatar.isEmpty()) body.put("avatar", avatar);

            String response = put("/api/accounts/" + nomeUtente, body);
            JsonNode root = mapper.readTree(response);
            return root.path("messaggio").asText("Account modificato");
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public String eliminaAccount(String nomeUtente) {
        try {
            String response = delete("/api/accounts/" + nomeUtente);
            JsonNode root = mapper.readTree(response);
            return root.path("messaggio").asText("Account eliminato");
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }
}
