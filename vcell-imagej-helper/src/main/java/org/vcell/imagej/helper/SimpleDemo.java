package org.vcell.imagej.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

public class SimpleDemo {
	private static SimpleDateFormat EASY_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		try {
			VCellHelper vh = new VCellHelper();
			
			//
			// SEARCH for a public simulation (in a BioModel), get information about the simulation and download a portion of the data
			//
			// Set the date range for models we are interested in
			VCellHelper.VCellModelVersionTimeRange vcellModelVersionTimeRange = new VCellHelper.VCellModelVersionTimeRange(EASY_DATE.parse("2016-01-01 00:00:00"), EASY_DATE.parse("2017-01-01 00:00:00"));
			// Set the ModelName, ApplicationName and SimulationName for models we are interested in
			VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm, "tutorial60", "Rule-based_egfr_tutorial", "*stoch", "Simulation1",null,null);
			// Use VCellHelper to contact VCell client and return preliminary info
			ArrayList<VCellModelSearchResults> vcellModelSearchResults = vh.getSearchedModelSimCacheKey(true, search,vcellModelVersionTimeRange);
			//  Our search was constructed such that only 1 simulation should be in the search results
			if(vcellModelSearchResults.size() != 1) {
				throw new Exception("Expecting exactly 1 model from search results");
			}
			System.out.println("Model name='"+vcellModelSearchResults.get(0).getModelName()+"'");
			System.out.println("Application name='"+vcellModelSearchResults.get(0).getApplicationName()+"'");
			System.out.println("Simulation name='"+vcellModelSearchResults.get(0).getSimulationName()+"'"+" geomDims="+vcellModelSearchResults.get(0).getGeometryDimension()+" mathType="+vcellModelSearchResults.get(0).getMathType());
			// Get the cacheKey that identifies the particular simulation result we are interested in
			String simulationInfoCacheKey = vcellModelSearchResults.get(0).getCacheKey();
			
			// Get more specific information about our simulation using the cacheKey (times, parameter scan count, variable names,...)
			// (the cacheKey was stored in the running VCell client during the previous search and is used to quickly lookup information)
			VCellHelper.IJVarInfos ijVarInfos = vh.getSimulationInfo(simulationInfoCacheKey);
			// Get the actual data (our simulation is non-spatial ODE)
			VCellHelper.TimePointData timePointData = vh.getTimePointData(simulationInfoCacheKey, "Dimers", null,0);
			// Check the length of the data == length of timepoints (should be true for non-spatial data)
			if(timePointData.getTimePointData().length != ijVarInfos.getTimes().length) {
				throw new Exception("Expecting datalength for variable "+timePointData.getTimePointData().length+" to match number of timepoints "+ijVarInfos.getTimes().length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
