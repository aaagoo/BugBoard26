package modello;

public class Account {

    private String nomeUtente;
    private String password;
    private String nome;
    private String cognome;
    private String email;
    private Ruolo ruolo;
    private String avatar;
    private Integer issueAssegnate;

    public Account() {}

    public Account(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar, Integer issueAssegnate) {
        this.nomeUtente = nomeUtente;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
        this.avatar = avatar;
        this.issueAssegnate = issueAssegnate;
    }

    public String getNomeUtente() { return nomeUtente; }
    public void setNomeUtente(String nomeUtente) { this.nomeUtente = nomeUtente; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getIssueAssegnate() { return issueAssegnate; }
    public void setIssueAssegnate(Integer issueAssegnate) { this.issueAssegnate = issueAssegnate; }
}