# JRForge 🔧

A powerful Regex testing and Java error explanation plugin for IntelliJ IDEA.

---

## Features

### 1. Regex Tester
- Test any regex pattern against a string in real time
- See all matches with their exact positions
- Common preset patterns available (Email, Phone, URL, Date, IP, and more)

### 2. Explain Pattern
- Live explanation of your regex as you type
- Breaks down each token and explains what it matches
- Great for learning and debugging complex patterns

### 3. Test in JRForge (Editor Integration)
- Select any text in your Java file
- Right click → **"Test in JRForge"**
- Selected text is instantly sent to the Test String field

### 4. Editor Highlighting
- Hit **"Test Regex"** and every match lights up directly in your code file
- No more mental mapping — see matches exactly where they are
- Highlights clear automatically when you test a new pattern

### 5. 🤖 AI Error Explainer *(Powered by Claude)*
- Paste any Java error or exception
- Get a clear English explanation of what went wrong
- Understand why it happened and how to fix it
- Includes a code example for the fix

### 6. 🧬 Regex Reverse Engineer *(Never seen in any IDE)*
- Don't know regex? No problem
- Give examples of what should and shouldn't match
- Plugin generates the regex pattern for you automatically
- Validates that the pattern doesn't accidentally match negative examples

---

## Installation

### Manual Installation
1. Download the latest `.zip` from (https://github.com/your-username/JRForge/releases)
2. Open IntelliJ IDEA
3. Go to **File → Settings → Plugins**
4. Click ⚙️ → **Install Plugin from Disk**
5. Select the downloaded `.zip` file
6. Restart IntelliJ IDEA

---

## Usage

### Testing a Regex
1. Open JRForge panel (right side of IntelliJ)
2. Enter your regex in **Regex Pattern** field
3. Enter test string in **Test String** field
4. Click **Test Regex**

### Using Presets
1. Click **"-- Common Patterns --"** dropdown
2. Select any preset (Email, Phone, URL, etc.)
3. Pattern is auto-filled — just add your test string

### Test in JRForge
1. Select any text in your Java file
2. Right click
3. Click **"Test in JRForge"**

### AI Error Explainer
1. Copy any Java error from your console
2. Go to **🤖 AI Error Explainer** tab in JRForge
3. Paste the error
4. Click **✨ Explain with AI**

### Regex Reverse Engineer
1. Go to **🧬 Reverse Engineer** tab
2. Add examples that **should match** (one per line)
3. Add examples that **should NOT match** (one per line)
4. Click **🧬 Generate Regex**

---

## Tech Stack

- **Language:** Java
- **UI:** Java Swing
- **Plugin SDK:** IntelliJ Platform SDK
- **AI:** Anthropic Claude API
- **Build:** Gradle (Kotlin DSL)

---

## Project Structure
