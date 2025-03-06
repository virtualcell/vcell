package org.vcell.cli.run.results;

import cbit.vcell.solver.Simulation;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;

import java.util.ArrayList;
import java.util.List;

public class ValueHolder <T extends LazySBMLDataAccessor> {
    /**
     * The list of results, with each entry being a different run (only 1 for a single task, multiple for repeated tasks)
     */
    public final List<T> listOfResultSets;
    final Simulation vcSimulation;

    public ValueHolder(Simulation simulation) {
        this.vcSimulation = simulation;
        this.listOfResultSets = new ArrayList<>();
    }

    /**
     * Shallow Copy constructor
     * @param other
     */
    public ValueHolder(ValueHolder<? extends T> other) {
        this.vcSimulation = other.vcSimulation;
        this.listOfResultSets = new ArrayList<>(other.listOfResultSets);
    }

    public boolean isEmpty() {
        return this.listOfResultSets.isEmpty();
    }

    public ValueHolder<T> createEmptySetWithSameVCsim(){
        return new ValueHolder<>(this.vcSimulation);
    }

    // even though listOfResultsSets is public, the issue is providing a function reference to "string"
    public List<T> getListOfResultSets() {
        return this.listOfResultSets;
    }

    /*public int[] getJobCoordinate(int index){
        String[] names = vcSimulation.getMathOverrides().getScannedConstantNames();
        java.util.Arrays.sort(names); // must do things in a consistent way
        int[] bounds = new int[names.length]; // bounds of scanning matrix
        for (int i = 0; i < names.length; i++){
            bounds[i] = vcSimulation.getMathOverrides().getConstantArraySpec(names[i]).getNumValues() - 1;
        }
        int[] coordinates = BeanUtils.indexToCoordinate(index, bounds);
        return coordinates;
    }*/
}
