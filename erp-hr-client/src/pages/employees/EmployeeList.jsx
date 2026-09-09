
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';

function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [error, setError] = useState('');
  const [search, setSearch] = useState(""); // for search input field

  const [branchFilter, setBranchFilter] = useState("");
  const [departmentFilter, setDepartmentFilter] = useState("");
  const [jobTitleFilter, setJobTitleFilter] = useState("");


  useEffect(() => {
    apiClient.get('/employees')
      .then((data) => setEmployees(data)) // get the list of employees from the backend and set it to employees array
      .catch((err) => setError(err.message));
  }, []); // empty array = run this once, when the page first loads

  // get unique branches, departments, and job titles from employees
  const branches = [...new Set(
    employees
      .map((employee) => employee.branchName)
      .filter(Boolean)
  )];

  const departments = [...new Set(
    employees
      .map((employee) => employee.departmentName)
      .filter(Boolean)
  )];

  const jobTitles = [...new Set(
    employees
      .map((employee) => employee.jobTitleName)
      .filter(Boolean)
  )];


  // filter employees based on search input and selected filters
  const filteredEmployees = employees.filter((employee) => {
    const searchTerm = search.toLowerCase();

    const matchesSearch =
      employee.fullNameEn.toLowerCase().includes(searchTerm) ||
      employee.empCode.toLowerCase().includes(searchTerm);

    const matchesBranch =
      !branchFilter || employee.branchName === branchFilter;

    const matchesDepartment =
      !departmentFilter || employee.departmentName === departmentFilter;

    const matchesJobTitle =
      !jobTitleFilter || employee.jobTitleName === jobTitleFilter;

    return (
      matchesSearch &&
      matchesBranch &&
      matchesDepartment &&
      matchesJobTitle
    );
  });

  return (
    <div>
      <h1>All Employees</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      
      <input
        type="text"
        placeholder="Search by name or employee code..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <br />
      <br />

      {/* Branch filter */}
      <select
        value={branchFilter}
        onChange={(e) => setBranchFilter(e.target.value)}
        style={{
          padding: '10px 14px',
          marginRight: '10px',
          border: '1px solid #ccc',
          borderRadius: '6px',
          backgroundColor: '#fff',
          fontSize: '14px',
          color: '#333',
          cursor: 'pointer',
          outline: 'none'
        }}
      >
        <option value="">All Branches</option>

        {branches.map((branch) => (
          <option key={branch} value={branch}>
            {branch}
          </option>
        ))}
      </select>

      {/* Department filter */}
      <select
        value={departmentFilter}
        onChange={(e) => setDepartmentFilter(e.target.value)}
        style={{
          padding: '10px 14px',
          marginRight: '10px',
          border: '1px solid #ccc',
          borderRadius: '6px',
          backgroundColor: '#fff',
          fontSize: '14px',
          color: '#333',
          cursor: 'pointer',
          outline: 'none'
        }}
      >
        <option value="">All Departments</option>

        {departments.map((department) => (
          <option key={department} value={department}>
            {department}
          </option>
        ))}
      </select>

      {/* Job Title filter */}
      <select
        value={jobTitleFilter}
        onChange={(e) => setJobTitleFilter(e.target.value)}
        style={{
          padding: '10px 14px',
          marginRight: '10px',
          border: '1px solid #ccc',
          borderRadius: '6px',
          backgroundColor: '#fff',
          fontSize: '14px',
          color: '#333',
          cursor: 'pointer',
          outline: 'none'
        }}
      >
        <option value="">All Job Titles</option>

        {jobTitles.map((jobTitle) => (
          <option key={jobTitle} value={jobTitle}>
            {jobTitle}
          </option>
        ))}
      </select>

      <br />
      <br />

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Code</th>
            <th>Name</th>
            <th>Branch</th>
            <th>Department</th>
            <th>Job Title</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {filteredEmployees.map((emp) => (
            <tr key={emp.id}>
              <td>{emp.empCode}</td>
              <td>{emp.fullNameEn}</td>
              <td>{emp.branchName}</td>
              <td>{emp.departmentName}</td>
              <td>{emp.jobTitleName}</td>
              <td><span className={statusClass(emp.empStatus)}>{emp.empStatus}</span></td>
              <td><Link to={`/dashboard/employees/${emp.id}`}>View</Link></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default EmployeeList;
