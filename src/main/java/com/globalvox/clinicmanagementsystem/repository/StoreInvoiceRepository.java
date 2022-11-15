package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.StoreInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreInvoiceRepository extends JpaRepository<StoreInvoice,Long> {

    StoreInvoice findAllById(Long id);
}
