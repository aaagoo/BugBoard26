package backend.rest;

import backend.service.NotificaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifiche")
@CrossOrigin(origins = "*")
public class NotificaRestController {

    private final NotificaService notificaService;

    public NotificaRestController(NotificaService notificaService) {
        this.notificaService = notificaService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Map<String, Object>>> getNotifiche(@PathVariable String username) {
        return ResponseEntity.ok(notificaService.getNotifiche(username));
    }

    @PutMapping("/{id}/letta")
    public ResponseEntity<?> segnaComeLetta(@PathVariable Long id) {
        notificaService.segnaComeLetta(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/count")
    public ResponseEntity<Integer> contaNonLette(@PathVariable String username) {
        return ResponseEntity.ok(notificaService.contaNonLette(username));
    }
}
