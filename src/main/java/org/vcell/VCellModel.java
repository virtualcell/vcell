package org.vcell;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.sbml.jsbml.SBMLDocument;

import ij.plugin.frame.RoiManager;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModel {

    public final static int TIRF = 0;

    private String name;
    private ArrayList<VCellModelParameter> parameters;
    private SBMLDocument sbmlDocument;
    private BufferedImage image;

    public VCellModel(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    
    public ArrayList<VCellModelParameter> getParameters() {
    	return parameters;
    }

	public SBMLDocument getSbmlDocument() {
		return sbmlDocument;
	}
	
	public void setSbmlDocument(SBMLDocument sbmlDocument) {
		this.sbmlDocument = sbmlDocument;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	@Override
    public String toString() {
    	return name;
    }
}
