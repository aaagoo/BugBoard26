package backend.repository;

import backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByNomeUtente(String nomeUtente);
    boolean existsByNomeUtente(String nomeUtente);
    void deleteByNomeUtente(String nomeUtente);
}
