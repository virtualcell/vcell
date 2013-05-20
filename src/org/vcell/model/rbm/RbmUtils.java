package org.vcell.model.rbm;

import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.vcell.model.bngl.ASTAction;
import org.vcell.model.bngl.ASTAttributePattern;
import org.vcell.model.bngl.ASTBondExist;
import org.vcell.model.bngl.ASTBondPossible;
import org.vcell.model.bngl.ASTBondState;
import org.vcell.model.bngl.ASTKineticsParameter;
import org.vcell.model.bngl.ASTModel;
import org.vcell.model.bngl.ASTMolecularComponentPattern;
import org.vcell.model.bngl.ASTMolecularDefinitionBlock;
import org.vcell.model.bngl.ASTMolecularTypePattern;
import org.vcell.model.bngl.ASTObservable;
import org.vcell.model.bngl.ASTObservablesBlock;
import org.vcell.model.bngl.ASTParameter;
import org.vcell.model.bngl.ASTParameterBlock;
import org.vcell.model.bngl.ASTProduct;
import org.vcell.model.bngl.ASTReactant;
import org.vcell.model.bngl.ASTReactionRule;
import org.vcell.model.bngl.ASTReactionRulesBlock;
import org.vcell.model.bngl.ASTSeedSpecies;
import org.vcell.model.bngl.ASTSeedSpeciesBlock;
import org.vcell.model.bngl.ASTSpeciesPattern;
import org.vcell.model.bngl.BNGLParser;
import org.vcell.model.bngl.BNGLParserVisitor;
import org.vcell.model.bngl.Node;
import org.vcell.model.bngl.ParseException;
import org.vcell.model.bngl.SimpleNode;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.Observable.ObservableType;

import cbit.vcell.parser.Expression;

import com.ibm.icu.util.StringTokenizer;

public class RbmUtils {
	
	public static class BnglObjectConstructionVisitor implements BNGLParserVisitor {
		private RbmModelContainer rbmModelContainer;
		
		public BnglObjectConstructionVisitor() {
			this(null);
		}
		
		public BnglObjectConstructionVisitor(RbmModelContainer rbmModelContainer) {
			this.rbmModelContainer = rbmModelContainer;
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
				if (rbmModelContainer.getParameter(node.getName()) == null) {
					Expression exp = new Expression(node.getExpressionString());
//					exp.bindExpression(bioModel.getModel());
					RbmParameter mp = new RbmParameter(node.getName(), exp);
					rbmModelContainer.addParameter(mp);
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
			SeedSpecies seedSpecies = new SeedSpecies();
			node.childrenAccept(this, seedSpecies);
			try {
				Expression exp = new Expression(node.getInitial());
				seedSpecies.setInitialCondition(exp);
			} catch (Exception ex) {
				throw new RuntimeException("Initial condition for seed species " + toBnglString(seedSpecies.getSpeciesPattern()) + " can not be initialized: " + ex.getMessage());
			}	
			if (data instanceof RbmModelContainer) {
				assert data == rbmModelContainer;
				try {
					rbmModelContainer.addSeedSpecies(seedSpecies);
				} catch (Exception ex) {
					throw new RuntimeException(ex.getMessage());
				}
			}
			return seedSpecies;
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
				assert data == rbmModelContainer;
				ReactionRule reactionRule = rbmModelContainer.createReactionRule(node.getArrowString().equals("<->"));
				node.childrenAccept(this, reactionRule);
				rbmModelContainer.addReactionRule(reactionRule);
				return reactionRule;
			}
			return null;
		}

		public Object visit(ASTReactant node, Object data) {
	      for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
	    	  Node child = node.jjtGetChild(i);
	    	  Object object = child.jjtAccept(this, data);
	    	  if (data instanceof ReactionRule && object instanceof SpeciesPattern) {
	    		  ((ReactionRule) data).addReactant((SpeciesPattern) object);
	    	  }
	      }
	      return null;
		}

