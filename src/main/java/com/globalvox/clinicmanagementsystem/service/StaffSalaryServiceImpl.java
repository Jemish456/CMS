package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.entity.StaffSalary;
import com.globalvox.clinicmanagementsystem.repository.StaffSalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffSalaryServiceImpl implements StaffSalaryService {

    @Autowired
    private StaffSalaryRepository staffSalaryRepository;

    @Override
    public List<StaffSalary> staffSalary() {
        return staffSalaryRepository.findAll();
    }

    @Override
    public List<StaffSalary> findByStaffMember(StaffMember staffMember) {
        return staffSalaryRepository.findByStaffMember(staffMember);
    }

    @Override
    public void save(StaffSalary staffSalary) {
        staffSalaryRepository.save(staffSalary);
    }

    @Override
    public StaffSalary findByGivenMonthDate(int month, Long id) {
        return staffSalaryRepository.findByGivenMonthDate(month,id);
    }
}
