#!/usr/bin/env python3
"""
Parse VCell support email (.eml or .json) and extract structured error info.

Single-file mode (human-readable digest):
    python tools/parse_support_email.py <file.eml|file.json>

Batch mode (CSV index over a directory):
    python tools/parse_support_email.py --batch <dir> \
        [--out-emails emails.csv] [--out-traces traces.csv]

emails.csv: one row per email (per-email metadata + signature list)
traces.csv: one row per (email, stack-trace) — for cross-pivoting and clustering
"""

import argparse
import csv
import email
import email.message
import email.utils
import hashlib
import json
import re
import sys
from datetime import datetime, timezone
from pathlib import Path


def decode_eml(path: str) -> tuple[email.message.Message, str]:
    """Return (parsed message, plain-text body)."""
    with open(path, "rb") as f:
        msg = email.message_from_bytes(f.read())
    body = ""
    if msg.is_multipart():
        for part in msg.walk():
            if part.get_content_type() == "text/plain":
                body = part.get_payload(decode=True).decode("utf-8", errors="replace")
                break
    else:
        body = msg.get_payload(decode=True).decode("utf-8", errors="replace")
    return msg, body


def extract_json_from_body(body: str) -> dict:
    """Find and parse the JSON object embedded in the email body.

    Walks the body tracking string-literal state so that '{' / '}' inside
    JSON string values (e.g. log content with literal braces) don't throw
    off the brace counter.
    """
    idx = body.find("{")
    if idx == -1:
        raise ValueError("No JSON object found in email body")
    depth = 0
    in_str = False
    escape = False
    for i in range(idx, len(body)):
        c = body[i]
        if in_str:
            if escape:
                escape = False
            elif c == "\\":
                escape = True
            elif c == '"':
                in_str = False
            continue
        if c == '"':
            in_str = True
        elif c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                return json.loads(body[idx : i + 1])
    raise ValueError("Unbalanced braces in JSON payload")


def parse_file(path: str) -> dict:
    """Parse a .eml or .json support email file into the inner JSON dict."""
    p = Path(path)
    if p.suffix == ".json":
        with open(p) as f:
            return json.load(f)
    elif p.suffix == ".eml":
        _, body = decode_eml(path)
        return extract_json_from_body(body)
    else:
        raise ValueError(f"Unsupported file type: {p.suffix}")


# ---------- stack-trace extraction & signatures ----------

EXCEPTION_START = re.compile(r"^([\w$.]+(Exception|Error))(:|$)")
CAUSED_BY = re.compile(r"^Caused by:\s*([\w$.]+(Exception|Error))")


def extract_stack_traces(text: str) -> list[str]:
    """Extract Java stack traces from a block of text.

    A trace starts with an exception-class line and is followed by
    'at ' / 'Caused by:' / '...' continuation lines.
    """
    traces: list[str] = []
    current: list[str] = []
    in_trace = False
    for raw in text.split("\n"):
        line = raw.strip()
        if not in_trace:
            if EXCEPTION_START.match(line) or line.startswith("Caused by:"):
                in_trace = True
                current = [line]
            continue
        if (
            line.startswith("at ")
            or line.startswith("Caused by:")
            or line.startswith("...")
        ):
            current.append(line)
        else:
            if len(current) > 1:
                traces.append("\n".join(current))
            current = []
            in_trace = False
            if EXCEPTION_START.match(line):
                in_trace = True
                current = [line]
    if len(current) > 1:
        traces.append("\n".join(current))
    return traces


def normalize_frame(frame: str) -> str:
    """Strip line numbers and synthetic lambda/$$ suffixes for stable signatures."""
    f = frame.strip()
    # at com.foo.Bar.method(Bar.java:123) -> at com.foo.Bar.method(Bar.java)
    f = re.sub(r"\(([^)]*?):\d+\)", r"(\1)", f)
    # lambda$method$0 -> lambda$method
    f = re.sub(r"lambda\$(\w+)\$\d+", r"lambda$\1", f)
    # synthetic accessors like access$000
    f = re.sub(r"access\$\d+", "access", f)
    return f


