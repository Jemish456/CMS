package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.StaffLeaveTaken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface StaffLeaveTakenRepository extends JpaRepository<StaffLeaveTaken,Long> {

    List<StaffLeaveTaken> findAllByStaffMemberId(Long id);

    List<StaffLeaveTaken> findByLeaveFromDateGreaterThanEqualAndLeaveToDateLessThanEqualAndStaffMemberId(Date from,Date to,Long id);

    List<StaffLeaveTaken> findByLeaveFromDateGreaterThanEqualAndLeaveFromDateLessThanEqualAndLeaveToDateGreaterThanAndStaffMemberId(Date fromAfter,Date fromBefore,Date toAfter,Long id);

    List<StaffLeaveTaken> findByLeaveFromDateLessThanAndLeaveToDateGreaterThanEqualAndLeaveFromDateLessThanEqualAndStaffMemberId(Date fromBefore,Date toAfter,Date ToBefore,Long id);

    List<StaffLeaveTaken> findByLeaveFromDateLessThanAndLeaveToDateGreaterThanEqualAndStaffMemberId(Date from,Date fromAfter,Long id);

    //get data whose date is between fromdate and todate
    @Query(
            value = "select * from staff_leave_taken " +
                    "where staff_member_id=:id " +
                    "and(" +
                    "    ((MONTH(leave_from_date) = :month) && (YEAR(leave_from_date) = :year)) " +
                    "    or " +
                    "    ((MONTH(leave_to_date) = :month) && (YEAR(leave_to_date) = :year))" +
                    ")",
            nativeQuery = true
    )
    List<StaffLeaveTaken> getDataById(int month,int year,Long id);

    @Query(value = "select * staff_leave_taken where staff_member_id=:id and leave_from_date between :fromDate and :toDate",nativeQuery = true)
    boolean isDateAvailable(Long id,Date fromDate,Date toDate);

}
