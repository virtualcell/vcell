import vcellapi
import sys

def main(argv):
    if (len(argv)!=5):
        print("usage: testapi host port clientID userid")
        sys.exit(1)
         
    host = argv[1]           # "vcellapi.cam.uchc.edu"
    port = argv[2]           # 8080
    clientID = argv[3]       # "85133f8d-26f7-4247-8356-d175399fc2e6"
    userid = argv[4]         # schaff
    password = raw_input("password ")

    api = vcellapi.VCellApi(host,port,clientID)
    api.authenticate(userid,password)
    biomodels = api.getBiomodels(vcellapi.BiomodelsQuerySpec())
    for model in biomodels:
        print("model name = "+model.name);
        for app in model.applications:
            print("   app name = "+app.name)
        for sim in model.simulations:
            print("   sim name = "+sim.name)


    bFirstSimulationToStartStop = True;
    if (len(biomodels)>0):
        # test /biomodel/[bmkey]
        print(" ... re-fetching first biomodel ...");
        biomodelsQuerySpec = vcellapi.BiomodelsQuerySpec()
        biomodelsQuerySpec.owner = userid
        firstBiomodel = api.getBiomodels(biomodelsQuerySpec)[0];
        print("biomodel : "+firstBiomodel.bmKey+" : "+firstBiomodel.name);
        for app in firstBiomodel.applications:
            print("   app : "+app.name)

        for sim in firstBiomodel.simulations:
            print("   sim (returned with BioModel) : "+sim.key+" : "+sim.name);

            # test /biomodel/[bmkey]/simulation/simkey
            simulation = api.getSimulation(firstBiomodel.bmKey, sim.key);
            print("   sim (retrieved separately) : "+simulation.key+" : "+simulation.name);

            if (bFirstSimulationToStartStop):
                bFirstSimulationToStartStop = False;
                # test /biomodel/[bmkey]/simulation/[simkey]/startSimulation
                simTasksQuerySpec = vcellapi.SimulationTasksQuerySpec();
                simTasksQuerySpec.simId = sim.key;
                beforeStartSimTasks = api.getSimulationTasks(simTasksQuerySpec);
                print("SENDING START SIMULATION");
                justAfterStartSimTasks = api.startSimulation(firstBiomodel.bmKey, sim.key);
                print("SENT START SIMULATION");

                print("WAITING 5 seconds");
                sleep(5)
                longAfterStartSimTasks = api.getSimulationTasks(simTasksQuerySpec);

                print("SENDING STOP SIMULATION");
                justAfterStopSimTasks = api.stopSimulation(firstBiomodel.bmKey, sim.key);
                print("SENT STOP SIMULATION");

                print("WAITING 5 seconds");
                sleep(5)
                longAfterStopSimTasks = api.getSimulationTasks(simTasksQuerySpec);

                print("\n\nsimulation status:");
                for simTask in beforeStartSimTasks:
                    print("    BEFORE START Job = "+simTask.jobIndex+", Task = "+simTask.taskId+", Status = "+simTask.status);

                for simTask in justAfterStartSimTasks:
                    print("    JUST AFTER START Job = "+simTask.jobIndex+", Task = "+simTask.taskId+", Status = "+simTask.status);

                for simTask in longAfterStartSimTasks:
                    print("    LONG AFTER START Job = "+simTask.jobIndex+", Task = "+simTask.taskId+", Status = "+simTask.status);

                for simTask in justAfterStopSimTasks:
                    print("    JUST AFTER STOP Job = "+simTask.jobIndex+", Task = "+simTask.taskId+", Status = "+simTask.status);

                for simTask in longAfterStopSimTasks:
                    print("    LONG AFTER STOP Job = "+simTask.jobIndex+", Task = "+simTask.taskId+", Status = "+simTask.status);

                print("\n\n");

            print("\n");

    # test /simtask
    simTasks = vcellApiClient.getSimulationTasks(vcellapi.SimulationTasksQuerySpec());
    for simTask in simTasks:
        print("simTask : "+simTask.simKey+" : "+simTask.simName);

    print("done")




if __name__ == "__main__":
   main(sys.argv)