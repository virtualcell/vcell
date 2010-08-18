package org.vcell.smoldyn.inputfile.smoldynwriters;


import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.inputfile.SmoldynWritingException;
import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.Surface;
import org.vcell.smoldyn.model.util.Cylinder;
import org.vcell.smoldyn.model.util.Disk;
import org.vcell.smoldyn.model.util.Hemisphere;
import org.vcell.smoldyn.model.util.Panel;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.Rectangle;
import org.vcell.smoldyn.model.util.Sphere;
import org.vcell.smoldyn.model.util.SurfaceActions;
import org.vcell.smoldyn.model.util.Triangle;
import org.vcell.smoldyn.model.util.Panel.ShapeType;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulation.SmoldynException;
import org.vcell.smoldyn.simulationsettings.util.Color;
import org.vcell.smoldyn.simulationsettings.util.SurfaceGraphics;


/**
 * @author mfenwick
 *
 */
public class SurfaceWriter {

	private Simulation simulation;
	private Model model;
	private PrintWriter writer;
	private Geometryable geometry;
	
	
	public SurfaceWriter(Simulation simulation, PrintWriter writer) {
		this.simulation = simulation;
		this.model = simulation.getModel();
		this.writer = writer;
		this.geometry = model.getGeometry();
	}
	
	
	public void write() throws SmoldynWritingException {
		writer.println("# surfaces");
		Surface [] surfaces = geometry.getSurfaces();
		for(Surface surface : surfaces) {
			writer.println(SmoldynFileKeywords.Surface.start_surface + " " + surface.getName());
			
			writeActions(surface);
			writeGraphics(surface);
			writePanels(surface);
			
			writeNeighbors(surface);
			writeUnboundedEmitters(surface);

			writer.println(SmoldynFileKeywords.Surface.end_surface);
			writer.println();
		}
		writer.println();
	}

	private void writeActions(Surface surface) {
		try {
			Hashtable<Species, SurfaceActions> table = model.getSurfaceActions(surface.getName());
			if(table == null) {
				return;
			}
			Set<Map.Entry<Species, SurfaceActions>> entries = table.entrySet();
			for(Map.Entry<Species, SurfaceActions> entry : entries) {
				Species s = entry.getKey();
				SurfaceActions a = entry.getValue();
				writeReflect(a, s);
				writeTransmit(a, s);
				writeAbsorb(a, s);
			}
		} catch (SmoldynException e) {
			e.printStackTrace();
		}
	}
	
	private void writeAbsorb(SurfaceActions a, Species s) {
		writer.println(SmoldynFileKeywords.Surface.rate + " " + s.getName() + " " + SmoldynFileKeywords.surfaceactionstates.bsoln + 
				" " + SmoldynFileKeywords.surfaceactionstates.back + " " + a.getBackabsorptionrate());
		writer.println(SmoldynFileKeywords.Surface.rate + " " + s.getName() + " " + SmoldynFileKeywords.surfaceactionstates.fsoln + 
				" " + SmoldynFileKeywords.surfaceactionstates.front + " " + a.getFrontabsorptionrate());
	}

	private void writeTransmit(SurfaceActions a, Species s) {
		writer.println(SmoldynFileKeywords.Surface.rate + " " + s.getName() + " " + SmoldynFileKeywords.surfaceactionstates.bsoln + 
				" " + SmoldynFileKeywords.surfaceactionstates.fsoln + " " + a.getBacktransmissionrate());
		writer.println(SmoldynFileKeywords.Surface.rate + " " + s.getName() + " " + SmoldynFileKeywords.surfaceactionstates.fsoln + 
				" " + SmoldynFileKeywords.surfaceactionstates.bsoln + " " + a.getFronttransmissionrate());
	}

	private void writeReflect(SurfaceActions a, Species s) {
		writer.println("# this is a comment:  Smoldyn automatically calculates the reflection rate");
		writer.println("#     as that which is neither transmitted nor absorbed");
	}
	
	private void writeGraphics(Surface surface) {
		SurfaceGraphics graphics = this.simulation.getSurfaceGraphics(surface);
		if (graphics != null) {
			Color front = graphics.getColor();
			writer.println(SmoldynFileKeywords.Surface.color + " " + SmoldynFileKeywords.Surface.front +" "+ front.getRed() + " " + front.getGreen()
				+ " " + front.getBlue() + " " + front.getAlpha());
			Color back = graphics.getColor();
			writer.println(SmoldynFileKeywords.Surface.color + " " + SmoldynFileKeywords.Surface.back +" "+ back.getRed() + " " + back.getGreen() 
				+ " " + back.getBlue() + " " + back.getAlpha());
			writer.println(SmoldynFileKeywords.Surface.polygon + " " + graphics.getDrawfacetype() + " " + graphics.getDrawmode());
//			writer.println(SmoldynFileKeywords.Surface.thickness + " " + graphics.getThickness());
//			writer.println(SmoldynFileKeywords.Surface.stipple + " " + graphics.getStippleFactor() + " " + graphics.getStipplePattern());
		}
		//shininess <face> <value>
	}
	
