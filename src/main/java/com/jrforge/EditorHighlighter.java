package com.jrforge.plugin.editor;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class EditorHighlighter {

    // We store added highlighters so we can clear them later
    private static final List<RangeHighlighter> activeHighlighters = new ArrayList<>();

    // Highlight color — bright yellow background like a real search result
    private static final TextAttributes MATCH_ATTR = new TextAttributes(
            null,
            new JBColor(new java.awt.Color(255, 235, 59),   // light theme: yellow
                    new java.awt.Color(180, 140, 0)),   // dark theme: darker gold
            null,
            EffectType.BOXED,
            java.awt.Font.BOLD
    );

    /**
     * Highlight all regex matches in the currently open editor file.
     * Call this from your plugin action or tool window button.
     */
    public static void highlightInEditor(Project project, Editor editor, String regexPattern) {
        if (editor == null || regexPattern == null || regexPattern.isBlank()) {
            Messages.showWarningDialog(project,
                    "Please open a file and enter a regex pattern first.",
                    "JRForge – Highlight");
            return;
        }

        // Clear previous highlights first
        clearHighlights(editor);

        String fileText = editor.getDocument().getText();
        MarkupModel markupModel = editor.getMarkupModel();

        Pattern pattern;
        try {
            pattern = Pattern.compile(regexPattern);
        } catch (PatternSyntaxException ex) {
            Messages.showErrorDialog(project,
                    "Invalid regex pattern: " + ex.getMessage(),
                    "JRForge – Pattern Error");
            return;
        }

        Matcher matcher = pattern.matcher(fileText);
        int count = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end   = matcher.end();
            if (start == end) continue; // skip zero-length matches

            RangeHighlighter h = markupModel.addRangeHighlighter(
                    start,
                    end,
                    HighlighterLayer.SELECTION - 1,  // just below selection layer
                    MATCH_ATTR,
                    HighlighterTargetArea.EXACT_RANGE
            );
            h.setErrorStripeMarkColor(java.awt.Color.YELLOW);
            h.setErrorStripeTooltip("JRForge match: " + matcher.group());
            activeHighlighters.add(h);
            count++;
        }

        if (count == 0) {
            Messages.showInfoMessage(project,
                    "No matches found in the current file.",
                    "JRForge – Highlight");
        } else {
            // Show a non-blocking notification in the status bar
            com.intellij.openapi.wm.WindowManager.getInstance()
                    .getStatusBar(project)
                    .setInfo("JRForge: " + count + " match(es) highlighted.");
        }
    }

    /**
     * Remove all JRForge highlights from the editor.
     */
    public static void clearHighlights(Editor editor) {
        MarkupModel markupModel = editor.getMarkupModel();
        for (RangeHighlighter h : activeHighlighters) {
            if (h.isValid()) {
                markupModel.removeHighlighter(h);
            }
        }
        activeHighlighters.clear();
    }
}
