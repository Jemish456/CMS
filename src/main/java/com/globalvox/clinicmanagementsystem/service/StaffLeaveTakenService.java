package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaveTaken;

import java.util.Date;
import java.util.List;

public interface StaffLeaveTakenService {

    void save(StaffLeaveTaken staffLeaveTaken);

    List<StaffLeaveTaken> findAllByStaffMemberId(Long id);

//    int getStaffLeavesOfSalaryMonth(Date fromDate,Date toDate,Long id);
//
//    int getStaffLeaveOfOnlySalaryMonth(Date fromAfter,Date fromBefore,Date toAfter,Long id);
//
//    int getStaffLeaveOfOnlySalaryMonth2(Date fromBefore,Date toAfter,Date toBefore,Long id);

    double getLeaveCount(Date from,Date to,Long id);

    boolean isDateAvailable(Long id,Date fromDate,Date toDate);
}
