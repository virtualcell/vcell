package org.vcell.util.gui.exporter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public abstract class SelectorExtensionFilter extends ExtensionFilter {
	
	public enum Selector {
		DEFAULT,
		ANY,
		SPATIAL,
		NONSPATIAL,
		DETERMINISTIC,
		STOCHASTIC,
		ZEROAPP,
		SBML,
	}

	protected final SelectorExtensionFilter.Selector[] selectors;
	
//		FileFilters.register(this);
	
	SelectorExtensionFilter(String arg_extension, String descr, SelectorExtensionFilter.Selector... selectors) {
		super(arg_extension, descr);
		this.selectors = selectors;
		FileFilters.register(this);
	}

	SelectorExtensionFilter(String[] arg_extensions, String descr, SelectorExtensionFilter.Selector... selectors) {
		super(arg_extensions, descr);
		this.selectors = selectors;
		FileFilters.register(this);
	}

	/**
	 * does filter support <em>all</em> selectors in list?
	 * @param selectors
	 * @return true if it does
	 */
	public boolean supports(SelectorExtensionFilter.Selector... selectors) {
		List<SelectorExtensionFilter.Selector> required = Arrays.asList(selectors);
		List<SelectorExtensionFilter.Selector> us = Arrays.asList(this.selectors);
		return us.containsAll(required);
		
	}
	
		//FileUtils.writeStringToFile(exportFile, x);
	public abstract void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext simulationContext) throws Exception; 
		
	
}