def trace_top_exception(trace: str) -> str:
    """Best-effort: classify by the *innermost* Caused-by, else the outer class."""
    chosen = None
    for line in trace.split("\n"):
        s = line.strip()
        m = CAUSED_BY.match(s)
        if m:
            chosen = m.group(1)
            continue
        if chosen is None:
            m = EXCEPTION_START.match(s)
            if m:
                chosen = m.group(1)
    return chosen or "UnknownException"


def trace_signature(trace: str, top_n: int = 5) -> tuple[str, str, list[str]]:
    """Return (sig_hash, exception_class, top_frames).

    Signature inputs: innermost exception class + first N normalized 'at' frames.
    """
    exc = trace_top_exception(trace)
    frames = all_frames(trace)[:top_n]
    sig_input = exc + "\n" + "\n".join(frames)
    sig = hashlib.sha1(sig_input.encode()).hexdigest()[:12]
    return sig, exc, frames


def all_frames(trace: str) -> list[str]:
    """Return all 'at <frame>' lines from a trace, normalized."""
    out = []
    for line in trace.split("\n"):
        s = line.strip()
        if s.startswith("at "):
            out.append(normalize_frame(s))
    return out


# VCell-owned package prefixes used to identify "our code" in a stack frame.
VCELL_PACKAGE_PREFIXES = (
    "at cbit.",
    "at org.vcell.",
    "at org.jlibsedml.",
    "at vcell.",
)


def innermost_vcell_frame(frames: list[str]) -> str | None:
    """First frame from the top that's in VCell-owned code; None if none."""
    for f in frames:
        if any(f.startswith(p) for p in VCELL_PACKAGE_PREFIXES):
            return f
    return None


# Frame format after normalization: "at <pkg>.<Class>[$Inner].<method>(<File.java>)"
# Also possible: "(Native Method)", "(Unknown Source)", "(SourceFile)"
FRAME_RE = re.compile(r"^at\s+(\S+?)\(([^)]*)\)$")


def parse_frame(frame: str) -> dict:
    """Return {class_fqn, simple_class, method, file} or {} if unparseable."""
    m = FRAME_RE.match(frame)
    if not m:
        return {}
    qualified, file_part = m.group(1), m.group(2)
    if "." not in qualified:
        return {}
    class_fqn, method = qualified.rsplit(".", 1)
    simple_class = class_fqn.rsplit(".", 1)[-1].split("$")[0]
    return {
        "class_fqn": class_fqn,
        "simple_class": simple_class,
        "method": method,
        "file": file_part,
    }


def short_signature(exc: str, frames: list[str]) -> str:
    """12-char hash of (exception class + the given frame list)."""
    sig_input = exc + "\n" + "\n".join(frames)
    return hashlib.sha1(sig_input.encode()).hexdigest()[:12]


# ---------- per-email metadata extraction ----------

INNER_SENT_RE = re.compile(
    r"^Sent:\s*(.+?)$", re.MULTILINE
)
PLATFORM_JAVA_RE = re.compile(r"Java\s+([0-9._+]+)")
PLATFORM_ARCH_RE = re.compile(r"on the (\w+) architecture")
PLATFORM_OS_RE = re.compile(r"running version (.+)$")


def parse_inner_sent_date(body: str) -> str | None:
    """Pull the inner 'Sent: ...' date string from the forwarded body and
    return it as ISO-8601, best-effort.

    Example body line:
        Sent: Saturday, April 25, 2026 12:14:36 AM (UTC-05:00) Eastern Time (US & Canada)
    """
    m = INNER_SENT_RE.search(body)
    if not m:
        return None
    raw = m.group(1).strip()
    # Drop everything from the first '(' onward (covers both UTC offset and tz name parens)
    cleaned = raw.split("(", 1)[0].strip()
    # Pad single-digit hour: "8:17:09 AM" -> "08:17:09 AM"
    cleaned = re.sub(r"(\b\d{1,2}),\s+(\d{4})\s+(\d):", r"\1, \2 0\3:", cleaned)
    fmts = [
        "%A, %B %d, %Y %I:%M:%S %p",
        "%A, %B %d, %Y %I:%M %p",
        "%B %d, %Y %I:%M:%S %p",
    ]
    for fmt in fmts:
        try:
            dt = datetime.strptime(cleaned, fmt)
            return dt.isoformat()
        except ValueError:
            continue
    return raw  # fallback: keep raw so it's at least visible


