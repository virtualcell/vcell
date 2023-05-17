package org.vcell.sbml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class SBMLSimulationSpec {

    public final double start;
    public final double duration;
    public final int steps;
    public final String[] variables;
    public final double absoluteTolerance;
    public final double relativeTolerance;
    public final String[] amountVars;
    public final String[] concentrationVars;

    public SBMLSimulationSpec(double start, double duration, int steps, String[] variables,
                              double absoluteTolerance, double relativeTolerance,
                              String[] amountVars, String[] concentrationVars) {
        this.start = start;
        this.duration = duration;
        this.steps = steps;
        this.variables = variables;
        this.absoluteTolerance = absoluteTolerance;
        this.relativeTolerance = relativeTolerance;
        this.amountVars = amountVars;
        this.concentrationVars = concentrationVars;
    }


    public static SBMLSimulationSpec fromSpecFile(String specText) {
        double start = 0;
        double duration = 5;
        int steps = 10;
        String[] variables = {};
        double absoluteTolerance = 0;
        double relativeTolerance = 0;
        String[] amountVars = {};
        String[] concentrationVars = {};

        /**
         * start: 0
         * duration: 5
         * steps: 50
         * variables: S1, S2
         * absolute: 1.000000e-007
         * relative: 0.0001
         * amount: S1, S2
         * concentration:
         */

        String[] lines = specText.split("\n");
        for (String line : lines) {
            StringTokenizer tokens = new StringTokenizer(line, ":, ");
            String command = tokens.nextToken();
            if (command.equals("start")) {
                start = Double.parseDouble(tokens.nextToken());
            } else if (command.equals("duration")) {
                duration = Double.parseDouble(tokens.nextToken());
            } else if (command.equals("steps")) {
                steps = Integer.parseInt(tokens.nextToken());
            } else if (command.equals("variables")) {
                variables = Collections.list(tokens).toArray(new String[0]);
            } else if (command.equals("absolute")) {
                absoluteTolerance = Double.parseDouble(tokens.nextToken());
            } else if (command.equals("relative")) {
                relativeTolerance = Double.parseDouble(tokens.nextToken());
            } else if (command.equals("amount")) {
                amountVars = Collections.list(tokens).toArray(new String[0]);
            } else if (command.equals("concentration")) {
                concentrationVars = Collections.list(tokens).toArray(new String[0]);
            } else {
                throw new RuntimeException("unexpected token '"+command+"' in line "+line);
            }
        }
        return new SBMLSimulationSpec(start, duration, steps, variables,
                absoluteTolerance, relativeTolerance, amountVars, concentrationVars);
    }
}
