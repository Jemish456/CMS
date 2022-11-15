package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Prescription;
import com.globalvox.clinicmanagementsystem.entity.PrescriptionDrug;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

public interface PrescriptionDrugService {

    String save(PrescriptionDrug prescriptionDrug, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);

    Optional<PrescriptionDrug> findById(Long drugId);

    void deleteById(Long drugId);

    List<PrescriptionDrug> findAllByPrescriptionId(Long id);

    List<String> getPrescriptionDrugName(Prescription prescription);
}
