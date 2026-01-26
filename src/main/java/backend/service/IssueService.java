package backend.service;

import modello.Priorita;
import modello.Tipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;

@Service
public class IssueService {
    
    private static final String KEY_MESSAGGIO = "messaggio";
    private static final String KEY_SUCCESSO = "successo";

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png");

    @Transactional
    public String creaIssue(String titolo, String descrizione, Priorita priorita, Tipo tipo,
                            String creatoreUsername, String assegnatarioManuale, String immagineUrl) {
        
        String assegnatarioFinale;
        
        if (assegnatarioManuale != null && !assegnatarioManuale.trim().isEmpty()) {
            assegnatarioFinale = assegnatarioManuale;
        } else {
            assegnatarioFinale = jdbcTemplate.queryForObject(
                    "SELECT * FROM trova_utente_libero(?)",
                    String.class,
                    creatoreUsername
            );
        }

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM crea_issue(?, ?, ?::priorita_enum, ?::tipo_enum, ?, ?, ?)",
                titolo, descrizione, priorita.name(), tipo.name(),
                creatoreUsername, assegnatarioFinale, immagineUrl
        );

        return result.get(KEY_MESSAGGIO) + " e assegnata a " + assegnatarioFinale;
    }

    @Transactional
    public String modificaIssue(Long issueId, String titolo, String descrizione, Priorita priorita, Tipo tipo,
                                String assegnatario, String immagineUrl, String richiedente) {
        
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM modifica_issue(?, ?, ?, ?::priorita_enum, ?::tipo_enum, ?, ?, ?)",
                issueId, titolo, descrizione, priorita.name(), tipo.name(),
                assegnatario, immagineUrl, richiedente
        );

        return (String) result.get(KEY_MESSAGGIO);
    }

    public String uploadImmagine(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException("Formato file non supportato. Sono ammessi solo JPG e PNG.");
            }

            String originalFileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            String sanitizedFileName = originalFileName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._-]", "");
            String fileName = UUID.randomUUID().toString() + "_" + sanitizedFileName;

            String urlString = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(file.getBytes());
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
            } else {
                try (java.io.InputStream errorStream = conn.getErrorStream()) {
                    String errorBody = new String(errorStream.readAllBytes());
                    System.err.println("Errore Supabase: " + responseCode + ", Body: " + errorBody);
                    throw new RuntimeException("Errore upload Supabase: " + responseCode + " - " + errorBody);
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'upload dell'immagine: " + e.getMessage());
        }
    }

    public byte[] downloadImmagine(String urlImmagine) {
        try {
            if (!urlImmagine.startsWith(supabaseUrl)) {
                throw new IllegalArgumentException("URL non valido o esterno non consentito");
            }

            URL url = new URL(urlImmagine);
            try (InputStream in = url.openStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore download immagine: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getAllIssues() {
        return jdbcTemplate.queryForList("SELECT * FROM get_all_issues()");
    }

    public List<Map<String, Object>> getIssueByAssegnatario(String nomeUtente) {
        return jdbcTemplate.queryForList(
                "SELECT * FROM get_issue_by_assegnatario(?)",
                nomeUtente
        );
    }

    public Map<String, Object> getIssueById(Long id) {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT * FROM get_issue_by_id(?)",
                id
        );
        if (results.isEmpty()) {
            throw new IllegalArgumentException("Issue non trovata");
        }
        return results.get(0);
    }

    @Transactional
    public String eliminaIssue(Long issueId, String nomeUtente) {
        String immagineUrl = null;
        try {
            Map<String, Object> issue = getIssueById(issueId);
            immagineUrl = (String) issue.get("immagineurl");
        } catch (Exception e) {
            // Ignora se non trova l'issue, lascia che il DB gestisca l'errore
        }

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM elimina_issue(?, ?)",
                issueId,
                nomeUtente
        );
        
        String messaggio = (String) result.get(KEY_MESSAGGIO);

        if (messaggio != null && messaggio.contains(KEY_SUCCESSO) && immagineUrl != null && !immagineUrl.isEmpty()) {
            eliminaImmagineDaSupabase(immagineUrl);
        }

        return messaggio;
    }

    @Transactional
    public String risolviIssue(Long issueId) {
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM risolvi_issue(?)",
                issueId
        );
        return (String) result.get(KEY_MESSAGGIO);
    }

    private void eliminaImmagineDaSupabase(String immagineUrl) {
        try {
            String nomeFile = immagineUrl.substring(immagineUrl.lastIndexOf("/") + 1);
            String urlCancellazione = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + nomeFile;

            URL url = new URL(urlCancellazione);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 204) {
                System.out.println("Immagine eliminata da Supabase: " + nomeFile);
            } else {
                System.err.println("Errore eliminazione immagine Supabase. Codice: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Eccezione durante eliminazione immagine: " + e.getMessage());
        }
    }
}
