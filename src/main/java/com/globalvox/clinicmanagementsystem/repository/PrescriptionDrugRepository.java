package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Prescription;
import com.globalvox.clinicmanagementsystem.entity.PrescriptionDrug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionDrugRepository extends JpaRepository<PrescriptionDrug,Long> {
    List<PrescriptionDrug> findAllByPrescriptionId(Long id);

    Optional<PrescriptionDrug> findById(Long drugId);

    @Query("select medicineName from PrescriptionDrug where prescription=:prescription")
    List<String> getPrescriptionDrugName(Prescription prescription);

    int countByMorningDoseAndAfternoonDoseAndEveningDose(String morning,String afterNoon,String evening);
}
