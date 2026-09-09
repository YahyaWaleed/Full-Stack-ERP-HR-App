import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import { useNavigate } from 'react-router-dom';

function JobTitleList() {
  const [jobTitles, setJobTitles] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    apiClient.get('/jobs')
      .then(setJobTitles)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Job Titles</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Code</th>
            <th>Title (EN)</th>
            <th>Title (AR)</th>
            <th>Grade</th>
            <th>Min Salary</th>
            <th>Max Salary</th>
            <th>Managerial</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {jobTitles.map((j) => (
            <tr key={j.id}>
              <td>{j.code}</td>
              <td>{j.titleEn}</td>
              <td>{j.titleAr}</td>
              <td>{j.jobGrade}</td>
              <td>{j.minSalary}</td>
              <td>{j.maxSalary}</td>
              <td>{j.managerial ? 'Yes' : 'No'}</td>
              <td>
                <button onClick={() => navigate(`/dashboard/jobs/${j.id}/employees`)}>
                  View Employees
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default JobTitleList;