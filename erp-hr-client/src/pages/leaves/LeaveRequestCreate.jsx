
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Select from 'react-select';
import { apiClient } from '../../api/apiClient';

function LeaveRequestCreate() {
  const navigate = useNavigate();
  const [error, setError] = useState('');

  const [employees, setEmployees] = useState([]);
  const [leaveTypes, setLeaveTypes] = useState([]);

  const [form, setForm] = useState({
    empId: '',
    typeId: '',
    startDate: '',
    endDate: '',
    reason: '',
  });

  // get employees and leave types when the page loads
useEffect(() => {
  Promise.all([
    apiClient.get('/employees?size=1000'),
    apiClient.get('/leave-types')
  ])
    .then(([employeeData, leaveTypeData]) => {
      setEmployees(employeeData.content);
      setLeaveTypes(leaveTypeData);
    })
    .catch((err) => setError(err.message));
}, []);

const handleChange = (e) => {
  setForm({ ...form, [e.target.name]: e.target.value });
};

  // Active employees only
  const employeeOptions = employees
    .filter((employee) => employee.empStatus === 'ACTIVE')
    .map((employee) => ({
      value: employee.id,
      label: `${employee.empCode} - ${employee.fullNameEn}`,
    }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      // empId is part of the URL path and is also included in the request body
      await apiClient.post(`/employees/${form.empId}/leaves`, form);

      navigate('/dashboard/leaves/list');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h1>Create Leave Request</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <form onSubmit={handleSubmit}>

        <label>Employee:</label>
        <br />

        <Select
          options={employeeOptions}
          value={
            employeeOptions.find(
              (option) => option.value === Number(form.empId)
            ) || null
          }
          onChange={(selectedOption) =>
            setForm({
              ...form,
              empId: selectedOption ? selectedOption.value : '',
            })
          }
          placeholder="Select Employee"
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
        <br />

        <label>Leave Type:</label>
        <br />

        <select
          name="typeId"
          value={form.typeId}
          onChange={handleChange}
          required
        >
          <option value="">Select Leave Type</option>

          {leaveTypes.map((leaveType) => (
            <option key={leaveType.id} value={leaveType.id}>
              {leaveType.code} - {leaveType.nameEn}
            </option>
          ))}
        </select>

        <br />
        <br />

        <label>Start Date:</label>
        <br />

        <input
          type="date"
          name="startDate"
          value={form.startDate}
          onChange={handleChange}
          required
        />

        <br />
        <br />

        <label>End Date:</label>
        <br />

        <input
          type="date"
          name="endDate"
          value={form.endDate}
          onChange={handleChange}
          required
        />

        <br />
        <br />

        <label>Reason:</label>
        <br />

        <input
          name="reason"
          placeholder="Reason"
          value={form.reason}
          onChange={handleChange}
        />

        <br />
        <br />

        <button type="submit">Submit Request</button>

      </form>
    </div>
  );
}

export default LeaveRequestCreate;

