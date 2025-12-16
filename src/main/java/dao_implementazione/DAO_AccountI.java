package dao_implementazione;

import dao_interfaccia.DAO_Account;
import connessione.ConnessioneDatabase;
import modello.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DAO_AccountI implements DAO_Account {
    private static DAO_AccountI instance;

    private DAO_AccountI() {
        // Inizializzazione della connessione al database, se necessario
    }

    public static DAO_AccountI getInstance() {
        if (instance == null) {
            instance = new DAO_AccountI();
        }
        return instance;
    }

    @Override
    public boolean creaUtente(String nomeUtente, String password, String nome,
                              String cognome, String email, String ruolo) {
        String sql = "SELECT * FROM crea_utente(?, ?, ?, ?, ?, ?::ruolo_enum)";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, nomeUtente);
            stmt.setString(2, password);
            stmt.setString(3, nome);
            stmt.setString(4, cognome);
            stmt.setString(5, email);
            stmt.setString(6, ruolo);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean successo = rs.getBoolean("successo");
                String messaggio = rs.getString("messaggio");
                System.out.println(messaggio);
                return successo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean login(String nomeUtente, String password) {
        String sql = "SELECT * FROM login(?, ?)";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, nomeUtente);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("successo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Utente getUtente(String nomeUtente) {
        String sql = "SELECT * FROM utente WHERE nomeUtente = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeUtente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Utente(
                        rs.getString("nomeUtente"),
                        rs.getString("password"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("email"),
                        Ruolo.valueOf(rs.getString("ruolo"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
