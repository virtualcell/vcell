package cbit.vcell.export.nrrd;
import java.io.*;
public class NrrdWriter {
/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static void writeArrayFieldString(FileWriter fw, String name, double[] data) throws IOException {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		fw. write(name + ": " + bf.toString().trim() + "\n");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static void writeArrayFieldString(FileWriter fw, String name, int[] data) throws IOException {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		fw. write(name + ": " + bf.toString().trim() + "\n");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static void writeArrayFieldString(FileWriter fw, String name, String[] data) throws IOException {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		fw. write(name + ": " + bf.toString().trim() + "\n");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 10:51:32 AM)
 * @param headerFileName java.lang.String
 * @param baseDir java.io.File
 * @param info cbit.vcell.export.nrrd.NrrdInfo
 * @exception java.io.IOException The exception description.
 * @exception java.io.FileNotFoundException The exception description.
 */
public static NrrdInfo writeNRRD(String fileName, File baseDir, NrrdInfo info) throws IOException, FileNotFoundException {
	if (info == null) throw new IOException("No info specified for header");
	if (!baseDir.exists()) throw new FileNotFoundException("Base directory " + baseDir.getCanonicalPath() + " does not exist");
	// for simplicity we enforce header and data files to be in the same directory
	File file = new File(baseDir, fileName);
	File dataFile = new File(baseDir, info.getDatafile());
	if (file.exists()) throw new IOException("File " + file.getCanonicalPath() + " already exists");
	if (! dataFile.exists()) throw new FileNotFoundException("Referenced data file " + baseDir.getCanonicalPath() + " does not exist");
	// should be OK to write now
	// write header first
	FileWriter fw = new FileWriter(file);
	try {
		fw.write(NrrdInfo.MAGIC + "\n");
		fw.write("endian: " + NrrdInfo.ENDIAN + "\n");
		fw.write("# " + info.getMainComment() + "\n");
		fw.write("type: " + info.getType() + "\n");
		fw.write("dimension: " + info.getDimension() + "\n");
		fw.write("encoding: " + info.getEncoding() + "\n");
		writeArrayFieldString(fw, "sizes", info.getSizes());
		writeArrayFieldString(fw, "spacings", info.getSpacings());
		writeArrayFieldString(fw, "aximins", info.getAxismins());
		writeArrayFieldString(fw, "axismaxs", info.getAxismaxs());
		writeArrayFieldString(fw, "centers", info.getCenters());
		writeArrayFieldString(fw, "labels", info.getLabels());
		writeArrayFieldString(fw, "units", info.getUnits());
		fw.write("min: " + info.getMin() + "\n");
		fw.write("max: " + info.getMax() + "\n");
		fw.write("lineskip: " + info.getLineskip() + "\n");
		fw.write("byteskip: " + info.getByteskip() + "\n");
		if (info.isSeparateHeader()) {
			fw.write("datafile: " + info.getDatafile() + "\n");
		} else {
			fw.write("\n");
		}
	} catch (IOException exc) {
		throw exc;
	} finally {
		fw.close();
	}
	// if we didn't want a detached header, append the datafile
	if (! info.isSeparateHeader()) {
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath(), true));
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(dataFile));
		byte[] bytes = new byte[65536];
		try {
			int b = in.read(bytes);
			while (b != -1) {
				out.write(bytes, 0, b);
				b = in.read(bytes);
			}
		} catch (IOException exc) {
			throw exc;
		} finally {
			in.close();
			out.close();
		}
	}
	// successful write
	info.setHasData(true);
	info.setHeaderfile(fileName);
	return info;
}
}