	private void writePanels(Surface surface) throws SmoldynWritingException {
		SurfaceGraphics surfacegraphics = this.simulation.getSurfaceGraphics(surface);
		HashMap<ShapeType, Integer> shapemaxes = new HashMap<ShapeType, Integer>();
		for(ShapeType s : ShapeType.values()) {
			shapemaxes.put(s, 0);
		}		
		for(Panel panel : surface.getPanels()) {
			shapemaxes.put(panel.getShapeType(), shapemaxes.get(panel.getShapeType()) + 1);
		}
		for(ShapeType s : ShapeType.values()) {
			writer.println(SmoldynFileKeywords.Surface.max_panels + " " + s + " " + shapemaxes.get(s));
		}
		for(Panel panel : surface.getPanels()) {
			if (panel.getShapeType() == ShapeType.cyl) {
				writeCylinder((Cylinder) panel, surfacegraphics);
			} else if (panel.getShapeType() == ShapeType.disk) {
				writeDisk((Disk) panel, surfacegraphics);
			} else if (panel.getShapeType() == ShapeType.hemi) {
				writeHemisphere((Hemisphere) panel, surfacegraphics);
			} else if (panel.getShapeType() == ShapeType.rect) {
				writeRectangle((Rectangle) panel, surfacegraphics);
			} else if (panel.getShapeType() == ShapeType.sph) {
				writeSphere((Sphere) panel, surfacegraphics);
			} else if (panel.getShapeType() == ShapeType.tri) {
				writeTriangle((Triangle) panel, surfacegraphics);
			} else {
				throw new RuntimeException("unrecognized shape type: " + panel.getShapeType());
			}
		}
	}


	private void writeCylinder(Cylinder cylinder, SurfaceGraphics surfacegraphics) throws SmoldynWritingException {
		Utilities.writeUnimplementedWarning("cylinders", true);
	}
	
	private void writeDisk(Disk disk, SurfaceGraphics surfacegraphics) throws SmoldynWritingException {
		Utilities.writeUnimplementedWarning("disks", true);
	}

	private void writeHemisphere(Hemisphere hemisphere, SurfaceGraphics surfacegraphics) throws SmoldynWritingException {
		Utilities.writeUnimplementedWarning("hemispheres", true);
	}

	private void writeRectangle(Rectangle rectangle, SurfaceGraphics surfacegraphics) throws SmoldynWritingException {
		final Point corner = rectangle.getCorner();
		final String sign;
		if(rectangle.isSign()) {
			sign = "+";
		} else {
			sign = "-";
		}
		writer.println(SmoldynFileKeywords.Surface.panel + " " + rectangle.getShapeType() + " " + sign + 
				rectangle.getPerpendicular_axis() + " " + corner.getX() + " " + corner.getY() + " " + corner.getZ() + " " + 
				rectangle.getDim1() + " " + rectangle.getDim2() + " " + rectangle.getName());
	}

	
	/**
	 * @param sphere
	 * @param surfacegraphics
	 */
	private void writeSphere(Sphere sphere, SurfaceGraphics surfacegraphics) {
		Point center = sphere.getCenter();
		writer.println(SmoldynFileKeywords.Surface.panel + " " + sphere.getShapeType() + " " + center.getX() 
				+ " " + center.getY() + " " + center.getZ() + " " + sphere.getRadius() + " " 
				+ surfacegraphics.getSlices() + " " + surfacegraphics.getStacks() + " " + sphere.getName());
	}

	private void writeTriangle(Triangle triangle, SurfaceGraphics surfacegraphics) {
		this.writer.print(SmoldynFileKeywords.Surface.panel + " " + triangle.getShapeType());
		for(Point point : triangle.getPoints()) {
			this.writer.print(" " + point.getX() + " " + point.getY() + " " + point.getZ());
		}
		this.writer.println();
	}
	
	private void writeUnboundedEmitters(Surface surface) throws SmoldynWritingException {
		//TODO
	}
	
	private void writeNeighbors(Surface surface) throws SmoldynWritingException {
		//TODO
	}
}
