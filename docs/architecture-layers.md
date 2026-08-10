# Layer architecture: biological, mathematical, solver

The rules that govern what belongs in each layer and which way dependencies point.

**Why this document exists.** These principles are long-standing and obvious to anyone who has
worked on the design from the start. They are not obvious from the code, because the layers model
the same domain with near-identical vocabulary, and because several long-standing violations look
like precedent. This is an attempt to write them down so they can be applied — and argued with —
without needing the history.

> **Provenance.** Rules marked **[stated]** were given directly by the project's lead architect.
> Rules marked **[inferred]** were derived from the code and should be corrected if wrong. Known
> violations are listed at the end rather than hidden, so the document describes the intended
> design without pretending the code already matches it.

## The three layers

| layer | packages | owns |
|---|---|---|
| **biological** | `cbit.vcell.model`, `org.vcell.model.rbm`, `cbit.vcell.mapping`, `cbit.vcell.biomodel` | what the user edits: physiology (species, reactions, rules) and applications (`SimulationContext`, species-context specs, reaction-rule specs) |
| **mathematical** | `cbit.vcell.math` | the `MathDescription`: a complete, self-contained mathematical statement of what is to be solved |
| **solver** | `org.vcell.solver.*`, `cbit.vcell.solver` | turning a `MathDescription` into one specific solver's input, running it, reading its output |

Two translations connect them, and they are the only places where translation happens:

```
biological  --(math mapping)-->  mathematical  --(solver writer)-->  solver input
```

- **bio → math** is the math mapping (`LangevinMathMapping`, `RulebasedMathMapping`, and siblings).
- **math → solver** is the solver wrapper (`LangevinLngvWriter`, `NFsimXMLWriter`, and siblings),
  which takes a `MathDescription` as input.

## The dependency rule

**[stated]** Dependencies point one way only:

```
biological  →  mathematical  →  solver
```

`cbit.vcell.math` depends on **neither** the biological layer nor the solver layer. It may depend on
`cbit.vcell.parser` (expressions), `cbit.vcell.geometry`, and general utilities.

This is already the grain of the code: 24 files under `cbit/vcell/mapping/` import
`cbit.vcell.math.*`, so moving a shared type *down* into math is always safe.

Enforced by `MathNamespaceSeparationTest`, which fails the build on a forbidden import and also
fails when an accepted exception becomes stale.

## P1 — A math description stands alone

**[stated]** A `MathDescription` must be describable, comparable and persistable **without reference
to the application that generated it**, even where names, structure and semantics coincide.

The molecules, sites and bonds of a SpringSaLaD math description are math entities whose sole
purpose is to drive the solver. They are *not* the biological entities they were generated from.

### Corollary — everything the solver reads must be persisted

**[stated]** If the solver consumes a value, the math description must persist it. A stored math that
omits such a value is incomplete: reload it without regenerating from biology and it drives a
different simulation.

This settles cases that otherwise look like judgement calls. `is2D` is copied from
`SpeciesContextSpec` and written into the `.lngv`, so it must survive both serialization paths -
it was previously written to VCML but read back with `Boolean.getBoolean` (which reads a *system
property*, not the token, so it was always `false`), and omitted from the XML path entirely although
its tag was declared. Both are now fixed. Being derivable from biology is not a reason to omit it.

## P2 — Duplicate shared concepts into math; do not import them

**[stated, by precedent]** Where the math layer needs a concept that also exists in biology,
**duplicate it under a distinct name** rather than importing the biological type. The established
convention is a `Particle*` prefix:

| biological (`org.vcell.model.rbm`) | mathematical (`cbit.vcell.math`) |
|---|---|
| `MolecularType` | `ParticleMolecularType` |
| `MolecularComponent` | `ParticleMolecularComponent` |
| `SpeciesPattern` | `ParticleSpeciesPattern` |
| `MolecularComponentPattern.BondType` | `ParticleMolecularComponentPattern.ParticleBondType` |

