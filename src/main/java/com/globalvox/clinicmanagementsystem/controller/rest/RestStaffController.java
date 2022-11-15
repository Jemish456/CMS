package com.globalvox.clinicmanagementsystem.controller.rest;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaveTaken;
import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.repository.StaffLeaveTakenRepository;
import com.globalvox.clinicmanagementsystem.repository.StaffMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
public class RestStaffController {

    @Autowired
    private StaffLeaveTakenRepository staffLeaveTakenRepository;

    @Autowired
    private StaffMemberRepository staffMemberRepository;

    @GetMapping("/attendance")
    public ResponseEntity<?> getAttendance(@RequestParam("day") String day,
                                           @RequestParam("month") String month,
                                           @RequestParam("year") String year)
            throws ParseException {

        String s = day+"/"+month+"/"+year;
        Date joiningDate = new SimpleDateFormat("dd/MM/yyyy").parse(s);

        //getting data of members by joining date
        List<StaffMember> staffMemberList = staffMemberRepository.findAllByJoiningDateLessThanEqual(joiningDate);

        List<HashMap<String,Object>> attendanceList = new ArrayList<>();

        for(StaffMember staffMember : staffMemberList) {

            HashMap<String,Object> hashMap = new HashMap<>();

            Long id = staffMember.getId();

            //getting list of leaves through passing joining month and year and id of the staffmember
            List<StaffLeaveTaken> staffLeaveTakenList =
                    staffLeaveTakenRepository.getDataById(Integer.parseInt(month),Integer.parseInt(year),id);

            hashMap.put("id",staffMember.getId());
            hashMap.put("name",staffMember.getFirstName().toUpperCase() + " " + staffMember.getLastName().toUpperCase());
            hashMap.put("joiningDate",staffMember.getJoiningDate());

            ArrayList<Integer> absentList = new ArrayList<>();

            //if there are leave taken
            if(staffLeaveTakenList.size() != 0){

                for (StaffLeaveTaken leave: staffLeaveTakenList) {

                    //converting date object into localdate
                    LocalDate start = Instant
                            .ofEpochMilli(leave.getLeaveFromDate().getTime())
                                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    LocalDate end = Instant
                            .ofEpochMilli(leave.getLeaveToDate().getTime())
                                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    //loop from before date and end date
                    for (LocalDate date = start;
                         ((date.isBefore(end) || date.isEqual(end)));
                         date = date.plusDays(1)) {

                        if((date.getMonthValue() == Integer.parseInt(month))){
                            absentList.add(date.getDayOfMonth());
                        } else {
                            continue;
                        }
                    }
                }
            }
            else {
                absentList.add(0);
            }

            hashMap.put("absentList",absentList);

            attendanceList.add(hashMap);
        }

        return  ResponseEntity.ok(attendanceList);
    }

    @GetMapping("/getFirstJoiningDate")
    public ResponseEntity<?> getFirstJoiningDate() {
        Date date = staffMemberRepository.findMinJoiningDate();
        return  ResponseEntity.ok(date);

    }

}
