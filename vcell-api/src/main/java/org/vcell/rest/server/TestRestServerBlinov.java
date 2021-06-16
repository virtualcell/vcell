package org.vcell.rest.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.VirtualHost;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.rest.VCellApiApplication;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.SimContextRep;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import freemarker.template.Configuration;

public class TestRestServerBlinov extends Restlet{

	private static ArrayList<BioModelRep> bioModelRepArr = new ArrayList<BioModelRep>();
	private static Hashtable<String,File> mapBMidToFileName = new Hashtable<String,File>();
	private static Hashtable<String,BioModelRep> mapBMidToBiomodelRep = new Hashtable<String,BioModelRep>();
	public TestRestServerBlinov () {
	}
	public static void main(String[] args) {
		try {
			//The following mimics the the VCell server and database without having to connect to them
			//arg[0] (first argument) is directory path on local system where you have exported .vcml models from VCell client
			// that you want to use to test the biomodel web page generating code, these files are a proxy for the VCell database
			File biomodel_vcml_dir = new File(args[0]);// Test biomodel vcml documents
			File[] dirFiles = biomodel_vcml_dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile();
				}
			});
//			final String xmlString = new XMLSource(dirFiles[i]).getXmlString();
//			final List<Element> parseOverrideElementsFromVCML = MathOverrides.parseOverrideElementsFromVCML(new CommentStringTokenizer(xmlString));
			for(int i=0;i<dirFiles.length;i++) {
				try {
					//Read in each file found in the directory passed in args[0] check if it can be imported as a BioModel
					//If it fails just skip.
					//The imported BioModel is cached as BioModelRep info and are returned when the Resource objects make a 'query'
					final BioModel bm = XmlHelper.XMLToBioModel(new XMLSource(dirFiles[i]));
					mapBMidToFileName.put(bm.getVersion().getVersionKey().toString(), dirFiles[i]);
					final SimulationContext[] simulationContexts = bm.getSimulationContexts();
					SimContextRep[] scr = new SimContextRep[simulationContexts.length];
					KeyValue[] scrKeys = new KeyValue[simulationContexts.length];
					for(int j=0;j<simulationContexts.length;j++) {
						//KeyValue scKey, BigDecimal branchID, String name, User owner, KeyValue mathKey)
						scrKeys[j] = simulationContexts[j].getVersion().getVersionKey();
						scr[j] = new SimContextRep(scrKeys[j], simulationContexts[j].getVersion().getBranchID(),
								simulationContexts[j].getName(), simulationContexts[j].getVersion().getOwner(), simulationContexts[j].getModel().getKey());
					}
					final Simulation[] simulations = bm.getSimulations();
					SimulationRep[] sreps = new SimulationRep[simulations.length];
					KeyValue[] srepKeys = new KeyValue[simulations.length];
					for(int j=0;j<simulations.length;j++) {
						//KeyValue key, BigDecimal branchID, String name, User owner, KeyValue mathKey, SolverTaskDescription solverTaskDescription, Element[] mathOverrideElements
						srepKeys[j] = simulations[j].getVersion().getVersionKey();
						//public SimulationRep(KeyValue key, BigDecimal branchID, String name, User owner, KeyValue mathKey, SolverTaskDescription solverTaskDescription, MathOverrides.Element[] mathOverrideElements) {

//						final MathOverrides mathOverrides = simulations[j].getSolverTaskDescription().getSimulation().getMathOverrides();
//						for(int k=0;k<mathOverrides.getScanCount();k++) {
//							MathOverrides.Element elem = new Element(mathOverrides.get, null);
//						}
						sreps[j] = new SimulationRep(srepKeys[j], simulations[j].getVersion().getBranchID(),
								simulations[j].getName(), simulations[j].getVersion().getOwner(), simulations[j].getMathDescription().getKey(),
								simulations[j].getSolverTaskDescription(), new MathOverrides.Element[] {});
					}
					//KeyValue bmKey, String name, int privacy, User[] groupUsers, Date date, String annot, BigDecimal branchID, KeyValue modelRef, User owner, KeyValue[] simKeyList, KeyValue[] simContextKeyList)
					BioModelRep bmrep =
						new BioModelRep(bm.getVersion().getVersionKey(), bm.getName(), bm.getVersion().getGroupAccess().getGroupid().intValue(),
								new User[] {}, bm.getVersion().getDate(), bm.getVersion().getAnnot(), bm.getVersion().getBranchID(),
								bm.getModel().getVersion().getVersionKey(), bm.getVersion().getOwner(), srepKeys, scrKeys);
					for(int j=0;j<simulationContexts.length;j++) {
						bmrep.addSimContextRep(scr[j]);
					}
					for(int j=0;j<simulations.length;j++) {
						bmrep.addSimulationRep(sreps[j]);
					}
					bioModelRepArr.add(bmrep);
					mapBMidToBiomodelRep.put(bm.getVersion().getVersionKey().toString(), bmrep);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//Create a Restlet server on local machine that will accept queries from a regular web browser
			Component restletComponent  = new Component();
			restletComponent.getClients().add(Protocol.CLAP);//So FreeMarker html formatting scripts work (.ftl files in {EclipseWorkspaceRootDir}/vcell/vcell-api/src/main/resources/(*.ftl}
			Server server = restletComponent.getServers().add(Protocol.HTTP, 8182);
			server.getContext().getParameters().add("useForwardedForHeader", "true");
//			restletComponent.getInternalRouter().setDefaultMatchingMode(Template.MODE_STARTS_WITH);
			final VirtualHost defaultHost = restletComponent.getDefaultHost();
			//getVcellApiApplication() actually makes the connections URL->ServerResource
			//e.g. http://{HostNameWhereThisProgramIsRunning}:8182/biomodel -> BiomodelServerResource
			// See getVCellApiApplication() for other mappings (router attachments)
			defaultHost.attach(getVCellApiApplication());
			//Start servlet which can be accessed with base URL http://{HostNameWhereThisProgramIsRunning}:8182
			restletComponent.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//Create Fake RestDatabaseService that doesn't query a database but the local cache generated when this program started (see main(...))
	public static RestDatabaseService getRestDatabaseService() throws DataAccessException {
		//Created unimplemented ConnectionFactory (we don't use actual database connections)
		ConnectionFactory connFac = new ConnectionFactory() {

			@Override
			public void close() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void failed(Connection con, Object lock) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Connection getConnection(Object lock) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void release(Connection con, Object lock) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public KeyFactory getKeyFactory() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public DatabaseSyntax getDatabaseSyntax() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		
		//Create unimplemented KeyFactory (we don't make keys for the database)
		KeyFactory keyf = new KeyFactory() {

			@Override
			public String getCreateSQL() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getDestroySQL() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String nextSEQ() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String currSEQ() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public KeyValue getNewKey(Connection con) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BigDecimal getUniqueBigDecimal(Connection con) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		
		//Create unimplemented DatabaseServerImpl (required argument for RestDatabaseService but will never be called for our tests)
		DatabaseServerImpl dbsi = new DatabaseServerImpl(connFac, keyf) {
			@Override
			public AdminDBTopLevel getAdminDBTopLevel() {
				return null;
			}
		};
		
		//Create the Fake RestDatabaseService and provide a bare implementation of the methods we need to do our tests
		//the methods use local caches of imported biomodels to respond to queries
		return new RestDatabaseService(dbsi, null, null) {
			@Override
			public String query(BiomodelVCMLServerResource resource, User vcellUser)throws SQLException, DataAccessException {
				try {
					//
					//Both BiomodelVCMLServerResource and BiomodelSBMLServerResource use this to get the initial BioModel vcml content
					//
					//Responds to queries (e.g. http://myhost:8182/biomodel/173365344/biomodel.vcml) ending in "/"+BIOMODEL+"/{"+BIOMODELID+"}/"+VCML_DOWNLOAD (ass set in getVCellApiApplication())
//					final ConcurrentMap<String, Object> attributes = resource.getRequest().getAttributes();
					//Get the values between the / slashes in the query URL
					final List<String> segments = resource.getRequest().getOriginalRef().getSegments();
					// For this Resource segments[0] ="biomodel", segments[1] = BIOMODELID, segments[2] = "biomodel.vcml"
					String bmKey = segments.get(1);
//					String docType = segments.get(2);
//					if(docType.endsWith(".vcml")) {
					//Read the cached file contents and return
						File f = mapBMidToFileName.get(bmKey);//http://dockerbuild:8182/biomodel/0/biomodel.sbml
						//final Map<String, String> valuesMap = resource.getQuery().getValuesMap();
						return new String(Files.readAllBytes(f.toPath()));						
//					}else if(docType.endsWith(".sbml")) {
//						
//					}
//					throw new IllegalArgumentException("Unexpected docType="+docType);
				} catch (IOException e) {
					throw new DataAccessException(e.getMessage(),e);
				}
			}

			@Override
			public BioModelRep getBioModelRep(KeyValue bmKey, User vcellUser)throws SQLException, ObjectNotFoundException, DataAccessException {
				return mapBMidToBiomodelRep.get(bmKey.toString());
			}

			@Override
			public BioModelRep[] query(BiomodelsServerResource resource, User vcellUser)throws SQLException, DataAccessException {
				return bioModelRepArr.toArray(new BioModelRep[0]);
			}			
			
		};
	}
	public static VCellApiApplication getVCellApiApplication() throws DataAccessException {
		return new VCellApiApplication(getRestDatabaseService(), null, null, null, null, new Configuration(), null, null) {
			@Override
			public Restlet createInboundRoot() {
				// TODO Auto-generated method stub
				
				Router rootRouter = new Router(getContext());
				//rootRouter.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
				rootRouter.attach("/"+BIOMODEL, BiomodelsServerResource.class);
				rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}", BiomodelServerResource.class); 
				rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SBML_DOWNLOAD, BiomodelSBMLServerResource.class);
				rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}/"+VCML_DOWNLOAD, BiomodelVCMLServerResource.class); 
				return rootRouter;
			}

			@Override
			public User getVCellUser(ChallengeResponse response, AuthenticationPolicy authPolicy) {
				// TODO Auto-generated method stub
				return new User("gsoc",new KeyValue("0"));
			}
			
		};
	}
}
