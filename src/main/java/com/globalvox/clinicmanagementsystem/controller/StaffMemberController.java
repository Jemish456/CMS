package com.globalvox.clinicmanagementsystem.controller;
import com.globalvox.clinicmanagementsystem.dto.StaffMemberCreationDTO;
import com.globalvox.clinicmanagementsystem.dto.StaffMemberUpdateDTO;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.entity.enums.LeaveType;
import com.globalvox.clinicmanagementsystem.entity.enums.StaffMembers;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.repository.StaffLeaveTakenRepository;
import com.globalvox.clinicmanagementsystem.repository.StaffLeavesRepository;
import com.globalvox.clinicmanagementsystem.service.StaffLeaveTakenService;
import com.globalvox.clinicmanagementsystem.service.StaffMemberService;
import com.globalvox.clinicmanagementsystem.service.StaffSalaryService;
import com.globalvox.clinicmanagementsystem.service.TaxSectionService;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/staff")
public class StaffMemberController {

    @Autowired
    private StaffMemberService staffMemberService;

    @Autowired
    private StaffLeaveTakenRepository staffLeaveTakenRepository;

    @Autowired
    private StaffLeaveTakenService staffLeaveTakenService;

    @Autowired
    private StaffSalaryService staffSalaryService;

    @Autowired
    private StaffLeavesRepository staffLeavesRepository;

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaxSectionService taxSectionService;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    /* Add new staff member, with specific role */
    @GetMapping("/add")
    public String addStaffMember(Model model) {
        StaffMemberCreationDTO memberCreationDTO = new StaffMemberCreationDTO();
        model.addAttribute("member", memberCreationDTO);
        model.addAttribute("memberRole", StaffMembers.values());
        model.addAttribute("genders", Gender.values());
        return "staff/register";
    }

    /* If the email or mobile number is already registered, shows error while registration */
    @PostMapping("/add")
    public String addStaffMember(@Valid @ModelAttribute("member") StaffMemberCreationDTO staffMemberCreationDTO, BindingResult bindingResult, Model model) {
        int mailCount = staffMemberService.countByEmailAndIdNot(staffMemberCreationDTO.getEmail(), staffMemberCreationDTO.getId());
        int mobileNumberCount = staffMemberService.countByMobileNumberAndIdNot(staffMemberCreationDTO.getMobileNumber(), staffMemberCreationDTO.getId());

        if (mailCount > 0)
            bindingResult.addError(new FieldError("staffMember", "email", "Email Already Taken."));
        if (mobileNumberCount > 0)
            bindingResult.addError(new FieldError("staffMember", "mobileNumber", "MobileNumber Already Taken."));

        if (bindingResult.hasErrors()) {
            model.addAttribute("memberRole", StaffMembers.values());
            model.addAttribute("genders", Gender.values());
            return "staff/register";
        }
        StaffMember member = modelMapper.map(staffMemberCreationDTO, StaffMember.class);
        staffMemberService.save(member);
        return "redirect:/staff/list";
    }

    /* Edit particular staff member details*/
    @GetMapping("/edit")
    public String editStaffMember(@RequestParam("memberId") Long memberId, Model model) {
        Optional<StaffMember> member = staffMemberService.findById(memberId);
        if (member.isPresent()) {
            StaffMemberUpdateDTO staffMemberUpdateDTO = modelMapper.map(member.get(), StaffMemberUpdateDTO.class);
            model.addAttribute("member", staffMemberUpdateDTO);
            model.addAttribute("genders", Gender.values());
            model.addAttribute("memberRole", StaffMembers.values());
        } else {
            throw new NotFoundException("Member not found!");
        }
        return "staff/edit";
    }

    @PostMapping("/edit")
    public String editStaffMember(@Valid @ModelAttribute("member") StaffMemberUpdateDTO staffMemberUpdateDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        Optional<StaffMember> member = staffMemberService.findById(staffMemberUpdateDTO.getId());

        if (member.isPresent()) {
            int mailCount = staffMemberService.countByEmailAndIdNot(staffMemberUpdateDTO.getEmail(), staffMemberUpdateDTO.getId());
            int mobileNumberCount = staffMemberService.countByMobileNumberAndIdNot(staffMemberUpdateDTO.getMobileNumber(), staffMemberUpdateDTO.getId());

            if (mailCount > 0)
                bindingResult.addError(new FieldError("staffMember", "email", "Email Already Taken."));
            if (mobileNumberCount > 0)
                bindingResult.addError(new FieldError("staffMember", "mobileNumber", "MobileNumber Already Taken."));

            if (bindingResult.hasErrors()) {
                model.addAttribute("genders", Gender.values());
                model.addAttribute("memberRole", StaffMembers.values());
                return "staff/edit";
            }

            modelMapper.map(staffMemberUpdateDTO, member.get());
            staffMemberService.save(member.get());
            redirectAttributes.addAttribute("memberId", member.get().getId());
            return "redirect:/staff/details";
        } else {
            throw new NotFoundException("Staff Member Not Found");
        }
    }

