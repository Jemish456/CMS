package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaveManage;

import java.util.List;
import java.util.Optional;

public interface StaffLeaveManageService {
    void save(StaffLeaveManage staffLeaveManage);
    Optional<StaffLeaveManage> findByYear(int year);
    List<StaffLeaveManage> findAll();
    Optional<StaffLeaveManage> findByActiveFalse();
}
