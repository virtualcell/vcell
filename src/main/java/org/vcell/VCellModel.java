package org.vcell;

import java.util.ArrayList;

import org.sbml.jsbml.SBMLDocument;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModel {

    public final static int TIRF = 0;

    private String name;
    private ArrayList<VCellModelParameter> parameters;
    private SBMLDocument sbmlDocument;

    public VCellModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
    	return name;
    }

	public SBMLDocument getSbmlDocument() {
		return sbmlDocument;
	}
	
	public void setSbmlDocument(SBMLDocument sbmlDocument) {
		this.sbmlDocument = sbmlDocument;
	}
}
