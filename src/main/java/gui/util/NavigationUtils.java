package gui.util;

import gui.swing.HomeAmm;
import gui.swing.HomeUtente;
import modello.Account;
import modello.Ruolo;

public class NavigationUtils {

    public static void redirectByRole(Account utente) {
        Ruolo ruolo = utente.getRuolo();

        if (ruolo == Ruolo.AMMINISTRATORE) {
            new HomeAmm();
        } else {
            new HomeUtente();
        }
    }
}
