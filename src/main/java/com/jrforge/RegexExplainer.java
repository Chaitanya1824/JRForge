package com.jrforge;

import java.util.*;

public class RegexExplainer {

    public static String explain(String regex) {
        if (regex == null || regex.trim().isEmpty()) {
            return "Enter a regex pattern to see its explanation.";
        }

        StringBuilder explanation = new StringBuilder();
        explanation.append("Pattern Breakdown:\n\n");

        int i = 0;
        while (i < regex.length()) {
            char c = regex.charAt(i);

            if (c == '\\' && i + 1 < regex.length()) {
                char next = regex.charAt(i + 1);
                String token = "\\" + next;
                String desc = explainEscape(next);

                // Check for quantifier after escape
                String quantifier = "";
                int skip = 0;
                if (i + 2 < regex.length()) {
                    String q = getQuantifier(regex, i + 2);
                    if (!q.isEmpty()) {
                        quantifier = " " + explainQuantifier(q);
                        skip = q.length();
                    }
                }

                explanation.append("  ").append(token)
                        .append(getQuantifierRaw(regex, i + 2))
                        .append("  →  ").append(desc).append(quantifier).append("\n");
                i += 2 + skip;

            } else if (c == '[') {
                int end = regex.indexOf(']', i);
                if (end != -1) {
                    String charClass = regex.substring(i, end + 1);
                    explanation.append("  ").append(charClass)
                            .append("  →  Character class: matches any character in ").append(charClass).append("\n");
                    i = end + 1;
                } else {
                    explanation.append("  [  →  Opening character class bracket\n");
                    i++;
                }

            } else if (c == '(') {
                explanation.append("  (  →  Start of capturing group\n");
                i++;

            } else if (c == ')') {
                explanation.append("  )  →  End of capturing group\n");
                i++;

            } else if (c == '{') {
                int end = regex.indexOf('}', i);
                if (end != -1) {
                    String quantifier = regex.substring(i, end + 1);
                    explanation.append("  ").append(quantifier)
                            .append("  →  ").append(explainQuantifier(quantifier)).append("\n");
                    i = end + 1;
                } else {
                    i++;
                }

            } else if (c == '*') {
                explanation.append("  *  →  Zero or more times\n");
                i++;
            } else if (c == '+') {
                explanation.append("  +  →  One or more times\n");
                i++;
            } else if (c == '?') {
                explanation.append("  ?  →  Zero or one time (optional)\n");
                i++;
            } else if (c == '.') {
                explanation.append("  .  →  Any character except newline\n");
                i++;
            } else if (c == '^') {
                explanation.append("  ^  →  Start of string\n");
                i++;
            } else if (c == '$') {
                explanation.append("  $  →  End of string\n");
                i++;
            } else if (c == '|') {
                explanation.append("  |  →  OR — matches either side\n");
                i++;
            } else {
                explanation.append("  ").append(c)
                        .append("  →  Literal character '").append(c).append("'\n");
                i++;
            }
        }

        return explanation.toString();
    }

    private static String explainEscape(char c) {
        switch (c) {
            case 'd': return "Any digit (0-9)";
            case 'D': return "Any non-digit";
            case 'w': return "Any word character (a-z, A-Z, 0-9, _)";
            case 'W': return "Any non-word character";
            case 's': return "Any whitespace (space, tab, newline)";
            case 'S': return "Any non-whitespace character";
            case 'b': return "Word boundary";
            case 'B': return "Non-word boundary";
            case 'n': return "Newline character";
            case 't': return "Tab character";
            case 'r': return "Carriage return";
            case '.': return "Literal dot '.'";
            case '*': return "Literal asterisk '*'";
            case '+': return "Literal plus '+'";
            case '?': return "Literal question mark '?'";
            case '(': return "Literal opening parenthesis";
            case ')': return "Literal closing parenthesis";
            case '[': return "Literal opening bracket";
            case ']': return "Literal closing bracket";
            case '{': return "Literal opening brace";
            case '}': return "Literal closing brace";
            case '\\': return "Literal backslash";
            case '^': return "Literal caret";
            case '$': return "Literal dollar sign";
            case '|': return "Literal pipe";
            default: return "Escaped character '\\" + c + "'";
        }
    }

    private static String explainQuantifier(String q) {
        if (q.equals("{1}")) return "Exactly 1 time";
        if (q.matches("\\{\\d+\\}")) {
            String num = q.replaceAll("[{}]", "");
            return "Exactly " + num + " times";
        }
        if (q.matches("\\{\\d+,\\}")) {
            String num = q.replaceAll("[{,}]", "");
            return "At least " + num + " times";
        }
        if (q.matches("\\{\\d+,\\d+\\}")) {
            String[] parts = q.replaceAll("[{}]", "").split(",");
            return "Between " + parts[0] + " and " + parts[1] + " times";
        }
        return q;
    }

    private static String getQuantifier(String regex, int start) {
        if (start >= regex.length()) return "";
        char c = regex.charAt(start);
        if (c == '*' || c == '+' || c == '?') return String.valueOf(c);
        if (c == '{') {
            int end = regex.indexOf('}', start);
            if (end != -1) return regex.substring(start, end + 1);
        }
        return "";
    }

    private static String getQuantifierRaw(String regex, int start) {
        if (start >= regex.length()) return "";
        char c = regex.charAt(start);
        if (c == '*' || c == '+' || c == '?') return String.valueOf(c);
        if (c == '{') {
            int end = regex.indexOf('}', start);
            if (end != -1) return regex.substring(start, end + 1);
        }
        return "";
    }
}