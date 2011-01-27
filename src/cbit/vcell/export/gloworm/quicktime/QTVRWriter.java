package cbit.vcell.export.gloworm.quicktime;
import javax.swing.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.SimpleFilenameFilter;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.ZEnforcer;

import cbit.vcell.export.gloworm.atoms.AtomConstants;

//import sun.misc.Compare;
//import sun.misc.Sort;
import java.util.Arrays;

import java.awt.*;


import java.io.*;
public class QTVRWriter {

	public static String version = "1.4";
	private class Params {
		private java.io.File[] sourceFiles;
		private java.io.File outputFile;
		// defaults
		private boolean selfContained = true;
		private int codec = CODEC_NONE;
		private float quality = 0.1f;
		private int sampleDuration = 1000; // arbitrary, doesn't matter if no animation in qtvr
	}
	private int workerCount = 0;
	private AsynchProgressPopup pp = null;
	public final static int CODEC_NONE = 0;
	public final static int CODEC_JPEG = 1;
	private Component parent;
	private Params params = new Params();

/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:30:06 AM)
 * @param parent java.awt.Component
 */
public QTVRWriter(java.awt.Component parent) {
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Throwable exc) {
		exc.printStackTrace();
	}
	setParent(parent);
	pp = new AsynchProgressPopup(getParent(), "Creating QTVR file(s)", "", null,false, true);
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:45:11 AM)
 */
private void assembleQTVR(Params workingParams, VRMediaChunk vrChunk, ObjectMediaChunk objChunk, VideoMediaChunk[] vidChunks, int sampleDuration, int sampleNumber) throws java.io.IOException, java.util.zip.DataFormatException {
	MediaTrack qtvrTrack = new MediaTrack(vrChunk);
	MediaTrack objectTrack = new MediaTrack(objChunk);
	MediaTrack imageTrack = new MediaTrack(vidChunks);
	qtvrTrack.setWidth(imageTrack.getWidth());
	qtvrTrack.setHeight(imageTrack.getHeight());
	objectTrack.setWidth(imageTrack.getWidth());
	objectTrack.setHeight(imageTrack.getHeight());
	VRMediaMovie vrMovie = VRMediaMovie.createVRMediaMovie(qtvrTrack, objectTrack, imageTrack, null, sampleDuration * sampleNumber * workingParams.sourceFiles.length, AtomConstants.defaultTimeScale);
	MediaMethods.writeMovie(workingParams.outputFile, vrMovie, true);
}


private Params cloneParams() {
	Params clonedParams = new Params();
	clonedParams.sourceFiles = new java.io.File[params.sourceFiles.length];
	for (int i = 0; i < clonedParams.sourceFiles.length; i++){
		clonedParams.sourceFiles[i] = new java.io.File(params.sourceFiles[i].getPath());
	}
	clonedParams.outputFile = params.outputFile;
	clonedParams.selfContained = params.selfContained;
	clonedParams.codec = params.codec;
	clonedParams.quality = params.quality;
	return clonedParams;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 12:54:40 AM)
 * @param message java.lang.Object
 */

protected static JPanel createMessagePanel(String message) {
	JTextArea textArea = new JTextArea(message);
	textArea.setEditable(false);
	textArea.setWrapStyleWord(true);
	textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
	textArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

	//
	// determine "natural" TextArea prefered size (what it would like if it didn't wrap lines)
	// and try to set size accordingly (within limits ... e.g. 200<=X<=500 and 100<=Y<=400).
	//
	textArea.setLineWrap(false);
	Dimension textAreaPreferredSize = textArea.getPreferredSize();
	textArea.setLineWrap(true);
	Dimension preferredSize = new Dimension((int)Math.min(500,Math.max(200,textAreaPreferredSize.getWidth()+20)),
											(int)Math.min(400,Math.max(100,textAreaPreferredSize.getHeight()+20)));

	
	JScrollPane scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	JPanel panel = new JPanel(new BorderLayout());
	scroller.setViewportView(textArea);
	scroller.getViewport().setPreferredSize(preferredSize);
	panel.add(scroller, BorderLayout.CENTER);
	return panel;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:50:19 AM)
 */
