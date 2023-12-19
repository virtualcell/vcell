package org.vcell.rest.n5data;

import cbit.vcell.math.MathException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.janelia.saalfeldlab.n5.Compression;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.IOException;
import java.util.ArrayList;

/*
 Code for export

 Tracking, asynchronous, logging

 */

public class N5Service {

    Logger lg = LogManager.getLogger(N5Service.class);

    private User user;
    private String simID;

    private final String locationOfSimData;


    public N5Service(String simID, User user) {
        this.simID = simID;
        this.user = user;
        this.locationOfSimData = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty);
    }


    public void exportToN5(String[] species, Compression compression) throws IOException, DataAccessException, MathException {
//        N5Exporter n5Exporter = new N5Exporter();
//        n5Exporter.initalizeDataControllers(simID, user.getName(), user.getID().toString());
//        ArrayList<DataIdentifier> dataIdentifiers = new ArrayList<>();
//        for(String specie: species){
//            dataIdentifiers.add(n5Exporter.getSpecificDI(specie));
//        }
//        n5Exporter.exportToN5(dataIdentifiers, compression);
        return;
    }


//    public ArrayList<String> supportedSpecies() throws IOException, DataAccessException {
//
//    }



}

