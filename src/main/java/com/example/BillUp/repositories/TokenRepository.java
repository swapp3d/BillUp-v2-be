package com.example.BillUp.repositories;

import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUserAndRevokedFalse(User user);
    List<Token> findAllByUserAndRevokedFalse(User user);
    List<Token> findAllByUserAndRevokedFalseAndTokenType(User user, TokenType tokenType);
}
