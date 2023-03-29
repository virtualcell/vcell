package org.vcell.cli.run.hdf5;

import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;

import org.jlibsedml.DataGenerator;
import org.jlibsedml.Variable;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Stream;

public class SimpleDataGenCalculator {
    private final double EMPTY_VALUE = Double.NaN;
    private Expression equation;
    private Map<String, Double> bindingMap;

    public SimpleDataGenCalculator(DataGenerator dataGen) throws ExpressionException {
        this.equation = new Expression(dataGen.getMathAsString());
        this.bindingMap = new LinkedHashMap<>(); // LinkedHashMap preserves insertion order

        String[] variableArray = dataGen.getListOfVariables().stream().map(Variable::getId).toArray(String[]::new);
        SymbolTable symTable = new SimpleSymbolTable(variableArray);
        this.equation.bindExpression(symTable);

        for (String var : variableArray){
            bindingMap.put(var, this.EMPTY_VALUE);
        }
    }

    public void setArgument(String parameter, Double argument){
        if (!this.bindingMap.containsKey(parameter)) throw new IllegalArgumentException(String.format("\"%s\" is not a parameter of the expression", parameter));
        this.bindingMap.put(parameter, argument);
    }

    public void setArguments(Map<String, Double> parameterToArgumentMap){
        for (String param : parameterToArgumentMap.keySet()){
            this.bindingMap.put(param, parameterToArgumentMap.get(param));
        }
    }

    public double evaluateWithCurrentArguments(boolean shouldClear) throws ExpressionException, DivideByZeroException {
        Double[] args = this.bindingMap.values().toArray(new Double[0]);
        double answer = this.equation.evaluateVector(Stream.of(args).mapToDouble(Double::doubleValue).toArray());
        if (shouldClear) for (String key : this.bindingMap.keySet()) this.bindingMap.put(key, EMPTY_VALUE);
        return answer;
    }

    public double evaluateWithProvidedArguments(Map<String, Double> parameterToArgumentMap) throws ExpressionException, DivideByZeroException {
        List<Double> args = new LinkedList<>();

        // Confirm we have the correct params
        if (parameterToArgumentMap.size() != this.bindingMap.size()) throw new IllegalArgumentException("Incorrect number of entries.");
        if (!parameterToArgumentMap.keySet().equals(this.bindingMap.keySet())) throw new IllegalArgumentException("Parameter 'keys' don't match");

        // Prepare args
        for (String param : this.bindingMap.keySet()){ // binding map, because keys are set-similar but only binding map preserves order for sure!
            args.add(parameterToArgumentMap.get(param));
        }

        // Solve
        return this.equation.evaluateVector(args.stream().mapToDouble(Double::doubleValue).toArray());
    }
}
