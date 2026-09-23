import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import Pagination from '../../shared/components/Pagination';
import { statusClass } from '../../shared/utils/statusClass';
import { useAuth } from '../auth/useAuth';
import { payrollApi } from './api';

const PAYSLIP_COLUMNS = [
  { key: 'empCode', label: 'Employee' },
  { key: 'employeeName', label: 'Name' },
  { key: 'basicSalary', label: 'Basic', align: 'right' },
  { key: 'totalEarnings', label: 'Earnings', align: 'right' },
  { key: 'totalDeductions', label: 'Deductions', align: 'right' },
  { key: 'netPay', label: 'Net Pay', align: 'right' },
  { key: 'status', label: 'Status' },
  { key: 'view', label: '', render: (p) => <Link to={`/dashboard/payroll/payslips/${p.id}`}>View</Link> },
];

function PayrollPeriodDetails() {
  const { periodCode } = useParams();
  const { isAdmin } = useAuth();
  const { data: period, error: loadError } = useApi(`/payroll-periods/${periodCode}`);
  const [page, setPage] = useState(0);
  // payslips are HR_ADMIN only on the backend
  const { data: payslips, loading } = useApi(isAdmin ? `/payroll-periods/${periodCode}/payslips?page=${page}&size=20` : null);
  const [payMethod, setPayMethod] = useState('BANK');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(null);

  const act = (question, action) => async () => {
    if (!window.confirm(question)) return;
    setError(null);
    setBusy(true);
    try {
      await action();
    } catch (err) {
      setError(err);
    } finally {
      setBusy(false);
    }
  };

  if (loadError) return <ErrorMessage error={loadError} />;
  if (!period) return <p className="muted">Loading…</p>;

  return (
    <div>
      <h1>Period: {period.periodCode}</h1>
      <ErrorMessage error={error} />
      <dl className="details">
        <dt>Status</dt><dd><span className={statusClass(period.status)}>{period.status}</span></dd>
        <dt>Fiscal Year</dt><dd>{period.fiscalYear}</dd>
        <dt>Dates</dt><dd>{period.startDate} to {period.endDate} ({period.workingDays} working days)</dd>
        <dt>Pay Date</dt><dd>{period.payDate}</dd>
        {period.processedAt && <><dt>Processed</dt><dd>{period.processedAt.replace('T', ' ').slice(0, 16)}</dd></>}
      </dl>

      {isAdmin && period.status === 'OPEN' && (
        <div className="page-actions">
          <button type="button" disabled={busy}
                  onClick={act(`Run payroll for ${periodCode}? This generates a payslip for every employee.`, () => payrollApi.run(periodCode))}>
            {busy ? 'Running…' : 'Run Payroll'}
          </button>
        </div>
      )}

      {isAdmin && period.status === 'PROCESSED' && (
        <div className="inline-form">
          <select aria-label="Payment method" value={payMethod} onChange={(e) => setPayMethod(e.target.value)}>
            <option value="BANK">Bank</option>
            <option value="CASH">Cash</option>
            <option value="CHEQUE">Cheque</option>
          </select>
          <button type="button" disabled={busy}
                  onClick={act(`Mark ${periodCode} as paid via ${payMethod}?`, () => payrollApi.pay(periodCode, payMethod))}>
            Pay Period
          </button>
        </div>
      )}

      {isAdmin && (
        <>
          <h3>Payslips</h3>
          <DataTable columns={PAYSLIP_COLUMNS} rows={payslips?.content} loading={loading}
                     emptyText="No payslips generated yet — run payroll first." />
          <Pagination page={page} totalPages={payslips?.totalPages ?? 0} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}

export default PayrollPeriodDetails;
