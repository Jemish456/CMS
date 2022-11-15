package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.entity.TaxSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaxSectionRespository extends JpaRepository<TaxSection,Long> {

    Optional<TaxSection> findByStaffMember(StaffMember staffMember);

    @Query(value = "select * from tax_section where tax_section.financial_year = :financialYear"
            ,nativeQuery = true)
    List<TaxSection> allMemberWithTaxSectionInCurrentYear(int financialYear);
}
