import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { apiClient } from '../api/apiClient';
import EmployeeList from './employees/EmployeeList';
import LeaveRequestsList from './leaves/LeaveRequestsList';
import LoanList from './loans/LoanList';
import ContractsExpiringReport from './reports/ContractsExpiringReport';
import './HomeOverview.css';

function HomeOverview() {
  const context = useOutletContext();
  const username = context?.username || 'HR Manager';
  const [activeQuickView, setActiveQuickView] = useState(null);

  const [stats, setStats] = useState({
    employeesCount: 0,
    pendingLeaves: 0,
    activeLoans: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadOverviewData() {
      try {
        setLoading(true);
        // Safely fetch counts using existing apiClient
        const [empRes, leaveRes, loanRes] = await Promise.allSettled([
          apiClient.get('/employees'),
          apiClient.get('/leaves'),
          apiClient.get('/loans')
        ]);

       setStats({
        employeesCount: empRes.status === 'fulfilled' && Array.isArray(empRes.value)
          ? empRes.value.filter((e) => e.empStatus === 'ACTIVE').length
          : 0,
        pendingLeaves: leaveRes.status === 'fulfilled' && Array.isArray(leaveRes.value)
          ? leaveRes.value.filter((l) => l.status === 'PENDING').length
          : 0,
        activeLoans: loanRes.status === 'fulfilled' && Array.isArray(loanRes.value)
          ? loanRes.value.filter((l) => l.status === 'ACTIVE').length
          : 0,
        });
      } catch (err) {
        console.error("Error loading overview metrics:", err);
      } finally {
        setLoading(false);
      }
    }

    loadOverviewData();
  }, []);

  const role = localStorage.getItem('role') || 'User';
  
  return (
    <div>
      <div>
        <h2>Welcome, {username}</h2>
        <p>{role}</p>
        <p>Activity Overview</p>
      </div>

      <hr />

      {/* Metrics Row */}
      <div style={{ display: 'flex', gap: '20px', marginBottom: '20px' }}>

        <div
          style={{
            border: '1px solid #ccc',
            padding: '10px',
            width: '150px',
            cursor: 'pointer',
          }}
          onClick={() => setActiveQuickView('employees')}
        >
          <div className="kpi-icon">👥</div>
          <p>Total Employees</p>
          <h3>{loading ? '...' : stats.employeesCount}</h3>
        </div>

        <div
          style={{
            border: '1px solid #ccc',
            padding: '10px',
            width: '150px',
            cursor: 'pointer',
          }}
          onClick={() => setActiveQuickView('leaves')}
        >
          <div className="kpi-icon">📝</div>
          <p>Pending Leaves</p>
          <h3>{loading ? '...' : stats.pendingLeaves}</h3>
        </div>

        <div
          style={{
            border: '1px solid #ccc',
            padding: '10px',
            width: '150px',
            cursor: 'pointer',
          }}
          onClick={() => setActiveQuickView('loans')}
        >
          <div className="kpi-icon">💰</div>
          <p>Active Loans</p>
          <h3>{loading ? '...' : stats.activeLoans}</h3>
        </div>

      </div>

      {/* Shortcuts & Quick Actions */}
      <div className="quick-navigation">
        <h3>Quick Navigation</h3>
        <ul className="quick-navigation-list">
          <li className="quick-navigation-item"><a className="quick-navigation-link" href="#employees" onClick={(event) => { event.preventDefault(); setActiveQuickView('employees'); }}>View Employees</a></li>
          <li className="quick-navigation-item"><a className="quick-navigation-link" href="#leaves" onClick={(event) => { event.preventDefault(); setActiveQuickView('leaves'); }}>Review Leave Requests</a></li>
          <li className="quick-navigation-item"><a className="quick-navigation-link" href="#loans" onClick={(event) => { event.preventDefault(); setActiveQuickView('loans'); }}>View Loan Requests</a></li>
          <li className="quick-navigation-item">
            <a
              className="quick-navigation-link"
              href="#expiring-contracts"
              onClick={(event) => {
                event.preventDefault();
                setActiveQuickView('expiring-contracts');
              }}
            >
              View Expiring Contracts
            </a>
          </li>
        </ul>
      </div>

      {activeQuickView === 'employees' && <EmployeeList />}
      {activeQuickView === 'leaves' && <LeaveRequestsList />}
      {activeQuickView === 'loans' && <LoanList />}
      {activeQuickView === 'expiring-contracts' && <ContractsExpiringReport />}
    </div>
  );
}

export default HomeOverview;