private VideoMediaChunk[] createVideoChunks(Params workingParams, long[] offsets, int dataLength, int sampleNumber, int width, int height, short colorDepth) throws java.io.IOException, java.util.zip.DataFormatException {
		boolean isGrayscale = colorDepth > 32;
		int bitsPerPixel = isGrayscale ? colorDepth - 32 : colorDepth;
		// check for single pixel width/height errors...
		while (dataLength != sampleNumber * width * height * bitsPerPixel / 8) {
			width ++;
			if (dataLength == sampleNumber * width * height * bitsPerPixel / 8) break;
			width --; width --;
			if (dataLength == sampleNumber * width * height * bitsPerPixel / 8) break;
			width ++; height ++;
			if (dataLength == sampleNumber * width * height * bitsPerPixel / 8) break;
			height ++; height ++;
			if (dataLength == sampleNumber * width * height * bitsPerPixel / 8) break;
			throw new java.util.zip.DataFormatException("Data length does not match width/height/colordepth");
		}
		VideoMediaSample sample = null;
		VideoMediaChunk[] vidChunks = new VideoMediaChunk[sampleNumber*workingParams.sourceFiles.length];
		java.io.RandomAccessFile fr = null;
		byte[] sampleBytes = null;
		int progress = 0;
		for (int j = 0; j < workingParams.sourceFiles.length; j++){
			if (workingParams.selfContained) {
				fr = new java.io.RandomAccessFile(workingParams.sourceFiles[j], "r");
				fr.seek(offsets[j]);
			}
			for (int i = 0; i < sampleNumber; i++){
				if (workingParams.selfContained) {
					sampleBytes = new byte[dataLength/sampleNumber];
					fr.readFully(sampleBytes);
					sample = getVideoMediaSample(workingParams, sampleBytes, width, height, bitsPerPixel, isGrayscale);
					vidChunks[j+i*workingParams.sourceFiles.length] = new VideoMediaChunk(sample, workingParams.outputFile);
				} else {
					sample = new VideoMediaSampleRaw(width, height, workingParams.sampleDuration, dataLength/sampleNumber, bitsPerPixel, isGrayscale);
					vidChunks[j+i*workingParams.sourceFiles.length] = new VideoMediaChunk(sample, new java.net.URL("file", "/", workingParams.sourceFiles[j].getAbsolutePath())+"\0", "url ");
					vidChunks[j+i*workingParams.sourceFiles.length].setOffset((int)offsets[j]+i*dataLength/sampleNumber);
				}
				int currentProgress = (j*sampleNumber+i)*100/(sampleNumber*workingParams.sourceFiles.length);
				if (currentProgress != progress) {
					progress = currentProgress;
					pp.setMessage("Writing "+workingParams.outputFile.getName());
					pp.setProgress(progress);
				}
			}
			if (workingParams.selfContained) {
				fr.close();
			}
		}
		return vidChunks;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 3:16:35 AM)
 * @return java.io.File
 * @param movieFile java.io.File
 */
private java.io.File getModifiedMovieFileName(java.io.File movieFile, String suffix) {
	java.io.File parent = movieFile.getParentFile();
	String name = movieFile.getName();
	if (name.length() > 4 && name.substring(name.length() - 4, name.length()).equalsIgnoreCase(".mov")) {
		name = name.substring(0, name.length() - 4);
	}
	name += suffix + ".mov";
	if (name.indexOf(" cuts ") > 0) name = name.substring(0,name.indexOf(" cuts "))+"_c_"+name.substring(name.indexOf("cuts")+5,name.length());
	if (name.indexOf(" projs ") > 0) name = name.substring(0,name.indexOf(" projs "))+"_p_"+name.substring(name.indexOf("projs")+6,name.length());
	return new java.io.File(parent, name);
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:23:58 AM)
 * @return java.awt.Component
 */
private java.awt.Component getParent() {
	return parent;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 2:22:27 AM)
 */
