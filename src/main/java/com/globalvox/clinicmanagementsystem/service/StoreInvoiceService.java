package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.StoreInvoice;

import java.util.Date;
import java.util.List;

public interface StoreInvoiceService {
    List<StoreInvoice> findAllByCreatedAtIsBetween(Date yesterday, Date toDate);

    Object findAllById(Long invoiceId);
}
