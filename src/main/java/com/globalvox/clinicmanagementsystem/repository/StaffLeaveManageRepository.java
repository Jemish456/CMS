package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaveManage;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.DecimalMax;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffLeaveManageRepository extends JpaRepository<StaffLeaveManage,Long> {
    Optional<StaffLeaveManage> findByYear(int year);
    int countByActiveFalse();
    int countByActiveTrue();
    Optional<StaffLeaveManage> findByActiveFalse();
}
