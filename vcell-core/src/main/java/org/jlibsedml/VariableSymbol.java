package org.jlibsedml;

/**
 * Enumeration of special SED-ML symbol URNs.
 * @author radams
 *
 */
public enum VariableSymbol {

    TIME("urn:sedml:symbol:time"),
    UNKOWN("UNKNOWN");
    
    /**
     * Getter for the URN that identifies this symbol. In SEDML l1V1, only <br/>
     * "urn:sedml:symbol:time" </br>
     * is supported.
     *  
     * @return A <code>String</code> for the URN of the symbol.
     */
	public String getUrn() {
		return urn;
	}
	private String urn;
    VariableSymbol(String urn){
    	this.urn=urn;
    }
    
    /**
     * Getter for a {@link VariableSymbol} for the supplied URN.
     * @param urn A <code>String</code>representing a URN.
     * @return The appropriate VariableSymbol or {@link VariableSymbol#UNKOWN} if not a recognized symbol.
     */
    public static VariableSymbol getVariableSymbolFor(String urn) {
    	if(urn.equals(TIME.getUrn())){
    		return TIME;
    	}
    	return UNKOWN;
    }
}
