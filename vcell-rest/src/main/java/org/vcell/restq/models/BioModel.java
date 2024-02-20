package org.vcell.restq.models;

import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.parser.ExpressionException;
import org.vcell.util.document.User;

import java.util.ArrayList;

public record BioModel(
        String bmKey,
        String name,
        int privacy,
        String[] groupUsers,

        Long savedDate,

        String annot,

        String branchID,

        String modelKey,

        String ownerName,

        String ownerKey,

        Simulation[] simulations,

        Application[] applications
) {





        public String getBmKey() {
            return bmKey;
        }



        public String getName() {
            return name;
        }



        public int getPrivacy() {
            return privacy;
        }



        public String[] getGroupUsers() {
            return groupUsers;
        }



        public Long getSavedDate() {
            return savedDate;
        }



        public String getAnnot() {
            return annot;
        }



        public String getBranchID() {
            return branchID;
        }



        public String getModelKey() {
            return modelKey;
        }



        public String getOwnerName() {
            return ownerName;
        }



        public String getOwnerKey() {
            return ownerKey;
        }



//        public SimulationRepresentation[] getSimulations() {
//            return simulations;
//        }
//
//
//
//        public ApplicationRepresentation[] getApplications() {
//            return applications;
//        }



        public static BioModel fromBioModelRep(BioModelRep bioModelRep) throws ExpressionException {
            ArrayList<String> groupList = new ArrayList<>();
            for (User user: bioModelRep.getGroupUsers()){
                groupList.add(user.getName());
            }
            return new BioModel(bioModelRep.getBmKey().toString(), bioModelRep.getName(), bioModelRep.getPrivacy(),
                    groupList.toArray(new String[groupList.size()]), bioModelRep.getDate().getTime(), bioModelRep.getAnnot(), bioModelRep.getBranchID().toString(),
                    bioModelRep.getModelRef().toString(), bioModelRep.getOwner().getName(), bioModelRep.getOwner().getID().toString(), null, null);
//            this.bmKey = bioModelRep.getBmKey().toString();
//            this.name = bioModelRep.getName();
//            this.privacy = bioModelRep.getPrivacy();
//
//            ArrayList<String> groupList = new ArrayList<String>();
//            for (User user : bioModelRep.getGroupUsers()) {
//                groupList.add(user.getName());
//            }
//            this.groupUsers = groupList.toArray(new String[groupList.size()]);
//
//            this.savedDate = bioModelRep.getDate().getTime();
//
//            this.annot = bioModelRep.getAnnot();
//            this.branchID = bioModelRep.getBranchID().toString();
//            this.modelKey = bioModelRep.getModelRef().toString();
//            this.ownerName = bioModelRep.getOwner().getName();
//            this.ownerKey = bioModelRep.getOwner().getID().toString();

//            ArrayList<SimulationRepresentation> simulationList = new ArrayList<SimulationRepresentation>();
//            for (SimulationRep simRep : bioModelRep.getSimulationRepList()){
//                simulationList.add(new SimulationRepresentation(simRep,bioModelRep));
//            }
//            this.simulations = simulationList.toArray(new SimulationRepresentation[simulationList.size()]);
//
//            ArrayList<ApplicationRepresentation> applicationList = new ArrayList<ApplicationRepresentation>();
//            for (SimContextRep simContextRep : bioModelRep.getSimContextRepList()){
//                applicationList.add(new ApplicationRepresentation(simContextRep));
//            }
//            this.applications = applicationList.toArray(new ApplicationRepresentation[applicationList.size()]);
        }

}
