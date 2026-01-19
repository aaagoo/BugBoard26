package controller;

import modello.Notifica;
import modello.Ruolo;
import modello.Account;
import remote.client.AccountClient;
import remote.client.AuthClient;
import remote.client.IssueClient;
import remote.client.NotificationClient;
import sessione.SessioneManager;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private static Controller instance;
    
    private final AuthClient authClient;
    private final AccountClient accountClient;
    private final IssueClient issueClient;
    private final NotificationClient notificationClient;

    private Controller() {
        String baseUrl = "http://localhost:8080";
        this.authClient = new AuthClient(baseUrl);
        this.accountClient = new AccountClient(baseUrl);
        this.issueClient = new IssueClient(baseUrl);
        this.notificationClient = new NotificationClient(baseUrl);
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    // --- Auth ---
    public Account login(String nomeUtente, String password) {
        try {
            Account utente = authClient.login(nomeUtente, password);
            if (utente != null) {
                SessioneManager.getInstance().setUtenteCorrente(utente);
                return utente;
            }
        } catch (Exception e) {
            System.out.println("[Controller] Errore login: " + e.getMessage());
        }
        return null;
    }

    // --- Account ---
    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) {
        return accountClient.creaAccount(nomeUtente, password, nome, cognome, email, ruolo, avatar);
    }

    public List<Map<String, Object>> getAllAccounts() {
        return accountClient.getAllAccounts();
    }

    public Account getUtenteByNomeUtente(String nomeUtente) {
        return accountClient.getUtente(nomeUtente);
    }

    public String modificaAccount(String nomeUtente, String password, String nome, String cognome, String email, String avatar) {
        String messaggio = accountClient.modificaAccount(nomeUtente, password, nome, cognome, email, avatar);
        Account utenteCorrente = SessioneManager.getInstance().getUtenteCorrente();
        if (utenteCorrente != null && utenteCorrente.getNomeUtente().equals(nomeUtente)) {
            Account aggiornato = accountClient.getUtente(nomeUtente);
            SessioneManager.getInstance().setUtenteCorrente(aggiornato);
        }
        return messaggio;
    }

    public String eliminaAccount(String nomeUtente) {
        return accountClient.eliminaAccount(nomeUtente);
    }

    public List<Map<String, Object>> getUtenti() {
        return getAllAccounts().stream()
                .filter(account -> "UTENTE".equals(account.get("ruolo")))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAmministratori() {
        return getAllAccounts().stream()
                .filter(account -> "AMMINISTRATORE".equals(account.get("ruolo")))
                .collect(Collectors.toList());
    }

    public Account getUtenteCorrente() {
        return SessioneManager.getInstance().getUtenteCorrente();
    }

    // --- Issue ---
    public String uploadImmagine(File file) {
        try {
            return issueClient.uploadImage(file);
        } catch (Exception e) {
            System.out.println("[Controller] Errore upload immagine: " + e.getMessage());
            return null;
        }
    }

    public String getProxyImageUrl(String originalUrl) {
        return issueClient.getProxyUrl(originalUrl);
    }

    public String creaIssue(String titolo, String descrizione, String priorita, String tipo, String assegnatarioUsername, String immagineUrl) {
        Account utente = getUtenteCorrente();
        if (utente == null) {
            return "Errore: Utente non loggato";
        }
        return issueClient.creaIssue(titolo, descrizione, priorita, tipo, utente.getNomeUtente(), assegnatarioUsername, immagineUrl);
    }

    public String modificaIssue(Long issueId, String titolo, String descrizione, String priorita, String tipo, String assegnatarioUsername, String immagineUrl) {
        Account utente = getUtenteCorrente();
        if (utente == null) {
            return "Errore: Utente non loggato";
        }
        return issueClient.modificaIssue(issueId, titolo, descrizione, priorita, tipo, assegnatarioUsername, immagineUrl, utente.getNomeUtente());
    }

    public List<Map<String, Object>> getAllIssues() {
        return issueClient.getAllIssues();
    }

    public List<Map<String, Object>> getIssueAssegnate(String nomeUtente) {
        try {
            return issueClient.getIssueByAssegnatario(nomeUtente);
        } catch (Exception e) {
            System.out.println("[Controller] Errore getIssueAssegnate: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getIssueById(Long id) {
        try {
            return issueClient.getIssueById(id);
        } catch (Exception e) {
            System.out.println("[Controller] Errore getIssueById: " + e.getMessage());
            return null;
        }
    }

    public String eliminaIssue(Long issueId) {
        Account utente = getUtenteCorrente();
        if (utente == null) {
            return "Errore: Utente non loggato";
        }
        return issueClient.eliminaIssue(issueId, utente.getNomeUtente());
    }

    public String risolviIssue(Long issueId) {
        return issueClient.risolviIssue(issueId);
    }

    // --- Notifiche ---
    public List<Notifica> getNotifiche() {
        Account utente = getUtenteCorrente();
        if (utente == null) return new ArrayList<>();
        return notificationClient.getNotifiche(utente.getNomeUtente());
    }

    public void segnaNotificaLetta(Long id) {
        notificationClient.segnaNotificaLetta(id);
    }

    public int contaNotificheNonLette() {
        Account utente = getUtenteCorrente();
        if (utente == null) return 0;
        return notificationClient.contaNotificheNonLette(utente.getNomeUtente());
    }
}
