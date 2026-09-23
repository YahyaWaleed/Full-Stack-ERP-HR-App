import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { weekdaysBetween } from '../../shared/utils/forms';
import { payrollApi } from './api';

// the month picker fills in the start/end dates; working days exclude Fridays and Saturdays
function PayrollPeriodCreate() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ periodCode: '', startDate: '', endDate: '', payDate: '' });
  const [error, setError] = useState(null);

  const choosePeriod = (e) => {
    const periodCode = e.target.value; // YYYY-MM
    if (!periodCode) {
      setForm({ ...form, periodCode });
      return;
    }
    const [y, m] = periodCode.split('-').map(Number);
    const last = new Date(y, m, 0).getDate();
    setForm({
      periodCode,
      startDate: `${periodCode}-01`,
      endDate: `${periodCode}-${String(last).padStart(2, '0')}`,
      payDate: `${periodCode}-${String(Math.min(27, last)).padStart(2, '0')}`,
    });
  };

  const set = (e) => setForm({ ...form, [e.target.name]: e.target.value });
  const workingDays = weekdaysBetween(form.startDate, form.endDate);
  const fiscalYear = form.periodCode ? Number(form.periodCode.slice(0, 4)) : '';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (form.payDate < form.startDate || form.payDate > form.endDate) {
      setError('Pay date must be within the payroll period.');
      return;
    }
    try {
      await payrollApi.createPeriod({ ...form, workingDays, fiscalYear });
      navigate(`/dashboard/payroll/periods/${form.periodCode}`);
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h1>Open New Payroll Period</h1>
      <ErrorMessage error={error} />
      <form className="form-grid" onSubmit={handleSubmit}>
        <fieldset>
          <legend>Period</legend>
          <label htmlFor="periodCode">Month</label>
          <input id="periodCode" type="month" name="periodCode" value={form.periodCode} onChange={choosePeriod} required />
          <label htmlFor="fiscalYear">Fiscal Year</label>
          <input id="fiscalYear" value={fiscalYear} disabled />
          <label htmlFor="startDate">Start Date</label>
          <input id="startDate" type="date" name="startDate" value={form.startDate} onChange={set} required />
          <label htmlFor="endDate">End Date</label>
          <input id="endDate" type="date" name="endDate" value={form.endDate} min={form.startDate} onChange={set} required />
          <label htmlFor="payDate">Pay Date</label>
          <input id="payDate" type="date" name="payDate" value={form.payDate} min={form.startDate} max={form.endDate} onChange={set} required />
          <label htmlFor="workingDays">Working Days (excludes Friday & Saturday)</label>
          <input id="workingDays" value={workingDays} disabled />
        </fieldset>
        <div className="form-actions"><button type="submit">Open Period</button></div>
      </form>
    </div>
  );
}

export default PayrollPeriodCreate;
