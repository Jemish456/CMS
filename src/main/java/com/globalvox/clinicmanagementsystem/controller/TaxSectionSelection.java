package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaveTaken;
import com.globalvox.clinicmanagementsystem.entity.StaffMember;
import com.globalvox.clinicmanagementsystem.entity.StaffSalary;
import com.globalvox.clinicmanagementsystem.entity.TaxSection;
import com.globalvox.clinicmanagementsystem.service.StaffLeaveTakenService;
import com.globalvox.clinicmanagementsystem.service.StaffMemberService;
import com.globalvox.clinicmanagementsystem.service.StaffSalaryService;
import com.globalvox.clinicmanagementsystem.service.TaxSectionService;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Controller
@RequestMapping("/tax")
public class TaxSectionSelection {

    @Autowired
    private StaffMemberService staffMemberService;

    @Autowired
    private TaxSectionService taxSectionService;

    @Autowired
    private StaffLeaveTakenService staffLeaveTakenService;

    @Autowired
    private StaffSalaryService staffSalaryService;

    @GetMapping("/section")
    private String taxSection(Model model) throws ParseException {

//        Date currentDate= ServiceUtil.getDateWithFormat(new Date());
        TaxSection taxSection = new TaxSection();
        /* Taking today date*/
        Date todayDate = new Date();
        int financialYear = ServiceUtil.getYear(todayDate);

        /* All member List Without Tax Section of  Current Financial Year*/
        List<StaffMember> staffMemberList = staffMemberService.allMemberWithoutTaxSection(financialYear);
        /* All member List With Tax Section of  Current Financial Year*/
        List<TaxSection> taxSections = taxSectionService.allMemberWithTaxSectionInCurrentYear(financialYear);

        model.addAttribute("members",taxSections);
        model.addAttribute("taxSection",taxSection);
        model.addAttribute("staffMemberList",staffMemberList);
        return "tax-section/apply-section";
    }

    /* Apply TAX System(NEW OR OLD) */
    @PostMapping("/applySection")
    public String taxSystemAssine(@ModelAttribute("taxSection")TaxSection taxSection){
        double annualIncome = taxSection.getStaffMember().getSalary()*12;
        double taxAbleIncome = annualIncome;

        /* for old TAX System Calculating Taxable Income */
        if (taxSection.getTaxSystem().compareTo("OLD") == 0){
             taxAbleIncome = annualIncome -
                    (taxSection.getStandardDeduction()
                            +taxSection.getProfessionalTax()
                            +taxSection.getSection24B()
                            +taxSection.getSection80C()
                            +taxSection.getSection80D()
                    );
        }
        /* Saving Tax Able Income*/
        taxSection.setTaxAbleIncome(taxAbleIncome);
        taxSectionService.save(taxSection);
        return "redirect:/tax/section";
    }

    @GetMapping("/salary")
    private String calculateSalary(Model model){
        /* List of Member till today joining date   */
        List<StaffMember> staffMembers = staffMemberService.findByJoiningDateLessThanEqual(new Date());
        model.addAttribute("members",staffMembers);
        return "tax-section/tax-salary";
    }

}
