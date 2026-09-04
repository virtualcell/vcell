#!/usr/bin/env python3
"""
Turn a recorded UI script plus its screenshots into a VCell help page skeleton.

What this does and does not do is the whole point. It writes the MECHANICAL parts of a
page - the ordered steps, the image references in the right places, a target and title
that match the file name - and leaves the prose to a person. Auto-generated help text
reads like auto-generated help text; the recorder knows what was clicked, not why it
matters to a user.

Output is the custom XML under vcell-client/UserDocumentation/originalXML, compiled to
JavaHelp and HTML by org.vcell.documentation.DocumentCompiler. Tag vocabulary comes from
VCellDocTags: page / introduction / appearance / operations / para / list / item / bold /
imgReference / link.

The images are NOT copied anywhere. DocumentCompiler resolves <imgReference target="X.png"/>
against its own image directory, so a person decides which captures are worth keeping and
puts those in topics/image/ under names that mean something - "SS_ViewerControls.png", not
"step-03-click.png". The scaffold names them as it found them and says so in a comment.

Usage:
  doc-scaffold.py <recording.json> --shots DIR --target LangevinViewer
                  [--title "SpringSaLaD Viewer"] [--out page.xml]
"""

import argparse
import html
import json
import os
import re
import sys

MAX_HELP_IMAGE_BYTES = 500000  # DocumentCompiler.MAX_IMG_FILE_SIZE

# How a recorded verb reads as an instruction to a user. The recorder stores what was
# done to which component; this is the sentence stem a writer will finish.
PHRASING = {
    "menu":                 'Choose <bold>%s</bold> from the menu.',
    "click":                'Click %s.',
    "rightClick":           'Right-click %s.',
    "setText":              'Type <bold>%s</bold> into %s.',
    "selectTab":            'Select the %s tab.',
    "selectTreeRow":        'Select %s.',
    "expandTreeRow":        'Expand %s.',
    "doubleClickTreeRow":   'Double-click %s to open it.',
    "rightClickTreeRow":    'Right-click %s.',
    "selectTableRow":       'Select %s.',
    "doubleClickTableRow":  'Double-click %s.',
    "rightClickTableRow":   'Right-click %s.',
}


def readable_target(step):
    """The friendliest name we have for what a step acted on."""
    note = step.get("note") or ""
    # note looks like: JButton 'Reset view'   /   JMenuItem 'Detach: allow minimizing...'
    quoted = re.search(r"'([^']*)'", note)
    if quoted and quoted.group(1).strip():
        label = quoted.group(1).strip()
        # a tooltip is a sentence; a button label is a name. Keep the first clause only.
        label = re.split(r"[:.]", label)[0].strip()
        return "<bold>%s</bold>" % html.escape(label)
    selector = step.get("selector") or ""
    if selector.startswith("name="):
        return "the <bold>%s</bold> control" % html.escape(selector[5:])
    return "the control"


def sentence(step):
    verb = step.get("verb")
    stem = PHRASING.get(verb)
    if stem is None:
        return "TODO: describe this step (%s)." % html.escape(str(verb))
    if verb == "menu":
        return stem % html.escape((step.get("path") or "").replace(">", " &gt; "))
    if verb == "setText":
        return stem % (html.escape(step.get("text", "")), readable_target(step))
    if verb == "selectTab":
        title = step.get("tabTitle")
        return stem % ("<bold>%s</bold>" % html.escape(title.strip()) if title
                       else "tab %s" % step.get("index"))
    if "row" in step:
        # What the row DISPLAYS, when the recorder captured it - "select
        # SpringSalad_SolverSuite.vcml" is documentation, "select row 3" is not.
        text = step.get("rowText")
        if text:
            # a file chooser reports absolute paths; the name is what a reader needs
            if os.sep in text:
                text = os.path.basename(text)
            return stem % ("<bold>%s</bold>" % html.escape(text))
        return stem % ("row %s" % step.get("row"))
    return stem % readable_target(step)


