package com.pragma.plazoleta.domain.utils;

import java.util.regex.Pattern;

public class Constants {
    private Constants() {}
    
    public static final String EMPLOYEE_ROLE = "EMPLOYEE";
    public static final String CUSTOMER_ROLE = "CUSTOMER";
    public static final int MAXIMUM_PHONE_LENGTH = 13;
    public static final Pattern PHONE_PATTERN_REQUIRED = Pattern.compile("^\\+?\\d{1,13}$");
    public static final Pattern NAME_PATTERN_REQUIRED = Pattern.compile(".*[a-zA-Z].*");
    public static final String UUID_PATTERN = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
}
