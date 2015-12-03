package org.vcell.vis.io;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5CompoundDS;
import ncsa.hdf.object.h5.H5ScalarDS;

import org.vcell.vis.core.Vect3D;

public class Hdf5Reader {

	/**
		 * Z = boolean
	[B = byte
	[S = short
	[I = int
	[J = long
	[F = float
	[D = double
	[C = char
	[L = any non-primitives(Object)
		 * @author schaff
		 *
		 */
	public static abstract class DataColumn {
			private String colName;
			public DataColumn(String name){
				this.colName = name;
			}
			public abstract int getNumRows();
			public abstract double getValue(int index);
		}

	public static class IntColumn extends DataColumn {
		int[] data;
		public IntColumn(String name, int[] data){
			super(name);
			this.data = data;
		}
		@Override
		public int getNumRows(){
			return data.length;
		}
		@Override
		public double getValue(int index){
			return data[index];
		}
	}

	public static class LongColumn extends DataColumn {
		long[] data;
		public LongColumn(String name, long[] data){
			super(name);
			this.data = data;
		}
		@Override
		public int getNumRows(){
			return data.length;
		}
		@Override
		public double getValue(int index){
			return data[index];
		}
	}

	public static class DoubleColumn extends DataColumn {
		double[] data;
		public DoubleColumn(String name, double[] data){
			super(name);
			this.data = data;
		}
		@Override
		public int getNumRows(){
			return data.length;
		}
		@Override
		public double getValue(int index){
			return data[index];
		}
	}

	public static Attribute getAttribute(Group group, String name) throws Exception{
		List<Attribute> attributes = group.getMetadata();
		for (Attribute attr : attributes){
			if (attr.getName().equals(name)){
				return attr;
			}
		}
		throw new RuntimeException("failed to find attribute "+name);
	}

	public static double getDoubleAttribute(Group group, String name) throws Exception{
		Attribute attr = getAttribute(group,name);
		return ((double[])attr.getValue())[0];
	}

	public static float getFloatAttribute(Group group, String name) throws Exception{
		Attribute attr = getAttribute(group,name);
		return ((float[])attr.getValue())[0];
	}

	public static int getIntAttribute(Group group, String name) throws Exception{
		Attribute attr = getAttribute(group,name);
		return ((int[])attr.getValue())[0];
	}

	public static String getStringAttribute(Group group, String name) throws Exception{
		Attribute attr = getAttribute(group,name);
		return ((String[])attr.getValue())[0];
	}

	public static Vect3D getVect3DAttribute(Group group, String name, double defaultZ) throws Exception{
		String str = getStringAttribute(group, name);
		return parseAttrString(str,defaultZ);
	}

	public static Group getChildGroup(Group group, String name){
		List<HObject> memberList = group.getMemberList();
		for (HObject member : memberList) {
			if (member.getName().equals(name)){
				if (member instanceof Group) {
					return (Group)member;
				}else{
					throw new RuntimeException("expecting type Group for group member '"+name+"'");
				}
			}
		}
		throw new RuntimeException("child group '"+name+"' not found");
	}

	public static Hdf5Reader.DataColumn[] getDataTable(Group group, String name) throws Exception{
		List<HObject> memberList = group.getMemberList();
		for (HObject member : memberList) {
			if (member.getName().equals(name)){
				if (member instanceof H5CompoundDS) {
					H5CompoundDS compoundDataSet = (H5CompoundDS) member;
					Vector columnValueArrays = (Vector)compoundDataSet.read();
					String[] columnNames = compoundDataSet.getMemberNames();
					ArrayList<Hdf5Reader.DataColumn> dataColumns = new ArrayList<Hdf5Reader.DataColumn>();
					for (int c=0;c<columnNames.length;c++){
						Object column = columnValueArrays.get(c);
						if (column instanceof int[]){
							dataColumns.add(new Hdf5Reader.IntColumn(columnNames[c], (int[])columnValueArrays.get(c)));
						}else if (column instanceof double[]){
							dataColumns.add(new Hdf5Reader.DoubleColumn(columnNames[c], (double[])columnValueArrays.get(c)));
						}else{
							throw new RuntimeException("unexpected type '"+column.getClass().getName()+"' for group member '"+name+"'");
						}
					}
					return dataColumns.toArray(new Hdf5Reader.DataColumn[0]);
				}else if (member instanceof H5ScalarDS){
					H5ScalarDS compoundDataSet = (H5ScalarDS) member;
					Object column = compoundDataSet.read();
					if (column instanceof int[]){
						return new Hdf5Reader.DataColumn[] { new Hdf5Reader.IntColumn("col", (int[])column) };
					}else if (column instanceof double[]){
						return new Hdf5Reader.DataColumn[] { new Hdf5Reader.DoubleColumn("col", (double[])column) };
					}else if (column instanceof long[]){
						return new Hdf5Reader.DataColumn[] { new Hdf5Reader.LongColumn("col", (long[])column) };
					}else{
						throw new RuntimeException("unexpected type '"+column.getClass().getName()+"' for group member '"+name+"'");
					}
				}else{
					throw new RuntimeException("expecting type H5CompoundDS for group member '"+name+"', found type "+member.getClass().getName());
				}
			}
		}
		throw new RuntimeException("group member '"+name+"' not found");
	}

	public static Vect3D parseAttrString(String attrString, double defaultZ){
		StringTokenizer st = new StringTokenizer(attrString, "{,} ");
		List<Double> valueList = new ArrayList<Double>();
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			valueList.add(Double.parseDouble(token));
		}
		if (valueList.size()==2){
			return new Vect3D(valueList.get(0),valueList.get(1),defaultZ);
		}else if (valueList.size()==3){
			return new Vect3D(valueList.get(0),valueList.get(1),valueList.get(2));
		}else{
			throw new RuntimeException("cannot parse, unexpected array size "+valueList.size());
		}
	}


}
