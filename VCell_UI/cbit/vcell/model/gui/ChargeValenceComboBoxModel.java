package cbit.vcell.model.gui;

/**
 * Insert the type's description here.
 * Creation date: (11/18/2002 3:33:14 PM)
 * @author: Jim Schaff
 */
public class ChargeValenceComboBoxModel extends javax.swing.AbstractListModel implements javax.swing.ComboBoxModel, java.beans.PropertyChangeListener {
	private Object[] items = {
		new Integer(-5),
		new Integer(-4),
		new Integer(-3),
		new Integer(-2),
		new Integer(-1),
		new Integer(1),
		new Integer(2),
		new Integer(3),
		new Integer(4),
		new Integer(5)};
	private cbit.vcell.model.Kinetics kinetics = null;
	
/**
 * ChargeValenceComboBoxModel constructor comment.
 */
public ChargeValenceComboBoxModel() {
	super();
}
public Object getElementAt(int i){
	return items[i];
}
/**
 * Insert the method's description here.
 * Creation date: (11/18/2002 3:35:31 PM)
 * @return cbit.vcell.model.Kinetics
 */
public cbit.vcell.model.Kinetics getKinetics() {
	return kinetics;
}
public Object getSelectedItem() {
	if (getKinetics() != null && getKinetics().getReactionStep() != null){
		for (int i=0;i<items.length;i++){
			try {
				if (items[i] instanceof Integer && ((Integer)items[i]).intValue() == getKinetics().getReactionStep().getChargeCarrierValence().getExpression().evaluateConstant()){
					return items[i];
				}
			}catch (cbit.vcell.parser.ExpressionException e){
				e.printStackTrace(System.out);
			}
		}
	}
	return new Integer(1);
}
public int getSize(){
	return items.length;
}
public void propertyChange(java.beans.PropertyChangeEvent evt){
	if (getKinetics() != null && evt.getSource() == getKinetics().getReactionStep() && evt.getPropertyName().equals("chargeCarrierValence")){
		fireContentsChanged(this,0,getSize()-1);
	}
			
}
/**
 * Insert the method's description here.
 * Creation date: (11/18/2002 4:08:57 PM)
 */
public void refresh() {
	fireContentsChanged(this,0,getSize()-1);
}
/**
 * Insert the method's description here.
 * Creation date: (11/18/2002 3:35:31 PM)
 * @param newKinetics cbit.vcell.model.Kinetics
 */
public void setKinetics(cbit.vcell.model.Kinetics newKinetics) {
	if (kinetics!=null && kinetics.getReactionStep()!=null){
		kinetics.getReactionStep().removePropertyChangeListener(this);
	}
	kinetics = newKinetics;
	if (newKinetics!=null && newKinetics.getReactionStep()!=null){
		newKinetics.getReactionStep().addPropertyChangeListener(this);
	}	
	fireContentsChanged(this,0,getSize()-1);
}
public void setSelectedItem(Object selectedItem) {
	if (getKinetics() != null){
		cbit.vcell.model.ReactionStep rs = kinetics.getReactionStep();
		if (rs != null){
			if (selectedItem instanceof Integer){
				try {
					rs.getChargeCarrierValence().setExpression(new cbit.vcell.parser.Expression(((Integer)selectedItem).intValue()));
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
				}
			}
		}
	}
}
}
