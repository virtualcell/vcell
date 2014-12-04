package org.vcell.model.rbm;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

import java.util.StringTokenizer;

public class RbmNetworkGenerator {
	
	private static final String BEGIN_MODEL = "begin model";
	private static final String END_MODEL = "end model";
	private static final String BEGIN_REACTIONS = "begin reaction rules";
	private static final String END_REACTIONS = "end reaction rules";
	private static final String BEGIN_OBSERVABLES = "begin observables";
	private static final String END_OBSERVABLES = "end observables";
	private static final String BEGIN_FUNCTIONS = "begin functions";
	private static final String END_FUNCTIONS = "end functions";
	private static final String END_MOLECULE_TYPES = "end molecule types";
	private static final String BEGIN_MOLECULE_TYPES = "begin molecule types";
	private static final String BEGIN_SPECIES = "begin seed species";
	private static final String END_SPECIES = "end seed species";
	private static final String END_PARAMETERS = "end parameters";
	private static final String BEGIN_PARAMETERS = "begin parameters";

	public static void writeBngl(BioModel bioModel, PrintWriter writer) {
		SimulationContext sc = bioModel.getSimulationContexts()[0];	// TODO: we assume one single simulation context which may not be the case
		writeBngl(sc, writer, false);
	}
	public static void writeBngl(SimulationContext simulationContext, PrintWriter writer, boolean ignoreFunctions) {
		Model model = simulationContext.getModel();
		RbmModelContainer rbmModelContainer = model.getRbmModelContainer();
		
		writer.println(BEGIN_MODEL);
		writer.println();
		
		writer.println(BEGIN_PARAMETERS);
		List<Parameter> paramList = rbmModelContainer.getParameterList();
		for (Parameter param : paramList) {
			writer.println(RbmUtils.toBnglString(param,false));
		}
		if(ignoreFunctions) {	// we cheat and transform all functions into constant parameters
			List<Parameter> functionList = rbmModelContainer.getFunctionList();
			for (Parameter function : functionList) {
				writer.println(RbmUtils.toBnglStringIgnoreExpression(function));
			}
		}
		writer.println(END_PARAMETERS);
		writer.println();
		
		writer.println(BEGIN_MOLECULE_TYPES);
		List<MolecularType> molList = rbmModelContainer.getMolecularTypeList();
		for (MolecularType mt : molList) {
			writer.println(RbmUtils.toBnglString(mt));
		}
		writer.println(END_MOLECULE_TYPES);
		writer.println();
		
		writer.println(BEGIN_SPECIES);
		SpeciesContext[] speciesContexts = model.getSpeciesContexts();
		for(SpeciesContext sc : speciesContexts) {
			if(!sc.hasSpeciesPattern()) { continue; }
			writer.println(RbmUtils.toBnglString(simulationContext, sc));
		}
		writer.println(END_SPECIES);
		writer.println();
				
		writer.println(BEGIN_OBSERVABLES);
		List<RbmObservable> observablesList = rbmModelContainer.getObservableList();
		for (RbmObservable oo : observablesList) {
			writer.println(RbmUtils.toBnglString(oo));
		}
		writer.println(END_OBSERVABLES);
		writer.println();

		if(!ignoreFunctions) {
			writer.println(BEGIN_FUNCTIONS);
			List<Parameter> functionList = rbmModelContainer.getFunctionList();
			for (Parameter function : functionList) {
				writer.println(RbmUtils.toBnglString(function,true));
			}
			writer.println(END_FUNCTIONS);
			writer.println();
		}

		writer.println(BEGIN_REACTIONS);
		List<ReactionRule> reactionList = rbmModelContainer.getReactionRuleList();
		for (ReactionRule rr : reactionList) {
			writer.println(RbmUtils.toBnglStringLong(rr));
		}
		writer.println(END_REACTIONS);	
		writer.println();
		
		writer.println(END_MODEL);	
		writer.println();
		
		NetworkConstraints constraints = rbmModelContainer.getNetworkConstraints();
		writer.print("generate_network({");
		writer.print("max_iter=>" + constraints.getMaxIteration());
		writer.print(",");
		writer.print("max_agg=>" + constraints.getMaxMoleculesPerSpecies());
		StringBuilder max_stoich = new StringBuilder(); 
		for (MolecularType mt : molList) {
			Integer stoich = constraints.getMaxStoichiometry(mt);
			if (stoich != null) {
				if (max_stoich.length() > 0) {
					max_stoich.append(",");
				}
				max_stoich.append(mt.getName() + "=>" + stoich);
			}
		}
		if (max_stoich.length() > 0) {
			writer.print(",max_stoich={" + max_stoich + "}");
		}
		writer.print(",overwrite=>1");
		writer.println("})");
		writer.println();
	}
	
	private static class ReactionLine {
		String no;
		String reactants;
		String products;
		String ruleLabel;
		private ReactionLine(String no, String reactants, String products,
				String ruleLabel) {
			super();
			this.no = no;
			this.reactants = reactants;
			this.products = products;
			this.ruleLabel = ruleLabel;
		}
	}
	
