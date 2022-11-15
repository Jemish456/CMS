package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.ClinicServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicServicesRepository extends JpaRepository<ClinicServices,Long> {
    List<ClinicServices> findAll();
    ClinicServices findByName(String name);

    @Query("select name from ClinicServices ")
    List<String> getServiceNames();
}
