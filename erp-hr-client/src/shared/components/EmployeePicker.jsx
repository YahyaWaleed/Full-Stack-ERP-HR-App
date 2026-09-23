import AsyncSelect from 'react-select/async';
import { api } from '../api/client';
import { selectStyles } from './selectStyles';

const toOption = (e) => ({ value: e.id, label: `${e.empCode} — ${e.fullNameEn}`, employee: e });

// searches employees on the server as you type (GET /employees?q=...) instead of downloading
// the whole company into the browser. `filters` adds query params, e.g. { status: 'ACTIVE' } or { managerial: true }.
function EmployeePicker({ value, onChange, id, filters = {}, placeholder = 'Type a name or employee code…' }) {
  const query = new URLSearchParams({ size: '20', ...filters }).toString();

  const loadOptions = async (text) => {
    const page = await api.get(`/employees?${query}&q=${encodeURIComponent(text)}`);
    return page.content.map(toOption);
  };

  return (
    <AsyncSelect
      inputId={id}
      cacheOptions
      defaultOptions
      loadOptions={loadOptions}
      value={value ? toOption(value) : null}
      onChange={(option) => onChange(option ? option.employee : null)}
      placeholder={placeholder}
      isClearable
      styles={selectStyles}
      noOptionsMessage={({ inputValue }) => (inputValue ? 'No matching employees' : 'Start typing to search')}
    />
  );
}

export default EmployeePicker;
