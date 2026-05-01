package org.vcell.cli.run.hdf5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.components.Variable;

import java.util.*;

public class Hdf5DataSourceSpatialSimMetadata {
    private final static Logger lg = LogManager.getLogger(Hdf5DataSourceSpatialSimVars.class);

    private final Set<String> speciesNames;
    private int[] timeBounds;
    private double[] times;
    private int[] spaceTimeDimensions;
    private double[][] scanTargetValues;
    private String[] scanTargets;

    private int[] scanTargetUpperInclusiveBound;

    public Hdf5DataSourceSpatialSimMetadata(){
        this.speciesNames = new HashSet<>();
    }

    public boolean addSpecies(String name){
        boolean hasBeenOverwritten= this.speciesNames.contains(name);
        this.speciesNames.add(name);
        return hasBeenOverwritten;
    }

    public void addAllSpecies(Set<String> species){
        this.speciesNames.addAll(species);
    }

    public boolean containsSpecies(Variable species){
        return this.speciesNames.contains(species.getName()) || "t".equals(species.getName());
    }

    public boolean containsAllRequestedSpecies(List<Variable> allSpecies){
        for (Variable species : allSpecies){
            if (!this.containsSpecies(species)) return false;
        }
        return true;
    }

    public void validateTimeBounds(int[] timeBounds){
        this.timeBounds= timeBounds;
    }

    public void validateTimes(double[] times){
        this.times = times;
    }

    public void validateSpaceTimeDimensions(int[] spaceTimeDimensions){
        if (this.spaceTimeDimensions == null) this.spaceTimeDimensions = spaceTimeDimensions;
        else if (0 != Arrays.compare(this.spaceTimeDimensions, spaceTimeDimensions))
            throw new IllegalArgumentException("Conflicting space-times were detected within the same simulation.");
    }

    public void validateScanBounds(int[] scanBounds){
        this.scanTargetUpperInclusiveBound = scanBounds;
    }

    public void validateScanValues(double[][] scanTargetValues){
        this.scanTargetValues = scanTargetValues;
    }

    public void validateScanTargets(String[] scanTargets){
        this.scanTargets = scanTargets;
    }

    public int[] getSpaceTimeDimensions(){
        return this.spaceTimeDimensions;
    }

    public int getNumSpaceTimeDimensions(){
        return this.spaceTimeDimensions.length;
    }

    public boolean containsSpecies(String key){
        return this.speciesNames.contains(key);
    }

    public int[] getTimeBounds() {
        return this.timeBounds;
    }

    public double[] getTimes() {
        return this.times;
    }

    public int[] getScanBounds(){
        return this.scanTargetUpperInclusiveBound;
    }

    public double[][] getScanValues(){
        return this.scanTargetValues;
    }

    public String[] getScanTargets(){
        return this.scanTargets;
    }

    public boolean hasEquivalentShapeTo(Hdf5DataSourceSpatialSimMetadata otherSimMetadata){
        return this.speciesNames.equals(otherSimMetadata.speciesNames)
                && Arrays.equals(this.spaceTimeDimensions, otherSimMetadata.spaceTimeDimensions)
                && Arrays.equals(this.times, otherSimMetadata.times);
    }

    // Do we need thi still?
    private void fillMissingTimeDimensions(String vcellVarId){
        if (this.spaceTimeDimensions != null || !"t".equals(vcellVarId)) return;
        this.spaceTimeDimensions = new int[]{1, this.times.length};
    }
}
