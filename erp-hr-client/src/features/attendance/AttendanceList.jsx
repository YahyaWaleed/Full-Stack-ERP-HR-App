import { useState } from 'react';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import EmployeePicker from '../../shared/components/EmployeePicker';
import ErrorMessage from '../../shared/components/ErrorMessage';
import Pagination from '../../shared/components/Pagination';
import PeriodSelect from '../../shared/components/PeriodSelect';

const COLUMNS = [
  { key: 'periodCode', label: 'Period' },
  { key: 'workingDays', label: 'Working Days', align: 'right' },
  { key: 'presentDays', label: 'Present', align: 'right' },
  { key: 'paidLeaveDays', label: 'Paid Leave', align: 'right' },
  { key: 'unpaidAbsentDays', label: 'Unpaid Absent', align: 'right' },
  { key: 'overtimeHours', label: 'Overtime Hours', align: 'right' },
  { key: 'lateMinutes', label: 'Late Minutes', align: 'right' },
];

// one employee's history (searched on the server) or one month for everybody (paginated)
function AttendanceList() {
  const [employee, setEmployee] = useState(null);
  const [periodCode, setPeriodCode] = useState('');
  const [page, setPage] = useState(0);

  const path = employee
    ? `/employees/${employee.id}/attendance`
    : `/attendance?page=${page}&size=25${periodCode ? `&periodCode=${periodCode}` : ''}`;
  const { data, error, loading } = useApi(path);
  const rows = employee ? data : data?.content;

  return (
    <div>
      <h1>Attendance Sheet</h1>
      <ErrorMessage error={error} />

      <div className="filters">
        <div className="filter-wide">
          <label htmlFor="attendance-employee">Employee</label>
          <EmployeePicker id="attendance-employee" value={employee} onChange={(e) => { setEmployee(e); setPage(0); }} />
        </div>
        {!employee && (
          <div className="filter-wide">
            <label htmlFor="attendance-period">Period</label>
            <PeriodSelect id="attendance-period" value={periodCode} onChange={(p) => { setPeriodCode(p); setPage(0); }} />
          </div>
        )}
      </div>

      {employee && <h3>{employee.empCode} — {employee.fullNameEn}</h3>}
      <DataTable
        columns={employee ? COLUMNS : [{ key: 'empId', label: 'Employee #' }, ...COLUMNS]}
        rows={rows} loading={loading} emptyText="No attendance records." />
      {!employee && <Pagination page={page} totalPages={data?.totalPages ?? 0} onPageChange={setPage} />}
    </div>
  );
}

export default AttendanceList;
