import { act, render, screen, waitFor } from '@testing-library/react';
import { afterEach, describe, expect, it } from 'vitest';
import { fakeServer } from '../../test/fakeServer';
import { clearApiCache, invalidate, useApi } from './useApi';

function Periods({ label = 'periods' }) {
  const { data, loading } = useApi('/payroll-periods');
  return <p>{label}: {loading ? 'loading' : data.map((p) => p.periodCode).join(',')}</p>;
}

function Employee({ id }) {
  const { data } = useApi(`/employees/${id}`);
  return <p>employee: {data ? data.name : '…'}</p>;
}

afterEach(() => clearApiCache());

describe('useApi', () => {
  it('shares one request between components asking for the same data', async () => {
    const { calls } = fakeServer({ 'GET /payroll-periods': () => [{ periodCode: '2026-07' }] });

    render(<><Periods label="a" /><Periods label="b" /></>);

    expect(await screen.findByText('a: 2026-07')).toBeTruthy();
    expect(await screen.findByText('b: 2026-07')).toBeTruthy();
    expect(calls).toHaveLength(1);
  });

  it('serves a later mount from the cache', async () => {
    const { calls } = fakeServer({ 'GET /payroll-periods': () => [{ periodCode: '2026-07' }] });
    const { unmount } = render(<Periods />);
    await screen.findByText('periods: 2026-07');
    unmount();

    render(<Periods />);
    expect(screen.getByText('periods: 2026-07')).toBeTruthy(); // immediately, no loading state
    expect(calls).toHaveLength(1);
  });

  it('refetches mounted views after invalidate()', async () => {
    let n = 0;
    fakeServer({ 'GET /payroll-periods': () => [{ periodCode: `v${(n += 1)}` }] });
    render(<Periods />);
    await screen.findByText('periods: v1');

    act(() => invalidate('/payroll-periods'));

    expect(await screen.findByText('periods: v2')).toBeTruthy();
  });

  it('never lets a slow response for an old id overwrite the current one', async () => {
    fakeServer({
      'GET /employees/1': async () => {
        await new Promise((r) => setTimeout(r, 50));
        return { name: 'slow first' };
      },
      'GET /employees/2': () => ({ name: 'second' }),
    });
    const { rerender } = render(<Employee id={1} />);
    rerender(<Employee id={2} />);

    await screen.findByText('employee: second');
    await new Promise((r) => setTimeout(r, 80)); // let the first request finish too
    await waitFor(() => expect(screen.getByText('employee: second')).toBeTruthy());
  });
});
