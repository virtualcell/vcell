# Pathway Commons

- **Source:** `Tutorial06_PathwayCommons_6.0.pdf` (46 pp, 2016-11-01) — the oldest
  document in the set.
- **Superseded by a 7.7 rewrite?** No.
- **Status:** reproduced by [`pathway-commons.sh`](../pathway-commons.sh), 0 errors.
  Both third-party services verified live, 2026-09-07.

## Objective

Import pathway data from the external Pathway Commons database into a VCell physiology,
and link VCell species to pathway entities.

## Storyline

1. Open the **Pathway Comm** tab in the Database Navigation pane; type a query and
   press Search. Green text is the data source, red text the organism.
2. `Open Web Link` opens the entry in a browser; `Preview` starts an import.
3. In the preview, filter entities by `Type` or by name, select the ones wanted, then
   `Import > Selected Only`. `ctrl+a` selects all on a page; multi-page results need
   the arrow icon and a repeat.
4. `Pathway > Pathway Diagram` shows what was imported; the circle icons re-lay it out.
5. Select a region of the diagram and use `Physiology Links > Import into Physiology`.
   Expressions can be edited inline before confirming.
6. Link an existing VCell species to a pathway entity via
   `Pathway Links > Edit Pathway Links…` and the `Link` checkbox column.
7. `Pathway Objects` lists everything imported in table form.

## The third-party question, answered

This is the only tutorial in the set that depends on services outside VCell, and the
caution that used to stand here — *check before investing* — has now been checked. It
needs **two** services, and both answer:

| Service | Endpoint | State |
|---|---|---|
| Pathway Commons search | `https://www.pathwaycommons.org/pc2/search` | live, API v14, 343 hits for `"insulin"` |
| Reactome BioPAX export | `https://reactome.org/ReactomeRESTfulAPI/RESTfulWS/biopaxExporter/Level2/<numeric id>` | live |

The Reactome one is worth a note: that RESTful API is the *old* one, superseded by
ContentService, and it answers **400** to a stable `R-HSA-` identifier. It works because
`extractReactomeId` strips the prefix and passes the bare number, which is what that API
has always taken. It is the most likely part of this tutorial to break next, so the script
says which URL to check when the preview comes back empty.

The panel itself has already been modernised — the search targets the current `pc2` API,
not the `webservice.do` endpoint the 2016 document was written against, and pathway links
open Reactome detail pages. `PathwayCommonsRequest.defaultBaseURL` still names the retired
`http://www.pathwaycommons.org/pc/webservice.do`, but nothing on this path uses it.

## Scripting it: the diagram is avoidable, and the PDF says so

Steps 4 and 5 work on the **Pathway Diagram** — "click a corner of the diagram, drag your
cursor over all entities and release" — which is a pixel gesture on a custom canvas. But
the document itself offers the other route two pages later: *"Click Pathway Objects to
organize the entities into list form"*, and from that list the same
`Physiology Links > Import into Physiology…` menu. The script takes the list.

Everything else is tables and menus:

- `ctrl+a` on a page of the preview table is a **row range** — and a range covers the whole
  table at once, so the PDF's "if a pathway extends to multiple pages, click the right
  arrow icon and repeat" is not needed.
- the `Link` column in *Edit Pathway Links…* is a checkbox column, which `setCell` can now
  tick.
- the species the PDF adds with the species tool comes from the Species table's
  `(add new here)` row instead — offered because this model has exactly one structure.

The one choice the script has to make is the search term: the PDF is all screenshots and
never names one. It uses `insulin` (the example in the client's own code) and the pathway
*Acetylcholine regulates insulin secretion*, which is small and well-formed. Both are
overridable with `QUERY=` and `PATHWAY=`.
