package com.globalvox.clinicmanagementsystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class DoctorReviewPaginationDTO {

    Page<DoctorReviewDTO> doctorList;
    Integer totalPages,currentPage;

    public DoctorReviewPaginationDTO() {
    }

    public DoctorReviewPaginationDTO(Page<DoctorReviewDTO> doctorList, Integer pages, Integer currentPage) {
        this.doctorList = doctorList;
        this.totalPages = pages;
        this.currentPage = currentPage;
    }
}
