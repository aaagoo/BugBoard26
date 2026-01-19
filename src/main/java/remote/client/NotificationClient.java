package remote.client;

import modello.Notifica;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationClient extends BaseApiClient {

    public NotificationClient(String baseUrl) {
        super(baseUrl);
    }

    public List<Notifica> getNotifiche(String username) {
        try {
            String response = get("/api/notifiche/" + username);
            
            List<Map<String, Object>> rawList = mapper.readValue(response, List.class);
            List<Notifica> notifiche = new ArrayList<>();
            
            for (Map<String, Object> raw : rawList) {
                Notifica n = new Notifica();
                n.setId(((Number) raw.get("id")).longValue());
                n.setMessaggio((String) raw.get("messaggio"));
                n.setLetta((Boolean) raw.get("letta"));
                n.setDestinatarioUsername(username); 
                
                String dataStr = (String) raw.get("datacreazione");
                if (dataStr != null) {
                    if (dataStr.contains("+")) {
                        ZonedDateTime zdt = ZonedDateTime.parse(dataStr);
                        n.setDataCreazione(zdt.withZoneSameInstant(ZoneId.of("Europe/Rome")).toLocalDateTime());
                    } else {
                        n.setDataCreazione(LocalDateTime.parse(dataStr));
                    }
                }
                notifiche.add(n);
            }
            return notifiche;
        } catch (Exception e) {
            System.out.println("[NotificationClient] getNotifiche fallito: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void segnaNotificaLetta(Long id) {
        try {
            put("/api/notifiche/" + id + "/letta", null);
        } catch (Exception e) {
            System.out.println("[NotificationClient] segnaNotificaLetta fallito: " + e.getMessage());
        }
    }

    public int contaNotificheNonLette(String username) {
        try {
            String response = get("/api/notifiche/" + username + "/count");
            return Integer.parseInt(response);
        } catch (Exception e) {
            return 0;
        }
    }
}
