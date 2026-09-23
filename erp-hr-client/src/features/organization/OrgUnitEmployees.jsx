import { useMemo } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import EmployeeList from '../employees/EmployeeList';

// one page for "employees of a branch / department / job title" -- previously three copies of the same file,
// now the filtered employee list (GET /employees?branchId= | deptId= | jobId=)
const UNITS = {
  branchId: { list: '/branches', back: '/dashboard/branches', filter: 'branchId', name: (u) => u.nameEn, noun: 'Branch', plural: 'Branches' },
  deptId: { list: '/departments', back: '/dashboard/departments', filter: 'deptId', name: (u) => u.nameEn, noun: 'Department', plural: 'Departments' },
  jobTitleId: { list: '/jobs', back: '/dashboard/jobs', filter: 'jobId', name: (u) => u.titleEn, noun: 'Job Title', plural: 'Job Titles' },
};

function OrgUnitEmployees() {
  const params = useParams();
  const [param, unit] = Object.entries(UNITS).find(([key]) => params[key]);
  const id = params[param];
  const { data: units = [] } = useApi(unit.list, { ttl: 300_000 });
  const current = units.find((u) => String(u.id) === id);
  const fixedFilter = useMemo(() => ({ [unit.filter]: id }), [unit.filter, id]);

  return (
    <div>
      <p><Link to={unit.back}>← Back to {unit.plural}</Link></p>
      <h2>{unit.noun}: {current ? unit.name(current) : '…'}</h2>
      <EmployeeList key={id} fixedFilter={fixedFilter} />
    </div>
  );
}

export default OrgUnitEmployees;
