# VCell Support-Email Log Processing

Bulk-triage VCell user error reports submitted via the Contact-Us form. Each
report arrives as a forwarded `.eml` carrying an embedded JSON payload with
the client log and runtime metadata. The pipeline parses a directory of such
emails into structured CSVs that can be opened in a spreadsheet for
clustering and root-cause review.

## Input format

- All inputs live in `docs/support_emails/` as `.eml` files.
- Each `.eml` is single-part `text/plain`, forwarded by
  `vcell_support@uchc.edu`. The body is mostly an envelope; the actual
  payload is a single JSON object embedded after the forwarded-message
  separator.
- JSON keys:
  - `exceptionMessage` — the full client log as one string (possibly with
    embedded stack traces).
  - `stackTrace` — a wrapping `RuntimeException` whose message *is* the log.
    Ignored for trace extraction (would just produce duplicates).
  - `softwareVersion`, `platform`.

## Output

```
docs/support_emails/
├── emails.csv           one row per email
├── traces.csv           one row per (email, stack-trace)
├── file_pivot.csv       PRIMARY: one row per innermost-VCell source file
└── version_pivot.csv    secondary: one row per innermost-VCell signature
```

Open `file_pivot.csv` first.

## Usage

```bash
# Run from repo root
python3 tools/parse_support_email.py --batch docs/support_emails \
  --out-emails        docs/support_emails/emails.csv \
  --out-traces        docs/support_emails/traces.csv \
  --out-file-pivot    docs/support_emails/file_pivot.csv \
  --out-version-pivot docs/support_emails/version_pivot.csv

# Single-file human-readable digest
python3 tools/parse_support_email.py docs/support_emails/<file>.eml
```

To add new emails: drop them in `docs/support_emails/` and re-run. The script
also performs a code-presence check against the *current working tree*, so
the same corpus can give different answers as the codebase evolves.

## Pipeline

```
.eml ─▶ extract embedded JSON
     ─▶ parse stack traces from exceptionMessage
     ─▶ assign incident_id (resubmission collapse via log prefix hash)
     ─▶ compute four signatures per trace (top-5, top-2, top-1, innermost-VCell)
     ─▶ verify each cluster's code-presence in the live codebase
     ─▶ emit CSVs
```

## Design decisions

### Brace-balanced JSON extraction with string-literal awareness
The naïve "find `{` then balance `}`" approach fails when log content
contains literal braces. The extractor tracks string state so braces inside
JSON string values don't throw off the counter.

### Multiple signatures per trace, not one
Each trace gets four cluster keys at different granularities, all stored:
- `signature` — innermost exception class + top 5 normalized frames
- `sig_top2`, `sig_top1` — same with fewer frames
- `sig_innermost_vcell` — exception class + the innermost frame inside
  `cbit.*` / `org.vcell.*` / `org.jlibsedml.*`

Top-5 is too strict — synthetic lambda numbering and AWT dispatch chains
differ across submissions. Innermost-VCell is the most useful headline
because it ignores framework noise and zeroes in on our code. Storing all
four lets you re-cluster without re-parsing.

### Classify by innermost `Caused by:`
A single trace can chain exception types. We always use the innermost class
as `exception_class`, so a `RuntimeException` wrapping an `NPE` is grouped
with other NPEs.

### Frame normalization
Line numbers, `lambda$method$<digits>`, and synthetic accessors are stripped
before hashing — small refactors that shift a method don't fragment a
cluster.

### Resubmission collapse → incidents
Users hit "Send" repeatedly; the client log accumulates over a session, so
sequential reports from the same user share a long log prefix. We hash the
first 1024 chars of `exceptionMessage` (`log_prefix_sha1_1k`) and group
emails sharing the hash into one `incident_id`. In the current corpus this
collapses 75 emails to 33 incidents (one user's session generated 24
emails).

**Always rank by `incidents_total`, not `emails_total`.**

### Code-presence verification (the headline)
For each `(simple_class, method)` pair seen as an innermost VCell frame:
1. Look up `<Class>.java` under `vcell-*/src/main/java`.
2. Check the file for the method name as a token (constructors → class-name
   token; `lambda$X` → look for parent method `X`).
3. Tag as `present`, `partial_present`, `method_missing`, or
   `file_missing`.

This is binary truth about the current state of the code. It replaces
version-diff inference, which doesn't work at this sample size (see L1
below).

### Separation of parsing and corpus-level analysis
All enrichment (incident_id, code presence, pivots) runs as a second pass
after every email is parsed, in the same script. Keeps the command simple
and ensures incident IDs are stable across a single invocation.

## Schemas

### emails.csv
| column | meaning |
|---|---|
| `file` | source filename |
| `incident_id` | `inc_NNN`, shared across resubmissions |
| `forwarded_date_iso` | outer email Date header, UTC |
| `submitted_date_iso` | inner "Sent:" date from forwarded body, best-effort |
| `software_version`, `platform` | from JSON |
| `java_version`, `arch`, `os_version` | parsed from `platform` |
| `log_chars`, `log_lines` | size of `exceptionMessage` |
| `trace_count`, `distinct_trace_count` | per email |
| `trace_signatures`, `exception_classes` | semicolon-lists |
| `log_prefix_sha1_{1k,10k,100k}` | resubmission detection |
| `parse_error` | non-empty if extraction failed |

