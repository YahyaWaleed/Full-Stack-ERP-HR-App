import { api } from '../../shared/api/client';
import { invalidate } from '../../shared/api/useApi';
import { compact } from '../../shared/utils/forms';

// every write refreshes the cached reads it affects
const refresh = (id) => invalidate('/employees', '/dashboard', '/contracts', ...(id ? [`/employees/${id}`] : []));

export const employeesApi = {
  create: (form) => api.post('/employees', compact(form)).then((e) => { refresh(); return e; }),
  update: (id, form) => api.put(`/employees/${id}`, compact(form)).then((e) => { refresh(id); return e; }),
  terminate: (id, body) => api.post(`/employees/${id}/terminate`, compact(body)).then((e) => { refresh(id); return e; }),
  renewContract: (id, form) => api.post(`/employees/${id}/contracts`, compact(form)).then((c) => { refresh(id); return c; }),
  endContract: (contractId, endDate) =>
    api.post(`/contracts/${contractId}/end${endDate ? `?endDate=${endDate}` : ''}`).then(() => refresh()),
  addComponent: (id, form) => api.post(`/employees/${id}/salary-components`, compact(form)).then((c) => { refresh(id); return c; }),
  removeComponent: (id, componentId) => api.delete(`/employees/${id}/salary-components/${componentId}`).then(() => refresh(id)),
};

export const EMPLOYEE_STATUSES = ['ACTIVE', 'PROBATION', 'SUSPENDED', 'RESIGNED', 'TERMINATED'];
export const CONTRACT_TYPES = [
  ['PERMANENT', 'Permanent'], ['FIXED_TERM', 'Fixed Term'], ['PART_TIME', 'Part Time'],
  ['CONSULTANT', 'Consultant'], ['INTERN', 'Intern'],
];