The convention extends to **enums**, not just classes — `ParticleBondType` duplicates `BondType`
constant for constant. Accepting two definitions is the price of keeping the layers separable.

A second tier marks framework-specific extensions: `Langevin*` extends the shared `Particle*` types
with SpringSaLaD physics.

## P3 — Every scalar quantity in a math description is an `Expression`

**[stated]** Even where math generation only ever produces a floating-point literal, and even where
the eventual solver needs only a literal. Such a quantity may later be linked to a `Constant` symbol
even if generation does not do that today.

```
(biological entity as Double)  →  (math entity as Expression)  →  (solver entity as double)
```

That chain is correct and expected. It preserves the nature of the math layer across every
mathematical framework — ODE, PDE, stochastic, NFSim, particle Brownian dynamics, SpringSaLaD.

The layer is already uniform this way: `ParticleProperties`, `MacroscopicRateConstant`,
`JumpProcess`, `PdeEquation`, `Constant` and `Function` carry **no raw `double` fields at all**.

Consequences:

- **Comparison follows expression rules**, via `ExpressionUtils.ExpressionEquivalencePredicate` —
  not `Double.compare`. See P6.
- **Serialization writes the infix, unflattened.** Old documents hold float literals, new documents
  hold expressions that happen to be float literals, and both read back. Do not flatten on write.
- **The solver evaluates.** See P5.

## P4 — Solver input formats are not the math layer's concern

**[stated]** A math class must not know how to write a solver's input file. The solver format
belongs to the solver wrapper, which takes the `MathDescription` as input.

A `writeXxx(StringBuilder)` method on a math class that emits solver syntax is a violation, and it
is also how the math layer ends up importing the solver package.

## P5 — Translate at the boundaries, and fail loudly at the solver one

**[stated]** The math mapping converts biological values into math expressions. The solver wrapper
converts math expressions into whatever the solver needs, typically with
`flatten().evaluateConstant()`.

**If a value does not resolve to a number, the solver writer must fail loudly.** Writing a wrong
number would produce a silently wrong simulation.

**[inferred]** Translate on **value**, not on ordinal or identity: the enum translation in
`LangevinMathMapping.toMath(...)` matches on the VCML wire name, so reordering or renaming a
constant on either side fails at the boundary instead of silently mis-mapping.

## P6 — Three comparison outcomes, with three different consequences

**[stated]** Comparing two math descriptions has three outcomes, and each costs the user something
different:

| outcome | test | consequence |
|---|---|---|
| **identical** | `compareEqual` | the math must be saved to the database, or the edit is lost |
| **equivalent** | `testEquivalency` | recorded as the same as the parent in `SimulationVersion.parentSimulationReference`; existing results stay valid and visible |
| **not equivalent** | `testEquivalency` | the parent branch is cleared on save, and existing results are not shown, because they no longer correspond to the math |

