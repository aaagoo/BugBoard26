package backend.service;

import modello.Ruolo;
import modello.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountService {
    
    private static final String KEY_MESSAGGIO = "messaggio";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Account login(String username, String password) {
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM login(?, ?)",
                username, password
        );

        Boolean successo = (Boolean) result.get("successo");

        if (!Boolean.TRUE.equals(successo)) {
            return null;
        }

        Map<String, Object> accountData = jdbcTemplate.queryForMap(
                "SELECT nomeUtente, password, nome, cognome, email, ruolo, avatar, issueassegnate " +
                        "FROM account WHERE nomeUtente = ? OR email = ?",
                username, username
        );

        return new Account(
                (String) accountData.get("nomeutente"),
                (String) accountData.get("password"),
                (String) accountData.get("nome"),
                (String) accountData.get("cognome"),
                (String) accountData.get("email"),
                Ruolo.valueOf((String) accountData.get("ruolo")),
                (String) accountData.get("avatar"),
                (Integer) accountData.get("issueassegnate")
        );
    }

    public String generateToken(Account utente) {
        return "TOKEN_" + utente.getNomeUtente() + "_" + System.currentTimeMillis();
    }

    @Transactional
    public String creaAccount(String nomeUtente, String password, String nome, String cognome,
                              String email, Ruolo ruolo, String avatar) {
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM crea_account(?, ?, ?, ?, ?, ?::ruolo_enum, ?)",
                nomeUtente, password, nome, cognome, email, ruolo.name(), avatar
        );

        return (String) result.get(KEY_MESSAGGIO);
    }

    public List<Map<String, Object>> getAllAccounts() {
        return jdbcTemplate.queryForList("SELECT * FROM get_all_accounts()");
    }

    public Account getUtente(String nomeUtente) {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT nomeUtente, password, nome, cognome, email, ruolo, avatar, issueassegnate " +
                        "FROM account WHERE nomeUtente = ?",
                nomeUtente
        );

        if (results.isEmpty()) {
            return null;
        }

        Map<String, Object> row = results.get(0);
        return new Account(
                (String) row.get("nomeutente"),
                (String) row.get("password"),
                (String) row.get("nome"),
                (String) row.get("cognome"),
                (String) row.get("email"),
                Ruolo.valueOf((String) row.get("ruolo")),
                (String) row.get("avatar"),
                (Integer) row.get("issueassegnate")
        );
    }

    @Transactional
    public String modificaAccount(String nomeUtente, String password, String nome,
                                  String cognome, String email, String avatar) {
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM modifica_account(?, ?, ?, ?, ?, ?)",
                nomeUtente,
                password != null ? password : "",
                nome != null ? nome : "",
                cognome != null ? cognome : "",
                email != null ? email : "",
                avatar != null ? avatar : ""
        );

        return (String) result.get(KEY_MESSAGGIO);
    }

    @Transactional
    public String eliminaAccount(String nomeUtente) {
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM elimina_account(?)",
                nomeUtente
        );

        return (String) result.get(KEY_MESSAGGIO);
    }
}
