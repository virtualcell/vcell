#!/usr/bin/env python3
"""backlog-lint — mechanical consistency checks over the VCell issue backlog.

Checks facts, never judgment. Every rule here is something that can be decided from
metadata alone: an issue that is on no board, a Priority that does not equal the sum it
is defined to be, a card marked Done whose issue is still open. Nothing in here decides
whether an issue matters — that stays with people.

Gating, not reporting. A scheduled job that merely posts a report gets ignored; this repo
learned that with the BMDB nightly. So findings are compared against a committed baseline
(baseline.json) of accepted violations, and the run fails only on findings that are NOT in
it. Accept the current state deliberately with --update-baseline, review the diff, commit.

Reads the org ProjectV2 board, which GITHUB_TOKEN cannot do: supply a PAT in
GH_PROJECT_TOKEN with `read:project` scope (plus `project` if you enable --fix-board).

Usage:
    backlog_lint.py                      # lint, exit 1 on new findings
    backlog_lint.py --strict             # exit 1 on ANY finding, baseline ignored
    backlog_lint.py --update-baseline    # accept current findings into baseline.json
    backlog_lint.py --fix-board          # also ADD unboarded issues to the board (write)
    backlog_lint.py --format markdown    # report as markdown (default: text)
"""

from __future__ import annotations

import argparse
import json
import os
import re
import sys
import urllib.error
import urllib.request
from datetime import datetime, timedelta, timezone
from pathlib import Path

OWNER = "virtualcell"
REPO = "vcell"
PROJECT_NUMBER = 1

BASELINE = Path(__file__).with_name("baseline.json")
API = "https://api.github.com/graphql"

# --- thresholds ------------------------------------------------------------------
STALE_ACTIVE_DAYS = 30      # "Active" must mean someone is working on it now
MANY_ASSIGNEES = 3          # 3+ assignees means nobody owns it
THIN_BODY_CHARS = 200       # a Queued issue must be startable from its body
POOL_STATUS = "Pool"        # where --fix-board files newly-added issues

# Labels naming a release that has shipped. Release planning belongs in board status,
# not in labels that outlive the release by years.
SHIPPED_RELEASE_LABELS = {
    "Next Release", "VCell-7.5.0", "VCell-7.5.1", "VCell-7.6.0",
}


# --- GraphQL ---------------------------------------------------------------------

def gql(query: str, variables: dict, token: str) -> dict:
    body = json.dumps({"query": query, "variables": variables}).encode()
    req = urllib.request.Request(
        API, data=body,
        headers={
            "Authorization": f"bearer {token}",
            "Content-Type": "application/json",
            "User-Agent": "vcell-backlog-lint",
        },
    )
    try:
        with urllib.request.urlopen(req, timeout=60) as r:
            payload = json.load(r)
    except urllib.error.HTTPError as e:
        detail = e.read().decode(errors="replace")[:500]
        raise SystemExit(f"GitHub API {e.code}: {detail}")
    if "errors" in payload:
        raise SystemExit("GraphQL errors: " + json.dumps(payload["errors"])[:800])
    return payload["data"]


ISSUES_Q = """
query($owner:String!, $repo:String!, $cursor:String) {
  repository(owner:$owner, name:$repo) {
    issues(first:100, after:$cursor, states:OPEN) {
      pageInfo { hasNextPage endCursor }
      nodes {
        id number title body updatedAt
        labels(first:30) { nodes { name } }
        assignees(first:10) { nodes { login } }
      }
    }
  }
}
"""

PROJECT_Q = """
query($owner:String!, $number:Int!, $cursor:String) {
  organization(login:$owner) {
    projectV2(number:$number) {
      id
      items(first:100, after:$cursor) {
        pageInfo { hasNextPage endCursor }
        nodes {
          id
          content { __typename ... on Issue { number state repository { name } } }
          fieldValues(first:30) {
            nodes {
              __typename
              ... on ProjectV2ItemFieldSingleSelectValue {
                name field { ... on ProjectV2SingleSelectField { name } } }
              ... on ProjectV2ItemFieldNumberValue {
                number field { ... on ProjectV2FieldCommon { name } } }
            }
          }
        }
      }
    }
  }
}
"""


def fetch_issues(token: str) -> dict[int, dict]:
    out, cursor = {}, None
    while True:
        d = gql(ISSUES_Q, {"owner": OWNER, "repo": REPO, "cursor": cursor}, token)
        page = d["repository"]["issues"]
        for n in page["nodes"]:
            out[n["number"]] = {
                "id": n["id"],
                "number": n["number"],
                "title": n["title"],
                "body": n["body"] or "",
                "updatedAt": n["updatedAt"],
                "labels": {l["name"] for l in n["labels"]["nodes"]},
                "assignees": [a["login"] for a in n["assignees"]["nodes"]],
            }
        if not page["pageInfo"]["hasNextPage"]:
            return out
        cursor = page["pageInfo"]["endCursor"]


