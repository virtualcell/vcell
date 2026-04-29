#!/usr/bin/env python3
"""
Parse VCell support email (.eml or .json) and extract structured error info.

Usage:
    python tools/parse_support_email.py <file.eml|file.json>

Output: structured text with sections for metadata, log entries, and stack traces,
suitable for analysis by an LLM or developer.
"""

import email
import json
import re
import sys
from pathlib import Path


def decode_eml(path: str) -> str:
    """Decode a .eml file and return the plain-text body."""
    with open(path, "rb") as f:
        msg = email.message_from_bytes(f.read())
    if msg.is_multipart():
        for part in msg.walk():
            if part.get_content_type() == "text/plain":
                return part.get_payload(decode=True).decode("utf-8", errors="replace")
    return msg.get_payload(decode=True).decode("utf-8", errors="replace")


def extract_json_from_body(body: str) -> dict:
    """Find and parse the JSON object embedded in the email body."""
    # Find the first '{' which starts the JSON payload
    idx = body.find("{")
    if idx == -1:
        raise ValueError("No JSON object found in email body")
    # Find the matching closing brace (the JSON is typically one object)
    depth = 0
    for i in range(idx, len(body)):
        if body[i] == "{":
            depth += 1
        elif body[i] == "}":
            depth -= 1
            if depth == 0:
                return json.loads(body[idx : i + 1])
    raise ValueError("Unbalanced braces in JSON payload")


def parse_file(path: str) -> dict:
    """Parse a .eml or .json support email file into a structured dict."""
    p = Path(path)
    if p.suffix == ".json":
        with open(p) as f:
            return json.load(f)
    elif p.suffix == ".eml":
        body = decode_eml(path)
        return extract_json_from_body(body)
    else:
        raise ValueError(f"Unsupported file type: {p.suffix}")


def extract_stack_traces(text: str) -> list[str]:
    """Extract Java stack traces from a block of text.

    A stack trace starts with a line containing an exception class
    (e.g., 'java.lang.NullPointerException: ...') followed by lines
    starting with 'at ' or 'Caused by:'.
    """
    traces = []
    current_trace = []
    in_trace = False

    for line in text.split("\n"):
        stripped = line.strip()
        # Detect start of a stack trace
        if not in_trace and re.match(
            r"^([\w$.]+Exception|[\w$.]+Error|Caused by:)", stripped
        ):
            in_trace = True
            current_trace = [stripped]
        elif in_trace:
            if stripped.startswith("at ") or stripped.startswith("Caused by:") or stripped.startswith("..."):
                current_trace.append(stripped)
            else:
                if len(current_trace) > 1:
                    traces.append("\n".join(current_trace))
                current_trace = []
                in_trace = False
                # Check if this line starts a new trace
                if re.match(
                    r"^([\w$.]+Exception|[\w$.]+Error|Caused by:)", stripped
                ):
                    in_trace = True
                    current_trace = [stripped]

    if current_trace and len(current_trace) > 1:
        traces.append("\n".join(current_trace))

    return traces


def extract_error_lines(text: str) -> list[str]:
    """Extract lines containing ERROR, WARN, Exception, or notable messages."""
    errors = []
    for line in text.split("\n"):
        stripped = line.strip()
        if not stripped:
            continue
        if any(
            kw in stripped
            for kw in ["ERROR", "SEVERE", "Exception", "Error:", "FATAL", "WARN", "failed", "Failed"]
        ):
            # Skip stack trace continuation lines (handled separately)
            if not stripped.startswith("at ") and not stripped.startswith("..."):
                errors.append(stripped)
    return errors


def format_output(data: dict) -> str:
    """Format parsed email data into readable sections."""
    sections = []

    # Metadata
    sections.append("## Metadata")
    sections.append(f"- **Version:** {data.get('softwareVersion', 'unknown')}")
    sections.append(f"- **Platform:** {data.get('platform', 'unknown')}")
    sections.append("")

    exception_msg = data.get("exceptionMessage", "")
    stack_trace = data.get("stackTrace", "")

    # Error/warning lines from the log
    error_lines = extract_error_lines(exception_msg)
    if error_lines:
        sections.append("## Error and Warning Lines")
        # Deduplicate while preserving order
        seen = set()
        for line in error_lines:
            if line not in seen:
                seen.add(line)
                sections.append(f"- {line}")
        sections.append("")

    # Stack traces from exceptionMessage (client log)
    log_traces = extract_stack_traces(exception_msg)

    # Stack traces from stackTrace field
    st_traces = extract_stack_traces(stack_trace)

    all_traces = log_traces + st_traces

    if all_traces:
        sections.append("## Stack Traces")
        # Deduplicate traces
        seen_traces = set()
        trace_num = 0
        for trace in all_traces:
            if trace not in seen_traces:
                seen_traces.add(trace)
                trace_num += 1
                first_line = trace.split("\n")[0]
                sections.append(f"### Trace {trace_num}: {first_line}")
                sections.append("```")
                sections.append(trace)
                sections.append("```")
                sections.append("")

    # Full log (truncated to last 100 lines for context)
    if exception_msg:
        log_lines = [l for l in exception_msg.split("\n") if l.strip()]
        sections.append(f"## Log Tail (last 100 of {len(log_lines)} lines)")
        sections.append("```")
        for line in log_lines[-100:]:
            sections.append(line)
        sections.append("```")

    return "\n".join(sections)


def main():
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} <file.eml|file.json>", file=sys.stderr)
        sys.exit(1)

    path = sys.argv[1]
    if not Path(path).exists():
        print(f"File not found: {path}", file=sys.stderr)
        sys.exit(1)

    data = parse_file(path)
    print(format_output(data))


if __name__ == "__main__":
    main()
