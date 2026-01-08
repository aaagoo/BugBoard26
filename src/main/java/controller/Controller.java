package controller;

import dao_interfaccia.*;
import modello.Ruolo;
import modello.Utente;
import java.util.List;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import sessione.SessioneManager;

public class Controller {
    private static Controller instance;
    private final DAO_AccountInt DAO_Account;

    private Controller() {
        this.DAO_Account = dao_implementazione.DAO_Account.getInstance();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) {
        try {
            return DAO_Account.creaAccount(nomeUtente, password, nome, cognome, email, ruolo, avatar);
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

    public Utente getUtenteByNomeUtente(String nomeUtente) {
        try {
            return DAO_Account.getUtente(nomeUtente);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String eliminaAccount(String nomeUtente) {
        try {
            return DAO_Account.eliminaAccount(nomeUtente);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore nell'eliminazione dell'account";
        }
    }

    public Utente getUtenteCorrente() {
        return SessioneManager.getInstance().getUtenteCorrente();
    }

    public String modificaAccount(String nomeUtente, String password, String nome, String cognome, String email, String avatar) {
        try {
            String messaggio = DAO_Account.modificaAccount(nomeUtente, password, nome, cognome, email, avatar);

            if (messaggio.contains("successo")) {
                Utente utenteCorrente = SessioneManager.getInstance().getUtenteCorrente();
                if (utenteCorrente != null && utenteCorrente.getNomeUtente().equals(nomeUtente)) {
                    Utente utenteAggiornato = DAO_Account.getUtente(nomeUtente);
                    SessioneManager.getInstance().setUtenteCorrente(utenteAggiornato);
                }
            }
            return messaggio;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore: " + e.getMessage();
        }
    }



}
