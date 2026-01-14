package modello;

import java.time.LocalDateTime;

public class Issue {

    private Long id;
    private String titolo;
    private String descrizione;
    private Priorita priorita;
    private Tipo tipo;
    private String immagineUrl;
    private String creatoreUsername;
    private String assegnatarioUsername;
    private LocalDateTime dataCreazione;
    private boolean risolto = false;
    private LocalDateTime dataRisoluzione;

    public Issue() {}

    public Issue(String titolo, String descrizione, Priorita priorita, Tipo tipo, String creatoreUsername) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.priorita = priorita;
        this.tipo = tipo;
        this.creatoreUsername = creatoreUsername;
        this.dataCreazione = LocalDateTime.now();
        this.risolto = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public Priorita getPriorita() { return priorita; }
    public void setPriorita(Priorita priorita) { this.priorita = priorita; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public String getImmagineUrl() { return immagineUrl; }
    public void setImmagineUrl(String immagineUrl) { this.immagineUrl = immagineUrl; }

    public String getCreatoreUsername() { return creatoreUsername; }
    public void setCreatoreUsername(String creatoreUsername) { this.creatoreUsername = creatoreUsername; }

    public String getAssegnatarioUsername() { return assegnatarioUsername; }
    public void setAssegnatarioUsername(String assegnatarioUsername) { this.assegnatarioUsername = assegnatarioUsername; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public boolean isRisolto() { return risolto; }
    public void setRisolto(boolean risolto) { this.risolto = risolto; }

    public LocalDateTime getDataRisoluzione() { return dataRisoluzione; }
    public void setDataRisoluzione(LocalDateTime dataRisoluzione) { this.dataRisoluzione = dataRisoluzione; }
}
