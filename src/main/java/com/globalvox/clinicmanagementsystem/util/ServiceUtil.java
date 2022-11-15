
package com.globalvox.clinicmanagementsystem.util;

import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.repository.StaffLeaveManageRepository;
import com.globalvox.clinicmanagementsystem.repository.StaffLeavesRepository;
import com.globalvox.clinicmanagementsystem.repository.StaffMemberRepository;
import com.globalvox.clinicmanagementsystem.service.StaffLeaveManageService;
import com.globalvox.clinicmanagementsystem.service.StaffLeaveTakenService;
import com.globalvox.clinicmanagementsystem.service.StaffMemberService;
import com.globalvox.clinicmanagementsystem.service.StaffSalaryService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class ServiceUtil {

    @Autowired
    private StaffMemberService staffMemberService;

    @Autowired
    private StaffLeaveTakenService staffLeaveTakenService;

    @Autowired
    private StaffSalaryService staffSalaryService;

    @Autowired
    private StaffLeavesRepository staffLeavesRepository;

    @Autowired
    private StaffLeaveManageService staffLeaveManageService;

    @Autowired
    private StaffMemberRepository staffMemberRepository;

    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    public static Date addMinutesToDate(int minutes, Date beforeTime) {
        return new Date(beforeTime.getTime() + TimeUnit.MINUTES.toMillis(minutes));
    }

    public static Date getDate(Date date, Date time) {
        SimpleDateFormat format = new SimpleDateFormat("HH");
        SimpleDateFormat formatMin = new SimpleDateFormat("mm");

        String hour = format.format(time);
        String min = formatMin.format(time);
        Integer hours = Integer.parseInt(hour);

        date.setHours(hours);
        date.setHours(hours);
        date.setMinutes(Integer.parseInt(min));
        date.setSeconds(00);
        return date;
    }

    public static Long getAuthUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        System.out.println(customUser);
        return customUser.getId();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static Date getDateWithFormat(Date date) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.parse(format.format(date));
    }

    public static Date getDateWithSQLFormat(Date date) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(format.format(date));
    }

    public static int getMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return Integer.parseInt(format.format(date));
    }

    public static int getYear(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return Integer.parseInt(format.format(date));
    }

    public static int getDaysBetweenDates(Date fromDate, Date toDate) {
        long diff = Math.abs(toDate.getTime() - fromDate.getTime());
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        return (int) (days + 1);
    }

    public static Date getGivenMonthFirstDate(Date date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date currentMonthFirstDate = cal.getTime();
        return format.parse(format.format(currentMonthFirstDate));
    }

    public static Date getGivenMonthLastDate(Date date) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);

        Date currentMonthLastDate = cal.getTime();
        return format.parse(format.format(currentMonthLastDate));
    }

    public static Date getPreviousMonthLastDate(Date date) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);

        Date preMonthDate = cal.getTime();
        return format.parse(format.format(preMonthDate));
    }

    public static int getCurrentMonthDays(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date getLeaveExpiryDate(Date date, int carryForwardMonths) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, carryForwardMonths);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date date1 = cal.getTime();
        return format.parse(format.format(date1));
    }

    public static Date getNewEffectiveDate(Date date, int carryForwardMonths) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, carryForwardMonths);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date date1 = cal.getTime();
        return format.parse(format.format(date1));
    }

    public static String getYearFirstDate(Date date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, 1);

        Date date1 = cal.getTime();
        return format.format(date1);
    }

    public void calculateStaffSalary(Date startDate, Date endDate, boolean flag) {

        int totalDaysOfMonth = ServiceUtil.getCurrentMonthDays(endDate);

        /* find list of staff member's who's joining date is less than or equal to end date*/
        List<StaffMember> staffMemberList = staffMemberService.findByJoiningDateLessThanEqual(endDate);
        if (staffMemberList != null) {
            for (StaffMember member : staffMemberList) {

                /* get staff members leaves details*/
                int currentYear = getYear(new Date());
                Optional<StaffLeaves> staffLeaves = staffLeavesRepository.findByStaffMemberIdAndYear(member.getId(), currentYear);
                double leaveDaysBalance = staffLeaves.get().getBalance();
                double leaveDaysUsed = staffLeaves.get().getUsed();
                double leaveDaysAllowed = staffLeaves.get().getAllowed();

                /* find total working days and working days of staff */
                double totalWorkingDays = 0;
                double workingDays = 0;
                double leaveCount = staffLeaveTakenService.getLeaveCount(startDate, endDate, member.getId());

                if ((member.getJoiningDate().compareTo(startDate) < 0)) {
                    totalWorkingDays = ServiceUtil.getDaysBetweenDates(startDate, endDate);
                } else {
                    totalWorkingDays = ServiceUtil.getDaysBetweenDates(member.getJoiningDate(), endDate);
                }
                workingDays = (leaveCount <= leaveDaysBalance) ? totalWorkingDays : totalWorkingDays - leaveCount + leaveDaysBalance;

                /* count salary of month,salary per day and save staffSalary*/
                Double salaryPerMonth = member.getSalary();
                Double salaryPerDay = salaryPerMonth / totalDaysOfMonth;
                Double salary = ServiceUtil.round(workingDays * salaryPerDay, 2);
                int salaryMonth = ServiceUtil.getMonth(endDate);

                /* checking staff salary already exists or not */
                StaffSalary staffSalary = staffSalaryService.findByGivenMonthDate(ServiceUtil.getMonth(endDate), member.getId());
                if (staffSalary == null) {
                    staffSalary = new StaffSalary();
                }
                staffSalary.setDate(endDate);
                staffSalary.setMonth(salaryMonth);
                staffSalary.setSalary(salary);
                staffSalary.setStaffMember(member);
                staffSalaryService.save(staffSalary);

                /* update staff leaves on monthly basis*/
                if (flag) {
                    if (leaveDaysBalance >= leaveCount) {
                        staffLeaves.get().setBalance(leaveDaysBalance - leaveCount);
                        staffLeaves.get().setUsed(leaveDaysUsed + leaveCount);
                    } else {
                        staffLeaves.get().setBalance(0);
                        staffLeaves.get().setUsed(leaveDaysUsed + leaveCount);
                    }
                    staffLeavesRepository.save(staffLeaves.get());
                }
            }
        }
    }

    public void addStaffLeaves() throws ParseException {
        Date today = getDateWithFormat(new Date());
        int currentYear = getYear(today);

        /* Find Staff Leave Manage Of Current Year */
        Optional<StaffLeaveManage> staffLeaveManage = staffLeaveManageService.findByYear(currentYear);
        int carryForwardMonths = staffLeaveManage.get().getCarryForwardExpiry();
        Date leaveExpiryDate = ServiceUtil.getLeaveExpiryDate(today, carryForwardMonths);
        int leaveExpiryMonth = (ServiceUtil.getMonth(leaveExpiryDate));

        /* Find All Employees Who's joining Date is less than Leave Expiry/Carry Forward Expiry date */
        List<StaffMember> staffMemberList = staffMemberRepository.findAllByJoiningDateLessThan(leaveExpiryDate);
        for (StaffMember staffMember : staffMemberList) {
            int durationMonth;
            if (staffMember.getJoiningDate().compareTo(today) < 0) {
                durationMonth = carryForwardMonths;
            } else {
                durationMonth = leaveExpiryMonth - ServiceUtil.getMonth(staffMember.getJoiningDate());
            }

            /* Check that Staff Leaves already exists or not */
            Optional<StaffLeaves> staffLeaves = staffLeavesRepository.findByStaffMemberIdAndYear(staffMember.getId(), currentYear);
            if (staffLeaves.isPresent()) {
                staffLeaves.get().setAllowed(staffLeaveManage.get().getPaidLeaves() * durationMonth);
                staffLeaves.get().setBalance(staffLeaveManage.get().getPaidLeaves() * durationMonth);
                staffLeaves.get().setUsed(0);
                staffLeaves.get().setStaffMember(staffMember);
                staffLeaves.get().setYear(currentYear);
                staffLeavesRepository.save(staffLeaves.get());
            } else {
                StaffLeaves newStaffLeaves = new StaffLeaves();
                newStaffLeaves.setAllowed(staffLeaveManage.get().getPaidLeaves() * durationMonth);
                newStaffLeaves.setBalance(staffLeaveManage.get().getPaidLeaves() * durationMonth);
                newStaffLeaves.setUsed(0);
                newStaffLeaves.setStaffMember(staffMember);
                newStaffLeaves.setYear(currentYear);
                staffLeavesRepository.save(newStaffLeaves);
            }
        }
    }

    public static Date getTodayStartingTime(Date today) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        Date yesterdayDate = calendar.getTime();
        return format.parse(format.format(yesterdayDate));
    }

    /* Get Random Date by Month Number*/
    public static Date getRandomDateOfMonth(int month) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH,1);

        Date dateOfMonth=calendar.getTime();
        return format.parse(format.format(dateOfMonth));

    }

    //    Calculate Tax with Old TAX System
    public static double calculateTaxWithOldSystem(Double taxableIncome){

        double taxWithoutCess= 0;
        double taxWithCess = 0;
        if (taxableIncome>=10_00_000){
            //Tax for more than 10_00_000 income (more than 10lakh)
            taxWithoutCess = 112500 + 30*(taxableIncome-10_00_000)/100;
        }
        else if (taxableIncome>=5_00_000){
            //Tax for income (more than 5 lakh and less then 10 lakh)
            taxWithoutCess = 12500 + 20*(taxableIncome-5_00_000)/100;
        }
        else if (taxableIncome>=2_50_000){
            //Tax for income (more than 2.5 lakh and less then 5 lakh)
            taxWithoutCess = 5*(taxableIncome-2_50_000)/100;
        }
        else {
            //Tax for income (less than 2.5 lakh)
            taxWithoutCess = 0;
        }
        taxWithCess = taxWithoutCess + 4*taxWithoutCess/100;
        return taxWithCess;
    }

    // Calculate Tax with New TAX System
    public static double calculateTaxWithNewSystem(Double taxableIncome){
        double taxWithoutCess= 0;
        double taxWithCess = 0;

        if (taxableIncome>=15_00_000){
            // income is more than 15 lakh
            taxWithoutCess = 1_87_500 + 30*(taxableIncome-15_00_000)/100;
        }
        else if(taxableIncome>=12_50_000){
            // income is between 12.5 lakh to 15 lakh
            taxWithoutCess = 1_25_500 + 25*(taxableIncome-12_50_000)/100;
        }
        else if(taxableIncome>=10_00_000){
            // income is between 10 lakh to 12.5 lakh
            taxWithoutCess = 75_000 + 20*(taxableIncome-10_00_000)/100;
        }
        else if(taxableIncome>=7_50_000){
            // income is between 7.5 lakh to 10 lakh
            taxWithoutCess = 37_500 + 15*(taxableIncome-7_50_000)/100;
        }
        else if(taxableIncome>=5_00_000){
            // income is between 5 lakh to 7.5 lakh
            taxWithoutCess = 12_500 + 10*(taxableIncome-5_00_000)/100;
        }
        else if(taxableIncome>=2_50_000){
            // income is between 2.5 lakh to 5 lakh
            taxWithoutCess =  5*(taxableIncome-2_50_000)/100;
        }
        else {
            // income is less than 2.5 lakh
            taxWithoutCess =0;
        }
        taxWithCess = taxWithoutCess + 4*taxWithoutCess/100;

        return taxWithCess;
    }
    // Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();
//        // You cannot create an array using a long type.// It needs to be an int type.// Before converting to an int type, check// to ensure that file is not larger than Integer.MAX_VALUE.
//        if (length > Integer.MAX_VALUE) {
//            // File is too large
//        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public static String decimalFormatTwoDigit(Double num){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(num);
    }

    public static String convertToBase64(Path path) {
        byte[] imageAsBytes = new byte[0];
        try {
            Resource resource = new UrlResource(path.toUri());
            InputStream inputStream = resource.getInputStream();
            imageAsBytes = IOUtils.toByteArray(inputStream);

        } catch (IOException e) {
            System.out.println("\n File read Exception");
        }

        return Base64.getEncoder().encodeToString(imageAsBytes);
    }
}
