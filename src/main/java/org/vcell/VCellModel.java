package org.vcell;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.sbml.jsbml.SBMLDocument;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModel {

    private String name;
    private ArrayList<VCellModelParameter> parameters;
    private SBMLDocument sbmlDocument;
    private BufferedImage image;

    public VCellModel(String name) {
        this.name = name;
        parameters = new ArrayList<>();
        
        // TODO: For debugging, remove eventually
        parameters.add(new VCellModelParameter("A_diff", null, "µm2.s-1", VCellModelParameter.DIFFUSION));
        parameters.add(new VCellModelParameter("A_conc", null, "µM", VCellModelParameter.CONCENTRATION));
        parameters.add(new VCellModelParameter("B_diff", null, "µm2.s-1", VCellModelParameter.DIFFUSION));
        parameters.add(new VCellModelParameter("B_conc", null, "µM", VCellModelParameter.CONCENTRATION));
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
