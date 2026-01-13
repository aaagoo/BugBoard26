package backend.service;

import modello.Priorita;
import modello.Tipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class IssueService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public String creaIssue(String titolo, String descrizione, Priorita priorita, Tipo tipo,
                            String creatoreUsername, String immagineUrl) {
        String assegnatarioUsername = jdbcTemplate.queryForObject(
                "SELECT * FROM trova_utente_libero()",
                String.class
        );

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM crea_issue(?, ?, ?::priorita_enum, ?::tipo_enum, ?, ?, ?)",
                titolo, descrizione, priorita.name(), tipo.name(),
                creatoreUsername, assegnatarioUsername, immagineUrl
        );

        return result.get("messaggio") + " e assegnata a " + assegnatarioUsername;
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
        List<String> results = jdbcTemplate.queryForList(
                "SELECT creatoreUsername FROM issue WHERE id = ?",
                String.class,
                issueId
        );

        if (results.isEmpty()) {
            return "Issue non trovata";
        }

        if (!results.get(0).equals(nomeUtente)) {
            return "Non sei autorizzato a eliminare questa issue";
        }

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM elimina_issue(?)",
                issueId
        );
        return (String) result.get("messaggio");
    }

    @Transactional
    public String risolviIssue(Long issueId) {
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM risolvi_issue(?)",
                issueId
        );
        return (String) result.get("messaggio");
    }
}
