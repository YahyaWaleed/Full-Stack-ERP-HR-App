import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { statusClass } from '../../shared/utils/statusClass';
import { useAuth } from '../auth/useAuth';
import { employeesApi } from './api';

function EmployeeDetails() {
  const { id } = useParams();
  const { isAdmin } = useAuth();
  const { data: employee, error } = useApi(`/employees/${id}`);
  const { data: contracts } = useApi(`/employees/${id}/contracts`);
  const { data: components } = useApi(`/employees/${id}/salary-components`);
  const { data: balances } = useApi(`/employees/${id}/leave-balances`);
  const { data: payslips } = useApi(isAdmin ? `/employees/${id}/payslips` : null); // payslips are admin-only

  if (error) return <ErrorMessage error={error} />;
  if (!employee) return <p className="muted">Loading…</p>;
  const terminated = employee.empStatus === 'TERMINATED';

  return (
    <div>
      <h1>{employee.fullNameEn}</h1>
      <div className="page-actions">
        {isAdmin && !terminated && <Link className="button-link" to={`/dashboard/employees/${id}/edit`}>Edit</Link>}
        {isAdmin && !terminated && <Link className="button-link" to={`/dashboard/employees/${id}/contracts/renew`}>Renew Contract</Link>}
      </div>

      <dl className="details">
        <dt>Code</dt><dd>{employee.empCode}</dd>
        <dt>Arabic Name</dt><dd dir="rtl">{employee.fullNameAr}</dd>
        <dt>Department</dt><dd>{employee.departmentName}</dd>
        <dt>Job Title</dt><dd>{employee.jobTitleName}</dd>
        <dt>Branch</dt><dd>{employee.branchName}</dd>
        <dt>Manager</dt><dd>{employee.managerName || 'None'}</dd>
        <dt>Hire Date</dt><dd>{employee.hireDate}</dd>
        <dt>Status</dt><dd><span className={statusClass(employee.empStatus)}>{employee.empStatus}</span>
          {employee.terminationDate && <> since {employee.terminationDate}</>}</dd>
        <dt>Email</dt><dd>{employee.email || '—'}</dd>
        <dt>Mobile</dt><dd>{employee.mobile || '—'}</dd>
        {isAdmin && <><dt>Bank</dt><dd>{employee.bankName || '—'} {employee.bankAccount}</dd></>}
      </dl>

      <h3>Contracts</h3>
      <DataTable rows={contracts} emptyText="No contracts." columns={[
        { key: 'contractNo', label: 'Contract No' },
        { key: 'contractType', label: 'Type' },
        { key: 'startDate', label: 'Start' },
        { key: 'endDate', label: 'End' },
        { key: 'originalEndDate', label: 'Originally Ending' },
        { key: 'basicSalary', label: 'Basic', align: 'right' },
        { key: 'status', label: 'Status', render: (c) => <span className={statusClass(c.status)}>{c.status}</span> },
      ]} />

      <SalaryComponents empId={id} components={components} canEdit={isAdmin && !terminated} />

      <h3>Leave Balances</h3>
      <DataTable rows={balances} emptyText="No leave balances." columns={[
        { key: 'leaveTypeName', label: 'Leave Type' },
        { key: 'fiscalYear', label: 'Year' },
        { key: 'entitledDays', label: 'Entitled', align: 'right' },
        { key: 'usedDays', label: 'Used', align: 'right' },
        { key: 'remainingDays', label: 'Remaining', align: 'right' },
      ]} />

      {isAdmin && (
        <>
          <h3>Payslips</h3>
          <DataTable rows={payslips} emptyText="No payslips yet." columns={[
            { key: 'periodCode', label: 'Period' },
            { key: 'netPay', label: 'Net Pay', align: 'right' },
            { key: 'status', label: 'Status' },
            { key: 'view', label: '', render: (p) => <Link to={`/dashboard/payroll/payslips/${p.id}`}>View</Link> },
          ]} />
        </>
      )}

      {isAdmin && !terminated && <Termination employee={employee} />}
    </div>
  );
}

