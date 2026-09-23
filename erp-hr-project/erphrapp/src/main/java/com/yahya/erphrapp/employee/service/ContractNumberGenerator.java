package com.yahya.erphrapp.employee.service;

import com.yahya.erphrapp.employee.repository.EmployeeContractRepository;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// The one place contract numbers are made: CT-<start year>-<employee id, 4 digits>-<sequence, 2 digits>,
// e.g. CT-2026-0035-02. The sequence continues from the employee's highest existing number rather than
// counting rows, so deleting a contract never causes a number to be reused.
// Callers must hold a write lock on the employee row (EmployeeRepository.findByIdForUpdate) so two
// concurrent renewals for the same employee cannot both pick the same sequence.
@Component
public class ContractNumberGenerator {

    private static final Pattern SEQUENCE = Pattern.compile("^CT-\\d{4}-(\\d{4})-(\\d{2})$");

    private final EmployeeContractRepository contractRepository;

    public ContractNumberGenerator(EmployeeContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public String next(long empId, int startYear) {
        int highest = 0;
        for (String contractNo : contractRepository.findContractNosByEmployeeId(empId)) {
            Matcher m = SEQUENCE.matcher(contractNo);
            if (m.matches() && Long.parseLong(m.group(1)) == empId) {
                highest = Math.max(highest, Integer.parseInt(m.group(2)));
            } else {
                // contracts from before this format (e.g. seeded CT-2010-001) still count towards the sequence
                highest = Math.max(highest, 1);
            }
        }
        return String.format("CT-%d-%04d-%02d", startYear, empId, highest + 1);
    }
}
