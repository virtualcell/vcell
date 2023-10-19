/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Pair;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.Simulation;

public class SpringSaLaDExporter {
	
	/* ********** Strings to represent the different object categories ****/
	public final static String SPATIAL_INFORMATION = "SYSTEM INFORMATION";
	public final static String TIME_INFORMATION = "TIME INFORMATION";
	public final static String MOLECULES = "MOLECULES";
	public final static String MOLECULE_FILES = "MOLECULE FILES";
	public final static String DECAY_REACTIONS = "CREATION/DECAY REACTIONS";
	public final static String TRANSITION_REACTIONS = "STATE TRANSITION REACTIONS";
	public final static String ALLOSTERIC_REACTIONS = "ALLOSTERIC REACTIONS";
	public final static String BINDING_REACTIONS = "BIMOLECULAR BINDING REACTIONS";
	public final static String MOLECULE_COUNTERS = "MOLECULE COUNTERS";
	public final static String STATE_COUNTERS = "STATE COUNTERS";
	public final static String BOND_COUNTERS = "BOND COUNTERS";
	public final static String SITE_PROPERTY_COUNTERS = "SITE PROPERTY COUNTERS";
	public final static String CLUSTER_COUNTERS = "CLUSTER COUNTERS";
	public final static String SYSTEM_ANNOTATION = "SYSTEM ANNOTATIONS";
	public final static String MOLECULE_ANNOTATIONS = "MOLECULE ANNOTATIONS";
	public final static String REACTION_ANNOTATIONS = "REACTION ANNOTATIONS";

	public final static String DEFAULT_FOLDER = "Default Folder";
	private String systemName;		// The system name (same as the file name, usually)
	public final static String DEFAULT_SYSTEM_NAME = "New Model";

	String sFile;
	int ssldMajor;
	int ssldMinor;

	public SpringSaLaDExporter(String sFile, int ssldMajor, int ssldMinor) {
		this.sFile = sFile;
		this.ssldMajor = ssldMajor;
		this.ssldMinor = ssldMinor;
	}

