package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.entity.TaxSection;

import java.util.List;
import java.util.Optional;

public interface TaxSectionService {

    void save(TaxSection taxSection);

    Optional<TaxSection> findByStaffMember(StaffMember staffMember);

    List<TaxSection> allMemberWithTaxSectionInCurrentYear(int financialYear);

    List<TaxSection> findAll();
}
