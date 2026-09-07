# Spatial Rule-Based Guide

- **Source:** `SpatialRuleBasedGuide.pdf` (7 pp, 2017-06-14)
- **Superseded by a 7.7 rewrite?** No direct replacement.
- **Status:** audited by [`spatial-rule-based.sh`](../spatial-rule-based.sh) — prose and
  figures, no steps to follow, so the script checks what the document *asserts* against the
  current client. **5 claims still hold, 3 have gone stale.**

## What it is

The companion to `SingleCompartmentRuleBased.pdf`, extending rule-based modelling to
spatial applications — the same guide-style prose rather than a click-through.

## Why it is the likeliest of the set to be wrong

Written against VCell 6.1 and carrying the same stale installation preamble. Because the
7.7 refresh covered the two *single-compartment* rule-based tutorials but not this one,
spatial rule-based modelling is the **gap in the current documentation set** — the only
rule-based topic with no current document.

It also ends with a list of limitations explicitly labelled *"temporary, will be lifted in
future releases of VCell"*, written nine years ago. Whether they have been is exactly the
sort of thing a reader cannot find out by reading.

## The audit

### Gone stale

| Claim | What the client does now |
|---|---|
| the guide points the reader at a tutorial model called **`Mix_Reactions_Rules`** | no such model in the Tutorials folder |
| …and at **`RB_Enzyme_Kinetics`** | no such model in the Tutorials folder |
| "**Only mass-action kinetic laws are supported**" in reaction rules — listed under the temporary limitations | a rule now offers **Mass Action** *and* **Henri-Michaelis-Menten (Irreversible)**. One of the limitations has been at least partly lifted, and the document that says otherwise is the only one covering this topic |

### Still true

- `Rule-based_Ran_transport`, the model the guide is built around, is still in the Tutorials
  folder.
- Importing BNGL still asks about units and simulation volume, and still creates two
  applications — though they are named `BioNetGen app` and `NFSim app`, not the bare
  `BioNetGen` and `NFSim` the guide uses.
- **"Molecules cannot have identical sites"** — still refused, and VCell says so plainly:
  *"Site 's' already exists in Molecule 'Twin'! Multiple identical Sites not supported
  witin a Molecule."* (VCell's own typo, "witin", not the guide's.)
- `New Application` still offers Deterministic, Stochastic and Network-Free — and now
  **SpringSaLaD** as well, which this 2017 document predates.

### A note on the check that nearly hung

VCell refuses `Twin(s,s)` through a **modal** dialog, so the `setCell` call blocks until
something dismisses it. The audit runs that call in the background and then goes looking
for the dialog, because a check that can hang is not a check.
