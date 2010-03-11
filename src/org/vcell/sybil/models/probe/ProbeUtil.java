package org.vcell.sybil.models.probe;

import java.util.Map;

import org.vcell.sybil.util.text.StringUtil;

/*   ProbeUtil  --- by Oliver Ruebenacker, UCHC --- February 2010
 *   Useful methods for Probes, e.g. pretty print
 */

public class ProbeUtil {

	public static String prettyPrint(Probe probe) {
		StringBuffer buffer = new StringBuffer();
		prettyPrint(probe, buffer);
		return buffer.toString();
	}
	
	public static final int prettyPrintMaxDepth = 10;
	
	public static void prettyPrint(Probe probe, StringBuffer buffer) {
		prettyPrint(probe, buffer, 0, prettyPrintMaxDepth);
	}
	
	protected static void printLine(StringBuffer buffer, int depth, String text) {
		buffer.append(StringUtil.multiply("  ", depth) + text + "\n");
	}
	
	public static void prettyPrint(Probe probe, StringBuffer buffer, int depth, int depthMax) {
		if(probe instanceof Probe.Element) {
			Probe.Element element = (Probe.Element) probe;
			printLine(buffer, depth, element.key() + ": " + element.result());
		} else if(probe instanceof Probe.Group) {
			Probe.Group group = (Probe.Group) probe;
			printLine(buffer, depth, group.key());
			if(depth < depthMax) {
				for(Probe child : group.children()) {
					prettyPrint(child, buffer, depth + 1, depthMax);
				}
			} else {
				printLine(buffer, depth + 1, "[truncated]");
			}
		} else {
			printLine(buffer, depth, probe.key());
			for(Map.Entry<String, String> result: probe.results().entrySet()) {
				printLine(buffer, depth + 1, result.getKey() + ": " + result.getValue());
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(prettyPrint(ProbesDefault.probesAll));
	}
	
}
