package cbit.vcell.microscopy;

import java.io.File;
import java.io.FileNotFoundException;

import cbit.sql.KeyValue;
import cbit.vcell.client.server.DataSetControllerProvider;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.server.User;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.LocalDataSetController;

/**
 */
public class LocalWorkspace {
	private String workspaceDirectory = null;
	private String simDataDirectory = null;
	private User owner = null;
	private String mostRecentFilename = null;
	private DataSetControllerImpl dataSetControllerImpl = null;
	private VCDataManager vcDataManager = null;
	private UserPreferences userPreferences = null;
	
	private static long LAST_GENERATED_KEY = System.currentTimeMillis();
	
	private StdoutSessionLog sessionLog = null;
	private LocalDataSetController localDataSetController = null;
	
	public static final String workSpacePath =
		System.getProperty("user.dir") + System.getProperty("file.separator"); 

	private static String osname = null;
	static {
		if (ResourceUtil.bWindows) {
			osname = "windows";
		} else if (ResourceUtil.bMac) {
			osname = "mac";
		} else if (ResourceUtil.bLinux) {
			osname = "linux";
		} else {
			throw new RuntimeException(System.getProperty("os.name") + " is not supported.");
		}
	}
	private final static String RES_DIRECTORY =
		"resources\\cbit\\vcell\\resource\\" + osname;

	private final static String EXE_FV =
		LocalWorkspace.workSpacePath +LocalWorkspace.RES_DIRECTORY +
		System.getProperty("file.separator") + "FiniteVolume" + ResourceUtil.EXE_SUFFIX;


	/**
	 * Constructor for LocalWorkspace.
	 * @param argWorkspaceDirectory String
	 * @param argOwner User
	 * @param argSimDataDirectory String
	 */
	public LocalWorkspace(String argWorkspaceDirectory, User argOwner, String argSimDataDirectory){
		this.workspaceDirectory = argWorkspaceDirectory;
		this.owner = argOwner;
		this.simDataDirectory = argSimDataDirectory;
		this.userPreferences = new UserPreferences(null);
		this.sessionLog = new StdoutSessionLog(getOwner().getName());
		System.setProperty(PropertyLoader.finiteVolumeExecutableProperty, LocalWorkspace.EXE_FV);
	}

	public static final KeyValue createNewKeyValue(){
		if(LAST_GENERATED_KEY != System.currentTimeMillis()){
			LAST_GENERATED_KEY = System.currentTimeMillis();
		}else{
			LAST_GENERATED_KEY+= 1;
		}
		return new KeyValue(LAST_GENERATED_KEY+"");
	}
	
	// get external image dataset file name or ROI file name
	public File getExternalDataFile(ExternalDataInfo arg_extDataInfo)
	{
		final ExternalDataInfo extDataInfo = arg_extDataInfo;
		String name = new File(extDataInfo.getFilename()).getName();
		File f = new File(LocalWorkspace.workSpacePath);
		//the actual file path is the fileparent in externalDataInfo plus username plus filename.
		f = new File(f, extDataInfo.getExternalDataIdentifier().getOwner().getName());
		f = new  File(f,name);
		return f;
	}	

	/**
	 * Method getWorkspaceDirectory.
	 * @return String
	 */
	public String getWorkspaceDirectory() {
		return workspaceDirectory;
	}

	/**
	 * Method getOwner.
	 * @return User
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Method getUserPreferences.
	 * @return UserPreferences
	 */
	public UserPreferences getUserPreferences(){
		return userPreferences;
	}
	
	/**
	 * Method getMostRecentFilename.
	 * @return String
	 */
	public String getMostRecentFilename() {
		return mostRecentFilename;
	}

	/**
	 * Method setMostRecentFilename.
	 * @param mostRecentFilename String
	 */
	public void setMostRecentFilename(String mostRecentFilename) {
		this.mostRecentFilename = mostRecentFilename;
	}

	/**
	 * Method getSimDataDirectory.
	 * @return String
	 */
	public String getSimDataDirectory() {
		return simDataDirectory;
	}

	/**
	 * Method getDataSetControllerImpl.
	 * @return DataSetControllerImpl
	 * @throws FileNotFoundException
	 */
	public DataSetControllerImpl getDataSetControllerImpl() throws FileNotFoundException{ 
		if (dataSetControllerImpl==null){
			File rootDir = new File(getSimDataDirectory());
			dataSetControllerImpl = new DataSetControllerImpl(sessionLog,new Cachetable(1000),rootDir,rootDir);
		}
		return dataSetControllerImpl;
	}
	
	/**
	 * Method getVCDataManager.
	 * @return VCDataManager
	 */
	public VCDataManager getVCDataManager() throws Exception{
		if (vcDataManager==null){
			localDataSetController =
				new LocalDataSetController(
						null,sessionLog,
						getDataSetControllerImpl(),
						null,getOwner()
					);

			vcDataManager =
				new VCDataManager(
					new DataSetControllerProvider(){
						public DataSetController getDataSetController(){
							return localDataSetController;
						}
					}
				);
		}
		return vcDataManager;
	}

}
