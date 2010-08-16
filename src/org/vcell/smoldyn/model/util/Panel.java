package org.vcell.smoldyn.model.util;

import org.vcell.smoldyn.model.Surface;



/**
 * The common interface describing the six different shapes which a panel may have,
 * according to Smoldyn's rules.  A panel may be explicitly given a name, otherwise it
 * is automatically set by Smoldyn.  Please refer to Smoldyn manual for more information.
 * The names are used to specify neighbor panels, which allows surface diffusion from
 * panel to panel.  The getSurface and setSurface methods may turn out not to be necessary--
 * Smoldyn allows panels to be neighbors even if they are from different surfaces--this
 * can allow diffusion from one surface to another.  In this case, one needs to know
 * the surface of a panel.  There are other workarounds to solve this problem, and it may
 * turn out that there will never be a need for molecules to diffuse from one surface to
 * another.  Currently these methods are not used, so setting the surface to null is 
 * not a problem.
 * @author mfenwick
 *
 */
public interface Panel {
	
	public ShapeType getShapeType();
	public String getName();
	public Surface getSurface();
	public Panel [] getNeighbors();
	public void setSurface(Surface surface);
	
	
	/**
	 * Smoldyn recognizes six shapes for {@link Panel}s.
	 * 
	 * @author mfenwick
	 *
	 */
	public enum ShapeType {
		rect, 
		tri, 
		sph, 
		cyl, 
		hemi, 
		disk,
	}
}
