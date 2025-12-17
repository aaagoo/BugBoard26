package sessione;

import modello.Utente;

public class SessioneManager {
    private static SessioneManager instance;
    private Utente utenteCorrente;

    private SessioneManager() {
    }

    public static SessioneManager getInstance() {
        if (instance == null) {
            instance = new SessioneManager();
        }
        return instance;
    }

    public void setUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public void logout() {
        utenteCorrente = null;
    }

    public boolean isLoggato() {
        return utenteCorrente != null;
    }
}
