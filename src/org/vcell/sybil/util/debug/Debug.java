package org.vcell.sybil.util.debug;

/*   Debug  --- by Oliver Ruebenacker, UCHC --- April to May 2008
 *   A few simple static methods to support debugging
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.RuleReasoner;

public class Debug {

	public static PrintStream out = System.out;

	static public <T> void listSet(Set<T> set, String name) {
		out.println(name + " is a set with " + set.size() + " element(s):");
		for(T element : set) {
			out.println(element);
		}		
	}
	
	static public <K, V> void listMap(Map<K, V> map, String name) {
		out.println(name + " is a map with " + map.size() + " entries:");
		for(Map.Entry<K, V> entry : map.entrySet()) {
			out.println("  " + entry.getKey().toString() + "  ->  " + entry.getValue().toString());
		}
	}
	
	static public void showBoolean(boolean bool, String name) { out.println(name + ": " + bool); }
	static public void show(Object object, String name) { out.println(name + ": " + object); }
	static public void message(String message) { out.println(message); }
	
	static public void checkInfModelValidity(InfModel infModel) {
		ValidityReport validity = infModel.validate();
		message("Validity report is valid: " + validity.isValid());
		message("Validity report is clean: " + validity.isClean());
		Iterator<?> iter = validity.getReports();
		while(iter.hasNext()) {
			Object reportObject = iter.next();
			if(reportObject instanceof ValidityReport.Report) {
				ValidityReport.Report report = (ValidityReport.Report) reportObject;
				if(report.isError) { message("Error"); }
				else { message("Warning"); }
				message(report.description);
			}
		}

	}
	
	static public void checkReasoner(Reasoner reasoner) {
		if(reasoner instanceof RuleReasoner) {
			RuleReasoner rr = (RuleReasoner) reasoner;
			message("Reasoner " + rr.toString() + " is a rule reasoner and these are the rules:");
			for(Object ruleObject : rr.getRules()) {
				if(ruleObject instanceof Rule) {
					Rule rule = (Rule) ruleObject;
					message(rule.toString());
				}
			}
		} else {
			message("Reasoner " + reasoner.toString() + " not recognized as a rule reasoner.");
		}
	}
	
	public static void printComponentHierarchy(Component component) {
		printComponentHierarchy(component, "");
	}
	
	public static void printComponentHierarchy(Component component, String prefix) {
		Dimension size = component.getSize();
		message(prefix + "(" + size.width + ", " + size.height + ") - " + component);
		if(component instanceof Container) {
			Container container = (Container) component;
			for(Component child : container.getComponents()) {
				printComponentHierarchy(child, prefix + "  ");
			}
		}
	}
	
}
