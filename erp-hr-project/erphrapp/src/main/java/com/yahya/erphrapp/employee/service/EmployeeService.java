package com.yahya.erphrapp.employee.service;

import com.yahya.erphrapp.audit.AuditService;
import com.yahya.erphrapp.employee.dto.EmployeeRequest;
import com.yahya.erphrapp.employee.dto.EmployeeResponse;
import com.yahya.erphrapp.employee.dto.TerminateEmployeeRequest;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.entity.EmployeeContract;
import com.yahya.erphrapp.employee.mapper.EmployeeMapper;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.employee.repository.EmployeeSpecifications;
import com.yahya.erphrapp.exception.BadRequestException;
import com.yahya.erphrapp.exception.ConflictException;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import com.yahya.erphrapp.leaves.repository.LeaveRequestRepository;
import com.yahya.erphrapp.leaves.service.LeaveBalanceService;
import com.yahya.erphrapp.loans.entity.Loan;
import com.yahya.erphrapp.loans.repository.LoanRepository;
import com.yahya.erphrapp.organization.repository.BranchRepository;
import com.yahya.erphrapp.organization.repository.DepartmentRepository;
import com.yahya.erphrapp.organization.repository.JobTitleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final JobTitleRepository jobTitleRepository;
    private final EmployeeContractService contractService;
    private final LeaveBalanceService leaveBalanceService;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LoanRepository loanRepository;
    private final AuditService auditService;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
                           BranchRepository branchRepository, DepartmentRepository departmentRepository,
                           JobTitleRepository jobTitleRepository, EmployeeContractService contractService,
                           LeaveBalanceService leaveBalanceService, LeaveRequestRepository leaveRequestRepository,
                           LoanRepository loanRepository, AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.branchRepository = branchRepository;
        this.departmentRepository = departmentRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.contractService = contractService;
        this.leaveBalanceService = leaveBalanceService;
        this.leaveRequestRepository = leaveRequestRepository;
        this.loanRepository = loanRepository;
        this.auditService = auditService;
    }

    // create an employee together with their first contract and leave balances -- all or nothing
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (request.getContract() == null) {
            throw new BadRequestException("contract: the first contract is required when creating an employee");
        }
        Employee employee = new Employee();
        applyRequest(employee, request);
        employee.setEmpStatus(Employee.EmployeeStatus.ACTIVE);
        employeeRepository.saveAndFlush(employee); // one insert; the database assigns emp_code

        EmployeeContract contract = contractService.createInitial(employee, request.getContract());
        leaveBalanceService.initializeFor(employee, contract.getStartDate().getYear(), contract.getAnnualLeaveDays());

        auditService.record("EMPLOYEE_CREATED", "EMPLOYEE", employee.getEmpCode(),
                "contract=" + contract.getContractNo() + " basic=" + contract.getBasicSalary());
        return employeeMapper.toResponse(employee);
    }

    // list employees; every filter is optional (GET /employees?branchId=&deptId=&jobId=&status=&managerial=&q=)
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployees(Long branchId, Long deptId, Long jobId, Employee.EmployeeStatus status,
                                               Boolean managerial, String q, Pageable pageable) {
        boolean admin = isCurrentUserAdmin();
        return employeeRepository.findAll(EmployeeSpecifications.filter(branchId, deptId, jobId, status, managerial, q), pageable)
                .map(e -> maskFor(admin, employeeMapper.toResponse(e)));
    }

    // read one employee
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        return maskFor(isCurrentUserAdmin(), employeeMapper.toResponse(employee));
    }

    // update an employee; if the client sends the version it loaded, a concurrent edit is reported as 409
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        if (request.getVersion() != null && request.getVersion() != employee.getVersion()) {
            throw new ObjectOptimisticLockingFailureException(Employee.class, id);
        }
        if (request.getManagerId() != null && request.getManagerId().equals(id)) {
            throw new BadRequestException("An employee cannot be their own manager");
        }
        String before = employee.getJobTitle().getTitleEn() + "/" + employee.getDepartment().getNameEn();
        applyRequest(employee, request);
        employeeRepository.saveAndFlush(employee);

        auditService.record("EMPLOYEE_UPDATED", "EMPLOYEE", employee.getEmpCode(),
                "job/dept before=" + before + " after=" + employee.getJobTitle().getTitleEn() + "/" + employee.getDepartment().getNameEn());
        return employeeMapper.toResponse(employee);
    }

    // termination as one unit: status + date, active contract closed, pending leave and future approved leave
    // cancelled (the trigger gives the days back). Outstanding loans must be settled first.
    @Transactional
    public EmployeeResponse terminateEmployee(Long id, TerminateEmployeeRequest request) {
        Employee employee = employeeRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        LocalDate date = request != null && request.getTerminationDate() != null ? request.getTerminationDate() : LocalDate.now();

        List<Loan> outstanding = loanRepository.findAllByEmployeeId(id).stream()
                .filter(l -> l.getStatus() == Loan.LoanStatus.ACTIVE && l.getRemainingBalance().compareTo(BigDecimal.ZERO) > 0)
                .toList();
        if (!outstanding.isEmpty()) {
            BigDecimal owed = outstanding.stream().map(Loan::getRemainingBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            throw new ConflictException("Employee still owes " + owed + " on " + outstanding.size()
                    + " active loan(s) (ids " + outstanding.stream().map(l -> String.valueOf(l.getId())).toList()
                    + "). Close or cancel them before terminating.");
        }

        employee.terminate(date);
        employeeRepository.save(employee);

        Optional<EmployeeContract> contract = contractService.closeActiveOnTermination(id, date);

        int cancelled = 0;
        for (LeaveRequest leave : leaveRequestRepository.findAllByEmployeeId(id)) {
            boolean pending = leave.getStatus() == LeaveRequest.LeaveStatus.PENDING;
            boolean approvedAfterExit = leave.getStatus() == LeaveRequest.LeaveStatus.APPROVED && leave.getStartDate().isAfter(date);
            if (pending || approvedAfterExit) {
                leave.setStatus(LeaveRequest.LeaveStatus.CANCELLED);
                leave.setDecidedOn(LocalDate.now());
                leaveRequestRepository.save(leave);
                cancelled++;
            }
        }

        auditService.record("EMPLOYEE_TERMINATED", "EMPLOYEE", employee.getEmpCode(),
                "date=" + date + " contract=" + contract.map(EmployeeContract::getContractNo).orElse("none")
                        + " leaveCancelled=" + cancelled
                        + (request != null && request.getReason() != null ? " reason=" + request.getReason() : ""));
        return employeeMapper.toResponse(employee);
    }

    // copies the request onto the entity and resolves the referenced branch, department, job title and manager
    private void applyRequest(Employee employee, EmployeeRequest request) {
        employeeMapper.copyFields(request, employee);
        // a blank optional field means "none": stored as NULL, so it never clashes with the UNIQUE email/insurance_no columns
        employee.setEmail(blankToNull(employee.getEmail()));
        employee.setInsuranceNo(blankToNull(employee.getInsuranceNo()));
        employee.setMobile(blankToNull(employee.getMobile()));
        employee.setAddress(blankToNull(employee.getAddress()));
        employee.setBankName(blankToNull(employee.getBankName()));
        employee.setBankAccount(blankToNull(employee.getBankAccount()));
        employee.setBranch(branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", request.getBranchId())));
        employee.setDepartment(departmentRepository.findById(request.getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDeptId())));
        employee.setJobTitle(jobTitleRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job Title", request.getJobId())));
        employee.setManager(request.getManagerId() == null ? null : employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager", request.getManagerId())));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    // HR_USER may see employees but not their bank accounts
    private static EmployeeResponse maskFor(boolean admin, EmployeeResponse response) {
        if (!admin) {
            response.setBankAccount(null);
        }
        return response;
    }

    private static boolean isCurrentUserAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_HR_ADMIN"));
    }
}
