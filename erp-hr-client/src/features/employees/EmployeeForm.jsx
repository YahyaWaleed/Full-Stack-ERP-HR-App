import { useState } from 'react';
import { useApi } from '../../shared/api/useApi';
import EmployeePicker from '../../shared/components/EmployeePicker';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { CONTRACT_TYPES } from './api';

const EMPTY_CONTRACT = {
  contractType: 'PERMANENT', startDate: '', endDate: '', basicSalary: '', currency: 'EGP',
  weeklyHours: 40, annualLeaveDays: 21, probationMonths: 3, notes: '',
};

const FIELDS_FROM = (e) => ({
  fullNameAr: e?.fullNameAr ?? '', fullNameEn: e?.fullNameEn ?? '', gender: e?.gender ?? 'M',
  birthDate: e?.birthDate ?? '', nationalId: e?.nationalId ?? '', maritalStatus: e?.maritalStatus ?? 'SINGLE',
  dependents: e?.dependents ?? 0, email: e?.email ?? '', mobile: e?.mobile ?? '', address: e?.address ?? '',
  hireDate: e?.hireDate ?? '', deptId: e?.deptId ?? '', jobId: e?.jobId ?? '', branchId: e?.branchId ?? '', insuranceNo: e?.insuranceNo ?? '',
  bankName: e?.bankName ?? '', bankAccount: e?.bankAccount ?? '', paymentMethod: e?.paymentMethod ?? 'BANK',
});

