package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    Inventory findAllById(Long id);

    List<Inventory> findAllByIdNotIn(Collection<Long> idList);
}
