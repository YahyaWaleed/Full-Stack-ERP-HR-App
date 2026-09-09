package com.yahya.erphrapp.payroll.controller;

import com.yahya.erphrapp.payroll.dto.TaxBracketResponse;
import com.yahya.erphrapp.payroll.service.TaxBracketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tax-brackets")
public class TaxBracketController {

    private final TaxBracketService taxBracketService;

    public TaxBracketController(TaxBracketService taxBracketService) {
        this.taxBracketService = taxBracketService;
    }

    // read all tax brackets
    @GetMapping
    public List<TaxBracketResponse> getBrackets() {
        return taxBracketService.getBrackets();
    }

    // read tax brackets for one fiscal year
    @GetMapping("/fiscal-year/{year}")
    public List<TaxBracketResponse> getBracketsByFiscalYear(@PathVariable int year) {
        return taxBracketService.getBracketsByFiscalYear(year);
    }

    // read one tax bracket by its own ID
    @GetMapping("/{id}")
    public TaxBracketResponse getBracket(@PathVariable Long id) {
        return taxBracketService.getBracket(id);
    }
}