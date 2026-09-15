package com.yahya.erphrapp.employee.service;

import com.yahya.erphrapp.employee.dto.EmployeeContractRequest;
import com.yahya.erphrapp.employee.dto.EmployeeRequest;
import com.yahya.erphrapp.employee.dto.EmployeeResponse;
import com.yahya.erphrapp.employee.entity.Employee;
import com.yahya.erphrapp.employee.entity.EmployeeContract;
import com.yahya.erphrapp.employee.mapper.EmployeeMapper;
import com.yahya.erphrapp.employee.repository.EmployeeContractRepository;
import com.yahya.erphrapp.employee.repository.EmployeeRepository;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.leaves.entity.LeaveBalance;
import com.yahya.erphrapp.leaves.entity.LeaveRequest;
import com.yahya.erphrapp.leaves.entity.LeaveType;
import com.yahya.erphrapp.leaves.repository.LeaveBalanceRepository;
import com.yahya.erphrapp.leaves.repository.LeaveRequestRepository;
import com.yahya.erphrapp.leaves.repository.LeaveTypeRepository;
import com.yahya.erphrapp.organization.entity.Branch;
import com.yahya.erphrapp.organization.entity.Department;
import com.yahya.erphrapp.organization.entity.JobTitle;
import com.yahya.erphrapp.organization.repository.BranchRepository;
import com.yahya.erphrapp.organization.repository.DepartmentRepository;
import com.yahya.erphrapp.organization.repository.JobTitleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {
    // inject the repo and mapper
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private  final JobTitleRepository jobTitleRepository;
    private final EmployeeContractRepository employeeContractRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public EmployeeService(LeaveRequestRepository leaveRequestRepository ,LeaveBalanceRepository leaveBalanceRepository, LeaveTypeRepository leaveTypeRepository, EmployeeMapper employeeMapper, EmployeeRepository employeeRepository, BranchRepository branchRepository, DepartmentRepository departmentRepository, JobTitleRepository jobTitleRepository, EmployeeContractRepository employeeContractRepository) {
        this.employeeMapper = employeeMapper;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.employeeRepository = employeeRepository;
        this.branchRepository = branchRepository;
        this.departmentRepository = departmentRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.employeeContractRepository =employeeContractRepository;
    }

    @Transactional
    // create an employee
    public EmployeeResponse createEmployee(EmployeeRequest employeeRequest, EmployeeContractRequest employeeContractRequest) {
        Branch branch = branchRepository.findById(employeeRequest.getBranchId()).orElseThrow(() -> new ResourceNotFoundException("Branch",employeeRequest.getBranchId()));
        Department department = departmentRepository.findById(employeeRequest.getDeptId()).orElseThrow(() -> new ResourceNotFoundException("Department" , employeeRequest.getDeptId()));
        JobTitle title = jobTitleRepository.findById(employeeRequest.getJobId()).orElseThrow(() -> new ResourceNotFoundException("Job Title", employeeRequest.getJobId()));

        Employee manager = null;
        if (employeeRequest.getManagerId() != null) {
            manager = employeeRepository.findById(employeeRequest.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", employeeRequest.getManagerId()));
        }

        Employee employee = new Employee();

        employee.setFullNameAr(employeeRequest.getFullNameAr());
        employee.setFullNameEn(employeeRequest.getFullNameEn());
        employee.setAddress(employeeRequest.getAddress());
        employee.setBankAccount(employeeRequest.getBankAccount());
        employee.setBankName(employeeRequest.getBankName());
        employee.setBranch(branch);
        employee.setBirthDate(employeeRequest.getBirthDate());
        employee.setNationalId(employeeRequest.getNationalId());
        employee.setEmail(employeeRequest.getEmail());
        employee.setDepartment(department);
        employee.setDependents(employeeRequest.getDependents());
        employee.setPaymentMethod(Employee.PaymentMethod.valueOf(employeeRequest.getPaymentMethod()));
        employee.setInsuranceNo(employeeRequest.getInsuranceNo());
        employee.setHireDate(employeeRequest.getHireDate());
        employee.setMaritalStatus(Employee.MaritalStatus.valueOf(employeeRequest.getMaritalStatus()));
        employee.setMobile(employeeRequest.getMobile());
        employee.setManager(manager);
        employee.setJobTitle(title);
        employee.setGender(Employee.Gender.valueOf(employeeRequest.getGender()));
        employee.setEmpStatus(Employee.EmployeeStatus.ACTIVE);
        employee.setEmpCode("TMP-" + UUID.randomUUID().toString().substring(0, 8));
        employeeRepository.save(employee);
        employee.setEmpCode(String.format("EMP-%04d", employee.getId()));


        // create the contract for the employee
        EmployeeContract employeeContract = new EmployeeContract();

        employeeContract.setAnnualLeaveDays(employeeContractRequest.getAnnualLeaveDays());
        employeeContract.setBasicSalary(employeeContractRequest.getBasicSalary());
        employeeContract.setCurrency(employeeContractRequest.getCurrency());
        employeeContract.setContractType(EmployeeContract.ContractType.valueOf(employeeContractRequest.getContractType()));
        employeeContract.setEmployee(employee);
        employeeContract.setNotes(employeeContractRequest.getNotes());
        employeeContract.setStartDate(employeeContractRequest.getStartDate());
        employeeContract.setEndDate(employeeContractRequest.getEndDate());
        employeeContract.setWeeklyHours(employeeContractRequest.getWeeklyHours());
        employeeContract.setProbationMonths(employeeContractRequest.getProbationMonths());
        employeeContract.setStatus(EmployeeContract.ContractStatus.ACTIVE);
        int contractYear = employeeContract.getStartDate().getYear();
        employeeContract.setContractNo(String.format("CT-%d-%03d", contractYear, employee.getId()));

        employeeContractRepository.save(employeeContract);

        // create the leave balance for the new employee
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        for (LeaveType leaveType : leaveTypes) {

            // only create balances for leave types that affect the balance
            if (!leaveType.isAffectsBalance()) {
                continue;
            }

            // check gender restriction
            if (!leaveType.getGenderRestriction().name().equals("ANY")
                    && !leaveType.getGenderRestriction().name().equals(employee.getGender().name())) {
                continue;
            }

            LeaveBalance balance = new LeaveBalance();

            balance.setEmployee(employee);
            balance.setLeaveType(leaveType);
            balance.setFiscalYear(employeeContract.getStartDate().getYear());

            // Annual Leave uses the employee's contract entitlement
            if (leaveType.getCode().equals("ANN")) {
                balance.setEntitledDays(BigDecimal.valueOf(employeeContract.getAnnualLeaveDays()));
            } else {
                balance.setEntitledDays(BigDecimal.valueOf(leaveType.getAnnualQuota()));
            }

            balance.setCarriedForward(BigDecimal.ZERO);
            balance.setUsedDays(BigDecimal.ZERO);

            leaveBalanceRepository.save(balance);
        }
        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee);
    }


    // read all employees
    public Page<EmployeeResponse> getEmployees(Pageable pageable) {
        return employeeRepository.findAllBy(pageable)
                .map(employeeMapper::toResponse);
    }

    // read one employee
    public EmployeeResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        return employeeMapper.toResponse(employee);
    }

    @Transactional
    // update an employee
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest employeeRequest) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id));

        Branch branch = branchRepository.findById(employeeRequest.getBranchId()).orElseThrow(() -> new ResourceNotFoundException("Branch",employeeRequest.getBranchId()));
        Department department = departmentRepository.findById(employeeRequest.getDeptId()).orElseThrow(() -> new ResourceNotFoundException("Department" , employeeRequest.getDeptId()));
        JobTitle title = jobTitleRepository.findById(employeeRequest.getJobId()).orElseThrow(() -> new ResourceNotFoundException("Job Title", employeeRequest.getJobId()));

        Employee manager = null;
        if (employeeRequest.getManagerId() != null) {
            manager = employeeRepository.findById(employeeRequest.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", employeeRequest.getManagerId()));
        }

        employeeRepository.save(employee);
        employee.setEmpCode(String.format("EMP-%04d", employee.getId()));
        employee.setFullNameAr(employeeRequest.getFullNameAr());
        employee.setFullNameEn(employeeRequest.getFullNameEn());
        employee.setAddress(employeeRequest.getAddress());
        employee.setBankAccount(employeeRequest.getBankAccount());
        employee.setBankName(employeeRequest.getBankName());
        employee.setBranch(branch);
        employee.setBirthDate(employeeRequest.getBirthDate());
        employee.setNationalId(employeeRequest.getNationalId());
        employee.setEmail(employeeRequest.getEmail());
        employee.setDepartment(department);
        employee.setDependents(employeeRequest.getDependents());
        employee.setMaritalStatus(Employee.MaritalStatus.valueOf(employeeRequest.getMaritalStatus()));
        employee.setPaymentMethod(Employee.PaymentMethod.valueOf(employeeRequest.getPaymentMethod()));
        employee.setInsuranceNo(employeeRequest.getInsuranceNo());
        employee.setHireDate(employeeRequest.getHireDate());
        employee.setMobile(employeeRequest.getMobile());
        employee.setManager(manager);
        employee.setJobTitle(title);
        employee.setGender(Employee.Gender.valueOf(employeeRequest.getGender()));

        employeeRepository.save(employee);

        return employeeMapper.toResponse(employee);
    }

    // terminate an Employee (sets his contract to TERMINATED
    @Transactional
    public void terminateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        employee.setEmpStatus(Employee.EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);

        // cancel any pending leave requests
        leaveRequestRepository.findAllByEmployeeIdAndStatus(id, LeaveRequest.LeaveStatus.PENDING)
                .forEach(request -> {
                    request.setStatus(LeaveRequest.LeaveStatus.CANCELLED);
                    leaveRequestRepository.save(request);
                });
    }

    // find all employees in one branch
    public List<EmployeeResponse> getEmployeesByBranchId(Long branchId) {
        return employeeRepository.findAllByBranchId(branchId)
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    // find all employees in a department
    public List<EmployeeResponse> getEmployeesByDeptId(Long deptId) {
        return employeeRepository.findAllByDepartmentId(deptId)
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    // find all employees with same job title
    public List<EmployeeResponse> getEmployeesByJobTitleId(Long jobTitleId) {
        return employeeRepository.findAllByJobTitleId(jobTitleId)
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

}
