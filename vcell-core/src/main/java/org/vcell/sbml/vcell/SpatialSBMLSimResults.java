package org.vcell.sbml.vcell;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import org.jlibsedml.UniformTimeCourse;
import org.sbml.jsbml.SBase;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLSpatialDataAccessor;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class SpatialSBMLSimResults extends SBMLSimResults {
    protected final DataSetControllerImpl dataSetController;
    protected final VCSimulationIdentifier simId;
    protected final VCDataIdentifier vcDId;
    protected final double[] originalTimes;
    protected final CartesianMesh cartesianMesh;

    public SpatialSBMLSimResults(SimulationJob vcellSimJob, File userDir, SBMLSymbolMapping sbmlMapping, MathSymbolMapping mathMapping) throws DataAccessException, MathException, IOException {
        super(sbmlMapping, mathMapping); // All spatial solvers don't need interpolation
        try {
            this.dataSetController = SBMLSimResults.initializeDataSetController(userDir);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found: `" + userDir.getAbsolutePath() + "`", e);
        }
        this.simId = SpatialSBMLSimResults.initializeSimulationIdentifier(vcellSimJob, userDir);

        this.vcDId = new VCSimulationDataIdentifier(this.simId, vcellSimJob.getJobIndex());

        this.originalTimes = this.dataSetController.getDataSetTimes(this.vcDId);
        this.cartesianMesh = this.dataSetController.getMesh(this.vcDId);
    }

    @Override
    public double[] getOriginalTimes() {
        return this.originalTimes;
    }

    @Override
    public int getMaxDataFlatLength() {
        return this.cartesianMesh.getSizeZ() * this.cartesianMesh.getSizeY() * this.cartesianMesh.getSizeX() * this.originalTimes.length;
    }

    @Override
    public List<Integer> getSpatialDimensions(){
        return List.of(this.cartesianMesh.getSizeZ(), this.cartesianMesh.getSizeY(), this.cartesianMesh.getSizeX());
    }

    @Override
    public LazySBMLDataAccessor getSBMLDataAccessor(String sbmlId, UniformTimeCourse utcSim) {
        String key = SpatialSBMLSimResults.createUniqueKey(sbmlId, utcSim);
        if (this.lazyAccessorMapping.containsKey(key))return this.lazyAccessorMapping.get(key);
        LazySBMLSpatialDataAccessor newAccessor = new LazySBMLSpatialDataAccessor(this.generateCallable(sbmlId, utcSim), this.getMaxDataFlatLength(), this.getSpatialDimensions(), Arrays.stream(this.getDesiredTimes(utcSim)).boxed().toList());
        this.lazyAccessorMapping.put(key, newAccessor);
        return newAccessor;
    }

    @Override
    protected Callable<SBMLDataRecord> generateCallable(String vcellVarId, UniformTimeCourse utcSim){
        return new Callable<>() {
            /**
             * Access upon request the data desired from the appropriate
             *
             * @return computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            public SBMLDataRecord call() throws Exception {
                return SpatialSBMLSimResults.this.getDataForSBMLVar(vcellVarId, utcSim);
            }
        };
    }

    protected static VCSimulationIdentifier initializeSimulationIdentifier(SimulationJob vcellSimJob, File userDir){
        User user = new User(userDir.getName(), null);
        cbit.vcell.solver.Simulation vcellSim = vcellSimJob.getSimulation();
        return new VCSimulationIdentifier(vcellSim.getKey(), user);
    }

    public SBMLDataRecord getDataForSBMLVar(String sbmlId, UniformTimeCourse utcSim) throws IOException, DataAccessException, ExpressionException {
        DataOperation dataOperation = new DataOperation.DataProcessingOutputInfoOP(this.vcDId,true, this.DEFAULT_OUTPUT_CONTEXT);
        DataOperationResults.DataProcessingOutputInfo results = (DataOperationResults.DataProcessingOutputInfo) this.dataSetController.doDataOperation(dataOperation);
        Set<String> varNamesSet = new HashSet<>(Arrays.asList(results.getVariableNames()));

        DataIdentifier[] dataIdentifiers = this.dataSetController.getDataIdentifiers(this.DEFAULT_OUTPUT_CONTEXT, this.vcDId);
        Set<String> identifierSet = Arrays.stream(dataIdentifiers).map(DataIdentifier::getName).collect(Collectors.toSet());
        identifierSet.addAll(varNamesSet);

        double[] times = this.getDesiredTimes(utcSim);
        List<Integer> spatialDimensions = List.of(this.cartesianMesh.getSizeZ(), this.cartesianMesh.getSizeY(), this.cartesianMesh.getSizeX());

        SBase sBase = this.sbmlMapping.getMappedSBase(sbmlId);
        if (sBase == null) throw new DataAccessException("Cannot find SBase for " + sbmlId);

        SymbolTableEntry runtimeSte = this.sbmlMapping.getSte(sBase, SymbolContext.RUNTIME);
        SymbolTableEntry ste = (runtimeSte != null) ? runtimeSte : this.sbmlMapping.getSte(this.sbmlMapping.getMappedSBase(sbmlId), SymbolContext.INITIAL);
        if (ste instanceof Structure.StructureSize) ste = this.getStructureSizeSymbolTableEntry(ste, sbmlId);

        cbit.vcell.math.Variable mathVar = this.mathMapping.getVariable(ste);
        return this.getDataForSBMLVar(mathVar, ste, identifierSet, spatialDimensions, times, utcSim);
    }

    protected double[] getRawDataAtAllTimes(String varName) throws DataAccessException {
        int meshSize = this.cartesianMesh.getSizeZ() * this.cartesianMesh.getSizeY() * this.cartesianMesh.getSizeX();
        double[] originalTimes = this.getOriginalTimes();
        double[] originalData = new double[meshSize * originalTimes.length];
        for (int i = 0; i < originalTimes.length; i++) {
            SimDataBlock sdb = this.dataSetController.getSimDataBlock(this.DEFAULT_OUTPUT_CONTEXT, this.vcDId, varName, originalTimes[i]);
            double[] flatDataBlock = sdb.getData();
            System.arraycopy(flatDataBlock, 0, originalData, i * meshSize, flatDataBlock.length);
        }
        return originalData;
    }
}
