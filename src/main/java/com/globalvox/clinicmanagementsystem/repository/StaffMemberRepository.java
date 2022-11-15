package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember,Long> {

    Optional<StaffMember> findById(Long staffMemberId);

    List<StaffMember> findAll();

    List<StaffMember> findAllByJoiningDateLessThanEqual(Date doj);

    void deleteById(Long staffMemberId);

    long count();

    List<StaffMember> findAllByJoiningDateLessThan(Date date);

    int countByEmailAndIdNot(String email,Long id);

    int countByMobileNumberAndIdNot(String mobileNumber,Long id);

    List<StaffMember> findByJoiningDateLessThanEqual(Date date);

    @Query(
            value = "select min(joining_date) from staff_member",
            nativeQuery = true
    )
    Date findMinJoiningDate();

    @Query(value = "select * from staff_member as sm  where sm.id not in (select tax_section.staff_member_id from tax_section where tax_section.financial_year = :financialYear)"
    ,nativeQuery = true)
    List<StaffMember> listOfStaffMemberWithoutTaxSystem(int financialYear);

    @Query(
            value = "select * from staff_member " +
                    "where " +
                    "    ((MONTH(joining_date) < :month) && (YEAR(joining_date) = :year)) ",
            nativeQuery = true
    )
    List<StaffMember> getStaffMembers(int month,int year);
}
