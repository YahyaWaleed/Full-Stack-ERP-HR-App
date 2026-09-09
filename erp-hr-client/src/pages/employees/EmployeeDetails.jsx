
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';

function EmployeeDetails() {
  const { id } = useParams(); // reads the :id from the URL
  const navigate = useNavigate();
  const [employee, setEmployee] = useState(null);
  const [error, setError] = useState('');

  const [contract, setContract] = useState(null);
  const [showContract, setShowContract] = useState(false);
  const [contractError, setContractError] = useState('');

  useEffect(() => {
    apiClient.get(`/employees/${id}`)
      .then((data) => setEmployee(data))
      .catch((err) => setError(err.message));
  }, [id]); // re-run this if the id in the URL ever changes

  const handleTerminate = async () => {
    if (!window.confirm('Are you sure you want to terminate this employee?')) return;
    try {
      await apiClient.post(`/employees/${id}/terminate`);
      navigate('/dashboard/employees/list');
    } catch (err) {
      setError(err.message);
    }
  };


  const handleViewContract = async () => {
    if (showContract) {
      setShowContract(false);
      return;
    }

    try {
      setContractError('');

      // get all contracts
      const contracts = await apiClient.get('/contracts');

      // find the contract belonging to this employee
      const employeeContract = contracts.find(
        (contract) => contract.empCode === employee.empCode
      );

      if (!employeeContract) {
        setContractError('No contract found for this employee.');
        return;
      }

      // use the contract ID to get the full contract
      const data = await apiClient.get(`/contracts/${employeeContract.id}`);

      setContract(data);
      setShowContract(true);
    } catch (err) {
      setContractError(err.message);
    }
  };



  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!employee) return <p>Loading...</p>;

  return (
    <div>
      <h1>{employee.fullNameEn}</h1>

      <h3>Personal Details</h3>

      <p><strong>Employee Code:</strong> {employee.empCode}</p>
      <p><strong>Full Name (English):</strong> {employee.fullNameEn}</p>
      <p><strong>Full Name (Arabic):</strong> {employee.fullNameAr}</p>
      <p><strong>Gender:</strong> {employee.gender}</p>
      <p><strong>Birth Date:</strong> {employee.birthDate || 'Not provided'}</p>
      <p><strong>National ID:</strong> {employee.nationalId || 'Not provided'}</p>
      <p><strong>Marital Status:</strong> {employee.maritalStatus}</p>
      <p><strong>Dependents:</strong> {employee.dependents}</p>
      <p><strong>Email:</strong> {employee.email || 'Not provided'}</p>
      <p><strong>Mobile:</strong> {employee.mobile || 'Not provided'}</p>
      <p><strong>Address:</strong> {employee.address || 'Not provided'}</p>

      <h3>Job Details</h3>

      <p><strong>Hire Date:</strong> {employee.hireDate}</p>
      <p><strong>Department:</strong> {employee.departmentName}</p>
      <p><strong>Job Title:</strong> {employee.jobTitleName}</p>
      <p><strong>Branch:</strong> {employee.branchName}</p>
      <p><strong>Manager:</strong> {employee.managerName || 'None'}</p>

      <p>
        <strong>Status:</strong>{' '}
        <span className={statusClass(employee.empStatus)}>
          {employee.empStatus}
        </span>
      </p>

      <p>
        <strong>Termination Date:</strong>{' '}
        {employee.terminationDate || 'N/A'}
      </p>

      <h3>Bank & Insurance</h3>

      <p>
        <strong>Insurance No.:</strong>{' '}
        {employee.insuranceNo || 'Not provided'}
      </p>

      <p>
        <strong>Bank Name:</strong>{' '}
        {employee.bankName || 'Not provided'}
      </p>

      <p>
        <strong>Bank Account:</strong>{' '}
        {employee.bankAccount || 'Not provided'}
      </p>

      <p>
        <strong>Payment Method:</strong>{' '}
        {employee.paymentMethod || 'Not provided'}
      </p>

      <h3>System Information</h3>

      <p>
        <strong>Created At:</strong>{' '}
        {employee.createdAt || 'Not available'}
      </p>

      <p>
        <strong>Updated At:</strong>{' '}
        {employee.updatedAt || 'Not available'}
      </p>

      <br />

      <button onClick={handleViewContract}>
        {showContract ? 'Hide Contract' : 'View Contract'}
      </button>

      {contractError && (
        <p style={{ color: 'red' }}>{contractError}</p>
      )}

      {showContract && contract && (
        <div>
          <h3>Employee Contract</h3>

          <p><strong>Contract ID:</strong> {contract.id}</p>
          <p><strong>Employee Code:</strong> {contract.empCode}</p>
          <p><strong>Contract No.:</strong> {contract.contractNo}</p>
          <p><strong>Contract Type:</strong> {contract.contractType}</p>
          <p><strong>Start Date:</strong> {contract.startDate}</p>
          <p><strong>End Date:</strong> {contract.endDate || 'No End Date'}</p>
          <p><strong>Basic Salary:</strong> {contract.basicSalary}</p>
          <p><strong>Currency:</strong> {contract.currency}</p>
          <p><strong>Weekly Hours:</strong> {contract.weeklyHours}</p>
          <p><strong>Annual Leave Days:</strong> {contract.annualLeaveDays}</p>
          <p><strong>Probation Period:</strong> {contract.probationMonths} months</p>
          <p><strong>Status:</strong> {contract.status}</p>
          <p><strong>Notes:</strong> {contract.notes || 'No notes'}</p>
        </div>
      )}

      <br />

      {employee.empStatus !== 'TERMINATED' && (
        <button onClick={handleTerminate}>Terminate Employee</button>
      )}
    </div>
  );
}

export default EmployeeDetails;
