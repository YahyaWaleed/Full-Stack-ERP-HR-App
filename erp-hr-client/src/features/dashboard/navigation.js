// sidebar sections; adminOnly entries are hidden from HR_USER (the routes are guarded as well)
export const NAV_SECTIONS = [
  {
    title: 'Main Menu',
    items: [{ to: '/dashboard', label: 'Dashboard Overview', end: true }],
  },
  {
    title: 'HR Management',
    items: [
      { to: '/dashboard/employees', label: 'Employees' },
      { to: '/dashboard/attendance', label: 'Attendance' },
      { to: '/dashboard/leaves', label: 'Leaves' },
      { to: '/dashboard/loans', label: 'Loans' },
      { to: '/dashboard/payroll', label: 'Payroll' },
    ],
  },
  {
    title: 'Organization',
    items: [
      { to: '/dashboard/branches', label: 'Branches' },
      { to: '/dashboard/departments', label: 'Departments' },
      { to: '/dashboard/jobs', label: 'Job Titles' },
    ],
  },
  {
    title: 'Reports',
    adminOnly: true, // salaries and bank data
    items: [
      { to: '/dashboard/reports', label: 'Reports' },
      { to: '/dashboard/audit', label: 'Audit Log' },
    ],
  },
];

// readable names for URL segments, used by the breadcrumbs
export const SEGMENT_LABELS = {
  dashboard: 'Dashboard',
  employees: 'Employees',
  list: 'List',
  create: 'New',
  edit: 'Edit',
  contracts: 'Contracts',
  renew: 'Renew',
  attendance: 'Attendance',
  leaves: 'Leaves',
  balances: 'Balances',
  loans: 'Loans',
  payroll: 'Payroll',
  periods: 'Periods',
  payslips: 'Payslips',
  branches: 'Branches',
  departments: 'Departments',
  jobs: 'Job Titles',
  reports: 'Reports',
  audit: 'Audit Log',
};
