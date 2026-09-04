#!/usr/bin/env python3
"""
Replay a script recorded by the UI recorder (see bridge.sh record).

Every step is issued through the bridge endpoint that already implements that verb, so
replay adds no new way to drive the UI - it just plays back what the recorder captured.

Two drivers, chosen at playback time from the same recording:

  semantic (default)  buttons go through doClick(); nothing moves the cursor. Fast, and
                      the right choice for CI, where a stray pointer is noise.
  robot               the cursor glides to each target before the click, and the click is
                      a real native press/release. Slower, and the only mode worth filming
                      - a click with no pointer anywhere near it reads as a broken video.

Timing comes from the recording. --speed divides every gap; --max-delay caps the long
pause where somebody went for coffee.

--from/--to replay a slice of the script, which is what lets a scenario assert between
steps (and lets a tutorial re-shoot one segment without re-recording the whole thing).

Usage:
  replay.py <script.json> [--driver semantic|robot] [--speed N] [--max-delay MS]
            [--from N] [--to N] [--port N] [--dry-run] [--quiet]
"""

import argparse
import json
import sys
import time
import urllib.parse
import urllib.request

# verb -> (endpoint, [extra step fields to pass through])
VERBS = {
    "click":                ("click",                []),
    "rightClick":           ("rightClick",           []),
    "setText":              ("setText",              ["text", "enter"]),
    "selectTab":            ("selectTab",            ["index"]),
    "selectTreeRow":        ("selectTreeRow",        ["row"]),
    "doubleClickTreeRow":   ("doubleClickTreeRow",   ["row"]),
    "rightClickTreeRow":    ("rightClickTreeRow",    ["row"]),
    "selectTableRow":       ("selectTableRow",       ["row"]),
    "doubleClickTableRow":  ("doubleClickTableRow",  ["row"]),
    "rightClickTableRow":   ("rightClickTableRow",   ["row"]),
    "menu":                 ("menu",                 []),
}

# Verbs the robot driver replaces outright. Everything else already acts through a model
# (selecting a tab, a tree row) and only needs the cursor brought over for the camera.
ROBOT_CLICK_VERBS = {"click"}


class Bridge:
    def __init__(self, port, quiet=False):
        self.base = "http://127.0.0.1:%d" % port
        self.quiet = quiet

    def get(self, endpoint, **params):
        url = "%s/%s" % (self.base, endpoint)
        if params:
            url += "?" + urllib.parse.urlencode(
                {k: v for k, v in params.items() if v is not None})
        with urllib.request.urlopen(url, timeout=130) as resp:
            body = resp.read().decode("utf-8")
        try:
            return json.loads(body)
        except ValueError:
            return {"raw": body}


def replay(path, driver, speed, max_delay, port, dry_run, quiet, first=1, last=None,
           step_timeout=15000):
    with open(path, encoding="utf-8") as fh:
        script = json.load(fh)
    all_steps = script.get("steps", [])
    last = len(all_steps) if last is None else last
    if first < 1 or last > len(all_steps) or first > last:
        raise SystemExit("step range %d..%d is outside the script's 1..%d"
                         % (first, last, len(all_steps)))
    steps = all_steps[first - 1:last]
    bridge = Bridge(port, quiet)

    if not dry_run:
        bridge.get("health")

    failures = 0
    for n, step in enumerate(steps, first):
        verb = step.get("verb")
        if verb not in VERBS:
            print("  step %d: unknown verb %r - skipped" % (n, verb), file=sys.stderr)
            failures += 1
            continue
        endpoint, extras = VERBS[verb]

        delay = min(step.get("delayMs", 0), max_delay) / 1000.0 / speed
        if delay > 0 and not dry_run:
            time.sleep(delay)

        # /menu takes the menu text path; every other verb takes a component selector
        selector = step.get("path") if verb == "menu" else step.get("selector")
        params = {"path": selector}
        for field in extras:
            if field in step:
                value = step[field]
                params[field] = str(value).lower() if isinstance(value, bool) else value

        label = "%-20s %s" % (verb, selector)
        note = step.get("note")
        if note:
            label += "   (%s)" % note
        if not quiet:
            print("  %2d. %s" % (n, label))
        if dry_run:
            continue

        def perform():
            if driver == "robot" and selector and verb != "menu":
                # Bring the pointer over first so the click is visible where it lands. A
                # menu path is not a component selector, so menus stay on the semantic
                # path - and they have to: replaying a menu through a popup is exactly
                # what the recorder avoided capturing in the first place.
                bridge.get("glide", path=selector, ms=500)
                if verb in ROBOT_CLICK_VERBS:
                    return bridge.get("robotClick", path=selector, glideMs=0)
            return bridge.get(endpoint, **params)

        # Retry until the step takes. A target is routinely not ready the instant the
        # previous one finishes - a menu item sits disabled for a moment after a modal
        # dialog is dismissed, a control appears a beat after the window that holds it.
        # Retrying is safe precisely because both failure signals mean nothing happened:
        # an "error" reply, or a false result meaning the selector did not resolve.
        deadline = time.time() + step_timeout / 1000.0
        while True:
            result = perform()
            problem = None
            if isinstance(result, dict):
                if "error" in result:
                    problem = result["error"]
                elif any(v is False for v in result.values()):
                    problem = "%s did not resolve" % selector
            if problem is None or time.time() >= deadline:
                break
            time.sleep(0.4)

        if problem is not None:
            print("      FAILED: %s" % problem, file=sys.stderr)
            failures += 1

        bridge.get("idle")

        # Wait for the window this step opened rather than sleeping and hoping. This is
        # what lets a recording made on one machine replay on a slower one.
        opens = step.get("opensWindow")
        if opens:
            waited = bridge.get("waitFor", textContains=opens, state="showing",
                                timeoutMs=30000, intervalMs=200)
            if not waited.get("satisfied"):
                print("      FAILED: window %r never appeared" % opens, file=sys.stderr)
                failures += 1
            elif not quiet:
                print("      (waited %sms for %r)" % (waited.get("elapsedMs"), opens))

    return failures


def main():
    ap = argparse.ArgumentParser(description="Replay a recorded VCell UI script.")
    ap.add_argument("script")
    ap.add_argument("--driver", choices=("semantic", "robot"), default="semantic")
    ap.add_argument("--speed", type=float, default=1.0,
                    help="divide every recorded gap by this (default 1.0 = as recorded)")
    ap.add_argument("--max-delay", type=int, default=5000,
                    help="cap any single gap, in ms (default 5000)")
    ap.add_argument("--from", dest="first", type=int, default=1,
                    help="first step to replay, 1-based (default 1)")
    ap.add_argument("--to", dest="last", type=int, default=None,
                    help="last step to replay, inclusive (default: the end)")
    ap.add_argument("--step-timeout", type=int, default=15000,
                    help="how long to keep retrying one step, in ms (default 15000)")
    ap.add_argument("--port", type=int, default=9123)
    ap.add_argument("--dry-run", action="store_true", help="list the steps without acting")
    ap.add_argument("--quiet", action="store_true")
    args = ap.parse_args()

    if args.speed <= 0:
        ap.error("--speed must be positive")

    failures = replay(args.script, args.driver, args.speed, args.max_delay,
                      args.port, args.dry_run, args.quiet, args.first, args.last,
                      args.step_timeout)
    if failures:
        print("REPLAY FAILED: %d step(s) did not take" % failures, file=sys.stderr)
        return 1
    if not args.quiet:
        print("replay ok")
    return 0


if __name__ == "__main__":
    sys.exit(main())
