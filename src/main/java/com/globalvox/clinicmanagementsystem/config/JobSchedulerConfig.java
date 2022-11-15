package com.globalvox.clinicmanagementsystem.config;

import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.entity.enums.AppointmentStatus;
import com.globalvox.clinicmanagementsystem.repository.*;
import com.globalvox.clinicmanagementsystem.service.*;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Configuration
@EnableScheduling
@EnableAsync
public class JobSchedulerConfig {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private StaffLeaveManageService staffLeaveManageService;

    @Autowired
    private ServiceUtil serviceUtil;

//    @Bean
//    @PostConstruct
//    public int getCronValue() {
//        int currentYear = ServiceUtil.getYear(new Date());
//        Optional<StaffLeaveManage> staffLeaveManage = staffLeaveManageRepository.findByYear(currentYear);
//        return staffLeaveManage.get().getCarryForwardExpiry();
//    }

    /* Run on every day at 12AM
    cron parameter second,minute,hour,day of month,month,day of week */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateOldAppointmentStatus() {
        List<Appointment> appointmentList = appointmentService.findAllByStartDateTimeLessThan(new Timestamp(System.currentTimeMillis()));
        for (Appointment appointment : appointmentList) {
            appointment.setStatus(AppointmentStatus.NOSHOW);
            appointmentService.save(appointment);
        }
    }

    /* Runs every month Last Day
    cron parameter second,minute,hour,day of month,month,day of week */
//    @Transactional
//    @Scheduled(cron = "* * * * * *")
//    public void addStaffSalary() throws ParseException {
//        Date today = ServiceUtil.getDateWithFormat(new Date());
//        Date lastDate = today;
//        Date firstDate = ServiceUtil.getGivenMonthFirstDate(lastDate);
//        /* Flag is for to allow update staff leaves */
//        serviceUtil.calculateStaffSalary(firstDate, lastDate, true);
//    }
}