def parse_outer_date(msg: email.message.Message) -> str | None:
    raw = msg.get("Date")
    if not raw:
        return None
    try:
        dt = email.utils.parsedate_to_datetime(raw)
        if dt.tzinfo is None:
            dt = dt.replace(tzinfo=timezone.utc)
        return dt.astimezone(timezone.utc).isoformat()
    except (TypeError, ValueError):
        return raw


def parse_platform(platform: str) -> tuple[str, str, str]:
    """Return (java_version, arch, os_version) best-effort from the platform string."""
    java_v = (PLATFORM_JAVA_RE.search(platform).group(1)
              if PLATFORM_JAVA_RE.search(platform) else "")
    arch = (PLATFORM_ARCH_RE.search(platform).group(1)
            if PLATFORM_ARCH_RE.search(platform) else "")
    os_v = (PLATFORM_OS_RE.search(platform).group(1).strip()
            if PLATFORM_OS_RE.search(platform) else "")
    return java_v, arch, os_v


def sha1_prefix(text: str, n: int) -> str:
    return hashlib.sha1(text[:n].encode("utf-8", errors="replace")).hexdigest()[:12]


def extract_email_record(path: Path) -> tuple[dict, list[dict], str]:
    """Return (email_record, list_of_trace_records, parse_error).

    On parse error, email_record has minimal fields and parse_error is non-empty.
    """
    base = {
        "file": path.name,
        "forwarded_date_iso": "",
        "submitted_date_iso": "",
        "software_version": "",
        "platform": "",
        "java_version": "",
        "arch": "",
        "os_version": "",
        "log_chars": 0,
        "log_lines": 0,
        "trace_count": 0,
        "distinct_trace_count": 0,
        "trace_signatures": "",
        "exception_classes": "",
        "log_prefix_sha1_1k": "",
        "log_prefix_sha1_10k": "",
        "log_prefix_sha1_100k": "",
        "parse_error": "",
    }
    trace_rows: list[dict] = []

    try:
        if path.suffix == ".eml":
            msg, body = decode_eml(str(path))
            base["forwarded_date_iso"] = parse_outer_date(msg) or ""
            base["submitted_date_iso"] = parse_inner_sent_date(body) or ""
            data = extract_json_from_body(body)
        else:
            with open(path) as f:
                data = json.load(f)
    except Exception as e:
        base["parse_error"] = f"{type(e).__name__}: {e}"
        return base, trace_rows, base["parse_error"]

    base["software_version"] = data.get("softwareVersion", "")
    platform = data.get("platform", "")
    base["platform"] = platform
    java_v, arch, os_v = parse_platform(platform)
    base["java_version"] = java_v
    base["arch"] = arch
    base["os_version"] = os_v

    log = data.get("exceptionMessage", "") or ""
    base["log_chars"] = len(log)
    base["log_lines"] = log.count("\n") + (1 if log else 0)
    base["log_prefix_sha1_1k"] = sha1_prefix(log, 1024)
    base["log_prefix_sha1_10k"] = sha1_prefix(log, 10 * 1024)
    base["log_prefix_sha1_100k"] = sha1_prefix(log, 100 * 1024)

    traces = extract_stack_traces(log)
    seen_sigs: list[str] = []
    distinct_classes: list[str] = []
    for idx, trace in enumerate(traces):
        exc = trace_top_exception(trace)
        frames = all_frames(trace)
        sig = short_signature(exc, frames[:5])
        sig_top1 = short_signature(exc, frames[:1])
        sig_top2 = short_signature(exc, frames[:2])
        innermost = innermost_vcell_frame(frames)
        sig_innermost_vcell = (
            short_signature(exc, [innermost]) if innermost else "no-vcell-frame"
        )
        innermost_parts = parse_frame(innermost) if innermost else {}
        trace_lines = trace.split("\n")
        caused_by_count = sum(1 for ln in trace_lines if ln.strip().startswith("Caused by:"))
        trace_rows.append({
            "file": path.name,
            "trace_index": idx,
            "incident_id": "",  # filled in by corpus-level pass
            "is_first_in_email": idx == 0,
            "signature": sig,
            "sig_top1": sig_top1,
            "sig_top2": sig_top2,
            "sig_innermost_vcell": sig_innermost_vcell,
            "innermost_vcell_frame": innermost or "",
            "innermost_vcell_file": innermost_parts.get("file", ""),
            "innermost_vcell_class": innermost_parts.get("simple_class", ""),
            "innermost_vcell_method": innermost_parts.get("method", ""),
            "exception_class": exc,
            "frame_count": len(frames),
            "caused_by_count": caused_by_count,
            "top_frames": "\n".join(frames[:5]),
            "full_trace": trace,
        })
        if sig not in seen_sigs:
            seen_sigs.append(sig)
        if exc not in distinct_classes:
            distinct_classes.append(exc)

    base["trace_count"] = len(traces)
    base["distinct_trace_count"] = len(seen_sigs)
    base["trace_signatures"] = ";".join(seen_sigs)
    base["exception_classes"] = ";".join(distinct_classes)
    return base, trace_rows, ""