def fetch_board(token: str) -> tuple[str, dict[int, dict]]:
    """Returns (project node id, {issue number: {field name: value}})."""
    out, cursor, project_id = {}, None, None
    while True:
        d = gql(PROJECT_Q, {"owner": OWNER, "number": PROJECT_NUMBER, "cursor": cursor}, token)
        proj = (d.get("organization") or {}).get("projectV2")
        if proj is None:
            # GraphQL returns null -- not an error -- when the viewer cannot see a ProjectV2.
            # A token can therefore authenticate fine (no 401) and still land here, which is
            # the single most likely misconfiguration. Say so instead of a TypeError.
            raise SystemExit(
                f"error: cannot read project #{PROJECT_NUMBER} of '{OWNER}'.\n"
                "The token authenticated, but the API returned no project, which means it "
                "cannot see it:\n"
                "  - classic PAT: needs the `read:project` scope "
                "(`gh auth refresh -s read:project` for a gh token)\n"
                "  - fine-grained PAT: needs organization permission 'Projects: Read-only', "
                "AND approval by a virtualcell org owner -- tokens work for everything else "
                "while that approval is pending\n"
                "  - or the project number is wrong / it was deleted")
        project_id = proj["id"]
        page = proj["items"]
        for n in page["nodes"]:
            c = n.get("content") or {}
            if c.get("__typename") != "Issue":
                continue
            if (c.get("repository") or {}).get("name") != REPO or c.get("state") != "OPEN":
                continue
            fv = {}
            for f in n["fieldValues"]["nodes"]:
                fname = (f.get("field") or {}).get("name")
                if not fname:
                    continue
                fv[fname] = f.get("name") if f.get("name") is not None else f.get("number")
            out[c["number"]] = fv
        if not page["pageInfo"]["hasNextPage"]:
            return project_id, out
        cursor = page["pageInfo"]["endCursor"]


def simplicity_value(label: str | None) -> int | None:
    """'Simple (5)' -> 5. Parsed, not hardcoded, so option renames do not break the check."""
    if not label:
        return None
    m = re.search(r"\((\d+)\)\s*$", label)
    return int(m.group(1)) if m else None


# --- checks ----------------------------------------------------------------------
# Each returns a list of (issue_number, detail). Keep every check decidable from
# metadata alone; anything needing judgment belongs in docs/backlog/, not here.

CHECKS: dict[str, str] = {
    "not-on-board":          "Open issue is not on the project board",
    "priority-formula":      "Priority != Importance + Simplicity",
    "scored-not-ranked":     "Importance is set but Priority was never computed",
    "half-scored":           "Simplicity is set but Importance is not, so no Priority is possible",
    "done-but-open":         "Board says Done but the issue is still open",
    "stale-active":          f"Status is Active but untouched for {STALE_ACTIVE_DAYS}+ days",
    "shipped-release-label": "Carries a label naming a release that has already shipped",
    "many-assignees":        f"{MANY_ASSIGNEES}+ assignees, so nobody owns it",
    "queued-thin-body":      f"Queued but body is under {THIN_BODY_CHARS} chars",
}


def run_checks(issues: dict[int, dict], board: dict[int, dict]) -> dict[str, list[tuple[int, str]]]:
    found: dict[str, list[tuple[int, str]]] = {k: [] for k in CHECKS}
    cutoff = datetime.now(timezone.utc) - timedelta(days=STALE_ACTIVE_DAYS)

    for num, iss in sorted(issues.items()):
        fields = board.get(num)

        if fields is None:
            found["not-on-board"].append((num, iss["title"]))

        stale_labels = sorted(iss["labels"] & SHIPPED_RELEASE_LABELS)
        if stale_labels:
            found["shipped-release-label"].append((num, ", ".join(stale_labels)))

        if len(iss["assignees"]) >= MANY_ASSIGNEES:
            found["many-assignees"].append(
                (num, f"{len(iss['assignees'])}: {', '.join(iss['assignees'])}"))

        if fields is None:
            continue

        status = fields.get("Status")
        imp = fields.get("Importance")
        pri = fields.get("Priority")
        simp = simplicity_value(fields.get("Simplicity"))

        if status == "Done":
            found["done-but-open"].append((num, iss["title"]))

        if status == "Active":
            updated = datetime.fromisoformat(iss["updatedAt"].replace("Z", "+00:00"))
            if updated < cutoff:
                found["stale-active"].append((num, f"last updated {iss['updatedAt'][:10]}"))

        if status == "Queued":
            n = len(re.sub(r"\s+", " ", iss["body"]).strip())
            if n < THIN_BODY_CHARS:
                found["queued-thin-body"].append((num, f"{n} chars"))

        if imp is not None and simp is not None and pri is not None:
            expected = int(imp) + simp
            if int(pri) != expected:
                found["priority-formula"].append(
                    (num, f"Priority={int(pri)} but Importance {int(imp)} + Simplicity {simp} = {expected}"))

        if imp is not None and pri is None:
            hint = f", would be {int(imp) + simp}" if simp is not None else ""
            found["scored-not-ranked"].append((num, f"Importance={int(imp)}{hint}"))

        if simp is not None and imp is None:
            found["half-scored"].append((num, f"Simplicity={fields.get('Simplicity')}"))

    return found


