package org.vcell.model.rbm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import org.vcell.model.bngl.ASTAction;
import org.vcell.model.bngl.ASTAddNode;
import org.vcell.model.bngl.ASTAndNode;
import org.vcell.model.bngl.ASTAttributePattern;
import org.vcell.model.bngl.ASTBondExist;
import org.vcell.model.bngl.ASTBondPossible;
import org.vcell.model.bngl.ASTBondState;
import org.vcell.model.bngl.ASTCompartment;
import org.vcell.model.bngl.ASTCompartmentsBlock;
import org.vcell.model.bngl.ASTExpression;
import org.vcell.model.bngl.ASTFloatNode;
import org.vcell.model.bngl.ASTFuncNode;
import org.vcell.model.bngl.ASTFunctionDecl;
import org.vcell.model.bngl.ASTFunctionsBlock;
import org.vcell.model.bngl.ASTIdNode;
import org.vcell.model.bngl.ASTInvertTermNode;
import org.vcell.model.bngl.ASTKineticsParameter;
import org.vcell.model.bngl.ASTLiteralNode;
import org.vcell.model.bngl.ASTMinusTermNode;
import org.vcell.model.bngl.ASTModel;
import org.vcell.model.bngl.ASTMolecularComponentPattern;
import org.vcell.model.bngl.ASTMolecularDefinitionBlock;
import org.vcell.model.bngl.ASTMolecularTypePattern;
import org.vcell.model.bngl.ASTMultNode;
import org.vcell.model.bngl.ASTNotNode;
import org.vcell.model.bngl.ASTObservable;
import org.vcell.model.bngl.ASTObservablePattern;
import org.vcell.model.bngl.ASTObservablesBlock;
import org.vcell.model.bngl.ASTOrNode;
import org.vcell.model.bngl.ASTParameter;
import org.vcell.model.bngl.ASTParameterBlock;
import org.vcell.model.bngl.ASTPowerNode;
import org.vcell.model.bngl.ASTProduct;
import org.vcell.model.bngl.ASTReactant;
import org.vcell.model.bngl.ASTReactionRule;
import org.vcell.model.bngl.ASTReactionRulesBlock;
import org.vcell.model.bngl.ASTRelationalNode;
import org.vcell.model.bngl.ASTSeedSpecies;
import org.vcell.model.bngl.ASTSeedSpeciesBlock;
import org.vcell.model.bngl.ASTSpeciesPattern;
import org.vcell.model.bngl.BNGLParser;
import org.vcell.model.bngl.BNGLParserVisitor;
import org.vcell.model.bngl.Node;
import org.vcell.model.bngl.ParseException;
import org.vcell.model.bngl.SimpleNode;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.util.Pair;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.client.ClientRequestManager.BngUnitSystem;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Action;
import cbit.vcell.math.ParticleComponentStateDefinition;
import cbit.vcell.math.ParticleComponentStatePattern;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleMolecularComponent;
import cbit.vcell.math.ParticleMolecularComponentPattern;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.math.ParticleMolecularTypePattern;
import cbit.vcell.math.ParticleSpeciesPattern;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.Expression.FunctionFilter;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;

public class RbmUtils {
	
	@Deprecated
	public static int reactionRuleLabelIndex;
	@Deprecated
	public static ArrayList<String> reactionRuleNames = new ArrayList<String>();
	public static final String SiteStruct = "AAA";		// site indicating compartment
	public static final String SiteProduct = "AAB";		// site indicating product index, 0 if reactant (seed species)
	
	public static class BnglObjectConstructionVisitor implements BNGLParserVisitor {
		private boolean stopOnError = true;	// throw exception if object which should have been there is missing
		private Model model = null;
		private List<SimulationContext> appList = null;
		private final BngUnitSystem bngUnitSystem;		// never null
		
		public BnglObjectConstructionVisitor() {
			this(null, null, null, true);
		}
		public BnglObjectConstructionVisitor(Model model, List<SimulationContext> appList, boolean stopOnError) {
			this(model, appList, null, stopOnError);
		}
		public BnglObjectConstructionVisitor(Model model, List<SimulationContext> appList, BngUnitSystem bngUnitSystem, boolean stopOnError) {
			this.bngUnitSystem = bngUnitSystem;
			this.stopOnError = stopOnError;
			this.model = model;
			this.appList = appList;
		}
		
		public Object visit(SimpleNode node, Object data) {
			return null;
		}

