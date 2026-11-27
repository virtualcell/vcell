# How to simulate a rule-based model

Once rule-based features are specified in the BioModel Physiology, several numerical methods can be
used to run simulations. As is the case with all Virtual Cell BioModels, a Rule-Based BioModel may
have multiple Applications, including [deterministic](../Appendix/Definitions.md#deterministic) and
[stochastic](../Appendix/Definitions.md#stochastic)
([rule-based](../Appendix/Definitions.md#rule-based)) applications; in addition, for rule-based
models [network-free](../Appendix/Definitions.md#networkfree) applications can be created. Each
Application, in turn, can have multiple Simulations, in which different numerical methods or
conditions are used.

**Rule-based Deterministic and Stochastic Applications**

When a [deterministic](../Appendix/Definitions.md#deterministic) or
[stochastic](../Appendix/Definitions.md#stochastic) application of a rule-based model is created,
the [BioNetGen](../Appendix/Definitions.md#bionetgen) network-generation algorithm is used to
generate the complete network of species and reactions that is obtained by applying the set of
reaction rules to the set of [seed species](../Appendix/Definitions.md#seed-species). Initial
amounts of seed species are defined in the
[Network](../BioModelApplications/Specifications/NetworkSpecifications.md) specification tab.
[Network](../BioModelApplications/Specifications/NetworkSpecifications.md) specification parameters
(number of iterations to apply rules and maximum number of molecules per species in the generated
network) are used to control the size of the network, which otherwise can become infinitely large.
The generated species and reactions can be viewed in the
[Network](../BioModelApplications/Specifications/NetworkSpecifications.md) or the
[Reaction Diagram](../Physiology/ReactDiagView.md) view.

After the network is generated, determiministic or stochastic
[simulations](../BioModelApplications/Simulations/simulations_overview.md) can be run as for any
Application.

**Network-Free Applications**

In [network-free](../Appendix/Definitions.md#networkfree) applications of a rule-based BioModel, a
network is not generated. Instead, the amounts of model [Observables](../Physiology/Observables.md)
and initially defined [species](../Physiology/SpeciesTabView.md) are simulated using the
[NFSim](../Appendix/Definitions.md#nfsim) engine. Simulation parameters are defined in the
[NFSim Simulation Editor](../BioModelApplications/Simulations/NFSimSimulationEditor.md).

**Rule-Based MathModel**

The MathModel for [deterministic](../Appendix/Definitions.md#deterministic) or
[stochastic](../Appendix/Definitions.md#stochastic) applications generated for rule-based models
have a standard VCell structure that includes the full list of species and reactions, and includes
observables defined by functions. The MathModel for
[rule-based](../Appendix/Definitions.md#rule-based) applications has a unique syntax.
