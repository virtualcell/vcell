package org.jlibsedml.execution;

/**
 * Interface for mappings between simulation results and model identifiers.
 * @author radams
 *
 */
public interface IModel2DataMappings {

	/**
	 * Boolean test for whether a mapping exists for the argument
	 * @param spId A non-null variable identifier in the model.
	 * @return <code>true</code> if a mapping exists, false otherwise.
	 */
	public boolean hasMappingFor(String spId) ;
	
	/**
	 * Gets a zero-based column index for the given species identifier, or -1 if no 
	 *   no such column exists.
	 * @param speciesID A non-null <code>String</code>.
	 * @return The column index, or -1 if not found.
	 */
	public int getColumnIndexFor(String speciesID);
	
	/**
	 * GEts the column title for a given species identifier, or <code>null</code> if not found.
	 * @param speciesID A non-null <code>String</code>.
	 * @return The column title or <code>null</code> if not found.
	 */
	public String getColumnTitleFor(String speciesID);
}
