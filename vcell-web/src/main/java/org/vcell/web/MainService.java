package org.vcell.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.httpclient.URI;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.ssl.SSLContexts;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.ConfigurationException;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCInfoContainer;

import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.Hdf5Utils;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

public class MainService {

//	private DatabaseServerImpl databaseServerImpl;
//	private AdminDBTopLevel adminDbTopLevel;
//	private DataServerImpl dataServerImpl = null;
	private HttpServer server;
	private static HashMap<String,AuthenticationInfo> useridMap = new HashMap<String,AuthenticationInfo>();
	private static ConnectionFactory conFactory;
	
	private static class AuthenticationInfo {
		final User user;
		final DigestedPassword digestedPassword;
		AuthenticationInfo(User user, DigestedPassword digestedPassword){
			this.user = user;
			this.digestedPassword = digestedPassword;
		}
	}

	public static void main(String[] args) {
		try {
			new MainService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MainService() throws SQLException, DataAccessException, FileNotFoundException, ConfigurationException {

		ResourceUtil.setNativeLibraryDirectory();
		MainService.conFactory = DatabaseService.getInstance().createConnectionFactory();
		KeyFactory keyFactory = conFactory.getKeyFactory();
		DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory);
		AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
		String cacheSize = PropertyLoader.getRequiredProperty(PropertyLoader.simdataCacheSizeProperty);
		long maxMemSize = Long.parseLong(cacheSize);

		Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20,maxMemSize);
		DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(cacheTable,
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty)),
				new File(PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirInternalProperty, PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty))));
		ExportServiceImpl exportServiceImpl = new ExportServiceImpl();
		DataServerImpl dataServerImpl = new DataServerImpl(dataSetControllerImpl, exportServiceImpl);

		String exportBaseURL = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseURLProperty);

		try (InputStream inputStream = new FileInputStream(new File(System.getProperty(PropertyLoader.vcellapiKeystoreFile)))) {
			final KeyStore serverKeyStore = KeyStore.getInstance("jks");
			String pwd = Files.readAllLines(new File(System.getProperty(PropertyLoader.vcellapiKeystorePswdFile)).toPath()).get(0);
			serverKeyStore.load(inputStream, pwd.toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(serverKeyStore, pwd.toCharArray());
			KeyManager[] serverKeyManagers = keyManagerFactory.getKeyManagers();
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(serverKeyStore);
			TrustManager[] serverTrustManagers = trustManagerFactory.getTrustManagers();
			final SSLContext sslContext = SSLContexts.createDefault();
			sslContext.init(serverKeyManagers, serverTrustManagers, new SecureRandom());
			int listenPort = Integer.parseInt(System.getProperty(PropertyLoader.webDataServerPort));
			server = ServerBootstrap.bootstrap().registerHandler("*", new HttpRequestHandler() {
				@Override
				public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
//					Connection con = null;
//	            	try {
//	            		synchronized (MainService.this) {
//		            		if(availableConnections.size() == 0 && inUseConnections.size() == 3) {
//	            				String errMesg = "<html><body>req='"+request.toString()+"' <br>All available connections in use, retry again.</br>"+"</body></html>";
//	            				response.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
//	            				StringEntity se = new StringEntity(errMesg);
//	            				se.setContentType(ContentType.TEXT_HTML.getMimeType());
//	            				response.setEntity(se);
//	            				return;
//		            		}else {
//		            			if((availableConnections.size() + inUseConnections.size()) < 3) {
//		            				con = new connection;
//		            			}
//		            			con = availableConnections.remove(0);
//		            			inUseConnections.add(con);
//		            		}
//	            		}
	            		if(request.getRequestLine().getMethod().toUpperCase().equals("GET")) {
		            		URI uri = new URI(request.getRequestLine().getUri(),true);
							final List<NameValuePair> parse = URLEncodedUtils.parse(uri.getQuery(),Charset.forName("utf-8"));
							TreeMap<String,String> queryMap = new TreeMap<String,String>();
							for(NameValuePair nameValuePair:parse) {
								String values = queryMap.get(nameValuePair.getName());
								if(values == null) {
//									values = new ArrayList<String>();
									queryMap.put(nameValuePair.getName(), nameValuePair.getValue());
								}
//								values.add(nameValuePair.getValue());
							}
	            			try {
	            				User authuser = null;
	            				//HttpRequest request = (HttpRequest)req;
	            				//Use "WWW-Authenticate - Basic" authentication scheme
	            				//Browser takes care of asking user for credentials and sending them
	            				//Must be used with https connection to hide credentials
	            				//Header authHeader = request.getHeaders().getFirst("Authorization");
	            				Header authHeader = request.getFirstHeader("Authorization");
	            				if(authHeader != null) {//caller included a user and password
	            					String typeAndCredential = authHeader.getValue();
//	            					System.out.println("--"+up);
	            					java.util.StringTokenizer st = new java.util.StringTokenizer(typeAndCredential," ");
	            					String type=st.nextToken();
	            					String userAndPasswordB64 = st.nextToken();
	            					String s = new String(Base64.getDecoder().decode(userAndPasswordB64));
//	            					System.out.println("type="+type+" decoded="+s);
	            					if(type.equals("Basic")) {
	            						java.util.StringTokenizer st2 = new java.util.StringTokenizer(s,":");
	            						if(st2.countTokens() == 2) {
	            							String usr=st2.nextToken();
	            							String pw = st2.nextToken();
	            	//						System.out.println("user="+usr+" password="+pw);
	            							UserLoginInfo.DigestedPassword dpw = new UserLoginInfo.DigestedPassword(pw);
	            	//						System.out.println(dpw);
//	            							VCellApiApplication application = ((VCellApiApplication)getApplication());
//	            							authuser = application.getUserVerifier().authenticateUser(usr,dpw.getString().toCharArray());
	            							authuser = authenticateUser(usr,dpw.getString().toCharArray(),adminDbTopLevel);
	            	//						System.out.println(authuser);
	            						}
	            					}
	            				}
	            				if(authuser == null) {
//	            					//If we get here either there was not user/pw or user/pw didn't authenticate
//	            					//We need to add a response header
//	            					//Response headers container might be null so add one if necessary
//	            					if(((HttpResponse)response).getAllHeaders() == null) {
//	            						((HttpResponse)response).getAttributes().
//	            							put(HeaderConstants.ATTRIBUTE_HEADERS,new Series(Header.class));
//	            					}
	            					//Tell whoever called us we want a user and password that we will check against admin vcell users
	            					response.addHeader("WWW-Authenticate", "Basic");
									response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
	            					return;
	            				}
	            				
//	            				Form form = request.getResourceRef().getQueryAsForm();
//	            				if (form.getFirst("stats") != null){
//	            					String requestTypeString = form.getFirstValue("stats", true);//get .../rpc?stats=value 'value'
//	            					if((authuser.getName().equals("frm") || 
//	            							authuser.getName().equals("les") ||
//	            							authuser.getName().equals("ion") ||
//	            							authuser.getName().equals("danv") ||
//	            							authuser.getName().equals("mblinov") ||
//	            							authuser.getName().equals("ACowan"))) {
//	            							String result = restDatabaseService.getBasicStatistics();
//	            							response.setStatus(Status.SUCCESS_OK);
//	            							response.setEntity(result, MediaType.TEXT_HTML);
//	            							return;
//	            						} 								
//
//	            				}else 
	            					if(queryMap.get("route") != null && queryMap.get("type") != null && queryMap.get("simid") != null && queryMap.get("jobid") != null) {
//	            						final Path path = Paths.get(uri.getPath());
//	            						final Iterator<Path> iterator = path.iterator();
	            						final String SIMDATADDF5 = "simhdf5";
	            						final String ODE = "ode";
	            						String simdataRoute = queryMap.get("route");
	            						String dataType = queryMap.get("type");
	            						if(simdataRoute.toLowerCase().equals(SIMDATADDF5) && dataType.toLowerCase().equals(ODE)) {
	            							String simID = null;
	            							TreeSet<Integer> jobIDs = new TreeSet<Integer>();
//	            							String userKey = null;
//	            							String userid = null;
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
	            								}
//	            								else if(nvp.getName().toLowerCase().equals("userkey")) {
//	            									userKey = nvp.getValue();
//	            								}else if(nvp.getName().toLowerCase().equals("userid")) {
//	            									userid = URLDecoder.decode(nvp.getValue(), "UTF-8");
//	            								}
	            								else if(nvp.getName().toLowerCase().equals("blank")) {
	            									blank = Double.parseDouble(nvp.getValue());
	            								}
	            							}
//	            							User user = new User(userid,new KeyValue(userKey));
	            							VCSimulationIdentifier vcsid = new VCSimulationIdentifier(new KeyValue(simID), authuser);
	            							File hdf5File = createOdeHdf5(vcsid, jobIDs.toArray(new Integer[0]), blank,dataServerImpl);
	            							
	            							URL url = new URL(exportBaseURL + hdf5File.getName());
	            							response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
	            							response.addHeader("Location",url.toString());
	            							response.setEntity(null);
	            							return;
	            						}
	            				}else if(queryMap.get("modelInfos") != null) {
	            					File hdf5TempFile = createInfosHdf5(authuser,databaseServerImpl);
	    							URL url = new URL(exportBaseURL + hdf5TempFile.getName());
	    							response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
	    							response.addHeader("Location",url.toString());
	    							response.setEntity(null);
	    							return;
	            				}
	            					
	        					response.setStatusCode(HttpStatus.SC_NOT_FOUND);
	        					String mesg = "<html><body>req='"+uri.toString()+"' <br>No vcell-web service path matches this query</br>"+"</body></html>";
	            				StringEntity se = new StringEntity(mesg);
	            				se.setContentType(ContentType.TEXT_HTML.getMimeType());
	            				response.setEntity(se);

	            			} catch (Exception e) {
	            				String errMesg = "<html><body>Error RpcRestlet.handle(...) req='"+request.toString()+"' <br>err='"+e.getMessage()+"'</br>"+"</body></html>";
//	            				getLogger().severe(errMesg);
	            				e.printStackTrace();
	            				response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	            				StringEntity se = new StringEntity(errMesg);
	            				se.setContentType(ContentType.TEXT_HTML.getMimeType());
	            				response.setEntity(se);
	            			}

	            		}
				}
			}).setListenerPort(listenPort).setSslContext(sslContext).create();
			server.start();
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	private static File createInfosHdf5(User authuser,DatabaseServerImpl databaseServerImpl) throws IOException, HDF5LibraryException, HDF5Exception, DataAccessException {
		String exportBaseDir = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirInternalProperty);
		File hdf5TempFile = File.createTempFile("webexport_Infos_"+TokenMangler.fixTokenStrict(authuser.getName())+"_", ".hdf", new File(exportBaseDir));
