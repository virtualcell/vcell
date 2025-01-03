// Code generated by jtd-codegen for Java + Jackson v0.2.1

package org.vcell.optimization.jtd;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CopasiOptimizationMethodOptimizationMethodType {
    @JsonProperty("SRES")
    SRES,

    @JsonProperty("evolutionaryProgram")
    EVOLUTIONARY_PROGRAM,

    @JsonProperty("geneticAlgorithm")
    GENETIC_ALGORITHM,

    @JsonProperty("geneticAlgorithmSR")
    GENETIC_ALGORITHM_SR,

    @JsonProperty("hookeJeeves")
    HOOKE_JEEVES,

    @JsonProperty("levenbergMarquardt")
    LEVENBERG_MARQUARDT,

    @JsonProperty("nelderMead")
    NELDER_MEAD,

    @JsonProperty("particleSwarm")
    PARTICLE_SWARM,

    @JsonProperty("praxis")
    PRAXIS,

    @JsonProperty("randomSearch")
    RANDOM_SEARCH,

    @JsonProperty("simulatedAnnealing")
    SIMULATED_ANNEALING,

    @JsonProperty("steepestDescent")
    STEEPEST_DESCENT,

    @JsonProperty("truncatedNewton")
    TRUNCATED_NEWTON,
}
