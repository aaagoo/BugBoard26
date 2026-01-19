package gui.util;

import modello.Account;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class Utility {

    // --- ImageUtils ---
    public static Image getIconaApplicazione() {
        return ImageUtils.getIconaApplicazione();
    }

    public static Icon getSVGIcon(String name, int width, int height) {
        return ImageUtils.getSVGIcon(name, width, height);
    }

    public static void caricaAvatar(JLabel label, String avatarName, int width, int height) {
        ImageUtils.caricaAvatar(label, avatarName, width, height);
    }

    public static void caricaImmagine(JLabel label, String resourcePath, int width, int height) {
        ImageUtils.caricaImmagine(label, resourcePath, width, height);
    }

    public static String[] getAvatarFiles() {
        return ImageUtils.getAvatarFiles();
    }

    public static String scegliAvatar(JFrame parent, String avatarCorrente) {
        return ImageUtils.scegliAvatar(parent, avatarCorrente);
    }

    // --- TableUtils ---
    public static void popolaTabella(JTable tabella, List<Map<String, Object>> dati, String[] colonne) {
        TableUtils.popolaTabella(tabella, dati, colonne);
    }

    public static void popolaTabellaAccount(JTable tabella, List<Map<String, Object>> dati) {
        TableUtils.popolaTabellaAccount(tabella, dati);
    }

    public static void popolaTabellaIssue(JTable dashboardTable, List<Map<String, Object>> dati) {
        TableUtils.popolaTabellaIssue(dashboardTable, dati);
    }

    public static void impostaLarghezzeColonne(JTable table, int... larghezze) {
        TableUtils.impostaLarghezzeColonne(table, larghezze);
    }

    public static void impostaColorazioneRisolto(JTable table) {
        TableUtils.impostaColorazioneRisolto(table);
    }

    public static void selezionaRigaTabella(JTable tabella, JTextField field, int colonnaIndice) {
        TableUtils.selezionaRigaTabella(tabella, field, colonnaIndice);
    }

    // --- DateUtils ---
    public static String formattaData(Object dataObj) {
        return DateUtils.formattaData(dataObj);
    }

    // --- NavigationUtils ---
    public static void redirectByRole(Account utente) {
        NavigationUtils.redirectByRole(utente);
    }
}
