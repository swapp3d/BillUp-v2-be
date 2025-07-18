package com.example.BillUp.repositories;

import com.example.BillUp.entities.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResidenceRepository extends JpaRepository<Residence, Long> {

    List<Residence> findByUserId(Long userId);

    @Query("SELECT r FROM Residence r WHERE LOWER(r.streetAddress) LIKE LOWER(CONCAT(:query, '%'))")
    List<Residence> searchByAddress(@Param("query") String query);

}
