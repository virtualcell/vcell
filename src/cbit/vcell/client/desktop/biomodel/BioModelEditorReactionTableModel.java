/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.model.rbm.RbmUtils;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.DefaultScrollTableActionManager;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.ModelProcessEquation;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.ModelProcess;
import cbit.vcell.model.ModelProcessDynamics;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorReactionTableModel extends BioModelEditorRightSideTableModel<ModelProcess> {	
	public final static int COLUMN_EQUATION = 0;
	public final static int COLUMN_NAME = 1;
	public final static int COLUMN_STRUCTURE = 2;
	public final static int COLUMN_DEPICTION = 3;
	public final static int COLUMN_KINETICS = 4;
	public final static int COLUMN_LINK = 5;
	public final static int COLUMN_DEFINITION = 6;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private static String[] columnNames = new String[] {"Reaction", "Name", "Structure", "Depiction", "Kinetics", "Link", "BioNetGen Definition"};
	
	public BioModelEditorReactionTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
//		disableActions();
	}
	
	@SuppressWarnings("unused")
	private void disableActions() {
		// example on how to disable right click batch editing on a column
		// the Name column is excluded by default
		if(ownerTable.getScrollTableActionManager() instanceof DefaultScrollTableActionManager) {
			DefaultScrollTableActionManager dstam = (DefaultScrollTableActionManager)ownerTable.getScrollTableActionManager();
			dstam.disablePopupAtColumn(COLUMN_EQUATION);
		}
	}

	public Class<?> getColumnClass(int column) {
		switch (column) {		
			case COLUMN_NAME: {
				return String.class;
			}
			case COLUMN_LINK: {
				return BioPaxObject.class;
			}
			case COLUMN_DEPICTION: {
				return Object.class;
			}
			case COLUMN_EQUATION:{
				return ModelProcessEquation.class;
			}
			case COLUMN_STRUCTURE: {
				return Structure.class;
			}
			case COLUMN_KINETICS: {
				return ModelProcessDynamics.class;
			}
			case COLUMN_DEFINITION: {
				return String.class;
			}
		}
		return Object.class;
	}

	protected ArrayList<ModelProcess> computeData() {
		ArrayList<ModelProcess> processList = new ArrayList<ModelProcess>();
		if (getModel() != null){
			ModelProcess[] modelProcesses = getModel().getModelProcesses();
			if (searchText == null || searchText.length() == 0) {
				processList.addAll(Arrays.asList(modelProcesses));
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();	
				for (ModelProcess process : modelProcesses){
					boolean bMatchRelationshipObj = false;
					if (process instanceof ReactionStep){
						ReactionStep reactionStep = (ReactionStep)process;
						HashSet<RelationshipObject> relObjsHash = bioModel.getRelationshipModel().getRelationshipObjects(reactionStep);
						for(RelationshipObject relObj:relObjsHash){
							if(relObj.getBioPaxObject() instanceof Entity){
								if(((Entity)relObj.getBioPaxObject()).getName().get(0).toLowerCase().contains(lowerCaseSearchText)){
									bMatchRelationshipObj = true;
									break;
								}
							}
						}
					}
					if (bMatchRelationshipObj || process.containsSearchText(lowerCaseSearchText)){
						processList.add(process);
					}
				}
			}
		}
		return processList;
	}

	public Object getValueAt(int row, int column) {
		if (getModel() == null) {
			return null;
		}
		try{
			ModelProcess process = getValueAt(row);
			if (process != null) {
				switch (column) {
					case COLUMN_NAME: {
						return process.getName();
					} 
					case COLUMN_LINK: {
						if (process instanceof ModelProcess){
							ModelProcess reactionStep = (ModelProcess)process;
							HashSet<RelationshipObject> relObjsHash = bioModel.getRelationshipModel().getRelationshipObjects(reactionStep);
							if(relObjsHash != null && relObjsHash.size() > 0) {
								BioPaxObject bpo = relObjsHash.iterator().next().getBioPaxObject();
								return bpo;
							}
						}
						return null;
					} 
					case COLUMN_EQUATION: {
						return new ModelProcessEquation(process, bioModel.getModel());
					} 
					case COLUMN_STRUCTURE: {
						return process.getStructure();
					} 
					case COLUMN_KINETICS: {
						return process.getDynamics();
					}
					case COLUMN_DEFINITION: {
						return new ModelProcessEquation(process, bioModel.getModel());
					}
				}
			} else {
				if (column == COLUMN_EQUATION) {
					return new ModelProcessEquation(null, bioModel.getModel());
				} 
			}
			return null;
		} catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	
	public boolean isCellEditable(int row, int column) {
		if (bioModel == null) {
			return false;
		}
		ModelProcess process = getValueAt(row);
		if (column == COLUMN_NAME && process != null) {
			return true;
		}
		if (column == COLUMN_EQUATION) {
			if(process instanceof ReactionStep) {
				return true;
			} else {
				return false;
			}
//			return bioModel.getModel().getNumStructures() == 1;
		}
		return false;
	}
//	public boolean isCellEditable(int row, int column) {
//		if (bioModel == null) {
//			return false;
//		}
//		ModelProcess process = getValueAt(row);
//		if (column == COLUMN_NAME && process != null) {
//			return true;
//		}
//		if (column == COLUMN_EQUATION) {
////			if(bioModel.getModel().getNumStructures() != 1) {
////				return false;
////			}
//			Object o = getValueAt(row);
//			if(o instanceof ReactionRule) {
//				ReactionRule rr = (ReactionRule)o;
//				final List<ReactantPattern> rpList = rr.getReactantPatterns();
//				for(ReactantPattern rp : rpList) {
//					final List<MolecularTypePattern> mtpList = rp.getSpeciesPattern().getMolecularTypePatterns();
//					for(MolecularTypePattern mtp : mtpList) {
//						MolecularType mt = mtp.getMolecularType();
//						if(mt.getComponentList().size() != 0) {
//							return false;
//						}
//					}
//				}
//				final List<ProductPattern> ppList = rr.getProductPatterns();
//				for(ProductPattern pp : ppList) {
//					final List<MolecularTypePattern> mtpList = pp.getSpeciesPattern().getMolecularTypePatterns();
//					for(MolecularTypePattern mtp : mtpList) {
//						MolecularType mt = mtp.getMolecularType();
//						if(mt.getComponentList().size() != 0) {
//							return false;
//						}
//					}
//				}
//			}
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == bioModel.getModel()) {
			if (evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
				//updateStructureComboBox();
			} else if (evt.getPropertyName().equals(Model.PROPERTY_NAME_REACTION_STEPS)) {
				ReactionStep[] oldValue = (ReactionStep[]) evt.getOldValue();
				if (oldValue != null) {
					for (ReactionStep rs : oldValue) {
						rs.removePropertyChangeListener(this);
					}
				}
				ReactionStep[] newValue = (ReactionStep[]) evt.getNewValue();
				if (newValue != null) {
					for (ReactionStep rs : newValue) {
						rs.addPropertyChangeListener(this);
					}
				}
				refreshData();
			}
		} else if (evt.getSource() instanceof ReactionStep) {
			ReactionStep reactionStep = (ReactionStep) evt.getSource();
			int changeRow = getRowIndex(reactionStep);
			if (changeRow >= 0) {
				fireTableRowsUpdated(changeRow, changeRow);
			}
		}
