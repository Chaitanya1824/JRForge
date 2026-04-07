package com.jrforge;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.regex.*;
// IntelliJ Platform SDK imports — requires IntelliJ Platform Gradle Plugin in build.gradle.kts
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegexToolWindow {

    // FIX: made fields final (warnings at lines 18–22)
    private final JPanel mainPanel;
    private final JTextField regexField;
    private final JTextArea testInput;
    private final JTextArea resultArea;
    private final JTextArea explainArea;

    public RegexToolWindow() {
        mainPanel  = new JPanel(new BorderLayout(10, 10));
        regexField = new JTextField();
        testInput  = new JTextArea(4, 30);
        resultArea = new JTextArea(5, 30);
        explainArea = new JTextArea(5, 30);

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
        JPanel topPanel  = new JPanel(new BorderLayout(5, 5));
        JPanel labelRow  = new JPanel(new BorderLayout(5, 5));
        labelRow.add(new JLabel("Regex Pattern:"), BorderLayout.WEST);
        labelRow.add(presetBox, BorderLayout.EAST);
        topPanel.add(labelRow, BorderLayout.NORTH);
        topPanel.add(regexField, BorderLayout.CENTER);

        JButton testButton    = new JButton("Test Regex");
        JButton highlightBtn  = new JButton("\uD83D\uDD8A Highlight in Editor");
        JButton clearBtn      = new JButton("\u2715 Clear");

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonRow.add(testButton);
        buttonRow.add(highlightBtn);
        buttonRow.add(clearBtn);
        topPanel.add(buttonRow, BorderLayout.EAST);

        // Highlight button logic
        highlightBtn.addActionListener((ActionEvent e) -> {
            String pattern = regexField.getText().trim();
            if (pattern.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Enter a regex pattern first.", "JRForge",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            DataContext dataContext = DataManager.getInstance().getDataContext(mainPanel);
            Project project = CommonDataKeys.PROJECT.getData(dataContext);
            Editor  editor  = CommonDataKeys.EDITOR.getData(dataContext);
            EditorHighlighter.highlightInEditor(project, editor, pattern);
        });

        clearBtn.addActionListener((ActionEvent e) -> {
            DataContext dataContext = DataManager.getInstance().getDataContext(mainPanel);
            Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
            if (editor != null) {
                EditorHighlighter.clearHighlights(editor);
            }
        });

        // Middle panel - Test input
        JPanel middlePanel = new JPanel(new BorderLayout(5, 5));
        middlePanel.add(new JLabel("Test String:"), BorderLayout.NORTH);
        testInput.setLineWrap(true);
        middlePanel.add(new JScrollPane(testInput), BorderLayout.CENTER);

        // Tabbed panel - Results + Explainer
        JTabbedPane tabbedPane = new JTabbedPane();

        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        tabbedPane.addTab("Match Results", new JScrollPane(resultArea));

        explainArea.setEditable(false);
        explainArea.setLineWrap(true);
        explainArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tabbedPane.addTab("Explain Pattern", new JScrollPane(explainArea));
        tabbedPane.addTab("Reverse Engineer", buildReverseEngineerPanel());

        // Add all to main panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(middlePanel, BorderLayout.NORTH);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Preset selection action
        // FIX: null-check on selected (NullPointerException warning at line 120)
        presetBox.addActionListener((ActionEvent e) -> {
            String selected = (String) presetBox.getSelectedItem();
            if (selected == null) return;
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
                default:
                    break;
            }
        });

        // Button action
        testButton.addActionListener((ActionEvent e) -> {
            testRegex();
            explainRegex();
        });

        // Live explain as user types
        regexField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { explainRegex(); }
            public void removeUpdate(DocumentEvent e)  { explainRegex(); }
            public void changedUpdate(DocumentEvent e) { explainRegex(); }
        });
    }

    private void testRegex() {
        String regex      = regexField.getText().trim();
        String testString = testInput.getText().trim();

        if (regex.isEmpty()) {
            resultArea.setText("Please enter a regex pattern.");
            return;
        }

        try {
            Pattern pattern  = Pattern.compile(regex);
            Matcher matcher  = pattern.matcher(testString);
            StringBuilder sb = new StringBuilder();
            int matchCount   = 0;

            while (matcher.find()) {
                matchCount++;
                sb.append("Match ").append(matchCount)
                        .append(": \"").append(matcher.group()).append("\"")
                        .append(" at index ").append(matcher.start())
                        .append("-").append(matcher.end()).append("\n");
            }

            if (matchCount == 0) {
                resultArea.setText("No matches found.");
            } else {
                // FIX: removed unnecessary .toString() call (warning at line 191)
                resultArea.setText("Found " + matchCount + " match(es):\n\n" + sb);
            }

        } catch (PatternSyntaxException ex) {
            resultArea.setText("Invalid regex: " + ex.getMessage());
        }
    }

    private void explainRegex() {
        String regex = regexField.getText();
        explainArea.setText(RegexExplainer.explain(regex));
    }

    public JPanel getContent() { return mainPanel; }

    // FIX: parameter 'selectedText' was unused (warning at line 208) — now actually used
    public void setRegexPattern(String selectedText) {
        regexField.setText(selectedText);
    }

    // FIX: extracted copyToClipboard helper to reduce method length (warning at line 227)
    private void copyToClipboard(String text, JButton copyBtn) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
        copyBtn.setText("Copied!");
        new Timer(1500, ev -> copyBtn.setText("Copy Pattern")).start();
    }

    private JPanel buildReverseEngineerPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel    inputLabel      = new JLabel("Enter sample strings (one per line):");
        JTextArea sampleInput     = new JTextArea(6, 40);
        sampleInput.setLineWrap(true);
        sampleInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JLabel     outputLabel      = new JLabel("Generated Regex Pattern:");
        JTextField generatedPattern = new JTextField();
        generatedPattern.setEditable(false);
        generatedPattern.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));

        JLabel validationLabel = new JLabel(" ");
        validationLabel.setForeground(new Color(0, 150, 0));

        JButton generateBtn = new JButton("Generate Regex");
        JButton copyBtn     = new JButton("Copy Pattern");

        generateBtn.addActionListener((ActionEvent e) -> {
            String raw = sampleInput.getText().trim();
            if (raw.isEmpty()) {
                validationLabel.setText("Please enter at least one sample.");
                validationLabel.setForeground(Color.ORANGE);
                return;
            }
            List<String> samples = new ArrayList<>(
                    Arrays.asList(raw.split("\\r?\\n"))
            );
            samples.removeIf(String::isBlank);

            // FIX: RegexReverseEngineer must exist in the same package (com.jrforge)
            String pattern = RegexReverseEngineer.generate(samples);
            generatedPattern.setText(pattern);
            regexField.setText(pattern); // auto-copy to main regex field

            List<String> failures = RegexReverseEngineer.validatePattern(pattern, samples);
            if (failures.isEmpty()) {
                validationLabel.setForeground(new Color(0, 150, 0));
                validationLabel.setText("Matches all " + samples.size() + " sample(s)!");
            } else {
                validationLabel.setForeground(Color.RED);
                validationLabel.setText(failures.size() + " sample(s) didn't match.");
            }
        });

        copyBtn.addActionListener((ActionEvent e) -> {
            String text = generatedPattern.getText();
            if (!text.isEmpty()) {
                copyToClipboard(text, copyBtn);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.add(generateBtn);
        btnRow.add(copyBtn);

        JPanel outputPanel = new JPanel(new BorderLayout(4, 4));
        outputPanel.add(outputLabel,      BorderLayout.NORTH);
        outputPanel.add(generatedPattern, BorderLayout.CENTER);
        outputPanel.add(validationLabel,  BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.add(btnRow,        BorderLayout.NORTH);
        bottom.add(outputPanel,   BorderLayout.CENTER);

        panel.add(inputLabel,                   BorderLayout.NORTH);
        panel.add(new JScrollPane(sampleInput), BorderLayout.CENTER);
        panel.add(bottom,                       BorderLayout.SOUTH);

        return panel;
    }

    // FIX: 'getPanel()' was never used (warning at line 288) — removed duplicate,
    // use getContent() instead (already defined above). If the platform requires
    // getPanel(), keep this and remove getContent().
    // public JPanel getPanel() { return mainPanel; }
}