### traces.csv
| column | meaning |
|---|---|
| `file`, `trace_index` | locator within the corpus |
| `incident_id` | inherited from email |
| `is_first_in_email` | True for `trace_index == 0` (often the trigger) |
| `signature`, `sig_top2`, `sig_top1`, `sig_innermost_vcell` | cluster keys at four granularities |
| `innermost_vcell_frame` | full frame string |
| `innermost_vcell_file`, `_class`, `_method` | parsed pieces |
| `exception_class` | innermost (root-cause) class |
| `frame_count`, `caused_by_count` | depth metrics |
| `top_frames` | first 5 normalized frames |
| `full_trace` | full trace text — for diagnosis |

### file_pivot.csv (primary)
One row per `innermost_vcell_file`. Sorted by `code_status` (present first),
then `incidents_total` desc.

| column | meaning |
|---|---|
| `innermost_vcell_file`, `simple_class` | identity |
| `code_status` | `present` / `partial_present` / `method_missing` / `file_missing` / `no_vcell_frame` |
| `source_paths` | actual paths under the repo (semicolon-list) |
| `distinct_methods`, `methods_present`, `methods_missing` | per-file breakdown |
| `exception_classes`, `distinct_signatures` | what's clustered here |
| `incidents_total`, `emails_total`, `traces_total` | weight |
| `first_seen_date`, `last_seen_date`, `versions_seen` | when |
| `sample_email_file` | one example to drill into |
| `recent_commit_count` | commits to `source_paths` since 1 year before `first_seen_date` (git archaeology) |
| `recent_commits` | first few commit subjects, joined with ` \| ` — eyeball for "fix"-shaped messages |
| `v_<version>` | per-version incident count (presence map, NOT fix status) |

### version_pivot.csv (secondary)
One row per `sig_innermost_vcell`. Useful when one file has multiple
distinct bugs that should not be grouped.

## Learnings

### L1. Absence is not evidence of fix
With ~30 incidents across 4 versions and no systematic bug-fixing culture
in the corpus's time window, "bug appears in build_15 but not build_47" is
**almost always sampling noise**, not a fix. Treat the `v_<version>`
columns as a presence map (which versions we have evidence for), not as a
fix-status diff.

### L2. Code-presence beats version-diff
Whether a file and method still exist in the current codebase is a binary
fact you can verify in seconds, and it doesn't depend on who happened to
submit reports in which month. It's the most reliable filter for "this
cluster is worth investigating right now."

### L3. Resubmission inflates everything
Ranking by raw trace count was wildly skewed by one user's 24-email session.
The 1k-prefix hash is a cheap, effective collapse. A more sophisticated
version (longest common prefix per pair) is possible but unnecessary at this
corpus size.

### L4. Multiple traces per email is the rule
Average is 5.4 traces per email; max observed is 21. The first trace is
usually the trigger; later ones are often downstream effects of the same
root cause, or unrelated background errors during the same session. The
`is_first_in_email` flag lets you toggle between "trigger only" and
"everything" views.

### L5. Framework-only traces are noise
Many traces bottom out entirely in `java.desktop.*` / `javax.swing.*` (focus
loss, table editing, AWT event dispatch). They land in the
`no_vcell_frame` cluster. We can't fix Swing; deprioritize.

### L6. Counts are qualitative, not quantitative
In single-digit territory, ranking by 7 vs 6 vs 5 incidents tells you very
little. Use counts to filter the long tail of `n=1` clusters, then read the
actual `full_trace`. The corpus answers "what kinds of things go wrong",
not "how often."

### L7. `Thread.dumpStack()` output is not an exception
Java's `Thread.dumpStack()` constructs a synthetic `Exception` headed
`"java.lang.Exception: Stack trace"` solely to print the current call
stack. The program continues. `VCellThreadChecker` uses it to log
threading-hygiene advisories. The parser detects this pattern (header +
`at java.base/java.lang.Thread.dumpStack` frame) and excludes those
"traces" from clusters — otherwise they inflate counts with non-failures
the user never noticed.

### L8. Pair version-presence with git-archaeology
A bug that appears only in older builds may be a real fix-already-shipped
case (the user is on outdated software) or sampling noise — version
counts alone can't tell you which. The `recent_commit_count` /
`recent_commits` columns surface git activity on the cluster's source
files since one year before the earliest incident, so you can spot
fix-shaped commit subjects before re-investigating. (`MathSymbolMapping`
in the current corpus shows two such commits: literally "NullPointerException
in TreeMap because of concurrent modification" and a follow-up — the
14 corpus traces are users on a pre-fix build.)

## Open questions / future work

- **Pre-trace context.** The log lines just before a stack trace often
  describe what the user clicked. Currently discarded; would be valuable to
  attach per trace.
- **Within-email trace dedup.** A single bug can be caught and rethrown,
  producing the same signature multiple times in one log. Currently counted
  separately.
- **Co-occurrence matrix.** Pairs of signatures that always appear together
  in the same email are likely the same root cause surfacing twice. Useful
  for further collapse.
- **User-action vocabulary.** Recognize common action lines (`User clicked
  X`, `Saving model Y`) and cluster by action + signature.
- **Stable incident_id across runs.** Currently incident IDs are renumbered
  each batch. For long-running triage, persist a mapping `prefix_hash →
  inc_id` so notes attached to an incident survive new emails arriving.
