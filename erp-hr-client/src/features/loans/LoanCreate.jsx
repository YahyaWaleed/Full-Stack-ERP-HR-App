import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import EmployeePicker from '../../shared/components/EmployeePicker';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { LOAN_TYPES, loansApi } from './api';

function LoanCreate() {
  const navigate = useNavigate();
  const [employee, setEmployee] = useState(null);
  const [approver, setApprover] = useState(null);
  const [form, setForm] = useState({
    type: 'PERSONAL', principalAmount: '', installmentsCount: '', startPeriod: '',
    requestDate: new Date().toISOString().slice(0, 10),
  });
  const [error, setError] = useState(null);
  const set = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  // calculated, not typed: principal spread evenly (rounded up to the cent so the installments cover it)
  const monthlyInstallment = form.principalAmount && form.installmentsCount
    ? (Math.ceil((Number(form.principalAmount) / Number(form.installmentsCount)) * 100) / 100).toFixed(2)
    : '';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const loan = await loansApi.create({ ...form, empId: employee.id, approvedById: approver?.id ?? '', monthlyInstallment });
      navigate(`/dashboard/loans/${loan.id}`);
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h1>Create Loan</h1>
      <ErrorMessage error={error} />
      <form className="form-grid" onSubmit={handleSubmit}>
        <fieldset>
          <legend>Loan</legend>
          <label htmlFor="loan-employee">Employee</label>
          <EmployeePicker id="loan-employee" value={employee} onChange={setEmployee} filters={{ status: 'ACTIVE' }} />

          <label htmlFor="type">Loan Type</label>
          <select id="type" name="type" value={form.type} onChange={set}>
            {LOAN_TYPES.map(([v, t]) => <option key={v} value={v}>{t}</option>)}
          </select>

          <label htmlFor="principalAmount">Principal Amount</label>
          <input id="principalAmount" type="number" min="1" step="0.01" name="principalAmount" value={form.principalAmount} onChange={set} required />

          <label htmlFor="installmentsCount">Number of Installments</label>
          <input id="installmentsCount" type="number" min="1" max="120" name="installmentsCount" value={form.installmentsCount} onChange={set} required />

          <label htmlFor="monthly">Monthly Installment (calculated)</label>
          <input id="monthly" value={monthlyInstallment} disabled />

          <label htmlFor="startPeriod">First Deduction Period (YYYY-MM)</label>
          <input id="startPeriod" type="month" name="startPeriod" value={form.startPeriod} onChange={set} required />

          <label htmlFor="requestDate">Request Date</label>
          <input id="requestDate" type="date" name="requestDate" value={form.requestDate} onChange={set} required />

          <label htmlFor="loan-approver">Approved By (manager, optional)</label>
          <EmployeePicker id="loan-approver" value={approver} onChange={setApprover} filters={{ managerial: 'true' }} />
        </fieldset>
        <div className="form-actions">
          <button type="submit" disabled={!employee}>Create Loan</button>
        </div>
      </form>
    </div>
  );
}

export default LoanCreate;
