# Rule-Based Ran Transport

- **Source:** `VCell6.1_Rule-Based_Ran_Transport_Tutorial.pdf` (69 pp, 2017-07-18)
- **Superseded by a 7.7 rewrite?** **Yes** —
  `VCell_Tutorials/7.7/VCell Tutorial_ Rule-Based Ran Transport 7.7.pdf` (2025-07-18).
- **Status:** reproduced by
  [`rule-based-ran-transport.sh`](../rule-based-ran-transport.sh) against the **7.7**
  document, matching the public reference model exactly (0 errors, 0 warnings). The 6.1
  document is the historical record.

## Objective

The RAN nuclear-transport system again — the same biology as `multi-app-transport` — but
expressed with molecules, sites and reaction rules instead of named species and
reactions, and simulated across compartments.

## How it is built

Same method as [rule-based-egfr](rule-based-egfr.md), and for the same reason: the reaction
rule editor is one hand-rendered component with no children, and a rule's BioNetGen
definition cell is read-only, so no script can address a rule. The model is written as BNGL
and imported — the other documented route, and here it carries **everything**. Unlike EGFR
this model is purely rule-based, so nothing has to be added back afterwards, and the export
raises no "Simple Reactions cannot be exported" warning.

The BNGL is what VCell writes when the public reference model **`Rule-based_Ran_transport`**
(VCell database → BioModels → Tutorials) is exported to `.bngl`.

What survives the round trip is the interesting part — this is the multi-compartment case:

- **Five structures**, and BNGL says which are which by the dimension in its `compartments`
  block: `3` for a volume (`nuc`, `cyt`, `EC`), `2` for a membrane (`pm`, `nm`). The script
  checks all five come back with the right types.
- **An `anchors` block** — `RCC1(nuc)` pins that molecule to the nucleus.
- **A transport rule that crosses compartments**:
  `Transport: @nuc:Ran(cargo!+) <-> @cyt:Ran(cargo!+)`, rate `2.0 * 602.0`.

Import units matter here and differ from EGFR: this model counts **molecules**, not
concentrations — its seed species are 1000 each and its transport rate is written per
molecule.

## Note

The 6.1 document remains superseded; the script follows the 2025 rewrite.
