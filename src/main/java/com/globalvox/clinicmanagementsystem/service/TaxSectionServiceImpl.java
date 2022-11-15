package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.entity.TaxSection;
import com.globalvox.clinicmanagementsystem.repository.StaffMemberRepository;
import com.globalvox.clinicmanagementsystem.repository.TaxSectionRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaxSectionServiceImpl implements TaxSectionService {

    @Autowired
    private TaxSectionRespository taxSectionRespository;


    @Override
    public void save(TaxSection taxSection) {
        taxSectionRespository.save(taxSection);
    }

    @Override
    public Optional<TaxSection> findByStaffMember(StaffMember staffMember) {
        return taxSectionRespository.findByStaffMember(staffMember);
    }

    @Override
    public List<TaxSection> allMemberWithTaxSectionInCurrentYear(int financialYear) {
        return taxSectionRespository.allMemberWithTaxSectionInCurrentYear(financialYear);
    }

    @Override
    public List<TaxSection> findAll() {
        return taxSectionRespository.findAll();
    }

}
