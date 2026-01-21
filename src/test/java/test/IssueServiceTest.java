package test;

import backend.service.IssueService;
import modello.Priorita;
import modello.Tipo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private IssueService issueService;

    @Test
    void testCreaIssueSuccesso() {
        // Arrange
        String titolo = "Bug Login";
        String descrizione = "Errore 500";
        Priorita priorita = Priorita.HIGH;
        Tipo tipo = Tipo.BUG;
        String creatore = "user1";
        String assegnatario = "dev1";
        String immagine = "url_img";

        // Simula la chiamata alla funzione SQL 'crea_issue'
        when(jdbcTemplate.queryForMap(
                contains("SELECT * FROM crea_issue"),
                eq(titolo),
                eq(descrizione),
                eq("HIGH"),
                eq("BUG"),
                eq(creatore),
                eq(assegnatario),
                eq(immagine)
        )).thenReturn(Map.of("messaggio", "Issue creata con successo"));

        // Act
        String risultato = issueService.creaIssue(titolo, descrizione, priorita, tipo, creatore, assegnatario, immagine);

        // Assert
        assertTrue(risultato.contains("Issue creata con successo"));
    }

    @Test
    void testCreaIssueFallito() {
        // Arrange
        String titolo = "Titolo Errato";
        String descrizione = "Descrizione";
        Priorita priorita = Priorita.LOW;
        Tipo tipo = Tipo.QUESTION;
        String creatore = "user1";
        String assegnatario = "dev1"; // Assegnatario manuale per evitare trova_utente_libero
        String immagine = null;
        String messaggioErroreDB = "Errore: Titolo troppo lungo";

        // Simula la chiamata SQL che restituisce un messaggio di errore
        when(jdbcTemplate.queryForMap(
                contains("SELECT * FROM crea_issue"),
                eq(titolo), eq(descrizione), eq("LOW"), eq("QUESTION"),
                eq(creatore), eq(assegnatario), eq(immagine)
        )).thenReturn(Map.of("messaggio", messaggioErroreDB));
        // Act
        String risultato = issueService.creaIssue(titolo, descrizione, priorita, tipo, creatore, assegnatario, immagine);
        // Assert
        assertTrue(risultato.contains("Errore: Titolo troppo lungo"), "Il risultato dovrebbe contenere il messaggio di errore del DB");
    }
}