		public Object visit(ASTModel node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		public Object visit(ASTParameterBlock node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		public Object visit(ASTParameter node, Object data) {
			try {
				if (model.getRbmModelContainer().getParameter(node.getName()) == null) {
					Expression exp = null;
					String expS = node.getExpressionString();
					if(expS == null || expS.equals("")) {
						exp = new Expression(0);
					} else {
						exp = new Expression(expS);
					}
					exp.bindExpression(model.getRbmModelContainer().getSymbolTable());
					model.getRbmModelContainer().addParameter(node.getName(), exp, model.getUnitSystem().getInstance_TBD());
				}
			} catch (Exception ex) {
				throw new RuntimeException("Parameter '" + node.getName() + " can not be added, " + ex.getMessage());
			}
			return null;
		}

		public Object visit(ASTMolecularDefinitionBlock node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		public Object visit(ASTSeedSpeciesBlock node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		public Object visit(ASTSeedSpecies node, Object data) {
			assert model != null;
			assert appList != null;
			SeedSpecies seedSpecies = new SeedSpecies();		// limited scope, we only need it to allow for a proper visitation process
			node.childrenAccept(this, seedSpecies);
			try {
				Expression exp = new Expression(node.getInitial());
				seedSpecies.setInitialCondition(exp);
			if (data instanceof RbmModelContainer) {
				try {
					Structure structure;
					String strStruct = node.getCompartment();
					if(strStruct != null && !strStruct.isEmpty()) {
						structure = model.getStructure(strStruct);
					} else {
						structure = model.getStructure(0);
					}
					SpeciesContext speciesContext = model.createSpeciesContext(structure, seedSpecies.getSpeciesPattern());
					for(SimulationContext application : appList) {
						SpeciesContextSpec scs = application.getReactionContext().getSpeciesContextSpec(speciesContext);
						if (bngUnitSystem.isConcentration() && application.isUsingConcentration()){
							scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration).setExpression(exp);
						}else if (!bngUnitSystem.isConcentration() && !application.isUsingConcentration()){
							scs.getParameter(SpeciesContextSpec.ROLE_InitialCount).setExpression(exp);
						}else if (!bngUnitSystem.isConcentration() && application.isUsingConcentration()){
							Expression covertedConcentration = scs.convertParticlesToConcentration(exp);
							scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration).setExpression(covertedConcentration);
						}else if (bngUnitSystem.isConcentration() && !application.isUsingConcentration()){
							Expression covertedAmount = scs.convertConcentrationToParticles(exp);
							scs.getParameter(SpeciesContextSpec.ROLE_InitialCount).setExpression(covertedAmount);
						}
						scs.setConstant(node.isClamped());
					}
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					throw new RuntimeException(ex.getMessage());
				}
			}
			return seedSpecies;
			} catch (Exception ex) {
				throw new RuntimeException("Initial condition for Species " + toBnglString(seedSpecies.getSpeciesPattern(), null, CompartmentMode.hide, 0) + " can not be initialized: " + ex.getMessage());
			}	
		}

		public Object visit(ASTReactionRulesBlock node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		public Object visit(ASTObservablesBlock node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		public Object visit(ASTAction node, Object data) {
			return null;
		}
		
		public Object visit(ASTReactionRule node, Object data) {
			if (data instanceof RbmModelContainer) {
				// initialize with default structure as a placeholder, we'll set the structure properly a little below
				ReactionRule reactionRule = model.getRbmModelContainer().createReactionRule(node.getLabel(), model.getStructures()[0], node.getArrowString().equals("<->"));
				node.childrenAccept(this, reactionRule);
				if(model.getStructures().length > 0) {
					// pick a structure from the participants' structures
					// TODO: use the proper structure for the keyword once it gets implemented in the parser
					Structure structure = RbmUtils.findStructure(model, reactionRule);
					reactionRule.setStructure(structure);
				}
				try {
					model.getRbmModelContainer().addReactionRule(reactionRule);
				} catch (PropertyVetoException ex) {
					ex.printStackTrace();
					throw new RuntimeException("Unexpected " + ReactionRule.typeName + " exception: " + ex.getMessage());
				}
				return reactionRule;
			}
			return null;
		}

		public Object visit(ASTReactant node, Object data) {
	      for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
	    	  Node child = node.jjtGetChild(i);
	    	  Object object = child.jjtAccept(this, data);
	    	  if (data instanceof ReactionRule && object instanceof SpeciesPattern) {
	    		  ReactionRule rr = (ReactionRule) data;
	    		  SpeciesPattern sp = (SpeciesPattern) object;
	    		  String structureName = node.getCompartment();
	    		  Structure structure;
	    		  if(structureName != null && !structureName.isEmpty()) {
	    			  structure = model.getStructure(structureName);
	    		  } else {
	    			  structure = rr.getStructure();
	    		  }
	    		  // if even one molecular type pattern in the rule has explicit match, we assume that what we parse has 
	    		  // full match information already and don't try to rematch ourselves
	    		  if(sp.hasExplicitParticipantMatch() || rr.hasExplicitParticipantMatch()) {
	    			  rr.addReactant(new ReactantPattern(sp, structure), false);
	    		  } else {
	    			  rr.addReactant(new ReactantPattern(sp, structure));
	    		  }
	    	  }
	      }
	      return null;
		}

		public Object visit(ASTProduct node, Object data) {
			for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
		    	  Node child = node.jjtGetChild(i);
		    	  Object object = child.jjtAccept(this, data);
		    	  if (data instanceof ReactionRule && object instanceof SpeciesPattern) {
		    		  ReactionRule rr = (ReactionRule) data;
		    		  SpeciesPattern sp = (SpeciesPattern) object;
		    		  String structureName = node.getCompartment();
		    		  Structure structure;
		    		  if(structureName != null && !structureName.isEmpty()) {
		    			  structure = model.getStructure(structureName);
		    		  } else {
		    			  structure = rr.getStructure();
		    		  }
		    		  // if even one molecular type pattern in the rule has explicit match, we assume that what we parse has 
		    		  // full match information already and don't try to rematch ourselves
		    		  if(rr.hasExplicitParticipantMatch()) {
		    			  rr.addProduct(new ProductPattern(sp, structure), false);
		    		  } else {
		    			  rr.addProduct(new ProductPattern(sp, structure));
		    		  }
		    	  }
		     }
		     return null;
		}
		
//		public Object visit(ASTKineticsParameter node, Object data) {
//			if (data instanceof ReactionRule) {
//				ReactionRule rr = (ReactionRule)data;
//				
//				int parameterIndex = 0;
//				for (int i=0; i<node.jjtGetParent().jjtGetNumChildren(); i++){
//					if (node.jjtGetParent().jjtGetChild(i) == node){
//						break;
//					}
//					if (node.jjtGetParent().jjtGetChild(i) instanceof ASTKineticsParameter){
//						parameterIndex++;
//					}
//				}
//
//				if (parameterIndex == 0) {
//					try {
//						rr.getKineticLaw().setParameterValue(RbmKineticLaw.ParameterType.MassActionForwardRate, new Expression(node.getValue()));
//					} catch (PropertyVetoException | ExpressionException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				} else {
//					try {
//						rr.getKineticLaw().setParameterValue(RbmKineticLaw.ParameterType.MassActionReverseRate, new Expression(node.getValue()));
//					} catch (ExpressionException | PropertyVetoException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//			}
//			return null;
//		}

		public Object visit(ASTKineticsParameter node, Object data) {
			if (data instanceof ReactionRule) {
				ReactionRule rr = (ReactionRule)data;
				//
				// BngUnitSystem already specified the ModelUnitSystem for this vcell model.
				// this tells us what the assumed units are for time, volume, concentration, etc.
				// 
				// for first order mass action kinetics the K_forward and K_reverse will be consistent no matter what (1/time)
				// for "molecular" based models, we have to translate Nth-order mass action kinetic parameters we have to divide by volume^(N-1) ... for N>1
				//
				int parameterIndex = 0;
				for (int i=0; i<node.jjtGetParent().jjtGetNumChildren(); i++){
					if (node.jjtGetParent().jjtGetChild(i) == node){
						break;
					}
					if (node.jjtGetParent().jjtGetChild(i) instanceof ASTKineticsParameter){
						parameterIndex++;
					}
				}
				if (parameterIndex == 0) {
					try {
						int numReactants = rr.getReactantPatterns().size();
						if (numReactants > 1 && !bngUnitSystem.isConcentration()){
							Double bnglModelVolume = bngUnitSystem.getVolume();
							double reactantsFactor = Math.pow(bnglModelVolume,numReactants-1);
							Expression correctedRate = Expression.mult(new Expression(node.getValue()),new Expression(reactantsFactor)).flatten();
							rr.getKineticLaw().setLocalParameterValue(RbmKineticLawParameterType.MassActionForwardRate, correctedRate);
						}else{
							String value = node.getValue();
							Expression newExpression = getBoundExpression(value, model.getRbmModelContainer().getSymbolTable());
							//Expression newExpression = new Expression(value);
							RbmKineticLaw kineticLaw = rr.getKineticLaw();
							kineticLaw.setLocalParameterValue(RbmKineticLawParameterType.MassActionForwardRate, newExpression);
						}
					} catch (PropertyVetoException | ExpressionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					try {
						int numProducts = rr.getProductPatterns().size();
						if (numProducts > 1 && !bngUnitSystem.isConcentration()){
							Double bnglModelVolume = bngUnitSystem.getVolume();
							double productsFactor = Math.pow(bnglModelVolume,numProducts-1);
							Expression correctedRate = Expression.mult(new Expression(node.getValue()),new Expression(productsFactor)).flatten();
							rr.getKineticLaw().setLocalParameterValue(RbmKineticLawParameterType.MassActionReverseRate, correctedRate);
//	this results in an expression which is molecules/time ... but we need concentration/time.
//    K' = K * V^(N-1)
						}else{
							String value = node.getValue();
							Expression newExpression = getBoundExpression(value, model.getRbmModelContainer().getSymbolTable());
							//Expression newExpression = new Expression(node.getValue());
							rr.getKineticLaw().setLocalParameterValue(RbmKineticLawParameterType.MassActionReverseRate, newExpression);
						}
					} catch (ExpressionException | PropertyVetoException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			return null;
		}

		public Object visit(ASTSpeciesPattern node, Object data) {
			SpeciesPattern sp = new SpeciesPattern();
			node.childrenAccept(this, sp);
			if (data instanceof RbmObservable) {
				((RbmObservable) data).addSpeciesPattern(sp);
			} else if (data instanceof SeedSpecies) {
				((SeedSpecies) data).setSpeciesPattern(sp);
			} /* if data instanceof Reactant or Product we'll do 
			((ReactionRule) data).addReactant((SpeciesPattern) object); 
			right after returning from this function, within the caller code */
			return sp;
		}

		public Object visit(ASTMolecularTypePattern node, Object data) {
			String name = node.getName();
			if (data == null || data instanceof RbmModelContainer) {
				MolecularType molecularType = new MolecularType(name, model);
				node.childrenAccept(this, molecularType);
				if (data != null) {
					try {
						model.getRbmModelContainer().addMolecularType(molecularType, false);
					} catch (Exception ex) {
						throw new RuntimeException(ex.getMessage());
					}
				}
				return molecularType;
			} else if (data instanceof SpeciesPattern) {
				assert model != null;
				assert model.getRbmModelContainer() != null;
				MolecularType molecularType = model.getRbmModelContainer().getMolecularType(name);
				if (molecularType == null) {
					if(stopOnError) {
						throw new RuntimeException(MolecularType.typeName + " '" + name + "' doesn't exist!");
					} else {
						System.out.println(MolecularType.typeName + " '" + name + "' doesn't exist! Creating it.");
					}
					molecularType = new MolecularType(name, model);
					try {
						model.getRbmModelContainer().addMolecularType(molecularType, false);
					} catch (ModelException | PropertyVetoException e) {
						e.printStackTrace();
						throw new RuntimeException("Unexpected " + SpeciesPattern.typeName + " exception: " + e.getMessage());
					}
				}
				MolecularTypePattern molecularTypePattern = new MolecularTypePattern(molecularType, false);
				node.childrenAccept(this, molecularTypePattern);
				String match = node.getMatchLabel();
				if(match != null && !match.isEmpty()) {
					molecularTypePattern.setParticipantMatchLabel(match);
				}
				((SpeciesPattern) data).addMolecularTypePattern(molecularTypePattern);
				molecularTypePattern.ClearProcessedMolecularComponentsMultiMap();
				return molecularTypePattern;
			}
			return null;
		}

		public Object visit(ASTMolecularComponentPattern node, Object data) {
			String name = node.getName();
			if (data instanceof MolecularType) {
				MolecularComponent[] mcl = ((MolecularType) data).getMolecularComponents(name);
				if(mcl.length > 0) {
					String message = MolecularComponent.typeName + " '" + name + "' already exists in " + MolecularType.typeName + " '" 
								+ ((MolecularType) data).getDisplayName() + "'! ";
					message += " <font color=red>Multiple identical " + MolecularComponent.typeName + "s not supported witin a " + MolecularType.typeName + ".</font> ";
					throw new RuntimeException(message);
				}
				MolecularComponent molecularComponent = new MolecularComponent(name);
				node.childrenAccept(this, molecularComponent);
				((MolecularType)data).addMolecularComponent(molecularComponent);				
				return molecularComponent;
			} else if (data instanceof MolecularTypePattern) {
				MolecularTypePattern molecularTypePattern = (MolecularTypePattern) data;
				MolecularType molecularType = molecularTypePattern.getMolecularType();
				MolecularComponent[] molecularComponents = molecularType.getMolecularComponents(name);
				// find first unused molecularComponent with the correct name;	TODO: this is probably obsolete, all logic here is bad !!! revisit !!!
				MolecularComponent molecularComponent = null;
				if(molecularComponents.length == 1) {
					molecularComponent = molecularType.getMolecularComponent(name);
				} else {
					molecularComponent = molecularTypePattern.getFirstUnprocessedMolecularComponent(name, molecularComponents);
				}				
				if (molecularComponent == null) {
					if(stopOnError) {
						throw new RuntimeException(MolecularComponent.typeName + " '" + name + "' doesn't exist in " + MolecularType.typeName + " '" 
									+ ((MolecularTypePattern) data).getDisplayName() + "'!");
					} else {
						System.out.println(MolecularComponent.typeName + " '" + name + "' doesn't exist in " + MolecularType.typeName + " '" 
									+ ((MolecularTypePattern) data).getDisplayName() + "'! Creating it.");
					}
					molecularComponent = new MolecularComponent(name);
					molecularType.addMolecularComponent(molecularComponent);
					node.childrenAccept(this, molecularComponent);
				}
				MolecularComponentPattern molecularComponentPattern;
				if(!molecularTypePattern.hasMolecularComponentPattern(molecularComponent)) {
					// we create and add the molecular component pattern to the molecular type pattern
					// getMolecularComponentPattern() does all that automatically
					molecularComponentPattern = molecularTypePattern.getMolecularComponentPattern(molecularComponent);
					molecularComponentPattern.setBondType(BondType.None);
					node.childrenAccept(this, molecularComponentPattern);
					return molecularComponentPattern;
				} else {
					String message = MolecularComponentPattern.typeName + " '" + name + "' already exists in " + MolecularTypePattern.typeName + " '" 
								+ ((MolecularTypePattern) data).getDisplayName() + "'! ";
					message += " <font color=red>Multiple identical " + MolecularComponentPattern.typeName + "s not supported witin a " + MolecularTypePattern.typeName + ".</font> ";
					throw new RuntimeException(message);
				}
			}
			return null;
		}

		public Object visit(ASTAttributePattern node, Object data) {
			if (data instanceof MolecularComponent) {
				ComponentStateDefinition componentState = new ComponentStateDefinition(node.getComponentState());
				((MolecularComponent)data).addComponentStateDefinition(componentState);
				return componentState;
			} else if (data instanceof MolecularComponentPattern) {
				ComponentStatePattern componentStatePattern = null;
				ComponentStateDefinition componentState = ((MolecularComponentPattern) data).getMolecularComponent().getComponentStateDefinition(node.getComponentState());
				if(componentState != null) {
					componentStatePattern = new ComponentStatePattern(componentState);
				} else {
					if(stopOnError) {
						throw new RuntimeException("ComponentStateDefinition '" + node.getComponentState() + "' doesn't exist!");
					} else {
						System.out.println("ComponentStateDefinition '" + node.getComponentState() + "' doesn't exist! Creating it.");
					}
					// here we try to recover what's missing
					componentState = new ComponentStateDefinition(node.getComponentState());
					((MolecularComponentPattern) data).getMolecularComponent().addComponentStateDefinition(componentState);
					componentStatePattern = new ComponentStatePattern(componentState);
				}
				((MolecularComponentPattern) data).setComponentStatePattern(componentStatePattern);
				return componentStatePattern;
			}
			return null;
		}

		public Object visit(ASTBondState node, Object data) {
			if (data instanceof MolecularComponentPattern) {
				((MolecularComponentPattern) data).setBondId(node.getState());
			}
			return null;
		}

		public Object visit(ASTBondExist node, Object data) {
			if (data instanceof MolecularComponentPattern) {
				((MolecularComponentPattern) data).setBondType(BondType.Exists);
			}
			return null;
		}

		public Object visit(ASTBondPossible node, Object data) {
			if (data instanceof MolecularComponentPattern) {
				((MolecularComponentPattern) data).setBondType(BondType.Possible);
			}
			return null;
		}

		public Object visit(ASTObservable node, Object data) {
			if (data instanceof RbmModelContainer) {
				RbmObservable observable = model.getRbmModelContainer().createObservable(RbmObservable.ObservableType.valueOf(node.getType()));
				try {
					model.getRbmModelContainer().addObservable(observable);
					observable.setName(node.getName());
				} catch (PropertyVetoException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage(), e);
				} catch (ModelException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage(), e);
				}
				node.childrenAccept(this, observable);
				return observable;
			}else{
				return null;
			}
		}
		
		@Override
		public Object visit(ASTObservablePattern node, Object data) {
			if (data instanceof RbmObservable){
				RbmObservable observable = (RbmObservable)data;
				observable.setStructure(model.getStructure(node.getCompartment()));
				node.childrenAccept(this, observable);
			}
			return null;
		}

		@Override
		public Object visit(ASTFunctionsBlock node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTExpression node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}
		
/*		public Object visit(ASTExpression node, Object data) {
			if (data instanceof ReactionRule) {
				Expression exp;
				try {
					exp = new Expression(node.toBNGL());
				} catch (ExpressionException e) {
					e.printStackTrace();
					throw new RuntimeException("expression exception: "+e.getMessage(),e);
				}
				if (((ReactionRule) data).getForwardRate() == null) {
					((ReactionRule) data).setForwardRate(exp);
				} else {
					((ReactionRule) data).setReverseRate(exp);
				}
			}
			return null;
		}
*/

		@Override
		public Object visit(ASTOrNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTAndNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTRelationalNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTAddNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTMinusTermNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTMultNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTInvertTermNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTPowerNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTNotNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTFuncNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTFloatNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTIdNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTLiteralNode node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}

		@Override
		public Object visit(ASTFunctionDecl node, Object data) {
			try {
				if (model.getRbmModelContainer().getFunction(node.getName()) == null) {
					String expS = node.getExpressionString();
					Expression exp = getBoundExpression(expS, model.getRbmModelContainer().getSymbolTable());
					model.getRbmModelContainer().addFunction(node.getName(), exp, model.getUnitSystem().getInstance_TBD());
				}
			} catch (Exception ex) {
				throw new RuntimeException("Function '" + node.getName() + " can not be added, " + ex.getMessage(),ex);
			}
			return null;
		}
		@Override
		public Object visit(ASTCompartmentsBlock node, Object data) {
			node.childrenAccept(this, data);
			return null;
		}
		@Override
		public Object visit(ASTCompartment node, Object data) {
			String name = node.getName();
			if(name == null) {
				return null;
			}
			int dimension = node.getDimension();
			String volume  = node.getVolume();		// TODO: now what
			try {
				if(dimension == 2) {
					model.addMembrane(name);
				} else {
					model.addFeature(name);
				}
			} catch (ModelException | PropertyVetoException e) {
				e.printStackTrace();
				throw new RuntimeException("BngVisitor: Unable to create Structure, " + e.getMessage());
			}
			return null;
		}
		
	}
	
	public static Expression getBoundExpression(String expressionString, final SymbolTable symbolTable) throws ExpressionException {
		Expression exp = null;
		if(expressionString == null || expressionString.equals("")) {
			exp = new Expression(0);
		} else {
			exp = new Expression(expressionString);
		}
		//
		// look for function invocations of unsupported but known functions
		//
		FunctionInvocation[] invocations = exp.getFunctionInvocations(new FunctionFilter() {
			
			@Override
			public boolean accept(String functionName, FunctionType functionType) {
				if(functionName.equalsIgnoreCase("if")) {
					return true;
				}
				return false;
			}
		});
		if(invocations != null && invocations.length > 0) {
			for (FunctionInvocation invocation : invocations){
				if (invocation.getFunctionName().equalsIgnoreCase("if")){
					// build new expression
					// if (testExp, trueExp, falseExp)
					//
					Expression testExp = invocation.getArguments()[0];
					Expression trueExp = invocation.getArguments()[1];
					Expression falseExp = invocation.getArguments()[2];
					Expression testPassed = Expression.relational("!=", testExp, new Expression(0.0));
					Expression testFailed = Expression.relational("==", testExp, new Expression(0.0));
					Expression newExp = Expression.add(
							Expression.mult(testPassed,trueExp), 
							Expression.mult(testFailed,falseExp));
					
					// substitute new expression replacing if()
					exp.substituteInPlace(invocation.getFunctionExpression(),newExp);
				}
			}
			System.out.println(invocations.toString());
		}
		//
		// "if()" functions are goine ... but lets look for BNGL function invocations
		//   1) if they have no arguments, drop the "()".
		//   2) if they have arguments, have to substitute the arguments in the expression ... flatten it.
		//
		invocations = exp.getFunctionInvocations(new FunctionFilter() {
			
			@Override
			public boolean accept(String functionName, FunctionType functionType) {
				return true;
			}
		});
		if(invocations != null && invocations.length > 0) {
			for (FunctionInvocation invocation : invocations){
				if (invocation.getArguments().length == 0){
					//
					// no arguments, look for existing parameter by name (parameter name is function name).
					//
					// look up "identifier()" as "identifier" to find a model parameter generated earlier when processing functions (or prior functions)
					//
					SymbolTableEntry parameter = symbolTable.getEntry(invocation.getFunctionName());
					if (parameter != null){
						exp.substituteInPlace(invocation.getFunctionExpression(), new Expression(parameter,parameter.getNameScope()));
					}else{
						//
						// didn't find a parameter, may be a built-in function with zero arguments built into VCell. (none exists right now).
						//
						SymbolTableFunctionEntry vcellFunction = (SymbolTableFunctionEntry)symbolTable.getEntry(invocation.getFormalDefinition());
						if (vcellFunction!=null){
							//
							// nothing to do, vcell will parse and interpret this correctly
							//
						}else{
							throw new RuntimeException("function \""+invocation.getFunctionExpression().infix()+"\" not found as a bngl function or as a vcell built-in function");
						}
					}
				}else{
					//
					// should be a build-in vcell function with arguments ... user defined functions with arguments not supported yet in bngl import.
					//
					FunctionType builtinFunctionType = cbit.vcell.parser.ASTFuncNode.FunctionType.fromFunctionName(invocation.getFunctionName());
					if (builtinFunctionType==null){
						throw new RuntimeException("function \""+invocation.getFunctionExpression().infix()+"\" not found as a built-in VCell function (and bngl functions with arguments are not yet supported");
					}else{
						if (invocation.getArguments().length != builtinFunctionType.getArgTypes().length){
							throw new RuntimeException("built-in function \""+invocation.getFunctionExpression().infix()+"\" expects "+builtinFunctionType.getArgTypes().length+" arguments");
						}
					}
				}
				System.out.println(invocations.toString());
			}
		}
		exp.bindExpression(symbolTable);
		return exp;
	}
	
	public enum BnglLocation {
		Compartment, Parameter, Species, MoleculeType, Observable, Function, ReactionRule, OutsideBlock
	}
	private static class BnglComment {
		String entityName;
		int lineNumber;
		BnglLocation location = BnglLocation.OutsideBlock;
		String comment;
		private  BnglComment() {}
		public BnglComment(String entityName, int lineNumber, BnglLocation location, String comment) {
			this.entityName = entityName;
			this.lineNumber = lineNumber;
			this.location = location;
			this.comment = comment;
		}
	}
	// preprocessor
	public static ASTModel importBnglFile(Reader reader) throws ParseException {
		BufferedReader br = new BufferedReader(reader);
		return importBnglFile(br);
	}
	public static ASTModel importBnglFile(BufferedReader br) throws ParseException {
		try {
			// remove all the comments
			reactionRuleNames.clear();
			StringBuilder sPrologBuilder = new StringBuilder();		// just the prologue
			StringBuilder sb = new StringBuilder();
			ArrayList<BnglComment> comments = new ArrayList<BnglComment>();
			int lineNumber = 0;
			boolean bEscapingExpressionBegin = false;
			boolean inProlog = true;
			BnglLocation location = BnglLocation.OutsideBlock;
			Set<String> compartments = new HashSet<> ();
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				line = applySyntaxCorrections(line);

				// we capture all the prologue and insert in in the BioModel notes
				if(line.startsWith("begin model") || line.startsWith("begin parameters")) {
					inProlog = false;
				}
				if(inProlog == true) {	
					sPrologBuilder.append(line);
					sPrologBuilder.append("\n");
					sb.append("\n");
					lineNumber++;
					continue;
				}
				
				if(line.startsWith("version")) {	// we include the version in the prologue even if it shows up later in the code
					sPrologBuilder.append(line);
					sPrologBuilder.append("\n");
					sb.append("\n");
					lineNumber++;
					continue;
				}
				
				if (line.length() == 0 || line.charAt(0) == '#') {
					if(line.length() > 0 && line.charAt(0) == '#') {
						BnglComment bc = new BnglComment("empty", lineNumber, location, line);
						comments.add(bc);
					}
					sb.append("\n");
					lineNumber++;
					continue;
				}
				if (line.endsWith(":\\")) {		// we'll try to treat single line labels as comments
					sb.append("\n");
					lineNumber++;
					continue;
				} else if(line.endsWith("\\")) {	// concatenation with next line, we make one single continuous line
					String line2 = br.readLine();
					if (line2 == null) {
						break;
					}
					line2 = line2.trim();
					// we don't try to get too fancy here, we'll just assume there are 2 lines of code somewhere
					// within a block and we simply concatenate them; if it's more than 2 we're out of luck
					int concatenationTokenIndex = line.lastIndexOf("\\");
					line = line.substring(0, concatenationTokenIndex);
					if(line.endsWith(" ")) {
						line = line + line2;
					} else {
						line = line + " " + line2;
					}
					sb.append("\n");	// we add an empty line so that the total number of lines in the document won't change
					lineNumber++;
				}
				
				if (line.startsWith("end parameters") || line.startsWith("end seed species")) {
					// TODO: position sensitive, do not move this 'if' down
					bEscapingExpressionBegin = false;
				}
				
				int commentIndex = line.indexOf('#');	// remove comments which follow some code
				if (commentIndex >= 0) {
					BnglComment bc = new BnglComment("item ", lineNumber, location, line.substring(commentIndex));
					comments.add(bc);
					line = line.substring(0, commentIndex);
				}
					
				int labelEndIndex = line.indexOf(":");
				if(labelEndIndex > 0 && line.substring(0, labelEndIndex).contains("@")) {
					;		// this is not a label, is a compartment
				} else {
					if(labelEndIndex == -1) {
						// there may still be a label present, just without the ":"
						StringTokenizer st = new StringTokenizer(line);
						String nextToken = st.nextToken();
						String prefix = "";
						if (nextToken.matches("[0-9]+") ) {	// labels missing the ":" must be purely numeric
							//  we transform them in proper labels by adding a letter prefix and the ":" suffix
							if(location == BnglLocation.ReactionRule) {
								prefix = "r";	// labels can't start with a number, so we add "r" as a prefix
							} else {
								prefix = "l";	// this just for consistency, below we'll actually remove all labels except the reaction rule ones
							}
							line = line.replaceFirst(nextToken, prefix + nextToken + ":");
							labelEndIndex = line.indexOf(":");
						}
					}
					// remove labels except for reaction rule labels
					if (labelEndIndex >= 0 && line.length() > labelEndIndex) {
						// we're certain they cannot be inside a comment since comments were removed already (above)
						if(location == BnglLocation.ReactionRule) {
							String reactionRuleName = line.substring(0, labelEndIndex);
							if(reactionRuleNames.indexOf(reactionRuleName) != -1) {		// already in use 
								line = line.substring(labelEndIndex);
								reactionRuleName = reactionRuleName + "_" + lineNumber;
								line = reactionRuleName + line;
							}
							reactionRuleNames.add(reactionRuleName);
						} else {
							line = line.substring(labelEndIndex + 1);
						}
					}
				}
				line = line.trim();				// there may be some blanks hanging around
				if (line.length() == 0) {		// line may be empty now
					sb.append("\n");
					lineNumber++;
					continue;
				}
				if (bEscapingExpressionBegin) {
					StringTokenizer st = new StringTokenizer(line);
					String nextToken = st.nextToken();
					if (Character.isDigit(nextToken.charAt(0))) {
						sb.append(nextToken + " ");
						nextToken = st.nextToken();
					}
					sb.append(nextToken);
					sb.append(" {");
					while (st.hasMoreTokens()) {
						sb.append(st.nextToken());
					}
					sb.append("}");
					sb.append("\n");
				} else {
					sb.append(line + "\n");
					if(location == BnglLocation.Compartment) {
						// make a list with all the compartment names, we'll need them during phase 2 to remove the "containing
						// compartment" (which we can't deal with) and to build the escape expression for the volume using the  { }
						StringTokenizer st = new StringTokenizer(line);
						String firstToken = st.nextToken();		// first token is compartment name
						if(!compartments.add(firstToken)) {
							System.out.println("BnglPreprocessor: Duplicate compartment name!" + firstToken);
						}
					}
				}
				if (line.startsWith("begin parameters") || line.startsWith("begin seed species")) {
					// TODO: position sensitive, do not move this 'if' up
					bEscapingExpressionBegin = true;
				}
				location = markBlock(location, line);
				lineNumber++;
			}
			br.close();
			
			ListIterator<BnglComment> it = comments.listIterator();
			while(it.hasNext()) {
				BnglComment bc = it.next();
				System.out.println(bc.entityName + " " + bc.location + " " + bc.comment);
			}

			// pass 2, deal with comments and labels
			String cleanedBngl = sb.toString();
//			System.out.println(cleanedBngl);
			br = new BufferedReader(new StringReader(cleanedBngl));
			sb = new StringBuilder();
			lineNumber = 0;
			location = BnglLocation.OutsideBlock;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;				// end of document
				}
				if(line.isEmpty()) {
					sb.append("\n");
					lineNumber++;
					continue;
				}
				BnglLocation newLocation = markBlock(location, line);
				if((newLocation == BnglLocation.ReactionRule) && (location == BnglLocation.ReactionRule)) {
					int labelEndIndex = line.indexOf(":");
					if(labelEndIndex == -1) {		// no label on this reaction rule, so we make one
						String reactionRuleName = generateReactionRuleName();
						if(reactionRuleNames.indexOf(reactionRuleName) != -1) {		// already in use 
							reactionRuleName = reactionRuleName + "_" + lineNumber;
						}
						reactionRuleNames.add(reactionRuleName);
						line = reactionRuleName + ":" + line;
						sb.append(line + "\n");
					} else {
						sb.append(line + "\n");		// rule has label, we keep it as it is
					}
				} else if((newLocation == BnglLocation.Compartment) && (location == BnglLocation.Compartment)) {
					// remove the optional "containing compartment" if specified
					System.out.println(line);
					
					StringTokenizer st = new StringTokenizer(line);
					String lastToken = "";
					while (st.hasMoreTokens()) {
						lastToken = st.nextToken();
					}
					if(compartments.contains(lastToken)) {
						line = line.substring(0, line.lastIndexOf(lastToken));
					}
					line = line.trim();
					// start again and add the { } around the expression of volume
					st = new StringTokenizer(line);
					String nextToken = st.nextToken();		// name
					sb.append(nextToken + " ");
					nextToken = st.nextToken();				// dimension
					sb.append(nextToken + " ");
					sb.append("{");
					while (st.hasMoreTokens()) {
						sb.append(st.nextToken());
					}
					sb.append("}");
					sb.append("\n");

					
				} else {		// all other cases nothing special to do
					sb.append(line + "\n");
				}
				location = newLocation;
				lineNumber++;
			}
			br.close();
			cleanedBngl = sb.toString();
//			System.out.println(cleanedBngl);
			
//			BufferedWriter writer = null;
//			try	{
//			    writer = new BufferedWriter( new FileWriter( "c:\\TEMP\\workRules.bngl"));
//			    writer.write( cleanedBngl);
//			}
//			catch ( IOException e) {
//			}
//			finally {
//			    try {
//			        if ( writer != null)
//			        writer.close( );
//			    }
//			    catch ( IOException e) {
//			    }
//			}
//		    BufferedReader br1 = new BufferedReader(new FileReader("c:\\TEMP\\workRules.bngl"));
//		    try {
//		        StringBuilder sb1 = new StringBuilder();
//		        String line = br1.readLine();
//		        while (line != null) {
//		            sb1.append(line);
//		            sb1.append("\n");
//		            line = br1.readLine();
//		        }
//		        cleanedBngl = sb1.toString();
//		    } finally {
//		        br.close();
//		    }
//			System.out.println(cleanedBngl);
			
			BNGLParser parser = new BNGLParser(new StringReader(cleanedBngl));
			ASTModel astModel = parser.Model();
			
			String prologString = sPrologBuilder.toString();
			astModel.setProlog(prologString);
			return astModel;
//			BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(rbmModelContainer);
//			astModel.jjtAccept(constructionVisitor, rbmModelContainer); 
		} catch (Throwable ex) {
//			ex.printStackTrace();
			if(ex instanceof ParseException) {
				ParseException pe = (ParseException)ex;
				throw pe;
			} else {
				throw new ParseException(ex.getMessage());
			}
		}
	}
	public static String generateReactionRuleName() {
		String label = "r" + reactionRuleLabelIndex;
		reactionRuleLabelIndex++;
		return label;
	}
	public static BnglLocation markBlock(BnglLocation currentLocation, String line) {
		if(line.startsWith("begin compartments")) {
			return BnglLocation.Compartment;
		}
		else if(line.startsWith("begin parameters")) {
			return BnglLocation.Parameter;
		}
		else if(line.startsWith("begin species")) {
			return BnglLocation.Species;
		}
		else if(line.startsWith("begin molecule types")) {
			return BnglLocation.MoleculeType;
		}
		else if(line.startsWith("begin observables")) {
			return BnglLocation.Observable;
		}
		else if(line.startsWith("begin functions")) {
			return BnglLocation.Function;
		}
		else if(line.startsWith("begin reaction rules")) {
			return BnglLocation.ReactionRule;
		}
		else if(line.startsWith("end compartments")) {
			return BnglLocation.OutsideBlock;
		}
		else if(line.startsWith("end parameters")) {
			return BnglLocation.OutsideBlock;
		}
		else if(line.startsWith("end species")) {
			return BnglLocation.OutsideBlock;
		}
		else if(line.startsWith("end molecule types")) {
			return BnglLocation.OutsideBlock;
		}
		else if(line.startsWith("end observables")) {
			return BnglLocation.OutsideBlock;
		}
		else if(line.startsWith("end functions")) {
			return BnglLocation.OutsideBlock;
		}
		else if(line.startsWith("end reaction rules")) {
			return BnglLocation.OutsideBlock;
		}
		return currentLocation;
	}
	public static String applySyntaxCorrections(String line) {
		if( (line.startsWith("begin molecules") ) || (line.startsWith("end molecules")) ) {
			line = line.replace("molecules", "molecule types");
		}
		if( (line.startsWith("begin molecule type") && !line.startsWith("begin molecule types")) || (line.startsWith("end molecule type") && !line.startsWith("end molecule types")) ) {
			line = line.replace("molecule type", "molecule types");
		}
		if( (line.startsWith("begin species") ) || (line.startsWith("end species")) ) {
			line = line.replace("species", "seed species");
		}
		if( (line.startsWith("begin reaction_rules") ) || (line.startsWith("end reaction_rules")) ) {
			line = line.replace("reaction_rules", "reaction rules");
		}
		return line;
	}
	
