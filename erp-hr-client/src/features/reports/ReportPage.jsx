import { useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import Pagination from '../../shared/components/Pagination';
import PeriodSelect from '../../shared/components/PeriodSelect';
import PrintButton from '../../shared/components/PrintButton';
import { REPORT_COLUMNS } from './reportColumns';

const PAGE_SIZE = 50;

// one component for every report (review 4.1): parameters come from the backend catalogue,
// columns from reportColumns.jsx; pageable reports are fetched a page at a time
// keyed by slug: moving from one report to another starts with a clean form
function ReportPage({ slug: slugProp }) {
  const params = useParams();
  const slug = slugProp ?? params.slug;
  return <Report key={slug} slug={slug} />;
}

function Report({ slug }) {
  const { data: catalogue, error: catalogueError } = useApi('/reports', { ttl: 300_000 });
  const report = catalogue?.find((r) => r.slug === slug);

  const [values, setValues] = useState({});
  const [submitted, setSubmitted] = useState(null); // the parameters of the last "Run"
  const [page, setPage] = useState(0);

  const needsInput = report ? report.params.some((p) => p.required) : true;
  // reports without required inputs run straight away
  const effective = useMemo(() => submitted ?? (report && !needsInput ? {} : null), [submitted, report, needsInput]);

  const path = useMemo(() => {
    if (!report || !effective) return null;
    const query = new URLSearchParams(Object.entries(effective).filter(([, v]) => v !== '' && v != null));
    if (report.pageable) {
      query.set('page', String(page));
      query.set('size', String(PAGE_SIZE));
    }
    const qs = query.toString();
    return `/reports/${slug}${qs ? `?${qs}` : ''}`;
  }, [report, effective, page, slug]);

  const { data, error, loading } = useApi(path);
  const rows = Array.isArray(data) ? data : data?.content;
  const columns = REPORT_COLUMNS[slug] ?? (rows?.[0] ? Object.keys(rows[0]).map((key) => ({ key, label: key })) : []);

  if (catalogueError) return <ErrorMessage error={catalogueError} />;
  if (!catalogue) return <p className="muted">Loading…</p>;
  if (!report) return <p className="error-message">Unknown report “{slug}”.</p>;

  const run = (e) => {
    e.preventDefault();
    setPage(0);
    setSubmitted({ ...values });
  };

  return (
    <div>
      <h1>{report.title}</h1>
      <PrintButton />

      {report.params.length > 0 && (
        <form className="inline-form no-print" onSubmit={run}>
          {report.params.map((p) => (
            <ParamInput key={p.name} param={p} value={values[p.name] ?? ''}
                        onChange={(v) => setValues((old) => ({ ...old, [p.name]: v }))} />
          ))}
          <button type="submit">Run Report</button>
        </form>
      )}

      <ErrorMessage error={error} />
      {path
        ? <DataTable columns={columns} rows={rows} loading={loading} rowKey={(r, i) => i} emptyText="No rows for these parameters." />
        : <p className="muted">Choose the parameters and run the report.</p>}
      {report.pageable && data && !Array.isArray(data) && (
        <>
          <p className="muted small">{data.totalElements} row(s)</p>
          <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}

const LABELS = { periodCode: 'Period', fiscalYear: 'Fiscal Year', status: 'Status', months: 'Months Ahead' };
const STATUSES = ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'];
const REPORTABLE_PERIODS = ['PROCESSED', 'APPROVED', 'PAID', 'CLOSED'];

function ParamInput({ param, value, onChange }) {
  const id = `param-${param.name}`;
  const label = `${LABELS[param.name] ?? param.name}${param.required ? '' : ' (optional)'}`;
  let input;
  if (param.type === 'PERIOD_CODE') {
    input = <PeriodSelect id={id} value={value} onChange={onChange} onlyStatuses={REPORTABLE_PERIODS} />;
  } else if (param.name === 'status') {
    input = (
      <select id={id} value={value} onChange={(e) => onChange(e.target.value)}>
        <option value="">Any</option>
        {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
      </select>
    );
  } else if (param.type === 'INTEGER') {
    input = <input id={id} type="number" value={value} placeholder={param.defaultValue ?? ''} required={param.required}
                   onChange={(e) => onChange(e.target.value)} />;
  } else {
    input = <input id={id} value={value} required={param.required} onChange={(e) => onChange(e.target.value)} />;
  }
  return (
    <div className="filter-wide">
      <label htmlFor={id}>{label}</label>
      {input}
    </div>
  );
}

export default ReportPage;
