# SpringSaLaD: biological, mathematical and solver abstractions

How a SpringSaLaD BioModel application becomes a `MathDescription` and then a Langevin
solver input, which types live in which layer, and where the layering is currently
violated.

This is the first of a planned pair. A companion document will describe the same
pipeline for **NFSim** (rule-based) applications, after which the two can be compared
directly. The two pipelines share their math-layer vocabulary, so the comparison is
not academic — see [The `Particle*` duplication pattern](#the-particle-duplication-pattern).

Everything below was established by reading the code on `master` as of 2026-08-08;
[Appendix: how to re-derive these facts](#appendix-how-to-re-derive-these-facts) gives
the commands, because several of these claims are the kind that quietly rot.

## The separation principle

> The molecules, sites and bonds of the SpringSaLaD **math description** are math-only
> entities, defined in the math namespace, whose sole purpose is to drive the Langevin
> solver. They are *not* the biological entities of the application (SimulationContext)
> that generated them, even where names, structure and semantics coincide.

The layers, and the packages that own them:

| layer | package | role |
|---|---|---|
| biological | `cbit.vcell.mapping`, `cbit.vcell.model`, `org.vcell.model.rbm` | what the user edits: application (SimulationContext), species context specs, reaction rule specs |
| mathematical | `cbit.vcell.math` | `MathDescription`: solver-facing, self-contained, comparable, persisted as VCML |
| solver | `org.vcell.solver.langevin` | writes the `.lngv` input consumed by the LangevinNoVis01 solver |

Dependencies must point **downward only**: biological → mathematical → solver. The
reverse (math reaching into biology) is the violation this document tracks.

The direction is already well established in the codebase: **24 files under
`cbit/vcell/mapping/` import `cbit.vcell.math.*`**. So moving a shared type *down* into
the math namespace is always safe; it is the existing grain of the code, not a new idea.

## The `Particle*` duplication pattern

The math layer does not reuse the rule-based biological entities. It **duplicates** them
under a `Particle*` prefix — deliberately, so that the two can never be confused:

| biological (`org.vcell.model.rbm`) | mathematical (`cbit.vcell.math`) |
|---|---|
| `MolecularType` | `ParticleMolecularType` |
| `MolecularComponent` | `ParticleMolecularComponent` |
| `ComponentStateDefinition` | `ParticleComponentStateDefinition` |
| `MolecularTypePattern` | `ParticleMolecularTypePattern` |
| `MolecularComponentPattern` | `ParticleMolecularComponentPattern` |
| `ComponentStatePattern` | `ParticleComponentStatePattern` |
| `SpeciesPattern` | `ParticleSpeciesPattern` |
| (observables) | `ParticleObservable`, `VolumeParticleObservable` |

This pattern is the precedent, and it is what makes the rest of this document actionable:
**the SpringSaLaD violations below are not a missing design, they are an incompletely
applied existing one.** Where a Langevin math class needs a biological concept, the
answer is already known — duplicate it into the math namespace with a distinct name.

SpringSaLaD's own math types extend the shared `Particle*` base classes:

- `LangevinParticleMolecularType extends ParticleMolecularType`
- `LangevinParticleMolecularComponent extends ParticleMolecularComponent`
- `LangevinParticleJumpProcess extends ParticleJumpProcess`

### The naming convention, and how to extend it

Two tiers are already in use, and they should be stated explicitly because the remaining
work is simply to finish applying them:

| tier | prefix | meaning |
|---|---|---|
| shared math vocabulary | `Particle*` | math-namespace twin of a rule-based biological entity, used by both NFSim and SpringSaLaD |
| SpringSaLaD-specific math | `Langevin*` | extends the shared tier with Langevin physics |

The convention already reaches enums on the NFSim side —
`rbm.MolecularComponentPattern.BondType` has the math twin
`math.ParticleMolecularComponentPattern.ParticleBondType`, same constants and symbols,
no import from biology. SpringSaLaD is the one place the convention was not carried
through: `LangevinParticleJumpProcess` imports `ReactionRuleSpec.Subtype` and
`ReactionRuleSpec.TransitionCondition` from the mapping layer rather than owning
equivalents.

Applying the convention gives the missing types their names directly — for example
`LangevinParticleJumpProcess.ParticleTransitionCondition` and
`...ParticleJumpProcessSubtype`, or free-standing `math` enums if other Langevin types
come to need them. Whichever shape is chosen, two constraints hold:

- **VCML wire names must not change.** `subtype.columnName` and
  `transitionCondition.vcellName` are what gets written and parsed
  (`LangevinParticleJumpProcess.java:74-76`, and `Subtype.fromName(...)` on read), so the
  math-side enum must reproduce those strings exactly or stored models stop loading.
- **The mapping layer translates.** `LangevinMathMapping` converts the biological enum to
  the math one at generation time; the dependency stays biological → mathematical.

The biological `BondType` comments already document how the two vocabularies correspond
(`Possible("?")` is SpringSaLaD's `TransitionCondition.NONE`, `None("-")` is `FREE`,
`Specified("")` is `BOUND`), which is a useful cross-check when writing the translation —
and a warning that the terminology is genuinely confusing in this area.

## Biological → mathematical

`cbit.vcell.mapping.LangevinMathMapping` is the translator. It reads the application
(species context specs, reaction rule specs, model parameters, the rule-based
transformation) and emits math entities:

```
LangevinParticleMolecularType        <- molecular type + its 2D flag
LangevinParticleMolecularComponent   <- site: radius, diffusion rate, location, coordinate, color
LangevinParticleJumpProcess          <- reaction: subtype, transition condition, bond length
ParticleProperties / ParticleInitialConditionCount   <- initial conditions
VolumeParticleVariable / VolumeParticleObservable    <- state and outputs
ParticleMolecularTypePattern / ...Pattern            <- reactant and product patterns
```

The biological originals (`LangevinSpeciesContextSpec`, `ReactionRuleSpec`,
`SpeciesContextSpec`, `SiteAttributesSpec`) carry attributes with **near-identical names**
to their math counterparts — radius, diffusion rate, location, color. This is the main
hazard when working in this area: a plausible-looking `getRadius()` may belong to either
layer, and only the package distinguishes them.

## Mathematical → solver

`org.vcell.solver.langevin.LangevinLngvWriter` writes the `.lngv` input file.

**At runtime it is genuinely math-driven.** It is constructed from a `SimulationJob` and
reads only through `simulationJob.getSimulation().getMathDescription()` — geometry,
subdomains, particle molecular types. It never obtains a `SimulationContext` or a
`BioModel`. `MathDescription.isLangevin()` gates the write.

So the *data flow* already honours the separation principle. What does not is the
**vocabulary**: the writer references biological classes purely for compile-time
constants and enum types —

- `SpeciesContextSpec.SourceMoleculeString`, `.SinkMoleculeString`, `.AnchorSiteString`, `.TrackClusters`
- `Structure.SpringStructureEnum.Intracellular` / `.Extracellular` (used to validate geometry names)
- `ReactionRuleSpec.Subtype`, `ReactionRuleSpec.TransitionCondition` (the types carried by the math objects)

This distinction matters for planning: no biological object graph is traversed, so the fix
is relocating shared vocabulary rather than restructuring dataflow.

## Layering violations in `cbit.vcell.math`

18 biological imports across 5 files. Five are dead on arrival.

| # | file | imports | verdict |
|---|---|---|---|
| 1 | `LangevinParticleJumpProcess` | `ReactionRuleSpec` | **stale** (referenced only by a commented-out field) — deleted |
| 2 | `LangevinParticleMolecularComponent` | `cbit.vcell.model.Structure` | **stale** — deleted |
| 3 | `LangevinParticleMolecularComponent` | `rbm.ComponentStateDefinition` | **stale** — deleted |
| 4 | `ParticleMolecularTypePattern` | `MolecularTypePattern.TRIVIAL_MATCH` | one string constant |
| 5 | `MathDescription` | `AbstractMathMapping.*_SUFFIX` | naming-convention constants |
| 6 | `MathDescription` | `VCellErrorMessages` | message catalog; mechanical |
| 7 | `MathDescription` | `Model.ReservedSymbol` | live — used by `updateReservedSymbols(...)` |
| 8 | `LangevinParticleJumpProcess` | `Subtype`, `TransitionCondition` | **value enums** — math-side equivalents |
| 9 | `MathRuleFactory` | `RuleAnalysis`, `RuleEntry`, `*Entry`, `ParticipantType`, bond entries, `RbmUtils` | shared contract declared in a biological package — move the contract |

> **Counting caution.** An earlier revision of this table listed `Model.ReservedSymbol` and
> `RuleAnalysis`/`RuleEntry` as stale. They are not — `ReservedSymbol` appears in the
> signature of `MathDescription.updateReservedSymbols`, and `MathRuleEntry implements
> RuleEntry`. The error came from counting "body references" as *matching lines minus one
> for the import*: for a **nested** import such as `RuleAnalysis.RuleEntry`, the import line
> does not match an identifier pattern anchored on non-identifier characters (the preceding
> `.` is excluded), so subtracting one discarded a real use. Decide staleness by grepping
> for the symbol and excluding lines that *begin with* `import`, never by arithmetic on
> counts. The compiler caught this immediately; a `grep`-only review would not have.

Notes on the two that are not mechanical:

**#9 is the SpringSaLaD one that matters.** `subtype` and `transitionCondition` define what a
Langevin jump process *is*, and they are written to VCML by name. Following the `Particle*`
precedent, they want math-side enums with byte-identical VCML wire names, with the mapping
layer translating at generation time. Watch out: `ReactionRuleSpec` contains a **duplicate
nested `TransitionCondition`** (`ReactionRuleSpec.java:464`) with the same constants as the
top-level one (`:110`), so confirm which the math path actually binds to before moving anything.

**#10 is not SpringSaLaD at all** — it is rule-based/NFSim territory. `MathRuleFactory`'s
inner classes *implement* biological contracts (`MathRuleEntry implements RuleEntry`,
`MathParticipantEntry implements ParticipantEntry`, and so on), which looks like a layering
inversion. It is not: there is a parallel `cbit.vcell.model.ModelRuleFactory` implementing
the same interfaces over biological objects, so `RuleAnalysis` is a deliberate shared
contract — one algorithm, one adapter per layer. The only defect is that the interfaces are
declared in `org.vcell.model.rbm`; moving them to a neutral package removes the violation
and leaves both adapters unchanged. See
[nfsim-abstractions.md](nfsim-abstractions.md#the-ruleanalysis-bridge--a-shared-abstraction-not-an-inversion),
where this is analysed in full.

There is **no ArchUnit dependency and no architecture test** in the repo, so nothing prevents
these from reappearing via an IDE auto-import. A guard test that fails the build when
`cbit.vcell.math` imports `cbit.vcell.model`, `cbit.vcell.mapping`, `cbit.vcell.biomodel` or
`org.vcell.model` — with any accepted exception listed explicitly — is what makes "zero
violations" durable rather than a one-off cleanup.

## Math comparison: identical / equivalent / differences

VCell needs to compare two `MathDescription`s and decide whether they are identical,
equivalent, or different with a list of reasons. For SpringSaLaD, **this was never
implemented.**

The plumbing is all present and does reach the Langevin types:

```
MathDescription.compareEqual        -> particleMolecularTypes, subDomainList
  SubDomain.compareEqual0           -> listOfParticleProperties, listOfParticleJumpProcesses
    LangevinParticleMolecularType / ...Component / ...JumpProcess
```

and then stops, because all three Langevin overrides are stubs:

```java
LangevinParticleMolecularComponent other = (LangevinParticleMolecularComponent)obj;
if(false) {			// TODO: compare everything that needs comparing
    return false;
}
return super.compareEqual(obj);
```

`if(false)` is dead code and `other` is never read, so the subclass contributes nothing and
every SpringSaLaD-specific field is invisible:

| class | fields never compared |
|---|---|
| `LangevinParticleMolecularType` | `is2D` |
| `LangevinParticleMolecularComponent` | `fieldRadius`, `fieldDiffusionRate`, `fieldLocation`, `fieldCoordinate`, `fieldColor` |
| `LangevinParticleJumpProcess` | `subtype`, `transitionCondition`, `bondLength` |

**Consequence.** Two SpringSaLaD maths differing only in a site's radius, diffusion rate,
location or coordinate, or a reaction's bond length, subtype or transition condition,
compare as *identical*. That is the dangerous direction — a false "nothing changed".
`Simulation.compareEqual` (`Simulation.java:320`) and `MathModel.compareEqual`
(`MathModel.java:179`) both depend on this, so editing a site's diffusion rate may fail to
invalidate existing simulation results.

Four further problems in the same area:

**Comparison is asymmetric.** Each Langevin override guards `instanceof Langevin…`, but the
parent `ParticleMolecularType.compareEqual` accepts *any* `ParticleMolecularType`. So
`plain.compareEqual(langevin)` can be true while `langevin.compareEqual(plain)` is false.
`Compare.isEqual(List,List)` only ever evaluates `v1[i].compareEqual(v2[i])`, so the answer
depends on argument order. This outlives the stub fix and must be fixed with it.

**`MathCompareResults` cannot express a list of differences.** It carries one `Decision`, one
`details` string, and two variable-name lists. Reporting "here are the differences" needs an
additive change to the result type — `Decision` and `toDatabaseStatus()` must keep working,
since the decision strings are persisted.

**The "equivalent" tier degenerates.** `testEquivalency` runs invariants → `compareEqual` →
`MathUtilities.getCanonicalMathDescriptions`, and that canonicalisation expands and flattens
*equations over state variables*. SpringSaLaD math carries essentially no equations — its
content is molecular types, jump processes and observables — so flattening is a no-op and
"equivalent" collapses onto "identical". Equivalence for Langevin needs a different
definition: order-insensitive matching keyed on names, with a tolerance policy for doubles.

**Ordering.** `Compare.isEqual(List,List)` is positional. If two generations emit molecular
types or jump processes in a different order, the comparison reports differences that are not
real.

Two further cautions before enabling any of this:

- **Round-trip fidelity is a prerequisite.** There are two read paths — the VCML tokenizer
  (`LangevinParticleMolecularComponent.read`) and `XmlReader` (`:2540`, `:7985`, `:8036`).
  If any field is written but not read back, then the moment comparison starts looking at that
  field, previously-"identical" maths will begin reporting differences after save/reload.
- **`isLangevin()` is fragile.** It decides from the first `ParticleJumpProcess` it encounters,
  so a SpringSaLaD model with no reactions has no `LangevinParticleJumpProcess` at all and is
  classified non-Langevin.

`MathGenCompareTest` currently contains **no SpringSaLaD coverage** — no reference to
`langevin` or `springsalad` anywhere in it.

## Open questions

- Should `fieldColor` (`org.vcell.util.springsalad.NamedColor`) count as a math difference?
  It is a display attribute that does not drive the solver, yet it is written to VCML.
- Does relocating `Subtype`/`TransitionCondition` into the math namespace belong with the
  comparison fix, or as a separate change? Bundling turns a correctness fix into a refactor
  that touches VCML/XML read-write and the mapping layer.
- Guard test as a plain JUnit source-import check (no new dependency) or ArchUnit (cleaner,
  adds a test-scoped dependency)?

## Appendix: how to re-derive these facts

```bash
# biological imports inside the math namespace
grep -rnE "^import (cbit\.vcell\.(model|mapping|biomodel)|org\.vcell\.model)\." \
  vcell-core/src/main/java/cbit/vcell/math/

# the stubs
grep -rn "TODO: compare everything that needs comparing" --include='*.java' .

# what the solver writer actually consumes
grep -nE "SimulationContext|getBioModel|MathDescription" \
  vcell-core/src/main/java/org/vcell/solver/langevin/LangevinLngvWriter.java

# the legal direction, already in use
grep -rlE "^import cbit\.vcell\.math\." vcell-core/src/main/java/cbit/vcell/mapping/ | wc -l
```

Distinguishing a stale import from a real one needs care: a substring grep for
`ComponentStateDefinition` also matches `ParticleComponentStateDefinition`, which is the
math-side type. Anchor the pattern on non-identifier characters
(`(^|[^A-Za-z0-9_.])Type([^A-Za-z0-9_]|$)`) and subtract the import line itself.
