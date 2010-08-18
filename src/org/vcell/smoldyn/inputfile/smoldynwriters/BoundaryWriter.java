//package org.vcell.smoldyn.inputfile.smoldynwriters;
//
//import java.io.PrintWriter;
//
//import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
//import org.vcell.smoldyn.model.Boundaries;
//import org.vcell.smoldyn.model.Model;
//import org.vcell.smoldyn.simulation.Simulation;
//
//
//
///**
// * @author mfenwick
// *
// */
//public class BoundaryWriter {
//
//	private Model model;
//	private PrintWriter writer;
//	
//	
//	public BoundaryWriter(Simulation simulation, PrintWriter writer) {
//		this.model = simulation.getModel();
//		this.writer = writer;
//	}
//	
//	
//	public void write() {
////		System.err.println("Sorry, writing simulation boundaries is currently very poorly implemented and may not work properly");
//		writer.println("# boundaries and bounding walls");
//		double [] widths = new double[3];
//		int i = 0;
//		Boundaries b = model.getGeometry().getBoundaries();
//		writer.println(SmoldynFileKeywords.Space.boundaries + " " + i + " " + b.getXlow() + " " + b.getXhigh());
//		widths[i++] = b.getXhigh() - b.getXlow();
//		writer.println(SmoldynFileKeywords.Space.boundaries + " " + i + " " + b.getYlow() + " " + b.getYhigh());
//		widths[i++] = b.getYhigh() - b.getYlow();
//		writer.println(SmoldynFileKeywords.Space.boundaries + " " + i + " " + b.getZlow() + " " + b.getZhigh());
//		widths[i++] = b.getZhigh() - b.getZlow();
//		
//		writer.println();
//		String [] wallstart = {"start_surface boundingwalls", "action both all reflect", "color both 0 0 0",//"max_surface 10",  
//		"polygon both edge", "max_panels rect 6"};
//		for(String line : wallstart) {
//			writer.println(line);
//		}
//		double [][] walls = dims2Rects(widths[0], widths[1], widths[2]);
//		for(double [] wall : walls) {
//			String asString = arrayToString(wall);
//			writer.println(SmoldynFileKeywords.Surface.panel + " rect -" + asString);
//		}
//		writer.println(SmoldynFileKeywords.Surface.end_surface.toString());
//		writer.println();
//		writer.println();
//	}
//	
//	
//
//	private static double [] [] dims2Rects(double x, double y, double z) {
//		double [] x1 = {0, 0, 0, 0, y, z};
//		double [] x2 = {0, x, 0, 0, y, z};//should be a negative in front of the first 0
//		double [] y1 = {1, 0, 0, 0, x, z};
//		double [] y2 = {1, 0, y, 0, x, z};//should be a negative in front of the first 1
//		double [] z1 = {2, 0, 0, 0, x, y};
//		double [] z2 = {2, 0, 0, z, x, y};//should be a negative in front of the first 2
//		double [][] out = {x1, x2, y1, y2, z1, z2};
//		return out;
//	}
//	
//	
//	private static String arrayToString(double [] wall) {
//		String out = Double.toString(wall[0]);
//		for(int i = 1; i < wall.length; i++) {
//			out = out + " " + Double.toString(wall[i]);
//		}
//		return out;
//	}
//}
