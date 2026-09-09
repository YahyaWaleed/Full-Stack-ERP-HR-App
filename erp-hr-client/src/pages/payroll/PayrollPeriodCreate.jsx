import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';

function PayrollPeriodCreate() {
  const navigate = useNavigate();
  const [error, setError] = useState('');

  const [form, setForm] = useState({
    periodCode: '',
    startDate: '',
    endDate: '',
    payDate: '',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // counts every day in the range that isn't a Friday or Saturday
  const calculateWorkingDays = (startDate, endDate) => {
    if (!startDate || !endDate) return '';

    const start = new Date(startDate);
    const end = new Date(endDate);
    if (start > end) return '';

    let count = 0;
    const current = new Date(start);

    while (current <= end) {
      const day = current.getDay(); // 0 = Sunday, 5 = Friday, 6 = Saturday
      if (day !== 5 && day !== 6) {
        count++;
      }
      current.setDate(current.getDate() + 1);
    }

    return count;
  };

  // auto calcualte working days (without Friday & Saturday) whenever start or end date changes
  const workingDays = calculateWorkingDays(form.startDate, form.endDate);

  // auto getting fiscal year from period code (e.g. 2026-09 -> 2026)
  const fiscalYear = form.periodCode.includes('-')
    ? form.periodCode.split('-')[0]
    : '';

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await apiClient.post('/payroll-periods', { ...form, workingDays, fiscalYear });
      navigate('/dashboard/payroll/periods');
    } catch (err) {
      setError(err.message);
    }

      if (form.payDate < form.startDate || form.payDate > form.endDate) {
        setError('Pay date must be within the payroll period.');
        return;
      }

      try {
        await apiClient.post('/payroll-periods', { ...form, workingDays, fiscalYear });
        navigate('/dashboard/payroll/periods');
      } catch (err) {
        setError(err.message);
      }
    };
  

  return (
    <div>
      <h1>Open New Payroll Period</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <form onSubmit={handleSubmit}>
        <label>Period Code (e.g. 2026-09)</label><br />
        <input name="periodCode" value={form.periodCode} onChange={handleChange} /><br /><br />

        <label>Fiscal Year (calculated automatically)</label><br />
        <input value={fiscalYear} disabled /><br /><br />

        <label>Start Date</label><br />
        <input type="date" name="startDate" value={form.startDate} onChange={handleChange} /><br /><br />

        <label>End Date</label><br />
        <input type="date" name="endDate" value={form.endDate} onChange={handleChange} /><br /><br />

        <label>Pay Date</label><br />
        <input type="date" name="payDate" value={form.payDate} onChange={handleChange} /><br /><br />

        <label>Working Days (calculated automatically, excludes Friday & Saturday)</label><br />
        <input value={workingDays} disabled /><br /><br />

        <button type="submit">Open Period</button>
      </form>
    </div>
  );
}

export default PayrollPeriodCreate;