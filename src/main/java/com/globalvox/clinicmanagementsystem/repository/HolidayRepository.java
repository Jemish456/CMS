package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday,Long> {

    Optional<Holiday> findById (Long holidayId);

    List<Holiday> findAll();

    void deleteById (Long holidayId);

}