This is what makes the distinction load-bearing rather than cosmetic. An attribute that is persisted
but never reaches the solver — site colour — must be a difference for **identical** (or the edit is
never saved) and must *not* be a difference for **equivalent** (or a cosmetic edit discards the
user's results).

### Comparison is by exact class

**[stated]** A subclass never compares equal to its plain parent: the guard is
`getClass().equals(obj.getClass())`, not `instanceof`.

With `instanceof` on the parent and a stricter test on the subclass, `plain.compareEqual(langevin)`
was true while `langevin.compareEqual(plain)` was false - and since `Compare.isEqual(List,List)`
only ever evaluates `v1[i].compareEqual(v2[i])`, list comparison silently depended on argument
order. Beyond the asymmetry, a SpringSaLaD math and an NFSim math that happen to share molecule
names are statements for different solvers and should not compare equal.

Safe because the class is decided by the document: `ParticleMolecularTypeTag` reads back plain,
`LangevinParticleMolecularTypeTag` reads back Langevin, so the type round-trips stably. There is no
workflow in which an application changes mathematical framework between saves.

### Structural relationships are compared as stored

**[stated]** Internal links between sites are compared **directed** - `(A,B)` is not `(B,A)` - because
springs are written to the solver input in stored order (`LINK: Site n ::: Site m`), and the
orientation is preserved from the biological link through both serialization paths. Comparison is by
site *name* rather than object identity, so set iteration order cannot manufacture a difference.

### Equivalence idioms actually used

**[inferred]** Equivalence is not one mechanism. Four are in use, and they are chosen by what kind
of difference is being absorbed:

1. **Compare for equivalence directly.** `ExpressionUtils.functionallyEquivalent(exp1, exp2)` —
   1e-9 relative, 1e-12 absolute, zero special-cased, flatten-and-retry. Called throughout
   `MathDescription`'s comparison path, and *inside* several `compareEqual` implementations via
   `ExpressionEquivalencePredicate` passed to `Compare.isEqual(a, b, predicate)`. Note that
   `Expression.compareEqual` itself is literal (normalized infix), so equivalence is opt-in per field.
2. **A lenient `compareEqual` overload.** `compareEqual(Matchable, boolean bIgnoreX)` — the
   `bIgnoreMissingDomain(s)` family on `VolumeParticleVariable`, `FilamentVariable`, `PointVariable`,
   `MembraneRegionVariable`, `VolumeParticleSpeciesPattern`, `ConvolutionDataGenerator`.
3. **Normalize, re-compare, restore in `finally`.** For a systematic transformation of a whole
   quantity that a tolerance cannot absorb: KMOLE-scaled rates (powers 1–2), a missing symmetry
   factor defaulted to 1.0. `compareEqual` stays the single definition of equality; only the input
   is adjusted.
4. **Estimate the transformation numerically, then compare for equivalence.**
   `MathUtilities.compareEquivalent(FastInvariant, FastInvariant)` — a conserved quantity scaled by a
   constant is still conserved, so it probes with a **fixed seed** (`new Random(0)`, 5 successful
   trials of at most 500), averages the ratio, scales, then calls `functionallyEquivalent`.

Supporting habits, applied consistently: **match by name, not position**; **filter no-ops before
counting** (`filter(pjp -> !pjp.actionsNoop())`); and **give each known-benign difference its own
`Decision`** (`MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS`) so it is visible rather than
silently accepted.

## Known violations

Listed so they are not mistaken for precedent. Each is tracked as an explicit exception in
`MathNamespaceSeparationTest`.

| violation | nature |
|---|---|
| `MathRuleFactory` → `org.vcell.model.rbm.RuleAnalysis` and its `*Entry` interfaces | a genuinely shared contract with one adapter per layer (`ModelRuleFactory` / `MathRuleFactory`), whose interfaces are merely *declared* in a biological package. A package move, not a redesign. |
| `MathRuleFactory` → `RbmUtils` | `RbmUtils` carries paired biological/math overloads for every BNGL conversion; roughly half of a 2191-line class in a biological package is really a math-side BNGL writer. |
| `SiteAttributesSpec.writeType`, `MolecularInternalLinkSpec.writeLink` | a parallel `.lngv` export from the **biological** layer; same violation on a different axis, and apparently a deliberate second export path. |

`LangevinParticleMolecularComponent.writeType` / `.writeSite` were previously listed here: `.lngv`
syntax emitted from inside math, and the only reason `cbit.vcell.math` imported `org.vcell.solver.*`.
Both have moved to `LangevinLngvWriter`, and `MathNamespaceSeparationTest` now forbids
`org.vcell.solver.*` in math as well as the biological packages, so the rule is enforced in both
directions.

## See also

- [springsalad-abstractions.md](springsalad-abstractions.md) — the SpringSaLaD pipeline in detail
- [nfsim-abstractions.md](nfsim-abstractions.md) — the NFSim pipeline, and how the two compare
