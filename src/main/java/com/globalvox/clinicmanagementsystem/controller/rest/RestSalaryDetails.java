package com.globalvox.clinicmanagementsystem.controller.rest;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.service.StaffLeaveTakenService;
import com.globalvox.clinicmanagementsystem.service.StaffMemberService;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RestSalaryDetails {

    @Autowired
    private StaffMemberService staffMemberService;

    @Autowired
    private StaffLeaveTakenService staffLeaveTakenService;

    @RequestMapping(value = "/salaryDetails",method = RequestMethod.POST)
    public List<Double> showSalaryDetails(@RequestParam("id") Long id,@RequestParam("month")int month) throws ParseException {

        return salaryDetails(id,month);

    }

    /* Calculating Salary details */
    private List<Double> salaryDetails(long id,int month) throws ParseException {
        Double workingDay=0.0;
        Double totalLeave=0.0;
        Double salaryPerDay=0.0;
        List<Double> salaryDetail = null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date randomDate=ServiceUtil.getRandomDateOfMonth(month);
        Date firstDateOfMonth=ServiceUtil.getGivenMonthFirstDate(randomDate);
        Date lastDateOfMonth=ServiceUtil.getGivenMonthLastDate(randomDate);

        /* Taking Today date for Calculation */
        Date todayDate = new Date();
        Date today = ServiceUtil.getDateWithFormat(sdf.parse(sdf.format(todayDate)));
//        Date today = ServiceUtil.getDateWithFormat(sdf.parse("31/05/2022"));
        int totalDayOfMonth = ServiceUtil.getCurrentMonthDays(lastDateOfMonth);



        Optional<StaffMember> staffMember = staffMemberService.findById(id);
        /* Check Member Exist or not */
        if (staffMember.isPresent()){
            Date joiningDate = staffMember.get().getJoiningDate();
            int joiningMonth  = ServiceUtil.getMonth(joiningDate);

            salaryPerDay = staffMember.get().getSalary()/totalDayOfMonth;

            if (month == joiningMonth){
            /* Check current month and joining month is sem or not */

                if (joiningMonth == ServiceUtil.getMonth(today)){
                    /* joining month is same and today date  same or not base on that calculate working day and leave */
                    totalLeave =  staffLeaveTakenService.getLeaveCount(firstDateOfMonth,today,id);
                    workingDay = ServiceUtil.getDaysBetweenDates(joiningDate,today)-totalLeave;
                }else {
                    totalLeave =  staffLeaveTakenService.getLeaveCount(firstDateOfMonth,lastDateOfMonth,id);
                    workingDay = ServiceUtil.getDaysBetweenDates(joiningDate,lastDateOfMonth)-totalLeave;
                }
            }
            else if (joiningMonth>month){
                /* future joining Date*/
                totalDayOfMonth = 0;
                totalLeave = 0.0;
                workingDay = 0.0;
                salaryPerDay = 0.0;
            }
            else if (today.compareTo(lastDateOfMonth)<=0){
                /* checking before Joining date */
                totalLeave =  staffLeaveTakenService.getLeaveCount(firstDateOfMonth,today,id);
                workingDay = ServiceUtil.getDaysBetweenDates(firstDateOfMonth,today)-totalLeave;
                if (today.compareTo(firstDateOfMonth)<=0){
                    totalDayOfMonth = 0;
                    totalLeave = 0.0;
                    workingDay = 0.0;
                    salaryPerDay = 0.0;
                }
            }
            else {
                totalLeave = staffLeaveTakenService.getLeaveCount(firstDateOfMonth,lastDateOfMonth,id);
                workingDay = totalDayOfMonth-totalLeave;

            }

            /* Creating Rest Response and Returning response */
            salaryDetail = new ArrayList<>();
            salaryDetail.add((double) totalDayOfMonth);
            salaryDetail.add(workingDay);
            salaryDetail.add(ServiceUtil.round(salaryPerDay,2));
            salaryDetail.add(totalLeave);
            return salaryDetail;
        }
    return null;
    }
}