//		if (evt.getSource() == bioModel.getModel().getRbmModelContainer()) {
		if (evt.getSource() == bioModel.getModel()) {
			if (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_REACTION_RULE_LIST)) {
				List<ReactionRule> oldValue = (List<ReactionRule>) evt.getOldValue();
				if (oldValue != null) {
					for (ReactionRule rs : oldValue) {
						rs.removePropertyChangeListener(this);
					}
				}
				List<ReactionRule> newValue = (List<ReactionRule>)  evt.getNewValue();
				if (newValue != null) {
					for (ReactionRule rs : newValue) {
						rs.addPropertyChangeListener(this);
					}
				}
				refreshData();
			}
		} else if (evt.getSource() instanceof ReactionRule) {
			ReactionRule reactionRule = (ReactionRule) evt.getSource();
			int changeRow = getRowIndex(reactionRule);
			if (changeRow >= 0) {
				fireTableRowsUpdated(changeRow, changeRow);
			}
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null || value == null) {
			return;
		}
		try{
			ModelProcess modelProcess = getValueAt(row);
			if (modelProcess != null) {
				switch (column) {
				case COLUMN_NAME: {
					String inputValue = ((String)value);
					inputValue = inputValue.trim();
					modelProcess.setName(inputValue);
					break;
				} 
				case COLUMN_EQUATION: {
					String inputValue = (String)value;
					inputValue = inputValue.trim();
					if (modelProcess instanceof ReactionStep){
						ReactionStep reactionStep = (ReactionStep)modelProcess;
						ReactionParticipant[] rpArray = ModelProcessEquation.parseReaction(reactionStep, getModel(), inputValue);
						for (ReactionParticipant rp : rpArray) {
							SpeciesContext speciesContext = rp.getSpeciesContext();
							if (bioModel.getModel().getSpeciesContext(speciesContext.getName()) == null) {
								bioModel.getModel().addSpecies(speciesContext.getSpecies());
								bioModel.getModel().addSpeciesContext(speciesContext);
							}
						}
						reactionStep.setReactionParticipants(rpArray);
					}else if (modelProcess instanceof ReactionRule){
						ReactionRule oldReactionRule = (ReactionRule)modelProcess;
						// when editing an existing reaction rule
						ReactionRule newReactionRule = (ReactionRule)RbmUtils.parseReactionRule(inputValue, oldReactionRule.getStructure(), bioModel);
						if(newReactionRule != null) {
							oldReactionRule.setProductPatterns(newReactionRule.getProductPatterns(), false, false);
							oldReactionRule.setReactantPatterns(newReactionRule.getReactantPatterns(), false, false);
//							String name = oldReactionRule.getName();
//							RbmKineticLaw kl = oldReactionRule.getKineticLaw();
//							Structure st = oldReactionRule.getStructure();
//							getModel().getRbmModelContainer().removeReactionRule(oldReactionRule);
//							newReactionRule.setName(name);
//							newReactionRule.setKineticLaw(kl);
//							newReactionRule.setStructure(st);
//							getModel().getRbmModelContainer().addReactionRule(newReactionRule);
						}
					}
					break;
				}
				case COLUMN_STRUCTURE: {
					Structure s = (Structure)value;
					modelProcess.setStructure(s);
					break;
				} 
				}
			} else {
				switch (column) {
				case COLUMN_EQUATION: {
					if (getModel().getNumStructures() == 1) {
						String inputValue = ((String)value);
						inputValue = inputValue.trim();
						
						if(inputValue.contains("(") && inputValue.contains(")")) {
							ReactionRule reactionRule = (ReactionRule)RbmUtils.parseReactionRule(inputValue, getModel().getStructure(0), bioModel);
							getModel().getRbmModelContainer().addReactionRule(reactionRule);
						} else {
							if (BioModelEditorRightSideTableModel.ADD_NEW_HERE_REACTION_TEXT.equals(inputValue)) {
								return;
							}
							ReactionStep reactionStep = getModel().createSimpleReaction(getModel().getStructure(0));
							ReactionParticipant[] rpArray = ModelProcessEquation.parseReaction(reactionStep, getModel(), inputValue);
							for (ReactionParticipant rp : rpArray) {
								SpeciesContext speciesContext = rp.getSpeciesContext();
								if (bioModel.getModel().getSpeciesContext(speciesContext.getName()) == null) {
									bioModel.getModel().addSpecies(speciesContext.getSpecies());
									bioModel.getModel().addSpeciesContext(speciesContext);
								}
							}
							reactionStep.setReactionParticipants(rpArray);
						}
					}
					break;
				}
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage(), e);
		}
	}

	@Override
	public boolean isSortable(int col) {
		return true;
	}
	
	@Override
	public Comparator<ModelProcess> getComparator(final int col, final boolean ascending) {
		return new Comparator<ModelProcess>() {
            public int compare(ModelProcess o1, ModelProcess o2) {
            	int scale = ascending ? 1 : -1;
                if (col==COLUMN_NAME){
                	
                	String s1 = o1.getName();
                	String s2 = o2.getName();
                	String s1int = s1.replaceAll("[^0-9]", "");
                	String s2int = s2.replaceAll("[^0-9]", "");
                	// we try to properly sort the sequence x1, x11, x2, which should result in x1, x2, x11
                	// we look for pattern of letters followed numbers where we apply the special sort; for the rest we use normal (alphabetical) sort
                	if(s1int.isEmpty() || s2int.isEmpty()) {
                		return scale * o1.getName().compareTo(o2.getName());	// normal sort if no numbers
                	}
                	if(!s1.endsWith(s1int) || !s2.endsWith(s2int)) {
                		return scale * o1.getName().compareTo(o2.getName());	// normal sort if doesn't end in numbers
                	}
                	s1 = s1.substring(0, s1.indexOf(s1int));
                	s2 = s2.substring(0, s2.indexOf(s2int));
                	if(s1.isEmpty() || s2.isEmpty()) {
                		return scale * o1.getName().compareTo(o2.getName());	// normal sort if no letters
                	}
                	// we have the we want to properly sort: letters followed by numbers
                	if(!s1.equals(s2)) {
                		return scale * o1.getName().compareTo(o2.getName());	// normal sort if the letter part is not equal
                	}
                	Integer i1 = Integer.parseInt(s1int);
                	Integer i2 = Integer.parseInt(s2int);
					return scale * i1.compareTo(i2);
				} else if (col == COLUMN_EQUATION) {
					ModelProcessEquation re1 = new ModelProcessEquation(o1, bioModel.getModel());
					ModelProcessEquation re2 = new ModelProcessEquation(o2, bioModel.getModel());
					if(o1 instanceof ReactionStep && o2 instanceof ReactionRule) {
						return scale;
					} else if(o1 instanceof ReactionRule && o2 instanceof ReactionStep) {
						return -scale;
					}
					return scale * re1.toString().compareTo(re2.toString());
				} else if (col == COLUMN_STRUCTURE) {
					return scale * o1.getStructure().getName().compareTo(o2.getStructure().getName());
				}
				return 0;
            }
      };
	}
	
	public String checkInputValue(String inputValue, int row, int column) {
		ModelProcess modelProcess = getValueAt(row);
		String errMsg = null;
		switch (column) {
		case COLUMN_NAME:
			if (modelProcess == null || !modelProcess.getName().equals(inputValue)) {
				if (getModel().getReactionStep(inputValue) != null) {
					errMsg = "Reaction '" + inputValue + "' already exist!";
					errMsg += VCellErrorMessages.PressEscToUndo;
					errMsg = "<html>" + errMsg + "</html>";
					return errMsg;
				}
				if (getModel().getRbmModelContainer().getReactionRule(inputValue) != null) {
					errMsg = "ReactionRule '" + inputValue + "' already exist!";
					errMsg += VCellErrorMessages.PressEscToUndo;
					errMsg = "<html>" + errMsg + "</html>";
					return errMsg;
				}
			}
			break;
		case COLUMN_EQUATION:
			try {
				if (modelProcess instanceof ReactionStep){
					ReactionStep reactionStep = (ReactionStep)modelProcess;
					ModelProcessEquation.parseReaction(reactionStep, getModel(), inputValue);
				}else if (modelProcess instanceof ReactionRule){
					//ReactionRuleEmbedded reactionRule = (ReactionRuleEmbedded)modelProcess;
					ReactionRule newlyParsedReactionRule_NotUsedForValidation = RbmUtils.parseReactionRule(inputValue, getModel().getStructures()[0], bioModel);
				}else{
					// new row ... it's a rule if contains parentheses, plain reaction if it does not 
					if(inputValue.contains("(") && inputValue.contains(")")) {
						ReactionRule newlyParsedReactionRule_NotUsedForValidation = RbmUtils.parseReactionRule(inputValue, getModel().getStructures()[0], bioModel);
						if(newlyParsedReactionRule_NotUsedForValidation == null) {
							throw new RuntimeException("Unable to generate a reaction rule for this input.");
						}
					} else {
						ReactionStep reactionStep = (ReactionStep)modelProcess;
						ModelProcessEquation.parseReaction(reactionStep, getModel(), inputValue);
					}
				}
			} catch (org.vcell.model.bngl.ParseException ex) {
				errMsg = ex.getMessage();
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			} catch (Exception ex) {
				errMsg = ex.getMessage();
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			}
			break;
		case COLUMN_STRUCTURE:
			if (getModel().getStructure(inputValue) == null) {
				errMsg = "Structure '" + inputValue + "' does not exist!";
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			}
			break;
		}
		return null;
	}

	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final int row, final int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		if (column == COLUMN_STRUCTURE) {
			Set<String> words = new HashSet<String>();
			for (Structure s : getModel().getStructures()) {
				words.add(s.getName());
			}
			return words;
		}
		return null;
	}
	
	@Override
	protected void bioModelChange(PropertyChangeEvent evt) {		
		super.bioModelChange(evt);
//		ownerTable.getColumnModel().getColumn(COLUMN_STRUCTURE).setCellEditor(getStructureComboBoxEditor());
//		updateStructureComboBox();
		
		BioModel oldValue = (BioModel)evt.getOldValue();
		if (oldValue != null) {
			for (ModelProcess p : oldValue.getModel().getModelProcesses()) {
				p.removePropertyChangeListener(this);
			}
		}
		BioModel newValue = (BioModel)evt.getNewValue();
		if (newValue != null) {
			for (ModelProcess p : newValue.getModel().getModelProcesses()) {
				p.addPropertyChangeListener(this);
			}
		}
	}

	@Override
	public int getRowCount() {
//		return getRowCountWithAddNew();
		return super.getRowCount();
	}
}
