package cbit.vcell.graph;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;

public class SpeciesPatternShape {

	private static final int separationWidth = 10;		// width between 2 molecular type patterns
	private int xPos = 0;
	private int yPos = 0;
	
	private List<SpeciesTypeLargeShape> speciesShapes = new ArrayList<SpeciesTypeLargeShape>();
	
	final Graphics graphicsContext;
	
	private String name;
	private Displayable owner;
	private SpeciesPattern sp;
	
	
	public SpeciesPatternShape(int xPos, int yPos, Displayable owner, SpeciesPattern sp, Graphics graphicsContext) {
		this.owner = owner;
		this.sp = sp;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;

		int numPatterns = sp.getMolecularTypePatterns().size();
		int xPattern = xPos;
		for(int i = 0; i<numPatterns; i++) {
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(i);
			SpeciesTypeLargeShape stls = new SpeciesTypeLargeShape(xPattern, yPos, mtp, graphicsContext);
			xPattern += stls.getWidth() + separationWidth; 
			speciesShapes.add(stls);
		}
	}
	
	public SpeciesPattern getSpeciesPattern() {
		return sp;
	}

	public int getX(){
		return xPos;
	}
	public int getY(){
		return yPos;
	}
	
	public void paintSelf(Graphics g) {
		for(SpeciesTypeLargeShape stls : speciesShapes) {
			stls.paintSelf(g);
		}
	}
	
}