		public Object visit(ASTProduct node, Object data) {
			for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
		    	  Node child = node.jjtGetChild(i);
		    	  Object object = child.jjtAccept(this, data);
		    	  if (data instanceof ReactionRule && object instanceof SpeciesPattern) {
		    		  ((ReactionRule) data).addProduct((SpeciesPattern) object);
		    	  }
		     }
		     return null;
		}

		public Object visit(ASTKineticsParameter node, Object data) {
			if (data instanceof ReactionRule) {
				RbmParameter mp = rbmModelContainer.getParameter(node.getValue());
				if (mp == null) {
					throw new RuntimeException("Parameter '" + node.getValue() + "' is not defined!");
				}
				if (((ReactionRule) data).getForwardRate() == null) {
					((ReactionRule) data).setForwardRate(mp);
				} else {
					((ReactionRule) data).setReverseRate(mp);
				}
			}
			return null;
		}

		public Object visit(ASTSpeciesPattern node, Object data) {
			SpeciesPattern sp = new SpeciesPattern();
			node.childrenAccept(this, sp);
			if (data instanceof Observable) {
				((Observable) data).setSpeciesPattern(sp);
			} else if (data instanceof SeedSpecies) {
				((SeedSpecies) data).setSpeciesPattern(sp);
			}
			return sp;
		}

		public Object visit(ASTMolecularTypePattern node, Object data) {
			String name = node.getName();
			if (data == null || data instanceof RbmModelContainer) {
				MolecularType molecularType = new MolecularType(name);
				node.childrenAccept(this, molecularType);
				if (data != null) {
					assert data == rbmModelContainer;
					try {
						rbmModelContainer.addMolecularType(molecularType);
					} catch (Exception ex) {
						throw new RuntimeException(ex.getMessage());
					}
				}
				return molecularType;
			} else if (data instanceof SpeciesPattern) {
				assert rbmModelContainer != null;
				MolecularType molecularType = rbmModelContainer.getMolecularType(name);
				if (molecularType == null) {
					throw new RuntimeException("Molecule type '" + name + "' doesn't exist!");
				}
				MolecularTypePattern molecularTypePattern = new MolecularTypePattern(molecularType);
				node.childrenAccept(this, molecularTypePattern);
				((SpeciesPattern) data).addMolecularTypePattern(molecularTypePattern);
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
				MolecularType molecularType = ((MolecularTypePattern) data).getMolecularType();
				MolecularComponent molecularComponent = molecularType.getMolecularComponent(name);
				if (molecularComponent == null) {
					throw new RuntimeException("Molecule component '" + name + "' doesn't exist!");
				}
				MolecularComponentPattern molecularComponentPattern = ((MolecularTypePattern) data).getMolecularComponentPattern(molecularComponent);
				molecularComponentPattern.setBondType(BondType.None);
				node.childrenAccept(this, molecularComponentPattern);
				return molecularComponentPattern;
			}
			return null;
		}

		public Object visit(ASTAttributePattern node, Object data) {
			if (data instanceof MolecularComponent) {
				ComponentState componentState = new ComponentState(node.getComponentState());
				((MolecularComponent)data).addComponentState(componentState);
				return componentState;
			} else if (data instanceof MolecularComponentPattern) {				
				ComponentState componentState = ((MolecularComponentPattern) data).getMolecularComponent().getComponentState(node.getComponentState()); 
				((MolecularComponentPattern) data).setComponentState(componentState);
				return componentState;
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
			Observable observable = new Observable(node.getName(), ObservableType.valueOf(node.getType()));
			node.childrenAccept(this, observable);
			if (data instanceof RbmModelContainer) {
				assert data == rbmModelContainer;
				try {
					rbmModelContainer.addObservable(observable);
				} catch (Exception ex) {
					throw new RuntimeException(ex.getMessage());
				}
			}
			return observable;
		}
		
	}
	
	public static void importBnglFile(Reader reader, RbmModelContainer rbmModelContainer) throws ParseException {
		try {
			// remove all the comments
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(reader);
			boolean bEscapingExpressionBegin = false;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}
				if (line.startsWith("end parameters") || line.startsWith("end seed species")) {
					bEscapingExpressionBegin = false;
				}
				int commentIndex = line.indexOf('#');
				if (commentIndex >= 0) {
					line = line.substring(0, commentIndex);
				}
				if (line.length() == 0) {
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
					sb.append("}\n");
				} else {
					sb.append(line + "\n");
				}
				if (line.startsWith("begin parameters") || line.startsWith("begin seed species")) {
					bEscapingExpressionBegin = true;
				}
			}
			br.close();
			String commentRemovedBngl = sb.toString();
