import { describe, expect, it } from 'vitest';
import { compact, weekdaysBetween } from './forms';

describe('compact', () => {
  it('drops empty strings at every level so optional fields are sent as absent', () => {
    expect(compact({ email: '', mobile: '0100', contract: { endDate: '', basicSalary: 5000 } }))
      .toEqual({ mobile: '0100', contract: { basicSalary: 5000 } });
  });
});

describe('weekdaysBetween', () => {
  it('skips Fridays and Saturdays', () => {
    expect(weekdaysBetween('2026-10-04', '2026-10-10')).toBe(5); // Sun..Sat
  });

  it('is empty for a reversed or incomplete range', () => {
    expect(weekdaysBetween('2026-10-10', '2026-10-04')).toBe('');
    expect(weekdaysBetween('2026-10-10', '')).toBe('');
  });
});
