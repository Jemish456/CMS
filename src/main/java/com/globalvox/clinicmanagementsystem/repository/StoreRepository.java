package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Store;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends BaseUserRepository<Store> {
    Store findByEmail(String email);


}
