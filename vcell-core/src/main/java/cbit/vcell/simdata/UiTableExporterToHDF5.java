package cbit.vcell.simdata;

import cbit.vcell.export.server.JhdfUtils;
import cbit.vcell.math.ReservedVariable;
import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.WritableDataset;
import io.jhdf.api.WritableGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class UiTableExporterToHDF5 {
    public static File exportTableToHDF5(boolean bHistogram, String blankCellValue, int[] columns, int[] rows, String xVarColumnName, String hdf5DescriptionText, String[] columnNames, String[] paramScanParamNames, Double[][] paramScanParamValues, Object[][] rowColValues) throws Exception {
		File hdf5TempFile = null;
		try {
			hdf5TempFile = File.createTempFile("plot2D", ".hdf");
			// jhdf assembles the file in memory and writes it on close; this export is a table the
			// caller already holds, so there is nothing to stream
			WritableHdfFile hdf5File = HdfFile.write(hdf5TempFile.toPath());
			ArrayList<ArrayList<Integer>> paramScanJobs = new ArrayList<>();
			if(!bHistogram && !columnNames[0].equals((xVarColumnName==null? ReservedVariable.TIME.getName():xVarColumnName))) {
				throw new Exception("Expecting first column in table to have name '"+xVarColumnName+"'");
			}
			//Add arraylist for the parameter scan job, add the index of the xval column
			for(int i=0;i<columnNames.length;i++) {
				if(bHistogram) {
					ArrayList<Integer> tempAL = new ArrayList<Integer>();
					paramScanJobs.add(tempAL);
					break;
				} else if(columnNames[i].equals((xVarColumnName==null?ReservedVariable.TIME.getName():xVarColumnName))){
					if(i==0) {
						ArrayList<Integer> tempAL = new ArrayList<Integer>();
						tempAL.add(i);
						paramScanJobs.add(tempAL);
					}else {
						String str1 = columnNames[i-1];
						int str1Index = str1.lastIndexOf("Set ");
						String str2 = columnNames[i+1];
						int str2Index = str2.lastIndexOf("Set ");
						if(!str1.substring(str1Index).equals(str2.substring(str2Index))) {
							ArrayList<Integer> tempAL = new ArrayList<Integer>();
							tempAL.add(i);
							paramScanJobs.add(tempAL);
						}
					}
				}
			}
			//Add selected columns to the proper paramscan arraylist
			for(int j = 0; j< columns.length; j++) {
				if(bHistogram) {
					paramScanJobs.get(0).add(columns[j]);
				}else {
					if(columnNames[columns[j]].equals((xVarColumnName==null?ReservedVariable.TIME.getName():xVarColumnName))){
						continue;//skip xcolumns
					}
					for(int k=0;k<paramScanJobs.size();k++) {
						if(columns[j] >= paramScanJobs.get(k).get(0) && ((k+1) == paramScanJobs.size() || columns[j] < paramScanJobs.get(k+1).get(0))) {
							paramScanJobs.get(k).add(columns[j]);
//								System.out.println("HDF5frm"+columnNames[columns[j]));
						}
					}
				}
			}
			//Remove unselected indexes from set lists
			for(int k=0;k<paramScanJobs.size();k++) {
				final ListIterator<Integer> listIterator = paramScanJobs.get(k).listIterator();
				if(paramScanJobs.get(k).size() > 1) {// keep x val is there more selections for this set
					listIterator.next();
				}
				while(listIterator.hasNext()) {
					final Integer columIndex = listIterator.next();
					boolean bFound = false;
					for(int j = 0; j< columns.length; j++) {
						if(columIndex == columns[j]) {
							bFound = true;
							break;
						}
					}
					if(!bFound) {
						listIterator.remove();
					}
				}
			}
			//Write out the data to HDF5 file
			for(int k=0;k<paramScanJobs.size();k++) {
				int selectedColCount = paramScanJobs.get(k).size();
				if(selectedColCount == 0) {
					continue;
				}
				WritableGroup jobGroup = null;
				Integer setNumber = null;
				//((DefaultTableModel)getScrollPaneTable().getModel()).getDataVector()
				double[] fromData = new double[rows.length*selectedColCount];
				int actualLength = -1;
				int index = 0;
				ArrayList<String> dataTypes = new ArrayList<String>();
				ArrayList<String> dataIDs = new ArrayList<String>();
				ArrayList<String> dataShapes = new ArrayList<String>();
				ArrayList<String> dataLabels = new ArrayList<String>();
				ArrayList<String> dataNames = new ArrayList<String>();
				ArrayList<String> paramNames = new ArrayList<String>();
				ArrayList<String> paramValues = new ArrayList<String>();
				boolean bParamsDone = false;
				for(int cols=0;cols<paramScanJobs.get(k).size();cols++) {
					final Integer column = paramScanJobs.get(k).get(cols);
					dataTypes.add("float64");
					dataIDs.add("data_set_"+columnNames[column]);
					dataShapes.add(rows.length+"");
					dataLabels.add(columnNames[column]);
					String name = "--";
					if(columnNames[column].equals((xVarColumnName==null?ReservedVariable.TIME.getName():xVarColumnName))) {
						name = columnNames[column];
					}else {
						int indx = columnNames[column].lastIndexOf("-- Set ");
						if(indx != -1) {
							name = columnNames[column].substring(0, indx);
						}else {
							name = columnNames[column];
						}
					}
					dataNames.add(name);
					for(int myrows = 0; myrows< rows.length; myrows++) {
						final int row = rows[myrows];
						final Object valueAt = rowColValues[row][column];
						if(valueAt == null && actualLength == -1) {
							actualLength = myrows;
						}
//							System.out.println(row+" "+column+" "+valueAt);
						fromData[index] = Double.parseDouble((valueAt==null? blankCellValue :valueAt.toString()));
						index++;
					}
					actualLength = (actualLength==-1? rows.length:actualLength);
					String colName = columnNames[column];
//						System.out.println("HDF5frm "+colName);
					if(colName.lastIndexOf("Set ") != -1) {
						if(!bParamsDone) {
							bParamsDone = true;
							int set = Integer.parseInt(colName.substring(colName.lastIndexOf("Set ")+4));
							setNumber = set;
							for(int z=0;z<paramScanParamNames.length;z++) {
								paramNames.add(paramScanParamNames[z]);
								paramValues.add(paramScanParamValues[set][z]+"");
//									System.out.print(" "+paramScanParamValues[set][z]);
							}
//							System.out.println();
						}
					}

				}

				// [column][row], the shape the dataset was created with
				double[][] setData = new double[selectedColCount][actualLength];
				for(int i=0;i<selectedColCount;i++) {
					System.arraycopy(fromData, i* rows.length, setData[i], 0, actualLength);
				}
				jobGroup = hdf5File.putGroup("Set " + (setNumber == null ? k : setNumber));
				WritableDataset dataset = jobGroup.putDataset("data", setData);

				JhdfUtils.putAttribute(dataset, "_type", "ODE Data Export");
				JhdfUtils.putAttribute(dataset, "dataSetDataTypes", dataTypes);
				JhdfUtils.putAttribute(dataset, "dataSetIds", dataIDs);
				JhdfUtils.putAttribute(dataset, "dataSetLabels", dataLabels);
				JhdfUtils.putAttribute(dataset, "dataSetNames", dataNames);
				JhdfUtils.putAttribute(dataset, "dataSetShapes", dataShapes);
				JhdfUtils.putAttribute(dataset, "id", "report");
				if(paramNames.size() != 0) {
					JhdfUtils.putAttribute(dataset, "paramNames", paramNames);
					JhdfUtils.putAttribute(dataset, "paramValues", paramValues);
				}
			}
			if(hdf5DescriptionText != null) {
				JhdfUtils.putAttribute(hdf5File, "dataSourceDescr", Arrays.asList(hdf5DescriptionText));
			}
			hdf5File.close(); // writes the file
		}catch(Exception e) {
			if(hdf5TempFile != null && hdf5TempFile.exists()) {try{hdf5TempFile.delete();}catch(Exception ignored){}}
			throw e;
		}
		return hdf5TempFile;
	}

}
