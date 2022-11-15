package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.repository.InventoryRepository;
import com.globalvox.clinicmanagementsystem.repository.StockItemRepository;
import com.globalvox.clinicmanagementsystem.repository.StoreInvoiceDetailRepository;
import com.globalvox.clinicmanagementsystem.repository.StoreInvoiceRepository;
import com.globalvox.clinicmanagementsystem.service.InventoryService;
import com.globalvox.clinicmanagementsystem.service.StockItemService;
import com.globalvox.clinicmanagementsystem.service.StoreInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import java.util.*;


@PreAuthorize("hasRole('ROLE_STORE')")
@Controller
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StockItemService stockItemService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StoreInvoiceDetailRepository storeInvoiceDetailRepository;

    @Autowired
    private StoreInvoiceRepository storeInvoiceRepository;

    @Autowired
    private StoreInvoiceService storeInvoiceService;

    List<Inventory> inventoryList = new ArrayList<>();

    List<StockItem> stockItemList = new ArrayList<>();

    private List<HashMap<String,Object>> itemList = new ArrayList<>();

    Collection<Long> idList = new Stack<>() ;
//
//    @PostConstruct
//    public void setList(){
//        inventoryList = inventoryRepository.findAll();

    @RequestMapping("")
    public String loadHome() {
        return "store/home";
    }

    @GetMapping("/inventoryList")
    public String showInventoryPage(Model model){
        List<Inventory> inventoryList = inventoryService.findAll();
        model.addAttribute("inventory",new Inventory());
        model.addAttribute("inventoryList",inventoryList);
        return "store/add-inventory";
    }

