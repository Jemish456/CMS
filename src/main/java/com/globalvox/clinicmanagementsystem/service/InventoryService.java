package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Inventory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface InventoryService {
    String save(BindingResult bindingResult, Inventory inventory, Model Model);

    List<Inventory> findAll();


    Inventory findById(Long inventoryId);

    void delete(Long inventoryId);
}