//			System.out.println(commentRemovedBngl);
			BNGLParser parser = new BNGLParser(new StringReader(commentRemovedBngl));
			ASTModel astModel = parser.Model();
			BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(rbmModelContainer);
			astModel.jjtAccept(constructionVisitor, rbmModelContainer); 
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage());
		}
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
	
	public static SpeciesPattern parseSpeciesPattern(String inputString, RbmModelContainer rbmModelContainer) throws ParseException {
		try {
			BNGLParser parser = new BNGLParser(new StringReader(inputString));
			ASTSpeciesPattern astSpeciesPattern = parser.SpeciesPattern();
			BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(rbmModelContainer);
			SpeciesPattern speciesPattern = (SpeciesPattern) astSpeciesPattern.jjtAccept(constructionVisitor, null);
			return speciesPattern;
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage());
		}
	}
	
	public static ReactionRule parseReactionRule(String inputString, RbmModelContainer rbmModelContainer) throws ParseException {
		try {
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
			
			ReactionRule reactionRule = rbmModelContainer.createReactionRule(bReversible);
			String regex = "[^!]\\+";
			String[] patterns = left.split(regex);
			for (String sp : patterns) {
				SpeciesPattern speciesPattern = parseSpeciesPattern(sp, rbmModelContainer);
				reactionRule.addReactant(speciesPattern);
			}
			
			patterns = left.split(regex);
			for (String sp : patterns) {
				SpeciesPattern speciesPattern = parseSpeciesPattern(sp, rbmModelContainer);
				reactionRule.addProduct(speciesPattern);
			}			
			return reactionRule;
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage());
		}
	}
	
	public static String toBnglString(ComponentState componentState) {		
		return "~" + componentState.getName();
	}
	
	public static String toBnglString(MolecularComponent molecularComponent) {
		StringBuilder buffer = new StringBuilder(molecularComponent.getName());
		for (ComponentState componentState : molecularComponent.getComponentStates()) {
			buffer.append(toBnglString(componentState));
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
		if (molecularComponentPattern.getComponentState() != null) {
			buffer.append(toBnglString(molecularComponentPattern.getComponentState()));
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
		List<SpeciesPattern> reactants = reactionRule.getReactantPatterns();
		for (int i = 0;  i < reactants.size(); ++i) {
			if (i > 0) {
				buffer.append(" + ");
			}
			buffer.append(toBnglString(reactants.get(i)));
		}
		buffer.append(reactionRule.getReverseRate() == null ? " -> " : " <-> ");
		List<SpeciesPattern> products = reactionRule.getProductPatterns();
		for (int i = 0;  i < products.size(); ++i) {
			if (i > 0) {
				buffer.append(" + ");
			}
			buffer.append(toBnglString(products.get(i)));
		}
		return buffer.toString();
	}
	
	public static String toBnglStringLong(ReactionRule reactionRule) {
		String str = toBnglStringShort(reactionRule) + "\t\t" + reactionRule.getForwardRate().getName();
		if (reactionRule.getReverseRate() != null) {
			str += ", " + reactionRule.getReverseRate().getName();
		}
		return str;
	}
	
	public static String toBnglString(RbmParameter parameter) {
		return parameter.getName() + "\t\t" + parameter.getExpression().infix();
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
		if (molecularComponentPattern.getComponentState() != null) {
			molecularComponentPattern.getComponentState().removePropertyChangeListener(propertyChangeListener);
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
		if (molecularComponentPattern.getComponentState() != null) {
			molecularComponentPattern.getComponentState().addPropertyChangeListener(propertyChangeListener);
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
		for (ComponentState componentState : molecularComponent.getComponentStates()) {
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
		for (ComponentState componentState : molecularComponent.getComponentStates()) {
			componentState.addPropertyChangeListener(propertyChangeListener);
		}
	}

	public static String toBnglString(SeedSpecies seedSpecies) {		
		return toBnglString(seedSpecies.getSpeciesPattern()) + "\t\t" + seedSpecies.getInitialCondition().infix();
	}
}
