package cbit.vcell.desktop;
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
public static void chooseApplyPaste(
		String[] pasteDetails,
		cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter[] changingParamters,
		cbit.vcell.parser.Expression[] newParameterExpression) {
			

	if(pasteDetails.length != changingParamters.length || changingParamters.length != newParameterExpression.length){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingParamters.length];
	for(int i=0;i<changingParamters.length;i+= 1){
		//bEnableDisplay[i] = !changingParamters[i].getExpression().equals(newParameterExpression[i]);
		bEnableDisplay[i] = !cbit.util.Compare.isEqualOrNull(changingParamters[i].getExpression(),newParameterExpression[i]);
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		cbit.vcell.client.PopupGenerator.showInfoDialog("All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(pasteDetails,bEnableDisplay);
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
			cbit.vcell.client.PopupGenerator.showErrorDialog("Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste(
		String[] pasteDetails,
		cbit.vcell.model.Parameter[] changingParamters,
		cbit.vcell.parser.Expression[] newParameterExpression) {
			

	if(pasteDetails.length != changingParamters.length || changingParamters.length != newParameterExpression.length){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingParamters.length];
	for(int i=0;i<changingParamters.length;i+= 1){
		//bEnableDisplay[i] = !changingParamters[i].getExpression().equals(newParameterExpression[i]);
		bEnableDisplay[i] = !cbit.util.Compare.isEqualOrNull(changingParamters[i].getExpression(),newParameterExpression[i]);
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		cbit.vcell.client.PopupGenerator.showInfoDialog("All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(pasteDetails,bEnableDisplay);
	if(bChoices != null){
		StringBuffer statusMessages = new StringBuffer();
		boolean bFailure = false;
		for(int i=0;i<changingParamters.length;i+= 1){
			try{
				if(bChoices[i]){
					if (changingParamters[i] instanceof cbit.vcell.model.Kinetics.KineticsParameter){
						cbit.vcell.model.Kinetics kinetics = ((cbit.vcell.model.ReactionStep) changingParamters[i].getNameScope().getScopedSymbolTable()).getKinetics();
						kinetics.setParameterValue((cbit.vcell.model.Kinetics.KineticsParameter)changingParamters[i],newParameterExpression[i]);
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
			cbit.vcell.client.PopupGenerator.showErrorDialog("Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste(
		String[] pasteDetails,
		cbit.vcell.modelopt.ParameterMappingSpec[] changingParamters,
		cbit.vcell.parser.Expression[] newParameterExpression) {
			

	if(pasteDetails.length != changingParamters.length || changingParamters.length != newParameterExpression.length){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingParamters.length];
	for(int i=0;i<changingParamters.length;i+= 1){
		//bEnableDisplay[i] = !changingParamters[i].getExpression().equals(newParameterExpression[i]);
		bEnableDisplay[i] = !cbit.util.Compare.isEqualOrNull(new cbit.vcell.parser.Expression(changingParamters[i].getCurrent()),newParameterExpression[i]);
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		cbit.vcell.client.PopupGenerator.showInfoDialog("All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(pasteDetails,bEnableDisplay);
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
			cbit.vcell.client.PopupGenerator.showErrorDialog("Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste(
		String[] pasteDetails,
		cbit.vcell.solver.MathOverrides mathOverrides,
		String[] changingMathOverridesNames,
		java.util.Vector newMathOverridesValuesV) {
			

	if(pasteDetails.length != changingMathOverridesNames.length || changingMathOverridesNames.length != newMathOverridesValuesV.size()){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingMathOverridesNames.length];
	for(int i=0;i<changingMathOverridesNames.length;i+= 1){
		if(newMathOverridesValuesV.elementAt(i) instanceof cbit.vcell.parser.Expression){
			bEnableDisplay[i] =
				!cbit.util.Compare.isEqualOrNull(
					mathOverrides.getActualExpression(changingMathOverridesNames[i],0),
					//((cbit.vcell.math.Constant)newMathOverridesValuesV.elementAt(i)).getExpression());
					((cbit.vcell.parser.Expression)newMathOverridesValuesV.elementAt(i)));
		}else if(newMathOverridesValuesV.elementAt(i) instanceof cbit.vcell.solver.ConstantArraySpec){
			bEnableDisplay[i] =
				!cbit.util.Compare.isEqualOrNull(
					mathOverrides.getConstantArraySpec(changingMathOverridesNames[i]),
					(cbit.vcell.solver.ConstantArraySpec)newMathOverridesValuesV.elementAt(i));
		}else{
			cbit.vcell.client.PopupGenerator.showErrorDialog("Unexpected MathOverride type="+newMathOverridesValuesV.elementAt(i).getClass().getName()+"\nPaste Failed, nothing changed.");
			return;
		}
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		cbit.vcell.client.PopupGenerator.showInfoDialog("All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(pasteDetails,bEnableDisplay);
	if(bChoices != null){
		StringBuffer statusMessages = new StringBuffer();
		boolean bFailure = false;
		for(int i=0;i<changingMathOverridesNames.length;i+= 1){
			try{
				if(bChoices[i]){
					if(newMathOverridesValuesV.elementAt(i) instanceof cbit.vcell.parser.Expression){
						mathOverrides.putConstant(new cbit.vcell.math.Constant(changingMathOverridesNames[i],(cbit.vcell.parser.Expression)newMathOverridesValuesV.elementAt(i)));
					}else if(newMathOverridesValuesV.elementAt(i) instanceof cbit.vcell.solver.ConstantArraySpec){
						mathOverrides.putConstantArraySpec((cbit.vcell.solver.ConstantArraySpec)newMathOverridesValuesV.elementAt(i));
					}
				}
				statusMessages.append("(OK) "+pasteDetails+"\n");
			}catch(Exception e){
				bFailure = true;
				statusMessages.append("(Failed) "+pasteDetails+" "+e.getMessage()+" "+e.getClass().getName()+"\n");
			}
		}
		if(bFailure){
			cbit.vcell.client.PopupGenerator.showErrorDialog("Paste Results:\n"+statusMessages.toString());
		}
	}
			
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2006 11:05:19 AM)
 */
public static void chooseApplyPaste_NOT_USED(
		String[] pasteDetails,
		cbit.vcell.solver.MathOverrides mathOverrides,
		String[] changingMathOverridesNames,
		cbit.vcell.math.Constant[] newMathOverridesValues) {
			

	if(pasteDetails.length != changingMathOverridesNames.length || changingMathOverridesNames.length != newMathOverridesValues.length){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingMathOverridesNames.length];
	for(int i=0;i<changingMathOverridesNames.length;i+= 1){
		//bEnableDisplay[i] = !changingParamters[i].getExpression().equals(newParameterExpression[i]);
		bEnableDisplay[i] = !cbit.util.Compare.isEqualOrNull(mathOverrides.getActualExpression(changingMathOverridesNames[i],0),newMathOverridesValues[i].getExpression());
		bAtLeatOneDifferent = bAtLeatOneDifferent || bEnableDisplay[i];
	}

	if(!bAtLeatOneDifferent){
		cbit.vcell.client.PopupGenerator.showInfoDialog("All valid paste values are equal to the destination values.\nNo paste needed.");
		return;
	}
	
	boolean[] bChoices = showChoices(pasteDetails,bEnableDisplay);
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
			cbit.vcell.client.PopupGenerator.showErrorDialog("Paste Results:\n"+statusMessages.toString());
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
		cbit.util.BeanUtils.forceStringSize(s1,25," ",false)+" "+
		cbit.util.BeanUtils.forceStringSize(s2,25," ",false)+" "+
		cbit.util.BeanUtils.forceStringSize("'"+s3+"'",25," ",true)+
		" -> "+
		"'"+s4+"'";
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 4:35:20 PM)
 */
public static boolean isSCSRoleForDimension(int scsRole,int dimension){
	
	if(scsRole == cbit.vcell.mapping.SpeciesContextSpec.ROLE_InitialConcentration){
		return true;
	}
	if(scsRole == cbit.vcell.mapping.SpeciesContextSpec.ROLE_DiffusionRate && dimension > 0){
		return true;
	}
	if((scsRole == cbit.vcell.mapping.SpeciesContextSpec.ROLE_BoundaryValueXm || scsRole == cbit.vcell.mapping.SpeciesContextSpec.ROLE_BoundaryValueXp) && dimension > 0){
		return true;
	}
	if((scsRole == cbit.vcell.mapping.SpeciesContextSpec.ROLE_BoundaryValueYm || scsRole == cbit.vcell.mapping.SpeciesContextSpec.ROLE_BoundaryValueYp) && dimension > 1){
		return true;
	}
	if((scsRole == cbit.vcell.mapping.SpeciesContextSpec.ROLE_BoundaryValueZm || scsRole == cbit.vcell.mapping.SpeciesContextSpec.ROLE_BoundaryValueZp) && dimension > 2){
		return true;
	}

	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 9:18:01 AM)
 * @return boolean[]
 */
public static boolean[] showChoices(String[] choiceDescriptions,boolean[] bEnableDisplay) {
	javax.swing.JCheckBox[] jCheckBoxArr = new javax.swing.JCheckBox[choiceDescriptions.length];
	javax.swing.JPanel jp = new javax.swing.JPanel();
	java.awt.Font f = new java.awt.Font ("Monospaced",java.awt.Font.PLAIN,12);
	jp.setLayout(new javax.swing.BoxLayout(jp,javax.swing.BoxLayout.Y_AXIS));
	
	final java.util.Vector jcbHolder = new java.util.Vector();
	if(choiceDescriptions.length > 1){
		javax.swing.JButton selectAllJButton = new javax.swing.JButton("Select All");
		selectAllJButton.addActionListener(
			new java.awt.event.ActionListener(){
				public void actionPerformed(java.awt.event.ActionEvent e){
					for(int i=0;i<jcbHolder.size();i+= 1){
						javax.swing.JCheckBox jcb = (javax.swing.JCheckBox)jcbHolder.elementAt(i);
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
	int result = cbit.vcell.client.PopupGenerator.showComponentOKCancelDialog(null,jsp,"Choose Parameters to Paste");
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