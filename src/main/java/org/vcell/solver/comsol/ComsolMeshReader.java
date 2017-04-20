package org.vcell.solver.comsol;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.vcell.util.PropertyLoader;
import org.vcell.vis.vismesh.thrift.Vect3D;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisPolygon;
import org.vcell.vis.vismesh.thrift.VisTetrahedron;
import org.vcell.vis.vtk.VisMeshUtils;
import org.vcell.vis.vtk.VtkService;


public class ComsolMeshReader {
	
	public static class Field {
		public final String name;
		public final String units;
		public final double time;
		public final double[] data;
		
		public Field(String name, String units, double time, double[] data){
			this.name = name;
			this.units = units;
			this.time = time;
			this.data = data;
		}
	}

	public static void main(String[] args) {
		try {
			System.getProperties().setProperty(PropertyLoader.installationRoot, "d:\\developer\\eclipse\\workspace_refactor\\VCell_6.1_userdata");
			File comsolFile = new File("simoutput_multitime.txt");
			VisMesh visMesh = new VisMesh();
			read(visMesh, comsolFile);
			System.out.println("done");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void read(VisMesh visMesh, File comsolFile) throws IOException{
		ArrayList<Field> fields = new ArrayList<Field>();
		try (BufferedReader br = new BufferedReader(new FileReader(comsolFile))) {
			//
			// read header lines
			//
			String model = readStringParameter(br.readLine(),"Model");
			String version = readStringParameter(br.readLine(), "Version");
			String date = readStringParameter(br.readLine(), "Date");
			int dimension = readIntegerParameter(br.readLine(), "Dimension");
			int numNodes = readIntegerParameter(br.readLine(), "Nodes");
			int numElements = readIntegerParameter(br.readLine(), "Elements");
			int numExpressions = readIntegerParameter(br.readLine(), "Expressions");
			String description = readStringParameter(br.readLine(),"Description");
			String lengthUnit = readStringParameter(br.readLine(),"Length unit");

			visMesh.setDimension(dimension);
			
			//
			// read coordinates (and set points, origin and extent on VisMesh
			//
			readCoordinates(br, visMesh, numNodes);
			List<VisPoint> points = visMesh.getPoints();
			double minX = Double.POSITIVE_INFINITY;
			double minY = Double.POSITIVE_INFINITY;
			double minZ = Double.POSITIVE_INFINITY;
			double maxX = Double.NEGATIVE_INFINITY;
			double maxY = Double.NEGATIVE_INFINITY;
			double maxZ = Double.NEGATIVE_INFINITY;
			for (VisPoint p : points){
				minX = Math.min(p.x,minX);
				minY = Math.min(p.y,minY);
				minZ = Math.min(p.z,minZ);
				maxX = Math.max(p.x,maxX);
				maxY = Math.max(p.y,maxY);
				maxZ = Math.max(p.z,maxZ);
			}
			visMesh.setExtent(new Vect3D(maxX-minX, maxY-minY, maxZ-minZ));
			visMesh.setOrigin(new Vect3D(minX, minY, minZ));
						
			//
			// read triangles
			//
			readElements(br, visMesh, numElements);
			
			//
			// read fields
			//
			for (int fieldIndex=0; fieldIndex<numExpressions;fieldIndex++){
				fields.add(readField(br,numNodes));
			}
		}
		VtkService vtkService = VtkService.getInstance();
		String prefix = comsolFile.getName().replace(".comsoldat","");
		File vtuFile = new File(comsolFile.getParentFile(),prefix+".vtu");
		File indexFile = new File(comsolFile.getParentFile(),prefix+".comsolindex");
		vtkService.writeComsolVtkGridAndIndexData(visMesh, "domain", vtuFile, indexFile);
		HashSet<Double> times = new HashSet<Double>();
		for (Field field : fields){
			times.add(field.time);
			int timeIndex = times.size()-1;
			File outputMeshFile = new File(comsolFile.getParentFile(), prefix+"_"+field.name+"_"+timeIndex+".vtu");
			VisMeshUtils.writePointDataToVtu(vtuFile, field.name, field.data, outputMeshFile);
		}
	}
	
	private static Field readField(BufferedReader br, int numPoints) throws IOException{
		String firstLine = br.readLine();
		//    "% Data (c (mol/m^3) @ t=1)"
		String elementsTag = "% Data (";
		if (!firstLine.trim().startsWith(elementsTag)){
			throw new RuntimeException("read "+firstLine+"\nexpected to start with "+elementsTag);
		}
		String[] tokens = firstLine.replace(elementsTag, "").split("@");
		//   "c"  "(mol/m^3)"  "t=1)"
		String varName = tokens[0].split(" ")[0].trim();
		String varUnit = tokens[0].split(" ")[1].trim();
		double time = Double.parseDouble(tokens[1].replace("t=", "").replace(")","").trim());
		
		double[] data = new double[numPoints];
		for (int i=0;i<numPoints;i++){
			String valueString = br.readLine();
			data[i] = Double.parseDouble(valueString);
		}
		return new Field(varName,varUnit,time,data);
	}

	private static void readElements(BufferedReader br, VisMesh visMesh, int numElements) throws IOException{
		String line = br.readLine();
		String tetrahedraTag = "% Elements (tetrahedra)";
		String trianglesTag = "% Elements (triangles)";
		if (line.trim().equals(trianglesTag)){
			for (int i=0;i<numElements;i++){
				line = br.readLine();
				String[] tokens = line.split("\\ +");
				ArrayList<Integer> points = new ArrayList<Integer>();
				points.add(Integer.parseInt(tokens[0])-1);
				points.add(Integer.parseInt(tokens[1])-1);
				points.add(Integer.parseInt(tokens[2])-1);
				visMesh.addToPolygons(new VisPolygon(points));
			}
		}else if (line.trim().equals(tetrahedraTag)){
			for (int i=0;i<numElements;i++){
				line = br.readLine();
				String[] tokens = line.split("\\ +");
				ArrayList<Integer> points = new ArrayList<Integer>();
				points.add(Integer.parseInt(tokens[0])-1);
				points.add(Integer.parseInt(tokens[1])-1);
				points.add(Integer.parseInt(tokens[2])-1);
				points.add(Integer.parseInt(tokens[3])-1);
				visMesh.addToTetrahedra(new VisTetrahedron(points));
			}
		}else{
			throw new RuntimeException("read "+line+"\nexpected "+tetrahedraTag+" or "+trianglesTag);
		}
	}

	private static void readCoordinates(BufferedReader br, VisMesh visMesh, int numNodes) throws IOException{
		String line = br.readLine();
		String coordinatesTag = "% Coordinates";
		if (!line.trim().equals(coordinatesTag)){
			throw new RuntimeException("read "+line+"\nexpected "+coordinatesTag);
		}
		for (int i=0;i<numNodes;i++){
			line = br.readLine();
			String[] tokens = line.split("\\ +");
			double x = Double.parseDouble(tokens[0]);
			double y = Double.parseDouble(tokens[1]);
			double z = 0.0;
			visMesh.addToPoints(new VisPoint(x,y,z));
		}
	}

	private static String readStringParameter(String line, String paramName) throws IOException {
		String value;
		if (line.startsWith("% "+paramName+":")){
			value = line.split(":")[1].trim();
		}else{
			throw new RuntimeException("read "+line+"\nexpected "+paramName);
		}
		return value;
	}

	private static int readIntegerParameter(String line, String paramName) throws IOException {
		if (line.startsWith("% "+paramName+":")){
			return Integer.parseInt(line.split(":")[1].trim());
		}else{
			throw new RuntimeException("read "+line+"\nexpected "+paramName);
		}
	}

}
