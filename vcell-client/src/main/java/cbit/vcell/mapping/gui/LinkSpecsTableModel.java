package cbit.vcell.mapping.gui;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.*;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import org.vcell.model.rbm.*;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.springsalad.NamedColor;

import java.beans.PropertyChangeEvent;
import java.util.*;

public class LinkSpecsTableModel extends VCellSortTableModel<MolecularInternalLinkSpec> implements java.beans.PropertyChangeListener {

    public enum ColumnType {
        COLUMN_LINK("Link"),
        COLUMN_LENGTH("Length");

        public final String label;
        private ColumnType(String label){
            this.label = label;
        }
    }

    ArrayList<ColumnType> columns = new ArrayList<>();
    private SimulationContext fieldSimulationContext = null;
    private SpeciesContextSpec fieldSpeciesContextSpec = null;

    public LinkSpecsTableModel(ScrollTable table) {
        super(table);
        refreshColumns();
    }






    @Override
    public Class<?> getColumnClass(int column) {
        ColumnType columnType = columns.get(column);
        switch (columnType) {
            case COLUMN_LINK:
                return MolecularInternalLinkSpec.class;
            case COLUMN_LENGTH:
                return Expression.class;
            default:
                return Object.class;
        }
    }
    @Override
    public String getColumnName(int columnIndex){
        return columns.get(columnIndex).label;
    }
    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        try {
            if (getSpeciesContextSpec() == null) {
                return null;
            }
            Set<MolecularInternalLinkSpec> ilSet = getSpeciesContextSpec().getInternalLinkSet();
            MolecularInternalLinkSpec mils = getValueAt(row);
            ColumnType columnType = columns.get(col);
            switch (columnType) {
                case COLUMN_LINK:
                    if(mils == null) {
                        return null;
                    }
                    return mils;
                case COLUMN_LENGTH:
                    if(mils == null) {
                        return null;
                    }
                    return mils.getLinkLength();
                default:
                    return null;
            }
        } catch(Exception e) {
            return null;
        }
    }
    @Override
    public void setValueAt(Object aValue, int row, int col) {
        MolecularInternalLinkSpec mils = getValueAt(row);
        ColumnType columnType = columns.get(col);
        switch (columnType) {
            case COLUMN_LINK:

                return;
            case COLUMN_LENGTH:

                return;
            default:
                return;
        }
    }
     @Override
    public boolean isCellEditable(int row, int col) {
        ColumnType columnType = columns.get(col);
        switch (columnType) {
            case COLUMN_LINK:
            case COLUMN_LENGTH:
                return false;
            default:
                return false;
        }
    }

    @Override
    protected Comparator<MolecularInternalLinkSpec> getComparator(int col, boolean ascending) {
        return new Comparator<MolecularInternalLinkSpec>() {
            /**
             * Compares its two arguments for order.  Returns a negative integer,
             * zero, or a positive integer as the first argument is less than, equal
             * to, or greater than the second.<p>
             */
            public int compare(MolecularInternalLinkSpec mils1, MolecularInternalLinkSpec mils2) {

                ColumnType columnType = columns.get(col);
                switch (columnType) {
                    case COLUMN_LINK:
                    case COLUMN_LENGTH:
                   default:
                        return 1;
                }
            }
        };
    }




    public void setSimulationContext(SimulationContext simulationContext) {
        SimulationContext oldValue = fieldSimulationContext;
        int oldColumnCount = getColumnCount();
        if (oldValue != null) {
            oldValue.removePropertyChangeListener(this);
            oldValue.getGeometryContext().removePropertyChangeListener(this);
            updateListenersReactionContext(oldValue.getReactionContext(),true);
        }
        fieldSimulationContext = simulationContext;
        refreshColumns();
        int newColumnCount = getColumnCount();
        if (oldColumnCount != newColumnCount) {
            fireTableStructureChanged();
        }
        if (simulationContext != null) {
            simulationContext.addPropertyChangeListener(this);
            simulationContext.getGeometryContext().addPropertyChangeListener(this);
            updateListenersReactionContext(simulationContext.getReactionContext(),false);

//			autoCompleteSymbolFilter  = simulationContext.getAutoCompleteSymbolFilter();
            refreshData();
        }
    }
    private SimulationContext getSimulationContext() {
        return fieldSimulationContext;
    }

    public void setSpeciesContextSpec(SpeciesContextSpec speciesContextSpec) {
        SpeciesContextSpec oldValue = fieldSpeciesContextSpec;
        int oldColumnCount = getColumnCount();
        if (oldValue != null) {
            oldValue.removePropertyChangeListener(this);
        }
        fieldSpeciesContextSpec = speciesContextSpec;
        refreshColumns();
        int newColumnCount = getColumnCount();
        if (oldColumnCount != newColumnCount) {
            fireTableStructureChanged();
        }
        if (speciesContextSpec != null) {
            speciesContextSpec.addPropertyChangeListener(this);
        }
        refreshData();
    }
    private SpeciesContextSpec getSpeciesContextSpec() {
        return fieldSpeciesContextSpec;
    }

    private void updateListenersReactionContext(ReactionContext reactionContext,boolean bRemove) {

        if(bRemove) {
            reactionContext.removePropertyChangeListener(this);
            SpeciesContextSpec oldSpecs[] = reactionContext.getSpeciesContextSpecs();
            for (int i=0;i<oldSpecs.length;i++) {
                oldSpecs[i].removePropertyChangeListener(this);
                oldSpecs[i].getSpeciesContext().removePropertyChangeListener(this);
                Parameter oldParameters[] = oldSpecs[i].getParameters();
                for (int j = 0; j < oldParameters.length ; j++) {
                    oldParameters[j].removePropertyChangeListener(this);
                }
            }
        } else {
            reactionContext.addPropertyChangeListener(this);
            SpeciesContextSpec newSpecs[] = reactionContext.getSpeciesContextSpecs();
            for (int i=0;i<newSpecs.length;i++) {
                newSpecs[i].addPropertyChangeListener(this);
                newSpecs[i].getSpeciesContext().addPropertyChangeListener(this);
                Parameter newParameters[] = newSpecs[i].getParameters();
                for (int j = 0; j < newParameters.length ; j++) {
                    newParameters[j].addPropertyChangeListener(this);
                }
            }
        }
    }

    private void refreshColumns() {        // called in setSpeciesContextSpec()
        columns.clear();
        columns.addAll(Arrays.asList(ColumnType.values())); // initialize to all columns
        // TODO: may remove some columns ex: columns.remove(ColumnType.COLUMN_STRUCTURE)
    }

    public void refreshData() {        // called in setSimulationContext()
        List<MolecularInternalLinkSpec> molecularInternalLinkSpecList = computeData();
        setData(molecularInternalLinkSpecList);
        GuiUtils.flexResizeTableColumns(ownerTable);
    }
    protected List<MolecularInternalLinkSpec> computeData() {
        ArrayList<MolecularInternalLinkSpec> allParameterList = new ArrayList<MolecularInternalLinkSpec>();
        if(fieldSpeciesContextSpec != null && fieldSpeciesContextSpec.getSpeciesContext() != null) {
            Set<MolecularInternalLinkSpec> ilSet = fieldSpeciesContextSpec.getInternalLinkSet();
            if (ilSet == null || ilSet.isEmpty()) {
                return null;
            }
            for (MolecularInternalLinkSpec mils : ilSet) {
                allParameterList.add(mils);
            }
        } else {
            return null;
        }
        return allParameterList;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof ReactionContext && evt.getPropertyName().equals("speciesContextSpecs")) {
            refreshData();
        }
        if (evt.getSource() instanceof SpeciesContext && evt.getPropertyName().equals("name")) {
            fireTableRowsUpdated(0,getRowCount()-1);
        }

        if (evt.getSource() instanceof SpeciesContextSpec) {
            fireTableRowsUpdated(0,getRowCount()-1);
        }
        if (evt.getSource() instanceof SpeciesContextSpec.SpeciesContextSpecParameter) {    // prolly not needed
            fireTableRowsUpdated(0,getRowCount()-1);
        }
        if (evt.getSource() instanceof GeometryContext) {
            refreshColumns();
            fireTableStructureChanged();
        }

    }


}
