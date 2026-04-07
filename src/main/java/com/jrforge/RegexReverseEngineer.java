package com.jrforge.plugin.reverse;

import java.util.*;
import java.util.regex.*;

public class RegexReverseEngineer {

    /**
     * Takes a list of sample strings and generates a probable Java regex pattern.
     */
    public static String generate(List<String> samples) {
        if (samples == null || samples.isEmpty()) return "";

        // Deduce pattern from each sample, then merge
        List<String> patterns = new ArrayList<>();
        for (String sample : samples) {
            patterns.add(inferPattern(sample.trim()));
        }

        // Try to merge patterns into one using alternation
        return mergePatterns(patterns);
    }

    private static String inferPattern(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);

            // Detect digit runs → \d+
            if (Character.isDigit(c)) {
                int start = i;
                while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
                int len = i - start;
                sb.append(len == 1 ? "\\d" : "\\d{" + len + "}");
                continue;
            }

            // Detect letter runs → [A-Za-z]+
            if (Character.isLetter(c)) {
                int start = i;
                boolean hasUpper = false, hasLower = false;
                while (i < s.length() && Character.isLetter(s.charAt(i))) {
                    if (Character.isUpperCase(s.charAt(i))) hasUpper = true;
                    else hasLower = true;
                    i++;
                }
                int len = i - start;
                String cls = hasUpper && hasLower ? "[A-Za-z]"
                        : hasUpper ? "[A-Z]" : "[a-z]";
                sb.append(len == 1 ? cls : cls + "{" + len + "}");
                continue;
            }

            // Whitespace → \s
            if (Character.isWhitespace(c)) {
                sb.append("\\s");
                i++;
                continue;
            }

            // Special regex characters → escape them
            String special = "\\.^$|?*+()[]{}";
            if (special.indexOf(c) >= 0) {
                sb.append("\\").append(c);
            } else {
                sb.append(Pattern.quote(String.valueOf(c)));
            }
            i++;
        }
        return sb.toString();
    }

    /**
     * If all patterns are identical → return one.
     * If different → wrap in alternation: (pat1|pat2|...).
     */
    private static String mergePatterns(List<String> patterns) {
        Set<String> unique = new LinkedHashSet<>(patterns);
        if (unique.size() == 1) return unique.iterator().next();
        return "(" + String.join("|", unique) + ")";
    }

    /**
     * Validates that the generated pattern actually matches all samples.
     * Returns a list of any samples that do NOT match.
     */
    public static List<String> validatePattern(String pattern, List<String> samples) {
        List<String> failures = new ArrayList<>();
        try {
            Pattern p = Pattern.compile(pattern);
            for (String s : samples) {
                if (!p.matcher(s).matches()) {
                    failures.add(s);
                }
            }
        } catch (PatternSyntaxException e) {
            failures.addAll(samples); // invalid pattern
        }
        return failures;
    }
}
