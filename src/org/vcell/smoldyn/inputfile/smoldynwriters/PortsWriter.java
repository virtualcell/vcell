package org.vcell.smoldyn.inputfile.smoldynwriters;
//package org.vcell.smoldyn.matts_project.inputfile.smoldynwriters.modelwriters;
//
//import java.io.PrintWriter;
//
//import org.vcell.smoldyn.matts_project.inputfile.SmoldynFileKeywords;
//import org.vcell.smoldyn.matts_project.inputfile.smoldynwriters.Utilities;
//import org.vcell.smoldyn.matts_project.model.Model;
//import org.vcell.smoldyn.matts_project.model.attributes.Port;
//
//public class PortsWriter {
//
//	public static void write(Model model, PrintWriter writer) {
//		Utilities.writeUnimplementedWarning("ports", true);
////		Port [] ports = model.getPorts();
////		writer.println("# ports declarations");
////		writer.println(SmoldynFileKeywords.Port.max_port + " " + ports.length);
////		for(Port port : ports) {
////			writer.println(SmoldynFileKeywords.Port.start_port + " " + port.getName());
////			writer.println(SmoldynFileKeywords.Port.surface + " " + port.getSurface().getName());
////			writer.println(SmoldynFileKeywords.Port.face + " " + port.getFace());
////			writer.println(SmoldynFileKeywords.Port.end_port);
////		}
////		writer.println();
//	}
//}