	public static MolecularType parseMolecularType(String inputString) throws ParseException {
		try {
			BNGLParser parser = new BNGLParser(new StringReader(inputString));
			ASTMolecularTypePattern astMolecularPattern = parser.MolecularTypePattern();
			BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor();
			MolecularType molecularType = (MolecularType) astMolecularPattern.jjtAccept(constructionVisitor, null);
			return molecularType;
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage());
		}
	}
	
	//
	// this is called when the proper cBNGL syntax is observed, the species pattern has the form @comp:expression
	//
	public static String parseCompartment(String originalInputString, Model model) throws ParseException {
		String inputString = new String(originalInputString);
		if(inputString.startsWith("@") && inputString.contains(":")) {
			inputString = inputString.substring(1, inputString.lastIndexOf(":"));
//			Structure struct = model.getStructure(inputString);
//			if(struct == null) {
//				throw new RuntimeException("RbmUtils: Unable to find structure of " + originalInputString);
//			}
			return inputString;
		}
		return null;
	}
	//
	// this is called only when the compartment is faked by a site whose state is the name of the compartment
	// all the molecules in the species pattern have this extra fake site, and in an identical state (compartment)
	// we eliminate this species from all the molecule and return the state (compartment name)
	//
	public static Pair<List<String>, String> extractCompartment(String original) {
		String pattern = RbmUtils.SiteStruct;
		List<String> compartments = new ArrayList<>();
		String cleaned = "";
		while(true) {
			if(!original.contains(pattern)) {
				cleaned += original;	// all that's left in original needs to be appended to the cleaned string
				break;					// no more patterns to match, we're done!
			}
			String compartment = "";
			cleaned += original.substring(0, original.indexOf(pattern));
			original = original.substring(original.indexOf(pattern)+pattern.length()+1);	// ve get rid of all including the ~
			while(true) {
				// we keep moving the name of the compartment from original to cleaned until we encounter ',' or ')'
				char c = original.charAt(0);
				if(c == ',') {
					original = original.substring(1);	// delete the comma
					break;					// done cleaning this molecule
				} else if(c == ')') {
					if(cleaned.endsWith(",")) {
						// our pattern was the last element, so we need to get rid of the orphaned comma we have in 'cleaned'
						cleaned = cleaned.substring(0,  cleaned.length()-1);
					}
					break;					// done cleaning this molecule and we are at its end, no comma to delete
				} else {
					compartment += c;					// build compartment name
					original = original.substring(1);	// delete this letter from the original
				}
			}
			if(!compartments.contains(compartment)) {
				compartments.add(compartment);
			}
		}
		Pair<List<String>, String> p = new Pair<List<String>, String>(compartments, cleaned);
		return p;
	}
	public static String extractProduct(String original) {
		String pattern = RbmUtils.SiteProduct;
		List<String> compartments = new ArrayList<>();
		String cleaned = "";
		while(true) {
			if(!original.contains(pattern)) {
				cleaned += original;	// all that's left in original needs to be appended to the cleaned string
				break;					// no more patterns to match, we're done!
			}
			String compartment = "";
			cleaned += original.substring(0, original.indexOf(pattern));
			original = original.substring(original.indexOf(pattern)+pattern.length()+1);	// ve get rid of all including the ~
			while(true) {
				// we keep moving the name of the compartment from original to cleaned until we encounter ',' or ')'
				char c = original.charAt(0);
				if(c == ',') {
					original = original.substring(1);	// delete the comma
					break;					// done cleaning this molecule
				} else if(c == ')') {
					if(cleaned.endsWith(",")) {
						// our pattern was the last element, so we need to get rid of the orphaned comma we have in 'cleaned'
						cleaned = cleaned.substring(0,  cleaned.length()-1);
					}
					break;					// done cleaning this molecule and we are at its end, no comma to delete
				} else {
					compartment += c;					// build compartment name
					original = original.substring(1);	// delete this letter from the original
				}
			}
			if(!compartments.contains(compartment)) {
				compartments.add(compartment);
			}
		}
		return cleaned;
	}
	// almost as above, we replace all the "fake" compartments with the good one
	public static String repairCompartment(String original, String compartment) {
		String pattern = RbmUtils.SiteStruct;
		String cleaned = "";
		while(true) {
			if(!original.contains(pattern)) {
				cleaned += original;	// all that's left in original needs to be appended to the cleaned string
				break;					// no more patterns to match, we're done!
			}
			cleaned += original.substring(0, original.indexOf(pattern)+pattern.length()+1);
			original = original.substring(original.indexOf(pattern)+pattern.length()+1);
			while(true) {
				// we keep deleting the compartment from original until we encounter ',' or ')'
				char c = original.charAt(0);
				if(c == ',') {
					break;					// done cleaning this molecule
				} else if(c == ')') {
					break;					// done cleaning this molecule
				} else {
					original = original.substring(1);	// slowly consume compartment letters from the original
				}
			}
			cleaned += compartment;
		}
		// the cleaned string may be unsorted now that we replaced some strings here and there
		cleaned = sortAlphabetically(cleaned, ".");
		return cleaned;
	}
	public static String resetProductIndex(String original) {
		String pattern = RbmUtils.SiteProduct;
		String cleaned = "";
		while(true) {
			if(!original.contains(pattern)) {
				cleaned += original;	// all that's left in original needs to be appended to the cleaned string
				break;					// no more patterns to match, we're done!
			}
			cleaned += original.substring(0, original.indexOf(pattern)+pattern.length()+1);
			original = original.substring(original.indexOf(pattern)+pattern.length()+1);
			while(true) {
				// we keep deleting the product index from original until we encounter ',' or ')'
				char c = original.charAt(0);
				if(c == ',') {
					break;					// done cleaning this molecule
				} else if(c == ')') {
					break;					// done cleaning this molecule
				} else {
					original = original.substring(1);	// slowly consume the product index from the original (we actually know it's always just one char)
				}
			}
			cleaned += "0";
		}
		// the cleaned string may be unsorted now that we replaced some strings here and there
		cleaned = sortAlphabetically(cleaned, ".");
		return cleaned;
	}
	
