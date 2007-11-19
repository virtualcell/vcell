package cbit.vcell.model.gui;

import java.beans.PropertyChangeEvent;

import javax.swing.table.AbstractTableModel;

import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.parser.ScopedExpression;


/**
 * This table model is used to display the reactions that are possible to 
 * be transformed to Mass Actions. All the original rates are listed at right
 * columns and corresponding forward rates and reverse rates are listed at
 * left columns if the reactions can be transformed properly. All the table
 * cells are not editable. This table Model is called by TransformMassActionPanel. 
 * @author Tracy LI 
 */
public class TransformMassActionTableModel extends AbstractTableModel implements java.beans.PropertyChangeListener 
{
	
	private final int NUM_COLUMNS = 8;
	public final static int COLUMN_REACTION = 0;
	public final static int COLUMN_REACTIONTYPE = 1;
	public final static int COLUMN_KINETICS = 2;
	public final static int COLUMN_RATE = 3;
	public final static int COLUMN_TRANSFORM = 4;
	public final static int COLUMN_FORWARDRATE = 5;
	public final static int COLUMN_REVERSERATE = 6;
	public final static int COLUMN_REMARK = 7;
	
	public final String LABEL_REACTION = "Reaction";
	public final String LABEL_REACTIONTYPE = "Reaction Type";
	public final String LABEL_KINETICS = "Orgi. Kinetics";
	public final String LABEL_RATE = "Reaction rate";
	public final String LABEL_TRANSFORM = "Transform";
	public final String LABEL_FORWARDRATE = "Forward Rate";
	public final String LABEL_REVERSERATE = "Reverse Rate";
	public final String LABEL_REMARK = "Remark";
	
	private final String LABELS[] = {LABEL_REACTION, LABEL_REACTIONTYPE, LABEL_KINETICS, LABEL_RATE, LABEL_TRANSFORM, LABEL_FORWARDRATE, LABEL_REVERSERATE, LABEL_REMARK};
	private TransformMassActions transMAs = null;
	private Model fieldModel = null;
	private boolean[] isSelected = null; // to store values for checkboxes.
	protected transient java.beans.PropertyChangeSupport propertyChange;
	
	//Constructor of transform mass action table model
	public TransformMassActionTableModel() {
		super();
		addPropertyChangeListener(this);
	}
	
	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	
	/**
	 * Accessor for the propertyChange field.
	 */
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	
	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	/**
	 * Return the model value.
	 * @return the model Model
	 */
	public Model getModel() {
		return fieldModel;
	}
	
	/**
	 * set model value
	 * @param model, the new fieldModel value
	 */
	public void setModel(Model model)
	{
		Model oldValue = fieldModel;
		fieldModel = model;
		firePropertyChange("model", oldValue, fieldModel);
	}

	
	public TransformMassActions getTransformMassActions() {
		return transMAs;
	}
	
