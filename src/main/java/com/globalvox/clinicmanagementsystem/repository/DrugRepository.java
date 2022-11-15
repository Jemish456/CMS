package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrugRepository extends JpaRepository<Drug, Long> {
    Drug findDrugById(Long id);

    int countByNameAndPower(String name,String power);

    int countByNameAndPowerAndIdNot(String name,String power, Long id);

    List<Drug> findAllByOrderByName();
}
