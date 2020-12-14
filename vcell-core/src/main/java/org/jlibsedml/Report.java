package org.jlibsedml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the SED-ML 'Report' element for describing textual output of 
 *  a simulation.
 * @author radams
 *
 */
public final class Report extends Output {

	private ArrayList<DataSet> listOfDataSets;

		/**
		 * 
		 * @param id A unique <code>String</code> identifier for this object.
		 * @param name An optional <code>String</code> name for this object.
		 */
	   public Report(String id, String name) {
		   super(id, name);
		   listOfDataSets = new ArrayList<DataSet>();
	   }
	   
	   @Override
		public String getElementName() {
			return SEDMLTags.OUTPUT_REPORT;
		}
	   
	   /**
	    * Getter for a read-only list of {@link DataSet} objects contained in this report.
	    * @return non-null but possibly empty List<DataSet> .
	    */
	   public List<DataSet> getListOfDataSets() {
		   return Collections.unmodifiableList(listOfDataSets);
	   }
	   
	   /**
		 * Adds a {@link DataSet} to this object's list of DataSets, if not already present.
		 * @param dataSet A non-null {@link DataSet} element
		 * @return <code>true</code> if dataSet added, <code>false </code> otherwise.
		 */
		public boolean addDataSet(DataSet dataSet ){
			if(!listOfDataSets.contains(dataSet))
				return listOfDataSets.add(dataSet);
			return false;
		}
		
		  /**
		 * Removes a {@link DataSet} from this object's list of DataSets.
		 * @param dataSet A non-null {@link DataSet} element
		 * @return <code>true</code> if dataSet removed, <code>false </code> otherwise.
		 */
		public boolean removeDataSet(DataSet dataSet ){
			
				return listOfDataSets.remove(dataSet);
			
		}
	   
		/**
	     * Gets the type of this output.
	     * 
	     * @return SEDMLTags.REPORT_KIND
	     */
	   public String getKind() {
		   return SEDMLTags.REPORT_KIND;
	   }

	@Override
	public List<String>  getAllDataGeneratorReferences() {
		List<String> rc = new ArrayList<String>();
		for (DataSet d : listOfDataSets){
			rc.add(d.getDataReference());
		}
		return rc;
	}

	@Override
	public List<String> getAllIndependentDataGeneratorReferences() {
		return Collections.emptyList();
	}
	   
}
