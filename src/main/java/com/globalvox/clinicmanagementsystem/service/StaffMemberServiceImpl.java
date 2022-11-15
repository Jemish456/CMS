package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.repository.StaffMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class   StaffMemberServiceImpl implements StaffMemberService{

    @Autowired
    private StaffMemberRepository staffMemberRepository;

    @Override
    public List<StaffMember> findAll() {
        return staffMemberRepository.findAll();
    }

    @Override
    public void save(StaffMember staffMember) {
        if(staffMember.getId() == 0){
            StaffMember member=staffMemberRepository.save(staffMember);
            setStaffAnnualLeaves(member,member.getJoiningDate());
        }
        else {
            staffMemberRepository.save(staffMember);
        }
    }

    @Override
    public Optional<StaffMember> findById(Long staffMemberId) {
        return staffMemberRepository.findById(staffMemberId);
    }

    @Override
    public void deleteById(Long staffMemberId) {
        staffMemberRepository.deleteById(staffMemberId);
    }

    @Override
    public long count() {
        return staffMemberRepository.count();
    }

    @Override
    public List<StaffMember> findAllByJoiningDateLessThan(Date date) {
        return staffMemberRepository.findAllByJoiningDateLessThan(date);
    }

    @Override
    public int countByEmailAndIdNot(String email, Long id) {
        return staffMemberRepository.countByEmailAndIdNot(email,id);
    }

    @Override
    public int countByMobileNumberAndIdNot(String mobileNumber, Long id) {
        return staffMemberRepository.countByMobileNumberAndIdNot(mobileNumber,id);
    }

    @Override
    public List<StaffMember> findByJoiningDateLessThanEqual(Date date) {
        return staffMemberRepository.findByJoiningDateLessThanEqual(date);
    }


    private void setStaffAnnualLeaves(StaffMember staffMember, Date date){

    }

    @Override
    public List<StaffMember> allMemberWithoutTaxSection(int financialYear) {
        return staffMemberRepository.listOfStaffMemberWithoutTaxSystem(financialYear);
    }

}
