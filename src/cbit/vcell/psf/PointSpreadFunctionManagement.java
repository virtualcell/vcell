package cbit.vcell.psf;

import java.util.Hashtable;

import static cbit.vcell.xml.VFrapXmlHelper.CreateSaveVFrapDataSymbols;
import static cbit.vcell.xml.VFrapXmlHelper.LoadVFrapDisplayRoi;
import static cbit.vcell.xml.VFrapXmlHelper.LoadVFrapSpecialImages;
import static cbit.vcell.xml.VFrapXmlHelper.SaveVFrapSpecialImagesAsFieldData;
import static cbit.vcell.xml.VFrapXmlHelper.checkNameAvailability;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.zip.DataFormatException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import org.jdom.Element;
import org.vcell.util.BeanUtils;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.image.ImageException;
import cbit.image.ImageFile;
import cbit.image.VCImageUncompressed;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.importer.AnnotatedImageDataset;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXmlReader;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.units.VCUnitDefinition;

public class PointSpreadFunctionManagement {

	static final String psfStaticName = "fluor";
	
	static 	JFileChooser fc = null;
	private JPanel parentWindow = null;
	private SimulationContext simulationContext = null;
	
	public PointSpreadFunctionManagement(JPanel argParentWindow, SimulationContext argSimulationContext) {
		this.parentWindow = argParentWindow;
		this.simulationContext = argSimulationContext;

		//Create a file chooser
		fc = new JFileChooser();
		PointSpreadFunctionDataFilter filter = new PointSpreadFunctionDataFilter();
		fc.setFileFilter(filter);
}
	
	private void ChoosePSFFile(Hashtable<String, Object> hashTable) throws IOException, ImageException {
		int returnVal = fc.showOpenDialog(parentWindow);

		if (returnVal == JFileChooser.APPROVE_OPTION) { 
			File filePSF = fc.getSelectedFile();
			if (filePSF == null) {
				throw new RuntimeException("Unable to open PSF file.");
			}
			String filePSFPath = filePSF.getCanonicalPath();
			String filePSFNameExtended = filePSF.getName();
			String initialFieldDataName = filePSFNameExtended.substring(0, filePSFNameExtended.indexOf("."));
			String mixedFieldDataName = initialFieldDataName + "Mx";	// unused here but needed for some shared code
			
//			// testing
//			System.out.println("Reading file: "+filePSFPath);
			ImageFile imagePSFFile = new ImageFile(filePSFPath);
			
//			System.out.println("Writing Image "+filePSFName+".tif");
//			imageFile.writeAsTIFF(filePSFName+".tif");	// no path specified, writes in current dir (vCell)

			hashTable.put("filePSF", filePSF);
			hashTable.put("initialFieldDataName", initialFieldDataName);
			hashTable.put("mixedFieldDataName", mixedFieldDataName);
		} else {
			throw UserCancelException.CANCEL_GENERIC;
		}
	}
	public void importPointSpreadFunction() {
		
		AsynchClientTask[] taskArray = new AsynchClientTask[3];

		// select the desired PSF file 
		taskArray[0] = new AsynchClientTask("Select a file", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ChoosePSFFile(hashTable);
			}
		};
		// create and save the field data object
		taskArray[1] = new AsynchClientTask("Import objects", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				
				Component requesterComponent = parentWindow;
				DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(requesterComponent, DocumentWindow.class);
				DocumentManager documentManager = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
				if(documentManager == null){
					throw new RuntimeException("Not connected to server.");
				}
				// the following line of code may modify initialFieldDataName
				checkNameAvailability(hashTable, false, documentManager, requesterComponent);	// normal file name

				File filePSF = (File)hashTable.get("filePSF");
				String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
				
				ImageDataset imageDataset = ImageDatasetReader.readImageDataset(filePSF.getAbsolutePath(),null);
				Extent extent = imageDataset.getExtent();
				ISize isize = imageDataset.getISize();
				Origin origin = new Origin(0,0,0);
				CartesianMesh cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent,isize, 
					new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, 
							isize.getX(),isize.getY(),isize.getZ()),0,null,null,RegionImage.NO_SMOOTHING));
				// save field data
				int NumTimePoints = imageDataset.getImageTimeStamps().length; 
				int NumChannels = 1;
				double[][][] pixData = new double[NumTimePoints][NumChannels][]; 
				for(int i = 0 ; i < NumTimePoints; i++)
				{
					short[] originalData = imageDataset.getPixelsZ(0, i);	// images according to zIndex at specific time points(tIndex)
					double[] doubleData = new double[originalData.length];
					for(int j = 0; j < originalData.length; j++)
					{
						doubleData[j] = 0x0000ffff & originalData[j];
					}
					pixData[i][NumChannels-1] = doubleData;
				}

				FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
//				try {
//					fdos = ClientRequestManager.createFDOSFromImageFile(filePSF, false, null);
//				} catch (DataFormatException ex) {
//					throw new Exception("Cannot read image " + filePSF.getAbsolutePath()+"\n"+ex.getMessage());
//				}
				
				fdos.owner = documentManager.getUser();
				fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
				fdos.cartesianMesh = cartesianMesh;
				fdos.doubleSpecData =  pixData;
				fdos.specEDI = null;
				fdos.varNames = new String[] {psfStaticName};
				fdos.times = imageDataset.getImageTimeStamps();
				fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
				fdos.origin = origin;
				fdos.extent = extent;
				fdos.isize = isize;
				
		   		ExternalDataIdentifier pSFImageEDI = documentManager.saveFieldData(fdos, initialFieldDataName);
				hashTable.put("pSFImageEDI", pSFImageEDI);
			}
		};
		// create a data symbol for the PSF image saved above as field data
		taskArray[2] = new AsynchClientTask("Display Data Symbols", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				
		   		// --- create the data symbols associated with the time series
				String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
				ExternalDataIdentifier pSFImageEDI = (ExternalDataIdentifier)hashTable.get("pSFImageEDI");
				
		   		String fluorName = "psf_" + initialFieldDataName;
				DataSymbol fluorDataSymbol = new FieldDataSymbol( fluorName, DataSymbolType.POINT_SPREAD_FUNCTION, 
						simulationContext.getDataContext(), VCUnitDefinition.UNIT_TBD,
						pSFImageEDI, psfStaticName, VariableType.VOLUME.getTypeName(), 0D);
				simulationContext.getDataContext().addDataSymbol(fluorDataSymbol); 
			}
		};
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		ClientTaskDispatcher.dispatch(parentWindow, hash, taskArray, false, true, null);
	
	
	}

	
	public class PointSpreadFunctionDataFilter extends FileFilter {

	    public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }
	        ExtensionsManagement u = new ExtensionsManagement();
	        String extension = u.getExtension(f);
	        if (extension != null) {
	            if (extension.equals(ExtensionsManagement.jpeg)
	                	|| extension.equals(ExtensionsManagement.jpg)
	                	|| extension.equals(ExtensionsManagement.png)
	            		) {
	                    return true;
	            } else {
	                return false;
	            }
	        }
	        return false;
	    }
	    //The description of this filter
	    public String getDescription() {
	        return "File Formats accepted as Field Data for Point Spread Functions";
	    }
	}
	public class ExtensionsManagement {
	    public final static String jpeg = "jpeg";
	    public final static String jpg = "jpg";
	    public final static String gif = "gif";
	    public final static String tiff = "tiff";
	    public final static String tif = "tif";
	    public final static String png = "png";
	    public final static String vfrap = "vfrap";
	    
	    public String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
	}
}
