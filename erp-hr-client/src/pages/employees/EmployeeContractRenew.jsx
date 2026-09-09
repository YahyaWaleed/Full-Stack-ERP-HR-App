import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';

function EmployeeContractRenew() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [error, setError] = useState('');

  const [form, setForm] = useState({
    contractType: 'PERMANENT',
    startDate: '',
    endDate: '',
    basicSalary: '',
    currency: 'EGP',
    weeklyHours: 40,
    annualLeaveDays: 21,
    probationMonths: 0,
    notes: '',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await apiClient.post(`/employees/${id}/contracts`, form);
      navigate(`/dashboard/employees/${id}`);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h1>Renew Contract</h1>
      <p>This creates a new contract for this employee. Their current active contract will close automatically.</p>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <form onSubmit={handleSubmit}>
        <label>Contract Type</label><br />
        <select name="contractType" value={form.contractType} onChange={handleChange} required>
          <option value="PERMANENT">Permanent</option>
          <option value="FIXED_TERM">Fixed Term</option>
          <option value="PART_TIME">Part Time</option>
          <option value="CONSULTANT">Consultant</option>
          <option value="INTERN">Intern</option>
        </select><br /><br />

        <label>Start Date</label><br />
        <input type="date" name="startDate" value={form.startDate} onChange={handleChange} required /><br /><br />

        <label>End Date (optional)</label><br />
        <input type="date" name="endDate" value={form.endDate} onChange={handleChange} /><br /><br />

        <label>Basic Salary</label><br />
        <input name="basicSalary" value={form.basicSalary} onChange={handleChange} required /><br /><br />

        <label>Weekly Hours</label><br />
        <input name="weeklyHours" value={form.weeklyHours} onChange={handleChange} /><br /><br />

        <label>Annual Leave Days</label><br />
        <input name="annualLeaveDays" value={form.annualLeaveDays} onChange={handleChange} /><br /><br />

        <label>Notes</label><br />
        <input name="notes" value={form.notes} onChange={handleChange} /><br /><br />

        <button type="submit">Renew Contract</button>
      </form>
    </div>
  );
}

export default EmployeeContractRenew;