private VideoMediaSample getVideoMediaSample(Params workingParams, byte[] sampleBytes, int width, int height, int bitsPerPixel, boolean isGrayscale) throws java.io.IOException, java.util.zip.DataFormatException {
	VideoMediaSample sample = null;
	switch (workingParams.codec) {
		case CODEC_NONE: {
			sample = new VideoMediaSampleRaw(width, height, workingParams.sampleDuration, sampleBytes, bitsPerPixel, isGrayscale);
			break;
		} 
		case CODEC_JPEG: {
			int imageType;
			boolean supported = true;
			if (isGrayscale && bitsPerPixel == 8) {
				imageType = java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
			} else {
				supported = false;
			}
			if (!supported) throw new java.util.zip.DataFormatException("Unsupported color/pixelDepth combination");
			java.awt.image.BufferedImage bi1 = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
			java.awt.image.BufferedImage bi2 = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
			byte[] invert = new byte[256];
			for (int i = 0; i < 256; i++) invert[i] = (byte)(255 - i);
			java.awt.image.BufferedImageOp invertOp = new java.awt.image.LookupOp(new java.awt.image.ByteLookupTable(0, invert), null);
			java.awt.image.DataBuffer db = new java.awt.image.DataBufferByte(sampleBytes, sampleBytes.length, 0);
			java.awt.image.WritableRaster r = java.awt.image.Raster.createPackedRaster(db, width, height, 8, null);
			bi1.setData(r);
			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
			com.sun.image.codec.jpeg.JPEGImageEncoder enc = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(bout);
			invertOp.filter(bi1, bi2);
			com.sun.image.codec.jpeg.JPEGEncodeParam params = enc.getDefaultJPEGEncodeParam(bi2);
			params.setQuality(workingParams.quality, false);
			enc.setJPEGEncodeParam(params);
			enc.encode(bi2);
			sample = new VideoMediaSampleJPEG(width, height, workingParams.sampleDuration, bout.toByteArray(), bitsPerPixel, isGrayscale);
			break;
		} 
		default: {
			throw new java.util.zip.DataFormatException("Unsupported codec");
		}
	}
	return sample;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 3:01:00 AM)
 * @param message java.lang.Object
 */
private static JDialog prepareErrorDialog(Component requester, String message) {
	JPanel panel = createMessagePanel(message);
	JOptionPane pane = new JOptionPane(panel, JOptionPane.ERROR_MESSAGE);
	JDialog dialog = pane.createDialog(requester, "ERROR:");
	dialog.setResizable(true);
	return dialog;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 3:01:00 AM)
 * @param message java.lang.Object
 */
private static JDialog prepareInfoDialog(Component requester, String message) {
	JPanel panel = createMessagePanel(message);
	JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE);
	JDialog dialog = pane.createDialog(requester, "");
	dialog.setResizable(true);
	return dialog;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:23:58 AM)
 * @param newCodec int
 */
public void setCodec(int newCodec) {
	params.codec = newCodec;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:23:58 AM)
 * @param newOutputFile java.io.File
 */
public void setOutputFile(java.io.File newOutputFile) {
	params.outputFile = newOutputFile;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:23:58 AM)
 * @param newParent java.awt.Component
 */
private void setParent(java.awt.Component newParent) {
	parent = newParent;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:23:58 AM)
 * @param newQuality float
 */
public void setQuality(float newQuality) {
	params.quality = newQuality;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:23:58 AM)
 * @param newSelfContained boolean
 */
public void setSelfContained(boolean newSelfContained) {
	params.selfContained = newSelfContained;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 1:23:58 AM)
 * @param newSourceFiles java.io.File[]
 */
public void setSourceFiles(java.io.File[] newSourceFiles) {
	params.sourceFiles = newSourceFiles;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:17:45 AM)
 * @param owner java.awt.Component
 * @param message java.lang.Object
 */
public static void showErrorDialog(final Component requester, String message) {
	final JDialog dialog = prepareErrorDialog(null, message);
	if (SwingUtilities.isEventDispatchThread()) {
		ZEnforcer.showModalDialogOnTop(dialog,requester);
	} else {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					ZEnforcer.showModalDialogOnTop(dialog,requester);
				}
			});
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		} finally {
			dialog.dispose();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:17:45 AM)
 * @param owner java.awt.Component
 * @param message java.lang.Object
 */
