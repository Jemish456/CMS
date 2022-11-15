package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.entity.StaffSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffSalaryRepository extends JpaRepository<StaffSalary,Long> {

    List<StaffSalary> findByStaffMember(StaffMember staffMember);

//    @Query("select staffMember from StaffSalary where MONTH(date)=:month AND staff_member_id=:id")
    @Query(value = "select * from staff_salary where MONTH(date)=:month and staff_member_id=:id ",nativeQuery = true)
    StaffSalary findByGivenMonthDate(@Param("month") int month, @Param("id") Long id);
}
