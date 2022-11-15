package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.entity.Holiday;
import com.globalvox.clinicmanagementsystem.entity.enums.TypeOfHoliday;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/holiday")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    /* Holiday CRUD Operations */

    /* Listing of all holidays of current year */
    @RequestMapping("/show")
    public String showHolidays(Model model) {
        model.addAttribute("holidays", holidayService.findAll());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date date = new Date();
        model.addAttribute("year", formatter.format(date));

        return "holiday/holiday-list";
    }

    /* Add new Holiday */
    @GetMapping("/add")
    public String loadAddHoliday(Model model){
        Holiday holiday = new Holiday();
        model.addAttribute("holiday", holiday);
        model.addAttribute("type", TypeOfHoliday.values());
        return "holiday/add-holiday";
    }

    /*
    * While creating new data, check if from-date is not less than to-date,
    * and from-date must be of current year
    * */
    @PostMapping("/add")
    public String addHoliday(@Valid @ModelAttribute("holiday") Holiday holiday, BindingResult bindingResult, Model model) {
//        int year = new Date().getYear();
//        if(holiday.getFromDate().getYear() != year) {
//            bindingResult.addError(new FieldError("holiday", "fromDate", "Please Enter date from current year!"));
//        }

//        if((holiday.getFromDate().compareTo(holiday.getToDate()) > 0)){
//            bindingResult.addError(new FieldError("holiday","toDate","To-date can't be less than From-date"));
//        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("holiday", holiday);
            model.addAttribute("type", TypeOfHoliday.values());
            return "holiday/add-holiday";
        }

//        long difference = holiday.getToDate().getTime() - holiday.getFromDate().getTime();
//        long days = TimeUnit.MILLISECONDS.toDays(difference);
//        holiday.setDays((int)days+1);

        holidayService.save(holiday);
        return "redirect:/holiday/show";
    }

    /* Edit Holiday Information with proper validation */
    @GetMapping("/edit")
    public String loadEditHoliday(@RequestParam("holidayId") Long holidayId, Model model) {
        Optional<Holiday> holiday = holidayService.findById(holidayId);
        if(holiday.isPresent()) {
            model.addAttribute("holiday", holiday.get());
            model.addAttribute("type", TypeOfHoliday.values());
        } else {
            throw new NotFoundException("Holiday Not Found!");
        }
        return "holiday/edit-holiday";
    }

    @PostMapping("/edit")
    public String editHoliday(@ModelAttribute("holiday") Holiday holiday, BindingResult bindingResult, Model model) {
//        int year = new Date().getYear();
//        if(holiday.getFromDate().getYear() != year) {
//            bindingResult.addError(new FieldError("holiday", "fromDate", "Please Enter date from current year!"));
//        }
//        if((holiday.getFromDate().compareTo(holiday.getToDate()) > 0)){
//            bindingResult.addError(new FieldError("holiday","fromDate","To-date can't be less than From-date"));
//        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("holiday", holiday);
            model.addAttribute("type", TypeOfHoliday.values());
            return "redirect:/holiday/show";
        }

//        long difference = holiday.getToDate().getTime() - holiday.getFromDate().getTime();
//        long days = TimeUnit.MILLISECONDS.toDays(difference);
//        holiday.setDays((int)days+1);

        holidayService.save(holiday);
        return "redirect:/holiday/show";
    }

    /* Delete particular Holiday Information */
    @RequestMapping("/delete")
    public String deleteHoliday(@RequestParam("holidayId") Long holidayId) {
        Optional<Holiday> holiday = holidayService.findById(holidayId);
        if(holiday.isPresent()) {
            holidayService.deleteById(holidayId);
        } else {
            throw new NotFoundException("Holiday Not Found!");
        }
        return "redirect:/holiday/show";
    }

}
