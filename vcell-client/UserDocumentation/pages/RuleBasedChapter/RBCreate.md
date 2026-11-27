# How to create a rule-based model

There are several ways to create a [rule-based](../Appendix/Definitions.md#rule-based) model:

**Import** an existing rule-based model encoded in BNGL format, using
[File->Import menu](../Introduction/ImportBNGL.md). The rule-based model will be created with two
applications: a [network-free](../Appendix/Definitions.md#networkfree) application called **NFSim**,
and a [deterministic](../Appendix/Definitions.md#deterministic) network application called
**BioNetGen**. If some features in a BNGL file are not supported by VCell, a pop-up window appears
with suggestions for correcting unsupported features. If features in a BNGL file (e.g. specifying a
fixed concentration value) are supported by [BioNetGen](../Appendix/Definitions.md#bionetgen) but
not by [NFSim](../Appendix/Definitions.md#nfsim), only a single deterministic rule-based application
will be created.

**Create** a new rule-based model or **convert** an existing single-compartment reaction network
model into a rule-based model by adding rule-based features using the
[Physiology](../Physiology/PhysOverview.md) tabs.

To create a new rule-based model, the user has to perform the following operations:

- Create one or more [Molecules](../Physiology/Molecules.md) - structured objects with binding sites
  having optional attribute states
- Create one or more [Species](../Physiology/SpeciesTabView.md) that are made of molecules
  (optionally) connected through binding sites. These composite species will serve as seed species
  for either network generation or network-free simulation.
- Create one or more [reaction rules](../Appendix/Definitions.md#reaction-rules) (specified in the
  [Reactions](../Physiology/ReactTabView.md) Table) that describe transformations of species classes
  by specifying initial and final states of molecules participating in the reaction.
- Create one or more [Observables](../Physiology/Observables.md) that describe classes of species
  with specified features, e.g. having the same site in a phosphorylated state.
