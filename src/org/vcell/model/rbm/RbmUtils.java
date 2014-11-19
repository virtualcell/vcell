package org.vcell.model.rbm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.vcell.model.bngl.ASTAction;
import org.vcell.model.bngl.ASTAddNode;
import org.vcell.model.bngl.ASTAndNode;
import org.vcell.model.bngl.ASTAttributePattern;
import org.vcell.model.bngl.ASTBondExist;
import org.vcell.model.bngl.ASTBondPossible;
import org.vcell.model.bngl.ASTBondState;
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

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.Expression.FunctionFilter;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

import java.util.StringTokenizer;

public class RbmUtils {
	
	@Deprecated
	public static int reactionRuleLabelIndex;
	@Deprecated
	public static ArrayList<String> reactionRuleNames = new ArrayList<String>();
	
	public static class BnglObjectConstructionVisitor implements BNGLParserVisitor {
		private boolean stopOnError = true;	// throw exception if object which should have been there is missing
		private Model model = null;
		SimulationContext application = null;
		
		public BnglObjectConstructionVisitor() {
			this(null, null, true);
		}
		
		public BnglObjectConstructionVisitor(Model model, SimulationContext application, boolean stopOnError) {
			this.stopOnError = stopOnError;
			this.model = model;
			this.application = application;
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
					model.getRbmModelContainer().addParameter(node.getName(), exp);
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
			assert application != null;
			SeedSpecies seedSpecies = new SeedSpecies();		// limited scope, we only need it to allow for a proper visitation process
			node.childrenAccept(this, seedSpecies);
			try {
				Expression exp = new Expression(node.getInitial());
				seedSpecies.setInitialCondition(exp);
			if (data instanceof RbmModelContainer) {
				try {
					Structure structure = model.getStructure(0);
					SpeciesContext speciesContext = model.createSpeciesContext(structure, seedSpecies.getSpeciesPattern());
					
//					SimulationContext sc = bioModel.getSimulationContexts()[0];	// TODO: we assume one single simulation context which may not be the case
					SpeciesContextSpec scs = application.getReactionContext().getSpeciesContextSpec(speciesContext);
					scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration).setExpression(exp);
				} catch (Exception ex) {
					throw new RuntimeException(ex.getMessage());
				}
			}
			return seedSpecies;
			} catch (Exception ex) {
				throw new RuntimeException("Initial condition for seed species " + toBnglString(seedSpecies.getSpeciesPattern()) + " can not be initialized: " + ex.getMessage());
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
				ReactionRule reactionRule = model.getRbmModelContainer().createReactionRule(node.getLabel(), node.getArrowString().equals("<->"));
				node.childrenAccept(this, reactionRule);
				if(model.getStructures().length > 0) {
					reactionRule.setStructure(model.getStructure(0));
				}
				model.getRbmModelContainer().addReactionRule(reactionRule);
				return reactionRule;
			}
			return null;
		}

