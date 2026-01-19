package remote.client;

import com.fasterxml.jackson.databind.JsonNode;
import modello.Account;
import modello.Ruolo;
import sessione.SessioneManager;

import java.util.Map;

public class AuthClient extends BaseApiClient {

    public AuthClient(String baseUrl) {
        super(baseUrl);
    }

    public Account login(String username, String password) {
        try {
            Map<String, String> body = Map.of("username", username, "password", password);
            String response = post("/api/auth/login", body);
            JsonNode root = mapper.readTree(response);

            String token = root.path("token").asText();
            JsonNode utenteNode = root.path("utente");

            Account utente = new Account(
                    utenteNode.path("nomeUtente").asText(),
                    utenteNode.path("password").asText(),
                    utenteNode.path("nome").asText(),
                    utenteNode.path("cognome").asText(),
                    utenteNode.path("email").asText(),
                    Ruolo.valueOf(utenteNode.path("ruolo").asText()),
                    utenteNode.path("avatar").asText(),
                    utenteNode.path("issueAssegnate").asInt()
            );

            SessioneManager.getInstance().setToken(token);
            System.out.println("[AuthClient] Login remoto OK, token salvato");
            return utente;
        } catch (Exception e) {
            System.out.println("[AuthClient] Login remoto fallito: " + e.getMessage());
            return null;
        }
    }
}