# ---------- formatters ----------

def format_output(data: dict) -> str:
    """Format parsed email data into a readable single-file digest."""
    sections = ["## Metadata",
                f"- **Version:** {data.get('softwareVersion', 'unknown')}",
                f"- **Platform:** {data.get('platform', 'unknown')}",
                ""]

    log = data.get("exceptionMessage", "") or ""
    stack = data.get("stackTrace", "") or ""

    error_lines = []
    for line in log.split("\n"):
        s = line.strip()
        if not s:
            continue
        if any(kw in s for kw in ["ERROR", "SEVERE", "Exception", "Error:", "FATAL", "WARN", "failed", "Failed"]):
            if not s.startswith("at ") and not s.startswith("..."):
                error_lines.append(s)

    if error_lines:
        sections.append("## Error and Warning Lines")
        seen = set()
        for line in error_lines:
            if line not in seen:
                seen.add(line)
                sections.append(f"- {line}")
        sections.append("")

    all_traces = extract_stack_traces(log) + extract_stack_traces(stack)
    if all_traces:
        sections.append("## Stack Traces")
        seen_traces: set[str] = set()
        n = 0
        for trace in all_traces:
            if trace in seen_traces:
                continue
            seen_traces.add(trace)
            n += 1
            sig, exc, frames = trace_signature(trace)
            first = trace.split("\n")[0]
            sections.append(f"### Trace {n} [{sig}]: {first}")
            sections.append("```")
            sections.append(trace)
            sections.append("```")
            sections.append("")

    if log:
        log_lines = [l for l in log.split("\n") if l.strip()]
        sections.append(f"## Log Tail (last 100 of {len(log_lines)} lines)")
        sections.append("```")
        sections.extend(log_lines[-100:])
        sections.append("```")

    return "\n".join(sections)


# ---------- CLI ----------

EMAIL_FIELDS = [
    "file", "incident_id",
    "forwarded_date_iso", "submitted_date_iso",
    "software_version", "platform", "java_version", "arch", "os_version",
    "log_chars", "log_lines",
    "trace_count", "distinct_trace_count",
    "trace_signatures", "exception_classes",
    "log_prefix_sha1_1k", "log_prefix_sha1_10k", "log_prefix_sha1_100k",
    "parse_error",
]
TRACE_FIELDS = [
    "file", "trace_index", "incident_id", "is_first_in_email",
    "signature", "sig_top1", "sig_top2", "sig_innermost_vcell",
    "innermost_vcell_frame",
    "innermost_vcell_file", "innermost_vcell_class", "innermost_vcell_method",
    "exception_class",
    "frame_count", "caused_by_count", "top_frames", "full_trace",
]
PIVOT_FIELDS = [
    "sig_innermost_vcell", "exception_class", "innermost_vcell_frame",
    "incidents_total", "emails_total", "traces_total",
    "first_incident_date", "last_incident_date",
    "versions_present", "sample_file",
    # version columns appended dynamically
]