		public Object visit(ASTReactant node, Object data) {
	      for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
	    	  Node child = node.jjtGetChild(i);
	    	  Object object = child.jjtAccept(this, data);
	    	  if (data instanceof ReactionRule && object instanceof SpeciesPattern) {
	    		  ((ReactionRule) data).addReactant(new ReactantPattern((SpeciesPattern) object));
	    	  }
	      }
	      return null;
		}

		public Object visit(ASTProduct node, Object data) {
			for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
		    	  Node child = node.jjtGetChild(i);
		    	  Object object = child.jjtAccept(this, data);
		    	  if (data instanceof ReactionRule && object instanceof SpeciesPattern) {
		    		  ((ReactionRule) data).addProduct(new ProductPattern((SpeciesPattern) object));
		    	  }
		     }
		     return null;
		}

		public Object visit(ASTKineticsParameter node, Object data) {
			if (data instanceof ReactionRule) {
				ReactionRule rr = (ReactionRule)data;
				
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
						rr.getKineticLaw().setParameterValue(RbmKineticLaw.ParameterType.MassActionForwardRate, new Expression(node.getValue()));
					} catch (PropertyVetoException | ExpressionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					try {
						rr.getKineticLaw().setParameterValue(RbmKineticLaw.ParameterType.MassActionReverseRate, new Expression(node.getValue()));
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
				MolecularType molecularType = new MolecularType(name);
				node.childrenAccept(this, molecularType);
				if (data != null) {
					try {
						model.getRbmModelContainer().addMolecularType(molecularType);
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
						throw new RuntimeException("Molecule type '" + name + "' doesn't exist!");
					} else {
						System.out.println("Molecule type '" + name + "' doesn't exist!");
					}
					molecularType = new MolecularType(name);
					try {
						model.getRbmModelContainer().addMolecularType(molecularType);
					} catch (ModelException e) {
						e.printStackTrace();
					}
				}
				MolecularTypePattern molecularTypePattern = new MolecularTypePattern(molecularType);
				node.childrenAccept(this, molecularTypePattern);
				((SpeciesPattern) data).addMolecularTypePattern(molecularTypePattern);
				molecularTypePattern.ClearProcessedMolecularComponentsMultiMap();
				return molecularTypePattern;
			}
			return null;
		}

		public Object visit(ASTMolecularComponentPattern node, Object data) {
			String name = node.getName();
			if (data instanceof MolecularType) {
				MolecularComponent molecularComponent = new MolecularComponent(name);
				node.childrenAccept(this, molecularComponent);
				((MolecularType)data).addMolecularComponent(molecularComponent);				
				return molecularComponent;
			} else if (data instanceof MolecularTypePattern) {
				MolecularTypePattern molecularTypePattern = (MolecularTypePattern) data;
				MolecularType molecularType = molecularTypePattern.getMolecularType();
				MolecularComponent[] molecularComponents = molecularType.getMolecularComponents(name);
				// find first unused molecularComponent with the correct name;
				MolecularComponent molecularComponent = null;
				if(molecularComponents.length == 1) {
					molecularComponent = molecularType.getMolecularComponent(name);
				} else {
					molecularComponent = molecularTypePattern.getFirstUnprocessedMolecularComponent(name, molecularComponents);
				}				
				if (molecularComponent == null) {
					if(stopOnError) {
						throw new RuntimeException("Molecule component '" + name + "' doesn't exist!");
					} else {
						System.out.println("Molecule component '" + name + "' doesn't exist!");
					}
					molecularComponent = new MolecularComponent(name);
					molecularType.addMolecularComponent(molecularComponent);
					node.childrenAccept(this, molecularComponent);
				}
				MolecularComponentPattern molecularComponentPattern = molecularTypePattern.getMolecularComponentPattern(molecularComponent);
				molecularComponentPattern.setBondType(BondType.None);
				node.childrenAccept(this, molecularComponentPattern);
				return molecularComponentPattern;
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
						System.out.println("ComponentStateDefinition '" + node.getComponentState() + "' doesn't exist!");
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
					model.getRbmModelContainer().addFunction(node.getName(), exp);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Function '" + node.getName() + " can not be added, " + ex.getMessage());
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
					SymbolTableEntry ste = symbolTable.getEntry(invocation.getFunctionName());
					if (ste != null){
						exp.substituteInPlace(invocation.getFunctionExpression(), new Expression(ste,ste.getNameScope()));
					}else{
						throw new RuntimeException("function "+invocation.getFunctionExpression().infix()+" not found");
					}
				}else{
					//
					// one or more arguments ... have to flatten this one (do this later)
					//
					throw new RuntimeException("Function invocations with arguments not yet supported");
				}
				System.out.println(invocations.toString());
			}
		}
		exp.bindExpression(symbolTable);
		return exp;
	}
	
	public enum BnglLocation {
		Parameter, Species, MoleculeType, Observable, Function, ReactionRule, OutsideBlock
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
		try {
			// remove all the comments
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(reader);
			reactionRuleNames.clear();
			ArrayList<BnglComment> comments = new ArrayList<BnglComment>();
			int lineNumber = 0;
			boolean bEscapingExpressionBegin = false;
			BnglLocation location = BnglLocation.OutsideBlock;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				line = applySyntaxCorrections(line);
				
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
					// within a block and we simply concatenate them
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
					}
				}
				sb.append(line + "\n");
				location = newLocation;
				lineNumber++;
			}
			br.close();
			cleanedBngl = sb.toString();
			System.out.println(cleanedBngl);
			
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
			return astModel;