def load_shots(shots_dir):
    manifest = os.path.join(shots_dir, "shots.json")
    if os.path.isfile(manifest):
        with open(manifest, encoding="utf-8") as fh:
            return json.load(fh)
    found = []
    for name in sorted(os.listdir(shots_dir)):
        if name.lower().endswith(".png"):
            full = os.path.join(shots_dir, name)
            found.append({"tag": os.path.splitext(name)[0], "path": full,
                          "bytes": os.path.getsize(full)})
    return found


def build(recording, shots, target, title):
    with open(recording, encoding="utf-8") as fh:
        script = json.load(fh)
    steps = script.get("steps", [])
    shot_list = load_shots(shots) if shots else []
    by_tag = {s["tag"]: s for s in shot_list}

    out = []
    out.append('<?xml version="1.0" encoding="UTF-8"?>')
    out.append("<vcelldoc>")
    out.append('<page title="%s" target="%s">' % (html.escape(title), html.escape(target)))
    out.append("")
    out.append("\t<introduction>")
    out.append("\tTODO: one or two sentences on what this feature is for. Recorded from %d"
               % len(steps))
    out.append("\tinteraction(s) on %s." % html.escape(script.get("recordedAt", "an unknown date")))
    out.append("\t</introduction>")
    out.append("")

    start = by_tag.get("step-00-start")
    out.append("\t<appearance>")
    out.append("\tTODO: describe what the user is looking at.")
    if start:
        out.append('\t\t<imgReference target = "%s"/>' % os.path.basename(start["path"]))
    out.append("\t</appearance>")
    out.append("")

    out.append("\t<operations>")
    out.append("\t<list>")
    for n, step in enumerate(steps, 1):
        out.append("\t\t<item>%s</item>" % sentence(step))
        shot = next((s for s in shot_list if s["tag"].startswith("step-%02d-" % n)), None)
        if shot:
            out.append('\t\t<imgReference target = "%s"/>' % os.path.basename(shot["path"]))
    out.append("\t</list>")
    out.append("\t</operations>")
    out.append("")
    out.append("</page>")
    out.append("")
    out.append("</vcelldoc>")
    return "\n".join(out) + "\n", steps, shot_list


def main():
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("recording")
    ap.add_argument("--shots", help="directory of captures from replay.py --shots")
    ap.add_argument("--target", required=True,
                    help="page target id, referenced from TOC.xml (e.g. LangevinViewer)")
    ap.add_argument("--title", help="page title; defaults to the target")
    ap.add_argument("--out", help="write here instead of stdout")
    args = ap.parse_args()

    xml, steps, shot_list = build(args.recording, args.shots, args.target,
                                  args.title or args.target)
    if args.out:
        with open(args.out, "w", encoding="utf-8") as fh:
            fh.write(xml)
        print("wrote %s (%d step(s), %d image(s))" % (args.out, len(steps), len(shot_list)))
    else:
        sys.stdout.write(xml)

    oversized = [s for s in shot_list if s.get("bytes", 0) > MAX_HELP_IMAGE_BYTES]
    if oversized:
        print("", file=sys.stderr)
        print("%d image(s) exceed the help system's %d byte cap and would fail the doc "
              "build:" % (len(oversized), MAX_HELP_IMAGE_BYTES), file=sys.stderr)
        for s in oversized:
            print("  %-28s %d bytes" % (s["tag"], s["bytes"]), file=sys.stderr)
        print("Re-run replay.py with a lower --shot-scale.", file=sys.stderr)

    print("", file=sys.stderr)
    print("NEXT: copy the captures you want into "
          "vcell-client/UserDocumentation/originalXML/topics/image/ under meaningful "
          "names, update the imgReference targets to match, write the prose, and add a "
          "<tocitem> for target=%r in TOC.xml." % args.target, file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
