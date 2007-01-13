package cbit.vcell.desktop;

import org.vcell.expression.IExpression;

import cbit.gui.DialogUtils;
import cbit.vcell.modelapp.SpeciesContextSpec;
import cbit.vcell.simulation.ConstantArraySpec;
import cbit.vcell.simulation.MathOverrides;

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
		SpeciesContextSpec.SpeciesContextSpecParameter[] changingParamters,
		IExpression[] newParameterExpression) {
			

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
	
	boolean[] bChoices = DialogUtils.showChoices(pasteDetails,bEnableDisplay,"Choose Parameters to Paste");
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
		IExpression[] newParameterExpression) {
			

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
	
	boolean[] bChoices = DialogUtils.showChoices(pasteDetails,bEnableDisplay,"Choose Parameters to Paste");
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
		MathOverrides mathOverrides,
		String[] changingMathOverridesNames,
		java.util.Vector newMathOverridesValuesV) {
			

	if(pasteDetails.length != changingMathOverridesNames.length || changingMathOverridesNames.length != newMathOverridesValuesV.size()){
		throw new IllegalArgumentException(VCellCopyPasteHelper.class.getName()+".chooseApplyPaste(...) arguments must have unequal lengths");
	}
	//Only present things that will actually change
	boolean bAtLeatOneDifferent = false;
	boolean[] bEnableDisplay = new boolean[changingMathOverridesNames.length];
	for(int i=0;i<changingMathOverridesNames.length;i+= 1){
		if(newMathOverridesValuesV.elementAt(i) instanceof IExpression){
			bEnableDisplay[i] =
				!cbit.util.Compare.isEqualOrNull(
					mathOverrides.getActualExpression(changingMathOverridesNames[i],0),
					//((cbit.vcell.math.Constant)newMathOverridesValuesV.elementAt(i)).getExpression());
					((IExpression)newMathOverridesValuesV.elementAt(i)));
		}else if(newMathOverridesValuesV.elementAt(i) instanceof ConstantArraySpec){
			bEnableDisplay[i] =
				!cbit.util.Compare.isEqualOrNull(
					mathOverrides.getConstantArraySpec(changingMathOverridesNames[i]),
					(ConstantArraySpec)newMathOverridesValuesV.elementAt(i));
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
	
	boolean[] bChoices = DialogUtils.showChoices(pasteDetails,bEnableDisplay,"Choose Parameters to Paste");
	if(bChoices != null){
		StringBuffer statusMessages = new StringBuffer();
		boolean bFailure = false;
		for(int i=0;i<changingMathOverridesNames.length;i+= 1){
			try{
				if(bChoices[i]){
					if(newMathOverridesValuesV.elementAt(i) instanceof IExpression){
						mathOverrides.putConstant(new cbit.vcell.math.Constant(changingMathOverridesNames[i],(IExpression)newMathOverridesValuesV.elementAt(i)));
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
		MathOverrides mathOverrides,
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
	
	boolean[] bChoices = DialogUtils.showChoices(pasteDetails,bEnableDisplay,"Choose Parameters to Paste");
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

}