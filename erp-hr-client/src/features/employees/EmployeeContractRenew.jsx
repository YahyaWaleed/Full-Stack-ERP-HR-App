import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { ContractFields } from './EmployeeForm';
import { employeesApi } from './api';

function EmployeeContractRenew() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [error, setError] = useState(null);
  const [contract, setContract] = useState({
    contractType: 'PERMANENT', startDate: '', endDate: '', basicSalary: '', currency: 'EGP',
    weeklyHours: 40, annualLeaveDays: 21, probationMonths: 0, notes: '',
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await employeesApi.renewContract(id, contract);
      navigate(`/dashboard/employees/${id}`);
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h1>Renew Contract</h1>
      <p className="muted">
        This creates the next contract. The current one ends the day before the new start date; its originally agreed
        end date is kept on record.
      </p>
      <ErrorMessage error={error} />
      <form className="form-grid" onSubmit={handleSubmit}>
        <fieldset>
          <legend>New Contract</legend>
          <ContractFields contract={contract} onChange={(e) => setContract({ ...contract, [e.target.name]: e.target.value })} />
        </fieldset>
        <div className="form-actions"><button type="submit">Renew Contract</button></div>
      </form>
    </div>
  );
}

export default EmployeeContractRenew;
