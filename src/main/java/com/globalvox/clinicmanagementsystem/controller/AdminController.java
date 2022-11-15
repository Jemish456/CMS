package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.dto.StaffLeaveManageCreationDTO;
import com.globalvox.clinicmanagementsystem.entity.StaffLeaveManage;
import com.globalvox.clinicmanagementsystem.service.*;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.Date;

/* Only Admin has access of all functionality of this class */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private StaffMemberService staffMemberService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private StaffLeaveManageService staffLeaveManageService;

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private ModelMapper modelMapper;

    /* Admin */

    @RequestMapping("")
    public String loadHome() {
        return "admin/home";
    }

    /*show the details of the website like how many doctors, patients, pending and approved appointment*/
    @GetMapping("/panel")
    public String dashboard(Model model) {
        long totalDoctor = doctorService.count();
        long totalPatient = patientService.count();
        long totalStaffMembers = staffMemberService.count();
        long totalPendingAppointment = appointmentService.countAllByPendingStatus();
        long totalApprovedAppointment = appointmentService.countAllByApprovedStatus();
        long totalClosedAppointment = appointmentService.countAllByClosedStatus();

        model.addAttribute("totalDoctor", totalDoctor);
        model.addAttribute("totalPatient", totalPatient);
        model.addAttribute("totalStaffMembers", totalStaffMembers);
        model.addAttribute("totalPendingAppointment", totalPendingAppointment);
        model.addAttribute("totalApprovedAppointment", totalApprovedAppointment);
        model.addAttribute("totalClosedAppointment", totalClosedAppointment);

        return "admin/panel";
    }

    @GetMapping("/leave/manage")
    public String leaveManage(Model model){
        StaffLeaveManageCreationDTO staffLeaveManage = new StaffLeaveManageCreationDTO();
        model.addAttribute("staffLeaveManage", staffLeaveManage);
        model.addAttribute("staffLeaveManageList",staffLeaveManageService.findAll());
        return "admin/leave-manage";
    }

    @PostMapping("/leave/manage")
    public String leaveManage(@Valid @ModelAttribute("staffLeaveManage") StaffLeaveManageCreationDTO staffLeaveManageCreationDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            model.addAttribute("staffLeaveManageList",staffLeaveManageService.findAll());
            return "admin/leave-manage";
        }
        StaffLeaveManage staffLeaveManage = modelMapper.map(staffLeaveManageCreationDTO, StaffLeaveManage.class);
        staffLeaveManageService.save(staffLeaveManage);
        redirectAttributes.addFlashAttribute("success","Annual Leave Manage Added.");
        redirectAttributes.addFlashAttribute("staffLeaveManageList",staffLeaveManageService.findAll());

        return "redirect:/admin/leave/manage";
    }
}
