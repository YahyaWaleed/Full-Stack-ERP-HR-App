import { useState, useEffect } from 'react';
import Select from 'react-select';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function TopNetSalaryReport() {
  const [periodCode, setPeriodCode] = useState('');
  const [periods, setPeriods] = useState([]);
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  // Get payroll periods
  useEffect(() => {
    apiClient
      .get('/payroll-periods')
      .then(setPeriods)
      .catch((err) => setError(err.message));
  }, []);

  // Convert payroll periods into react-select options
  const periodOptions = periods.map((period) => ({
    value: period.periodCode,
    label: period.periodCode,
  }));

  const handleRun = async () => {
    if (!periodCode) {
      setError('Please select a payroll period.');
      return;
    }

    try {
      const data = await apiClient.get(
        `/reports/top-net-salary?periodCode=${periodCode}`
      );

      setRows(data);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h1>Top 10 by Net Salary</h1>

      <PrintButton />

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <label>Period Code</label>
      <br />

      <Select
        options={periodOptions}
        value={
          periodOptions.find(
            (option) => option.value === periodCode
          ) || null
        }
        onChange={(selectedOption) =>
          setPeriodCode(selectedOption ? selectedOption.value : '')
        }
        placeholder="Search Period Code"
        isSearchable
        isClearable
        styles={{
          control: (base, state) => ({
            ...base,
            backgroundColor: 'var(--color-surface)',
            borderColor: state.isFocused
              ? 'var(--color-primary)'
              : 'var(--color-border)',
            color: 'var(--color-text)',
            minHeight: '42px',
            borderRadius: '8px',
            boxShadow: state.isFocused
              ? '0 0 0 2px rgba(96, 165, 250, 0.15)'
              : 'none',
            '&:hover': {
              borderColor: 'var(--color-primary)',
            },
          }),

          menu: (base) => ({
            ...base,
            backgroundColor: 'var(--color-surface)',
            border: '1px solid var(--color-border)',
            borderRadius: '8px',
            overflow: 'hidden',
            zIndex: 9999,
          }),

          menuList: (base) => ({
            ...base,
            padding: '4px',
            backgroundColor: 'var(--color-surface)',
          }),

          option: (base, state) => ({
            ...base,
            backgroundColor: state.isSelected
              ? 'var(--color-primary)'
              : state.isFocused
                ? 'var(--color-bg)'
                : 'var(--color-surface)',
            color: state.isSelected
              ? '#ffffff'
              : 'var(--color-text)',
            padding: '10px 12px',
            borderRadius: '6px',
            cursor: 'pointer',
            '&:active': {
              backgroundColor: 'var(--color-primary)',
            },
          }),

          singleValue: (base) => ({
            ...base,
            color: 'var(--color-text)',
          }),

          placeholder: (base) => ({
            ...base,
            color: 'var(--color-text-muted)',
          }),

          input: (base) => ({
            ...base,
            color: 'var(--color-text)',
          }),

          dropdownIndicator: (base) => ({
            ...base,
            color: 'var(--color-text-muted)',
            '&:hover': {
              color: 'var(--color-text)',
            },
          }),

          clearIndicator: (base) => ({
            ...base,
            color: 'var(--color-text-muted)',
            '&:hover': {
              color: 'var(--color-text)',
            },
          }),

          indicatorSeparator: (base) => ({
            ...base,
            backgroundColor: 'var(--color-border)',
          }),
        }}
      />

      <br />

      <button onClick={handleRun}>Run Report</button>

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Employee</th>
            <th>Net Pay</th>
          </tr>
        </thead>

        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{i + 1}</td>
              <td>{r.fullNameAr}</td>
              <td>{r.netPay}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default TopNetSalaryReport;