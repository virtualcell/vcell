package cbit.vcell.client.task;

import java.io.File;

import org.vcell.util.gui.exporter.SelectorExtensionFilter;

import cbit.vcell.mapping.SimulationContext;

/**
 * super class of tasks for exporting; provides location for hashtable keys
 */
public abstract class ExportTask extends AsynchClientTask {

	/**
	 * key to use as flag the saved file name was different than user provided
	 */
	protected static final String RENAME_KEY = ChooseFile.class.getCanonicalName() + "-renameKey";
	/**
	 * key for {@link SelectorExtensionFilter} 
	 */
	protected static final String FILE_FILTER = "fileFilter";
	
	/**
	 *  key for {@link SimulationContext}
	 */
	protected static final String SIM_CONTEXT = "simContext";
	
	/**
	 *  key for {@link File} 
	 */
	protected static final String EXPORT_FILE = "exportFile";


	ExportTask(String name, int taskType) {
		super(name, taskType);
	}

	ExportTask(String name, int taskType, boolean skipIfAbort,
			boolean skipIfCancel) {
		super(name, taskType, skipIfAbort, skipIfCancel);
		// TODO Auto-generated constructor stub
	}


}
