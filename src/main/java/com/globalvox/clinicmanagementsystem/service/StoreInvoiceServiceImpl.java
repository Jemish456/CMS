package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StoreInvoice;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StoreInvoiceServiceImpl implements StoreInvoiceService {

    @Override
    public List<StoreInvoice> findAllByCreatedAtIsBetween(Date yesterday, Date toDate) {
        return null;
    }

    @Override
    public Object findAllById(Long invoiceId) {
        return null;
    }
}
