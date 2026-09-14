# Rule-Based Modelling — EGFR

- **Sources:** `VCell6.1_Rule-Based_Tutorial.pdf` (60 pp, 2017-07-18) and the shorter
  `SingleCompartmentRuleBased.pdf` (6 pp, 2017-06-14).
- **Superseded by a 7.7 rewrite?** **Yes** — see
  `VCell_Tutorials/7.7/VCell Tutorial_ Rule-Based EGFR 7.7.pdf` (2025-07-18).
- **Status:** reproduced by [`rule-based-egfr.sh`](../rule-based-egfr.sh) against the
  **7.7** document, matching the public reference model exactly (0 errors, 1 warning —
  the same warning the reference itself carries). The 6.1 pair is the historical record.

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

## Why the 6.1 pair is stale

Written against **VCell 6.1**: it tells the reader to download from a "RUN VCELL" menu that
no longer exists, and claims "Java (1.5.x or later) is required" — VCell has bundled its
own JRE for years. The 7.7 EGFR tutorial replaces all of it, and the script follows the
7.7 document.

## The hardest canvas in the set — and the one with no table behind it

Every other tutorial here builds its model by drawing, and every other one turned out to
have a table saying the same thing. This is where that stops.

Three of the four object types do have the route, and the tutorial names it itself:
*"Every table has a column BioNetGen definition… useful if you have separate BNGL code you
want to paste"*. Setting that one cell to `EGFR(ecd,tmd,Y1~u~p,Y2~u~p)` replaces about a
dozen right-click-on-a-shape gestures, and it works for Molecules, Species and Observables.

Reaction **rules** have no such route, for two separate reasons:

- `BioModelEditorReactionTableModel.isCellEditable` allows the equation/BNGL column only
  when the row is a `ReactionStep`. A `ReactionRule` is not one, so the cell is read-only —
  always, not just after first specification. (Directly below it sits a commented-out
  version that *would* have allowed it when the rule's molecules have no components, so
  this was contemplated once.)
- The rule editor has nothing to address. Its whole reactant/product pattern area is a
  single anonymous component — `ReactionRuleEditorPropertiesPanel$1`, 777×200 px, **zero
  children**. Molecules, sites, states and bonds are all painted onto it. The only real
  Swing controls are `Add Reactant`, `Add Product`, `Reversible` and three display toggles,
  so pressing New Rule gives an empty rule and no way to fill it in.

So the script takes the other documented path, the one the 6.1 reference guide describes:
write the model as **BNGL** and `File > Import` it, which also creates the BioNetGen and
NFSim applications on the way in.

## Where the numbers come from

The tutorial deliberately does not state most of them — it says throughout to *"match all
values to the model in the Tutorials folder"*. That model is public:
**`Rule-based_egfr_tutorial`**, VCell database → BioModels → Tutorials. The BNGL the script
writes is what VCell itself produces when that model is exported to `.bngl`, so every rate
constant is the reference's own.

## One thing BNGL cannot carry

The reference model has **eleven** reactions: ten rules and one ordinary reaction,
`ShcP -> ShcU` (Kf 0.005). VCell says so on the way out — *"Simple Reactions cannot be
exported to .bngl format. Some information will be lost."* — so the round trip drops it and
the script puts it back through the Reactions table, whose equation column **is** editable
for a ReactionStep.

Two traps around that, both of which the script handles and asserts:

- **Rename the species first.** BNGL seed species carry no names, so the import invents
  them from their patterns (`Shc_sh3_Yp`). Typing `ShcP -> ShcU` against those names does
  not fail — it silently creates two new empty species, because an equation cannot *place*
  a species. The script renames the five to `R`, `L`, `Grb2`, `ShcP`, `ShcU` first and then
  checks the count is still 5.
- **A single arrow does not make a reaction irreversible.** Reversibility is its own
  property; left alone the model warns that mass action "will be interpreted as a
  degradation of the product".

## A suggestion for VCell — issue #2068

Making the BioNetGen definition column writable for `ReactionRule` — the change the
commented-out block was reaching for — would let this tutorial be scripted the way it is
taught rather than the way around.

It is not sufficient on its own, though, and #2068 records why. Raw BNGL needs autocomplete
over the molecule patterns actually available, and site states, bonds and
bound / unbound / "bound to anything" (`!+`, `!?`) have to stay consistent across reactant
and product patterns — which the graphics editor currently guarantees by construction, and
by greying out the combinations that are impossible. An expert can type it correctly; a new
user is exactly who the graphics editor serves well. So the useful shape of a textual route
is a design question, not a one-line change.
