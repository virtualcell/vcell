#!/usr/bin/env python3
"""Analyze per-test timing from a Regression workflow run.

The regression workflow uploads Surefire XML reports as `surefire-reports-*`
artifacts (one per shard). This tool downloads them for a given run, ranks the
slowest test cases, and proposes balanced shard counts per group so the wall
clock of each shard stays under a target.

Usage:
    tools/analyze-regression-timing.py <run-id> [--target-min 8] [--keep]

Requires the `gh` CLI (authenticated). Downloads into a temp dir unless --keep.
"""
import argparse
import glob
import os
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET
from collections import defaultdict


def download(run_id, dest):
    subprocess.run(
        ["gh", "run", "download", str(run_id), "--dir", dest,
         "--pattern", "surefire-reports-*"],
        check=True,
    )


def parse(dest):
    # (group-ish classname, test display name) -> seconds
    cases = []
    for xml in glob.glob(os.path.join(dest, "**", "*.xml"), recursive=True):
        try:
            root = ET.parse(xml).getroot()
        except ET.ParseError:
            continue
        for tc in root.iter("testcase"):
            cls = tc.get("classname", "?")
            name = tc.get("name", "?")
            try:
                t = float(tc.get("time", "0"))
            except ValueError:
                t = 0.0
            cases.append((cls, name, t))
    return cases


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("run_id")
    ap.add_argument("--target-min", type=float, default=8.0,
                    help="target test-minutes per shard (excl. compile)")
    ap.add_argument("--top", type=int, default=25)
    ap.add_argument("--keep", action="store_true")
    args = ap.parse_args()

    dest = tempfile.mkdtemp(prefix="regtiming-") if not args.keep else "regtiming"
    os.makedirs(dest, exist_ok=True)
    download(args.run_id, dest)
    cases = parse(dest)
    if not cases:
        print("no testcases found (were surefire-reports-* artifacts uploaded?)", file=sys.stderr)
        sys.exit(1)

    per_class = defaultdict(float)
    per_class_n = defaultdict(int)
    for cls, _name, t in cases:
        per_class[cls] += t
        per_class_n[cls] += 1

    print(f"# {len(cases)} test cases across {len(per_class)} classes\n")
    print("## Slowest individual test cases")
    for cls, name, t in sorted(cases, key=lambda c: -c[2])[:args.top]:
        print(f"  {t/60:6.2f} min  {cls.split('.')[-1]}.{name}")

    print("\n## Per-class totals and suggested shard count "
          f"(target {args.target_min:.0f} test-min/shard)")
    import math
    for cls, total in sorted(per_class.items(), key=lambda c: -c[1]):
        shards = max(1, math.ceil((total / 60) / args.target_min))
        # a single case longer than the target can't be split by sharding
        worst = max((t for c, _n, t in cases if c == cls), default=0) / 60
        flag = "  <-- single case exceeds target; consider omitting" if worst > args.target_min else ""
        print(f"  {total/60:7.2f} min  n={per_class_n[cls]:4d}  ~{shards} shard(s)  "
              f"(worst case {worst:.1f} min)  {cls.split('.')[-1]}{flag}")

    if not args.keep:
        subprocess.run(["rm", "-rf", dest])


if __name__ == "__main__":
    main()
