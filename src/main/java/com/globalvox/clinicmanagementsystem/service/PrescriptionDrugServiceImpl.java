package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Drug;
import com.globalvox.clinicmanagementsystem.entity.Prescription;
import com.globalvox.clinicmanagementsystem.entity.PrescriptionDrug;
import com.globalvox.clinicmanagementsystem.repository.PrescriptionDrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;


@Service
public class PrescriptionDrugServiceImpl implements PrescriptionDrugService {
    @Autowired
    private PrescriptionDrugRepository prescriptionDrugRepository;

    @Autowired
    private PrescriptionDrugService prescriptionDrugService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Override
    public Optional<PrescriptionDrug> findById(Long drugId) {
        return prescriptionDrugRepository.findById(drugId);
    }

    @Override
    public String save(PrescriptionDrug prescriptionDrug, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (prescriptionDrug.getMorningDose().compareTo("0") == 0 && prescriptionDrug.getAfternoonDose().compareTo("0") == 0 && prescriptionDrug.getEveningDose().compareTo("0") == 0) {
            bindingResult.addError(new FieldError("prescriptionDrug", "morningDose", "Select at least one dose"));
        }
        if (bindingResult.hasErrors()) {
            List<Drug> drugList = drugService.findAllDrugs();
            model.addAttribute("drugList", drugList);
            model.addAttribute("prescriptionDrugList", prescriptionDrugService.findAllByPrescriptionId(prescriptionDrug.getPrescription().getId()));
            model.addAttribute("prescriptionDrug", prescriptionDrug);
            Optional<Prescription> prescription = prescriptionService.findById(prescriptionDrug.getPrescription().getId());
            model.addAttribute("doctor",prescription.get().getAppointment().getDoctor());
            model.addAttribute("patient",prescription.get().getAppointment().getPatient());
            if (prescriptionDrug.getId() == null) {
                model.addAttribute("drug", new Drug());
                return "prescription/prescription-drug-form";
            } else {
                return "prescription/prescription-drug-edit";
            }

        }
        redirectAttributes.addAttribute("prescriptionId", prescriptionDrug.getPrescription());
        redirectAttributes.addFlashAttribute("prescriptionDrugList", prescriptionDrugService.findAllByPrescriptionId(prescriptionDrug.getPrescription().getId()));
        if (prescriptionDrug.getId() == null) {
            prescriptionDrugRepository.save(prescriptionDrug);
            return "redirect:/doctor/prescription/drug/add";
        } else {
            prescriptionDrugRepository.save(prescriptionDrug);
            return "redirect:/doctor/prescription/drug/list";
        }
    }


    @Override
    public void deleteById(Long drugId) {
        prescriptionDrugRepository.deleteById(drugId);
    }

    @Override
    public List<PrescriptionDrug> findAllByPrescriptionId(Long id) {
        return prescriptionDrugRepository.findAllByPrescriptionId(id);
    }

    @Override
    public List<String> getPrescriptionDrugName(Prescription prescription) {
        return prescriptionDrugRepository.getPrescriptionDrugName(prescription);
    }
}
