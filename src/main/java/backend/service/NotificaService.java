package backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificaService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getNotifiche(String username) {
        return jdbcTemplate.queryForList("SELECT * FROM get_notifiche(?)", username);
    }

    public void segnaComeLetta(Long notificaId) {
        jdbcTemplate.queryForObject("SELECT segna_notifica_letta(?)", Void.class, notificaId);
    }

    public int contaNonLette(String username) {
        return jdbcTemplate.queryForObject("SELECT conta_notifiche_non_lette(?)", Integer.class, username);
    }
}