	public String getDocumentAsString(BioModel bioModel, SimulationContext simContext) {
		
		Model model = bioModel.getModel();
		Structure[] structures = model.getStructures();
		SpeciesContext[] speciesContexts = model.getSpeciesContexts();
		List<MolecularType> molecularTypeList = model.getRbmModelContainer().getMolecularTypeList();
		List<ReactionRule> reactionRuleList = model.getRbmModelContainer().getReactionRuleList();
		
		if(simContext == null) {	// we just need an application, no simulations are needed
			throw new RuntimeException("Exporting to SpringSaLaD file format needs at least one SpringSaLaD Application");
		}
		if(simContext.getMathDescription() == null) {
			throw new RuntimeException("Math Description not found, regenerate math first.");

		}
		// make a fake simulation, when exporting we just need some default simulation properties 
		Simulation simulation = new Simulation(simContext.getMathDescription(), simContext);
		
		
		Geometry geometry = simContext.getGeometry();
		GeometryContext geometryContext = simContext.getGeometryContext();
		GeometrySpec geometrySpec = geometry.getGeometrySpec();
		ReactionContext reactionContext = simContext.getReactionContext();
		SpeciesContextSpec[] speciesContextSpecs = reactionContext.getSpeciesContextSpecs();
		ReactionSpec[] reactionSpecs = reactionContext.getReactionSpecs();
		ReactionRuleSpec[] reactionRuleSpecs = reactionContext.getReactionRuleSpecs();
		
		Map<ReactionRuleSpec, SpeciesContext> creationMap = new LinkedHashMap<> ();
		Map<ReactionRuleSpec, SpeciesContext> decayMap = new LinkedHashMap<> ();
		Map<ReactionRuleSpec, Map<String, Object>> transitionMap = new LinkedHashMap<> ();
		Map<ReactionRuleSpec, Map<String, Object>> allostericMap = new LinkedHashMap<> ();
		Map<ReactionRuleSpec, Map<String, Object>> bindingMap = new LinkedHashMap<> ();
		Map<SpeciesContext, Pair<String, String>> moleculeCreationDecayRates = new LinkedHashMap<> ();
		try {
			for(ReactionRuleSpec rrs : reactionRuleSpecs) {
				if(rrs.isExcluded()) {
					continue;
				}
				Map<String, Object> analysisResults = new LinkedHashMap<> ();
				rrs.analizeReaction(analysisResults);
				switch(rrs.getSubtype(analysisResults)) {
				case CREATION:
					SpeciesContext scCreated = rrs.getCreatedSpecies(speciesContextSpecs, analysisResults);
					// if it doesn't exist, we should make a seed species exactly like the product pattern
					// and with the same name (maybe just make an Issue)
					creationMap.put(rrs, scCreated);
					break;
				case DECAY:
					SpeciesContext scDestroyed = rrs.getDestroyedSpecies(speciesContextSpecs, analysisResults);
					// see above, we need a seed species to match the reactant pattern
					decayMap.put(rrs, scDestroyed);
					break;
				case TRANSITION:
					transitionMap.put(rrs, analysisResults);
					break;
				case ALLOSTERIC:
					allostericMap.put(rrs, analysisResults);
					break;
				case BINDING:
					bindingMap.put(rrs, analysisResults);
					break;
				default:
					break;
				}
			}
			// prepare the data for the creation/decay output
			for(SpeciesContext sc : model.getSpeciesContexts()) {
				// skip the Source and the Sink molecules (use in Creation / Destruction reactions)
				if(SpeciesContextSpec.SourceMoleculeString.equals(sc.getName()) || SpeciesContextSpec.SinkMoleculeString.equals(sc.getName())) {
					continue;
				}
				moleculeCreationDecayRates.put(sc, new Pair("0.0", "0.0"));	// initialize all species with zero
			}
			for (Map.Entry<ReactionRuleSpec, SpeciesContext> entry : creationMap.entrySet()) {
				ReactionRuleSpec rrs = entry.getKey();
				SpeciesContext sc = entry.getValue();
				Pair<String, String> oldPair = moleculeCreationDecayRates.get(sc);
				if(oldPair == null) {
					throw new RuntimeException("Molecule being created not found in the list of Species");
				}
				RbmKineticLaw kineticLaw = rrs.getReactionRule().getKineticLaw();
				Expression creationRate = kineticLaw.getLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate);
				Pair<String, String> newPair = new Pair<> (creationRate.infix(), oldPair.two);
				moleculeCreationDecayRates.put(sc, newPair);
			}
			for (Map.Entry<ReactionRuleSpec, SpeciesContext> entry : decayMap.entrySet()) {
				ReactionRuleSpec rrs = entry.getKey();
				SpeciesContext sc = entry.getValue();
				Pair<String, String> oldPair = moleculeCreationDecayRates.get(sc);
				if(oldPair == null) {
					throw new RuntimeException("Molecule being destroyed not found in the list of Species");
				}
				RbmKineticLaw kineticLaw = rrs.getReactionRule().getKineticLaw();
				Expression decayRate = kineticLaw.getLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate);
				Pair<String, String> newPair = new Pair<> (oldPair.one, decayRate.infix());
				moleculeCreationDecayRates.put(sc, newPair);
			}
		} catch(Exception ex) {
			throw new RuntimeException("Failed to categorize the reaction rules by SpringSaLaD subtype: " + ex.getMessage());
		}
		
		try {
			reactionContext.convertSpeciesIniCondition(false);	// convert to count if currently is concentration
			
			StringBuilder sb = new StringBuilder();
			/* ********* BEGIN BY WRITING THE TIMES *********/
			sb.append("*** " + TIME_INFORMATION + " ***");
			sb.append("\n");
			writeTimeInformation(sb, simulation);
			sb.append("\n");

			/* ********* WRITE THE SPATIAL INFORMATION **********/
			sb.append("*** " + SPATIAL_INFORMATION + " ***");
			sb.append("\n");
			geometryContext.writeData(sb);	// TODO: need geometry
			sb.append("\n");

			/* ******* WRITE THE SPECIES INFORMATION ***********/
			sb.append("*** " + MOLECULES + " ***");
			sb.append("\n");
			sb.append("\n");
			for(SpeciesContext sc : model.getSpeciesContexts()) {
				SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(sc);
				scs.writeData(sb);
			}
			/* ******* WRITE THE MOLECULE INFORMATION FILES ***********/
			sb.append("*** " + MOLECULE_FILES + " ***");
			sb.append("\n");
			sb.append("\n");
			for(SpeciesContext sc : model.getSpeciesContexts()) {
				// skip the Source and the Sink molecules (use in Creation / Destruction reactions)
				if(SpeciesContextSpec.SourceMoleculeString.equals(sc.getName()) || SpeciesContextSpec.SinkMoleculeString.equals(sc.getName())) {
					continue;
				}
				SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(sc);
				sb.append("MOLECULE: " + sc.getName() + " " + scs.getFilename());
				sb.append("\n");
			}
			sb.append("\n");

			/* ******* WRITE THE DECAY REACTIONS ***************/
			sb.append("*** " + DECAY_REACTIONS + " ***");
			sb.append("\n");
			sb.append("\n");
			ReactionRuleSpec.writeDecayData(sb, moleculeCreationDecayRates);
			sb.append("\n");

			/* ******* WRITE THE TRANSITION REACTIONS **********/
			sb.append("*** " + TRANSITION_REACTIONS + " ***");
			sb.append("\n");
			sb.append("\n");
			for(ReactionRuleSpec rrs : reactionRuleSpecs) {
				if(rrs.isExcluded()) {
					continue;
				}
				rrs.writeData(sb, ReactionRuleSpec.Subtype.TRANSITION);
			}
			sb.append("\n");

			/* ******* WRITE THE ALLOSTERIC REACTIONS **********/
			sb.append("*** " + ALLOSTERIC_REACTIONS + " ***");
			sb.append("\n");
			sb.append("\n");
			for(ReactionRuleSpec rrs : reactionRuleSpecs) {
				if(rrs.isExcluded()) {
					continue;
				}
				rrs.writeData(sb, ReactionRuleSpec.Subtype.ALLOSTERIC);
			}
			sb.append("\n");

			/* ******* WRITE THE BINDING REACTIONS ************/
			sb.append("*** " + BINDING_REACTIONS + " ***");
			sb.append("\n");
			sb.append("\n");
			for(ReactionRuleSpec rrs : reactionRuleSpecs) {
				if(rrs.isExcluded()) {
					continue;
				}
				rrs.writeData(sb, ReactionRuleSpec.Subtype.BINDING);
			}
			sb.append("\n");

			/* ****** WRITE THE MOLECULE COUNTERS **********/
			sb.append("*** " + MOLECULE_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(Molecule molecule: molecules) {
//				molecule.getMoleculeCounter().writeMoleculeCounter(sb);
//			}
			Simulation.Counters.writeMoleculeCounters(simContext, sb);	// everything here is initialized with default
			sb.append("\n");

			/* ******  WRITE THE STATE COUNTERS *************/
			sb.append("*** " + STATE_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(Molecule molecule : molecules) {
//				for(SiteType type : molecule.getTypeArray()) {
//					for(State state : type.getStates()) {
//						state.getStateCounter().writeStateCounter(sb);
//					}
//				}
//			}
			Simulation.Counters.writeStateCounters(simContext, sb);	// everything here is initialized with default
			sb.append("\n");

			/* ***** WRITE THE BOND COUNTERS ***************/
			sb.append("*** " + BOND_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(BindingReaction reaction: bindingReactions) {
//				reaction.getBondCounter().writeBondCounter(sb);
//			}
			Simulation.Counters.writeBondCounters(simContext, sb);	// everything here is initialized with default
			sb.append("\n");

			/* ********  WRITE THE SITE PROPERTY COUNTERS ************/
			sb.append("*** " + SITE_PROPERTY_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(Molecule molecule : molecules) {
//				ArrayList<Site> sites = molecule.getSiteArray();
//				for(Site site : sites) {
//					site.getPropertyCounter().writeSitePropertyCounter(sb);
//				}
//			}
			Simulation.Counters.writeSitePropertyCounters(simContext, sb);	// everything here is initialized with default
			sb.append("\n");

			/* *************** WRITE THE TRACK CLUSTERS BOOLEAN ***********/
			sb.append("*** " + CLUSTER_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
			sb.append("Track_Clusters: " + SpeciesContextSpec.TrackClusters);
			sb.append("\n");
			sb.append("\n");

			/* ****** WRITE THE SYSTEM ANNOTATION ********************/
			sb.append("*** " + SYSTEM_ANNOTATION + " ***");
			sb.append("\n");
			sb.append("\n");
//			systemAnnotation.printAnnotation(sb);
			sb.append("\n");

			/* ****** WRITE THE MOLECULE ANNOTATIONS *****************/
			sb.append("*** " + MOLECULE_ANNOTATIONS + " ***");
			sb.append("\n");
			sb.append("\n");
//			writeMoleculeAnnotations(sb);
			sb.append("\n");

			/* ****** WRITE THE REACTION ANNOTATIONS *****************/
			sb.append("*** " + REACTION_ANNOTATIONS + " ***");
			sb.append("\n");
			sb.append("\n");
//			writeReactionAnnotations(sb);
			sb.append("\n");

			String ret = sb.toString();
			System.out.println(ret);
			return ret;
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
		}
		return null;
	}

	private static void writeTimeInformation(StringBuilder sb, Simulation simulation) {
		// general stuff is in solver task description
		simulation.getSolverTaskDescription().writeData(sb);	// TODO: need proper sim
		
		LangevinSimulationOptions lso = simulation.getSolverTaskDescription().getLangevinSimulationOptions();
		sb.append("dt_spring: " + lso.getIntervalSpring());		// 1.00E-9 default
		sb.append("\n");
		sb.append("dt_image: " + lso.getIntervalImage());		// 1.00E-4 default
		sb.append("\n");

	}

	public void writeDocumentStringToFile(String resultString, String parent, String name, Component parentComponent) throws IOException {
		// make directory for the whole project if needed
		// save just the biomodel string as main project file
		// MainGUI.java  saveItemActionPerformed()
		String child = name + ".ssld";			// alttest.ssld
		JPanel myPanel = new JPanel();

		// TODO: we don't need a project folder when exporting since all project data will be in the vcell database
		// we only keep this code for historic reasons while implementing the springsalad app as part of vcell
		myPanel.add(new JLabel("Do you want to create a project folder?\n(Press no if saving an already existing model)"));
		int n = JOptionPane.showConfirmDialog(parentComponent, myPanel, "Create Folder Option", JOptionPane.YES_NO_OPTION);
		if(n == JOptionPane.YES_OPTION) {
			parent = parent + File.separator + name;	// C:\TEMP\ss2vcell-test\dantest\alttest
			new File(parent).mkdir();
//			File f = new File(parent + File.separator + child);							// C:\TEMP\ss2vcell-test\dantest\alttest\alttest.txt
//			g.setFile(f);

			File targetDir = new File(parent + File.separator + "structure_files");		// C:\TEMP\ss2vcell-test\dantest\alttest\structure_files
			if (!targetDir.exists() || !targetDir.isDirectory()) {
				targetDir.mkdir();
			}
		}
		String fileName = parent + File.separator + child;
		writeStringToFile(resultString, fileName, false);
		System.out.println("finished exporting to ssld");
	}

	private static void writeStringToFile(String string, String filename, boolean bUseUTF8) throws IOException {
		File outputFile = new File(filename);
		OutputStreamWriter fileOSWriter = null;
		if (bUseUTF8) {
			fileOSWriter = new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8");
		} else {
			fileOSWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
		}
		fileOSWriter.write(string);
		fileOSWriter.flush();
		fileOSWriter.close();
	}

/*
    public void copyPDBtoNewLocation(){
    	for (Molecule m: molecules) {
    		if(m.getFilename() != null) {
    			File source = new File(m.getFilename());
    			String name = source.getName();
    			File targetDir = new File(file.getParent() + File.separator + "structure_files");
    			File target = new File(file.getParent() + File.separator + "structure_files" + File.separator + name);
   			
    			if (targetDir.exists() && targetDir.isDirectory() && !target.exists() && !target.isDirectory()) {
    				//if target does not exist, open writer
    				try(PrintWriter p = new PrintWriter(new FileWriter(target), true)){
    					//open reader of source
    					try (BufferedReader br = new BufferedReader(new FileReader(source))) {
    					    String line;
    					    while ((line = br.readLine()) != null) {
    					       p.println(line);
    					    }
    					}

    					m.setFile(target.toString());
    				}catch(IOException ioe){
    		            ioe.printStackTrace(System.out);
    		        }
    			}
    		}
    	}
    }
*/
}
