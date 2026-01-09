package remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import modello.Ruolo;
import modello.Utente;
import sessione.SessioneManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiClient {
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Utente login(String username, String password) {
        try {
            Map<String, String> body = Map.of("username", username, "password", password);
            String response = post("/api/auth/login", body);
            JsonNode root = mapper.readTree(response);

            String token = root.path("token").asText();
            JsonNode utenteNode = root.path("utente");

            Utente utente = new Utente(
                    utenteNode.path("nomeUtente").asText(),
                    utenteNode.path("password").asText(),
                    utenteNode.path("nome").asText(),
                    utenteNode.path("cognome").asText(),
                    utenteNode.path("email").asText(),
                    Ruolo.valueOf(utenteNode.path("ruolo").asText()),
                    utenteNode.path("avatar").asText()
            );

            SessioneManager.getInstance().setToken(token);
            System.out.println("[ApiClient] Login remoto OK, token salvato");
            return utente;
        } catch (Exception e) {
            System.out.println("[ApiClient] Login remoto fallito: " + e.getMessage());
            return null;
        }
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
            System.out.println("[ApiClient] getAllAccounts fallito: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Utente getUtente(String nomeUtente) {
        try {
            String response = get("/api/accounts/" + nomeUtente);
            JsonNode node = mapper.readTree(response);
            Utente utente = new Utente(
                    node.path("nomeUtente").asText(),
                    node.path("password").asText(),
                    node.path("nome").asText(),
                    node.path("cognome").asText(),
                    node.path("email").asText(),
                    Ruolo.valueOf(node.path("ruolo").asText()),
                    node.path("avatar").asText()
            );
            return utente;
        } catch (Exception e) {
            System.out.println("[ApiClient] getUtente fallito: " + e.getMessage());
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

    private String post(String path, Object body) throws IOException {
        return request("POST", path, body);
    }

    private String put(String path, Object body) throws IOException {
        return request("PUT", path, body);
    }

    private String get(String path) throws IOException {
        return request("GET", path, null);
    }

    private String delete(String path) throws IOException {
        return request("DELETE", path, null);
    }

    private String request(String method, String path, Object body) throws IOException {
        URL url = new URL(baseUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        String token = SessioneManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }

        if (body != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(mapper.writeValueAsBytes(body));
            }
        }

        int code = conn.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        StringBuilder sb = new StringBuilder();
        if (is != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
        }
        conn.disconnect();

        if (code < 200 || code >= 300) {
            throw new IOException("HTTP " + code + ": " + sb);
        }
        return sb.toString();
    }
}

