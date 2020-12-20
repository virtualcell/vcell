package org.jlibsedml;
/**
 * An ALgorithm Parameter for a specific simulation algorithm.
 * <br/>
 * This class does not currently verify the validity of a parameter for the
 *  given algorithm. 
 */
public class AlgorithmParameter {

   
    private String kisaoID;
    private String value;
    
    public AlgorithmParameter(String kisaoID, String value) {
        super();
        Assert.checkNoNullArgs(kisaoID);
        Assert.stringsNotEmpty(kisaoID);
        this.kisaoID = kisaoID;
        this.setValue(value);
    }
    
    public void setKisaoID(String kisaoID) {
        this.kisaoID = kisaoID;
    }
    public String getKisaoID() {
        return kisaoID;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public String toString() {
        String s = "AlgorithmParameter [";
        s += "kisaoID=" + kisaoID; 
        s += " value=" + value; 
        s += "]";
        return s;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((kisaoID == null) ? 0 : kisaoID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AlgorithmParameter other = (AlgorithmParameter) obj;
        if (kisaoID == null) {
            if (other.kisaoID != null)
                return false;
        } else if (!kisaoID.equals(other.kisaoID))
            return false;
        return true;
    }
}
