package test;

import backend.service.AccountService;
import modello.Account;
import modello.Ruolo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testLoginSuccesso() {
        //Preparazione dei dati e del comportamento simulato del DB.
        String username = "admin";
        String password = "password";

        //Simula la risposta della funzione SQL 'login'
        when(jdbcTemplate.queryForMap(contains("SELECT * FROM login"), eq(username), eq(password)))
                .thenReturn(Map.of("successo", true));

        //Simula la risposta della query per recuperare i dati utente
        Map<String, Object> mockUserData = Map.of(
                "nomeutente", "admin",
                "password", "password",
                "nome", "Mario",
                "cognome", "Rossi",
                "email", "mario@test.com",
                "ruolo", "AMMINISTRATORE",
                "avatar", "user.png",
                "issueassegnate", 0
        );
        when(jdbcTemplate.queryForMap(contains("SELECT nomeUtente"), eq(username), eq(username)))
                .thenReturn(mockUserData);

        //Esecuzione del metodo da testare
        Account result = accountService.login(username, password);

        //Verifica dei risultati
        assertNotNull(result, "L'account restituito non deve essere null");
        assertEquals("admin", result.getNomeUtente());
        assertEquals(Ruolo.AMMINISTRATORE, result.getRuolo());
    }

    @Test
    void testLoginFallito() {
        // Arrange
        String username = "wrong";
        String password = "wrong";

        // Simula login fallito dal DB
        when(jdbcTemplate.queryForMap(contains("SELECT * FROM login"), eq(username), eq(password)))
                .thenReturn(Map.of("successo", false));

        // Act
        Account result = accountService.login(username, password);

        // Assert
        assertNull(result, "Il login con credenziali errate deve restituire null");
    }
}