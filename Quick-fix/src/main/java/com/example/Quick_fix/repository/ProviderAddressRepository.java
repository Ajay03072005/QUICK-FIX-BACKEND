package com.example.Quick_fix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ProviderAddressEntity;

@Repository
public interface ProviderAddressRepository extends JpaRepository<ProviderAddressEntity,Integer > {

}
