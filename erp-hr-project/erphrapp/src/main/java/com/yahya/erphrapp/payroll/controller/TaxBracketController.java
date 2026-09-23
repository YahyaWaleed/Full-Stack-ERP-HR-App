package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.payroll.dto.TaxBracketResponse;
import com.yahya.erphrapp.payroll.service.TaxBracketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tax-brackets")
public class TaxBracketController {

    private final TaxBracketService taxBracketService;

    public TaxBracketController(TaxBracketService taxBracketService) {
        this.taxBracketService = taxBracketService;
    }

    // all brackets, or one fiscal year: GET /tax-brackets?fiscalYear=2026
    @GetMapping
    public List<TaxBracketResponse> getBrackets(@RequestParam(required = false) Integer fiscalYear) {
        return fiscalYear == null ? taxBracketService.getBrackets() : taxBracketService.getBracketsByFiscalYear(fiscalYear);
    }

    @GetMapping("/{id}")
    public TaxBracketResponse getBracket(@PathVariable Long id) {
        return taxBracketService.getBracket(id);
    }
}
