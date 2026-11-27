package org.vcell.api.dto;

import java.util.List;

/** values[point][time]; null where missing; non-finite as Double NaN/Infinity (JSON: "NaN", "Infinity", "-Infinity"). */
public record TimeSeries(String variable, List<Double> times, List<List<Double>> values) { }