public static void showInfoDialog(java.awt.Component component, String message) {
	final JDialog dialog = prepareInfoDialog(component, message);
	if (SwingUtilities.isEventDispatchThread()) {
		ZEnforcer.showModalDialogOnTop(dialog,JOptionPane.getRootFrame());
	} else {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					ZEnforcer.showModalDialogOnTop(dialog,JOptionPane.getRootFrame());
				}
			});
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		} finally {
			dialog.dispose();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/27/2005 1:10:48 AM)
 * @param workingParams.sourceFiles java.io.File[]
 * @param getOutputFile() java.io.File
 */
public void writeQTVR() throws java.io.IOException, java.util.zip.DataFormatException {
	
	final Params workingParams = cloneParams();
	swingthreads.SwingWorker worker = new swingthreads.SwingWorker() {
		Exception exc = null;
		public Object construct() {
			workerCount ++;
			pp.start();
			pp.setMessage("Checking source files");
			long[] offsets = new long[workingParams.sourceFiles.length];
			int[] dataLengths = new int[workingParams.sourceFiles.length];
			int sampleNumber = 0;
			int width = 0;
			int height = 0;
			short colorDepth = 0;
			int dataLength = 0;
			AtomReader ar = null;
			String error = null;
			try {
				// read input file details and check consistency
//				synchronized(QTVRWriter.this) {
					for (int i = 0; i < workingParams.sourceFiles.length; i++){
						ar = new AtomReader(workingParams.sourceFiles[i].getPath());
						ar.readAtoms();
						if (ar.getNumberOfTracks() > 1) {
							error = "More than one track in file "+workingParams.sourceFiles[i].getPath();
							break;
						}
						if (i == 0) {
							sampleNumber = ar.getSampleNumber(1);
							width = ar.getWidth(1);
							height = ar.getHeight(1);
							colorDepth = ar.getColorDepth(1);
							dataLength = ar.getDataLength();
						} else {
							if (
								(ar.getSampleNumber(1) != sampleNumber) ||
								(ar.getWidth(1) != width) ||
								(ar.getHeight(1) != height) ||
								(ar.getColorDepth(1) != colorDepth) ||
								(ar.getDataLength() != dataLength)
							) {
								error = "Source file "+workingParams.sourceFiles[i].getPath()+" inconsistent with previous files in sample number, width, height, data length, or color depth";
								break;
							}
						}
						offsets[i] = ar.getDataOffset();
					}
					if (error != null) {
						showErrorDialog(getParent(), error);
						return null;
					}
					// create QTVR file
					writeQTVRWorker(workingParams, offsets, sampleNumber, dataLength, width, height, colorDepth);
//				}
			} catch (Exception e) {
				exc = e;
			}
			workerCount --;
			return null;
		}
		public void finished() {
			if (exc != null) {
				showErrorDialog(getParent(), "Create QTVR failed\n"+exc);
			} else {
				Toolkit.getDefaultToolkit().beep();
				if (workerCount == 0) showInfoDialog(getParent(), "QTVR file(s) succesfully created");
			}
			if (workerCount == 0) pp.stop();
		}
	};
	worker.start();
}


/*
 * Creation date: (11/27/2005 1:10:48 AM)
 * @param inputFiles java.io.File[]
 * @param qtvrFile java.io.File
 */
private void writeQTVR(final java.io.File[] inputFiles, final java.io.File qtvrFile) throws java.io.IOException, java.util.zip.DataFormatException {

	// java.io.File jpegFile100 = getModifiedMovieFileName(qtvrFile, "JPEG100");
	java.io.File jpegFile = getModifiedMovieFileName(qtvrFile, "J");
	java.io.File referencedFile = getModifiedMovieFileName(qtvrFile, "R");
	if (qtvrFile.exists()) qtvrFile.delete();
	if (jpegFile.exists()) jpegFile.delete();
	if (referencedFile.exists()) referencedFile.delete();
	setSourceFiles(inputFiles);
	// JPEG max quality version
	setSelfContained(true);
	setCodec(QTVRWriter.CODEC_JPEG);
	setQuality(1.0f);
	setOutputFile(qtvrFile);
	writeQTVR();
	// JPEG compressed version
	setSelfContained(true);
	setCodec(QTVRWriter.CODEC_JPEG);
	setQuality(0.1f);
	setOutputFile(jpegFile);
	writeQTVR();
	// referenced version
	setSelfContained(false);
	setOutputFile(referencedFile);
	writeQTVR();
}


