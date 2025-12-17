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

    public static DAO_AccountI getInstance() {
        if (instance == null) {
            instance = new DAO_AccountI();
        }
        return instance;
    }

    @Override
    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo) {
        try (CallableStatement cstmt = ConnessioneDatabase.getInstance().connection.prepareCall("{ call crea_account(?, ?, ?, ?, ?, ?::ruolo_enum) }")) {
            cstmt.setString(1, nomeUtente);
            cstmt.setString(2, password);
            cstmt.setString(3, nome);
            cstmt.setString(4, cognome);
            cstmt.setString(5, email);
            cstmt.setString(6, ruolo.name());

            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("messaggio");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Errore sconosciuto";
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
        String sql = "SELECT * FROM account WHERE nomeUtente = ?";

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

    @Override
    public List<Map<String, Object>> getAllAccounts() {
        List<Map<String, Object>> accounts = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement cstmt = conn.prepareCall("{ call get_all_accounts() }")) {

            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> account = new HashMap<>();
                account.put("nomeUtente", rs.getString("nomeUtente"));
                account.put("password", rs.getString("password"));
                account.put("nome", rs.getString("nome"));
                account.put("cognome", rs.getString("cognome"));
                account.put("email", rs.getString("email"));
                account.put("ruolo", rs.getString("ruolo"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public String eliminaAccount(String nomeUtente) {
        try (CallableStatement cstmt = ConnessioneDatabase.getInstance().connection.prepareCall("{ call elimina_account(?) }")) {
            cstmt.setString(1, nomeUtente);

            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                boolean successo = rs.getBoolean("successo");
                String messaggio = rs.getString("messaggio");
                return messaggio;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Errore sconosciuto";
    }


}
