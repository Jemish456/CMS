package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Holiday;
import com.globalvox.clinicmanagementsystem.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    @Override
    public Optional<Holiday> findById(Long holidayId) {
        return holidayRepository.findById(holidayId);
    }

    @Override
    public List<Holiday> findAll() {
        return holidayRepository.findAll();
    }

    @Override
    public void save(Holiday holiday) {
        holidayRepository.save(holiday);
    }

    @Override
    public void deleteById(Long holidayId) {
        holidayRepository.deleteById(holidayId);
    }
}
