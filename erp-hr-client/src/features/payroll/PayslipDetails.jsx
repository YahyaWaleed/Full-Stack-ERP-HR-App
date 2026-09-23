import { useParams } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import PrintButton from '../../shared/components/PrintButton';
import { statusClass } from '../../shared/utils/statusClass';

const LINE_COLUMNS = [
  { key: 'compNameAr', label: 'Component' },
  { key: 'compType', label: 'Type' },
  { key: 'calcNote', label: 'Calculation' },
  { key: 'amount', label: 'Amount', align: 'right' },
];

function PayslipDetails() {
  const { id } = useParams();
  const { data: payslip, error } = useApi(`/payslips/${id}`);
  const { data: lines } = useApi(`/payslips/${id}/lines`);
  const { data: payment } = useApi(payslip?.status === 'PAID' ? `/payslips/${id}/payment` : null);

  if (error) return <ErrorMessage error={error} />;
  if (!payslip) return <p className="muted">Loading…</p>;
  const currency = payslip.currency || 'EGP';

  return (
    <div>
      <h1>Payslip {payslip.payslipNo}</h1>
      <PrintButton />
      <dl className="details">
        <dt>Employee</dt><dd>{payslip.empCode} — {payslip.employeeName}</dd>
        <dt>Period</dt><dd>{payslip.periodCode}</dd>
        <dt>Worked / Absent Days</dt><dd>{payslip.workedDays} / {payslip.absentDays}</dd>
        <dt>Basic Salary</dt><dd>{payslip.basicSalary} {currency}</dd>
        <dt>Total Earnings</dt><dd>{payslip.totalEarnings} {currency}</dd>
        <dt>Total Deductions</dt><dd>{payslip.totalDeductions} {currency}</dd>
        <dt>Net Pay</dt><dd><strong>{payslip.netPay} {currency}</strong></dd>
        <dt>Status</dt><dd><span className={statusClass(payslip.status)}>{payslip.status}</span></dd>
        {payment && <><dt>Paid</dt><dd>{payment.paidOn} by {payment.method} ({payment.reference})</dd></>}
      </dl>

      <h3>Breakdown</h3>
      <DataTable columns={LINE_COLUMNS} rows={lines} />
    </div>
  );
}

export default PayslipDetails;
