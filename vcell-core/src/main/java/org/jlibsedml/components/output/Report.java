package org.jlibsedml.components.output;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.listOfConstructs.ListOfDataSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the SED-ML 'Report' element for describing textual output of
 * a simulation.
 *
 * @author radams
 *
 */
public final class Report extends Output {

    private ListOfDataSets listOfDataSets;

    /**
     *
     * @param id   A unique <code>String</code> identifier for this object.
     * @param name An optional <code>String</code> name for this object.
     */
    public Report(SId id, String name) {
        super(id, name);
        this.listOfDataSets = new ListOfDataSets();
    }

    public Report clone() throws CloneNotSupportedException {
        Report clone = (Report) super.clone();
        clone.listOfDataSets =  this.listOfDataSets.clone();
        return clone;
    }

    @Override
    public String getElementName() {
        return SedMLTags.OUTPUT_REPORT;
    }

    /**
     * Getter for a read-only list of {@link DataSet} objects contained in this report.
     *
     * @return non-null but possibly empty List<DataSet> .
     */
    public ListOfDataSets getListOfDataSets() {
        return this.listOfDataSets;
    }

    /**
     * Getter for a read-only list of {@link DataSet} objects contained in this report.
     *
     * @return non-null but possibly empty List<DataSet> .
     */
    public List<DataSet> getDataSets() {
        return this.listOfDataSets.getContents();
    }

    /**
     * Adds a {@link DataSet} to this object's list of DataSets, if not already present.
     *
     * @param dataSet A non-null {@link DataSet} element
     */
    public void addDataSet(DataSet dataSet) {
        this.listOfDataSets.addContent(dataSet);
    }

    /**
     * Removes a {@link DataSet} from this object's list of DataSets.
     *
     * @param dataSet A non-null {@link DataSet} element
     */
    public void removeDataSet(DataSet dataSet) {
        this.listOfDataSets.removeContent(dataSet);
    }

    /**
     * Gets the type of this output.
     *
     * @return SEDMLTags.REPORT_KIND
     */
    public String getKind() {
        return SedMLTags.REPORT_KIND;
    }

    @Override
    public Set<SId> getAllDataGeneratorReferences() {
        return this.listOfDataSets.getContents().stream().map(DataSet::getDataReference).collect(Collectors.toSet());
    }

    @Override
    public SedBase searchFor(SId idOfElement){
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        for (DataSet var : this.getDataSets()) {
            elementFound = var.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        return elementFound;
    }

    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     *
     * @return the parameters and their values, listed in string form
     */
    @Override
    public String parametersToString() {
        // SEE ORIGINAL PARENT!!
        List<String> params = new ArrayList<>(), dataSetParams = new ArrayList<>();
        for (DataSet dataSet : this.getDataSets())
            dataSetParams.add(dataSet.getId() != null ? dataSet.getIdAsString() : '{' + dataSet.parametersToString() + '}');
        params.add(String.format("dataSets=[%s]", String.join(",", dataSetParams)));
        return super.parametersToString() + ", " + String.join(", ", params);
    }
}
