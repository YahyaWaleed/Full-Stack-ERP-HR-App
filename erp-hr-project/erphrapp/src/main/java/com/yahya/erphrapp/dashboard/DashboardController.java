package com.yahya.erphrapp.dashboard;

import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import com.yahya.erphrapp.leaves.repository.LeaveRequestRepository;
import com.yahya.erphrapp.loans.entity.Loan;
import com.yahya.erphrapp.loans.repository.LoanRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LoanRepository loanRepository;

    public DashboardController(EmployeeRepository employeeRepository,
                               LeaveRequestRepository leaveRequestRepository,
                               LoanRepository loanRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/summary")
    public DashboardSummary getSummary() {
        long employeeCount = employeeRepository.countByEmpStatus(Employee.EmployeeStatus.ACTIVE);
        long pendingLeaves = leaveRequestRepository.countByStatus(LeaveRequest.LeaveStatus.PENDING);
        long activeLoans = loanRepository.countByStatus(Loan.LoanStatus.ACTIVE);

        return new DashboardSummary(employeeCount, pendingLeaves, activeLoans);
    }
}