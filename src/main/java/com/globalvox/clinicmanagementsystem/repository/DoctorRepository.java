package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.dto.DoctorReviewDTO;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends BaseUserRepository<Doctor> {

    Doctor findByEmail(String email);

    Doctor findByMobileNumber(String mobileNumber);

    int countByEmail(String email);

    int countById(Long Id);

    int countByMobileNumber(String mobileNumber);

    Doctor findByVerificationCode(String code);

    int countByEmailAndIdNot(String email,Long id);

    int countByMobileNumberAndIdNot(String mobileNumber,Long id);

    Doctor findByResetPasswordToken(String token);

    @Query("SELECT d FROM Doctor d WHERE CONCAT(d.firstName,d.middleName,d.lastName,d.email) LIKE %?1%")
    List<Doctor> search(String keyword);

    //make constructor of doctorreviewdto which contain rating
    @Query(value = "select new com.globalvox.clinicmanagementsystem.dto.DoctorReviewDTO" +
            "(d.id,d.firstName,d.lastName, d.email,d.mobileNumber,avg(r.rating))" +
            " from Doctor d left join Review r on d.id = r.doctor.id group by " +
            "d.id")
    List<DoctorReviewDTO> getDoctorAndAvgRating();

    //make constructor of doctorreviewdto which contain rating and match 4 fields with keyword
    @Query(value = "select new com.globalvox.clinicmanagementsystem.dto.DoctorReviewDTO" +
        "(d.id,d.firstName,d.lastName, d.email,d.mobileNumber,avg(r.rating))" +
        " from Doctor d left join Review r on d.id = r.doctor.id " +
        "where lower(d.firstName) like %?1% or" +
        " lower(d.lastName) like %?1% or lower(d.email) like %?1% or lower(d.mobileNumber) like %?1% group by " +
        "d.id")
        Page<DoctorReviewDTO> getDoctorandRatingInSearch(@Param("keywords") String keywords, Pageable pageable);

    long count();

}
