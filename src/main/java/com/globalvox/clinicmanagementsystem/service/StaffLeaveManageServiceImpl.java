package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaveManage;
import com.globalvox.clinicmanagementsystem.repository.StaffLeaveManageRepository;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffLeaveManageServiceImpl implements StaffLeaveManageService {

    @Autowired
    private StaffLeaveManageRepository staffLeaveManageRepository;

    @Override
    public void save(StaffLeaveManage staffLeaveManage) {
        int year= ServiceUtil.getYear(staffLeaveManage.getEffectiveDate());
        int countActiveFalse=staffLeaveManageRepository.countByActiveFalse();
        int countActiveTrue=staffLeaveManageRepository.countByActiveTrue();

        /* If staff leave manage is empty then create new leave manage */
        if(countActiveTrue==0){
            staffLeaveManage.setYear(year);
            staffLeaveManage.setActive(false);
            staffLeaveManageRepository.save(staffLeaveManage);
        }else {
            /* if one leave manage is exists with true active status
                then check the leave manage with false active status*/
            if(countActiveFalse==0){
                staffLeaveManage.setYear(year);
                staffLeaveManage.setActive(false);
                staffLeaveManageRepository.save(staffLeaveManage);
            }else {
                Optional<StaffLeaveManage> staffLeaveManageOptional = staffLeaveManageRepository.findByActiveFalse();
                staffLeaveManageOptional.get().setYear(year);
                staffLeaveManageOptional.get().setCarryForwardExpiry(staffLeaveManage.getCarryForwardExpiry());
                staffLeaveManageOptional.get().setPaidLeaves(staffLeaveManage.getPaidLeaves());
                staffLeaveManageOptional.get().setEffectiveDate(staffLeaveManage.getEffectiveDate());
                staffLeaveManageRepository.save(staffLeaveManageOptional.get());
            }
        }
    }

    @Override
    public Optional<StaffLeaveManage> findByYear(int year) {
        return staffLeaveManageRepository.findByYear(year);
    }

    @Override
    public List<StaffLeaveManage> findAll() {
        return staffLeaveManageRepository.findAll();
    }

    @Override
    public Optional<StaffLeaveManage> findByActiveFalse() {
        return staffLeaveManageRepository.findByActiveFalse();
    }
}
