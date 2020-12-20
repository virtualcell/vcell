package org.jlibsedml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Encapsulates the Plot2d Sed-ML element.
 * @author anu/radams
 *
 */
public  class Plot2D extends Output {

	@Override
	public String toString() {
		return "Plot2D [listOfCurves=" + listOfCurves + ", name=" + getName() + "]";
	}

	private ArrayList<Curve> listOfCurves= new ArrayList<Curve>();


	/**
	 * 
	 * @param id A unique id for this element in the document.
	 * @param name An optional name for this element.
	 */
	   public Plot2D(String id, String name) {
		   super(id, name);
	   }
	   
	   /**
	    * Gets a read-only list of Curves contained in this element.
	    * @return A possibly empty but non-null {@link List} of {@link Curve} elements.
	    */
	   public List<Curve> getListOfCurves() {
		   return Collections.unmodifiableList(listOfCurves);
	   }
   
	   /**
	    * Gets the type of this output.
	    * @return SEDMLTags.PLOT2D_KIND
	    */
	   public String getKind() {
		   return SEDMLTags.PLOT2D_KIND;
	   }
	   
		/**
		 * Adds a {@link Curve} to this object's list of Curves, if not already present.
		 * @param curve A non-null {@link Curve} element
		 * @return <code>true</code> if curve added, <code>false </code> otherwise.
		 */
		public boolean addCurve(Curve curve ){
			if(!listOfCurves.contains(curve)) {
				return listOfCurves.add(curve);
			} else {
			    // TODO: add to error list
			}
			return false;
		}
		
		/**
		 * Removes a {@link Curve} from this object's list of Curves, if not already present.
		 * @param curve A non-null {@link Curve} element
		 * @return <code>true</code> if curve added, <code>false </code> otherwise.
		 */
		public boolean removeCurve(Curve curve ){
			
				return listOfCurves.remove(curve);
		
		}
		
		/**
		 * Returns a sublist of the {@link List} of Curves for this plot, that  use the output of the specified {@link DataGenerator}
		 *  for the X-axis.
		 * @param dg A non-null {@link DataGenerator}
		 * @return A possibly empty but non-null {@link List} of {@link Curve} elements.
		 */
		public List<Curve> getCurvesUsingDataGeneratorAsXAxis(DataGenerator dg){
			List<Curve> rc = new ArrayList<Curve>();
			for (Curve cv: listOfCurves){
				if(cv.getXDataReference().equals(dg.getId())){
					rc.add(cv);
				}
			}
			return rc;
		}
		
		
		/**
		 * Returns a sublist of the {@link List} of Curves for this plot, that  use the output of the specified {@link DataGenerator}
		 *  for the Y-axis.
		 * @param dg A non-null {@link DataGenerator}
		 * @return A possibly empty but non-null {@link List} of {@link Curve} elements.
		 */
		public List<Curve> getCurvesUsingDataGeneratorAsYAxis(DataGenerator dg){
			List<Curve> rc = new ArrayList<Curve>();
			for (Curve cv: listOfCurves){
				if(cv.getYDataReference().equals(dg.getId())){
					rc.add(cv);
				}
			}
			return rc;
		}

		@Override
		public List<String> getAllDataGeneratorReferences() {
			Set<String> rc = new TreeSet<String>();
			for (Curve c: listOfCurves){
				rc.add(c.getXDataReference());
				rc.add(c.getYDataReference());
			}
			List<String> rc2 = new ArrayList<String>();
			for (String id:rc){
				rc2.add(id);
			}
			return rc2;
	
		}

		@Override
		public List<String> getAllIndependentDataGeneratorReferences() {
			Set<String> rc = new TreeSet<String>();
			for (Curve c: listOfCurves){
				rc.add(c.getXDataReference());
			}
			List<String> rc2 = new ArrayList<String>();
			for (String id:rc){
				rc2.add(id);
			}
			return rc2;
		}

		@Override
		public String getElementName() {
			return SEDMLTags.OUTPUT_P2D;
		}
	   
}
