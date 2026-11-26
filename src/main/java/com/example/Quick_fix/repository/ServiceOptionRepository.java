package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ServiceOptionEntity;

@Repository
public interface ServiceOptionRepository extends JpaRepository<ServiceOptionEntity, Long> {
    List<ServiceOptionEntity> findByGroup_IdAndActiveTrue(Long groupId);
    List<ServiceOptionEntity> findByGroup_Id(Long groupId);
}
