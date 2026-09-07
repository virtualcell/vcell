#!/usr/bin/env bash
#
# VCell Tutorial: importing from Pathway Commons
#
# Reproduces the storyline of
# vcell.org/webstart/VCell_Tutorials/Tutorial06_PathwayCommons_6.0.pdf.
# See storylines/pathway-commons.md.
#
# The only tutorial in the set that depends on a third-party service staying up, and the
# oldest document in it (2016). Both services it needs are alive: the search goes to
# Pathway Commons' current pc2 API, and the import pulls BioPAX from Reactome.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

# The search term. The PDF only ever shows screenshots, so this is a choice: "insulin"
# is the example the client's own code comment uses, and it returns a page of small,
# well-formed Reactome pathways.
QUERY="${QUERY:-insulin}"
PATHWAY="${PATHWAY:-Acetylcholine regulates insulin secretion}"

dismiss OK
sleep 1

step "Search Pathway Commons for '$QUERY'"
must tab name=LeftBottomTabbedPane "Pathway Comm" >/dev/null; sleep 2
must settext name=PathwayCommonsSearchTextField "$QUERY" >/dev/null; sleep 1
must click name=PathwayCommonsSearchButton >/dev/null

# A live third-party service, so wait for an answer rather than sleeping on a guess. The
# client caps the search at a 10s connect and a 30s read; this allows for both plus the
# tree being built.
for i in $(seq 1 30); do
  sleep 2
  HITS=$("$B" findrow name=PathwayCommonsResponseTree "$PATHWAY" \
      | python3 -c 'import json,sys;print(json.load(sys.stdin)["row"])')
  [ "$HITS" -ge 0 ] && break
done
if [ "${HITS:--1}" -lt 0 ]; then
  echo "FATAL: no pathway matching '$PATHWAY' after searching for '$QUERY'." >&2
  echo "       Pathway Commons is a third party; check that" >&2
  echo "       https://www.pathwaycommons.org/pc2/search still answers." >&2
  exit 1
fi
echo "  '$PATHWAY' is row $HITS" >&2

step "Preview it: the pathway's entities, as a table"
must row name=PathwayCommonsResponseTree "$HITS" >/dev/null; sleep 2
must click name=PathwayCommonsPreviewButton >/dev/null
# The import pulls BioPAX Level 2 from Reactome, which is a second third-party service and
# a second thing to wait for rather than sleep on.
for i in $(seq 1 40); do
  sleep 2
  ROWS=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
def walk(n):
    if n.get("name") == "PathwayPreviewTable" and n.get("table"):
        print(n["table"]["rowCount"]); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
')
  [ "${ROWS:-0}" -gt 0 ] && break
done
if [ "${ROWS:-0}" -eq 0 ]; then
  echo "FATAL: the pathway preview stayed empty. The import reads BioPAX from" >&2
  echo "       https://reactome.org/ReactomeRESTfulAPI/RESTfulWS/biopaxExporter/Level2/" >&2
  echo "       - check that it still answers." >&2
  exit 1
fi
echo "  $ROWS entities in '$PATHWAY'" >&2

step "Import every entity - what ctrl+A and Import > Selected Only mean"
# The PDF says "hit ctrl+a" and, for a multi-page pathway, "click the right arrow icon and
# repeat". The keystroke is about the rows the table currently shows, so saying the range
# says the same thing - and it says it for the whole table at once, with no paging.
must trows name=PathwayPreviewTable "0-" >/dev/null; sleep 2
button_menu name=PathwayPreviewImportButton 'Selected Only'; sleep 10

IMPORTED=$("$B" readcell name=PathwayPreviewTable 0 'Imported?' \
    | python3 -c 'import json,sys; print(json.load(sys.stdin).get("value"))')
if [ "$IMPORTED" != "true" ]; then
  echo "FATAL: the first entity still reads Imported? = $IMPORTED" >&2
  exit 1
fi

step "Pathway Objects: the list form, not the diagram"
# This is the whole reason this tutorial is reachable. Steps 4 and 5 of the PDF work on the
# Pathway Diagram - "click a corner of the diagram, drag your cursor over all entities" -
# which is a pixel gesture on a custom canvas. But the PDF itself offers the other route
# two pages later: "click Pathway Objects to organize the entities into list form", and
# from there the same Physiology Links menu. Same statement, addressable.
navselect 'Pathway Objects'; sleep 4
OBJECTS=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
def walk(n):
    if n.get("name") == "PathwayObjectsTable" and n.get("table"):
        print(n["table"]["rowCount"]); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
')
echo "  $OBJECTS pathway objects" >&2

step "Import into the physiology"
must trows name=PathwayObjectsTable "0-" >/dev/null; sleep 2
button_menu name=PhysiologyLinksButton 'Import into Physiology...'; sleep 8
# The dialog lists every interaction and participant it is about to create, and lets the
# expressions be edited first. The tutorial accepts them as they come.
dialog_button 'Import into Physiology' OK; sleep 10
# VCell then reports what it converted. Waiting for that notice is also how we know the
# conversion finished.
answer OK; sleep 8

# The model tree carries the counts in its own labels, which is the shortest honest way
# to say "the physiology has something in it now".
SPECIES=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
def walk(n):
    if n.get("name") == "bioModelEditorTree" and n.get("tree"):
        for row in n["tree"]["rows"]:
            if str(row.get("text","")).startswith("Species ("):
                print(row["text"]); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
')
echo "  physiology now has $SPECIES" >&2
case "$SPECIES" in
  "Species (0)"|"") echo "FATAL: nothing was imported into the physiology" >&2; exit 1 ;;
esac

step "Link a species of your own to a pathway entity"
# The PDF adds this species with the species tool on the reaction diagram. The Species
# table has an "(add new here)" row that does the same thing - it is offered because this
# model has exactly one structure, which is the condition BioModelEditorSpeciesTableModel
# puts on it.
navselect 'Species'; sleep 3
ADD_ROW=$(row name=SpeciesTable '(add new here)' --exact)
must setcell name=SpeciesTable "$ADD_ROW" 0 "Ach_reporter" >/dev/null; sleep 3
must trow name=SpeciesTable "$(row name=SpeciesTable 'Ach_reporter' --exact)" >/dev/null; sleep 3

button_menu name=ModelPathwayLinksButton 'Edit Pathway Links...'; sleep 5
LINKS=$(dialog_table 'Edit Pathway Links')
must setcell "$LINKS" "$(row "$LINKS" 'acetylcholine' --exact --in 'Entity Name')" \
    "$(col "$LINKS" 'Link')" true >/dev/null; sleep 2
dialog_button 'Edit Pathway Links' Close; sleep 4

LINKED=$("$B" readcell name=SpeciesTable "$(row name=SpeciesTable 'Ach_reporter' --exact)" 'Link' \
    | python3 -c 'import json,sys; print(json.load(sys.stdin).get("value"))')
echo "  Ach_reporter is linked to: $LINKED" >&2
if [ "$LINKED" != "acetylcholine" ]; then
  echo "FATAL: the pathway link did not take (Link column reads '$LINKED')" >&2
  exit 1
fi

step "Done -- pathway searched, previewed, imported, and a species linked to it."
