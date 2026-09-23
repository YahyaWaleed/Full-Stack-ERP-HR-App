package com.yahya.erphrapp.report;

import java.util.List;

// one report = its URL slug, a title, the SQL and the parameters it accepts.
// Columns are returned as camelCase JSON fields (full_name_ar -> fullNameAr), so the SQL aliases are the contract.
public record ReportDefinition(String slug, String title, String sql, List<Param> params, boolean pageable) {

    public enum ParamType { TEXT, INTEGER, PERIOD_CODE }

    // defaultValue is used when the parameter is optional and not sent (null = SQL NULL, i.e. "no filter")
    public record Param(String name, ParamType type, boolean required, String defaultValue) {

        public static Param required(String name, ParamType type) {
            return new Param(name, type, true, null);
        }

        public static Param optional(String name, ParamType type, String defaultValue) {
            return new Param(name, type, false, defaultValue);
        }
    }
}
