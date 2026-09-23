// drops empty-string fields (recursively) so optional inputs are sent as "not provided", not as ""
export function compact(value) {
  if (Array.isArray(value)) return value.map(compact);
  if (value && typeof value === 'object') {
    return Object.fromEntries(
      Object.entries(value)
        .filter(([, v]) => v !== '' && v !== undefined)
        .map(([k, v]) => [k, compact(v)]),
    );
  }
  return value;
}

// counts days in [start, end] that aren't Friday or Saturday (public holidays are handled by the server)
export function weekdaysBetween(startDate, endDate) {
  if (!startDate || !endDate) return '';
  const start = new Date(`${startDate}T00:00:00`);
  const end = new Date(`${endDate}T00:00:00`);
  if (start > end) return '';
  let count = 0;
  for (const d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const day = d.getDay(); // 5 = Friday, 6 = Saturday
    if (day !== 5 && day !== 6) count += 1;
  }
  return count;
}