	public static void generateModel(BioModel bioModel, String netfile) throws Exception {
		Model model = bioModel.getModel();
		Map<String, SpeciesContext> speciesMap = new HashMap<String, SpeciesContext>();
		Map<String, ReactionStep> reactionMap = new HashMap<String, ReactionStep>();
		List<ReactionLine> reactionLineList = new ArrayList<ReactionLine>();
		BufferedReader br = new BufferedReader(new StringReader(netfile));
		int reversibleCount = 0;
		int reactionCount = 0;
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
			if (line.equals(BEGIN_PARAMETERS)) {
				while (true) {
					String line2 = br.readLine();
					line2 = line2.trim();
					if (line2.length() == 0) {
						continue;
					}
					if (line2.equals(END_PARAMETERS)) {
						break;
					}					
					StringTokenizer st = new StringTokenizer(line2);
					String token1 = st.nextToken();
					String token2 = st.nextToken();
					String token3 = st.nextToken();
					ModelParameter mp = model.new ModelParameter(token2, new Expression(token3), Model.ROLE_UserDefined, bioModel.getModel().getUnitSystem().getInstance_TBD());
					model.addModelParameter(mp);
				}
			} else if (line.equals(BEGIN_SPECIES)) {
				while (true) {
					String line2 = br.readLine();
					line2 = line2.trim();
					if (line2.length() == 0) {
						continue;
					}
					if (line2.equals(END_SPECIES)) {
						break;
					}					
					
					StringTokenizer st = new StringTokenizer(line2);
					String token1 = st.nextToken();  // no
					String token2 = st.nextToken();  // pattern
					String token3 = st.nextToken();  // initial condition
					
					String newname = token2.replaceAll("\\." , "_");
					newname = newname.replaceAll("[\\(,][a-zA-Z]\\w*", "");
					newname = newname.replaceAll("~|!\\d*", "");
					newname = newname.replaceAll("\\(\\)", "");
					newname = newname.replaceAll("\\)", "");
					
					SpeciesContext sc = model.createSpeciesContext(model.getStructure(0));
					sc.setName(newname);
					bioModel.getVCMetaData().setFreeTextAnnotation(sc, token2);					
					bioModel.getVCMetaData().setFreeTextAnnotation(sc.getSpecies(), token2);					
					speciesMap.put(token1, sc);
				}				
			} else if (line.equals(BEGIN_REACTIONS)) {
				while (true) {
					String line2 = br.readLine();
					line2 = line2.trim();
					if (line2.length() == 0) {
						continue;
					}
					if (line2.equals(END_REACTIONS)) {
						break;
					}
					++ reactionCount;
					
					StringTokenizer st = new StringTokenizer(line2);
					String token1 = st.nextToken();
					String token2 = st.nextToken(); // reactants
					String token3 = st.nextToken(); // products
					String token4 = st.nextToken(); // rate
					String token5 = st.nextToken();
					
					boolean bFoundReversible = false;
					Expression rate = new Expression(token4);
					for (ReactionLine rl : reactionLineList) {
						if (token2.equals(rl.products) && token3.equals(rl.reactants) && token5.equals(rl.ruleLabel + "r")) {
							ReactionStep rs = reactionMap.get(rl.no);
							((MassActionKinetics)rs.getKinetics()).getReverseRateParameter().setExpression(rate);
							reactionLineList.remove(rl);
							bFoundReversible = true;
							break;
						}
					}
					if (bFoundReversible) {
						++ reversibleCount;
						continue;
					}	
					ReactionLine rl = new ReactionLine(token1, token2, token3, token5);
					reactionLineList.add(rl);
					SimpleReaction reaction = model.createSimpleReaction(model.getStructure(0));
					reactionMap.put(token1, reaction);
					
					reaction.setModel(model);
					bioModel.getVCMetaData().setFreeTextAnnotation(reaction, line2);
					MassActionKinetics kinetics = new MassActionKinetics(reaction);
					reaction.setKinetics(kinetics);
					
					st = new StringTokenizer(token2, ",");
					while (st.hasMoreTokens()) {
						String t = st.nextToken();
						SpeciesContext sc = speciesMap.get(t);
						if (sc != null) {
							boolean bExists = false;
							for (ReactionParticipant rp : reaction.getReactionParticipants()) {
								if (rp instanceof Reactant && rp.getSpeciesContext() == sc) {
									rp.setStoichiometry(rp.getStoichiometry() + 1);
									bExists = true;
									break;
								}
							}
							if (!bExists) {
								reaction.addReactant(sc, 1);
							}
						}
					}
					st = new StringTokenizer(token3, ",");
					while (st.hasMoreTokens()) {
						String t = st.nextToken();
						SpeciesContext sc = speciesMap.get(t);
						if (sc != null) {
							boolean bExists = false;
							for (ReactionParticipant rp : reaction.getReactionParticipants()) {
								if (rp instanceof Product && rp.getSpeciesContext() == sc) {
									rp.setStoichiometry(rp.getStoichiometry() + 1);
									bExists = true;
									break;
								}
							}
							if (!bExists) {
								reaction.addProduct(sc, 1);
							}
						}
					}					
					kinetics.getForwardRateParameter().setExpression(rate);
				}
			}
		}
		System.out.println(model.getNumSpecies() + " species added");
		System.out.println(model.getNumReactions() + " reactions added");
		System.out.println(reversibleCount + " reversible reactions found");
		if (reactionCount != model.getNumReactions() + reversibleCount) {
			throw new RuntimeException("Reactions are not imported correctly!");
		}
	}
}
