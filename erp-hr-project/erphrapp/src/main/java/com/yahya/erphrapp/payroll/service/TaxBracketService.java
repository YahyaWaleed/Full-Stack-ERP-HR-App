package com.yahya.erphrapp.payroll.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import com.yahya.erphrapp.payroll.dto.TaxBracketResponse;
import com.yahya.erphrapp.payroll.entity.TaxBracket;
import com.yahya.erphrapp.payroll.mapper.TaxBracketMapper;
import com.yahya.erphrapp.payroll.repository.TaxBracketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxBracketService {

    private final TaxBracketRepository taxBracketRepository;
    private final TaxBracketMapper taxBracketMapper;

    public TaxBracketService(TaxBracketRepository taxBracketRepository, TaxBracketMapper taxBracketMapper) {
        this.taxBracketRepository = taxBracketRepository;
        this.taxBracketMapper = taxBracketMapper;
    }

    // read all tax brackets
    @Cacheable("taxBrackets")
    @Transactional(readOnly = true)
    public List<TaxBracketResponse> getBrackets() {
        return taxBracketRepository.findAllByOrderByPayrollSettingFiscalYearDescFromAmountAsc()
                .stream()
                .map(taxBracketMapper::toResponse)
                .toList();
    }

    // read all tax brackets for one fiscal year
    @Cacheable("taxBracketsByYear")
    @Transactional(readOnly = true)
    public List<TaxBracketResponse> getBracketsByFiscalYear(int fiscalYear) {
        return taxBracketRepository.findByPayrollSettingFiscalYearOrderByFromAmountAsc(fiscalYear)
                .stream()
                .map(taxBracketMapper::toResponse)
                .toList();
    }

    // read one tax bracket by its own ID
    @Transactional(readOnly = true)
    public TaxBracketResponse getBracket(Long id) {
        TaxBracket bracket = taxBracketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax Bracket", id));
        return taxBracketMapper.toResponse(bracket);
    }
}
