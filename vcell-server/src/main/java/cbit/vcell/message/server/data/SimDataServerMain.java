package cbit.vcell.message.server.data;

import cbit.vcell.resource.LibraryLoaderThread;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.sf.ehcache.search.expression.GreaterThanOrEqual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.dependency.server.VCellServerModule;

import java.lang.annotation.Native;

public class SimDataServerMain {
    public static final Logger lg = LogManager.getLogger(SimDataServerMain.class);

    Injector injector;

    private SimDataServerMain(Injector injector) {
        this.injector = injector;
    }

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                lg.info("Missing arguments: " + SimDataServer.class.getName() + " (CombinedData | ExportDataOnly | SimDataOnly)");
                System.exit(1);
            }
            SimDataServer.SimDataServiceType simDataServiceType = SimDataServer.SimDataServiceType.valueOf(args[0]);
            if (simDataServiceType == null) {
                lg.info("Invalid argument: " + SimDataServer.class.getName() + " (CombinedData | ExportDataOnly | SimDataOnly)");
                System.exit(1);
            }


            PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);
            lg.debug("properties loaded");
            new LibraryLoaderThread(false).start( );

            Injector injector = Guice.createInjector(new VCellServerModule());

            SimDataServer simDataServer = injector.getInstance(SimDataServer.class);
            simDataServer.init(simDataServiceType);

            try{
                NativeLib.HDF5.load();
            } catch(Exception e){
                lg.error("Unable to load HDF-Group HDF5 Library", e);
            }

        } catch (Exception e) {
            lg.error("VCellApiMain failed", e);
            System.exit(1);
        }
    }

    private static final String REQUIRED_SERVICE_PROPERTIES[] = {
            PropertyLoader.primarySimDataDirInternalProperty,
            PropertyLoader.primarySimDataDirExternalProperty,
            PropertyLoader.n5DataDir,
            PropertyLoader.s3ProxyExternalPort,
            PropertyLoader.s3ExportBaseURLProperty,
            PropertyLoader.vcellServerIDProperty,
            PropertyLoader.installationRoot,
            PropertyLoader.mongodbHostInternal,
            PropertyLoader.mongodbPortInternal,
            PropertyLoader.mongodbDatabase,
            PropertyLoader.jmsIntHostInternal,
            PropertyLoader.jmsIntPortInternal,
            PropertyLoader.jmsUser,
            PropertyLoader.jmsPasswordFile,
            PropertyLoader.pythonExe,
            PropertyLoader.jmsBlobMessageUseMongo,
            PropertyLoader.exportBaseURLProperty,
            PropertyLoader.exportBaseDirInternalProperty,
            PropertyLoader.simdataCacheSizeProperty,
            PropertyLoader.vcellapiKeystoreFile,
            PropertyLoader.vcellapiKeystorePswdFile
    };


}
