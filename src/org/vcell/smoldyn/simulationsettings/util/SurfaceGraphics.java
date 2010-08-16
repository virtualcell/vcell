package org.vcell.smoldyn.simulationsettings.util;

import org.vcell.smoldyn.model.Surface;
import org.vcell.smoldyn.model.util.FaceType;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * The information that Smoldyn uses to display a {@link Surface}.
 * 
 * <table border="2" id="SurfaceGraphicsTable">
 * <tr><th>data member</th><th>description</th></tr>
 * <tr><td>color</td><td>color of the surface{@link Color}</td></tr>
 * <tr><td>slices</td><td>vertical lines used to draw the surface</td></tr>
 * <tr><td>stacks</td><td>horizontal lines used to draw the surface</td></tr>
 * <tr><td>drawmode</td><td>what parts of the surface are drawn--edges, corners, etc. {@link Drawmode}</td></tr>
 * <tr><td>face</td><td>face of the surface to which this information applies {@link FaceType}</td></tr>
 * </table>
 * 
 * @author mfenwick
 *
 */
@Deprecated
public class SurfaceGraphics {

	private Color color;//front and back
	private Integer slices;
	private Integer stacks;
	private Drawmode drawmode;
	private FaceType drawfacetype;
	//thickness, stipplefactor, stipple pattern
	
	/**
	 * @param color Color
	 * @param drawmode Drawmode
	 * @param slices Integer--the number of longitudinal lines used to draw rounded panels on this surface
	 * @param stacks Integer--the number of latitudinal lines that will be used to draw rounded panels on this surface
	 * @throws NullPointerException if color or drawmode is null
	 */
	public SurfaceGraphics(Color color, Drawmode drawmode, int slices, int stacks) {
		if(color == null || drawmode == null) {
			SimulationUtilities.throwNullPointerException("color or drawmode in SurfaceGraphics");
		}
		this.color = color;
		this.drawmode = drawmode;
		this.slices = slices;
		this.stacks = stacks;
		this.drawfacetype = FaceType.both;
	}
	
	
	public Color getColor() {
		return this.color;
	}
	
	public Drawmode getDrawmode() {
		return this.drawmode;
	}
	
	public Integer getSlices() {
		return this.slices;
	}
	
	public Integer getStacks() {
		return this.stacks;
	}
	
	public FaceType getDrawfacetype() {
		return this.drawfacetype;
	}
	
	
	
	/**
	 * <table border="2" id="drawmodetableouter">
	 * <tr><td>Drawing modes for a Smoldyn {@link Surface}.</tr></td>
	 * <tr><td>
	 * <table border="1" id="drawmodetableinner">
	 * <tr><th>Draw mode</th><th>result</th></tr>
	 * <tr><td>Vertex</td><td>draw vertices</td></tr>
	 * <tr><td>Edge</td><td>draw edges</td></tr>
	 * <tr><td>Face</td><td>draw faces</td></tr>
	 * <tr><td>ve, ef, vf, and vef</td><td>combinations of vertex, edge, and face</td></tr>
	 * </table>
	 * </td></tr>
	 * </table>
	 * 
	 * @author mfenwick
	 *
	 */
	public enum Drawmode {
		
		none,
		vertex,
		edge,
		face,
		ve,//vertex + edge
		ef,//edge + face
		vf,//vertex + face
		vef,//vertex, edge, and face
		
	}
}
