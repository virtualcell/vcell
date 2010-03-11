package org.vcell.sybil.models.arq;

/*   InferenceDataSet  --- by Oliver Ruebenacker, UCHC --- December 2007 to October 2009
 *   Creates a dataset from a model, a schema, a query string and a reasoner
 */

import java.util.Iterator;

import org.vcell.sybil.models.reason.Reason.Choice;
import org.vcell.sybil.rdf.schemas.DatasetOntology;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

public class InferenceDataset implements Dataset {

	protected DataSource dataSource;
	
	public InferenceDataset(Choice choice, Model data, Model schema) {
		this(choice.infModel(data, schema), data, schema);
	}

	public InferenceDataset(Model modelDefault, Model data, Model schema) {
		dataSource = DatasetFactory.create();
		dataSource.setDefaultModel(modelDefault);
		dataSource.addNamedModel(DatasetOntology.Data.getURI(), data);
		dataSource.addNamedModel(DatasetOntology.Schema.getURI(), schema);
	}

	public Model data() { return dataSource.getNamedModel(DatasetOntology.Data.getURI()); }
	public Model schema() { return dataSource.getNamedModel(DatasetOntology.Schema.getURI()); }

	public DatasetGraph asDatasetGraph() { return dataSource.asDatasetGraph(); }
	public void close() { dataSource.close(); }
	public boolean containsNamedModel(String uri) { return dataSource.containsNamedModel(uri); }
	public Model getDefaultModel() { return dataSource.getDefaultModel(); }
	public Lock getLock() { return dataSource.getLock(); }
	public Model getNamedModel(String uri) { return dataSource.getNamedModel(uri); }
	public Iterator<String> listNames() { return dataSource.listNames(); }

}
