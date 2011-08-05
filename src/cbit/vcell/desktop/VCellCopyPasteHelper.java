/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;

import java.awt.Component;

import javax.swing.JCheckBox;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Constant;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.modelopt.ParameterMappingSpec;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.MathOverrides;

/**
 * Insert the type's description here.
 * Creation date: (5/17/2006 1:56:19 PM)
 * @author: Frank Morgan
 */
public class VCellCopyPasteHelper {
/**
 * VCellCopyPasteHelper constructor comment.
 */
public VCellCopyPasteHelper() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste(Component requester, 
		String[] pasteDetails,
		SpeciesContextSpec.SpeciesContextSpecParameter[] changingParamters,
		Expression[] newParameterExpression) {
			

	if(pasteDetails.length != changingParamters.length || changingParamters.length != newParameterExpression.length){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingParamters.length];
	for(int i=0;i<changingParamters.length;i+= 1){
		//bEnableDisplay[i] = !changingParamters[i].getExpression().equals(newParameterExpression[i]);
		bEnableDisplay[i] = !Compare.isEqualOrNull(changingParamters[i].getExpression(),newParameterExpression[i]);
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		PopupGenerator.showInfoDialog(requester, "All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(requester, pasteDetails,bEnableDisplay);
	if(bChoices != null){
		StringBuffer statusMessages = new StringBuffer();
		boolean bFailure = false;
		for(int i=0;i<changingParamters.length;i+= 1){
			try{
				if(bChoices[i]){
					changingParamters[i].setExpression(newParameterExpression[i]);
				}
				statusMessages.append("(OK) "+pasteDetails+"\n");
			}catch(Exception e){
				bFailure = true;
				statusMessages.append("(Failed) "+pasteDetails+" "+e.getMessage()+" "+e.getClass().getName()+"\n");
			}
		}
		if(bFailure){
			PopupGenerator.showErrorDialog(requester, "Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste(Component requester, 
		String[] pasteDetails,
		Parameter[] changingParamters,
		Expression[] newParameterExpression) {
			

	if(pasteDetails.length != changingParamters.length || changingParamters.length != newParameterExpression.length){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingParamters.length];
	for(int i=0;i<changingParamters.length;i+= 1){
		//bEnableDisplay[i] = !changingParamters[i].getExpression().equals(newParameterExpression[i]);
		bEnableDisplay[i] = !Compare.isEqualOrNull(changingParamters[i].getExpression(),newParameterExpression[i]);
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		PopupGenerator.showInfoDialog(requester, "All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(requester, pasteDetails,bEnableDisplay);
	if(bChoices != null){
		StringBuffer statusMessages = new StringBuffer();
		boolean bFailure = false;
		for(int i=0;i<changingParamters.length;i+= 1){
			try{
				if(bChoices[i]){
					if (changingParamters[i] instanceof Kinetics.KineticsParameter){
						Kinetics kinetics = ((ReactionStep) changingParamters[i].getNameScope().getScopedSymbolTable()).getKinetics();
						kinetics.setParameterValue((Kinetics.KineticsParameter)changingParamters[i],newParameterExpression[i]);
					}else{
						throw new Exception("Changing "+changingParamters[i].getNameScope().getName()+" "+changingParamters[i].getName()+" not yet implemented");
					}
				}
				statusMessages.append("(OK) "+pasteDetails+"\n");
			}catch(Exception e){
				bFailure = true;
				statusMessages.append("(Failed) "+pasteDetails+" "+e.getMessage()+" "+e.getClass().getName()+"\n");
			}
		}
		if(bFailure){
			PopupGenerator.showErrorDialog(requester, "Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste(Component requester, 
		String[] pasteDetails,
		ParameterMappingSpec[] changingParamters,
		Expression[] newParameterExpression) {
			

	if(pasteDetails.length != changingParamters.length || changingParamters.length != newParameterExpression.length){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingParamters.length];
	for(int i=0;i<changingParamters.length;i+= 1){
		//bEnableDisplay[i] = !changingParamters[i].getExpression().equals(newParameterExpression[i]);
		bEnableDisplay[i] = !Compare.isEqualOrNull(new Expression(changingParamters[i].getCurrent()),newParameterExpression[i]);
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		PopupGenerator.showInfoDialog(requester, "All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(requester, pasteDetails,bEnableDisplay);
	if(bChoices != null){
		StringBuffer statusMessages = new StringBuffer();
		boolean bFailure = false;
		for(int i=0;i<changingParamters.length;i+= 1){
			try{
				if(bChoices[i]){
					changingParamters[i].setCurrent(newParameterExpression[i].evaluateConstant());
				}
				statusMessages.append("(OK) "+pasteDetails+"\n");
			}catch(Exception e){
				bFailure = true;
				statusMessages.append("(Failed) "+pasteDetails+" "+e.getMessage()+" "+e.getClass().getName()+"\n");
			}
		}
		if(bFailure){
			PopupGenerator.showErrorDialog(requester, "Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste(Component requester, 
		String[] pasteDetails,
		MathOverrides mathOverrides,
		String[] changingMathOverridesNames,
		java.util.Vector<?> newMathOverridesValuesV) {
			

	if(pasteDetails.length != changingMathOverridesNames.length || changingMathOverridesNames.length != newMathOverridesValuesV.size()){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingMathOverridesNames.length];
	for(int i=0;i<changingMathOverridesNames.length;i+= 1){
		Object newValue = newMathOverridesValuesV.elementAt(i);
		if(newValue instanceof Expression){
			bEnableDisplay[i] = !Compare.isEqualOrNull(
					mathOverrides.getActualExpression(changingMathOverridesNames[i],0),
					((Expression)newValue));
		}else if(newValue instanceof ConstantArraySpec){
			bEnableDisplay[i] =	!Compare.isEqualOrNull(
					mathOverrides.getConstantArraySpec(changingMathOverridesNames[i]),
					(ConstantArraySpec)newValue);
		}else{
			PopupGenerator.showErrorDialog(requester, "Unexpected MathOverride type="+newValue.getClass().getName()+"\nPaste Failed, nothing changed.");
			return;
		}
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		PopupGenerator.showInfoDialog(requester, "All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(requester, pasteDetails,bEnableDisplay);
	if(bChoices != null){
		StringBuffer statusMessages = new StringBuffer();
		boolean bFailure = false;
		for(int i=0;i<changingMathOverridesNames.length;i+= 1){
			try{
				if(bChoices[i]){
					if(newMathOverridesValuesV.elementAt(i) instanceof Expression){
						mathOverrides.putConstant(new Constant(changingMathOverridesNames[i],(Expression)newMathOverridesValuesV.elementAt(i)));
					}else if(newMathOverridesValuesV.elementAt(i) instanceof ConstantArraySpec){
						mathOverrides.putConstantArraySpec((ConstantArraySpec)newMathOverridesValuesV.elementAt(i));
					}
				}
				statusMessages.append("(OK) "+pasteDetails+"\n");
			}catch(Exception e){
				bFailure = true;
				statusMessages.append("(Failed) "+pasteDetails+" "+e.getMessage()+" "+e.getClass().getName()+"\n");
			}
		}
		if(bFailure){
			PopupGenerator.showErrorDialog(requester, "Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste_NOT_USED(Component requester, 
		String[] pasteDetails,
		MathOverrides mathOverrides,
		String[] changingMathOverridesNames,
		Constant[] newMathOverridesValues) {
			

	if(pasteDetails.length != changingMathOverridesNames.length || changingMathOverridesNames.length != newMathOverridesValues.length){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingMathOverridesNames.length];
	for(int i=0;i<changingMathOverridesNames.length;i+= 1){
		//bEnableDisplay[i] = !changingParamters[i].getExpression().equals(newParameterExpression[i]);
		bEnableDisplay[i] = !Compare.isEqualOrNull(mathOverrides.getActualExpression(changingMathOverridesNames[i],0),newMathOverridesValues[i].getExpression());
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		PopupGenerator.showInfoDialog(requester, "All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(requester, pasteDetails,bEnableDisplay);
	if(bChoices != null){
		StringBuffer statusMessages = new StringBuffer();
		boolean bFailure = false;
		for(int i=0;i<changingMathOverridesNames.length;i+= 1){
			try{
				if(bChoices[i]){
					mathOverrides.putConstant(newMathOverridesValues[i]);
				}
				statusMessages.append("(OK) "+pasteDetails+"\n");
			}catch(Exception e){
				bFailure = true;
				statusMessages.append("(Failed) "+pasteDetails+" "+e.getMessage()+" "+e.getClass().getName()+"\n");
			}
		}
		if(bFailure){
			PopupGenerator.showErrorDialog(requester, "Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (5/15/2006 12:58:47 PM)
 * @return java.lang.String
 * @param s1 java.lang.String
 * @param s2 java.lang.String
 * @param s3 java.lang.String
 */
public static String formatPasteList(String s1, String s2, String s3,String s4) {
	return 
		BeanUtils.forceStringSize(s1,25," ",false)+" "+
		BeanUtils.forceStringSize(s2,25," ",false)+" "+
		BeanUtils.forceStringSize("'"+s3+"'",25," ",true)+
		" -> "+
		"'"+s4+"'";
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 4:35:20 PM)
 */
public static boolean isSCSRoleForDimension(int scsRole,int dimension){
	
	if(scsRole == SpeciesContextSpec.ROLE_InitialConcentration){
		return true;
	}
	if(scsRole == SpeciesContextSpec.ROLE_DiffusionRate && dimension > 0){
		return true;
	}
	if((scsRole == SpeciesContextSpec.ROLE_BoundaryValueXm || scsRole == SpeciesContextSpec.ROLE_BoundaryValueXp) && dimension > 0){
		return true;
	}
	if((scsRole == SpeciesContextSpec.ROLE_BoundaryValueYm || scsRole == SpeciesContextSpec.ROLE_BoundaryValueYp) && dimension > 1){
		return true;
	}
	if((scsRole == SpeciesContextSpec.ROLE_BoundaryValueZm || scsRole == SpeciesContextSpec.ROLE_BoundaryValueZp) && dimension > 2){
		return true;
	}

	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 9:18:01 AM)
 * @return boolean[]
 */
private static boolean[] showChoices(Component requester, String[] choiceDescriptions,boolean[] bEnableDisplay) {
	javax.swing.JCheckBox[] jCheckBoxArr = new javax.swing.JCheckBox[choiceDescriptions.length];
	javax.swing.JPanel jp = new javax.swing.JPanel();
	java.awt.Font f = new java.awt.Font ("Monospaced",java.awt.Font.PLAIN,12);
	jp.setLayout(new javax.swing.BoxLayout(jp,javax.swing.BoxLayout.Y_AXIS));
	
	final java.util.Vector<JCheckBox> jcbHolder = new java.util.Vector<JCheckBox>();
	if(choiceDescriptions.length > 1){
		javax.swing.JButton selectAllJButton = new javax.swing.JButton("Select All");
		selectAllJButton.addActionListener(
			new java.awt.event.ActionListener(){
				public void actionPerformed(java.awt.event.ActionEvent e){
					for(int i=0;i<jcbHolder.size();i+= 1){
						javax.swing.JCheckBox jcb = jcbHolder.elementAt(i);
						jcb.setSelected(true);
					}
				}
			}
		);
		jp.add(selectAllJButton);
	}
	for(int i=0;i<choiceDescriptions.length;i+= 1){
		javax.swing.JCheckBox jcb = new javax.swing.JCheckBox(choiceDescriptions[i],false);
		jCheckBoxArr[i] = jcb;
		jcb.setVisible(bEnableDisplay[i]);
		jcb.setFont(f);
		jp.add(jcb);
		jcbHolder.add(jcb);
	}

	javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(jp);
	jsp.setPreferredSize(new java.awt.Dimension(700,400));
	int result = PopupGenerator.showComponentOKCancelDialog(requester,jsp,"Choose Parameters to Paste");
	if(result == javax.swing.JOptionPane.OK_OPTION){
		boolean[] bChoices = new boolean[choiceDescriptions.length];
		for(int i=0;i<jCheckBoxArr.length;i+= 1){
			bChoices[i] = jCheckBoxArr[i].isSelected();
		}
		return bChoices;
	}else{
		return null;
	}
}
}
