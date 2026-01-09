package backend.service;

import dao_implementazione.DAO_Account;
import modello.Ruolo;
import modello.Utente;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class AccountService {
    private final DAO_Account dao;

    public AccountService() {
        this.dao = DAO_Account.getInstance();
    }

    public Utente login(String username, String password) throws SQLException {
        boolean successo = dao.login(username, password);
        return successo ? dao.getUtente(username) : null;
    }

    public String generateToken(Utente utente) {
        return "TOKEN_" + utente.getNomeUtente() + "_" + System.currentTimeMillis();
    }

    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) throws SQLException {
        return dao.creaAccount(nomeUtente, password, nome, cognome, email, ruolo, avatar);
    }

    public List<Map<String, Object>> getAllAccounts() throws SQLException {
        return dao.getAllAccounts();
    }

    public Utente getUtente(String nomeUtente) throws SQLException {
        return dao.getUtente(nomeUtente);
    }

    public String modificaAccount(String nomeUtente, String password, String nome, String cognome, String email, String avatar) throws SQLException {
        return dao.modificaAccount(nomeUtente, password, nome, cognome, email, avatar);
    }

    public String eliminaAccount(String nomeUtente) throws SQLException {
        return dao.eliminaAccount(nomeUtente);
    }
}
