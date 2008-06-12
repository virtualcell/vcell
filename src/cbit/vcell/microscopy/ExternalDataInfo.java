package cbit.vcell.microscopy;

import cbit.util.Matchable;
import cbit.vcell.simdata.ExternalDataIdentifier;

/**
 */
public class ExternalDataInfo implements Matchable {
	
	private ExternalDataIdentifier externalDataIdentifier = null;
	private String filename = null;
	
	/**
	 * Constructor for ExternalDataInfo.
	 * @param externalDataIdentifier ExternalDataIdentifier
	 * @param filename String
	 */
	public ExternalDataInfo(ExternalDataIdentifier externalDataIdentifier, String filename) {
		super();
		this.externalDataIdentifier = externalDataIdentifier;
		this.filename = filename;
	}
	/**
	 * Method getExternalDataIdentifier.
	 * @return ExternalDataIdentifier
	 */
	public ExternalDataIdentifier getExternalDataIdentifier() {
		return externalDataIdentifier;
	}
	/**
	 * Method getFilename.
	 * @return String
	 */
	public String getFilename() {
		return filename;
	}
	
	public boolean compareEqual(Matchable obj) 
	{
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof ExternalDataInfo) {
			ExternalDataInfo exInfo = (ExternalDataInfo) obj;
			if (!cbit.util.Compare.isEqualOrNull(getExternalDataIdentifier(), exInfo.getExternalDataIdentifier())){
				return false;
			}
			if (!cbit.util.Compare.isEqualOrNull(getFilename(),exInfo.getFilename())){
				return false;
			}
			return true;
		}
		return false;
		
	}
}
