package org.vcell;

import java.awt.image.BufferedImage;
import java.util.List;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ExplicitRule;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.text.parser.ParseException;
import org.vcell.vcellij.api.SimulationState;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModel {

    private String name;
    private String biomodelKey;
    private SBMLDocument sbmlDocument;
    private BufferedImage image;
    private SimulationState simulationState;

    public VCellModel(String name, String biomodelKey, SBMLDocument sbmlDocument) {
        this.name = name;
        this.biomodelKey = biomodelKey;
        this.sbmlDocument = sbmlDocument;
        simulationState = SimulationState.notRun;
    }

    public String getName() {
        return name;
    }
    
    public String getBiomodelKey() {
    	return biomodelKey;
    }
    
    public List<Parameter> getParameters() {
    	return sbmlDocument.getModel().getListOfParameters();
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
	
	public SimulationState getSimulationState() {
		return simulationState;
	}
	
	public void setSimulationState(SimulationState simulationState) {
		this.simulationState = simulationState;
	}
	
	public List<Species> getSpecies() {
		if (sbmlDocument == null) return null;
		return sbmlDocument.getModel().getListOfSpecies();
	}
	
	public String getValueStringForParameter(Parameter parameter) {
		Model model = sbmlDocument.getModel();
		ExplicitRule rule = model.getRuleByVariable(parameter.getId());
		if (rule == null) {
			return Double.toString(parameter.getValue());
		}
		return ASTNode.formulaToString(rule.getMath());
	}
	
	public ASTNode parseValueStringForParameter(Parameter parameter, String value) {
		
		Model model = sbmlDocument.getModel();
		ExplicitRule rule = model.getRuleByVariable(parameter.getId());
		
		if (rule != null) {
			try {
				return ASTNode.parseFormula(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@Override
    public String toString() {
    	return name;
    }
}
