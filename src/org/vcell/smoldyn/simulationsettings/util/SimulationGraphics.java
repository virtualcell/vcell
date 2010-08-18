package org.vcell.smoldyn.simulationsettings.util;

import org.vcell.smoldyn.simulation.Simulation;


/**
 * The display information that applies to the entire Smoldyn {@link Simulation}.
 * Currently only graphicstype and graphic_iter are used.
 * 
 * graphicstype: quality of graphics (see {@link GraphicsType})
 * graphic_iter: Smoldyn will only refresh graphics display every n iterations, where n is the value of graphic_iter
 * 
 * @author mfenwick
 *
 */
public class SimulationGraphics {

	private GraphicsType graphicstype = null;
	private Integer graphic_iter = null;
	private Float graphic_delay = null;
	private Integer frame_thickness = null;
	private Color frame_color = null;
	private Integer grid_thickness = null;
	private Color grid_color = null;
	private Color background_color = null;
//	 
//	private Integer tiff_iter = null;
//	private Integer tiff_name = null;
//	private Integer tiff_min = null;
//	private Integer tiff_max = null;
	
	/**
	 * Instantiates a new SimulationGraphics with default values for graphicstype and graphic_iter.
	 * 
	 * WARNING:  Invariants have not yet been defined for this class.
	 */
	public SimulationGraphics() {
		graphicstype = GraphicsType.opengl_good;
		graphic_iter = 10;
	}
	
	
	/**
	 * 
	 * @param graphic_iter
	 * @param graphicstype
	 */
	public SimulationGraphics(Integer graphic_iter, GraphicsType graphicstype) {
		this.graphic_iter = graphic_iter;
		this.graphicstype = graphicstype;
	}


	public GraphicsType getGraphicstype() {
		return graphicstype;
	}
	
	public Integer getGraphic_iter() {
		return graphic_iter;
	}
	
	public Float getGraphic_delay() {
		return graphic_delay;
	}
	
	public Integer getFrame_thickness() {
		return frame_thickness;
	}
	
	public Color getFrame_color() {
		return frame_color;
	}
	
	public Integer getGrid_thickness() {
		return grid_thickness;
	}
	
	public Color getGrid_color() {
		return grid_color;
	}
	
	public Color getBackground_color() {
		return background_color;
	}
	
//	public Integer getTiff_iter() {
//		return tiff_iter;
//	}
//	
//	public Integer getTiff_name() {
//		return tiff_name;
//	}
//	
//	public Integer getTiff_min() {
//		return tiff_min;
//	}
//	
//	public Integer getTiff_max() {
//		return tiff_max;
//	}
	
	
	/**
	 * Graphics quality settings that Smoldyn recognizes.  From best to no graphics at all, they are: opengl_better, opengl_good,
	 * opengl, and none.
	 * 
	 * @author mfenwick
	 *
	 */
	public enum GraphicsType {

		none,
		opengl,
		opengl_good,
		opengl_better,
		
	}
}


