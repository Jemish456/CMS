package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.entity.StaffSalary;
import com.globalvox.clinicmanagementsystem.repository.StaffSalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface StaffSalaryService {

    List<StaffSalary> staffSalary();

    List<StaffSalary> findByStaffMember(StaffMember staffMember);

    void save(StaffSalary staffSalary);

    StaffSalary findByGivenMonthDate(int month,Long id);
}
