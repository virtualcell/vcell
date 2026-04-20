package org.vcell.cli.run.hdf5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.components.Variable;
import org.jlibsedml.components.dataGenerator.DataGenerator;

import java.util.*;

public class Hdf5DataSourceSpatialSimVars {
    private final static Logger lg = LogManager.getLogger(Hdf5DataSourceSpatialSimVars.class);
    private Map<DataGenerator, String> sedmlDatasetToVarName;

    private Map<String, SortedSet<Hdf5DataSourceSpatialVarDataLocation>> varNameToSetOfLocations;

    //Constructors
    public Hdf5DataSourceSpatialSimVars(){
        this(new TreeMap<>());
    }

    public Hdf5DataSourceSpatialSimVars(Map<String, List<Hdf5DataSourceSpatialVarDataLocation>> varNameToListOfLocations){
        this.varNameToSetOfLocations = convertMapToUseLinkedHashSet(varNameToListOfLocations);
        this.sedmlDatasetToVarName = new HashMap<>();
    }

    // Getters / Setters
    public Set<DataGenerator> getVariableSet(){
        return this.sedmlDatasetToVarName.keySet();
    }

    public Map<String, List<Hdf5DataSourceSpatialVarDataLocation>> getVariableToLocationsMapping(){
        return convertMapToUseArrayList(this.varNameToSetOfLocations);
    }

    public void setVariableToLocationsMapping(Map<String, List<Hdf5DataSourceSpatialVarDataLocation>> variableToLocationsMapping){
        this.varNameToSetOfLocations = convertMapToUseLinkedHashSet(variableToLocationsMapping);
    }

    public List<Hdf5DataSourceSpatialVarDataLocation> getLocations(String varName){
        if (this.varNameToSetOfLocations.containsKey(varName)) return convertSetToArrayList(this.varNameToSetOfLocations.get(varName));
        throw new IllegalArgumentException(String.format("`%s` is an invalid species name; not located in variable mappings.", varName));
    }

    /**
     * Returns a list of the SpatialVarDataItems that pertain to the species referenced by the sedmlVariable.
     * <br\>
     * If the simulation referenced is not a parameter scan, the list will be of size 1.
     * @param sedmlVariable the variable to poll the values of
     * @return a list of all spatial results for a given species, across all parameter scans.
     */
    public List<Hdf5DataSourceSpatialVarDataLocation> getLocations(Variable sedmlVariable){
        return this.getLocations(sedmlVariable.getName());
    }

    public List<Hdf5DataSourceSpatialVarDataLocation> getLocations(DataGenerator dataGen){
        return this.getLocations(this.parseDataGen(dataGen));
    }

    public void setLocations(String varName, List<Hdf5DataSourceSpatialVarDataLocation> locations){
        this.varNameToSetOfLocations.put(varName, convertListToTreeSet(locations));
    }

    public void setLocations(Variable sedmlVariable, List<Hdf5DataSourceSpatialVarDataLocation> locations){
        this.setLocations(sedmlVariable.getName(), locations);
    }

    public void setLocations(DataGenerator dataGen, List<Hdf5DataSourceSpatialVarDataLocation> locations){
        this.setLocations(this.parseDataGen(dataGen), locations);
    }

    // Data Storage Helper methods
    public boolean containsVariable(String variableName){
        return this.varNameToSetOfLocations.containsKey(variableName);
    }

    public boolean containsVariable(Variable sedmlVariable){
        return this.containsVariable(sedmlVariable.getName());
    }

    public boolean containsVariable(DataGenerator dataGen){
        return this.containsVariable(this.parseDataGen(dataGen));
    }

    public void addLocation(String variableName, Hdf5DataSourceSpatialVarDataLocation newLocation){
        if (!this.containsVariable(variableName)) this.setLocations(variableName, new ArrayList<>());
        this.varNameToSetOfLocations.get(variableName).add(newLocation);
    }

    public void addLocation(Variable variable, Hdf5DataSourceSpatialVarDataLocation newLocation){
        this.addLocation(variable.getName(), newLocation);
    }

    public void addLocation(DataGenerator dataGen, Hdf5DataSourceSpatialVarDataLocation newLocation){
        this.addLocation(this.parseDataGen(dataGen), newLocation);
    }

    public void removeLocation(String variableName, Hdf5DataSourceSpatialVarDataLocation location){
        if (!this.containsVariable(variableName)) return;
        this.getLocations(variableName).remove(location);
    }

    public void removeLocation(Variable variable, Hdf5DataSourceSpatialVarDataLocation location){
        this.removeLocation(variable.getName(), location);
    }

    public void removeLocation(DataGenerator dataGen, Hdf5DataSourceSpatialVarDataLocation location){
        this.removeLocation(this.parseDataGen(dataGen), location);
    }

    public void integrateSimilarLocations(Hdf5DataSourceSpatialSimVars similarVars){
        for (String var : similarVars.varNameToSetOfLocations.keySet()){
            if (!this.containsVariable(var)) continue;
            // We bypass the list transformations, this is desired behavior anyway.
            this.varNameToSetOfLocations.get(var).addAll(similarVars.varNameToSetOfLocations.get(var));
        }
    }

    public int getMaxNumScans(){
        int max = 0;
        for (SortedSet<Hdf5DataSourceSpatialVarDataLocation> locations : this.varNameToSetOfLocations.values()){
            if (locations.size() > max) max = locations.size();
        }
        return max;
    }

    // Data Access Methods
    public double[] getFlatDataForAllVars(){
        List<Double> flatData = new LinkedList<>();
        for (DataGenerator dataGen : this.getVariableSet()){
            for (Hdf5DataSourceSpatialVarDataLocation dataItem : this.getLocations(dataGen)){
                flatData.addAll(Arrays.stream(dataItem.getSpatialDataFlat()).boxed().toList());
            }
        }
        return flatData.stream().mapToDouble(Double::doubleValue).toArray();
    }

    // Helper Methods
    private static <T> SortedSet<T> convertListToTreeSet(List<T> listToConvert){
        return new TreeSet<>(listToConvert);
    }

    private static <T> ArrayList<T> convertSetToArrayList(Set<T> setToConvert){
        return new ArrayList<>(setToConvert);
    }

    private static <K, V> Map<K, SortedSet<V>> convertMapToUseLinkedHashSet(Map<K, ? extends List<V>> mapToConvert){
        Map<K, SortedSet<V>> convertedMap = new HashMap<>();
        for (K key : mapToConvert.keySet()) convertedMap.put(key, convertListToTreeSet(mapToConvert.get(key)));
        return convertedMap;
    }

    private static <K, V> Map<K, List<V>> convertMapToUseArrayList(Map<K, ? extends Set<V>> mapToConvert){
        Map<K, List<V>> convertedMap = new HashMap<>();
        for (K key : mapToConvert.keySet()) convertedMap.put(key, convertSetToArrayList(mapToConvert.get(key)));
        return convertedMap;
    }

    /**
     * **PRIVATE**
     * Parses the incoming data set, throwing an exception if a multi-variable dataset is used, and maps the data set for future use.
     * @return a string id of the underlying desired variable id.
     */
    private String parseDataGen(DataGenerator dataGen){
        if (this.sedmlDatasetToVarName.containsKey(dataGen)) return this.sedmlDatasetToVarName.get(dataGen);
        if (dataGen.getVariables().size() != 1) throw new IllegalArgumentException("Multi-variable generator detected.");
        this.sedmlDatasetToVarName.put(dataGen, dataGen.getVariables().get(0).getName());
        return this.sedmlDatasetToVarName.get(dataGen);
    }
}


