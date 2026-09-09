import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';

function EmployeeCreate() {
  const navigate = useNavigate();
  const [error, setError] = useState('');

  const [departments, setDepartments] = useState([]);
  const [jobTitles, setJobTitles] = useState([]);
  const [branches, setBranches] = useState([]);
  const [employees, setEmployees] = useState([]);

  const [form, setForm] = useState({
    fullNameAr: '',
    fullNameEn: '',
    gender: 'M',
    birthDate: '',
    nationalId: '',
    maritalStatus: 'SINGLE',
    dependents: 0,
    email: '',
    mobile: '',
    address: '',
    hireDate: '',
    deptId: '',
    jobId: '',
    branchId: '',
    managerId: '',
    insuranceNo: '',
    bankName: '',
    bankAccount: '',
    paymentMethod: 'BANK',
    contract: {
      contractType: 'PERMANENT',
      startDate: '',
      endDate: '',
      basicSalary: '',
      currency: 'EGP',
      weeklyHours: 40,
      annualLeaveDays: 21,
      probationMonths: 3,
      notes: '',
    },
  });

  // loads departments, job titles, branches and employees from the backend
  useEffect(() => {
    const loadOptions = async () => {
      try {
        const [
          departmentsData,
          jobTitlesData,
          branchesData,
          employeesData,
        ] = await Promise.all([
          apiClient.get('/departments'),
          apiClient.get('/jobs'),
          apiClient.get('/branches'),
          apiClient.get('/employees'),
        ]);

        setDepartments(departmentsData);
        setJobTitles(jobTitlesData);
        setBranches(branchesData);
        setEmployees(employeesData);
      } catch (err) {
        setError(err.message);
      }
    };

    loadOptions();
  }, []);

  // updates a top-level field, e.g. empCode, fullNameEn
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // updates a field INSIDE the nested contract object
  const handleContractChange = (e) => {
    setForm({
      ...form,
      contract: { ...form.contract, [e.target.name]: e.target.value },
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await apiClient.post('/employees', form);
      navigate('/dashboard/employees/list');
    } catch (err) {
      setError(err.message);
    }
  };

  // get the job titles that are marked as managerial
  const managerialJobTitles = jobTitles.filter(
    (job) => job.managerial === true
  );

  const managers = employees.filter((employee) =>
    managerialJobTitles.some(
      (job) =>
        job.titleEn?.trim().toLowerCase() ===
        employee.jobTitleName?.trim().toLowerCase()
    )
  );

  return (
    <div>
      <h1>Create Employee</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <form onSubmit={handleSubmit}>
        <h3>Personal Details</h3>

        <label htmlFor="fullNameEn">Full Name (English)</label>
        <input
          id="fullNameEn"
          name="fullNameEn"
          placeholder="Enter full name in English"
          value={form.fullNameEn}
          onChange={handleChange}
        />

        <label htmlFor="fullNameAr">Full Name (Arabic)</label>
        <input
          id="fullNameAr"
          name="fullNameAr"
          placeholder="Enter full name in Arabic"
          value={form.fullNameAr}
          onChange={handleChange}
        />

        <label htmlFor="gender">Gender</label>
        <select
          id="gender"
          name="gender"
          value={form.gender}
          onChange={handleChange}
        >
          <option value="M">Male</option>
          <option value="F">Female</option>
        </select>

        <label htmlFor="birthDate">Birth Date</label>
        <input
          type="date"
          id="birthDate"
          name="birthDate"
          value={form.birthDate}
          onChange={handleChange}
        />

        <label htmlFor="nationalId">National ID</label>
        <input
          id="nationalId"
          name="nationalId"
          placeholder="Enter national ID"
          value={form.nationalId}
          onChange={handleChange}
        />

        <label htmlFor="maritalStatus">Marital Status</label>
        <select
          id="maritalStatus"
          name="maritalStatus"
          value={form.maritalStatus}
          onChange={handleChange}
        >
          <option value="SINGLE">Single</option>
          <option value="MARRIED">Married</option>
          <option value="DIVORCED">Divorced</option>
          <option value="WIDOWED">Widowed</option>
        </select>

        <label htmlFor="dependents">Number of Dependents</label>
        <input
          type="number"
          id="dependents"
          name="dependents"
          min="0"
          value={form.dependents}
          onChange={handleChange}
        />

        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          name="email"
          placeholder="Enter email"
          value={form.email}
          onChange={handleChange}
        />

        <label htmlFor="mobile">Mobile</label>
        <input
          id="mobile"
          name="mobile"
          placeholder="Enter mobile number"
          value={form.mobile}
          onChange={handleChange}
        />

        <label htmlFor="address">Address</label>
        <input
          id="address"
          name="address"
          placeholder="Enter address"
          value={form.address}
          onChange={handleChange}
        />

        <h3>Job Details</h3>

        <label htmlFor="hireDate">Hire Date</label>
        <input
          type="date"
          id="hireDate"
          name="hireDate"
          value={form.hireDate}
          onChange={handleChange}
          required
        />

        <label htmlFor="deptId">Department</label>
        <select
          id="deptId"
          name="deptId"
          value={form.deptId}
          onChange={handleChange}
          required
        >
          <option value="">Select Department</option>

          {departments.map((department) => (
            <option key={department.id} value={department.id}>
              {department.nameEn}
            </option>
          ))}
        </select>

        <label htmlFor="jobId">Job Title</label>
        <select
          id="jobId"
          name="jobId"
          value={form.jobId}
          onChange={handleChange}
          required
        >
          <option value="">Select Job Title</option>

          {jobTitles.map((job) => (
            <option key={job.id} value={job.id}>
              {job.titleEn}
            </option>
          ))}
        </select>

        <label htmlFor="branchId">Branch</label>
        <select
          id="branchId"
          name="branchId"
          value={form.branchId}
          onChange={handleChange}
          required
        >
          <option value="">Select Branch</option>

          {branches.map((branch) => (
            <option key={branch.id} value={branch.id}>
              {branch.nameEn}
            </option>
          ))}
        </select>

        <label htmlFor="managerId">Manager</label>
        <select
          id="managerId"
          name="managerId"
          value={form.managerId}
          onChange={handleChange}
        >
          <option value="">No Manager</option>

          {managers.map((employee) => (
            <option key={employee.id} value={employee.id}>
              {employee.fullNameEn} - {employee.jobTitleName}
            </option>
          ))}
        </select>

        <h3>Bank & Insurance</h3>

        <label htmlFor="insuranceNo">Insurance No.</label>
        <input
          id="insuranceNo"
          name="insuranceNo"
          placeholder="Enter insurance number"
          value={form.insuranceNo}
          onChange={handleChange}
        />

        <label htmlFor="bankName">Bank Name</label>
        <input
          id="bankName"
          name="bankName"
          placeholder="Enter bank name"
          value={form.bankName}
          onChange={handleChange}
        />

        <label htmlFor="bankAccount">Bank Account</label>
        <input
          id="bankAccount"
          name="bankAccount"
          placeholder="Enter bank account"
          value={form.bankAccount}
          onChange={handleChange}
        />

        <label htmlFor="paymentMethod">Payment Method</label>
        <select
          id="paymentMethod"
          name="paymentMethod"
          value={form.paymentMethod}
          onChange={handleChange}
        >
          <option value="BANK">Bank Transfer</option>
          <option value="CASH">Cash</option>
          <option value="CHEQUE">Cheque</option>
        </select>

        <h3>Initial Contract</h3>

        <label htmlFor="contractType">Contract Type</label>
        <select
          id="contractType"
          name="contractType"
          value={form.contract.contractType}
          onChange={handleContractChange}
        >
          <option value="PERMANENT">Permanent</option>
          <option value="FIXED_TERM">Fixed Term</option>
          <option value="PART_TIME">Part Time</option>
          <option value="CONSULTANT">Consultant</option>
          <option value="INTERN">Intern</option>
        </select>

        <label htmlFor="startDate">Contract Start Date</label>
        <input
          type="date"
          id="startDate"
          name="startDate"
          value={form.contract.startDate}
          onChange={handleContractChange}
        />

        <label htmlFor="endDate">Contract End Date</label>
        <input
          type="date"
          id="endDate"
          name="endDate"
          value={form.contract.endDate}
          onChange={handleContractChange}
        />

        <label htmlFor="basicSalary">Basic Salary</label>
        <input
          type="number"
          id="basicSalary"
          name="basicSalary"
          placeholder="Enter basic salary"
          value={form.contract.basicSalary}
          onChange={handleContractChange}
        />

        <label htmlFor="currency">Currency</label>
        <select
          id="currency"
          name="currency"
          value={form.contract.currency}
          onChange={handleContractChange}
        >
          <option value="EGP">EGP</option>
          <option value="KWD">KWD</option>
          <option value="MYR">MYR</option>
          <option value="USD">USD</option>
        </select>

        <label htmlFor="weeklyHours">Weekly Hours</label>
        <input
          type="number"
          id="weeklyHours"
          name="weeklyHours"
          min="1"
          value={form.contract.weeklyHours}
          onChange={handleContractChange}
        />

        <label htmlFor="annualLeaveDays">Annual Leave Days</label>
        <input
          type="number"
          id="annualLeaveDays"
          name="annualLeaveDays"
          min="0"
          value={form.contract.annualLeaveDays}
          onChange={handleContractChange}
        />

        <label htmlFor="probationMonths">Probation Period (Months)</label>
        <input
          type="number"
          id="probationMonths"
          name="probationMonths"
          min="0"
          value={form.contract.probationMonths}
          onChange={handleContractChange}
        />

        <label htmlFor="notes">Contract Notes</label>
        <textarea
          id="notes"
          name="notes"
          value={form.contract.notes}
          onChange={handleContractChange}
          placeholder="Additional contract notes"
          rows="6"
          cols="50"
        />

        <br />
        <br />

        <button type="submit">Create Employee</button>
      </form>
    </div>
  );
}

export default EmployeeCreate;