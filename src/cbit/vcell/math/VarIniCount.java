package cbit.vcell.math;
import cbit.vcell.parser.Expression;
/**
 * extends VarIniCondition, to distinguish iniCount from iniConcentration 
 * @author: Tracy LI
 */
public class VarIniCount extends VarIniCondition
{

public VarIniCount(Variable argVar, Expression argIniVal) {
		super(argVar, argIniVal);
		// TODO Auto-generated constructor stub
	}

/**
 * Insert the method's description here.
 * Creation date: (7/6/2006 5:42:05 PM)
 * @return java.lang.String
 */
public String getVCML() 
{
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCML.VarIniCount+"\t"+getVar().getName()+"\t"+getIniVal().infix()+";\n");
	return buffer.toString();
}

}