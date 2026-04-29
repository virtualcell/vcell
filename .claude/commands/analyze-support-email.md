---
description: Parse and analyze a VCell support email (.eml or .json) to identify bugs
---

Parse the VCell support email file and analyze the errors.

Run the parser to extract structured error information:

```
python3 tools/parse_support_email.py $ARGUMENTS
```

Then analyze the output:

1. **Identify the root cause exception** — the last/deepest `Caused by:` in each stack trace chain is usually the root cause. Focus on VCell package frames (`cbit.vcell.*`, `org.vcell.*`), not JDK internals.

2. **Classify the errors:**
   - **Crashes** — NullPointerException, IllegalStateException, etc. that indicate bugs in VCell code
   - **User errors** — expression parse errors, invalid model configurations
   - **Infrastructure** — SSL failures, connection timeouts, server-side issues
   - **UI bugs** — button-not-found, timing issues, EDT violations

3. **For each crash-level bug**, trace the call stack to the relevant source file in this repo. Read the code at the crash point and suggest what went wrong and how to fix it.

4. **Report format:**
   - Version and platform
   - Bullet list of distinct issues found, ordered by severity
   - For each fixable bug: the file:line, root cause, and suggested fix
