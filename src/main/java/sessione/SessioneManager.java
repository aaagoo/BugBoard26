package sessione;

import modello.Account;

public class SessioneManager {
    private static SessioneManager instance;
    private Account utenteCorrente;
    private String token;

    private SessioneManager() {
    }

    public static SessioneManager getInstance() {
        if (instance == null) {
            instance = new SessioneManager();
        }
        return instance;
    }

    public void setUtenteCorrente(Account utente) {
        this.utenteCorrente = utente;
    }

    public Account getUtenteCorrente() {
        return utenteCorrente;
    }

    public void logout() {
        utenteCorrente = null;
        token = null;
    }

    public boolean isLoggato() {
        return utenteCorrente != null;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
