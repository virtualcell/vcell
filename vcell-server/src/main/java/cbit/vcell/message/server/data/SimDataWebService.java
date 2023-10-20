package cbit.vcell.message.server.data;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.Hdf5Utils;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import org.apache.commons.httpclient.URI;
import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.*;

public class SimDataWebService {
    public static final Logger lg = LogManager.getLogger(SimDataWebService.class);
    private HttpServer server;
    private DataServerImpl dataServerImpl = null;

    public SimDataWebService(DataServerImpl dataServerImpl) {
        this.dataServerImpl = dataServerImpl;
    }


    public void startWebService() {
        try (InputStream inputStream = new FileInputStream(new File(PropertyLoader.getRequiredProperty(PropertyLoader.vcellapiKeystoreFile)))) {
            final KeyStore serverKeyStore = KeyStore.getInstance("jks");
            String pwd = Files.readAllLines(new File(PropertyLoader.getRequiredProperty(PropertyLoader.vcellapiKeystorePswdFile)).toPath()).get(0);
            serverKeyStore.load(inputStream, pwd.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(serverKeyStore, pwd.toCharArray());
            KeyManager[] serverKeyManagers = keyManagerFactory.getKeyManagers();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(serverKeyStore);
            TrustManager[] serverTrustManagers = trustManagerFactory.getTrustManagers();
            final SSLContext sslContext = SSLContexts.createDefault();
            sslContext.init(serverKeyManagers, serverTrustManagers, new SecureRandom());
            int listenPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.webDataServerPort));
            server = ServerBootstrap.bootstrap().registerHandler("/simhdf5/*", new HttpRequestHandler() {
                @Override
                public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
                    try {
                        URI uri = new URI(request.getRequestLine().getUri(),true);
                        final List<NameValuePair> parse = URLEncodedUtils.parse(uri.getQuery(), Charset.forName("utf-8"));
                        lg.info(uri.getQuery());
                        lg.info(uri.getPath());
                        final Path path = Paths.get(uri.getPath());
                        final Iterator<Path> iterator = path.iterator();
                        final String SIMDATADDF5 = "simhdf5";
                        final String ODE = "ode";
                        String simdataRoute = iterator.next().toString();
                        String dataType = iterator.next().toString();
                        if(simdataRoute.toLowerCase().equals(SIMDATADDF5) && dataType.toLowerCase().equals(ODE)) {
                            String simID = null;
                            TreeSet<Integer> jobIDs = new TreeSet<Integer>();
                            String userKey = null;
                            String userid = null;
                            double blank = -1.0;
                            for(NameValuePair nvp:parse) {
                                if(nvp.getName().toLowerCase().equals("simid")) {
                                    simID = nvp.getValue();
                                }else if(nvp.getName().toLowerCase().equals("jobid")) {
                                    String jobStr = URLDecoder.decode(nvp.getValue(), "UTF-8");// integer Separated by commas
                                    StringTokenizer st = new StringTokenizer(jobStr,",");
                                    while(st.hasMoreElements()) {
                                        jobIDs.add(Integer.parseInt(st.nextToken()));
                                    }
                                }else if(nvp.getName().toLowerCase().equals("userkey")) {
                                    userKey = nvp.getValue();
                                }else if(nvp.getName().toLowerCase().equals("userid")) {
                                    userid = URLDecoder.decode(nvp.getValue(), "UTF-8");
                                }else if(nvp.getName().toLowerCase().equals("blank")) {
                                    blank = Double.parseDouble(nvp.getValue());
                                }
                            }
                            User user = new User(userid,new KeyValue(userKey));
                            VCSimulationIdentifier vcsid = new VCSimulationIdentifier(new KeyValue(simID), user);
                            File hdf5File = createHdf5(vcsid, jobIDs.toArray(new Integer[0]), blank);

                            String exportBaseURL = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseURLProperty);
                            URL url = new URL(exportBaseURL + hdf5File.getName());
                            response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
                            response.addHeader("Location",url.toString());
                            response.setEntity(null);
                            return;
                        }
                        response.setStatusCode(HttpStatus.SC_NOT_FOUND);
                        response.setEntity(new StringEntity("Not Found"));
                    } catch (Exception e) {
                        lg.error(e.getMessage(), e);
                        response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                        response.setEntity(new StringEntity(e.getMessage()));

                    }
                }
            }).setListenerPort(listenPort).setSslContext(sslContext).create();
            server.start();

        }catch(Exception e) {
            lg.error(e.getMessage(), e);
        }
    }

    private File createHdf5(VCSimulationIdentifier vcsid,Integer[] scanJobs,double blankCellValue/*for Histogram*/) throws Exception{
        File hdf5TempFile = null;
        int hdf5FileID = -1;
        int jobGroupID = -1;
        try {
            String exportBaseDir = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirInternalProperty);
            hdf5TempFile = File.createTempFile("webexport_"+vcsid.getSimulationKey()+"_", ".hdf", new File(exportBaseDir));
            hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
            Hdf5Utils.insertAttribute(hdf5FileID, "simID",vcsid.getSimulationKey().toString());//Hdf5Utils.writeHDF5Dataset(hdf5FileID, "simID", null,vcsid.getSimulationKey().toString() , true);
            Hdf5Utils.insertAttribute(hdf5FileID,"exportUser",vcsid.getOwner().getName()) ;//Hdf5Utils.writeHDF5Dataset(hdf5FileID, "exportUser", null,vcsid.getOwner().getName() , true);

            for(int scan=0;scan<scanJobs.length;scan++) {
                VCDataIdentifier vcdid = new VCSimulationDataIdentifier(vcsid, scanJobs[scan]);
                ODESimData odeSimData = dataServerImpl.getODEData(vcsid.getOwner() , vcdid);
                int allColumnsCount = odeSimData.getColumnDescriptionsCount();
                int allRowsCount = odeSimData.getRowCount();
                TreeSet<ColumnDescription> orderedColumnNames = new TreeSet<ColumnDescription>(new Comparator<ColumnDescription>() {
                    @Override
                    public int compare(ColumnDescription o1, ColumnDescription o2) {
                        if(o1.getName().equals("t") && o2.getName().equals("t")) {//'t' as first in the list
                            return 0;
                        }else if(o1.getName().equals("t")) {
                            return -1;
                        }else if(o2.getName().equals("t")) {
                            return 1;
                        }
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }});
                orderedColumnNames.addAll(Arrays.asList(odeSimData.getColumnDescriptions()));
                jobGroupID = (int) Hdf5Utils.createGroup(hdf5FileID, "Set "+scan);
                //writeHDF5Dataset(hdf5FileID, "Set "+scan, null, null, false);
                Hdf5Utils.HDF5WriteHelper help0 =  Hdf5Utils.createDataset(jobGroupID, "data", new long[] {allColumnsCount,allRowsCount});
                //(HDF5WriteHelper) Hdf5Utils.writeHDF5Dataset(jobGroupID, "data", new long[] {allColumnsCount,allRowsCount}, new Object[] {}, false);
                double[] fromData = new double[allColumnsCount*allRowsCount];
                int index = 0;
                ArrayList<String> dataTypes = new ArrayList<String>();
                ArrayList<String> dataIDs = new ArrayList<String>();
                ArrayList<String> dataShapes = new ArrayList<String>();
                ArrayList<String> dataLabels = new ArrayList<String>();
                ArrayList<String> dataNames = new ArrayList<String>();
                Iterator<ColumnDescription> columnNamesIterator = orderedColumnNames.iterator();
                while(columnNamesIterator.hasNext()) {

                    ColumnDescription colDescr = columnNamesIterator.next();
                    final String columnName = colDescr.getName();
                    final int columnIndex = odeSimData.findColumn(columnName);
                    dataTypes.add("float64");
                    dataIDs.add("data_set_"+columnName);
                    dataShapes.add(allRowsCount+"");
                    dataLabels.add(columnName);
                    dataNames.add(columnName/*name*/);
                    double[] columnData = odeSimData.extractColumn(columnIndex);
                    for(int myrows=0;myrows<allRowsCount;myrows++) {
                        fromData[index] = columnData[myrows];
                        index++;
                    }
                }
//			Object[] objArr = new Object[] {fromData,new long[] {0,0},
//					new long[] {allColumnsCount,allRowsCount},new long[] {allColumnsCount,
//							allRowsCount},new long[] {0,0},new long[] {allColumnsCount,allRowsCount},help0.hdf5DataSpaceID};
                //			double[] copyFromData = (double[])((Object[])data)[0];
                //			long[] copyToStart = (long[])((Object[])data)[1];
                //			long[] copyToLength = (long[])((Object[])data)[2];
                //			long[] copyFromDims = (long[])((Object[])data)[3];
                //			long[] copyFromStart = (long[])((Object[])data)[4];
                //			long[] copyFromLength = (long[])((Object[])data)[5];
                Hdf5Utils.copySlice(help0.hdf5DatasetValuesID,fromData,new long[] {0,0},
                        new long[] {allColumnsCount,allRowsCount},new long[] {allColumnsCount,
                                allRowsCount},new long[] {0,0},new long[] {allColumnsCount,allRowsCount},help0.hdf5DataSpaceID);
                //writeHDF5Dataset(help0.hdf5DatasetValuesID, null, null, objArr, false);
                Hdf5Utils.insertAttribute(help0.hdf5DatasetValuesID, "_type", "ODE Data Export");//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "_type", null, "ODE Data Export", true);
                Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID, "dataSetDataTypes", dataTypes);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetDataTypes", null, dataTypes, true);
                Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID,"dataSetIds",dataIDs);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetIds", null,dataIDs , true);
                Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID,"dataSetLabels",dataLabels);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetLabels", null,dataLabels , true);
                Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID, "dataSetNames",dataNames);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetNames", null,dataNames , true);
                Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID, "dataSetShapes",dataShapes);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetShapes", null,dataShapes , true);
                Hdf5Utils.insertAttribute(help0.hdf5DatasetValuesID, "id","report");//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "id", null,"report" , true);
                Hdf5Utils.insertAttribute(help0.hdf5DatasetValuesID, "scanJobID",""+scanJobs[scan]);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "scanJobID", null,""+scanJobs[scan] , true);
                help0.close();
                H5.H5Gclose(jobGroupID);
                jobGroupID = -1;
            }
            H5.H5Fclose(hdf5FileID);
            hdf5FileID = -1;
            return hdf5TempFile;
        } finally {
            if(jobGroupID != -1) {try{H5.H5Gclose(jobGroupID);}catch(Exception e2){lg.error(e2.getMessage(), e2);}}
            if(hdf5FileID != -1) {try{H5.H5Fclose(hdf5FileID);}catch(Exception e2){lg.error(e2.getMessage(), e2);}}
        }
    }

}
