package cbit.vcell.microscopy;

import org.vcell.util.Compare;

import org.vcell.util.Matchable;
import org.vcell.util.document.ExternalDataIdentifier;

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
			if (!Compare.isEqualOrNull(getExternalDataIdentifier(), exInfo.getExternalDataIdentifier())){
				return false;
			}
			if (!Compare.isEqualOrNull(getFilename(),exInfo.getFilename())){
				return false;
			}
			return true;
		}
		return false;
		
	}
}
