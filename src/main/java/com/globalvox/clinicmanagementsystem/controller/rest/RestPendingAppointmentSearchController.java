package com.globalvox.clinicmanagementsystem.controller.rest;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.service.AppointmentService;
import com.globalvox.clinicmanagementsystem.service.DoctorService;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RestPendingAppointmentSearchController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @CrossOrigin(value = "*")
    @PostMapping("/doctor/search")
    public ResponseEntity<?> search(@RequestParam(value = "keywords",required = false,defaultValue="") String keyword,
                                    @RequestParam("page") Integer page,
                                    @RequestParam("size") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        Page<Appointment> appointmentList = appointmentService.findByKeyword(keyword,doctor.get(),pageable);
        return ResponseEntity.ok(appointmentList);
    }
}