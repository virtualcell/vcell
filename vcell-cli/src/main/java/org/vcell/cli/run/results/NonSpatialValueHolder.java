package org.vcell.cli.run.results;

import cbit.vcell.solver.Simulation;

import java.util.ArrayList;
import java.util.List;

public class NonSpatialValueHolder {
    public final List<double[]> listOfResultSets = new ArrayList<>();
    final Simulation vcSimulation;

    public NonSpatialValueHolder(Simulation simulation) {
        this.vcSimulation = simulation;
    }

    public boolean isEmpty() {
        return listOfResultSets.isEmpty();
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
