package dao_interfaccia;

import modello.Utente;

public interface DAO_Account {

    boolean creaUtente(String nomeUtente, String password, String nome, String cognome, String email, String ruolo) throws java.sql.SQLException;
    boolean login(String nomeUtente, String password) throws java.sql.SQLException;
    Utente getUtente(String nomeUtente) throws java.sql.SQLException;
}
