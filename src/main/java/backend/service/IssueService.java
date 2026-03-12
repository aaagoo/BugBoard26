package backend.service;

import modello.Priorita;
import modello.Tipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class IssueService {
    
    private static final String KEY_MESSAGGIO = "messaggio";
    private static final String KEY_SUCCESSO = "successo";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StorageService storageService;

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
            return storageService.upload(file);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'upload dell'immagine: " + e.getMessage());
        }
    }

    public byte[] downloadImmagine(String urlImmagine) {
        try {
            return storageService.download(urlImmagine);
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
            // Ignora se non trova l'issue
        }

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM elimina_issue(?, ?)",
                issueId,
                nomeUtente
        );
        
        String messaggio = (String) result.get(KEY_MESSAGGIO);

        if (messaggio != null && messaggio.contains(KEY_SUCCESSO) && immagineUrl != null && !immagineUrl.isEmpty()) {
            storageService.delete(immagineUrl);
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
}
