package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StockItem;
import com.globalvox.clinicmanagementsystem.repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockItemServiceImpl implements StockItemService {

    @Autowired
    private StockItemRepository StockItemRepository;

    @Override
    public List<StockItem> findAllStockItems() {
        return StockItemRepository.findAll();
    }

    @Override
    public void save(StockItem StockItem) {
        List<StockItem> StockItemList = StockItemRepository.findAll();
        if (StockItemList.isEmpty()){
            StockItemRepository.save(StockItem);
        }
        for (int i = 0; i <StockItemList.size() ; i++) {
            if (StockItemList.get(i).getInventory().getName().compareTo(StockItem.getInventory().getName()) == 0 &&
                StockItemList.get(i).getInventory().getCompany().compareTo(StockItem.getInventory().getCompany()) == 0 &&
                StockItemList.get(i).getInventory().getPower().compareTo(StockItem.getInventory().getPower()) == 0 &&
                StockItemList.get(i).getExpiryDate().compareTo(StockItem.getExpiryDate()) == 0 &&
                    StockItemList.get(i).getBuyingPrice().compareTo(StockItem.getBuyingPrice()) ==0 &&
                    StockItemList.get(i).getSellingPrice().compareTo(StockItem.getSellingPrice()) == 0
                ) {
                StockItemList.get(i).setQty(StockItem.getQty() + StockItemList.get(i).getQty());
                StockItemRepository.save(StockItemList.get(i));
                break;
            }
            else{
                 StockItemRepository.save(StockItem);
            }

        }

    }

    @Override
    public StockItem findById(Long StockItemId) {
        Optional<StockItem> result = StockItemRepository.findById(StockItemId);
        StockItem StockItem = null;
        if(result.isPresent()){
            StockItem = result.get();
        }
        return StockItem;
    }

    @Override
    public void delete(Long StockItemId) {
        StockItemRepository.deleteById(StockItemId);
    }
}
