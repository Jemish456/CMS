package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Inventory;
import com.globalvox.clinicmanagementsystem.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem,Long> {

    List<StockItem> findAllByInventoryEqualsAndQtyGreaterThanOrderByExpiryDateAsc(Inventory inventory,Integer qty);
}
