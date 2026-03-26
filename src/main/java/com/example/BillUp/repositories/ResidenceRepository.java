package com.example.BillUp.repositories;

import com.example.BillUp.entities.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResidenceRepository extends JpaRepository<Residence, Long> {

    List<Residence> findByUserId(Long userId);

    List<Residence> findByStreetAddressContainingIgnoreCase(String street);

    List<Residence> findAllByActiveTrue();

    @Query("SELECT r FROM Residence r WHERE r.active = true AND LOWER(r.streetAddress) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Residence> searchByAddress(@Param("query") String query);

    @Query(value = "SELECT * FROM residences WHERE id = :id", nativeQuery = true)
    Optional<Residence> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM residences WHERE user_id = :userId", nativeQuery = true)
    List<Residence> findByUserIdIncludingDeleted(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM residences WHERE user_id = :userId AND is_primary = true LIMIT 1", nativeQuery = true)
    Optional<Residence> findPrimaryByUserIdIncludingDeleted(@Param("userId") Long userId);

}