function SalaryComponents({ empId, components, canEdit }) {
  const { data: catalogue = [] } = useApi(canEdit ? '/salary-components' : null, { ttl: 300_000 });
  const [form, setForm] = useState({ compId: '', amount: '', percentage: '', effectiveFrom: '' });
  const [error, setError] = useState(null);

  const add = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await employeesApi.addComponent(empId, form);
      setForm({ compId: '', amount: '', percentage: '', effectiveFrom: '' });
    } catch (err) {
      setError(err);
    }
  };

  const remove = async (c) => {
    if (!window.confirm(`Remove ${c.componentName}? If it has already been paid, it is ended yesterday instead of deleted.`)) return;
    try {
      await employeesApi.removeComponent(empId, c.id);
    } catch (err) {
      setError(err);
    }
  };

  const selectable = catalogue.filter((c) => c.calcType !== 'COMPUTED' && c.active !== false);

  return (
    <>
      <h3>Salary Components</h3>
      <ErrorMessage error={error} />
      <DataTable rows={components} emptyText="No extra components." columns={[
        { key: 'componentName', label: 'Component' },
        { key: 'amount', label: 'Amount', align: 'right' },
        { key: 'percentage', label: '%', align: 'right' },
        { key: 'effectiveFrom', label: 'From' },
        { key: 'effectiveTo', label: 'To' },
        ...(canEdit ? [{ key: 'remove', label: '', render: (c) => !c.effectiveTo && (
          <button type="button" className="btn-reject" onClick={() => remove(c)}>Remove</button>) }] : []),
      ]} />
      {canEdit && (
        <form className="inline-form" onSubmit={add}>
          <select aria-label="Component" value={form.compId} required onChange={(e) => setForm({ ...form, compId: e.target.value })}>
            <option value="">Add component…</option>
            {selectable.map((c) => <option key={c.id} value={c.id}>{c.nameEn}</option>)}
          </select>
          <input aria-label="Amount" type="number" min="0" step="0.01" placeholder="Amount" value={form.amount}
                 onChange={(e) => setForm({ ...form, amount: e.target.value })} />
          <input aria-label="Percentage" type="number" min="0" max="100" step="0.01" placeholder="% of basic" value={form.percentage}
                 onChange={(e) => setForm({ ...form, percentage: e.target.value })} />
          <input aria-label="Effective from" type="date" required value={form.effectiveFrom}
                 onChange={(e) => setForm({ ...form, effectiveFrom: e.target.value })} />
          <button type="submit">Add</button>
        </form>
      )}
    </>
  );
}

// one step for the whole exit: date, contract, pending leave; refused while loans are outstanding (review 10.2)
function Termination({ employee }) {
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [reason, setReason] = useState('');
  const [error, setError] = useState(null);

  const terminate = async (e) => {
    e.preventDefault();
    if (!window.confirm(`Terminate ${employee.fullNameEn} as of ${date}? Their contract ends and pending leave is cancelled.`)) return;
    setError(null);
    try {
      await employeesApi.terminate(employee.id, { terminationDate: date, reason });
    } catch (err) {
      setError(err);
    }
  };

  return (
    <section className="danger-zone">
      <h3>Terminate Employee</h3>
      <ErrorMessage error={error} />
      <form className="inline-form" onSubmit={terminate}>
        <label htmlFor="terminationDate">Last working day</label>
        <input id="terminationDate" type="date" value={date} min={employee.hireDate} required onChange={(e) => setDate(e.target.value)} />
        <input aria-label="Reason" placeholder="Reason (optional)" maxLength={200} value={reason} onChange={(e) => setReason(e.target.value)} />
        <button type="submit" className="btn-reject">Terminate</button>
      </form>
    </section>
  );
}

export default EmployeeDetails;
