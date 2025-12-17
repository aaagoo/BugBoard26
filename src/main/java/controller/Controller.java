package controller;

import dao_implementazione.*;
import dao_interfaccia.*;
import modello.Ruolo;
import modello.Utente;
import java.util.List;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Controller {
    private static Controller instance;
    private final DAO_Account DAO_Account;

    private Controller() {
        this.DAO_Account = DAO_AccountI.getInstance();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo) {
        try {
            return DAO_Account.creaAccount(nomeUtente, password, nome, cognome, email, ruolo);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore nella creazione dell'account";
        }
    }

    public Utente login(String nomeUtente, String password) {
        try {
            boolean successo = DAO_Account.login(nomeUtente, password);

            if (successo) {
                Utente utente = DAO_Account.getUtente(nomeUtente);
                sessione.SessioneManager.getInstance().setUtenteCorrente(utente);
                return utente;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getAllAccounts() {
        try {
            return DAO_Account.getAllAccounts();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getUtenti() throws SQLException {
        return DAO_Account.getAllAccounts().stream()
                .filter(account -> "UTENTE".equals(account.get("ruolo")))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAmministratori() throws SQLException {
        return DAO_Account.getAllAccounts().stream()
                .filter(account -> "AMMINISTRATORE".equals(account.get("ruolo")))
                .collect(Collectors.toList());
    }

    public String eliminaAccount(String nomeUtente) {
        try {
            return DAO_Account.eliminaAccount(nomeUtente);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore nell'eliminazione dell'account";
        }
    }
}
