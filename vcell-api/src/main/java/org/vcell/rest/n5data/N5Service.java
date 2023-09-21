package org.vcell.rest.n5data;

import cbit.vcell.math.MathException;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.n5.N5Exporter;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.janelia.saalfeldlab.n5.Compression;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/*
 Code for export

 Tracking, asynchronous, logging

 */

public class N5Service {

    Logger lg = LogManager.getLogger(N5Service.class);


    public N5Service() {

    }


    public boolean exportToN5(String[] species, String simID, Compression compression, User user) throws IOException, DataAccessException, MathException {
        N5Exporter n5Exporter = new N5Exporter();
        String locationOfSimData = "/media/zeke/DiskDrive/Home/Work/CCAM/Repos/vcell/vcell-core/src/test/resources/simdata/n5";
        String pathToVCellSolvers = "/media/zeke/DiskDrive/App_Installations/VCell_Rel";
        n5Exporter.initalizeDataControllers(locationOfSimData, simID, pathToVCellSolvers, user.getName(), user.getID().toString());
        ArrayList<DataIdentifier> dataIdentifiers = new ArrayList<>();
        for(String specie: species){
            dataIdentifiers.add(n5Exporter.getSpecificDI(specie));
        }
        n5Exporter.exportToN5("/home/zeke/Downloads/apiTest.n5", dataIdentifiers);
        return true;
    }



}

