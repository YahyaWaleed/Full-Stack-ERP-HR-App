package com.yahya.erphrapp.leaves.service;

import com.yahya.erphrapp.leaves.repository.PublicHolidayRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// counts the days a leave actually costs: excludes the Friday/Saturday weekend and public holidays
@Component
public class WorkingDayCalculator {

    private static final Set<DayOfWeek> WEEKEND = Set.of(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

    private final PublicHolidayRepository holidayRepository;

    public WorkingDayCalculator(PublicHolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public int workingDaysBetween(LocalDate start, LocalDate end) {
        Set<LocalDate> holidays = new HashSet<>(holidayRepository.findDatesBetween(start, end));
        int count = 0;
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            if (!WEEKEND.contains(day.getDayOfWeek()) && !holidays.contains(day)) {
                count++;
            }
        }
        return count;
    }
}
