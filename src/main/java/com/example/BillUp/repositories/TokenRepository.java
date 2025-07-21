package com.example.BillUp.repositories;

import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUserAndRevokedFalse(User user);
}
