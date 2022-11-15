package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Holiday;

import java.util.List;
import java.util.Optional;

public interface HolidayService {

    Optional<Holiday> findById (Long holidayId);

    List<Holiday> findAll();

    void save(Holiday holiday);

    void deleteById (Long holidayId);

}
