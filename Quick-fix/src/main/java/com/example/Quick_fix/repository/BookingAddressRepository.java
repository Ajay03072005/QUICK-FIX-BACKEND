package com.example.Quick_fix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.BookingAddressEntity;

@Repository
public interface BookingAddressRepository
        extends JpaRepository<BookingAddressEntity, Long> {

    Optional<BookingAddressEntity> findByUniqueId(String uniqueId);

    boolean existsByUniqueId(String uniqueId);
}