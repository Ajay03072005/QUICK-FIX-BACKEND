package com.example.Quick_fix.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Quick_fix.Entity.ServiceGuidanceItemEntity;

@Repository
public interface ServiceGuidanceItemRepository extends JpaRepository<ServiceGuidanceItemEntity, Long> {
    List<ServiceGuidanceItemEntity> findByGuidance_Id(Long guidanceId);
    List<ServiceGuidanceItemEntity> findByGuidance_IdAndItemType(Long guidanceId, String itemType);
}
