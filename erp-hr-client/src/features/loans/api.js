import { api } from '../../shared/api/client';
import { invalidate } from '../../shared/api/useApi';
import { compact } from '../../shared/utils/forms';

const refresh = () => invalidate('/loans', '/employees', '/dashboard');

export const loansApi = {
  create: (form) => api.post('/loans', compact(form)).then((l) => { refresh(); return l; }),
  close: (id) => api.post(`/loans/${id}/close`).then(refresh),
  cancel: (id) => api.post(`/loans/${id}/cancel`).then(refresh),
};

export const LOAN_TYPES = [['PERSONAL', 'Personal'], ['ADVANCE', 'Salary Advance'], ['EMERGENCY', 'Emergency'], ['HOUSING', 'Housing']];