//    @PostMapping("/addInventory")
//    public String addInventory(@Valid @ModelAttribute("inventory")Inventory inventory,BindingResult bindingResult,Model model){
//        String redirectLink = inventoryService.save(bindingResult,inventory,model);
//        return redirectLink;
//    }

    @GetMapping("/addInventory")
    public String addInventory(Model model){
        Inventory inventory = new Inventory();
        model.addAttribute("inventory",inventory);
        return "store/edit-inventory";
    }

    @GetMapping("/editInventory")
    public String loadInventory(@RequestParam("inventoryId")Long inventoryId,Model model) {
        Inventory inventory = inventoryService.findById(inventoryId);
        model.addAttribute("inventory",inventory);
        return "store/edit-inventory";
    }

    @PostMapping("/editInventory")
    public String editInventory(@Valid @ModelAttribute("inventory")Inventory inventory, BindingResult bindingResult,Model model){
        String redirectLink = inventoryService.save(bindingResult,inventory,model);
        return redirectLink;
    }

    @GetMapping("inventory/delete")
    public String inventoryDelete(@RequestParam("inventoryId")Long inventoryId){
        inventoryService.delete(inventoryId);
        return "redirect:/store/inventoryList";
    }

    @GetMapping("/stockList")
    public String inventory(Model model){
        List<StockItem> stockItems = stockItemService.findAllStockItems();
        model.addAttribute("stockItems",stockItems);
        return "store/stock-list";
    }

    @GetMapping("/addStockItem")
    public String addStockItem(Model model){
        List<Inventory> inventoryList = inventoryService.findAll();
        List<StockItem> stockItems = stockItemService.findAllStockItems();
        model.addAttribute("inventoryList",inventoryList);
        model.addAttribute("stockItem",new StockItem());
        model.addAttribute("stockItems",stockItems);
        return "store/add-stock";
    }

    @PostMapping("/addStockItem")
    public String saveItem(@Valid  @ModelAttribute("stockItem")StockItem stockItem, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            List<Inventory> inventoryList = inventoryService.findAll();
            List<StockItem> stockItems = stockItemService.findAllStockItems();
            model.addAttribute("stockItems",stockItems);
            model.addAttribute("inventoryList",inventoryList);
            return "store/add-stock";
        }
        stockItemService.save(stockItem);
        return "redirect:/store/addStockItem";
    }

    @GetMapping("/stockUpdate")
    public String update(@RequestParam("stockItemId")Long stockItemId,Model model) {
        List<Inventory> inventoryList = inventoryService.findAll();
        StockItem stockItem = stockItemService.findById(stockItemId);
        model.addAttribute("inventoryList",inventoryList);
        model.addAttribute("stockItem",stockItem);
        return "store/add-stock";

    }

    @GetMapping("/stockDelete")
    public String delete(@RequestParam("stockItemId")Long stockItemId){
        stockItemService.delete(stockItemId);
        return "redirect:/store/stockList";
    }

    @GetMapping("/showInvoiceForm")
    public String showInvoiceForm(Model model){
//        inventoryList = inventoryService.findAll();

        if(idList.isEmpty()){
            inventoryList = inventoryService.findAll();
        } else {
            inventoryList = inventoryRepository.findAllByIdNotIn(idList);
        }

        model.addAttribute("inventoryList", inventoryList);

        String msg = "No More Items To Add";
        model.addAttribute("msg",msg);

        model.addAttribute("itemList",itemList);

        return "store/invoice/add-invoice";
    }

    @RequestMapping("/addStoreInvoiceDetail")
    public String addStoreInvoiceDetail(@RequestParam("id") Long inventoryId,@RequestParam("qty") Integer qty){

        if(qty > 0) {

            Inventory inventory = inventoryRepository.findAllById(inventoryId);

            List<StockItem> stockItemListByInventory = stockItemRepository.findAllByInventoryEqualsAndQtyGreaterThanOrderByExpiryDateAsc(inventory,0);

            if(!stockItemListByInventory.isEmpty()){

                //getting total quantity in inventory
                int totalQty = 0;

                for (StockItem item: stockItemListByInventory) {
                    totalQty += item.getQty();
                }

                //managing quantity
                if(qty <= totalQty) {

                    idList.add(inventoryId);

                    //managing quantity
                    int tempQty = qty;

                    for (StockItem item: stockItemListByInventory) {

                        HashMap<String,Object> itemHashmap = new HashMap<>();

                        itemHashmap.put("inventoryId",inventoryId);
                        itemHashmap.put("id",item.getId());

                        if((tempQty - item.getQty()) <= 0){
                            itemHashmap.put("qty",tempQty);
                            itemHashmap.put("totalPrice",(tempQty*(item.getSellingPrice())));

                            if((tempQty-item.getQty()) == 0)
                                item.setQty(0);
                            else
                                item.setQty(item.getQty()-tempQty);
                            System.out.println(item.getQty());

                            tempQty = 0;
                        } else {
                            itemHashmap.put("qty",item.getQty());
                            itemHashmap.put("totalPrice",(item.getQty()*(item.getSellingPrice())));
                            tempQty = tempQty - item.getQty();
                            item.setQty(0);
                        }

                        itemHashmap.put("stockItem",item);

                        boolean flag = false;

                        for (HashMap<String,Object> map: itemList) {
                            if(map.get("inventoryId") == itemHashmap.get("inventoryId")){
                                int oldQty = (int)map.get("qty");
                                int newQty = (int)itemHashmap.get("qty");
                                map.put("qty",oldQty+newQty);
                                flag = true;
                            }
                        }

                        if(!flag){
                            itemList.add(itemHashmap);
                        }

                        stockItemList.add(item);

                        if(tempQty == 0){
                            break;
                        }
                    }

                    //removing selected item from the inventory list
                    Inventory inventoryObj = null;

                    for (Inventory inv : inventoryList) {
                        if (inv.getId() == inventory.getId()) {
                            inventoryObj = inv;
                        }
                    }

                    if (inventoryObj != null)
                        inventoryList.remove(inventoryObj);

                } else {
                    System.out.println("qty is greater than total qty");
                }


            }
        }

        System.out.println(itemList);

        return "redirect:showInvoiceForm";
    }

    @PostMapping("/addStoreInvoice")
    public String addStoreInvoice(@RequestParam("buyername") String buyername, Model model){

        StoreInvoice storeInvoice = new StoreInvoice();

        storeInvoice.setName(buyername);

        Double totalPrice = 0.0;

        List<StoreInvoiceDetail> storeInvoiceDetailList = new ArrayList<>();

        for (HashMap<String,Object> item : itemList) {

            StoreInvoiceDetail storeInvoiceDetail = new StoreInvoiceDetail();
            storeInvoiceDetail.setQty((Integer) item.get("qty"));
            storeInvoiceDetail.setStockItem((StockItem) item.get("stockItem"));
            totalPrice = (Double) item.get("totalPrice");

            storeInvoiceDetailList.add(storeInvoiceDetail);

            storeInvoiceDetailRepository.save(storeInvoiceDetail);

        }

        storeInvoice.setTotalPrice(totalPrice);
        storeInvoice.setInvoiceDetailList(storeInvoiceDetailList);

        storeInvoiceRepository.save(storeInvoice);

        stockItemRepository.saveAll(stockItemList);

        clearAllList();

        model.addAttribute("storeInvoice",storeInvoice);

        return "store/invoice/view-invoice";
    }

    @GetMapping("/clearList")
    public void clearAllList(){
        itemList.clear();
        stockItemList.clear();
        idList.clear();
    }
}