//		System.out.println("/home/vcell/Downloads/hdf5/HDFView/bin/HDFView "+hdf5TempFile.getAbsolutePath()+" &");
		int hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		int bioModelsGroup = Hdf5Utils.createGroup(hdf5FileID, "BioModels");
		VCInfoContainer vcInfoContainer = null;
		try (Connection con = conFactory.getConnection(null)) {
			vcInfoContainer = DbDriver.getVCInfoContainer(authuser, con, DatabaseSyntax.ORACLE,DbDriver.EXTRAINFO_ALL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BioModelInfo[] bioModelInfos = vcInfoContainer.getBioModelInfos();
		Arrays.sort(bioModelInfos, new Comparator<BioModelInfo> () {
			@Override
			public int compare(BioModelInfo o1, BioModelInfo o2) {
				if(o1.getVersion().getOwner().getName().equals(o2.getVersion().getOwner().getName())) {
					if(o1.getVersion().getName().equals(o2.getVersion().getName())) {
						return o1.getVersion().getDate().compareTo(o2.getVersion().getDate());
					}
					return o1.getVersion().getName().compareToIgnoreCase(o2.getVersion().getName());
				}
				return o1.getVersion().getOwner().getName().compareToIgnoreCase(o2.getVersion().getOwner().getName());
			}});
		String lastUser = null;
		String lastModel = null;
		int lastUserGroupID = -1;
		int lastModelGroupID = -1;
		final DateFormat dateTimeInstance = DateFormat.getDateTimeInstance();
		for(BioModelInfo bioModelInfo:bioModelInfos) {
			if(lastUser == null || !lastUser.equals(bioModelInfo.getVersion().getOwner().getName())) {
				if(lastUserGroupID != -1) {
					H5.H5Gclose(lastUserGroupID);
				}
				lastUser = bioModelInfo.getVersion().getOwner().getName();
				lastUserGroupID = Hdf5Utils.createGroup(bioModelsGroup, lastUser);
			}
//			System.out.println("'"+lastModel+"'"+" "+"'"+bioModelInfo.getVersion().getName()+"'"+" "+(bioModelInfo.getVersion().getName().equals(lastModel)));
			if(lastModel == null || !lastModel.equals(bioModelInfo.getVersion().getName())) {
				if(lastModelGroupID != -1) {
					H5.H5Gclose(lastModelGroupID);
				}
				lastModel = bioModelInfo.getVersion().getName();
				lastModelGroupID = Hdf5Utils.createGroup(lastUserGroupID, lastModel.replace("/", "fwdslsh"));
				
			}
//			int bioModelGroupID = Hdf5Utils.createGroup(lastUserGroupID, (bioModelInfo.getVersion().getName()).replace("/", "fwdslsh"));
			final String format = dateTimeInstance.format(bioModelInfo.getVersion().getDate());
//			System.out.println(lastUser+" "+lastModel.replace("/", "fwdslsh")+" "+format);
			int dateGroupID = Hdf5Utils.createGroup(lastModelGroupID,format);
			//+"_"+dateTimeInstance.format(bioModelInfo.getVersion().getDate())
			Hdf5Utils.insertAttribute(dateGroupID, "versionKey", bioModelInfo.getVersion().getVersionKey().toString());
//    				        ArrayList<IJContextInfo> ijContextInfos = new ArrayList<>();
			BioModelChildSummary bioModelChildSummary = bioModelInfo.getBioModelChildSummary();
			if(bioModelChildSummary != null && bioModelChildSummary.getSimulationContextNames() != null && bioModelInfo.getBioModelChildSummary().getSimulationContextNames().length > 0) {
				for(int i = 0; i<bioModelInfo.getBioModelChildSummary().getSimulationContextNames().length;i++) {
					String bioModelContextName = bioModelInfo.getBioModelChildSummary().getSimulationContextNames()[i];
					if(bioModelContextName != null) {
						//dataspaceName.replace("/", "fwdslsh");
		    			int bmContextID = Hdf5Utils.createGroup(dateGroupID, bioModelContextName.replace("/", "fwdslsh"));
//		    			String annotatedFunctionXml = bioModelInfo.getAnnotatedFunctionsStr(bioModelContextName);
//		    			Hdf5Utils.insertAttribute(bmContextID,"annotfuncxml",(annotatedFunctionXml==null?"null":annotatedFunctionXml));
		    			Hdf5Utils.insertAttribute(bmContextID, "type", bioModelInfo.getBioModelChildSummary().getAppTypes()[i].toString());
		    			Hdf5Utils.insertAttribute(bmContextID, "dim", bioModelInfo.getBioModelChildSummary().getGeometryDimensions()[i]+"");
		    			Hdf5Utils.insertAttribute(bmContextID, "geoName", bioModelInfo.getBioModelChildSummary().getGeometryNames()[i]);
		    			if(bioModelInfo.getBioModelChildSummary().getSimulationNames(bioModelContextName) != null && bioModelInfo.getBioModelChildSummary().getSimulationNames(bioModelContextName).length > 0) {
		    				ArrayList<String> simNameArr = new ArrayList<String>();
		    				for(String simName:bioModelInfo.getBioModelChildSummary().getSimulationNames(bioModelContextName)) {
		    					int bmSimID = -1;
		    					if(simName.contains("/")) {//handle "/" forbidden in object names
		    						bmSimID = Hdf5Utils.createGroup(bmContextID, URLEncoder.encode(simName,"UTF-8"));
		    						Hdf5Utils.insertAttribute(bmSimID,"urlencoded","true");
		    					}else {
		    						bmSimID = Hdf5Utils.createGroup(bmContextID, simName);
		    					}
		    					Hdf5Utils.insertAttribute(bmSimID,"simid",(bioModelInfo.getSimID(simName)==null?"null":bioModelInfo.getSimID(simName).toString()));
		    					Hdf5Utils.insertAttribute(bmSimID,"scancount",bioModelInfo.getScanCount(simName)+"");
//		    					simNameArr.add(simName);
//    				        	simNameArr.add((bioModelInfo.getSimID(simName)==null?"null":bioModelInfo.getSimID(simName).toString()));
//    				        	simNameArr.add(bioModelInfo.getScanCount(simName)+"");
    				        	H5.H5Gclose(bmSimID);
		    				}
//							Hdf5Utils.insertStrings(bmContextID, "sims", new long[] {bioModelInfo.getBioModelChildSummary().getSimulationNames(bioModelContextName).length,3},simNameArr);
		    			}
		    			H5.H5Gclose(bmContextID);
//    			    					IJContextInfo ijContextInfo = new IJContextInfo(bioModelContextName,bioModelInfo.getBioModelChildSummary().getAppTypes()[i],bioModelInfo.getBioModelChildSummary().getGeometryDimensions()[i],bioModelInfo.getBioModelChildSummary().getGeometryNames()[i],ijSimInfos);
//    			    					ijContextInfos.add(ijContextInfo);
					}
				}
			}
			H5.H5Gclose(dateGroupID);

//    			        	modelInfos.add(new IJModelInfo(bioModelInfo.getVersion().getName(), bioModelInfo.getVersion().getDate(), IJDocType.bm, openVCDocumentVersionKeys.contains(bioModelInfo.getVersion().getVersionKey()),bioModelInfo.getVersion().getOwner().getName(),bioModelInfo.getVersion().getVersionKey(), ijContextInfos));
		}
		H5.H5Gclose(lastModelGroupID);
		H5.H5Gclose(lastUserGroupID);
		H5.H5Gclose(bioModelsGroup);
		H5.H5Fclose(hdf5FileID);
		return hdf5TempFile;
	}
	
	private static File createOdeHdf5(VCSimulationIdentifier vcsid,Integer[] scanJobs,double blankCellValue/*for Histogram*/,DataServerImpl dataServerImpl) throws Exception{
		File hdf5TempFile = null;
		int hdf5FileID = -1;
		int jobGroupID = -1;
		try {
			String exportBaseDir = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirInternalProperty);
			hdf5TempFile = File.createTempFile("webexport_Ode_"+vcsid.getSimulationKey()+"_", ".hdf", new File(exportBaseDir));
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
//				Object[] objArr = new Object[] {fromData,new long[] {0,0},
//						new long[] {allColumnsCount,allRowsCount},new long[] {allColumnsCount,
//								allRowsCount},new long[] {0,0},new long[] {allColumnsCount,allRowsCount},help0.hdf5DataSpaceID};
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
			if(jobGroupID != -1) {try{H5.H5Gclose(jobGroupID);}catch(Exception e2){e2.printStackTrace();}}
			if(hdf5FileID != -1) {try{H5.H5Fclose(hdf5FileID);}catch(Exception e2){e2.printStackTrace();}}
		}
	}
	
	private static User authenticateUser(String userid, char[] secret,AdminDBTopLevel adminDbTopLevel) throws ObjectNotFoundException, DataAccessException, SQLException{
		DigestedPassword digestedPassword = UserLoginInfo.DigestedPassword.createAlreadyDigested(new String(secret));
		AuthenticationInfo authInfo = useridMap.get(userid);
		if (authInfo!=null){
			if (authInfo.digestedPassword.equals(digestedPassword)){
				return authInfo.user;
			}
		}
		synchronized (adminDbTopLevel) {
			User user = null;
			user = adminDbTopLevel.getUser(userid, digestedPassword,true,false);
			// refresh stored list of user infos (for authentication)
			if (user!=null){
				useridMap.put(userid,new AuthenticationInfo(user,digestedPassword));
			}
			return user;
		}
	}

}
