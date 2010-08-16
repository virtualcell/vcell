package org.vcell.smoldyn.model.util;


/**
 * A Smoldyn Jump allows particles to be instantaneously transported across space from one {@link Panel} to another Panel.
 * The necessity (and Smoldyn syntax and semantics) of this feature are currently insufficiently clear to require a full 
 * implementation.
 * 
 * According to Smoldyn, a Jump takes a particle from one face of Panel to another Panel's face.  The Jump may also be bidirectional, 
 * and the Panel's may belong to different Surfaces.
 * 
 * @author mfenwick
 *
 */
public class Jump {

	private Panel panel1;
	private FaceType face1;
	private Panel panel2;
	private FaceType face2;
	private boolean isBidirectional;

	
	/**
	 * @param panel1
	 * @param face1
	 * @param panel2
	 * @param face2
	 * @param isBidirectional
	 */
	public Jump(Panel panel1, FaceType face1, Panel panel2, FaceType face2, boolean isBidirectional) {
		if (panel1.getShapeType() != panel2.getShapeType()) {
			throw new RuntimeException("shapes must be identical between panels in a jump (shapes were: " 
					+ panel1.getShapeType() + " and " + panel2.getShapeType() + ")");
		}
		this.panel1 = panel1;
		this.face1 = face1;
		this.panel2 = panel2;
		this.face2 = face2;
		this.isBidirectional = isBidirectional;
	}
	
	
	public Panel getPanel1() {
		return panel1;
	}
	
	public FaceType getFace1() {
		return face1;
	}
	
	public Panel getPanel2() {
		return panel2;
	}
	
	public FaceType getFace2() {
		return face2;
	}
	
	public boolean isBidirectional() {
		return isBidirectional;
	}
	
}
