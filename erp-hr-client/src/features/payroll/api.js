import { api } from '../../shared/api/client';
import { invalidate } from '../../shared/api/useApi';

// a payroll run touches payslips, loans (installments) and every report
const refresh = () => invalidate('/payroll-periods', '/payslips', '/loans', '/employees', '/reports', '/dashboard');

export const payrollApi = {
  createPeriod: (form) => api.post('/payroll-periods', form).then((p) => { refresh(); return p; }),
  run: (code) => api.post(`/payroll-periods/${code}/run`).then(refresh),
  pay: (code, method) => api.post(`/payroll-periods/${code}/pay?method=${method}`).then(refresh),
};