# --- baseline --------------------------------------------------------------------

def load_baseline() -> dict[str, set[int]]:
    if not BASELINE.exists():
        return {}
    raw = json.loads(BASELINE.read_text())
    return {k: set(v) for k, v in raw.get("accepted", {}).items()}


def write_baseline(found: dict[str, list[tuple[int, str]]]) -> None:
    doc = {
        "_comment": [
            "Accepted backlog-lint findings. The lint fails only on findings NOT listed here.",
            "Regenerate with: python3 tools/backlog-lint/backlog_lint.py --update-baseline",
            "Review the diff before committing -- shrinking lists are progress, growing ones",
            "are a decision to accept something.",
        ],
        "checks": CHECKS,
        "accepted": {k: sorted(n for n, _ in v) for k, v in found.items() if v},
    }
    BASELINE.write_text(json.dumps(doc, indent=2) + "\n")


# --- reporting -------------------------------------------------------------------

def report(found, baseline, strict, fmt) -> tuple[str, int]:
    lines: list[str] = []
    new_total = 0
    md = fmt == "markdown"

    def h(text, level=2):
        lines.append(("#" * level + " " + text) if md else "\n" + text)

    h("Backlog lint", 2)
    total = sum(len(v) for v in found.values())
    lines.append(f"{total} finding(s) across {len(CHECKS)} checks."
                 + ("" if strict else " Baseline-accepted findings are not failures."))

    for check, hits in found.items():
        if not hits:
            continue
        accepted = set() if strict else baseline.get(check, set())
        new = [(n, d) for n, d in hits if n not in accepted]
        new_total += len(new)
        flag = f"**{len(new)} new**" if (new and md) else f"{len(new)} new"
        h(f"{check} — {CHECKS[check]}", 3)
        lines.append(f"{len(hits)} total, {flag}." if new else f"{len(hits)} total, all accepted.")
        if new:
            if md:
                lines.append("")
                lines.append("| # | Detail |")
                lines.append("|---|---|")
            for n, d in new[:40]:
                url = f"https://github.com/{OWNER}/{REPO}/issues/{n}"
                lines.append(f"| [#{n}]({url}) | {d} |" if md else f"    #{n}  {d}")
            if len(new) > 40:
                lines.append(f"| … | {len(new) - 40} more not listed |" if md
                             else f"    … {len(new) - 40} more not listed")

    h("Result", 2)
    if new_total:
        lines.append(f"FAIL — {new_total} finding(s) not in the baseline.")
        lines.append("Fix them, or accept deliberately with --update-baseline and commit the diff.")
    else:
        lines.append("PASS — no findings outside the baseline.")
    return "\n".join(lines), new_total


# --- board write (opt-in) --------------------------------------------------------

ADD_M = """
mutation($project:ID!, $content:ID!) {
  addProjectV2ItemById(input:{projectId:$project, contentId:$content}) { item { id } }
}
"""


def add_to_board(project_id: str, issues, numbers, token) -> list[int]:
    added = []
    for n in numbers:
        gql(ADD_M, {"project": project_id, "content": issues[n]["id"]}, token)
        added.append(n)
    return added


# --- main ------------------------------------------------------------------------

def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--update-baseline", action="store_true",
                    help="accept all current findings into baseline.json")
    ap.add_argument("--strict", action="store_true",
                    help="fail on any finding, ignoring the baseline")
    ap.add_argument("--fix-board", action="store_true",
                    help="ADD unboarded open issues to the board (requires `project` scope)")
    ap.add_argument("--format", choices=["text", "markdown"], default="text")
    args = ap.parse_args()

    token = os.environ.get("GH_PROJECT_TOKEN") or os.environ.get("GITHUB_TOKEN")
    if not token:
        print("error: set GH_PROJECT_TOKEN to a PAT with `read:project` scope.\n"
              "GITHUB_TOKEN cannot read organization ProjectV2 boards.", file=sys.stderr)
        return 2

    issues = fetch_issues(token)
    project_id, board = fetch_board(token)
    found = run_checks(issues, board)

    if args.fix_board:
        unboarded = [n for n, _ in found["not-on-board"]]
        if unboarded:
            added = add_to_board(project_id, issues, unboarded, token)
            print(f"added {len(added)} issue(s) to the board: "
                  + ", ".join(f"#{n}" for n in added))
            print(f"note: they land in the board's default column; triage them into "
                  f"{POOL_STATUS!r} and score them.")
            found["not-on-board"] = []

    if args.update_baseline:
        write_baseline(found)
        print(f"baseline written to {BASELINE}")
        print("review the diff before committing.")
        return 0

    text, new_total = report(found, load_baseline(), args.strict, args.format)
    print(text)

    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        md, _ = report(found, load_baseline(), args.strict, "markdown")
        with open(summary, "a") as fh:
            fh.write(md + "\n")

    return 1 if new_total else 0


if __name__ == "__main__":
    sys.exit(main())
