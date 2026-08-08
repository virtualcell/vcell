# NFSim: biological, mathematical and solver abstractions

How a rule-based BioModel application becomes a `MathDescription` and then an NFSim
input, which types live in which layer, and how the layering compares with SpringSaLaD.

Companion to [springsalad-abstractions.md](springsalad-abstractions.md); the two share
their math-layer vocabulary. The comparison is in
[Compare and contrast](#compare-and-contrast-nfsim-vs-springsalad), which is the point
of the pair.

Established by reading the code on `master` as of 2026-08-08; see
[Appendix](#appendix-how-to-re-derive-these-facts).

## The two rule-based paths

A rule-based application can be simulated two ways, and only the second is NFSim:

| path | transformer | outcome |
|---|---|---|
| **network-generated** | `NetworkTransformer` | rules expanded into an explicit reaction network via BioNetGen, then solved by ODE/stochastic solvers |
| **network-free** | `RulebasedTransformer` | rules kept as rules, solved directly by **NFSim** |

Both are `SimContextTransformer`s operating on the application before math generation.
This document follows the network-free path, because that is the one whose math
description carries rules rather than reactions — the structural analogue of SpringSaLaD's.

## Layers

| layer | package | role |
|---|---|---|
| biological | `cbit.vcell.mapping`, `cbit.vcell.model`, `org.vcell.model.rbm` | application, reaction rules, species patterns, observables |
| mathematical | `cbit.vcell.math` | `MathDescription` with `Particle*` entities |
| solver | `org.vcell.solver.nfsim` | `NFsimXMLWriter` emits the NFSim XML input |

## The `Particle*` duplication, applied thoroughly

NFSim is where the math layer's duplication pattern was established. Every rule-based
biological entity has a math twin under a `Particle*` prefix — see the table in the
SpringSaLaD document. What matters here is that the duplication was applied **all the way
down, including enums**:

| biological | mathematical |
|---|---|
| `MolecularComponentPattern.BondType` | `ParticleMolecularComponentPattern.ParticleBondType` |

```java
// org.vcell.model.rbm.MolecularComponentPattern        // cbit.vcell.math.ParticleMolecularComponentPattern
public enum BondType {                                  public enum ParticleBondType {
    Specified(""),                                          Specified(""),
    Exists("+"),                                            Exists("+"),
    Possible("?"),                                          Possible("?"),
    None("-");                                              None("-");
```

Same constants, same symbols, different namespace. The math layer does not import the
biological enum — it owns an equivalent one.

**This is the single most useful precedent in either document.** SpringSaLaD's outstanding
violation is precisely the case NFSim already solved: `LangevinParticleJumpProcess` imports
`ReactionRuleSpec.Subtype` and `ReactionRuleSpec.TransitionCondition` from the mapping layer
instead of owning math-side equivalents. `ParticleBondType` is the template to copy. The
biological `BondType` even documents the correspondence in its own comments —
`Possible("?")` is SpringSaLaD's `TransitionCondition.NONE`, `None("-")` is its `FREE`,
`Specified("")` is its `BOUND` — so the two vocabularies are known to be related.

## Biological → mathematical

`cbit.vcell.mapping.RulebasedMathMapping` emits math entities:

```
ParticleMolecularType / ParticleMolecularComponent    <- molecular types and sites
ParticleComponentStateDefinition / ...StatePattern    <- component states
ParticleMolecularTypePattern / ...ComponentPattern    <- reactant and product patterns
ParticleJumpProcess                                   <- a rule
VolumeParticleSpeciesPattern / VolumeParticleVariable <- species and state
VolumeParticleObservable                              <- observables
ParticleProperties / ParticleInitialConditionCount    <- initial conditions
```

Note it emits the plain `ParticleJumpProcess`, where SpringSaLaD emits the
`LangevinParticleJumpProcess` subclass. The shared base carries rules; the SpringSaLaD
subclass adds Langevin-specific physics (subtype, transition condition, bond length).

## Mathematical → solver

`org.vcell.solver.nfsim.NFsimXMLWriter` is **math-driven**, like its Langevin counterpart.
Its internals take `MathDescription` directly:

```java
private static Element getListOfObservables(MathDescription mathDesc)
private static Element getListOfSpecies(MathDescription mathDesc, SimulationSymbolTable ...)
private static Element getListOfMoleculeTypes(MathDescription mathDesc)
private static Element getListOfParameters(MathDescription mathDesc, SimulationSymbolTable ...)
```

It never obtains a `SimulationContext` or `BioModel`; it works from
`clonedSimTask.getSimulation().getMathDescription()` plus a `SimulationSymbolTable`.

### The `RuleAnalysis` bridge — a shared abstraction, not an inversion

Writing NFSim XML requires analysing each rule (mapping reactant/product molecules and
bonds, deriving IDs). That algorithm is `org.vcell.model.rbm.RuleAnalysis`, and it is
**static and interface-driven**:

```java
public static RuleAnalysisReport analyze(RuleEntry rule, boolean bThrowExceptions)
public static Element getNFSimXML(RuleEntry rule, RuleAnalysisReport report)
```

Two adapters implement those interfaces, one per layer:

| adapter | package | wraps |
|---|---|---|
| `ModelRuleFactory.ModelRuleEntry` | `cbit.vcell.model` | a biological `ReactionRule` |
| `MathRuleFactory.MathRuleEntry` | `cbit.vcell.math` | a math `ParticleJumpProcess` |

with matching `*ParticipantEntry`, `*MolecularTypeEntry` and `*MolecularComponentEntry`
pairs. `NFsimXMLWriter` builds the math adapter and runs the shared algorithm:

```java
MathRuleFactory mathRuleFactory = new MathRuleFactory();
RuleAnalysisReport report = RuleAnalysis.analyze(rule, true);
Element reactionRuleElement = RuleAnalysis.getNFSimXML(rule, report);
```

This is a deliberate strategy pattern — one algorithm, two representations — **not** a
layering inversion. The only defect is where the contract is declared: `RuleEntry` and
friends live in `org.vcell.model.rbm`, a biological package, so the math adapter must
import from biology to implement them. Moving the interfaces (and `RuleAnalysis` itself,
which touches no concrete biological type) to a neutral or math package removes the
violation and leaves both adapters working unchanged.

> **Correction to an earlier assessment.** This was initially judged deep structural
> surgery on the grounds that a math class implements a biological interface. Finding the
> parallel `ModelRuleFactory` changes that: the interfaces are a shared contract, and the
> fix is a package move, not a redesign.

## Math comparison — implemented, and the template for SpringSaLaD

Where SpringSaLaD's comparison was never written, NFSim's is complete and unusually
sophisticated. Every shared `Particle*` type implements `compareEqual` with real field
comparisons — no stubs:

| type | `Compare.*` calls |
|---|---|
| `ParticleProperties` | 11 |
| `ParticleJumpProcess` | 10 |
| `ParticleObservable`, `ParticleMolecularComponentPattern` | 4 |
| `ParticleMolecularType`, `ParticleMolecularComponent`, `ParticleMolecularTypePattern` | 3 |
| `ParticleSpeciesPattern` | 2 |
| `ParticleComponentStateDefinition`, `ParticleComponentStatePattern` | 1 |

`MathCompareResults.Decision` carries five particle-specific outcomes, all raised from
`MathDescription.compareEquivalentCanonicalMath`:

- `MathDifferent_DIFFERENT_PARTICLE_PROPERTIES`
- `MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS`
- `MathDifferent_DIFFERENT_PARTICLE_JUMP_PROCESS`
- `MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS`
- `MathDifferent_LEGACY_SYMMETRY_PARTICLE_JUMP_PROCESS`

The comparison does four things worth copying:

1. **Filters no-op processes** — jump processes with only trivial actions (open→open) are
   dropped before comparing, so a cosmetic difference in process count is not a difference.
2. **Normalises missing data** — a missing symmetry factor is treated as the trivial 1.0.
3. **Matches out of order** — processes are matched by name, then compared, so emission
   order does not matter. (Contrast the positional `Compare.isEqual(List,List)`.)
4. **Tolerates known legacy divergence, and says so** — a rate differing only by the
   `KMOLE` unit-conversion factor, or only by symmetry factor, gets its own `LEGACY_*`
   decision instead of a bare "different", so old stored maths are explainable rather
   than merely unequal.

That is exactly the shape SpringSaLaD needs, and it means the Langevin work is mostly
**extending a working design to a subclass**, not inventing one.

`MathGenCompareTest` does exercise the rule-based path (SpringSaLaD has no coverage there
at all).

## Layering violations in the NFSim path

After the separation pass, everything in `cbit.vcell.math` is clean except one file,
`MathRuleFactory`, which holds the **only remaining violations in the math namespace** —
nine imports, and they are all the same thing: shared contracts declared in a biological
package.

| contract | declared in | math adapter | biological adapter |
|---|---|---|---|
| `RuleAnalysis` + its `*Entry` interfaces | `org.vcell.model.rbm` | `MathRuleFactory` | `cbit.vcell.model.ModelRuleFactory` |
| `RbmUtils` BNGL conversion | `org.vcell.model.rbm` | `toBnglString(Particle*)` overloads | `toBnglString(MolecularType, ...)` overloads |

`RuleAnalysis` is already layer-neutral in substance: it imports **no concrete biological
type** — only the JDK, log4j, jdom and its own `RuleAnalysisReport`. It is a static
algorithm over interfaces, with one adapter per layer.

`RbmUtils` is the same pattern, less obviously. It carries **paired overloads** throughout,
one biological and one math, for every BNGL conversion:

```java
toBnglString(ComponentStatePattern)      /  toBnglString(ParticleComponentStatePattern)
toBnglString(MolecularType, Model, ...)  /  toBnglString(ParticleMolecularType)
toBnglStringShort(ReactionRule, ...)     /  toBnglStringShort(ParticleJumpProcess, ...)
```

Eight such pairs. So roughly half of a 2191-line class in a biological package operates
purely on math types — a math-side BNGL writer that never got separated out.

### Why this is deferred rather than done

Resolving it is well-defined but is a different piece of work from the SpringSaLaD
separation, in a different subsystem:

- moving `RuleAnalysis` + `RuleAnalysisReport` to a neutral package rewrites **~36 import
  statements across 8 files**, four of them GUI classes in `vcell-client`
  (`RulesShapePanel`, `ParticipantSignatureShapePanel`, and others);
- splitting `RbmUtils` means extracting the math-typed overloads into a math-side BNGL
  writer and updating their callers.

Neither changes behaviour, and neither is needed for the SpringSaLaD math comparison. Both
are recorded as explicit exceptions in `MathNamespaceSeparationTest`, so they stay visible
and cannot be quietly added to.

## Compare and contrast: NFSim vs SpringSaLaD

Both pipelines have the same shape — application → `MathDescription` → solver input writer —
and both writers are genuinely math-driven at runtime, obtaining no `SimulationContext` or
`BioModel`. They diverge in how completely the separation was carried through, and the
failure modes are near mirror images.

| | NFSim | SpringSaLaD |
|---|---|---|
| bio → math | `RulebasedMathMapping` | `LangevinMathMapping` |
| math entities | shared `Particle*` | `Langevin*` subclasses of `Particle*` |
| solver writer | `NFsimXMLWriter` | `LangevinLngvWriter` |
| writer input | `MathDescription` | `MathDescription` |
| enums | **duplicated** into math (`ParticleBondType`) | **imported** from mapping (`Subtype`, `TransitionCondition`) |
| `compareEqual` | fully implemented | **`if(false)` stubs** |
| equivalence tier | particle-aware, order-insensitive, legacy-tolerant | degenerates to identity |
| regression coverage | present in `MathGenCompareTest` | none |
| remaining violation | shared contract declared in a biological package | biological vocabulary imported into math |

The contrast is sharpest on **vocabulary**. Faced with the same problem — a math entity
needing a concept that also exists in biology — NFSim duplicated the enum into the math
namespace and accepted two definitions to keep the layers clean. SpringSaLaD imported the
biological enum and kept one definition, coupling the layers. The user-facing consequence
is invisible until you try to compare two maths, at which point NFSim's answer is correct
and SpringSaLaD's is silently wrong.

The failure modes are complementary rather than similar:

- **NFSim's** violation is *architectural bookkeeping* — the design is right, the contract
  is declared in the wrong package. Nothing behaves incorrectly.
- **SpringSaLaD's** violation is *behavioural* — nine solver-relevant fields are never
  compared, so genuinely different maths report as identical, which can leave stale
  simulation results attached to an edited model.

That asymmetry suggests the order of work: SpringSaLaD's stubs are a correctness bug and
should be fixed on their own merits; the namespace cleanups in both pipelines are a
separate, lower-risk pass. And because NFSim already solved every design question
SpringSaLaD faces — enum duplication, order-insensitive matching, legacy tolerance,
per-difference decisions — the SpringSaLaD work is largely transcription rather than
invention.

## Appendix: how to re-derive these facts

```bash
# the enum duplication that is the template for SpringSaLaD
grep -n "enum ParticleBondType" -A 8 vcell-core/src/main/java/cbit/vcell/math/ParticleMolecularComponentPattern.java
grep -n "enum BondType"         -A 6 vcell-core/src/main/java/org/vcell/model/rbm/MolecularComponentPattern.java

# RuleAnalysis is a shared contract: two adapters, one per layer
grep -rn "implements RuleEntry\|implements ParticipantEntry" --include='*.java' vcell-core/src/main

# the NFSim writer is math-driven
grep -nE "MathDescription|SimulationContext|getBioModel" \
  vcell-core/src/main/java/org/vcell/solver/nfsim/NFsimXMLWriter.java

# particle-aware equivalence decisions and where they are raised
grep -rn "MathDifferent_.*PARTICLE" --include='*.java' vcell-core/src/main
```
