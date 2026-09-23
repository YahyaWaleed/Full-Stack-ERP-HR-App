// columns: [{ key, label, render?: (row, index) => node, align?: 'right' }]
function DataTable({ columns, rows, rowKey = 'id', emptyText = 'Nothing to show.', loading = false }) {
  if (loading && (!rows || rows.length === 0)) {
    return <p className="muted">Loading…</p>;
  }
  if (!rows || rows.length === 0) {
    return <p className="empty-state">{emptyText}</p>;
  }
  return (
    <div className="table-wrapper">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((c) => (
              <th key={c.key} className={c.align === 'right' ? 'align-right' : undefined}>{c.label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={typeof rowKey === 'function' ? rowKey(row, i) : row[rowKey] ?? i}>
              {columns.map((c) => (
                <td key={c.key} className={c.align === 'right' ? 'align-right' : undefined}>
                  {c.render ? c.render(row, i) : formatCell(row[c.key])}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function formatCell(value) {
  if (value === null || value === undefined || value === '') return '—';
  if (typeof value === 'boolean') return value ? 'Yes' : 'No';
  return String(value);
}

export default DataTable;
