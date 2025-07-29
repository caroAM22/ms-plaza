package com.pragma.plazoleta.domain.utils;

import java.util.regex.Pattern;

public class RegexPattern {
    private RegexPattern() {}
    public static final Pattern PHONE_PATTERN_REQUIRED = Pattern.compile("^\\+?\\d{1,13}$");
    public static final Pattern NAME_PATTERN_REQUIRED = Pattern.compile(".*[a-zA-Z].*");
}
