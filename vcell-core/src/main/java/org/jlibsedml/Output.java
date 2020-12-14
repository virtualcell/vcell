package org.jlibsedml;

import java.util.List;

/**
 * Base class for any kind of SED-ML output - e.g., a plot or report.
 * @author anu/radams
 *
 */
public abstract class Output extends AbstractIdentifiableElement{
	
    public  boolean accept(SEDMLVisitor visitor){
        if(visitor.visit(this)){
            if(isPlot2d()){
               for (Curve c: ((Plot2D)this).getListOfCurves()) {
                   if(! c.accept(visitor)){
                       return false;
                   }
               }
               return true;
            }
            else  if(isPlot3d()){
                for (Surface sf: ((Plot3D)this).getListOfSurfaces()) {
                    if(! sf.accept(visitor)){
                        return false;
                    }
                }
                return true;
             }
            else  if(isReport()){
                for (DataSet sds: ((Report)this).getListOfDataSets()) {
                    if(! sds.accept(visitor)){
                        return false;
                    }
                }
                return true;
             }else {
                 return false;
             }
        }else {
            return false;
        }
    }

	/**
	 * 
	 * @param id - non null or non-empty String.
	 * @param name - optional, can be null
	 * @throws IllegalArgumentException if <code>id</code> is <code>null</code>.
	 */
	public Output(String id, String name) {
		super(id,name);
		
	}

   /**
    * Gets the type of this output (Plot2D, Plot3D, Report)
    * @return A <code>non-null String</code>.
    */
   public abstract String getKind();
   
   /**
    * Boolean test for whether this output is a Plot2d description.
    * @return <code>true</code> if this is a Plot2d description, <code>false</code> otherwise.
    */
   public boolean isPlot2d(){
	   return getKind().equals(SEDMLTags.PLOT2D_KIND);
   }
   
   /**
    * Boolean test for whether this output is a Plot3d description.
    * @return <code>true</code> if this is a Plot3d description, <code>false</code> otherwise.
    */
   public boolean isPlot3d(){
	   return getKind().equals(SEDMLTags.PLOT3D_KIND);
   }
   
   
   /**
    * Boolean test for whether this output is a report description.
    * @return <code>true</code> if this is a report description, <code>false</code> otherwise.
    */
   public boolean isReport(){
	   return getKind().equals(SEDMLTags.REPORT_KIND);
   }
   
   /**
    * Gets a {@link List} of all {@link DataGenerator} identifiers used in this output.<br/>
    * This list will contain only unique entries; the same {@link DataGenerator} id will not appear
    * twice in this output.
    * @return A possibly empty but non-null {@link List} of {@link DataGenerator} id values.
    */
   public abstract List<String> getAllDataGeneratorReferences ();
   
   /**
    * Calculates and returns a non-redundant <code>List</code> of data generator references listed as being independent variables on the output.
    *  (i.e., the xDataReference). For {@link Report}s, (with no concept of independent/dependent variables), an empty list is returned.
    * @return A non-null but possibly empty <code>List</code> of {@link DataGenerator} references.
    */
   public abstract List<String> getAllIndependentDataGeneratorReferences();
   
	   
   
}
