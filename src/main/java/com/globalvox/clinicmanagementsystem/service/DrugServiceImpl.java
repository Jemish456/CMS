package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Drug;
import com.globalvox.clinicmanagementsystem.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;

@Service
public class DrugServiceImpl implements DrugService {

    @Autowired
    private DrugRepository drugRepository;

    @Override
    public List<Drug> findAllDrugs() {
        return drugRepository.findAllByOrderByName();
    }

    @Override
    public Drug findById(Long drugId) {
        Optional<Drug> result =drugRepository.findById(drugId);

        Drug drug = null;

        if(result.isPresent()){
            drug =result.get();
        }
        return drug;
    }

    @Override
    public void save(Drug drug, BindingResult bindingResult, Model model) {

            if (drug.getId() == null) {
              int count =   drugRepository.countByNameAndPower(drug.getName(),drug.getPower());
               if(count >0){
                   model.addAttribute("error", "Medicine already exist");
                  }
                  else{
                      drugRepository.save(drug);
                  }
            }
            else{
                int count = drugRepository.countByNameAndPowerAndIdNot(drug.getName(),drug.getPower(),drug.getId());
                if(count >0){
                    model.addAttribute("error", "Medicine already exist");
                }
                else{
                    drugRepository.save(drug);
                }
            }

    }


    @Override
    public void deleteById(Long drugId) {
        drugRepository.deleteById(drugId);
    }

}
