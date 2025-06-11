package com.yidigun.base.utils;

import com.yidigun.base.ErrorCode;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;

public enum SQLStateClass implements ErrorCode {

    // PosrtgreSQL SQL State Classes
    // TODO: 다른 DB 정보 확인

    // 커스텀 클래스
    UNKNOWN("??", "Unknown SQL State Class (maybe DBMS specific)")
    ,CONNECTION_TIMEOUT("C.", "Connection Timeout")
    ,QUERY_TIMEOUT("Q.", "Query Timeout")

    // 표준 클래스
    ,SUCCESS("00", "Success")
    ,WARNING("01", "Warning")
    ,NO_DATA("02", "No Data")
    ,NOT_COMPLETE("03", "Not Complete")
    ,CONNECTION_EXCEPTION("08", "Connection Exception")
    ,TRIGGERED_ACTION_EXCEPTION("09", "Triggered Action Exception")
    ,FEATURE_NOT_SUPPORTED("0A", "Feature Not Supported")
    ,INVALID_TRANSACTION_INITIATION("0B", "Invalid Transaction Initiation")
    ,LOCATOR_EXCEPTION("0F", "Locator Exception")
    ,INVALID_GRANTOR("0L", "Invalid Grantor")
    ,INVALID_ROLE_SPECIFICATION("0P", "Invalid Role Specification")
    ,DIAGNOSTICS_EXCEPTION("0Z", "Diagnostics Exception")
    ,CASE_NOT_FOUND("20", "Case Not Found")
    ,CARDINALITY_VIOLATION("21", "Cardinality Violation")
    ,DATA_EXCEPTION("22", "Data Exception")
    ,INTEGRITY_CONSTRAINT_VIOLATION("23", "Integrity Constraint Violation")
    ,INVALID_CURSOR_STATE("24", "Invalid Cursor State")
    ,INVALID_TRANSACTION_STATE("25", "Invalid Transaction State")
    ,INVALID_SQL_STATEMENT_NAME("26", "Invalid SQL Statement Name")
    ,TRIGGERED_DATA_CHANGE_VIOLATION("27", "Triggered Data Change Violation")
    ,INVALID_AUTHORIZATION_SPECIFICATION("28", "Invalid Authorization Specification")
    ,DEPENDENT_PRIVILEGE_DESCRIPTORS_STILL_EXIST("2B", "Dependent Privilege Descriptors Still Exist")
    ,INVALID_TRANSACTION_TERMINATION("2D", "Invalid Transaction Termination")
    ,SQL_ROUTINE_EXCEPTION("2F", "SQL Routine Exception")
    ,INVALID_CURSOR_NAME("34", "Invalid Cursor Name")
    ,EXTERNAL_ROUTINE_EXCEPTION("38", "External Routine Exception")
    ,EXTERNAL_ROUTINE_INVOCATION_EXCEPTION("39", "External Routine Invocation Exception")
    ,SAVEPOINT_EXCEPTION("3B", "Savepoint Exception")
    ,INVALID_CATALOG_NAME("3D", "Invalid Catalog Name")
    ,INVALID_SCHEMA_NAME("3F", "Invalid Schema Name")
    ,TRANSACTION_ROLLBACK("40", "Transaction Rollback")
    ,SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION("42", "Syntax Error or Access Rule Violation")
    ,WITH_CHECK_OPTION_VIOLATION("44", "With Check Option Violation")
    ,INSUFFICIENT_RESOURCES("53", "Insufficient Resources")
    ,PROGRAM_LIMIT_EXCEEDED("54", "Program Limit Exceeded")
    ,OBJECT_NOT_IN_PREREQUISITE_STATE("55", "Object Not In Prerequisite State")
    ,OPERATOR_INTERVENTION("57", "Operator Intervention")
    ,SYSTEM_ERROR("58", "System Error")
    ,CONFIGURATION_FILE_ERROR("F0", "Configuration File Error")
    ,FOREIGN_DATA_WRAPPER_ERROR("HV", "Foreign Data Wrapper Error")
    ,PLPGSQL_ERROR("P0", "PL/pgSQL Error")
    ,INTERNAL_ERROR("XX", "Internal Error")
    ;

    private final String code;
    private final String message;

    SQLStateClass(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() { return code; }
    @Override
    public String message() { return message; }
    @Override
    public boolean success() { return this == SUCCESS || this == WARNING; }

    public boolean timeout() {
        return this == CONNECTION_TIMEOUT || this == QUERY_TIMEOUT;
    }

    public static SQLStateClass of(String code) {
        if (code == null || code.length() < 2) {
            throw new IllegalArgumentException("SQL State codes must be 5 characters long, and code classes must be 2 characters long: code=" + code);
        }

        String clazz = code.substring(0, 2);
        for (SQLStateClass state : values()) {
            if (state.code.equals(clazz)) {
                return state;
            }
        }
        return UNKNOWN;
    }

    public static SQLStateClass of(SQLException cause) {

        // Timeout 예외 별도 처리
        if (cause instanceof SQLTimeoutException timeoutException) {
            return (cause.getSQLState().startsWith("08"))?
                    CONNECTION_TIMEOUT: QUERY_TIMEOUT;
        }

        // TODO: JDBC 드라이버 별 Timeout 예외 식별

        return of(cause.getSQLState());
    }
}
