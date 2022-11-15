package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Inventory;
import com.globalvox.clinicmanagementsystem.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public String save(BindingResult bindingResult, Inventory inventory, Model model) {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        if (inventoryList.isEmpty()) {
            inventoryRepository.save(inventory);
        }
        for (int i = 0; i < inventoryList.size(); i++) {
            if(inventoryList.get(i).getName().compareTo(inventory.getName()) == 0 &&
               inventoryList.get(i).getPower().compareTo(inventory.getPower()) == 0 &&
               inventoryList.get(i).getCompany().compareTo(inventory.getCompany()) == 0){
//                    bindingResult.addError(new FieldError("inventory","name","aleady exist"));
                    bindingResult.addError(new FieldError("inventory","power","already exist"));
//                bindingResult.addError(new FieldError("inventory","power","aleady exist"));
                    if(bindingResult.hasErrors()){
                        model.addAttribute("inventoryList",inventoryList);
                        model.addAttribute("inventory",inventory);
                        return "store/edit-inventory";
                    }
            }
            else{
                inventoryRepository.save(inventory);
            }
        }
        return "redirect:/store/inventoryList";
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory findById(Long inventoryId) {
        Optional<Inventory> result = inventoryRepository.findById(inventoryId);
        Inventory inventory = null;
        if(result.isPresent()){
            inventory = result.get();
        }
        return inventory;
    }

    @Override
    public void delete(Long inventoryId) {
        inventoryRepository.deleteById(inventoryId);
    }
}
