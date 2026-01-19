package controller;

import modello.Notifica;
import modello.Ruolo;
import modello.Account;
import sessione.SessioneManager;
import remote.ApiClient;
import java.util.stream.Collectors;
import java.io.File;

import java.util.*;

public class Controller {
    private static Controller instance;
    private final ApiClient apiClient;

    private Controller() {
        this.apiClient = new ApiClient("http://localhost:8080");
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public Account login(String nomeUtente, String password) {
        try {
            Account utente = apiClient.login(nomeUtente, password);
            if (utente != null) {
                SessioneManager.getInstance().setUtenteCorrente(utente);
                return utente;
            }
        } catch (Exception e) {
            System.out.println("[Controller] Errore login: " + e.getMessage());
        }
        return null;
    }

    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) {
        try {
            return apiClient.creaAccount(nomeUtente, password, nome, cognome, email, ruolo, avatar);
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public List<Map<String, Object>> getAllAccounts() {
        try {
            return apiClient.getAllAccounts();
        } catch (Exception e) {
            System.out.println("[Controller] Errore getAllAccounts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Account getUtenteByNomeUtente(String nomeUtente) {
        try {
            return apiClient.getUtente(nomeUtente);
        } catch (Exception e) {
            System.out.println("[Controller] Errore getUtente: " + e.getMessage());
            return null;
        }
    }

    public String modificaAccount(String nomeUtente, String password, String nome, String cognome, String email, String avatar) {
        try {
            String messaggio = apiClient.modificaAccount(nomeUtente, password, nome, cognome, email, avatar);
            Account utenteCorrente = SessioneManager.getInstance().getUtenteCorrente();
            if (utenteCorrente != null && utenteCorrente.getNomeUtente().equals(nomeUtente)) {
                Account aggiornato = apiClient.getUtente(nomeUtente);
                SessioneManager.getInstance().setUtenteCorrente(aggiornato);
            }
            return messaggio;
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public String eliminaAccount(String nomeUtente) {
        try {
            return apiClient.eliminaAccount(nomeUtente);
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
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

    public String uploadImmagine(File file) {
        try {
            return apiClient.uploadImage(file);
        } catch (Exception e) {
            System.out.println("[Controller] Errore upload immagine: " + e.getMessage());
            return null;
        }
    }

    public String getProxyImageUrl(String originalUrl) {
        return apiClient.getProxyUrl(originalUrl);
    }

    public String creaIssue(String titolo, String descrizione, String priorita, String tipo, String assegnatarioUsername, String immagineUrl) {
        try {
            Account utente = getUtenteCorrente();
            if (utente == null) {
                return "Errore: Utente non loggato";
            }
            return apiClient.creaIssue(titolo, descrizione, priorita, tipo, utente.getNomeUtente(), assegnatarioUsername, immagineUrl);
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public String modificaIssue(Long issueId, String titolo, String descrizione, String priorita, String tipo, String assegnatarioUsername, String immagineUrl) {
        try {
            Account utente = getUtenteCorrente();
            if (utente == null) {
                return "Errore: Utente non loggato";
            }
            return apiClient.modificaIssue(issueId, titolo, descrizione, priorita, tipo, assegnatarioUsername, immagineUrl, utente.getNomeUtente());
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public List<Map<String, Object>> getAllIssues() {
        try {
            return apiClient.getAllIssues();
        } catch (Exception e) {
            System.out.println("[Controller] Errore getAllIssues: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getIssueAssegnate(String nomeUtente) {
        try {
            return apiClient.getIssueByAssegnatario(nomeUtente);
        } catch (Exception e) {
            System.out.println("[Controller] Errore getIssueAssegnate: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getIssueById(Long id) {
        try {
            return apiClient.getIssueById(id);
        } catch (Exception e) {
            System.out.println("[Controller] Errore getIssueById: " + e.getMessage());
            return null;
        }
    }

    public String eliminaIssue(Long issueId) {
        try {
            Account utente = getUtenteCorrente();
            if (utente == null) {
                return "Errore: Utente non loggato";
            }
            return apiClient.eliminaIssue(issueId, utente.getNomeUtente());
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public String risolviIssue(Long issueId) {
        try {
            return apiClient.risolviIssue(issueId);
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    public List<Notifica> getNotifiche() {
        Account utente = getUtenteCorrente();
        if (utente == null) return new ArrayList<>();
        return apiClient.getNotifiche(utente.getNomeUtente());
    }

    public void segnaNotificaLetta(Long id) {
        apiClient.segnaNotificaLetta(id);
    }

    public int contaNotificheNonLette() {
        Account utente = getUtenteCorrente();
        if (utente == null) return 0;
        return apiClient.contaNotificheNonLette(utente.getNomeUtente());
    }

}
