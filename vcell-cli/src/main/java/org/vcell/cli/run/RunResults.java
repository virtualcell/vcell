package org.vcell.cli.run;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.solver.TempSimulation;
import org.vcell.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunResults {
    private final Map<BioModel, List<TempSimulation>> bioModelToListOfTemp;
    private final Map<TempSimulation, Pair<Status, Integer>> tempSimulationToStatus;

    public RunResults() {
        this.bioModelToListOfTemp = new HashMap<>();
        this.tempSimulationToStatus = new HashMap<>();
    }

    public List<TempSimulation> getTempSimulations(BioModel bioModel) {
        return this.bioModelToListOfTemp.get(bioModel);
    }

    public Pair<Status, Integer> getStatus(BioModel bioModel, TempSimulation tempSimulation) {
        return this.bioModelToListOfTemp.containsKey(bioModel) ? null : tempSimulationToStatus.get(tempSimulation);
    }

    public void setStatus(BioModel bioModel, TempSimulation tempSimulation, Integer duration) {
        this.setStatus(bioModel, tempSimulation, null, duration);
    }

    public void setStatus(BioModel bioModel, TempSimulation tempSimulation, Status status) {
        this.setStatus(bioModel, tempSimulation, status, null);
    }

    public void setStatus(BioModel bioModel, TempSimulation tempSimulation, Status status, Integer duration){
        if (!this.bioModelToListOfTemp.containsKey(bioModel)) this.bioModelToListOfTemp.put(bioModel, new ArrayList<>());
        List<TempSimulation> relevantSims = this.bioModelToListOfTemp.get(bioModel);
        if (!relevantSims.contains(tempSimulation)) this.bioModelToListOfTemp.get(bioModel).add(tempSimulation);
        if (!this.tempSimulationToStatus.containsKey(tempSimulation)) {
            this.tempSimulationToStatus.put(tempSimulation, new Pair<>(status, duration));
        }
        Pair<Status, Integer> previousStatuses = this.tempSimulationToStatus.get(tempSimulation);
        if (status != null){
            this.tempSimulationToStatus.put(tempSimulation, new Pair<>(status, previousStatuses.two));
            previousStatuses = this.tempSimulationToStatus.get(tempSimulation);
        }

        if (duration != null){
            this.tempSimulationToStatus.put(tempSimulation, new Pair<>(previousStatuses.one, duration));
        }
    }

    public List<IndividualResult> generateRecordsOfResults(){
        List<IndividualResult> results = new ArrayList<>();
        for (BioModel bm : this.bioModelToListOfTemp.keySet()) {
            for (TempSimulation ts : this.bioModelToListOfTemp.get(bm)) {
                Pair<Status, Integer> statusAndDuration = this.tempSimulationToStatus.get(ts);
                results.add(new IndividualResult(bm, ts, statusAndDuration.one, statusAndDuration.two));
            }
        }
        return results;
    }

    public record IndividualResult(BioModel bioModel, TempSimulation tempSimulation, Status status, Integer duration) {}
}
