import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { afterEach, describe, expect, it } from 'vitest';
import { fakeServer } from '../../test/fakeServer';
import { clearApiCache } from '../../shared/api/useApi';
import ReportPage from './ReportPage';

const CATALOGUE = [
  { slug: 'payroll-trend', title: 'Payroll Trend', params: [{ name: 'fiscalYear', type: 'INTEGER', required: false }], pageable: false },
  { slug: 'payroll-register', title: 'Payroll Register', params: [{ name: 'periodCode', type: 'PERIOD_CODE', required: true }], pageable: true },
];

function renderReport(slug) {
  return render(
    <MemoryRouter initialEntries={[`/dashboard/reports/${slug}`]}>
      <Routes><Route path="/dashboard/reports/:slug" element={<ReportPage />} /></Routes>
    </MemoryRouter>,
  );
}

afterEach(() => clearApiCache());

describe('ReportPage', () => {
  it('runs a report without required parameters straight away, with its configured columns', async () => {
    fakeServer({
      'GET /reports': () => CATALOGUE,
      'GET /reports/payroll-trend': () => [{ periodCode: '2026-07', employees: 35, gross: 1000, deductions: 100, net: 900, companyCost: 1200 }],
      'GET /payroll-periods': () => [],
    });
    renderReport('payroll-trend');

    expect(await screen.findByText('Payroll Trend')).toBeTruthy();
    expect(await screen.findByText('2026-07')).toBeTruthy();
    expect(screen.getByText('Company Cost')).toBeTruthy();   // column label from reportColumns.jsx
    expect(screen.getByLabelText(/Fiscal Year/)).toBeTruthy(); // optional parameter form from the catalogue
  });

  it('waits for required parameters before calling the server', async () => {
    const { calls } = fakeServer({ 'GET /reports': () => CATALOGUE, 'GET /payroll-periods': () => [] });
    renderReport('payroll-register');

    expect(await screen.findByText('Choose the parameters and run the report.')).toBeTruthy();
    expect(calls.some((c) => c.path.startsWith('/reports/payroll-register'))).toBe(false);
  });

  it('says so for an unknown report', async () => {
    fakeServer({ 'GET /reports': () => CATALOGUE });
    renderReport('no-such-report');
    expect(await screen.findByText(/Unknown report/)).toBeTruthy();
  });
});
