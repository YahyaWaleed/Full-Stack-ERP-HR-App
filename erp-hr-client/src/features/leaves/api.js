import { api } from '../../shared/api/client';
import { invalidate } from '../../shared/api/useApi';
import { compact } from '../../shared/utils/forms';

// leave decisions change balances (via the database trigger) and the dashboard counts too
const refresh = () => invalidate('/leaves', '/employees', '/dashboard');

export const leavesApi = {
  create: (empId, form) => api.post(`/employees/${empId}/leaves`, compact(form)).then((r) => { refresh(); return r; }),
  approve: (id) => api.post(`/leaves/${id}/approve`).then(refresh),
  reject: (id, rejectReason) => api.post(`/leaves/${id}/reject`, { rejectReason }).then(refresh),
  cancel: (id) => api.post(`/leaves/${id}/cancel`).then(refresh),
};
