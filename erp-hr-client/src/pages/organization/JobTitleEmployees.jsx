
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';

function JobTitleEmployees() {
  const { jobTitleId } = useParams();
  const navigate = useNavigate();

  const [employees, setEmployees] = useState([]);
  const [jobTitle, setJobTitle] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get(`/employees/job/${jobTitleId}`)
      .then(setEmployees)
      .catch((err) => setError(err.message));

    apiClient.get(`/jobs/${jobTitleId}`)
      .then(setJobTitle)
      .catch((err) => setError(err.message));
  }, [jobTitleId]);

  return (
    <div>
      <h1> {jobTitle && jobTitle.nameEn} <h4>Job Title Employees</h4></h1>

      

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <button onClick={() => navigate('/dashboard/jobs')}>
        Back to Job Titles
      </button>

      <br />
      <br />

      {employees.length === 0 && !error ? (
        <p>No employees found in this job title.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>Employee Code</th>
              <th>Full Name</th>
              <th>Email</th>
              <th>Department</th>
              <th>Job Title</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            {employees.map((employee) => (
              <tr key={employee.id}>
                <td>{employee.empCode}</td>
                <td>{employee.fullNameEn}</td>
                <td>{employee.email}</td>
                <td>{employee.departmentName || '-'}</td>
                <td>{employee.jobTitleName || '-'}</td>
                <td>{employee.empStatus || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default JobTitleEmployees;
