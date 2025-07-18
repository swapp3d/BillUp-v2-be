package com.example.BillUp.repositories;

import com.example.BillUp.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    @Query("SELECT t from Token t WHERE t.user.id = :userId AND t.revoked = false ")
    List<Token> findAllValidTokensOfUser(Long userId);

}
