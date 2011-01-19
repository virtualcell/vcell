package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;

public class StructureCartoon extends ModelCartoon{

	public Structure getSelectedStructure() throws Exception {
		Shape shape = getSelectedShape();
		if (shape!=null){
			if (shape instanceof FeatureShape){
				return ((FeatureShape)shape).getFeature();
			}	
			if (shape instanceof MembraneShape){
				return ((MembraneShape)shape).getMembrane();
			}	
		}	
		return null;
	}

	public void propertyChange(java.beans.PropertyChangeEvent event) {
		refreshAll();
	}

	@Override
	public void refreshAll() {
		try {
			objectShapeMap.clear();
			if(getModel() != null){
				// create all StructureShapes
				Structure structures[] = getModel().getStructures();
				for (int i=0;i<structures.length;i++){
					StructureShape ss;
					if (structures[i] instanceof Feature){
						ss = new FeatureShape((Feature)structures[i],getModel(),this);
					}else if (structures[i] instanceof Membrane){
						ss = new MembraneShape((Membrane)structures[i],getModel(),this);
					}else{
						ss = null;
					}	
					addShape(ss);
					structures[i].removePropertyChangeListener(this);
					structures[i].addPropertyChangeListener(this);
				}	
				// assign children to shapes according to hierarchy in Model
				int nullParentCount=0;
				for(Shape shape : getShapes()) {
					// for each featureShape, find corresponding membraneShape
					if (shape instanceof FeatureShape){
						FeatureShape fs = (FeatureShape)shape;
						Membrane membrane = fs.getFeature().getMembrane();
						if (membrane!=null){
							// add feature as child to membrane
							MembraneShape membraneShape = (MembraneShape)getShapeFromModelObject(membrane);
							membraneShape.addChildShape(fs);
							// add membrane as child to parent feature
							Feature parentFeature = membrane.getOutsideFeature();
							if (parentFeature!=null){
								FeatureShape parentFeatureShape = (FeatureShape)getShapeFromModelObject(parentFeature);
								parentFeatureShape.addChildShape(membraneShape);
							}else{
								throw new RuntimeException("StructureCartoon.updateAll(), membrane "+membrane.getName()+" doesn't have a parent");
							}
						}else{
							nullParentCount++;
						}		
					}	
				}
				if (nullParentCount>1){
					throw new RuntimeException("StructureCartoon.updateAll(), multiple features have no membranes");
				}
				updateSpeciesContexts();
			}
			fireGraphChanged(new GraphEvent(this));
		}catch (Exception e){
			handleException(e);
		}			
	}

	private void updateSpeciesContexts() {
		if(getModel() == null){
			return;
		}
		try {
			// create all speciesShapes for structures
			Structure structures[] = getModel().getStructures();
			for (int i=0;i<structures.length;i++){
				StructureShape structureShape = (StructureShape)getShapeFromModelObject(structures[i]);
				SpeciesContext speciesContexts[] = getModel().getSpeciesContexts(structures[i]);
				for (int j=0;j<speciesContexts.length;j++){
					SpeciesContext sc = speciesContexts[j];
					SpeciesContextShape ss = new SpeciesContextShape(sc,this);
					structureShape.addChildShape(ss);
					addShape(ss);
					sc.removePropertyChangeListener(this);
					sc.addPropertyChangeListener(this);
					sc.getSpecies().removePropertyChangeListener(this);
					sc.getSpecies().addPropertyChangeListener(this);
				}
			}	
		}catch (Exception e){
			handleException(e);
		}			
	}
}