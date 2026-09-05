# Pathway Commons

- **Source:** `Tutorial06_PathwayCommons_6.0.pdf` (46 pp, 2016-11-01) — the oldest
  document in the set.
- **Superseded by a 7.7 rewrite?** No.
- **Status:** storyline extracted; not scripted, and needs verification before rewriting.

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

## Caution before reusing this

This is the only tutorial in the set that depends on a **third-party service** staying up
and keeping its API shape. The client still ships the `Pathway Comm` tab and a
`PathwayCommonsResponseTree` component, so the feature is present — but whether the
remote endpoint still answers was **not verified**, and a 2016 document is the most
likely of the set to be describing something that no longer works. Check the service
before investing in a rewrite.
