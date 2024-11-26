package it.nsa.gestione_autenticazione.model.repository;

import it.nsa.gestione_autenticazione.model.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {
    Optional<Jwt> findByToken(String token);
    void deleteByToken(String token);
}
