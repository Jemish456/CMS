package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StaffMemberService {

    List<StaffMember> findAll();

    void save(StaffMember staffMember);

    Optional<StaffMember> findById(Long staffMemberId);

    void deleteById(Long staffMemberId);

    long count();

    List<StaffMember> findAllByJoiningDateLessThan(Date date);

    int countByEmailAndIdNot(String email,Long id);

    int countByMobileNumberAndIdNot(String mobileNumber,Long id);

    List<StaffMember> findByJoiningDateLessThanEqual(Date date);

//    void setStaffAnnualLeaves(StaffMember staffMember, Date date);

    List<StaffMember> allMemberWithoutTaxSection(int financialYear);
}