	/**
	 * Return the column class
	 * @return java.lang.Class
	 * @param column int
	 */
	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_REACTION:{
				return String.class;
			}
			case COLUMN_REACTIONTYPE:{
				return String.class;
			}
			case COLUMN_KINETICS:{
				return String.class;
			}
			case COLUMN_RATE:{
				return ScopedExpression.class;
			}
			case COLUMN_TRANSFORM:{
				return Boolean.class;
			}
			case COLUMN_FORWARDRATE:{
				return ScopedExpression.class;
			}
			case COLUMN_REVERSERATE:{
				return ScopedExpression.class;
			}
			case COLUMN_REMARK:{
				return String.class;
			}
			default:{
				return Object.class;
			}
		}
	}

	/**
	 * get number of columns.
	 * @return int	 
	 */
	public int getColumnCount() {
		return NUM_COLUMNS;
	}

	/**
	 * get column name according to the column index
	 * @return String
	 * @param column int
	 */
	public String getColumnName(int column) {
		if (column<0 || column>=NUM_COLUMNS){
			throw new RuntimeException("TransformMassActionTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
		}
		return LABELS[column];
	}

	/**
	 * get number of rows.
	 * @return int
	 */
	public int getRowCount() {
		if(getModel()!= null && getModel().getReactionSteps() != null)
		{
			return getModel().getReactionSteps().length;
		}
		else 
		{
			return 0;
		}
	}//end of method getRowCount()

	/**
	 * display data in table
	 */
	public Object getValueAt(int row, int col) {
		if (row<0 || row>=getRowCount()){
			throw new RuntimeException("TransformMassActionTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		if (col<0 || col>=NUM_COLUMNS){
			throw new RuntimeException("TransfromMassActionTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
		}
		
		Kinetics orgiKinetics = getModel().getReactionSteps()[row].getKinetics();
		Kinetics transKinetics = transMAs.getTransformedReactionSteps()[row].getTransformedReaction().getKinetics();
		switch (col){
			case COLUMN_REACTION:
			{
				return getModel().getReactionSteps()[row].getName();
			}
			case COLUMN_REACTIONTYPE:
			{
				if(getModel().getReactionSteps()[row] instanceof SimpleReaction)
				{
					return "reaction";
				}
				else
				{
					return "flux";
				}
			}
			case COLUMN_KINETICS:
			{
				if(orgiKinetics != null)
				{
					return orgiKinetics.getKineticsDescription().getDescription();
				}
				return null;
			}
			case COLUMN_RATE:
			{
				if(orgiKinetics != null)
				{
					Kinetics.KineticsParameter ratePara = orgiKinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate);
					if(ratePara != null && ratePara.getExpression() !=null)
					{
						return new ScopedExpression(ratePara.getExpression(),ratePara.getNameScope(),false);
					}
				}
				return null;
			}
			case COLUMN_TRANSFORM:
			{
				//we don't tick the checkbox if original reaction is already Mass Action
				//For flux, we don't tick the checkbox if the flux can be transfomed to Mass Action. It will actually be done in stoch math mapping
				//so the checkbox will not be ticked for flux regardless it can be transformed or not. 
				return getIsSelected(row);
			}
			case COLUMN_FORWARDRATE:
			{
				if(transKinetics != null)
				{
					if(transKinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
					{
						return new ScopedExpression(transKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).getExpression(), transKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).getNameScope(),false);
					}
				}
				return null;
			}
			case COLUMN_REVERSERATE:
			{
				if(transKinetics != null)
				{
					if(transKinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
					{
						return new ScopedExpression(transKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getExpression(), transKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getNameScope(),false);
					}
				}
				return null;
			}
			case COLUMN_REMARK:
			{
				if(transMAs.getTransformedReactionSteps()[row] != null)
				{
					return transMAs.getTransformedReactionSteps()[row].getTransformRemark();
				}
				return null;
    		}
			default:
			{
				return null;
			}
		}
	}// end of method getValueAt()
	
	/**
	 * All table cells are not editable in this table
	 * @return boolean
	 * @param rowIndex int
	 * @param columnIndex int
	 */
	public boolean isCellEditable(int row, int column) {
		if(column == COLUMN_TRANSFORM && transMAs.getTransformedReactionSteps()[row].getTransformType() == TransformMassActions.TransformedReaction.TRANSFORMED )
		{
			return true;
		}
		return false;
	}// end of method isCellEditable
	
	public void setValueAt(Object aValue, int row, int col)
	{
		if (row<0 || row>=getRowCount()){
			throw new RuntimeException("StructureMappingTableModel.setValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		if (col<0 || col>=NUM_COLUMNS){
			throw new RuntimeException("StructureMappingTableModel.setValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
		}
		switch (col){
			case COLUMN_TRANSFORM:{
				if(aValue != null)
				{
					setIsSelected(row, ((Boolean)aValue).booleanValue());
				}
				break;
			}
		}
	}
	
	public void saveTransformedReactions() throws Exception
	{
		boolean[] isTransformable = getTransformMassActions().getIsTransformable();
		TransformMassActions.TransformedReaction[] trs = getTransformMassActions().getTransformedReactionSteps();
		String okTransReacNames = ""; // names of those who can be transformed and will be transformed
		String noTransReacNames = ""; // names of those who can be transformed and will not be transformed
		String errReacNames = ""; // names of those who can not be transformed
		for(int i=0; i < isTransformable.length; i++)
		{
			if(trs[i].getTransformType() == TransformMassActions.TransformedReaction.TRANSFORMED && getIsSelected(i) )
			{
				okTransReacNames = okTransReacNames + trs[i].getTransformedReaction().getName()+ ",";
			}
			else if(trs[i].getTransformType() == TransformMassActions.TransformedReaction.TRANSFORMED &&  !getIsSelected(i))
			{
				noTransReacNames = noTransReacNames + trs[i].getTransformedReaction().getName()+ ",";
			}
			else if(!isTransformable[i])
			{
				errReacNames = errReacNames + trs[i].getTransformedReaction().getName()+ ",";
			}
		}
		//set transformed Mass Action kinetics to model
		ReactionStep[] reacSteps = new ReactionStep[trs.length];
		for(int i=0; i< trs.length; i++)
		{
			if(getIsSelected(i))
			{
				if(getModel().getReactionSteps()[i] instanceof SimpleReaction)// for simple reaction, we replace the kinetics
				{
					reacSteps[i] = getModel().getReactionSteps()[i];
					reacSteps[i].setKinetics(trs[i].getTransformedReaction().getKinetics());
				}
				else // for flux, we set the flux reaction back, coz we will parse it again in stoch math mapping.
				{
					reacSteps[i] = getModel().getReactionSteps()[i];
				}
			}
			else
			{
				reacSteps[i] = getModel().getReactionSteps()[i];
			}
		}
		getModel().setReactionSteps(reacSteps);
		String msg = "";
		if(!okTransReacNames.equals(""))
		{
			msg = msg + okTransReacNames + " have been transformed.\n";
		}
		if(!noTransReacNames.equals(""))
		{
			msg = msg + noTransReacNames + " are able to be transformed, but were not tranformed.\n";
		}
		if(!errReacNames.equals(""))
		{
			msg = msg + errReacNames + " are NOT able to be transformed.\n";
		}
		// message to be displayed in popupdialog of DocumentWindow
		if(!msg.equals(""))
		{
			throw new Exception(msg);
		}
	}//end of method saveTransformedReactions()

	public boolean getIsSelected(int index) {
		return isSelected[index];
	}

	public void setIsSelected(int index, boolean isSelected) {
		this.isSelected[index] = isSelected;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == this && evt.getPropertyName().equals("model"))
		{
			transMAs = new TransformMassActions();
			try
			{
				transMAs.transformReactions(getModel().getReactionSteps());
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			//initial values for checkbox
			isSelected = new boolean[getModel().getReactionSteps().length];
			for(int i=0; i<getModel().getReactionSteps().length; i++)
			{
				if(getModel().getReactionSteps()[i] instanceof SimpleReaction)
				{
					if(!getModel().getReactionSteps()[i].getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction) && transMAs.getIsTransformable()[i])
					{
						isSelected[i] = true;
					}
					else
					{
						isSelected[i] = false;
					}
				}
			}
			//setData(getUnsortedParameters());
			fireTableDataChanged();
		}
	}

	
}	