    /* Delete particular staff member */
    @RequestMapping("/delete")
    public String deleteStaffMember(@RequestParam("memberId") Long memberId) {
        Optional<StaffMember> member = staffMemberService.findById(memberId);
        if (member.isPresent()) {
            staffMemberService.deleteById(memberId);
        } else {
            throw new NotFoundException("Member not found!");
        }
        return "redirect:/staff/list";
    }

    /* List all present staff members*/
    @RequestMapping("/list")
    public String showStaffMembers(Model model) {
        model.addAttribute("members", staffMemberService.findAll());
        return "staff/list";
    }

    /* Show details of a particular staff member */
    @RequestMapping("/details")
    public String showDetailsOfMember(@RequestParam("memberId") Long memberId, Model model) {
        Optional<StaffMember> member = staffMemberService.findById(memberId);
        if (member.isPresent()) {
            model.addAttribute("member", member.get());
        }
        return "staff/view";
    }

    /* Staff Leave */

    /* Admin Can Apply Staff Leave */
    @GetMapping("/leave/apply")
    public String applyLeave(Model model) throws ParseException {
        Date currentDate = ServiceUtil.getDateWithFormat(new Date());
        model.addAttribute("staffLeaveTaken", new StaffLeaveTaken());
        model.addAttribute("LeaveTypes", LeaveType.values());
        model.addAttribute("staffMemberList", staffMemberService.findByJoiningDateLessThanEqual(currentDate));
        return "staff/leave/leave-apply";
    }

    @PostMapping("/leave/apply")
    public String applyLeave(@Valid @ModelAttribute("staffLeaveTaken") StaffLeaveTaken staffLeaveTaken, BindingResult bindingResult, Model model) {
        Date joiningDate = staffLeaveTaken.getStaffMember().getJoiningDate();

        /* here we compare leave to today's date  */
        if ((staffLeaveTaken.getLeaveFromDate().compareTo(joiningDate) < 0)) {
            bindingResult.addError(new FieldError("staffLeaveTaken", "leaveFromDate", "leave date can't be less than joining date"));
        }
        /* Validate the form date is valid or not */
        if (bindingResult.hasErrors()) {
            model.addAttribute("staffMemberList", staffMemberService.findAll());
            model.addAttribute("LeaveTypes", LeaveType.values());
            return "staff/leave/leave-apply";
        }
        int days = ServiceUtil.getDaysBetweenDates(staffLeaveTaken.getLeaveToDate(), staffLeaveTaken.getLeaveFromDate());
        staffLeaveTaken.setDays(days);
        staffLeaveTakenService.save(staffLeaveTaken);
        return "redirect:/staff/list";
    }

    /* Staff Salary */
    @GetMapping("/salary/list")
    public String loadStaffSalary() {
        return "hello";
    }

    @GetMapping("/salary")
    public String showSalary(Model model) throws ParseException {
        List<StaffMember> staffMembers = staffMemberService.findByJoiningDateLessThanEqual(new Date());
        int i, j;
        for (i = 0; i < staffMembers.size(); i++) {
            List<StaffSalary> tempSalaryList = staffMembers.get(i).getStaffSalaryList();
            staffMembers.get(i).setStaffSalaryList(new ArrayList<>());
            for (j = 0; j < 12; j++) {
                StaffSalary salary = new StaffSalary();
                salary.setMonth(j + 1);
                salary.setId((long) i);
                salary.setSalary(0.0);
                salary.setNetSalary(0.0);
                staffMembers.get(i).getStaffSalaryList().add(salary);
            }
            for (j = 0; j < tempSalaryList.size(); j++) {
                double salary = tempSalaryList.get(j).getSalary();
                int month = tempSalaryList.get(j).getMonth();
                staffMembers.get(i).getStaffSalaryList().get(month - 1).setSalary(salary);
            }
        }
        model.addAttribute("members", staffMembers);
        return "staff/salary-list";
    }

    @GetMapping("/calculate/salary")
    public String calculateSalary() throws ParseException {
        Date today = ServiceUtil.getDateWithFormat(new Date());
        Date firstDate = ServiceUtil.getGivenMonthFirstDate(today);
        /* Flag is for to allow update staff leaves */
        serviceUtil.calculateStaffSalary(firstDate, today, false);
        return "redirect:/staff/salary";
    }

    @GetMapping("/attendance/view")
    public String viewAttendance(Model model){
        return "staff/view-attendance";
    }


}