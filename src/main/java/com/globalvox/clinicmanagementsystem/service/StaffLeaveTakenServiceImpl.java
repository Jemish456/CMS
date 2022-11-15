package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaveTaken;
import com.globalvox.clinicmanagementsystem.entity.enums.LeaveType;
import com.globalvox.clinicmanagementsystem.repository.StaffLeaveTakenRepository;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class StaffLeaveTakenServiceImpl implements StaffLeaveTakenService {

    @Autowired
    private StaffLeaveTakenRepository staffLeaveTakenRepository;

    @Override
    public void save(StaffLeaveTaken staffLeaveTaken) {
        staffLeaveTakenRepository.save(staffLeaveTaken);
    }

    @Override
    public List<StaffLeaveTaken> findAllByStaffMemberId(Long id) {
        return staffLeaveTakenRepository.findAllByStaffMemberId(id);
    }


    /* For leave from and to date is in salary month */
    private double getStaffLeavesOfSalaryMonth(Date from,Date to,Long id){
        List<StaffLeaveTaken> leaveTakenList=staffLeaveTakenRepository.findByLeaveFromDateGreaterThanEqualAndLeaveToDateLessThanEqualAndStaffMemberId(from,to,id);
        double count=0;
        double tempCount;
        for(StaffLeaveTaken leaveTaken:leaveTakenList){
            tempCount=leaveTaken.getDays();
            if(leaveTaken.getLeaveType().toString().equals(LeaveType.Half.toString())){
                count+=tempCount/2;
            }else{
                count+=tempCount;
            }
        }
        return count;
    }

    /* For leave from date is in salary month and leave to date is in next month */
    private double getStaffLeaveOfOnlySalaryMonth(Date fromDate, Date toDate, Date toAfter, Long id) {
        List<StaffLeaveTaken> leaveTakenList=staffLeaveTakenRepository.findByLeaveFromDateGreaterThanEqualAndLeaveFromDateLessThanEqualAndLeaveToDateGreaterThanAndStaffMemberId(fromDate,toDate,toAfter,id);
        double count=0;
        double tempCount;
        for (StaffLeaveTaken leaveTaken:leaveTakenList){
            tempCount = ServiceUtil.getDaysBetweenDates(leaveTaken.getLeaveFromDate(),toDate);
            if(leaveTaken.getLeaveType().toString().equals(LeaveType.Half.toString())){
                count+=tempCount/2;
            }else{
                count+=tempCount;
            }
        }
        return count;
    }


    /* For leave from date is in previous month and also check  leave to date is in salary month or later */
    private double getStaffLeaveOfOnlySalaryMonth(Date fromDate,Date toDate, Long id) {
        List<StaffLeaveTaken> leaveTakenList=staffLeaveTakenRepository.findByLeaveFromDateLessThanAndLeaveToDateGreaterThanEqualAndStaffMemberId(fromDate,fromDate,id);
        double count=0;
        double tempCount;
        for (StaffLeaveTaken leaveTaken:leaveTakenList){
            if(leaveTaken.getLeaveToDate().compareTo(toDate) > 0)
                tempCount=ServiceUtil.getDaysBetweenDates(fromDate,toDate);
            else {
                tempCount=ServiceUtil.getDaysBetweenDates(fromDate,leaveTaken.getLeaveToDate());
            }
            if(leaveTaken.getLeaveType().toString().equals(LeaveType.Half.toString())){
                count+=tempCount/2;
            }else{
                count+=tempCount;
            }
        }
        return count;
    }

    @Override
    public double getLeaveCount(Date from, Date to, Long id) {
        double count=getStaffLeavesOfSalaryMonth(from,to,id)
                    + getStaffLeaveOfOnlySalaryMonth(from,to,to,id)
                    +getStaffLeaveOfOnlySalaryMonth(from,to,id);
        return count;
    }

    @Override
    public boolean isDateAvailable(Long id, Date fromDate, Date toDate) {
        return staffLeaveTakenRepository.isDateAvailable(id,fromDate,toDate);
    }
}
