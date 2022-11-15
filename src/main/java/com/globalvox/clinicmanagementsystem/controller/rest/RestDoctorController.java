package com.globalvox.clinicmanagementsystem.controller.rest;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.repository.DoctorRepository;
import com.globalvox.clinicmanagementsystem.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class RestDoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorRepository doctorRepository;

    @PostMapping(value = "/slots",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getSlots(@RequestParam(name = "doctorId",required = false,defaultValue = "0") Long doctorId,
                                 @RequestParam(name = "appointmentDate",required = false,defaultValue = "") String appointmentDate, HttpServletRequest request) {
        List<String> timeSlots = new ArrayList<>();
        try {
            Date newAppointmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(appointmentDate);

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date today = new Date();
            Date todayWithZeroTime = formatter.parse(formatter.format(today));

            // appointment booking date must be greater than current date
            if(newAppointmentDate.compareTo(todayWithZeroTime) >= 0){
                Optional<Doctor> doctor = doctorService.findById(doctorId);
                if (doctor.isPresent()){
                    timeSlots.addAll(doctorService.getTimeSlots(doctor.get(),newAppointmentDate));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return timeSlots;
    }

    @GetMapping(value = "",produces = "application/json")
    public List<Doctor> getDoctor(@RequestParam(name = "search",required = false,defaultValue = "") String search){
        return doctorRepository.search(search);
    }

}
