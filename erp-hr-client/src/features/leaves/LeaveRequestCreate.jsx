import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import EmployeePicker from '../../shared/components/EmployeePicker';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { weekdaysBetween } from '../../shared/utils/forms';
import { leavesApi } from './api';

function LeaveRequestCreate() {
  const navigate = useNavigate();
  const { data: leaveTypes = [] } = useApi('/leave-types', { ttl: 300_000 });
  const [employee, setEmployee] = useState(null);
  const [form, setForm] = useState({ typeId: '', startDate: '', endDate: '', reason: '', attachmentRef: '' });
  const [error, setError] = useState(null);

  const type = leaveTypes.find((t) => String(t.id) === String(form.typeId));
  const estimate = weekdaysBetween(form.startDate, form.endDate);
  const set = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  // only types that apply to the chosen employee's gender
  const types = leaveTypes.filter((t) => !employee || t.genderRestriction === 'ANY'
    || t.genderRestriction === employee.gender || t.genderRestriction === (employee.gender === 'F' ? 'FEMALE' : 'MALE'));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const created = await leavesApi.create(employee.id, form);
      navigate(`/dashboard/leaves/${created.id}`);
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h1>Create Leave Request</h1>
      <ErrorMessage error={error} />
      <form className="form-grid" onSubmit={handleSubmit}>
        <fieldset>
          <legend>Request</legend>
          <label htmlFor="leave-employee">Employee</label>
          <EmployeePicker id="leave-employee" value={employee} onChange={setEmployee} filters={{ status: 'ACTIVE' }} />

          <label htmlFor="typeId">Leave Type</label>
          <select id="typeId" name="typeId" value={form.typeId} onChange={set} required>
            <option value="">Select leave type</option>
            {types.map((t) => <option key={t.id} value={t.id}>{t.nameEn}</option>)}
          </select>
          {type && (
            <p className="muted small">
              {type.maxConsecutive > 0 && <>At most {type.maxConsecutive} working days at a time. </>}
              {type.affectsBalance ? 'Deducted from the annual balance.' : 'Does not use the balance.'}
            </p>
          )}

          <label htmlFor="startDate">Start Date</label>
          <input id="startDate" type="date" name="startDate" value={form.startDate} onChange={set} required />
          <label htmlFor="endDate">End Date</label>
          <input id="endDate" type="date" name="endDate" value={form.endDate} min={form.startDate} onChange={set} required />
          {estimate !== '' && (
            <p className="muted small">About {estimate} working day(s) — weekends and public holidays are not counted.</p>
          )}

          {type?.requiresAttachment && (
            <>
              <label htmlFor="attachmentRef">Supporting document (reference or link) — required</label>
              <input id="attachmentRef" name="attachmentRef" value={form.attachmentRef} onChange={set} required maxLength={255} />
            </>
          )}

          <label htmlFor="reason">Reason</label>
          <input id="reason" name="reason" value={form.reason} onChange={set} maxLength={200} />
        </fieldset>
        <div className="form-actions">
          <button type="submit" disabled={!employee}>Submit Request</button>
        </div>
      </form>
    </div>
  );
}

export default LeaveRequestCreate;
