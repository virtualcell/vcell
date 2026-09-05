# Rule-Based Modelling — EGFR

- **Sources:** `VCell6.1_Rule-Based_Tutorial.pdf` (60 pp, 2017-07-18) and the shorter
  `SingleCompartmentRuleBased.pdf` (6 pp, 2017-06-14).
- **Superseded by a 7.7 rewrite?** **Yes** — see
  `VCell_Tutorials/7.7/VCell Tutorial_ Rule-Based EGFR 7.7.pdf` (2025-07-18).
- **Status:** storyline extracted. **Do not rewrite these** — reproduce against the 7.7
  document instead, and treat these two as the historical record.

## Objective

Introduce rule-based modelling: molecules with binding sites and states, species as
structured objects, observables as model outputs, and reaction rules that transform
patterns rather than named species.

## Storyline (the 6.1 pair)

`SingleCompartmentRuleBased.pdf` is a **reference guide**, not a click-through: it
explains the concepts, then lists the modelling steps as prose.

1. **Concepts.** Molecules are made of sites that bind and carry states. Species are
   molecules joined by bonds — every state must be specified or the species is an
   invalid pool. Observables are functions over all species sharing a property.
   Reaction rules transform reactant patterns into product patterns.
2. **Getting a model.** Either load a public rule-based model from
   `VCellDB > BioModels > Tutorial`, or `File > Import` a BNGL file — which creates two
   applications, a Network-Free (`NFSim`) one and a deterministic (`BioNetGen`) one.
3. **Building one.** Create Molecules, Species, Observables and Reaction Rules from the
   right-click menus on the corresponding tabs. On a rule's Kinetics tab set whether it
   is reversible and give forward/reverse microscopic rate constants.
4. **Simulating.** Three application types over the same physiology:
   - *Deterministic* and *Stochastic* generate the network via BioNetGen first, bounded
     by max iterations and max molecules per species.
   - *Network-Free* skips generation and runs NFSim directly on observables.
5. **Limits worth knowing.** Mass-action only; no identical sites on a molecule; no
   include/exclude; generated species capped at 800 and reactions under 2,000.
   Symmetry factor 1/2 for `A+A→…`, statistical factor 2 for `A.A→A.A'`.

## Why it is stale

It is written against **VCell 6.1**, tells the reader to download from a "RUN VCELL" menu
that no longer exists, and claims "Java (1.5.x or later) is required" — VCell has bundled
its own JRE for years. The 7.7 EGFR tutorial replaces all of it.
