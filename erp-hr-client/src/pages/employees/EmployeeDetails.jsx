import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { useAuth } from '../../auth/AuthContext';

function EmployeeDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [employee, setEmployee] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get(`/employees/${id}`)
      .then((data) => setEmployee(data))
      .catch((err) => setError(err.message));
  }, [id]);

  const handleTerminate = async () => {
    if (!window.confirm('Are you sure you want to terminate this employee?')) return;
    try {
      await apiClient.post(`/employees/${id}/terminate`);
      navigate('/dashboard/employees/list');
    } catch (err) {
      setError(err.message);
    }
  };

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!employee) return <p>Loading...</p>;

  return (
    <div>
      <h1>{employee.fullNameEn}</h1>
      <p><strong>Code:</strong> {employee.empCode}</p>
      <p><strong>Department:</strong> {employee.departmentName}</p>
      <p><strong>Job Title:</strong> {employee.jobTitleName}</p>
      <p><strong>Branch:</strong> {employee.branchName}</p>
      <p><strong>Manager:</strong> {employee.managerName || 'None'}</p>
      <p><strong>Status:</strong> {employee.empStatus}</p>
      <p><strong>Email:</strong> {employee.email}</p>
      <p><strong>Mobile:</strong> {employee.mobile}</p>

      {isAdmin && employee.empStatus !== 'TERMINATED' && (
        <button onClick={handleTerminate}>Terminate Employee</button>
      )}
    </div>
  );
}

export default EmployeeDetails;