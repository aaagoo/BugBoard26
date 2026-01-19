package remote.client;

import com.fasterxml.jackson.databind.JsonNode;
import sessione.SessioneManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueClient extends BaseApiClient {

    public IssueClient(String baseUrl) {
        super(baseUrl);
    }

    public String creaIssue(String titolo, String descrizione, String priorita, String tipo, String creatoreUsername, String assegnatarioUsername, String immagineUrl) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("titolo", titolo);
            body.put("descrizione", descrizione);
            body.put("priorita", priorita);
            body.put("tipo", tipo);
            body.put("creatoreUsername", creatoreUsername);
            body.put("assegnatarioUsername", assegnatarioUsername);
            if (immagineUrl != null && !immagineUrl.isEmpty()) {
                body.put("immagineUrl", immagineUrl);
            }

            String response = post("/api/issues", body);
            JsonNode root = mapper.readTree(response);
            return root.path("messaggio").asText("Issue creata");
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public String modificaIssue(Long issueId, String titolo, String descrizione, String priorita, String tipo, String assegnatarioUsername, String immagineUrl, String richiedente) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("titolo", titolo);
            body.put("descrizione", descrizione);
            body.put("priorita", priorita);
            body.put("tipo", tipo);
            body.put("assegnatarioUsername", assegnatarioUsername);
            body.put("richiedente", richiedente);
            if (immagineUrl != null && !immagineUrl.isEmpty()) {
                body.put("immagineUrl", immagineUrl);
            }

            String response = put("/api/issues/" + issueId, body);
            JsonNode root = mapper.readTree(response);
            return root.path("messaggio").asText("Issue modificata");
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public List<Map<String, Object>> getAllIssues() {
        try {
            String response = get("/api/issues");
            return mapper.readValue(response, List.class);
        } catch (Exception e) {
            System.out.println("[IssueClient] getAllIssues fallito: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getIssueByAssegnatario(String nomeUtente) throws Exception {
        String response = get("/api/issues/assegnatario/" + nomeUtente);
        return mapper.readValue(response, List.class);
    }

    public Map<String, Object> getIssueById(Long id) throws Exception {
        String response = get("/api/issues/" + id);
        return mapper.readValue(response, Map.class);
    }

    public String eliminaIssue(Long issueId, String nomeUtente) {
        try {
            String response = delete("/api/issues/" + issueId + "?nomeUtente=" + nomeUtente);
            JsonNode root = mapper.readTree(response);
            return root.path("messaggio").asText("Issue eliminata");
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public String risolviIssue(Long issueId) {
        try {
            String response = put("/api/issues/" + issueId + "/risolvi", null);
            JsonNode root = mapper.readTree(response);
            return root.path("messaggio").asText("Issue risolta");
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public String uploadImage(File file) throws IOException {
        String boundary = "---" + System.currentTimeMillis() + "---";
        URL url = new URL(baseUrl + "/api/issues/upload");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String token = SessioneManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }

        try (OutputStream output = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
            writer.append("Content-Type: ").append(Files.probeContentType(file.toPath())).append("\r\n");
            writer.append("\r\n");
            writer.flush();

            Files.copy(file.toPath(), output);
            output.flush();

            writer.append("\r\n").append("--").append(boundary).append("--\r\n");
            writer.flush();
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
            throw new IOException("Upload fallito HTTP " + code + ": " + sb);
        }

        JsonNode root = mapper.readTree(sb.toString());
        return root.path("url").asText();
    }

    public String getProxyUrl(String originalUrl) {
        return baseUrl + "/api/issues/proxy-immagine?url=" + URLEncoder.encode(originalUrl, StandardCharsets.UTF_8);
    }
}
