package com.yahya.erphrapp.leaves.repository;

import com.yahya.erphrapp.leaves.entity.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, LocalDate> {

    @Query("select h.date from PublicHoliday h where h.date between :from and :to")
    List<LocalDate> findDatesBetween(LocalDate from, LocalDate to);
}