def assign_incident_ids(email_rows: list[dict], trace_rows: list[dict]) -> None:
    """Group emails by log_prefix_sha1_1k → assign 'inc_001' style ids in order
    of earliest submission date, then propagate to traces."""
    # Group emails by prefix; emails without a prefix (parse failures) each get their own id.
    groups: dict[str, list[dict]] = {}
    standalone_counter = 0
    for r in email_rows:
        key = r.get("log_prefix_sha1_1k") or f"_solo_{standalone_counter}"
        if not r.get("log_prefix_sha1_1k"):
            standalone_counter += 1
        groups.setdefault(key, []).append(r)

    def group_sort_key(g: list[dict]) -> str:
        dates = [r.get("submitted_date_iso") or r.get("forwarded_date_iso") or "" for r in g]
        return min(d for d in dates if d) if any(dates) else ""

    ordered = sorted(groups.values(), key=group_sort_key)
    file_to_incident: dict[str, str] = {}
    for i, group in enumerate(ordered, start=1):
        inc_id = f"inc_{i:03d}"
        for r in group:
            r["incident_id"] = inc_id
            file_to_incident[r["file"]] = inc_id
    for t in trace_rows:
        t["incident_id"] = file_to_incident.get(t["file"], "")


def compute_version_pivot(email_rows: list[dict], trace_rows: list[dict]) -> tuple[list[dict], list[str]]:
    """One row per sig_innermost_vcell cluster, with incident counts per version."""
    file_to_email = {r["file"]: r for r in email_rows}
    versions = sorted({r["software_version"] for r in email_rows if r["software_version"]})

    # cluster_key -> dict of stats
    clusters: dict[str, dict] = {}
    for t in trace_rows:
        key = t["sig_innermost_vcell"]
        c = clusters.setdefault(key, {
            "sig_innermost_vcell": key,
            "exception_class": t["exception_class"],
            "innermost_vcell_frame": t["innermost_vcell_frame"],
            "incidents": set(),
            "emails": set(),
            "traces": 0,
            "dates": [],
            "versions": set(),
            "sample_file": t["file"],
        })
        c["traces"] += 1
        c["emails"].add(t["file"])
        if t.get("incident_id"):
            c["incidents"].add(t["incident_id"])
        em = file_to_email.get(t["file"])
        if em:
            if em.get("software_version"):
                c["versions"].add(em["software_version"])
            d = em.get("submitted_date_iso") or em.get("forwarded_date_iso")
            if d:
                c["dates"].append(d)

    rows: list[dict] = []
    version_cols = [f"v_{v}" for v in versions]
    for c in clusters.values():
        row = {
            "sig_innermost_vcell": c["sig_innermost_vcell"],
            "exception_class": c["exception_class"],
            "innermost_vcell_frame": c["innermost_vcell_frame"],
            "incidents_total": len(c["incidents"]),
            "emails_total": len(c["emails"]),
            "traces_total": c["traces"],
            "first_incident_date": min(c["dates"])[:10] if c["dates"] else "",
            "last_incident_date": max(c["dates"])[:10] if c["dates"] else "",
            "versions_present": ";".join(sorted(c["versions"])),
            "sample_file": c["sample_file"],
        }
        # per-version incident counts
        per_version_incidents: dict[str, set[str]] = {v: set() for v in versions}
        for t in trace_rows:
            if t["sig_innermost_vcell"] != c["sig_innermost_vcell"]:
                continue
            em = file_to_email.get(t["file"])
            if em and em.get("software_version") and t.get("incident_id"):
                per_version_incidents[em["software_version"]].add(t["incident_id"])
        for v in versions:
            row[f"v_{v}"] = len(per_version_incidents[v])
        rows.append(row)

    rows.sort(key=lambda r: (-r["incidents_total"], r["sig_innermost_vcell"]))
    return rows, version_cols


