import { useState } from 'react';
import Select from 'react-select';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function LeaveBalancesReport() {
  const [fiscalYear, setFiscalYear] = useState('');
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  const currentYear = new Date().getFullYear();

  const fiscalYearOptions = Array.from(
    { length: 5 },
    (_, i) => ({
      value: String(currentYear - i),
      label: String(currentYear - i),
    })
  );

  const handleRun = async () => {
    if (!fiscalYear) {
      setError('Please select a fiscal year.');
      return;
    }

    try {
      const data = await apiClient.get(
        `/reports/leave-balances?fiscalYear=${fiscalYear}`
      );

      setRows(data);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h1>Leave Balances</h1>

      <PrintButton />

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <label>Fiscal Year</label>
      <br />

      <Select
        options={fiscalYearOptions}
        value={
          fiscalYearOptions.find(
            (option) => option.value === fiscalYear
          ) || null
        }
        onChange={(selectedOption) =>
          setFiscalYear(selectedOption ? selectedOption.value : '')
        }
        placeholder="Search Fiscal Year"
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
            <th>Employee</th>
            <th>Leave Type</th>
            <th>Entitled</th>
            <th>Used</th>
            <th>Remaining</th>
          </tr>
        </thead>

        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{r.empCode} - {r.fullNameAr}</td>
              <td>{r.leaveType}</td>
              <td>{r.entitledDays}</td>
              <td>{r.usedDays}</td>
              <td>{r.remainingDays}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LeaveBalancesReport;