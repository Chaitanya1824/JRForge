package com.jrforge;

import javax.swing.*;
import java.awt.*;
import java.util.regex.*;

public class RegexToolWindow {

    private JPanel mainPanel;
    private JTextField regexField;
    private JTextArea testInput;
    private JTextArea resultArea;
    private JTextArea explainArea;

    public RegexToolWindow() {
        mainPanel = new JPanel(new BorderLayout(10, 10));

        // Preset dropdown
        String[] presets = {
                "-- Common Patterns --",
                "Email Address",
                "Phone Number (10 digit)",
                "URL",
                "Date (DD/MM/YYYY)",
                "IP Address",
                "Password (min 8 chars)",
                "Only Digits",
                "Only Letters",
                "Alphanumeric"
        };
        JComboBox<String> presetBox = new JComboBox<>(presets);

        // Top panel - Regex input
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JPanel labelRow = new JPanel(new BorderLayout(5, 5));
        labelRow.add(new JLabel("Regex Pattern:"), BorderLayout.WEST);
        labelRow.add(presetBox, BorderLayout.EAST);
        topPanel.add(labelRow, BorderLayout.NORTH);
        regexField = new JTextField();
        topPanel.add(regexField, BorderLayout.CENTER);
        JButton testButton = new JButton("Test Regex");
        topPanel.add(testButton, BorderLayout.EAST);

        // Middle panel - Test input
        JPanel middlePanel = new JPanel(new BorderLayout(5, 5));
        middlePanel.add(new JLabel("Test String:"), BorderLayout.NORTH);
        testInput = new JTextArea(4, 30);
        testInput.setLineWrap(true);
        middlePanel.add(new JScrollPane(testInput), BorderLayout.CENTER);

        // Tabbed panel - Results + Explainer
        JTabbedPane tabbedPane = new JTabbedPane();

        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        tabbedPane.addTab("Match Results", new JScrollPane(resultArea));

        explainArea = new JTextArea(5, 30);
        explainArea.setEditable(false);
        explainArea.setLineWrap(true);
        explainArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tabbedPane.addTab("Explain Pattern", new JScrollPane(explainArea));

        // Add all to main panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(middlePanel, BorderLayout.NORTH);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Preset selection action
        presetBox.addActionListener(e -> {
            String selected = (String) presetBox.getSelectedItem();
            switch (selected) {
                case "Email Address":
                    regexField.setText("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");
                    break;
                case "Phone Number (10 digit)":
                    regexField.setText("\\d{10}");
                    break;
                case "URL":
                    regexField.setText("https?://[\\w\\-]+(\\.[\\w\\-]+)+(/[\\w\\-./?%&=]*)?");
                    break;
                case "Date (DD/MM/YYYY)":
                    regexField.setText("\\d{2}/\\d{2}/\\d{4}");
                    break;
                case "IP Address":
                    regexField.setText("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
                    break;
                case "Password (min 8 chars)":
                    regexField.setText("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
                    break;
                case "Only Digits":
                    regexField.setText("\\d+");
                    break;
                case "Only Letters":
                    regexField.setText("[a-zA-Z]+");
                    break;
                case "Alphanumeric":
                    regexField.setText("[a-zA-Z0-9]+");
                    break;
            }
        });

        // Button action
        testButton.addActionListener(e -> {
            testRegex();
            explainRegex();
        });

        // Live explain as user types
        regexField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { explainRegex(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { explainRegex(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { explainRegex(); }
        });
    }

    private void testRegex() {
        String regex = regexField.getText().trim();
        String testString = testInput.getText().trim();

        if (regex.isEmpty()) {
            resultArea.setText("Please enter a regex pattern.");
            return;
        }

        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(testString);
            StringBuilder results = new StringBuilder();
            int matchCount = 0;

            while (matcher.find()) {
                matchCount++;
                results.append("Match ").append(matchCount)
                        .append(": \"").append(matcher.group()).append("\"")
                        .append(" at index ").append(matcher.start())
                        .append("-").append(matcher.end()).append("\n");
            }

            if (matchCount == 0) {
                resultArea.setText("No matches found.");
            } else {
                resultArea.setText("Found " + matchCount + " match(es):\n\n" + results.toString());
            }

        } catch (PatternSyntaxException ex) {
            resultArea.setText("Invalid regex: " + ex.getMessage());
        }
    }

    private void explainRegex() {
        String regex = regexField.getText();
        explainArea.setText(RegexExplainer.explain(regex));
    }

    public JPanel getContent() {
        return mainPanel;
    }

    public void setRegexPattern(String selectedText) {
    }
}