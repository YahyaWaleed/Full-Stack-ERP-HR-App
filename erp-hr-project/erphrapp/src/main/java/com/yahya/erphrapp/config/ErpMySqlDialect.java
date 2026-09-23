package com.yahya.erphrapp.config;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

import java.sql.Types;

// MySQL dialect used so that ddl-auto=validate checks what matters without false alarms.
// The schema uses compact column types (TINYINT/SMALLINT/INT ids, CHAR(7) period codes) while the entities use
// int/Long/String. Those read and write correctly, but the stock validator treats every integer width -- and
// CHAR vs VARCHAR -- as a different type. This keeps the check for missing tables/columns and for real
// mismatches (text vs number, date vs datetime, ...) and only relaxes those two families.
public class ErpMySqlDialect extends MySQLDialect {

    public ErpMySqlDialect() {
        super();
    }

    public ErpMySqlDialect(DatabaseVersion version) {
        super(version);
    }

    public ErpMySqlDialect(DialectResolutionInfo info) {
        super(info);
    }

    @Override
    public boolean equivalentTypes(int typeCode1, int typeCode2) {
        return super.equivalentTypes(typeCode1, typeCode2)
                || (isIntegral(typeCode1) && isIntegral(typeCode2))
                || (isFixedOrVaryingChar(typeCode1) && isFixedOrVaryingChar(typeCode2));
    }

    private static boolean isIntegral(int typeCode) {
        return typeCode == Types.TINYINT || typeCode == Types.SMALLINT
                || typeCode == Types.INTEGER || typeCode == Types.BIGINT;
    }

    private static boolean isFixedOrVaryingChar(int typeCode) {
        return typeCode == Types.CHAR || typeCode == Types.VARCHAR
                || typeCode == Types.NCHAR || typeCode == Types.NVARCHAR;
    }
}
