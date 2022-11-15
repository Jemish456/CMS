package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaves;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffLeavesRepository  extends JpaRepository<StaffLeaves,Long> {

    StaffLeaves findByStaffMemberId(Long id);
    Optional<StaffLeaves> findByStaffMemberIdAndYear(Long id, int year);
}
