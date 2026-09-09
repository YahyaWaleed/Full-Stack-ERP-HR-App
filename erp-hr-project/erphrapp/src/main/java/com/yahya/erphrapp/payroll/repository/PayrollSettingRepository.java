package com.yahya.erphrapp.payroll.repository;

import com.yahya.erphrapp.payroll.entity.PayrollSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollSettingRepository extends JpaRepository<PayrollSetting, Integer> {
}