def find_repo_root(start: Path) -> Path:
    """Walk up from `start` looking for a directory containing 'vcell-core'."""
    p = start.resolve()
    for cand in [p, *p.parents]:
        if (cand / "vcell-core").is_dir():
            return cand
    return Path.cwd()


def index_java_sources(repo_root: Path) -> dict[str, list[Path]]:
    """Index all <Class>.java files under vcell-*/src/main/java by simple class name."""
    index: dict[str, list[Path]] = {}
    for module in repo_root.glob("vcell-*"):
        java_root = module / "src" / "main" / "java"
        if not java_root.is_dir():
            continue
        for p in java_root.rglob("*.java"):
            index.setdefault(p.stem, []).append(p)
    return index


def check_method_in_files(method: str, simple_class: str, files: list[Path]) -> str:
    """Return 'present' if any file contains a token matching `method` (or its
    constructor / lambda parent equivalent); 'method_missing' otherwise."""
    if method == "<init>":
        # Constructor: look for the simple class name as a method-style token
        pattern = re.compile(rf"\b{re.escape(simple_class)}\s*\(")
    elif method.startswith("lambda$"):
        # Synthetic lambda: look for the parent method name
        base = method[len("lambda$"):]
        pattern = re.compile(rf"\b{re.escape(base)}\s*\(")
    else:
        pattern = re.compile(rf"\b{re.escape(method)}\s*\(")
    for f in files:
        try:
            text = f.read_text(errors="replace")
        except OSError:
            continue
        if pattern.search(text):
            return "present"
    return "method_missing"


def verify_code_presence(trace_rows: list[dict], repo_root: Path) -> dict[tuple[str, str], dict]:
    """For each unique (simple_class, method) appearing as innermost-VCell frame,
    return {('Class', 'method'): {status, file_paths}}."""
    pairs: dict[tuple[str, str], None] = {}
    for t in trace_rows:
        cls = t.get("innermost_vcell_class")
        meth = t.get("innermost_vcell_method")
        if cls and meth:
            pairs[(cls, meth)] = None
    java_index = index_java_sources(repo_root)
    out: dict[tuple[str, str], dict] = {}
    for cls, meth in pairs:
        files = java_index.get(cls, [])
        if not files:
            out[(cls, meth)] = {"status": "file_missing", "files": []}
            continue
        status = check_method_in_files(meth, cls, files)
        out[(cls, meth)] = {
            "status": status,
            "files": [str(f.relative_to(repo_root)) for f in files],
        }
    return out


