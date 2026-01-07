package dao_interfaccia;

import modello.Utente;

import java.util.List;
import java.util.Map;
import modello.Ruolo;

public interface DAO_Account {

    String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) throws java.sql.SQLException;
    boolean login(String nomeUtente, String password) throws java.sql.SQLException;
    Utente getUtente(String nomeUtente) throws java.sql.SQLException;
    List<Map<String, Object>> getAllAccounts() throws java.sql.SQLException;
    String eliminaAccount(String nomeUtente) throws java.sql.SQLException;
    String modificaAccount(String nomeUtente, String password, String nome, String cognome, String email, String avatar) throws java.sql.SQLException;
}