//			BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(rbmModelContainer);
//			astModel.jjtAccept(constructionVisitor, rbmModelContainer); 
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage());
		}
	}
	public static String generateReactionRuleName() {
		String label = "r" + reactionRuleLabelIndex;
		reactionRuleLabelIndex++;
		return label;
	}
	public static BnglLocation markBlock(BnglLocation currentLocation, String line) {
		if(line.startsWith("begin parameters")) {
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
	
	public static SpeciesPattern parseSpeciesPattern(String inputString, Model model) throws ParseException {
		try {
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
	
	public static ReactionRule parseReactionRule(String inputString, BioModel bioModel) throws ParseException {
		return parseReactionRule(inputString, null, bioModel);
	}

	public static ReactionRule parseReactionRule(String inputString, String name, BioModel bioModel) throws ParseException {
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
			ReactionRule reactionRule = bioModel.getModel().getRbmModelContainer().createReactionRule(label, bReversible);
			String regex = "[^!]\\+";
			String[] patterns = left.split(regex);
			for (String sp : patterns) {
				SpeciesPattern speciesPattern = parseSpeciesPattern(sp, bioModel.getModel());
				reactionRule.addReactant(new ReactantPattern(speciesPattern));
			}
			
			patterns = right.split(regex);
			for (String sp : patterns) {
				SpeciesPattern speciesPattern = parseSpeciesPattern(sp, bioModel.getModel());
				reactionRule.addProduct(new ProductPattern(speciesPattern));
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
	public static String toBnglString(ComponentStateDefinition componentStateDefinition) {
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
	
	public static String toBnglString(MolecularType molecularType) {
		StringBuilder buffer = new StringBuilder(molecularType.getName());
		buffer.append("(");
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
	
	public static String toBnglString(SpeciesPattern speciesPattern) {
		if (speciesPattern == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		List<MolecularTypePattern> molecularTypePatterns = speciesPattern.getMolecularTypePatterns();
		for (int i = 0; i < molecularTypePatterns.size(); ++ i) {
			if (i > 0) {
				buffer.append(".");
			}
			buffer.append(toBnglString(molecularTypePatterns.get(i)));
		}
		return buffer.toString();
	}

	private static String toBnglString(MolecularTypePattern molecularTypePattern) {
		StringBuilder buffer = new StringBuilder(molecularTypePattern.getMolecularType().getName());
		buffer.append("(");
		List<MolecularComponentPattern> componentPatterns = molecularTypePattern.getComponentPatternList();
		boolean bAddComma = false;
		for (MolecularComponentPattern mcp : componentPatterns) {
			if (mcp.isImplied()) {
				continue;
			}
			if (bAddComma) {
				buffer.append(",");
			}
			buffer.append(toBnglString(mcp));
			bAddComma = true;
		}
		buffer.append(")");
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
	
	public static String toBnglStringShort(ReactionRule reactionRule) {
		StringBuilder buffer = new StringBuilder();
		List<ReactantPattern> reactants = reactionRule.getReactantPatterns();
		for (int i = 0;  i < reactants.size(); ++i) {
			if (i > 0) {
				buffer.append(" + ");
			}
			buffer.append(toBnglString(reactants.get(i).getSpeciesPattern()));
		}
		buffer.append(reactionRule.isReversible() ? " <-> " : " -> ");
		List<ProductPattern> products = reactionRule.getProductPatterns();
		for (int i = 0;  i < products.size(); ++i) {
			if (i > 0) {
				buffer.append(" + ");
			}
			buffer.append(toBnglString(products.get(i).getSpeciesPattern()));
		}
		return buffer.toString();
	}
	
	public static String toBnglStringLong(ReactionRule reactionRule) {
		String str = reactionRule.getName() + ":\t";
		str += toBnglStringShort(reactionRule);
		str += "\t\t";
		if(reactionRule.getKineticLaw().getParameterValue(RbmKineticLaw.ParameterType.MassActionForwardRate) != null) {
			str += reactionRule.getKineticLaw().getParameterValue(RbmKineticLaw.ParameterType.MassActionForwardRate).infix();
		} else {
			str += "(no name)";
		}
		if(!reactionRule.isReversible()) {
			return str;
		}
		if (reactionRule.getKineticLaw().getParameterValue(RbmKineticLaw.ParameterType.MassActionReverseRate) != null) {
			str += ", " + reactionRule.getKineticLaw().getParameterValue(RbmKineticLaw.ParameterType.MassActionReverseRate).infix();
		} else {
			throw new RuntimeException("Reaction rule " + reactionRule.getName() + " (reversible) is missing the reverse rate Kr (null).");
		}
		return str;
	}
	
	public static String toBnglString(Parameter parameter, boolean bFunction) {
		if (!bFunction){
			String str = parameter.getName() + "\t\t";
			if(parameter.getExpression() != null) {
				str += parameter.getExpression().infix();
			} else {
				str += "(undefined)";
			}
			return str;
		}else{
			String str = parameter.getName() + "()\t=\t";
			if(parameter.getExpression() != null) {
				str += parameter.getExpression().infix();
			} else {
				str += "(undefined)";
			}
			return str;
		}
	}
	
	public static String convertToBngl(SimulationContext simulationContext) {
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);
		RbmNetworkGenerator.writeBngl(simulationContext, pw);
		String bngl = bnglStringWriter.toString();
		pw.close();
		System.out.println(bngl);
		return bngl;
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

	public static String toBnglString(SimulationContext sc, SpeciesContext speciesContext) {
		SpeciesContextSpec scs = sc.getReactionContext().getSpeciesContextSpec(speciesContext);
		Expression expression = scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration).getExpression();
		String ret = toBnglString(speciesContext.getSpeciesPattern());
		return ret  + "\t\t" + expression.infix();
	}
	
	public static String toBnglString(RbmObservable observable) {
		String s = observable.getType() + "\t\t" + observable.getName() + "\t\t";
		for(SpeciesPattern sp : observable.getSpeciesPatternList()) {
			s += toBnglString(sp) + "\t";
		}
		return s;
	}
	
	
	
}
