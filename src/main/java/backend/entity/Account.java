package backend.entity;

import jakarta.persistence.*;
import modello.Ruolo;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @Column(length = 50)
    private String nomeUtente;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(length = 20)
    private String nome;

    @Column(length = 20)
    private String cognome;

    @Column(unique = true, length = 50)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo", nullable = false)
    private Ruolo ruolo;

    @Column(length = 50)
    private String avatar;

    // Costruttori
    public Account() {}

    public Account(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) {
        this.nomeUtente = nomeUtente;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
        this.avatar = avatar;
    }

    // Getter e Setter
    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Account{" +
                "nomeUtente='" + nomeUtente + '\'' +
                ", password='" + password + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", ruolo=" + ruolo +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