def compute_file_pivot(email_rows: list[dict], trace_rows: list[dict],
                       presence: dict[tuple[str, str], dict]) -> tuple[list[dict], list[str]]:
    """One row per innermost_vcell_file, with code-presence status and incident counts."""
    file_to_email = {r["file"]: r for r in email_rows}
    versions = sorted({r["software_version"] for r in email_rows if r["software_version"]})

    by_file: dict[str, dict] = {}
    for t in trace_rows:
        f = t.get("innermost_vcell_file") or "(no-vcell-frame)"
        cls = t.get("innermost_vcell_class")
        meth = t.get("innermost_vcell_method")
        c = by_file.setdefault(f, {
            "innermost_vcell_file": f,
            "simple_class": cls or "",
            "methods": set(),
            "method_statuses": {},
            "exception_classes": set(),
            "signatures": set(),
            "incidents": set(),
            "emails": set(),
            "traces": 0,
            "dates": [],
            "versions": set(),
            "source_paths": set(),
            "sample_email_file": t["file"],
        })
        c["traces"] += 1
        c["emails"].add(t["file"])
        if t.get("incident_id"):
            c["incidents"].add(t["incident_id"])
        c["exception_classes"].add(t["exception_class"])
        c["signatures"].add(t["sig_innermost_vcell"])
        if meth:
            c["methods"].add(meth)
            stat = presence.get((cls, meth), {})
            if stat:
                c["method_statuses"][meth] = stat["status"]
                for sp in stat.get("files", []):
                    c["source_paths"].add(sp)
        em = file_to_email.get(t["file"])
        if em:
            if em.get("software_version"):
                c["versions"].add(em["software_version"])
            d = em.get("submitted_date_iso") or em.get("forwarded_date_iso")
            if d:
                c["dates"].append(d)

    rows: list[dict] = []
    for c in by_file.values():
        statuses = set(c["method_statuses"].values())
        if c["innermost_vcell_file"] == "(no-vcell-frame)":
            file_status = "no_vcell_frame"
        elif not c["source_paths"]:
            file_status = "file_missing"
        elif "method_missing" in statuses and "present" not in statuses:
            file_status = "method_missing"
        elif "method_missing" in statuses and "present" in statuses:
            file_status = "partial_present"
        else:
            file_status = "present"
        present_methods = [m for m, s in c["method_statuses"].items() if s == "present"]
        missing_methods = [m for m, s in c["method_statuses"].items() if s == "method_missing"]
        per_v_inc = {v: set() for v in versions}
        for t in trace_rows:
            if (t.get("innermost_vcell_file") or "(no-vcell-frame)") != c["innermost_vcell_file"]:
                continue
            em = file_to_email.get(t["file"])
            if em and em.get("software_version") and t.get("incident_id"):
                per_v_inc[em["software_version"]].add(t["incident_id"])
        row = {
            "innermost_vcell_file": c["innermost_vcell_file"],
            "simple_class": c["simple_class"],
            "code_status": file_status,
            "source_paths": ";".join(sorted(c["source_paths"])),
            "distinct_methods": len(c["methods"]),
            "methods_present": ";".join(sorted(present_methods)),
            "methods_missing": ";".join(sorted(missing_methods)),
            "exception_classes": ";".join(sorted(c["exception_classes"])),
            "distinct_signatures": len(c["signatures"]),
            "incidents_total": len(c["incidents"]),
            "emails_total": len(c["emails"]),
            "traces_total": c["traces"],
            "first_seen_date": min(c["dates"])[:10] if c["dates"] else "",
            "last_seen_date": max(c["dates"])[:10] if c["dates"] else "",
            "versions_seen": ";".join(sorted(c["versions"])),
            "sample_email_file": c["sample_email_file"],
        }
        for v in versions:
            row[f"v_{v}"] = len(per_v_inc[v])
        rows.append(row)

    # Primary sort: code_status (present first), then incidents desc.
    status_order = {"present": 0, "partial_present": 1, "method_missing": 2,
                    "file_missing": 3, "no_vcell_frame": 4}
    rows.sort(key=lambda r: (status_order.get(r["code_status"], 9), -r["incidents_total"]))
    return rows, [f"v_{v}" for v in versions]