// used by "Create employee" (with the first contract) and "Edit employee" (with the version it loaded)
function EmployeeForm({ employee, onSubmit, submitLabel }) {
  const isEdit = Boolean(employee);
  const { data: departments = [] } = useApi('/departments', { ttl: 300_000 });
  const { data: jobTitles = [] } = useApi('/jobs', { ttl: 300_000 });
  const { data: branches = [] } = useApi('/branches', { ttl: 300_000 });

  const [form, setForm] = useState(() => FIELDS_FROM(employee));
  const [contract, setContract] = useState(EMPTY_CONTRACT);
  const [manager, setManager] = useState(employee?.managerName ? { id: employee.managerId, empCode: '', fullNameEn: employee.managerName } : null);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const set = (e) => setForm({ ...form, [e.target.name]: e.target.value });
  const setC = (e) => setContract({ ...contract, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSaving(true);
    try {
      await onSubmit({
        ...form,
        managerId: manager?.id ?? '',
        ...(isEdit ? { version: employee.version } : { contract }),
      });
    } catch (err) {
      setError(err);
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form-grid">
      <ErrorMessage error={error} />

      <fieldset>
        <legend>Personal Details</legend>
        <Field label="Full Name (English)" name="fullNameEn" value={form.fullNameEn} onChange={set} required maxLength={120} />
        <Field label="Full Name (Arabic)" name="fullNameAr" value={form.fullNameAr} onChange={set} required maxLength={120} dir="rtl" />
        <Select label="Gender" name="gender" value={form.gender} onChange={set} options={[['M', 'Male'], ['F', 'Female']]} />
        <Field label="Birth Date" name="birthDate" type="date" value={form.birthDate} onChange={set} required />
        <Field label="National ID" name="nationalId" value={form.nationalId} onChange={set} required
               pattern="\d{14}" title="14 digits" inputMode="numeric" />
        <Select label="Marital Status" name="maritalStatus" value={form.maritalStatus} onChange={set}
                options={[['SINGLE', 'Single'], ['MARRIED', 'Married'], ['DIVORCED', 'Divorced'], ['WIDOWED', 'Widowed']]} />
        <Field label="Number of Dependents" name="dependents" type="number" min="0" max="20" value={form.dependents} onChange={set} />
        <Field label="Email" name="email" type="email" value={form.email} onChange={set} maxLength={100} />
        <Field label="Mobile" name="mobile" value={form.mobile} onChange={set} maxLength={20} />
        <Field label="Address" name="address" value={form.address} onChange={set} maxLength={200} />
      </fieldset>

      <fieldset>
        <legend>Job Details</legend>
        <Field label="Hire Date" name="hireDate" type="date" value={form.hireDate} onChange={set} required />
        <Select label="Department" name="deptId" value={form.deptId} onChange={set} required placeholder="Select department"
                options={departments.map((d) => [d.id, d.nameEn])} />
        <Select label="Job Title" name="jobId" value={form.jobId} onChange={set} required placeholder="Select job title"
                options={jobTitles.map((j) => [j.id, j.titleEn])} />
        <Select label="Branch" name="branchId" value={form.branchId} onChange={set} required placeholder="Select branch"
                options={branches.map((b) => [b.id, b.nameEn])} />
        <label htmlFor="manager">Manager (employees in managerial job titles)</label>
        {/* server-side: GET /employees?managerial=true joins on job_titles.is_managerial (review 7.5) */}
        <EmployeePicker id="manager" value={manager} onChange={setManager} filters={{ managerial: 'true' }}
                        placeholder="No manager — type to search managers" />
      </fieldset>

      <fieldset>
        <legend>Bank & Insurance</legend>
        <Field label="Insurance No." name="insuranceNo" value={form.insuranceNo} onChange={set} maxLength={20} />
        <Field label="Bank Name" name="bankName" value={form.bankName} onChange={set} maxLength={60} />
        <Field label="Bank Account" name="bankAccount" value={form.bankAccount} onChange={set} maxLength={34} />
        <Select label="Payment Method" name="paymentMethod" value={form.paymentMethod} onChange={set}
                options={[['BANK', 'Bank Transfer'], ['CASH', 'Cash'], ['CHEQUE', 'Cheque']]} />
      </fieldset>

      {!isEdit && (
        <fieldset>
          <legend>Initial Contract</legend>
          <ContractFields contract={contract} onChange={setC} />
        </fieldset>
      )}

      <div className="form-actions">
        <button type="submit" disabled={saving}>{saving ? 'Saving…' : submitLabel}</button>
      </div>
    </form>
  );
}

export function ContractFields({ contract, onChange }) {
  return (
    <>
      <Select label="Contract Type" name="contractType" value={contract.contractType} onChange={onChange} options={CONTRACT_TYPES} />
      <Field label="Start Date" name="startDate" type="date" value={contract.startDate} onChange={onChange} required />
      <Field label={contract.contractType === 'FIXED_TERM' ? 'End Date' : 'End Date (optional)'} name="endDate" type="date"
             value={contract.endDate} onChange={onChange} required={contract.contractType === 'FIXED_TERM'} />
      <Field label="Basic Salary" name="basicSalary" type="number" min="1" step="0.01" value={contract.basicSalary} onChange={onChange} required />
      <Select label="Currency" name="currency" value={contract.currency} onChange={onChange}
              options={[['EGP', 'EGP'], ['USD', 'USD'], ['KWD', 'KWD'], ['MYR', 'MYR']]} />
      <Field label="Weekly Hours" name="weeklyHours" type="number" min="1" max="80" value={contract.weeklyHours} onChange={onChange} />
      <Field label="Annual Leave Days" name="annualLeaveDays" type="number" min="0" max="60" value={contract.annualLeaveDays} onChange={onChange} />
      <Field label="Probation Period (Months)" name="probationMonths" type="number" min="0" max="12" value={contract.probationMonths} onChange={onChange} />
      <label htmlFor="notes">Contract Notes</label>
      <textarea id="notes" name="notes" value={contract.notes} onChange={onChange} rows="4" maxLength={255} />
    </>
  );
}

function Field({ label, name, ...props }) {
  return (
    <>
      <label htmlFor={name}>{label}</label>
      <input id={name} name={name} {...props} />
    </>
  );
}

function Select({ label, name, options, placeholder, ...props }) {
  return (
    <>
      <label htmlFor={name}>{label}</label>
      <select id={name} name={name} {...props}>
        {placeholder && <option value="">{placeholder}</option>}
        {options.map(([value, text]) => <option key={value} value={value}>{text}</option>)}
      </select>
    </>
  );
}

export default EmployeeForm;
