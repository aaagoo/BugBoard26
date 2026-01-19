package modello;

import java.time.LocalDateTime;

public class Notifica {
    private Long id;
    private String destinatarioUsername;
    private String messaggio;
    private boolean letta;
    private LocalDateTime dataCreazione;

    public Notifica() {
    }

    public Notifica(Long id, String destinatarioUsername, String messaggio, boolean letta, LocalDateTime dataCreazione) {
        this.id = id;
        this.destinatarioUsername = destinatarioUsername;
        this.messaggio = messaggio;
        this.letta = letta;
        this.dataCreazione = dataCreazione;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestinatarioUsername() {
        return destinatarioUsername;
    }

    public void setDestinatarioUsername(String destinatarioUsername) {
        this.destinatarioUsername = destinatarioUsername;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public boolean isLetta() {
        return letta;
    }

    public void setLetta(boolean letta) {
        this.letta = letta;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    @Override
    public String toString() {
        return "Notifica{" +
                "id=" + id +
                ", destinatario='" + destinatarioUsername + '\'' +
                ", messaggio='" + messaggio + '\'' +
                ", letta=" + letta +
                ", data=" + dataCreazione +
                '}';
    }
}
