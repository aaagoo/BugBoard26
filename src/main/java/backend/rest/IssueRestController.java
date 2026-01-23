package backend.rest;

import backend.service.IssueService;
import modello.Priorita;
import modello.Tipo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueRestController {
    
    private static final String KEY_MESSAGGIO = "messaggio";
    private static final String KEY_SUCCESSO = "successo";
    
    private final IssueService issueService;

    public IssueRestController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadImmagine(@RequestParam("file") MultipartFile file) {
        try {
            String url = issueService.uploadImmagine(file);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(KEY_MESSAGGIO, "Errore upload: " + e.getMessage()));
        }
    }

    @GetMapping("/proxy-immagine")
    public ResponseEntity<byte[]> getImmagineProxy(@RequestParam String url) {
        try {
            byte[] imageBytes = issueService.downloadImmagine(url);

            MediaType mediaType = MediaType.IMAGE_PNG;
            if (url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if (url.toLowerCase().endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping
    public ResponseEntity<Object> creaIssue(@RequestBody Map<String, String> body) {
        try {
            Priorita priorita = Priorita.valueOf(body.get("priorita").toUpperCase());
            Tipo tipo = Tipo.valueOf(body.get("tipo").toUpperCase());

            String messaggio = issueService.creaIssue(
                    body.get("titolo"),
                    body.get("descrizione"),
                    priorita,
                    tipo,
                    body.get("creatoreUsername"),
                    body.getOrDefault("assegnatarioUsername", null),
                    body.getOrDefault("immagineUrl", null)
            );
            return ResponseEntity.ok(Map.of(KEY_MESSAGGIO, messaggio));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(KEY_MESSAGGIO, "Errore: Priorità o tipo non validi"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(KEY_MESSAGGIO, "Errore nella creazione dell'issue: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> modificaIssue(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Priorita priorita = Priorita.valueOf(body.get("priorita").toUpperCase());
            Tipo tipo = Tipo.valueOf(body.get("tipo").toUpperCase());

            String messaggio = issueService.modificaIssue(
                    id,
                    body.get("titolo"),
                    body.get("descrizione"),
                    priorita,
                    tipo,
                    body.get("assegnatarioUsername"),
                    body.getOrDefault("immagineUrl", null),
                    body.get("richiedente")
            );
            
            if (messaggio.contains(KEY_SUCCESSO)) {
                return ResponseEntity.ok(Map.of(KEY_MESSAGGIO, messaggio));
            } else {
                return ResponseEntity.status(403).body(Map.of(KEY_MESSAGGIO, messaggio));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(KEY_MESSAGGIO, "Errore: Priorità o tipo non validi"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(KEY_MESSAGGIO, "Errore nella modifica dell'issue: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllIssues() {
        try {
            List<Map<String, Object>> issues = issueService.getAllIssues();
            return ResponseEntity.ok(issues);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/assegnatario/{nomeUtente}")
    public ResponseEntity<List<Map<String, Object>>> getIssueByAssegnatario(@PathVariable String nomeUtente) {
        try {
            List<Map<String, Object>> issues = issueService.getIssueByAssegnatario(nomeUtente);
            return ResponseEntity.ok(issues);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getIssueById(@PathVariable Long id) {
        try {
            Map<String, Object> issue = issueService.getIssueById(id);
            return ResponseEntity.ok(issue);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(KEY_MESSAGGIO, "Issue non trovata"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminaIssue(@PathVariable Long id, @RequestParam String nomeUtente) {
        try {
            String messaggio = issueService.eliminaIssue(id, nomeUtente);
            if (messaggio.contains(KEY_SUCCESSO)) {
                return ResponseEntity.ok(Map.of(KEY_MESSAGGIO, messaggio));
            } else {
                return ResponseEntity.status(403).body(Map.of(KEY_MESSAGGIO, messaggio));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(KEY_MESSAGGIO, "Errore: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/risolvi")
    public ResponseEntity<Object> risolviIssue(@PathVariable Long id) {
        try {
            String messaggio = issueService.risolviIssue(id);
            if (messaggio.contains(KEY_SUCCESSO)) {
                return ResponseEntity.ok(Map.of(KEY_MESSAGGIO, messaggio));
            } else {
                return ResponseEntity.badRequest().body(Map.of(KEY_MESSAGGIO, messaggio));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(KEY_MESSAGGIO, "Errore: " + e.getMessage()));
        }
    }

}
