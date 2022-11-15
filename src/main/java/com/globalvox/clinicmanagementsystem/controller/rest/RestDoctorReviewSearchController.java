package com.globalvox.clinicmanagementsystem.controller.rest;

import com.globalvox.clinicmanagementsystem.dto.DoctorReviewDTO;
import com.globalvox.clinicmanagementsystem.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RestDoctorReviewSearchController {

    @Autowired
    private DoctorRepository doctorRepository;

    @CrossOrigin(value = "*")
    @PostMapping("/search/")
    public ResponseEntity<?> search(@RequestParam(value = "keywords",required = false,defaultValue="") String keywords,
                                    @RequestParam Integer page,
                                    @RequestParam Integer size){

        //defines page number and number of rows per page
        Pageable pageable = PageRequest.of(page,size);

        //get all the doctor and their avg rating according to keyword according to given page and size
        Page<DoctorReviewDTO> doctorList = this.doctorRepository.getDoctorandRatingInSearch(keywords,pageable);

        return ResponseEntity.ok(doctorList);
    }


}
