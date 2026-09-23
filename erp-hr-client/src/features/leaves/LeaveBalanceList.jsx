import { useState } from 'react';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import EmployeePicker from '../../shared/components/EmployeePicker';
import ErrorMessage from '../../shared/components/ErrorMessage';

const COLUMNS = [
  { key: 'leaveTypeName', label: 'Leave Type' },
  { key: 'fiscalYear', label: 'Fiscal Year' },
  { key: 'entitledDays', label: 'Entitled', align: 'right' },
  { key: 'carriedForward', label: 'Carried Forward', align: 'right' },
  { key: 'usedDays', label: 'Used', align: 'right' },
  { key: 'remainingDays', label: 'Remaining', align: 'right' },
];

function LeaveBalanceList() {
  const [employee, setEmployee] = useState(null);
  const { data, error, loading } = useApi(employee ? `/employees/${employee.id}/leave-balances` : null);

  return (
    <div>
      <h1>Leave Balances</h1>
      <ErrorMessage error={error} />
      <div className="filters">
        <div className="filter-wide">
          <label htmlFor="balance-employee">Employee</label>
          <EmployeePicker id="balance-employee" value={employee} onChange={setEmployee} />
        </div>
      </div>
      {employee && (
        <>
          <h3>{employee.empCode} — {employee.fullNameEn}</h3>
          <DataTable columns={COLUMNS} rows={data} loading={loading} emptyText="No leave balances." />
        </>
      )}
    </div>
  );
}

export default LeaveBalanceList;
