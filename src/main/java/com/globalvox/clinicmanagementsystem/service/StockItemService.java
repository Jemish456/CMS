package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StockItem;

import java.util.List;

public interface StockItemService {
    List<StockItem> findAllStockItems();

    void save(StockItem stockItem);

    StockItem findById(Long stockId);

    void delete(Long stockId);


}
