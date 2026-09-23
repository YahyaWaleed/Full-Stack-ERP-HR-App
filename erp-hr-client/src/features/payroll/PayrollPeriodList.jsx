import { Link } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { statusClass } from '../../shared/utils/statusClass';

const COLUMNS = [
  { key: 'periodCode', label: 'Period Code' },
  { key: 'fiscalYear', label: 'Fiscal Year' },
  { key: 'workingDays', label: 'Working Days', align: 'right' },
  { key: 'payDate', label: 'Pay Date' },
  { key: 'status', label: 'Status', render: (p) => <span className={statusClass(p.status)}>{p.status}</span> },
  { key: 'open', label: '', render: (p) => <Link to={`/dashboard/payroll/periods/${p.periodCode}`}>Open</Link> },
];

function PayrollPeriodList() {
  const { data: periods, error, loading } = useApi('/payroll-periods');
  return (
    <div>
      <h1>Payroll Periods</h1>
      <ErrorMessage error={error} />
      <DataTable columns={COLUMNS} rows={periods} loading={loading} emptyText="No payroll periods yet." />
    </div>
  );
}

export default PayrollPeriodList;