	private static String sortAlphabetically(String input, String separators) {
		List<String> entities = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(input, separators);
		while (tokenizer.hasMoreTokens()) {
			entities.add(tokenizer.nextToken());
		}
		Collections.sort(entities);
		String output = "";
		for(int i=0; i<entities.size(); i++) {
			if(i>0) {
				output += ".";
			}
			output += entities.get(i);
		}
		return output;
	}
	public static SpeciesPattern parseSpeciesPattern(String originalInputString, Model model) throws ParseException {
		String inputString = new String(originalInputString);
		try {
			if(inputString.startsWith("@") && inputString.contains(":")) {
//				throw new ParseException("RbmUtils: Unable to parse SpeciesPattern with compartment information.");
				// clean up the compartment information and parse the pure sp expression
				inputString = inputString.substring(inputString.lastIndexOf(":")+1);
			}
			BNGLParser parser = new BNGLParser(new StringReader(inputString));
			ASTSpeciesPattern astSpeciesPattern = parser.SpeciesPattern();
			BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(model, null, true);
			SpeciesPattern speciesPattern = (SpeciesPattern) astSpeciesPattern.jjtAccept(constructionVisitor, null);
			return speciesPattern;
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage());
		}
	}
	
	public static ReactionRule parseReactionRule(String inputString, Structure structure, BioModel bioModel) throws ParseException {
		return parseReactionRule(inputString, null, structure, bioModel);
	}

	public static ReactionRule parseReactionRule(String inputString, String name, Structure structure, BioModel bioModel) throws ParseException {
		try {
			String label = name;
//			int labelIndex = inputString.indexOf(':');		// TODO: the way we edit reaction rules now, we have no labels here
//			String label = "";
//			if(labelIndex>=0) {
//				label = inputString.substring(0, labelIndex);
//				inputString = inputString.substring(labelIndex+1);
//			}
//			if(label.isEmpty() || (reactionRuleNames.indexOf(label) != -1)) {
//				do {	// no label or label in use, we generate new label
//					label = generateReactionRuleName();
//				} while(reactionRuleNames.indexOf(label) != -1);
//				reactionRuleNames.add(label);
//			} else {
//				reactionRuleNames.add(label);
//			}

			int arrowIndex = inputString.indexOf("<->");
			boolean bReversible = true;
			if (arrowIndex < 0) {
				arrowIndex = inputString.indexOf("->");
				bReversible = false;
			}
			
			String left = inputString.substring(0, arrowIndex).trim();
			String right = inputString.substring(arrowIndex + (bReversible ? 3 : 2)).trim();
			if (left.length() == 0 && right.length() == 0) {
				return null;
			}
			
			// note that the constructor will try to honor the label from the editor but will generate a new one if already in use
			ReactionRule reactionRule = bioModel.getModel().getRbmModelContainer().createReactionRule(label, structure, bReversible);
			String regex = "[^!]\\+";
			String[] patterns = left.split(regex);
			for (String sp : patterns) {
				SpeciesPattern speciesPattern = parseSpeciesPattern(sp, bioModel.getModel());
				reactionRule.addReactant(new ReactantPattern(speciesPattern,reactionRule.getStructure()));
			}
			
			patterns = right.split(regex);
			for (String sp : patterns) {
				SpeciesPattern speciesPattern = parseSpeciesPattern(sp, bioModel.getModel());
				reactionRule.addProduct(new ProductPattern(speciesPattern,reactionRule.getStructure()));
			}			
			return reactionRule;
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage());
		}
	}
	
	public static String toBnglString(ComponentStatePattern componentStatePattern) {
		if(componentStatePattern.getComponentStateDefinition() != null) {
			return "~" + componentStatePattern.getComponentStateDefinition().getName();
		} else if(componentStatePattern.isAny()) {
			return "";
		} else {
			throw new RuntimeException("Unexpected state for ComponentStatePattern " + componentStatePattern);
		}
	}
	public static String toBnglString(ParticleComponentStatePattern componentStatePattern) {
		if(componentStatePattern.getParticleComponentStateDefinition() != null) {
			return "~" + componentStatePattern.getParticleComponentStateDefinition().getName();
		} else if(componentStatePattern.isAny()) {
			return "";
		} else {
			throw new RuntimeException("Unexpected state for ComponentStatePattern " + componentStatePattern);
		}
	}
	public static String toBnglString(ComponentStateDefinition componentStateDefinition) {
		if(componentStateDefinition == null) {
			return "";
		} else {
			return "~" + componentStateDefinition.getName();
		}
	}
	
	public static String toBnglString(ParticleComponentStateDefinition componentStateDefinition) {
		if(componentStateDefinition == null) {
			return "";
		} else {
			return "~" + componentStateDefinition.getName();
		}
	}
	
	public static String toBnglString(MolecularComponent molecularComponent) {
		StringBuilder buffer = new StringBuilder(molecularComponent.getName());
		for (ComponentStateDefinition componentStateDefinition : molecularComponent.getComponentStateDefinitions()) {
			buffer.append(toBnglString(componentStateDefinition));
		}
		return buffer.toString();
	}
	
	public static String toBnglString(ParticleMolecularComponent molecularComponent) {
		StringBuilder buffer = new StringBuilder(molecularComponent.getName());
		for (ParticleComponentStateDefinition componentStateDefinition : molecularComponent.getComponentStateDefinitions()) {
			buffer.append(toBnglString(componentStateDefinition));
		}
		return buffer.toString();
	}
	
	public static String toBnglString(MolecularType molecularType, Model model, CompartmentMode compartmentMode) {
		StringBuilder buffer = new StringBuilder(molecularType.getName());
		buffer.append("(");
		if(compartmentMode == CompartmentMode.asSite) {
			String site = SiteStruct;
			for(Structure str : model.getStructures()) {
				site += "~" + str.getName();
			}
			buffer.append(site + ",");
			
			site = SiteProduct;
			site += "~0~1~2~3";		// index of (max 3) products, 0 means reactant, seed species or observables
			buffer.append(site);
			if(molecularType.getComponentList().size() > 0) {
				buffer.append(",");
			}
		}
		List<MolecularComponent> componentList = molecularType.getComponentList();
		for (int i = 0; i < componentList.size(); ++ i) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(toBnglString(componentList.get(i)));
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	public static String toBnglString(ParticleMolecularType molecularType) {
		StringBuilder buffer = new StringBuilder(molecularType.getName());
		buffer.append("(");
		List<ParticleMolecularComponent> componentList = molecularType.getComponentList();
		for (int i = 0; i < componentList.size(); ++ i) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(toBnglString(componentList.get(i)));
		}
		buffer.append(")");
		return buffer.toString();
	}

	// ordered by position in the UI - this is what we want to show to the user or to export to file
	public static String toBnglString(SpeciesPattern speciesPattern, Structure structure, CompartmentMode compartmentMode, int productIndex) {
		StringBuilder buffer = new StringBuilder();
		List<MolecularTypePattern> molecularTypePatterns = speciesPattern.getMolecularTypePatterns();
		for (int i = 0; i < molecularTypePatterns.size(); ++ i) {
			if (i > 0) {
				buffer.append(".");
			}
			buffer.append(toBnglString(molecularTypePatterns.get(i), structure, compartmentMode, productIndex, false));
		}
		return buffer.toString();
	}
	// ---------------------------------------------------------------------------------- with sorting alphabetically
	// sorted alphabetically - because that is what BioNetGen is producing and at times we want to 
	// compare species patterns for identity as strings
	public static String toBnglStringSortedAlphabetically(SpeciesPattern speciesPattern, Structure structure, CompartmentMode compartmentMode, int productIndex) {
		if (speciesPattern == null) {
			return "";
		}
		List<String> mtpSortedList = new ArrayList<>();
		for(MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
			mtpSortedList.add(toBnglString(mtp, structure, compartmentMode, productIndex, true));		// true means we sort alphabetically
		}
		Collections.sort(mtpSortedList);

		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < mtpSortedList.size(); ++ i) {
			if (i > 0) {
				buffer.append(".");
			}
			buffer.append(mtpSortedList.get(i));
		}
		return buffer.toString();
	}

	public static String toBnglString(ParticleSpeciesPattern speciesPattern) {
		if (speciesPattern == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		List<ParticleMolecularTypePattern> molecularTypePatterns = speciesPattern.getParticleMolecularTypePatterns();
		for (int i = 0; i < molecularTypePatterns.size(); ++ i) {
			if (i > 0) {
				buffer.append(".");
			}
			buffer.append(toBnglString(molecularTypePatterns.get(i)));
		}
		return buffer.toString();
	}

	public static String toBnglString(MolecularTypePattern molecularTypePattern, Structure structure, CompartmentMode compartmentMode, int productIndex, boolean sort) {
		StringBuilder buffer = new StringBuilder(molecularTypePattern.getMolecularType().getName());
		buffer.append("(");
		boolean bAddComma = false;
		if(compartmentMode == CompartmentMode.asSite) {
			// insert a fake site, with bond "exists" and state named after the compartment of the species pattern
//			String site = "struct~" + structure.getName() + "!+";
			String site = SiteStruct + "~" + structure.getName() + ",";
			site += SiteProduct + "~" + productIndex;
			bAddComma = true;		// if other components follow, we need a comma after this
			buffer.append(site);
		}
		List<MolecularComponentPattern> componentPatterns = molecularTypePattern.getComponentPatternList();
		List<String> mcpStrings = new ArrayList<>();
		for (MolecularComponentPattern mcp : componentPatterns) {
			if (mcp.isImplied()) {
				continue;
			}
			mcpStrings.add(toBnglString(mcp));
		}
		if(sort) {
			Collections.sort(mcpStrings);	// we sort alphabetically the components!!! so that they hopefully match the .net format
		}
		
		for(String s : mcpStrings) {
			if (bAddComma) {
				buffer.append(",");
			}
			buffer.append(s);
			bAddComma = true;
		}
		buffer.append(")");
		if(molecularTypePattern.hasExplicitParticipantMatch()) {
			buffer.append("%" + molecularTypePattern.getParticipantMatchLabel());
		}
		return buffer.toString();
	}

	public static String toBnglString(ParticleMolecularTypePattern molecularTypePattern) {
		StringBuilder buffer = new StringBuilder(molecularTypePattern.getMolecularType().getName());
		buffer.append("(");
		List<ParticleMolecularComponentPattern> componentPatterns = molecularTypePattern.getMolecularComponentPatternList();
		boolean bAddComma = false;
		for (ParticleMolecularComponentPattern mcp : componentPatterns) {
//			if (mcp.isImplied()) {
//				continue;
//			}
			if (bAddComma) {
				buffer.append(",");
			}
			buffer.append(toBnglString(mcp));
			bAddComma = true;
		}
		buffer.append(")");
		if(molecularTypePattern.hasExplicitParticipantMatch()) {
			buffer.append("%" + molecularTypePattern.getMatchLabel());
		}
		return buffer.toString();
	}

	public static String toBnglString(MolecularComponentPattern molecularComponentPattern) {
		StringBuilder buffer = new StringBuilder(molecularComponentPattern.getMolecularComponent().getName());
		if (molecularComponentPattern.getComponentStatePattern() != null) {
			buffer.append(toBnglString(molecularComponentPattern.getComponentStatePattern()));
		}
		switch (molecularComponentPattern.getBondType()) {
		case Exists:
			buffer.append("!+");
			break;
		case None:
			break;
		case Possible:
			buffer.append("!?");
			break;
		case Specified:
			buffer.append("!" + molecularComponentPattern.getBondId());
			break;
		}
		return buffer.toString();
	}
	
	public static String toBnglString(ParticleMolecularComponentPattern molecularComponentPattern) {
		StringBuilder buffer = new StringBuilder(molecularComponentPattern.getMolecularComponent().getName());
		if (molecularComponentPattern.getComponentStatePattern() != null) {
			buffer.append(toBnglString(molecularComponentPattern.getComponentStatePattern()));
		}
		switch (molecularComponentPattern.getBondType()) {
		case Exists:
			buffer.append("!+");
			break;
		case None:
			break;
		case Possible:
			buffer.append("!?");
			break;
		case Specified:
			buffer.append("!" + molecularComponentPattern.getBondId());
			break;
		}
		return buffer.toString();
	}
	
	public static String toBnglStringShort(ReactionRule reactionRule, CompartmentMode compartmentMode) {
		StringBuilder buffer = new StringBuilder();
		List<ReactantPattern> reactants = reactionRule.getReactantPatterns();
		for (int i = 0;  i < reactants.size(); ++i) {
			if (i > 0) {
				buffer.append(" + ");
			}
			ReactantPattern rp = reactants.get(i);
			if(compartmentMode == CompartmentMode.show) {
				buffer.append("@" + rp.getStructure().getName() + ":");
				buffer.append(toBnglString(rp.getSpeciesPattern(), null, CompartmentMode.hide, 0));
			} else if(compartmentMode == CompartmentMode.asSite) {
				buffer.append(toBnglString(rp.getSpeciesPattern(), rp.getStructure(), CompartmentMode.asSite, 0));
			} else {
				buffer.append(toBnglString(rp.getSpeciesPattern(), null, CompartmentMode.hide, 0));
			}
		}
		buffer.append(reactionRule.isReversible() ? " <-> " : " -> ");
		List<ProductPattern> products = reactionRule.getProductPatterns();
		for (int i = 0;  i < products.size(); ++i) {
			if (i > 0) {
				buffer.append(" + ");
			}
			ProductPattern pp = products.get(i);
			if(compartmentMode == CompartmentMode.show) {
				buffer.append("@" + pp.getStructure().getName() + ":");
				buffer.append(toBnglString(pp.getSpeciesPattern(), null, CompartmentMode.hide, 0));	// because we already set the compartment
			} else if(compartmentMode == CompartmentMode.asSite) {
				buffer.append(toBnglString(pp.getSpeciesPattern(), pp.getStructure(), CompartmentMode.asSite, i+1));	// last arg is index of product
			} else {
				buffer.append(toBnglString(pp.getSpeciesPattern(), null, CompartmentMode.hide, 0));
			}
		}
		return buffer.toString();
	}
	
	public static String toBnglStringShort(ParticleJumpProcess particleJumpProcess, List<ParticleSpeciesPattern> reactantSpeciesPatterns, List<ParticleSpeciesPattern> productSpeciesPatterns) {
		StringBuilder buffer = new StringBuilder();

		for (int i=0; i<reactantSpeciesPatterns.size(); i++){
			ParticleSpeciesPattern reactantSpeciesPattern = reactantSpeciesPatterns.get(i);
			if (i>0) {
				buffer.append(" + ");
			}
			buffer.append(toBnglString(reactantSpeciesPattern));
		}
		// particleJumpProcesses are not reversible
		boolean bReversible = false;
		buffer.append(bReversible ? " <-> " : " -> ");
		
		for (int i=0; i<productSpeciesPatterns.size(); i++){
			ParticleSpeciesPattern productSpeciesPattern = productSpeciesPatterns.get(i);
			if (i>0) {
				buffer.append(" + ");
			}
			buffer.append(toBnglString(productSpeciesPattern));
		}
		return buffer.toString();
	}
	
	public static String toBnglStringLong(ReactionRule reactionRule, CompartmentMode compartmentMode) {
		String str = reactionRule.getName() + ":\t";
		str += toBnglStringShort(reactionRule, compartmentMode);
		str += "\t\t";
		if(reactionRule.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MassActionForwardRate) != null) {
			String str1 = reactionRule.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MassActionForwardRate).infixBng();
			str += str1;
		} else {
			System.out.println("Could not recover Kf from " + reactionRule.getName());
			str += "1";
		}
		if(!reactionRule.isReversible()) {
			return str;
		}
		if (reactionRule.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MassActionReverseRate) != null) {
			String str1 = reactionRule.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MassActionReverseRate).infixBng();
			str += ", " + str1;
		} else {
			throw new RuntimeException("Reaction rule " + reactionRule.getName() + " (reversible) is missing the reverse rate Kr (null).");
		}
		return str;
	}
	
	public static String toBnglStringLong_internal(ReactionRule reactionRule, CompartmentMode compartmentMode) {
		String str = reactionRule.getName() + ":\t";
		if(reactionRule.getReactantPatterns().isEmpty()) {
			str += "0";
		}
		str += toBnglStringShort(reactionRule, compartmentMode);
		if(reactionRule.getProductPatterns().isEmpty()) {
			str += " 0";	// this is the new syntax for Trash!!! 
		}
		str += "\t";
		switch (reactionRule.getKineticLaw().getRateLawType()){
		case MassAction:{
			FakeReactionRuleRateParameter fakeForwardRateParam = new FakeReactionRuleRateParameter(reactionRule, RbmKineticLawParameterType.MassActionForwardRate);
			str += fakeForwardRateParam.fakeParameterName;
			if (reactionRule.isReversible()) {
				FakeReactionRuleRateParameter fakeReverseRateParam = new FakeReactionRuleRateParameter(reactionRule, RbmKineticLawParameterType.MassActionReverseRate);
				str += ", " + fakeReverseRateParam.fakeParameterName;
			}
			return str;
		}
		default:{
			throw new RuntimeException("kinetic law "+reactionRule.getKineticLaw().getRateLawType().name()+" not yet supported.");
		}
		}
	}

	public static String toBnglString(SimulationContext sc, SpeciesContext speciesContext, CompartmentMode compartmentMode) {
		SpeciesContextSpec scs = sc.getReactionContext().getSpeciesContextSpec(speciesContext);
		Expression expression = scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration).getExpression();
		SpeciesPattern sp = speciesContext.getSpeciesPattern();
		
		String s = "";
		if(compartmentMode == CompartmentMode.show) {
			s += "@" + speciesContext.getStructure().getName() + ":";
			s += toBnglString(sp, null, CompartmentMode.hide, 0) + "";
		} else if(compartmentMode == CompartmentMode.asSite) {
			s += toBnglString(sp, speciesContext.getStructure(), CompartmentMode.asSite, 0) + "";
		} else {
			s += toBnglString(sp, null, CompartmentMode.hide, 0) + "";
		}
		s += " " + expression.infix();
		return s;
	}
	public static String toBnglStringSortedAlphabetically(SimulationContext sc, SpeciesContext speciesContext, CompartmentMode compartmentMode) {
		SpeciesContextSpec scs = sc.getReactionContext().getSpeciesContextSpec(speciesContext);
		Expression expression = scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration).getExpression();
		SpeciesPattern sp = speciesContext.getSpeciesPattern();
		
		String s = "";		// nothing to sort here, sorting done within the species pattern
		if(compartmentMode == CompartmentMode.show) {
			s += "@" + speciesContext.getStructure().getName() + ":";
			s += toBnglStringSortedAlphabetically(sp, null, CompartmentMode.hide, 0) + "";
		} else if(compartmentMode == CompartmentMode.asSite) {
			s += toBnglStringSortedAlphabetically(sp, speciesContext.getStructure(), CompartmentMode.asSite, 0) + "";
		} else {
			s += toBnglStringSortedAlphabetically(sp, null, CompartmentMode.hide, 0) + "";
		}
		s += " " + expression.infix();
		return s;
	}
	
	public static String toBnglString(RbmObservable observable, CompartmentMode compartmentMode) {
		String s = observable.getType() + " " + observable.getName() + " ";
		for(SpeciesPattern sp : observable.getSpeciesPatternList()) {
			if(compartmentMode == CompartmentMode.show) {
				s += "@" + observable.getStructure().getName() + ":";
				s += toBnglString(sp, null, CompartmentMode.hide, 0) + " ";
			} else if(compartmentMode == CompartmentMode.asSite) {
				s += toBnglString(sp, observable.getStructure(), CompartmentMode.asSite, 0) + " ";
			} else {
				s += toBnglString(sp, null, CompartmentMode.hide, 0) + " ";
			}
		}
		return s;
	}

	public static String toBnglString(Parameter parameter, boolean bFunction) {
		if (!bFunction){		// parameter
			String str = parameter.getName() + " ";
			if(parameter.getExpression() != null) {
				str += parameter.getExpression().infixBng();
			} else {
				str = "# " + str + "(expression undefined)";
			}
			return str;
		}else{					// function
			String str = parameter.getName() + "()\t=\t";
			if(parameter.getExpression() != null) {
				String symbols[] = parameter.getExpression().getSymbols();
				for(String s : symbols) {
					SymbolTableEntry ste = parameter.getExpression().getSymbolBinding(s);
					if(ste instanceof SpeciesContext) {
						System.out.println("SpeciesContext '" + s + "' found in expression of function, not exporting");
						str = "# " + str;
						break;
					}
				}
				str += parameter.getExpression().infixBng();
			} else {
				str = "# " + str + "(expression undefined)";
			}
			return str;
		}
	}
	public static boolean haveUnsupportedFunctions(List<Parameter> functionList) {
		for (Parameter parameter : functionList) {
			if(parameter.getExpression() != null) {
				String symbols[] = parameter.getExpression().getSymbols();
				for(String s : symbols) {
					SymbolTableEntry ste = parameter.getExpression().getSymbolBinding(s);
					if(ste instanceof SpeciesContext) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// returns the structure with the lowest dimension of all reaction participants
	// if different structures of the same dimension are encountered we return the first one found
	// TODO: we should try first to get the explicit compartment name if given (through a bngl keyword)
	// TODO: keep the 2 versions of findStructure synced!!!
	public static Structure findStructure(Model model, ReactionRule rr) {
		Structure ours = null;
		for (ReactantPattern rp : rr.getReactantPatterns()) {
			Structure theirs = rp.getStructure();
			if(ours == null || ours.getDimension() > theirs.getDimension()) {
				ours = theirs;
			}
		}
		for (ProductPattern pp : rr.getProductPatterns()) {
			Structure theirs = pp.getStructure();
			if(ours == null || ours.getDimension() > theirs.getDimension()) {
				ours = theirs;
			}
		}
		if(ours == null) {
			ours = model.getStructure(0);
		}
		return ours;
	}
	// not used; instead of this we should recover the structure of the rule that produced this reaction
	@Deprecated
	public static Structure findStructure(Model model, HashMap<Integer, String> speciesMap, BNGReaction forwardBNGReaction) {
		Structure ours = null;
		for (int j = 0; j < forwardBNGReaction.getReactants().length; j++) {
			BNGSpecies s = forwardBNGReaction.getReactants()[j];
			String scName = speciesMap.get(s.getNetworkFileIndex());
			SpeciesContext sc = model.getSpeciesContext(scName);
			Structure theirs = sc.getStructure();
			if(ours == null || ours.getDimension() > theirs.getDimension()) {
				ours = theirs;
			}
		}
		for (int j = 0; j < forwardBNGReaction.getProducts().length; j++) {
			BNGSpecies s = forwardBNGReaction.getProducts()[j];
			String scName = speciesMap.get(s.getNetworkFileIndex());
			SpeciesContext sc = model.getSpeciesContext(scName);
			Structure theirs = sc.getStructure();
			if(ours == null || ours.getDimension() > theirs.getDimension()) {
				ours = theirs;
			}
		}
		if(ours == null) {
			ours = model.getStructure(0);
		}
		return ours;
	}


// TODO: jim's code to replace species names with their patterns
//	private static String getSpeciesPatternSubstitutedExpression(Expression exp) {
//		Expression exp2 = new Expression(exp);
//		//
//		// 0 gather speciesContexts in expression  and   replace speciesContexts with dummy names which are unique
//		//
//		ArrayList<SpeciesContext> speciesContexts = new ArrayList<SpeciesContext>();
//		String[] symbols = exp2.getSymbols();
//		final String PREFIX = "DUMMYVARIABLE";
//		int count = 0;
//		for (String symbol : symbols){
//			SymbolTableEntry ste = exp.getSymbolBinding(symbol);
//			if (ste instanceof SpeciesContext){
//				SpeciesContext sc = (SpeciesContext)ste;
//				if (!sc.hasSpeciesPattern()){
//					throw new RuntimeException("Exception generating BNGL format: SpeciesContext "+sc.getName()+" does not have a species pattern");
//				}
//				speciesContexts.add(sc);
//				try {
//					exp2.substituteInPlace(new Expression(symbol), new Expression(PREFIX+"_"+count));
//				} catch (ExpressionException e) {
//					e.printStackTrace();
//					throw new RuntimeException("Exception generating BNGL format: "+e.getMessage(),e);
//				}
//				count++;
//			}
//		}
//		//
//		// create infix string and replace dummy names with species pattern
//		//
//		String infixString = exp2.infix();
//		count = 0;
//		for (SpeciesContext sc : speciesContexts){
//			infixString = infixString.replace(PREFIX+"_"+count, toBnglString(sc.getSpeciesPattern()));
//			count++;
//		}
//		return infixString;
//	}
	public static String toBnglStringIgnoreExpression(Parameter function) {
		String str = function.getName() + "\t\t";
		str += "1";			// we totally ignore the expression and replace it with a '1'
		return str;
	}
	
	public static boolean isParenthesisMatchBothEnds(String str) {
		if (!str.startsWith("(") || !str.endsWith(")")) {
			return false;	// it's got to begin and end with matched parenthesis
		}
		Stack<Character> stack = new Stack<Character>();
		char c;
		for(int i=0; i < str.length(); i++) {
			c = str.charAt(i);
			if(c == '(') {
				stack.push(c);
			} else if(c == ')') {
				stack.pop();
				if(stack.empty()) {
					if( i == str.length()-1) {
						return true;	// found a match at the end of the string
					} else {
						return false;	// found a match within the string
					}
				}
			}
		}
		return false;
	}
	
	public static void removePropertyChangeListener(SpeciesPattern speciesPattern, PropertyChangeListener propertyChangeListener) {
		if (speciesPattern != null) {
			speciesPattern.removePropertyChangeListener(propertyChangeListener);
			for (MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
				removePropertyChangeListener(mtp, propertyChangeListener);
			}
		}
	}

	public static void removePropertyChangeListener(MolecularTypePattern molecularTypePattern, PropertyChangeListener propertyChangeListener) {
		molecularTypePattern.removePropertyChangeListener(propertyChangeListener);
		for (MolecularComponentPattern mcp : molecularTypePattern.getComponentPatternList()) {
			removePropertyChangeListener(mcp, propertyChangeListener);
		}
	}

	public static void removePropertyChangeListener(MolecularComponentPattern molecularComponentPattern, PropertyChangeListener propertyChangeListener) {
		molecularComponentPattern.removePropertyChangeListener(propertyChangeListener);
		molecularComponentPattern.getMolecularComponent().removePropertyChangeListener(propertyChangeListener);
		if (molecularComponentPattern.getComponentStatePattern() != null) {
			molecularComponentPattern.getComponentStatePattern().removePropertyChangeListener(propertyChangeListener);
		}
	}
	
	public static void addPropertyChangeListener(SpeciesPattern speciesPattern, PropertyChangeListener propertyChangeListener) {
		if (speciesPattern == null) {
			return;
		}
		speciesPattern.addPropertyChangeListener(propertyChangeListener);
		for (MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
			addPropertyChangeListener(mtp, propertyChangeListener);
		}
	}

	public static void addPropertyChangeListener(MolecularTypePattern molecularTypePattern, PropertyChangeListener propertyChangeListener) {
		molecularTypePattern.addPropertyChangeListener(propertyChangeListener);
		for (MolecularComponentPattern mcp : molecularTypePattern.getComponentPatternList()) {
			addPropertyChangeListener(mcp, propertyChangeListener);
		}
	}

	public static void addPropertyChangeListener(MolecularComponentPattern molecularComponentPattern, PropertyChangeListener propertyChangeListener) {
		molecularComponentPattern.addPropertyChangeListener(propertyChangeListener);
		molecularComponentPattern.getMolecularComponent().addPropertyChangeListener(propertyChangeListener);
		if (molecularComponentPattern.getComponentStatePattern() != null) {
			molecularComponentPattern.getComponentStatePattern().addPropertyChangeListener(propertyChangeListener);
		}
	}
	
	public static void removePropertyChangeListener(MolecularType molecularType, PropertyChangeListener propertyChangeListener) {
		molecularType.removePropertyChangeListener(propertyChangeListener);
		for (MolecularComponent molecularComponent : molecularType.getComponentList()) {
			removePropertyChangeListener(molecularComponent,
					propertyChangeListener);
		}
	}

	public static void removePropertyChangeListener(MolecularComponent molecularComponent, PropertyChangeListener propertyChangeListener) {
		molecularComponent.removePropertyChangeListener(propertyChangeListener);
		for (ComponentStateDefinition componentState : molecularComponent.getComponentStateDefinitions()) {
			componentState.removePropertyChangeListener(propertyChangeListener);
		}
	}
	
	public static void addPropertyChangeListener(MolecularType molecularType, PropertyChangeListener propertyChangeListener) {
		molecularType.addPropertyChangeListener(propertyChangeListener);
		for (MolecularComponent molecularComponent : molecularType.getComponentList()) {
			addPropertyChangeListener(molecularComponent, propertyChangeListener);
		}
	}

	public static void addPropertyChangeListener(MolecularComponent molecularComponent,	PropertyChangeListener propertyChangeListener) {
		molecularComponent.addPropertyChangeListener(propertyChangeListener);
		for (ComponentStateDefinition componentState : molecularComponent.getComponentStateDefinitions()) {
			componentState.addPropertyChangeListener(propertyChangeListener);
		}
	}
}
