package org.vcell.cli.run.hdf5;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class
 */
public class Hdf5DataSourceNonspatial extends Hdf5DataSource implements Iterable<List<Double>>{

    /**
     * List of all data contained within a job relevant to HDF5 formatted files
     */
    private int numDimensions;
    private Hdf5DataFragment jobDataHead, jobDataTail;

    /*
     * Creates an empty data source
     * 
     * There are no paramaterized constructors, as they may result in "heap pollution"
     */
    public Hdf5DataSourceNonspatial(){
        this.numDimensions = 0;
        this.jobDataHead =  null;
        this.jobDataTail = jobDataHead;
    }

    /**
     * 
     * @return 
     */
    public void add(List<Double> data){
        this.jobDataTail.nextDimension = new Hdf5DataFragment(data);
        this.numDimensions += 1;
    }

    public int getNumberOfDimensions(){
        return this.numDimensions;
    }

    /**
     * 
     * @return
     */
    public boolean hasNext(){
        return this.jobDataHead == null ? false : true;
    }

    @Override
    public Iterator<List<Double>> iterator() {
        return new DataSourceIterator(this.jobDataHead);
    }

    /**
     * Struct-Subclass for holding job data
     */
    private class Hdf5DataFragment {
        public Hdf5DataFragment(List<Double> data){
            this.varData = data;
        }
        
        public Hdf5DataFragment nextDimension = null;
        public List<Double> varData = new LinkedList<>();
    }

    private class DataSourceIterator implements Iterator<List<Double>>{

        private Hdf5DataFragment head;

        /**
         * Constructor for the iterator
         * @param chainStart
         */
        public DataSourceIterator(Hdf5DataFragment chainStart){
            this.head = chainStart;
        }

        @Override
        public boolean hasNext() {
            return head != null && head.nextDimension != null;
        }

        @Override
        public List<Double> next() {
            List<Double> result = head == null ? null : head.varData;
            if (head != null) head = head.nextDimension;
            return result;
        }
    }
}
