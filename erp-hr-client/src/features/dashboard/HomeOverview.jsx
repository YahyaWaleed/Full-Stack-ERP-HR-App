import { lazy, Suspense, useState } from 'react';
import { useApi } from '../../shared/api/useApi';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { useAuth } from '../auth/useAuth';
import './HomeOverview.css';

// the quick views are whole pages -- only downloaded when someone opens one
const EmployeeList = lazy(() => import('../employees/EmployeeList'));
const LeaveRequestsList = lazy(() => import('../leaves/LeaveRequestsList'));
const LoanList = lazy(() => import('../loans/LoanList'));
const ContractsExpiring = lazy(() => import('../reports/ContractsExpiring'));

// three counts from one small endpoint -- not three full tables counted in the browser (review 7.4 / 6.5)
const KPIS = [
  { view: 'employees', icon: '👥', label: 'Total Employees', field: 'employeesCount' },
  { view: 'leaves', icon: '📝', label: 'Pending Leaves', field: 'pendingLeaves' },
  { view: 'loans', icon: '💰', label: 'Active Loans', field: 'activeLoans' },
];

function HomeOverview() {
  const { username, role, isAdmin } = useAuth();
  const [activeQuickView, setActiveQuickView] = useState(null);
  const { data: summary, error, loading } = useApi('/dashboard/summary');

  const links = [
    { view: 'employees', label: 'View Employees' },
    { view: 'leaves', label: 'Review Leave Requests' },
    { view: 'loans', label: 'View Loan Requests' },
    ...(isAdmin ? [{ view: 'expiring-contracts', label: 'View Expiring Contracts' }] : []), // built on an admin-only report
  ];

  return (
    <div>
      <div>
        <h2>Welcome, {username}</h2>
        <p>{role}</p>
        <p>Activity Overview</p>
      </div>

      <ErrorMessage error={error} />
      <hr />

      <div className="kpi-row">
        {KPIS.map((kpi) => (
          <button key={kpi.view} type="button" className={`kpi-card${activeQuickView === kpi.view ? ' selected' : ''}`}
                  onClick={() => setActiveQuickView(kpi.view)}>
            <span className="kpi-icon">{kpi.icon}</span>
            <span className="kpi-label">{kpi.label}</span>
            <strong className="kpi-value">{loading || !summary ? '…' : summary[kpi.field]}</strong>
          </button>
        ))}
      </div>

      <div className="quick-navigation">
        <h3>Quick Navigation</h3>
        <ul className="quick-navigation-list">
          {links.map((l) => (
            <li key={l.view} className="quick-navigation-item">
              <button type="button" className="quick-navigation-link" onClick={() => setActiveQuickView(l.view)}>
                {l.label}
              </button>
            </li>
          ))}
        </ul>
      </div>

      <Suspense fallback={<p className="muted">Loading…</p>}>
        {activeQuickView === 'employees' && <EmployeeList />}
        {activeQuickView === 'leaves' && <LeaveRequestsList />}
        {activeQuickView === 'loans' && <LoanList />}
        {isAdmin && activeQuickView === 'expiring-contracts' && <ContractsExpiring />}
      </Suspense>
    </div>
  );
}

export default HomeOverview;
