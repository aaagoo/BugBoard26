package backend.service;

import backend.entity.Account;
import backend.repository.AccountRepository;
import modello.Ruolo;
import modello.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Utente login(String username, String password) {
        Optional<Account> accountOpt = accountRepository.findByNomeUtente(username);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getPassword().equals(password)) {
                return mapToUtente(account);
            }
        }
        return null;
    }

    public String generateToken(Utente utente) {
        return "TOKEN_" + utente.getNomeUtente() + "_" + System.currentTimeMillis();
    }

    @Transactional
    public String creaAccount(String nomeUtente, String password, String nome, String cognome, String email, Ruolo ruolo, String avatar) {
        if (accountRepository.existsByNomeUtente(nomeUtente)) {
            return "Errore: Nome utente già esistente";
        }

        Account account = new Account();
        account.setNomeUtente(nomeUtente);
        account.setPassword(password);
        account.setNome(nome);
        account.setCognome(cognome);
        account.setEmail(email);
        account.setRuolo(ruolo);
        account.setAvatar(avatar);

        accountRepository.save(account);
        return "Account creato con successo";
    }


    public List<Map<String, Object>> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToMap)
                .collect(Collectors.toList());
    }

    public Utente getUtente(String nomeUtente) {
        return accountRepository.findByNomeUtente(nomeUtente)
                .map(this::mapToUtente)
                .orElse(null);
    }

    @Transactional
    public String modificaAccount(String nomeUtente, String password, String nome, String cognome, String email, String avatar) {
        Optional<Account> accountOpt = accountRepository.findByNomeUtente(nomeUtente);
        if (accountOpt.isEmpty()) {
            return "Errore: Account non trovato";
        }

        Account account = accountOpt.get();
        if (password != null && !password.isEmpty()) {
            account.setPassword(password);
        }
        if (nome != null && !nome.isEmpty()) account.setNome(nome);
        if (cognome != null && !cognome.isEmpty()) account.setCognome(cognome);
        if (email != null && !email.isEmpty()) account.setEmail(email);
        if (avatar != null && !avatar.isEmpty()) account.setAvatar(avatar);

        accountRepository.save(account);
        return "Account modificato con successo";
    }

    @Transactional
    public String eliminaAccount(String nomeUtente) {
        if (!accountRepository.existsByNomeUtente(nomeUtente)) {
            return "Errore: Account non trovato";
        }
        accountRepository.deleteByNomeUtente(nomeUtente);
        return "Account eliminato con successo";
    }

    private Utente mapToUtente(Account account) {
        return new Utente(
                account.getNomeUtente(),
                account.getPassword(),
                account.getNome(),
                account.getCognome(),
                account.getEmail(),
                account.getRuolo(),
                account.getAvatar()
        );
    }

    private Map<String, Object> mapToMap(Account account) {
        Map<String, Object> map = new HashMap<>();
        map.put("nomeUtente", account.getNomeUtente());
        map.put("password", account.getPassword());
        map.put("nome", account.getNome());
        map.put("cognome", account.getCognome());
        map.put("email", account.getEmail());
        map.put("ruolo", account.getRuolo().name());
        map.put("avatar", account.getAvatar());
        return map;
    }
}
