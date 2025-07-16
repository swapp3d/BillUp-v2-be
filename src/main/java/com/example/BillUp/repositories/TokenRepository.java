package com.example.BillUp.repositories;

import ch.qos.logback.core.subst.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    @Query("SELECT t from Token t WHERE t.user.id = :userId AND t.revoked = false ")
    List<Token> findAllValidTokensOfUser(Long userId);
}