def run_batch(dir_path: Path, out_emails: Path, out_traces: Path,
              out_version_pivot: Path, out_file_pivot: Path) -> None:
    files = sorted(p for p in dir_path.iterdir() if p.suffix in (".eml", ".json"))
    if not files:
        print(f"No .eml/.json files in {dir_path}", file=sys.stderr)
        sys.exit(1)
    email_rows: list[dict] = []
    trace_rows: list[dict] = []
    failures = 0
    for path in files:
        rec, traces, err = extract_email_record(path)
        email_rows.append(rec)
        trace_rows.extend(traces)
        if err:
            failures += 1
            print(f"[parse-fail] {path.name}: {err}", file=sys.stderr)

    # corpus-level enrichments
    assign_incident_ids(email_rows, trace_rows)
    repo_root = find_repo_root(Path(__file__).parent)
    presence = verify_code_presence(trace_rows, repo_root)
    file_pivot_rows, file_version_cols = compute_file_pivot(email_rows, trace_rows, presence)
    version_pivot_rows, version_cols = compute_version_pivot(email_rows, trace_rows)

    with open(out_emails, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=EMAIL_FIELDS)
        w.writeheader()
        w.writerows(email_rows)
    with open(out_traces, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=TRACE_FIELDS)
        w.writeheader()
        w.writerows(trace_rows)
    file_pivot_fields = [
        "innermost_vcell_file", "simple_class", "code_status", "source_paths",
        "distinct_methods", "methods_present", "methods_missing",
        "exception_classes", "distinct_signatures",
        "incidents_total", "emails_total", "traces_total",
        "first_seen_date", "last_seen_date", "versions_seen", "sample_email_file",
    ] + file_version_cols
    with open(out_file_pivot, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=file_pivot_fields)
        w.writeheader()
        w.writerows(file_pivot_rows)
    with open(out_version_pivot, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=PIVOT_FIELDS + version_cols)
        w.writeheader()
        w.writerows(version_pivot_rows)

    versions = sorted({r["software_version"] for r in email_rows if r["software_version"]})
    incidents = sorted({r["incident_id"] for r in email_rows if r["incident_id"]})
    print(f"Processed {len(email_rows)} emails ({failures} parse failures)")
    print(f"  Repo root for code-presence check: {repo_root}")
    print(f"  Distinct incidents (after resubmission collapse): {len(incidents)}")
    print(f"  Distinct innermost-VCell files:   {len({t.get('innermost_vcell_file') or '(no-vcell-frame)' for t in trace_rows})}")
    print(f"  Distinct innermost-VCell sigs:    {len({t['sig_innermost_vcell'] for t in trace_rows})}")
    print(f"  Distinct top-5 signatures:        {len({t['signature'] for t in trace_rows})}")
    print(f"  Distinct software versions:       {len(versions)}")
    for v in versions:
        n_emails = sum(1 for r in email_rows if r["software_version"] == v)
        n_inc = len({r["incident_id"] for r in email_rows if r["software_version"] == v and r["incident_id"]})
        print(f"    {v}: {n_emails} emails, {n_inc} incidents")

    status_counts: dict[str, int] = {}
    for r in file_pivot_rows:
        status_counts[r["code_status"]] = status_counts.get(r["code_status"], 0) + 1
    print(f"\n  File-level code presence:")
    for s in ["present", "partial_present", "method_missing", "file_missing", "no_vcell_frame"]:
        if s in status_counts:
            print(f"    {s:<18}{status_counts[s]:>3} files")

    print(f"\n  Top files by incidents (PRESENT in current code only):")
    shown = 0
    for r in file_pivot_rows:
        if r["code_status"] not in ("present", "partial_present"):
            continue
        if shown >= 15:
            break
        shown += 1
        excs = r["exception_classes"][:60]
        per_v = " ".join(f"{v.split('_')[-1]}={r[f'v_{v}']}" for v in versions if r[f'v_{v}'] > 0)
        print(f"    inc={r['incidents_total']:>3}  {r['innermost_vcell_file']:<48}  [{per_v}]")
        print(f"          methods={r['distinct_methods']}  status={r['code_status']}  excs={excs}")

    if status_counts.get("file_missing") or status_counts.get("method_missing"):
        print(f"\n  Files NOT confirmed in current code (probably refactored or stale):")
        for r in file_pivot_rows:
            if r["code_status"] in ("file_missing", "method_missing"):
                print(f"    inc={r['incidents_total']:>3}  {r['innermost_vcell_file']:<48}  status={r['code_status']}")

    print(f"\nWrote:\n  {out_emails}\n  {out_traces}\n  {out_file_pivot}  (primary view)\n  {out_version_pivot}  (secondary)")


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("path", help="single .eml/.json file, or a directory in --batch mode")
    ap.add_argument("--batch", action="store_true", help="treat path as a directory and emit CSVs")
    ap.add_argument("--out-emails", default="emails.csv")
    ap.add_argument("--out-traces", default="traces.csv")
    ap.add_argument("--out-file-pivot", default="file_pivot.csv")
    ap.add_argument("--out-version-pivot", default="version_pivot.csv")
    args = ap.parse_args()

    p = Path(args.path)
    if not p.exists():
        print(f"Path not found: {p}", file=sys.stderr)
        sys.exit(1)

    if args.batch:
        if not p.is_dir():
            print(f"--batch requires a directory; got {p}", file=sys.stderr)
            sys.exit(1)
        run_batch(
            p,
            Path(args.out_emails),
            Path(args.out_traces),
            Path(args.out_version_pivot),
            Path(args.out_file_pivot),
        )
        return

    data = parse_file(str(p))
    print(format_output(data))


if __name__ == "__main__":
    main()
