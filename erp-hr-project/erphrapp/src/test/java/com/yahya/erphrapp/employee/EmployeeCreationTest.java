package com.yahya.erphrapp.employee;

import com.yahya.erphrapp.AbstractIntegrationTest;
import com.yahya.erphrapp.employee.dto.EmployeeContractRequest;
import com.yahya.erphrapp.employee.dto.EmployeeRequest;
import com.yahya.erphrapp.employee.dto.EmployeeResponse;
import com.yahya.erphrapp.employee.service.EmployeeService;
import com.yahya.erphrapp.leaves.repository.LeaveBalanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeCreationTest extends AbstractIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Test
    void creatingEmployeeAlsoCreatesContractAndLeaveBalances() {
        EmployeeContractRequest contract = new EmployeeContractRequest();
        contract.setContractType("PERMANENT");
        contract.setStartDate(LocalDate.of(2026, 1, 1));
        contract.setBasicSalary(new BigDecimal("30000"));
        contract.setCurrency("EGP");
        contract.setAnnualLeaveDays(21);
        contract.setWeeklyHours(new BigDecimal("40"));
        contract.setProbationMonths(3);

        EmployeeRequest request = new EmployeeRequest();
        request.setFullNameEn("Test Employee");
        request.setFullNameAr("موظف تجريبي");
        request.setGender("M");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setNationalId("12345678901234");
        request.setMaritalStatus("SINGLE");
        request.setDependents(0);
        request.setHireDate(LocalDate.of(2026, 1, 1));
        request.setDeptId(1L);   // adjust to a real seeded department ID
        request.setJobId(1L);    // adjust to a real seeded job title ID
        request.setBranchId(1L); // adjust to a real seeded branch ID
        request.setPaymentMethod("BANK");
        request.setContract(contract);

        EmployeeResponse response = employeeService.createEmployee(request, contract);

        assertThat(response.getEmpCode()).startsWith("EMP-");
        assertThat(leaveBalanceRepository.findAllByEmployeeId(response.getId())).isNotEmpty();
    }
}