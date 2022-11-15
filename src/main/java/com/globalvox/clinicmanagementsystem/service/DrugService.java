package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Drug;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

public interface DrugService {
    List<Drug> findAllDrugs();

    Drug findById(Long drugId);

    void save(Drug drug, BindingResult bindingResult, Model model);

    void deleteById(Long drugId);

}
