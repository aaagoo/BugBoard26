package controller;

import dao_interfaccia.DAO_AccountInt;
import modello.Ruolo;
import modello.Utente;
import sessione.SessioneManager;
import remote.ApiClient;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private static Controller instance;
    private final DAO_AccountInt DAO_Account;
    private final ApiClient apiClient;

    private Controller() {
        this.DAO_Account = dao_implementazione.DAO_Account.getInstance();
        this.apiClient = new ApiClient("http://localhost:8080");
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public Utente login(String nomeUtente, String password) {
        System.out.println("[Controller] Tentativo login remoto");
        try {
            Utente remoto = apiClient.login(nomeUtente, password);
            if (remoto != null) {
                SessioneManager.getInstance().setUtenteCorrente(remoto);
                System.out.println("[Controller] Login remoto OK");
                return remoto;
            }
        } catch (Exception e) {
            System.out.println("[Controller] Errore login remoto: " + e.getMessage());
        }

        System.out.println("[Controller] Fallback a DAO locale");
        try {
            boolean successo = DAO_Account.login(nomeUtente, password);
            if (successo) {
                Utente utente = DAO_Account.getUtente(nomeUtente);
                SessioneManager.getInstance().setUtenteCorrente(utente);
                return utente;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) {
        try {
            return apiClient.creaAccount(nomeUtente, password, nome, cognome, email, ruolo, avatar);
        } catch (Exception e) {
            System.out.println("[Controller] Fallback DAO per creaAccount");
            try {
                return DAO_Account.creaAccount(nomeUtente, password, nome, cognome, email, ruolo, avatar);
            } catch (SQLException ex) {
                return "Errore: " + ex.getMessage();
            }
        }
    }

    public List<Map<String, Object>> getAllAccounts() {
        try {
            return apiClient.getAllAccounts();
        } catch (Exception e) {
            System.out.println("[Controller] Fallback DAO per getAllAccounts");
            try {
                return DAO_Account.getAllAccounts();
            } catch (SQLException ex) {
                return new ArrayList<>();
            }
        }
    }

    public Utente getUtenteByNomeUtente(String nomeUtente) {
        try {
            return apiClient.getUtente(nomeUtente);
        } catch (Exception e) {
            System.out.println("[Controller] Fallback DAO per getUtente");
            try {
                return DAO_Account.getUtente(nomeUtente);
            } catch (SQLException ex) {
                return null;
            }
        }
    }

    public String modificaAccount(String nomeUtente, String password, String nome, String cognome, String email, String avatar) {
        try {
            String messaggio = apiClient.modificaAccount(nomeUtente, password, nome, cognome, email, avatar);
            Utente utenteCorrente = SessioneManager.getInstance().getUtenteCorrente();
            if (utenteCorrente != null && utenteCorrente.getNomeUtente().equals(nomeUtente)) {
                Utente aggiornato = apiClient.getUtente(nomeUtente);
                SessioneManager.getInstance().setUtenteCorrente(aggiornato);
            }
            return messaggio;
        } catch (Exception e) {
            System.out.println("[Controller] Fallback DAO per modificaAccount");
            try {
                return DAO_Account.modificaAccount(nomeUtente, password, nome, cognome, email, avatar);
            } catch (SQLException ex) {
                return "Errore: " + ex.getMessage();
            }
        }
    }

    public String eliminaAccount(String nomeUtente) {
        try {
            return apiClient.eliminaAccount(nomeUtente);
        } catch (Exception e) {
            System.out.println("[Controller] Fallback DAO per eliminaAccount");
            try {
                return DAO_Account.eliminaAccount(nomeUtente);
            } catch (SQLException ex) {
                return "Errore: " + ex.getMessage();
            }
        }
    }

    public List<Map<String, Object>> getUtenti() throws SQLException {
        return getAllAccounts().stream()
                .filter(account -> "UTENTE".equals(account.get("ruolo")))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAmministratori() throws SQLException {
        return getAllAccounts().stream()
                .filter(account -> "AMMINISTRATORE".equals(account.get("ruolo")))
                .collect(Collectors.toList());
    }

    public Utente getUtenteCorrente() {
        return SessioneManager.getInstance().getUtenteCorrente();
    }
}
