import { useMemo } from 'react';
import Select from 'react-select';
import { useApi } from '../api/useApi';
import { selectStyles } from './selectStyles';

// searchable payroll-period dropdown; the period list is fetched once and shared through the cache
function PeriodSelect({ value, onChange, id, onlyStatuses }) {
  const { data: periods = [], error } = useApi('/payroll-periods', { ttl: 60_000 });

  const options = useMemo(
    () => periods
      .filter((p) => !onlyStatuses || onlyStatuses.includes(p.status))
      .map((p) => ({ value: p.periodCode, label: `${p.periodCode} (${p.status})` })),
    [periods, onlyStatuses],
  );

  return (
    <>
      <Select
        inputId={id}
        options={options}
        value={options.find((o) => o.value === value) || null}
        onChange={(option) => onChange(option ? option.value : '')}
        placeholder="Search period code…"
        isSearchable
        isClearable
        styles={selectStyles}
      />
      {error && <p className="field-error">Couldn’t load payroll periods: {error.message}</p>}
    </>
  );
}

export default PeriodSelect;
