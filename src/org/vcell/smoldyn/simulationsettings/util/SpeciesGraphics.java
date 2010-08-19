package org.vcell.smoldyn.simulationsettings.util;

import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.Species.StateType;


/**
 * The display information pertaining to a {@link SpeciesGraphics}.  Smoldyn allows display information to vary based on 
 * {@link Species} and {@link StateType}.
 * 
 * color: the color ({@link Color})
 * displaysize: the number of pixels in the width on the display
 * 
 * @author mfenwick
 *
 */
public class SpeciesGraphics {

	private Color solutioncolor = new Color(0, 0, 0);
	private int solutiondisplaysize = 0;
	private Color upcolor = new Color(0, 0, 0);
	private int updisplaysize = 0;
	private Color downcolor = new Color(0, 0, 0);
	private int downdisplaysize = 0;
	private Color frontcolor = new Color(0, 0, 0);
	private int frontdisplaysize = 0;
	private Color backcolor = new Color(0, 0, 0);
	private int backdisplaysize = 0;

	
	public SpeciesGraphics(Color color, int displaysize) {
		
	}


	public Color getSolutioncolor() {
		return solutioncolor;
	}


	public void setSolutioncolor(Color solutioncolor) {
		this.solutioncolor = solutioncolor;
	}


	public int getSolutiondisplaysize() {
		return solutiondisplaysize;
	}


	public void setSolutiondisplaysize(int solutiondisplaysize) {
		this.solutiondisplaysize = solutiondisplaysize;
	}


	public Color getUpcolor() {
		return upcolor;
	}


	public void setUpcolor(Color upcolor) {
		this.upcolor = upcolor;
	}


	public int getUpdisplaysize() {
		return updisplaysize;
	}


	public void setUpdisplaysize(int updisplaysize) {
		this.updisplaysize = updisplaysize;
	}


	public Color getDowncolor() {
		return downcolor;
	}


	public void setDowncolor(Color downcolor) {
		this.downcolor = downcolor;
	}


	public int getDowndisplaysize() {
		return downdisplaysize;
	}


	public void setDowndisplaysize(int downdisplaysize) {
		this.downdisplaysize = downdisplaysize;
	}


	public Color getFrontcolor() {
		return frontcolor;
	}


	public void setFrontcolor(Color frontcolor) {
		this.frontcolor = frontcolor;
	}


	public int getFrontdisplaysize() {
		return frontdisplaysize;
	}


	public void setFrontdisplaysize(int frontdisplaysize) {
		this.frontdisplaysize = frontdisplaysize;
	}


	public Color getBackcolor() {
		return backcolor;
	}


	public void setBackcolor(Color backcolor) {
		this.backcolor = backcolor;
	}


	public int getBackdisplaysize() {
		return backdisplaysize;
	}


	public void setBackdisplaysize(int backdisplaysize) {
		this.backdisplaysize = backdisplaysize;
	}

}