/**
 * Insert the method's description here.
 * Creation date: (11/27/2005 3:42:12 PM)
 * @param getSourceFiles() java.io.File[]
 * @param qtvrFile java.io.File
 */
private synchronized void writeQTVRWorker(Params workingParams, long[] offsets, int sampleNumber, int dataLength, int width, int height, short colorDepth) throws java.io.IOException, java.util.zip.DataFormatException {
	/* get video samples, put them into chunks and write them into qtvr file */
	VideoMediaChunk[] vidChunks = createVideoChunks(workingParams, offsets, dataLength, sampleNumber, width, height, colorDepth);
	/* make the single node VR World and required chunks */
	VRWorld singleObjVRWorld = VRWorld.createSingleObjectVRWorld(workingParams.sampleDuration, workingParams.sourceFiles.length, sampleNumber, (float)(width/2), (float)(height/2));
	singleObjVRWorld.getVRObjectSampleAtom(0).setControlSettings(singleObjVRWorld.getVRObjectSampleAtom(0).getControlSettings() | (Integer.parseInt("00001000",2))); // reverse pan controls (set bit 3)
	VRMediaChunk vrChunk = new VRMediaChunk(singleObjVRWorld);
	ObjectMediaChunk objChunk = new ObjectMediaChunk(singleObjVRWorld);
	MediaChunk[] chunks = new MediaChunk[vidChunks.length + 2];
	chunks[0] = vrChunk;
	chunks[1] = objChunk;
	for (int i = 0; i < vidChunks.length; i++){
		chunks[i+2] = vidChunks[i];
	}
	/* assemble tracks and write the rest of the file */
	assembleQTVR(workingParams, vrChunk, objChunk, vidChunks, workingParams.sampleDuration, sampleNumber);
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 3:43:15 PM)
 * @param rootDir java.io.File
 */
public void writeWormQTVR(File rootDir) {
	java.io.FileFilter dirFilter = new java.io.FileFilter() {
		public boolean accept(java.io.File f) {
			return f.isDirectory();
		}
	};
	java.io.File[] selectionDirs = rootDir.listFiles(dirFilter);
	java.util.Vector v = new java.util.Vector();
	for (int i = 0; i < selectionDirs.length; i++){
		java.io.File[] movDirs = selectionDirs[i].listFiles(dirFilter);
		for (int j = 0; j < movDirs.length; j++){
			v.add(movDirs[j]);
		}
	}
	java.io.File[] sourceDirs = (java.io.File[])BeanUtils.getArray(v, java.io.File.class);
	java.util.Hashtable sourceHash = new java.util.Hashtable();
	for (int i = 0; i < sourceDirs.length; i++){
		java.io.File[] files = sourceDirs[i].listFiles(new SimpleFilenameFilter("mov"));
		Arrays.sort(files);
		/*Compare comp = new Compare(){
				public int doCompare (Object o1, Object o2) 
					{ 
					return o1.toString().compareTo(o2.toString());
					}
				};
		Sort.quicksort(files, comp);*/
		sourceHash.put(sourceDirs[i], files);
	}
	java.util.Enumeration en = sourceHash.keys();
	while (en.hasMoreElements()) {
		java.io.File sourceDir = (java.io.File)en.nextElement();
		java.io.File[] sourceFiles = (java.io.File[])sourceHash.get(sourceDir);
		java.io.File outputFile = new java.io.File(rootDir, sourceDir.getName()+".mov");
		try {
			writeQTVR(sourceFiles, outputFile);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}	
}