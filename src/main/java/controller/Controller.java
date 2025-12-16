package controller;

import dao_implementazione.*;
import dao_interfaccia.*;
import modello.Utente;
import java.util.List;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;

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

    public boolean creaUtente(String nomeUtente, String password, String nome, String cognome, String email, String ruolo) {
        try {
            return DAO_Account.creaUtente(nomeUtente, password, nome, cognome, email, ruolo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utente login(String nomeUtente, String password) {
        try {
            boolean successo = DAO_Account.login(nomeUtente, password);

            if (successo) {
                Utente utente = DAO_Account.getUtente(nomeUtente);
                return utente;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
