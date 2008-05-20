package cbit.vcell.field;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import loci.formats.ImageTools;

import cbit.util.AsynchProgressPopup;
import cbit.util.BeanUtils;
import cbit.util.Extent;
import cbit.util.ISize;
import cbit.util.Origin;
import cbit.util.Preference;
import cbit.util.ProgressDialogListener;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.geometry.Coordinate;
import cbit.vcell.pslid.WebClientInterface;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.simdata.VariableType;

import cbit.vcell.pslid.*;

public class PSLIDPanel extends JPanel{
	
	final int modePslidExperimentalData = 0;
	final int modePslidGeneratedModel = 1;

	private volatile Thread threadState;
    public void stopThread() {
    	threadState = null;
    }
    public Thread getThreadState() {
    	return threadState;
    }

    private PSLIDPanel thisPanel;
	private JRadioButton compartmentRadioButton;
	private JPanel panel_2;
	private JRadioButton proteinRadioButton;
	private JPanel panel_1;
	public static class PSLIDSelectionInfo{
		public String cellName;
		public String proteinName;
		public String imageSetID;
		public String proteinImageURL;
		public String compartmentImageURL;
		public boolean isSynthetic;
		public String protocolTitle;
		public String microscopeName;
		public FieldDataFileOperationSpec fdos;
//		public byte[] proteinImage;
//		public byte[] compartmentImage;
//		public Origin origin;
//		public Extent extent;
//		public ISize isize;
	};
	private ButtonGroup buttongroup = new ButtonGroup();
	private JButton downloadButton;
	private JButton queryButton;
	private JLabel zsizeLabel_1;
	private JLabel zsizeLabel;
	private JLabel ysizeLabel_1;
	private JLabel ysizeLabel;
	private JLabel xsizeLabel_1;
	private JLabel xsizeLabel;
	private JPanel panelControls;
	private JComboBox imageIDcomboBox;
	private JLabel imageIdLabel;
	private JComboBox proteinCellcomboBox;
	private JScrollPane scrollPane = new JScrollPane();
	private JLabel pslidProteincellLabel = new JLabel();
	private static final String PROTCELLLABEL = "PSLID Protein/Cell";
	private Vector<String[]> sortedCellProtenV;
	private TreeMap<Integer,String> imageID_ProteinImageURL_Hash = null;
	private TreeMap<Integer,String> imageID_CompartmentImageURL_Hash = null;
	private TreeMap<Integer,ISize> imageID_DimHash = null;
	private TreeMap<Integer,Coordinate> imageID_PixelSizeHash = null;
	private TreeMap<Integer,String> imageID_ProtocolTitle_Hash = null;
	private TreeMap<Integer,String> imageID_MicroscopeName_Hash = null;
	
	private Object lastQueriedPSLIDCellProteinSeries = null;
	private Object lastDownloadedImageSetID = null;
	private FieldDataFileOperationSpec selectedFDOS = null;
	private BufferedImage proteinDisplayImage = null;
	private BufferedImage compartmentDisplayImage = null;
	
	private static final String CELL_PROTEIN_GENERATED = "generated";
	private static final String CELL_PROTEIN_COLLECTED = "collected";
	private static final int CELL_INDEX = 0;
	private static final int PROTEIN_INDEX = 1;
	private static final int KIND_INDEX = 2;
	
	private UserPreferences userPreferences;
	
	public PSLIDPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,7};
		gridBagLayout.columnWidths = new int[] {0,7};
		setLayout(gridBagLayout);

		pslidProteincellLabel.setText("PSLID Cell_Protein Series");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(pslidProteincellLabel, gridBagConstraints);

		proteinCellcomboBox = new JComboBox();
		proteinCellcomboBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(proteinCellcomboBox.getSelectedItem() == lastQueriedPSLIDCellProteinSeries){
					return;
				}
				lastDownloadedImageSetID = null;
				imageIDcomboBox.removeAllItems();
				guiState();
			}
		});
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 1;
		add(proteinCellcomboBox, gridBagConstraints_1);

		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.weighty = 1;
		gridBagConstraints_2.weightx = 1;
		gridBagConstraints_2.gridwidth = 3;
		gridBagConstraints_2.gridy = 3;
		gridBagConstraints_2.gridx = 0;
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.EAST;
		gridBagConstraints_3.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.insets = new Insets(4, 4, 4, 4);
		add(getQueryButton(), gridBagConstraints_6);
		add(getImageIdLabel(), gridBagConstraints_3);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 1;
		add(getImageIDcomboBox(), gridBagConstraints_4);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridwidth = 3;
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 0;
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.gridy = 1;
		gridBagConstraints_7.gridx = 2;
		add(getDownloadButton(), gridBagConstraints_7);
		add(getPanelControls(), gridBagConstraints_5);
		add(scrollPane, gridBagConstraints_2);
		thisPanel = this;
		init();
	}

	private static FieldDataFileOperationSpec createCompositeFDOS(
		byte[] proteinImage,byte[] compartmentImage,Coordinate pixelSizes,String cellProteinName) throws Exception{
		
		FileOutputStream fos = null;
		try {
			File proteinImageFile = File.createTempFile("pslidProt", ".tif");
			proteinImageFile.deleteOnExit();
			fos = new FileOutputStream(proteinImageFile);
			fos.write(proteinImage);
			fos.close();
			File compartmentImageFile = File.createTempFile("pslidComp", ".tif");
			compartmentImageFile.deleteOnExit();
			fos = new FileOutputStream(compartmentImageFile);
			fos.write(compartmentImage);
			fos.close();
//			FieldDataFileOperationSpec fdos_compartment = FieldDataGUIPanel.createFDOSFromImageFile(compartmentImageFile,false);
			loci.formats.ImageReader compartmentImageReader = new loci.formats.ImageReader();
			loci.formats.IFormatReader compartmentFormatReader = compartmentImageReader.getReader(compartmentImageFile.getAbsolutePath());
			compartmentFormatReader.setId(compartmentImageFile.getAbsolutePath());
//			compartmentFormatReader.setColorTableIgnored(true);
			byte[] compartmentBytes = compartmentFormatReader.openBytes(0);
			short[] compartmentShorts = new short[compartmentBytes.length];
			for (int i = 0; i < compartmentShorts.length; i++) {
				compartmentShorts[i] = compartmentBytes[i];
			}
//			FieldDataFileOperationSpec fdos_protein = FieldDataGUIPanel.createFDOSFromImageFile(proteinImageFile,false);
			loci.formats.ImageReader proteinImageReader = new loci.formats.ImageReader();
			loci.formats.IFormatReader proteinFormatReader = proteinImageReader.getReader(proteinImageFile.getAbsolutePath());
			proteinFormatReader.setId(proteinImageFile.getAbsolutePath());
//			byte[] proteinBytes = proteinFormatReader.openBytes(proteinImageFile.getAbsolutePath(), 0);
//			short[] proteinShorts = new short[proteinBytes.length];
//			for (int i = 0; i < compartmentShorts.length; i++) {
//				proteinShorts[i] = proteinBytes[i];
//			}
			short[] proteinShorts = ImageTools.getShorts(proteinFormatReader.openImage(0))[0];

			int xsize_uncrop = compartmentFormatReader.getSizeX();
			int ysize_uncrop = compartmentFormatReader.getSizeY();
//			if(
//					compartmentFormatReader.getSizeX(compartmentImageFile.getAbsolutePath()) != isize.getX() ||
//					compartmentFormatReader.getSizeY(compartmentImageFile.getAbsolutePath()) != isize.getY() ||
//					compartmentFormatReader.getSizeZ(compartmentImageFile.getAbsolutePath()) != isize.getZ() ||
//					proteinFormatReader.getSizeX(proteinImageFile.getAbsolutePath()) != isize.getX() ||
//					proteinFormatReader.getSizeY(proteinImageFile.getAbsolutePath()) != isize.getY() ||
//					proteinFormatReader.getSizeZ(proteinImageFile.getAbsolutePath()) != isize.getZ() ||
//					compartmentShorts.length != proteinShorts.length ||
//					compartmentShorts.length != (isize.getX() * isize.getY()*isize.getZ()))
//			{
//				throw new Exception("comaprtment and protein image sizes are incompatible");
//			}
			//Min Bounding
			final int CELL_PIXEL_VAL = 1;
			int minx = xsize_uncrop;
			int miny = ysize_uncrop;
			int maxx = 0;
			int maxy = 0;
			for (int i = 0; i < compartmentShorts.length; i++) {
				if(compartmentShorts[i] == CELL_PIXEL_VAL){
					minx = Math.min(minx,i%xsize_uncrop);
					miny = Math.min(miny,i/xsize_uncrop);
					maxx = Math.max(maxx,i%xsize_uncrop);
					maxy = Math.max(maxy,i/xsize_uncrop);
				}
			}
			//Pad
			minx = Math.max(minx-3,0);
			miny = Math.max(miny-3,0);
			maxx = Math.min(maxx+3,xsize_uncrop);
			maxy = Math.min(maxy+3,ysize_uncrop);
			//Crop
			int cropIndex = 0;
			short[] proteinCropShorts = new short[(maxx-minx+1) * (maxy-miny+1)];
			short[] compartmentCropShorts = new short[proteinCropShorts.length];
			for (int y = 0; y < ysize_uncrop; y++) {
				for (int x = 0; x < xsize_uncrop; x++) {
					if(y>= miny && y<= maxy && x>= minx && x <= maxx){
						proteinCropShorts[cropIndex] = proteinShorts[y*xsize_uncrop+x];
						compartmentCropShorts[cropIndex] = compartmentShorts[y*xsize_uncrop+x];
						cropIndex++;
					}
				}
			}
			final int xsize = maxx-minx+1;
			final int ysize = maxy-miny+1;
			
			FieldDataFileOperationSpec fdos_composite = new FieldDataFileOperationSpec();
			fdos_composite.variableTypes = new VariableType[] {VariableType.VOLUME,VariableType.VOLUME };
			fdos_composite.varNames = new String[] { "protein_"+cellProteinName,"compartment_"+cellProteinName };
			fdos_composite.shortSpecData = new short[][][] {{proteinCropShorts,compartmentCropShorts}};
			fdos_composite.times = new double[] { 0 };
			fdos_composite.origin = new Origin(0,0,0);
			fdos_composite.isize = new ISize(xsize,ysize,1);
			fdos_composite.extent =
				new Extent(fdos_composite.isize.getX()*pixelSizes.getX(),
						fdos_composite.isize.getY()*pixelSizes.getY(),
						(pixelSizes.getZ() == 0?.5:fdos_composite.isize.getZ()*pixelSizes.getZ()));
			return fdos_composite;
		}finally{
			if(fos != null){try{fos.close();}catch(Exception e){}}
		}
	}
	private void guiState(){
		boolean bLastQuerySame = proteinCellcomboBox.getSelectedItem() == lastQueriedPSLIDCellProteinSeries;
		boolean bLastDownloadSame = imageIDcomboBox.getSelectedItem() == lastDownloadedImageSetID;
		boolean isGenerated = sortedCellProtenV.get(proteinCellcomboBox.getSelectedIndex())[KIND_INDEX].equals(CELL_PROTEIN_GENERATED);
		boolean bEnableDownload = !bLastDownloadSame || (imageIDcomboBox.getItemCount() != 0 && isGenerated);
		queryButton.setEnabled(!bLastQuerySame);
		downloadButton.setEnabled(bEnableDownload);
		imageIDcomboBox.setEnabled(imageIDcomboBox.getItemCount() != 0);
		if(!bLastDownloadSame || !bLastQuerySame){
			selectedFDOS = null;
			scrollPane.setViewportView(null);
		}
		BeanUtils.enableComponents(getPanelControls(), selectedFDOS != null);
	}
	
	private void init(){
		buttongroup.add(getProteinRadioButton());
		buttongroup.add(getCompartmentRadioButton());
		imageIDcomboBox.setEnabled(false);
		downloadButton.setEnabled(false);
		BeanUtils.enableComponents(getPanelControls(), false);
	}

	// reads cell - protein data sets for either experimental data or generative models
	// populates combobox with pairs
	public void initCellProteinList(UserPreferences userPreferences,AsynchProgressPopup pp, int mode) throws Exception{
		this.userPreferences = userPreferences;
		if(proteinCellcomboBox.getModel().getSize() > 0){
			return;
		}
		  try {
			  TreeSet<String[]> combinedTreeSet = null;
			  
			  switch(mode) {
			  case modePslidExperimentalData:
//			http://pslid.cbi.cmu.edu/develop/return_xml_list.jsp?listtype=target_cell_name
				  TreeSet<String[]> sortedCellProteinTreeSet = readCellProteinListExperimental(pp);
				  combinedTreeSet = new TreeSet<String[]>(sortedCellProteinTreeSet);
				  break;
			  case modePslidGeneratedModel:
		      TreeSet<String[]> sortedGeneratedModelTreeSet = readCellProteinListGenerated(pp);
		    		//new java.net.URL("http://pslid.cbi.cmu.edu/develop/return_xml_list.jsp?listtype=gen_model"),pp);
	    			//new java.net.URL("http://pslid.cbi.cmu.edu/tcnp/return_xml_list.jsp?listtype=gen_model"),pp);
		      combinedTreeSet = new TreeSet<String[]>(sortedGeneratedModelTreeSet);
		      break;
			  }
	    	  sortedCellProtenV = new Vector<String[]>();
		      proteinCellcomboBox.removeAllItems();
		      Iterator<String[]> iter = combinedTreeSet.iterator();
		      while(iter.hasNext()){
		    	  String[] cellProteinPair = iter.next();
		    	  sortedCellProtenV.add(cellProteinPair);
//		    	  proteinCellcomboBox.addItem(cellProteinPair[0]+"_"+cellProteinPair[1]+"_"+cellProteinPair[2]);
		    	  proteinCellcomboBox.addItem(cellProteinPair[0]+",  "+cellProteinPair[1]);
		      }
		      proteinCellcomboBox.setSelectedIndex(0);
		  } catch (Exception e) {
			  sortedCellProtenV = null;
			  proteinCellcomboBox.removeAllItems();
			  throw e;
		  }
	}

	private TreeSet<String[]> readCellProteinListExperimental(AsynchProgressPopup pp) throws IOException{
		
		  WebClientInterface wci;
		  threadState = Thread.currentThread();
		  wci = new WebClientInterface(thisPanel, userPreferences,pp);
		  wci.requestCellProteinListExperimental();
		  wci.handshake();
		  if(wci.isExpired()) {
				System.out.println("Fetch request has expired");
				return null;
			}
		  
		  org.jdom.Element element = cbit.util.xml.XmlUtil.stringToXML(wci.getDoc().toString(), null);
		  org.vcell.cellml.JDOMTreeWalker jdtw = new org.vcell.cellml.JDOMTreeWalker(
				element, new org.jdom.filter.Filter() {
				public boolean matches(java.lang.Object obj) {
				//System.out.println(obj);
					if (obj instanceof org.jdom.Element
							&& ((org.jdom.Element) obj).getName()
							.equals("target_cell_name_result")) {
						return true;
						}
					return false;
					}
				});
		  TreeSet<String[]> sortedCellProteinTreeSet = new TreeSet<String[]>(
				new Comparator<String[]>() {
					public int compare(String[] o1, String[] o2) {
						if (o1[0].equals(o2[0])) {
							return o1[1].compareToIgnoreCase(o2[1]);
						}
						return o1[0].compareToIgnoreCase(o2[0]);
					}
				});
		  while (jdtw.hasNext()) {
			  org.jdom.Element ele = (org.jdom.Element) jdtw.next();
			  String[] cellProteinPair = new String[] {
					ele.getChild("cell_name").getTextTrim(),
					ele.getChild("target").getTextTrim(),
					CELL_PROTEIN_COLLECTED};
			  sortedCellProteinTreeSet.add(cellProteinPair);
		  }
		  return sortedCellProteinTreeSet;
	}
	
	
	private TreeSet<String[]> readCellProteinListGenerated(AsynchProgressPopup pp) throws IOException{

		WebClientInterface wci;
		threadState = Thread.currentThread();
		wci = new WebClientInterface(thisPanel, userPreferences,pp);
		wci.requestCellProteinListGenerated();
		wci.handshake();
		if(wci.isExpired()) {
			System.out.println("Fetch request has expired");
			return null;
		}
		org.jdom.Element element = cbit.util.xml.XmlUtil.stringToXML(wci.getDoc().toString(), null);
		org.vcell.cellml.JDOMTreeWalker jdtw = new org.vcell.cellml.JDOMTreeWalker(
					element, new org.jdom.filter.Filter() {
						public boolean matches(java.lang.Object obj) {
							//System.out.println(obj);
							if (obj instanceof org.jdom.Element
									&& ((org.jdom.Element) obj).getName()
											.equals("gen_model_result")) {
								return true;
							}
							return false;
						}
					});
		TreeSet<String[]> sortedGeneratedModelTreeSet = new TreeSet<String[]>(
					new Comparator<String[]>() {
						public int compare(String[] o1, String[] o2) {
							if (o1[0].equals(o2[0])) {
								return o1[1].compareToIgnoreCase(o2[1]);
							}
							return o1[0].compareToIgnoreCase(o2[0]);
						}
					});
		while (jdtw.hasNext()) {
				org.jdom.Element ele = (org.jdom.Element) jdtw.next();
				String[] cellProteinPair = new String[] {
						"Cell",
						ele.getChild("gen_model").getTextTrim(),
						CELL_PROTEIN_GENERATED};
				sortedGeneratedModelTreeSet.add(cellProteinPair);
		}
		return sortedGeneratedModelTreeSet;
	}

	private void initCellProteinImageInfo(){
		
		int selectedIndex = proteinCellcomboBox.getSelectedIndex();
		final String[] cell_proteinArr = sortedCellProtenV.elementAt(selectedIndex);
		
		if(cell_proteinArr[KIND_INDEX].equals(CELL_PROTEIN_GENERATED)){
			initGeneratedCellProteinImageInfo();
		} else {
			initExperimentalCellProteinImageInfo();
		}
	}

	private void initGeneratedCellProteinImageInfo() {

		int selectedIndex = proteinCellcomboBox.getSelectedIndex();
		final String[] cell_proteinArr = sortedCellProtenV.elementAt(selectedIndex);

	    imageID_ProteinImageURL_Hash = null;
	    imageID_CompartmentImageURL_Hash = null;
	    imageID_DimHash = null;
	    imageID_PixelSizeHash = null;
	    imageID_ProtocolTitle_Hash = null;
		imageID_MicroscopeName_Hash = null;
		lastQueriedPSLIDCellProteinSeries = proteinCellcomboBox.getSelectedItem();
		imageIDcomboBox.removeAllItems();
		imageIDcomboBox.addItem("Generated Cell/Protein Data");		// hardcoded for now
		return;
	}
	
	private void initExperimentalCellProteinImageInfo() {
	
		try{
			int selectedIndex = proteinCellcomboBox.getSelectedIndex();
			final String[] cell_proteinArr = sortedCellProtenV.elementAt(selectedIndex);
	
			final Thread[] pslidThread = new Thread[1];
			final AsynchProgressPopup[] pp = new AsynchProgressPopup[1];
			pp[0] =
				new AsynchProgressPopup(
					this,"Query PSLID ("+cell_proteinArr[0]+"/"+cell_proteinArr[1]+")","",true, true,
					true,
					new ProgressDialogListener(){
						public void cancelButton_actionPerformed(EventObject newEvent) {
							pp[0].stop();
							pslidThread[0].interrupt();
							stopThread();
						}
					}
				);
			SwingUtilities.invokeLater(new Runnable(){public void run() {pp[0].startKeepOnTop();}});
			pslidThread[0] =
				new Thread(
					new Runnable(){
						public void run() {
	
							java.io.InputStream is = null;
							try {
							    imageIDcomboBox.removeAllItems();
							    imageID_ProteinImageURL_Hash = new TreeMap<Integer, String>();
							    imageID_CompartmentImageURL_Hash = new TreeMap<Integer, String>();
							    imageID_DimHash = new TreeMap<Integer, ISize>();
							    imageID_PixelSizeHash = new TreeMap<Integer, Coordinate>();
							    imageID_ProtocolTitle_Hash = new TreeMap<Integer, String>();
							    imageID_MicroscopeName_Hash = new TreeMap<Integer, String>();
							    
								WebClientInterface wci;
								threadState = Thread.currentThread();
								wci = new WebClientInterface(thisPanel, userPreferences,pp[0]);
								wci.requestProteinCellDetails(cell_proteinArr[PROTEIN_INDEX], cell_proteinArr[CELL_INDEX]);
								wci.handshake();
								if(wci.isExpired()) {
									System.out.println("Fetch request has expired");
									return;
								}

							    org.jdom.Element element = cbit.util.xml.XmlUtil.stringToXML(wci.getDoc().toString(),null);
							    org.vcell.cellml.JDOMTreeWalker jdtw =
							    	  	new org.vcell.cellml.JDOMTreeWalker(element,
							    			new org.jdom.filter.Filter(){
												public boolean matches(java.lang.Object obj){
													if(	obj instanceof org.jdom.Element &&
														((org.jdom.Element)obj).getName().equals("search_result")){
														return true;
													}
													return false;
												}
											}
							    		);
							      
							    while(jdtw.hasNext()){
							    	  org.jdom.Element ele = (org.jdom.Element)jdtw.next();
							    	  String imageID = ele.getChild("image_id").getTextTrim();
							    	  String imageURL = ele.getChild("image_url").getTextTrim();
							    	  String proteinImageURL = ele.getChild("protein_image_url").getTextTrim();
							    	  String compartmentImageURL = ele.getChild("compartment_image_url").getTextTrim();
							    	  String regionMaskURL = ele.getChild("region_mask_url").getTextTrim();
							    	  int xDim = new Integer(ele.getChild("x_dim").getTextTrim()).intValue();
							    	  int yDim = new Integer(ele.getChild("y_dim").getTextTrim()).intValue();
							    	  int zDim = new Integer(ele.getChild("z_dim").getTextTrim()).intValue();
							    	  double xPixSize = new Double(ele.getChild("x_pixel_size").getTextTrim()).doubleValue();
							    	  double yPixSize = new Double(ele.getChild("y_pixel_size").getTextTrim()).doubleValue();
							    	  double zPixSize = new Double(ele.getChild("z_pixel_size").getTextTrim()).doubleValue();
							    	  
							    	  int stackPos = new Integer(ele.getChild("stack_pos").getTextTrim()).intValue();
							    	  String cellName = ele.getChild("cell_name").getTextTrim();					// HeLa
							    	  String cellOrganism = ele.getChild("cell_organism").getTextTrim();			// Homo Sapiens
							    	  String target = ele.getChild("target").getTextTrim();							// LAMP2
							    	  String segmenter = ele.getChild("segmenter").getTextTrim();
							    	  String experimentTitle = ele.getChild("exp_title").getTextTrim();				// Hela-h4b4
							    	  String experimentURL = ele.getChild("experiment_url").getTextTrim();
							    	  String protocolTitle = ele.getChild("protocol_title").getTextTrim();			// Immunofluorescence Labelling of HeLa cells
							    	  String protocolURL = ele.getChild("protocol_url").getTextTrim();
							    	  String microscopeName = ele.getChild("microscope_name").getTextTrim();		// ZEISS AXIOVERT
							    	  String microscopyFilter = ele.getChild("microscopy_filter_url").getTextTrim();
							    	  
							    	  imageID_ProteinImageURL_Hash.put(new Integer(imageID), proteinImageURL);
							    	  imageID_ProteinImageURL_Hash.put(new Integer(imageID), proteinImageURL);
							    	  imageID_CompartmentImageURL_Hash.put(new Integer(imageID), compartmentImageURL);
							    	  imageID_DimHash.put(new Integer(imageID),new ISize(xDim,yDim,zDim));
							    	  imageID_PixelSizeHash.put(new Integer(imageID),new Coordinate(xPixSize,yPixSize,zPixSize));
							    	  imageID_ProtocolTitle_Hash.put(new Integer(imageID), protocolTitle);
							    	  imageID_MicroscopeName_Hash.put(new Integer(imageID), microscopeName);
					//		    	  System.out.println(imageID);
					//		    	  System.out.println("  "+imageURL);
					//		    	  System.out.println("  "+proteinImageURL);
					//		    	  System.out.println("  "+compartmentImageURL);
					//		    	  System.out.println("  "+regionMaskURL);
					//		    	  System.out.println();
							      }
							      if(imageID_ProteinImageURL_Hash.size() == 0){
							    	  throw new Exception("No Image entries found");
							      }
							      Set<Integer> idSet = imageID_ProteinImageURL_Hash.keySet();
							      Iterator<Integer> idIter = idSet.iterator();
							      while(idIter.hasNext()){
							    	  imageIDcomboBox.addItem(idIter.next());
							      }
							      imageIDcomboBox.setSelectedIndex(0);
							      lastQueriedPSLIDCellProteinSeries = proteinCellcomboBox.getSelectedItem();
							} catch (Exception e) {
								lastQueriedPSLIDCellProteinSeries = null;
								  if(is != null){
									  try{
										  is.close();
									  }catch(IOException e2){
										  //do Nothing}
									  }
								  }
								  pp[0].stop();
								  if(!(e instanceof InterruptedException)){
									  PopupGenerator.showErrorDialog("Error getting Image information for cell/protein pair\n"+
										(cell_proteinArr!= null?"\""+cell_proteinArr[0]+"\"":"?")+"/"+(cell_proteinArr!= null?"\""+cell_proteinArr[1]+"\"":"?")+"\n"+
										e.getMessage());
								  }
							}finally{
								pp[0].stop();
								guiState();
							}
						}
					}
				);
			pslidThread[0].start();
		}catch(Exception e){
			PopupGenerator.showErrorDialog(e.getMessage());
		}
	}
	
	private void downloadPSLIDData(){

		if(sortedCellProtenV.elementAt(proteinCellcomboBox.getSelectedIndex())[KIND_INDEX].equals(CELL_PROTEIN_GENERATED)){
			downloadGeneratedCellProteinData();
		} else {
			downloadExperimentalCellProteinData();
		}
	}

	// download generated image
	private void downloadGeneratedCellProteinData(){

		final Thread[] pslidThread = new Thread[1];
		final AsynchProgressPopup[] pp = new AsynchProgressPopup[1];
		pp[0] = new AsynchProgressPopup(this, "Downloading PSLID Image Set (generated)", "", true, true, true,
				new ProgressDialogListener() {
					public void cancelButton_actionPerformed(EventObject newEvent) {
						pp[0].stop();
						pslidThread[0].interrupt();
					}
				});
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pp[0].startKeepOnTop();
			}
		});
		pslidThread[0] =
			new Thread(
				new Runnable() {
					public void run() {
						InputStream is = null;
						FileOutputStream fos = null;
						try{
						    imageID_ProteinImageURL_Hash = new TreeMap<Integer, String>();
						    imageID_CompartmentImageURL_Hash = new TreeMap<Integer, String>();
							final String baseURL1 = userPreferences.getSystemClientProperty(Preference.SYSCLIENT_pslidCellProteinImageGenURL_1);
							final String baseURL2 = userPreferences.getSystemClientProperty(Preference.SYSCLIENT_pslidCellProteinImageGenURL_2);

							URL generatedCellProteinImageURL =
								new URL( baseURL1 + 	// "http://pslid.cbi.cmu.edu/tcnp/genmodel_TCNP.jsp?protset1="+
									sortedCellProtenV.elementAt(proteinCellcomboBox.getSelectedIndex())[PROTEIN_INDEX]+
									baseURL2);			// "&selectset2=using&settype=regionset&settitle=2d+region+set&task=genmodel&table=tblregion_Sets&setnum=2&multisel=0&next=Continue");

							imageID_ProteinImageURL_Hash.put(new Integer(0), generatedCellProteinImageURL.toString());
							imageID_CompartmentImageURL_Hash.put(new Integer(0),generatedCellProteinImageURL.toString());

							WebClientInterface wci = new WebClientInterface(thisPanel, userPreferences,pp[0]);
							threadState = Thread.currentThread();
							wci.requestImage(generatedCellProteinImageURL.toString());
							wci.handshake();
							if(wci.isExpired()) {
								System.out.println("Fetch request has expired");
								return;
							}
					        File fileP = new File(wci.getDoc().toString());
					        byte[] generatedCellProteinImage = WebClientInterface.getBytesFromFile(fileP);
							
							File generatedImageFile = File.createTempFile("pslidGenerated", ".tif");
							generatedImageFile.deleteOnExit();
							fos = new FileOutputStream(generatedImageFile);
							fos.write(generatedCellProteinImage);
							fos.close();
							loci.formats.ImageReader generatedImageReader = new loci.formats.ImageReader();
							loci.formats.IFormatReader generatedFormatReader = generatedImageReader.getReader(generatedImageFile.getAbsolutePath());
							generatedFormatReader.setId(generatedImageFile.getAbsolutePath());
							short[][] generatedChannels = ImageTools.getShorts(generatedFormatReader.openImage(0));
							final int PROTEIN_CHANNEL = 1;
							final int CELL_CHANNEL = 2;
							final int NUCLEUS_CHANNEL = 0;
							final int EXTRACELLULAR_PIXEL_VAL = 128;
							final int CELL_PIXEL_VAL = 196;
							final int NUCLEUS_PIXEL_VAL = 255;
							short[] proteinShorts = generatedChannels[PROTEIN_CHANNEL];
							short[] compartmentShorts = new short[proteinShorts.length];
							for (int i = 0; i < compartmentShorts.length; i++) {
								compartmentShorts[i] =
									(generatedChannels[NUCLEUS_CHANNEL][i] != 0?(short)NUCLEUS_PIXEL_VAL:(generatedChannels[CELL_CHANNEL][i] != 0?(short)CELL_PIXEL_VAL:(short)0) );
							}
							int xsize_uncrop = generatedFormatReader.getSizeX();
							int ysize_uncrop = generatedFormatReader.getSizeY();

							//Fill
							int MASTER_INDEX = 0;
							int XINDEX = 1;
							int YINDEX = 2;
							compartmentShorts[0] = EXTRACELLULAR_PIXEL_VAL;
							for(int i=0;i<3;i+= 1){
								pp[0].setMessage("Filling "+(i==0?"outside":(i==1?"cell":"nucleus"))+" of Generated Cell/Protein Image)...");
								short FILL_FLAG = (i==0?(short)EXTRACELLULAR_PIXEL_VAL:(i==1?(short)CELL_PIXEL_VAL:(short)NUCLEUS_PIXEL_VAL));
								Vector<int[]> pendingChecks = new Vector<int[]>();
								for (int j = 0; j < compartmentShorts.length; j++) {
									if(compartmentShorts[j] == FILL_FLAG && compartmentShorts[j+1] == 0){
										pendingChecks.add(new int[] {j,j%xsize_uncrop,j/xsize_uncrop});
										break;
									}
								}
								while(pendingChecks.size() > 0){
									int[] data = pendingChecks.remove(0);
										if(data[XINDEX]-1 >= 0 && compartmentShorts[data[MASTER_INDEX]-1] == 0){
											pendingChecks.add(new int[] {data[MASTER_INDEX]-1,data[XINDEX]-1,data[YINDEX]});
											compartmentShorts[data[MASTER_INDEX]-1] = FILL_FLAG;
										}
										if(data[YINDEX]-1 >= 0 && compartmentShorts[data[MASTER_INDEX]-xsize_uncrop] == 0){
											pendingChecks.add(new int[] {data[MASTER_INDEX]-xsize_uncrop,data[XINDEX],data[YINDEX]-1});
											compartmentShorts[data[MASTER_INDEX]-xsize_uncrop] = FILL_FLAG;
										}
										if(data[XINDEX]+1 < xsize_uncrop && compartmentShorts[data[MASTER_INDEX]+1] == 0){
											pendingChecks.add(new int[] {data[MASTER_INDEX]+1,data[XINDEX]+1,data[YINDEX]});
											compartmentShorts[data[MASTER_INDEX]+1] = FILL_FLAG;
										}
										if(data[YINDEX]+1 < ysize_uncrop && compartmentShorts[data[MASTER_INDEX]+xsize_uncrop] == 0){
											pendingChecks.add(new int[] {data[MASTER_INDEX]+xsize_uncrop,data[XINDEX],data[YINDEX]+1});
											compartmentShorts[data[MASTER_INDEX]+xsize_uncrop] = FILL_FLAG;
										}
								}
							}
							
//							BufferedImage tempProtImage = ImageTools.makeImage(proteinShorts,xsize_uncrop,ysize_uncrop);
//							tempProtImage = ImageTools.scale2D(tempProtImage, xsize_uncrop/2, ysize_uncrop/2, Image.SCALE_REPLICATE,tempProtImage.getColorModel());
//							proteinShorts = ImageTools.getShorts(tempProtImage)[0];
//							BufferedImage tempCompImage = ImageTools.makeImage(compartmentShorts,xsize_uncrop,ysize_uncrop);
//							tempCompImage = ImageTools.scale2D(tempCompImage, xsize_uncrop/2, ysize_uncrop/2, Image.SCALE_REPLICATE,tempCompImage.getColorModel());
//							compartmentShorts = ImageTools.getShorts(tempCompImage)[0];
//							xsize_uncrop = tempCompImage.getWidth();
//							ysize_uncrop = tempCompImage.getHeight();
							//Scale
							int scaleIndex = 0;
							short[] proteinScaleShorts = new short[xsize_uncrop/2 * ysize_uncrop/2];
							short[] compartmentScaleShorts = new short[proteinScaleShorts.length];
							for (int y = 0; y < ysize_uncrop; y+= 2) {
								for (int x = 0; x < xsize_uncrop; x+= 2) {
									proteinScaleShorts[scaleIndex] = proteinShorts[y*xsize_uncrop+x];
									compartmentScaleShorts[scaleIndex] = compartmentShorts[y*xsize_uncrop+x];
									scaleIndex++;
								}
							}
							proteinShorts = proteinScaleShorts;
							compartmentShorts = compartmentScaleShorts;
							xsize_uncrop = xsize_uncrop/2;
							ysize_uncrop = ysize_uncrop/2;
							
							
							//Min Bounding
							int minx = xsize_uncrop;
							int miny = ysize_uncrop;
							int maxx = 0;
							int maxy = 0;
							for (int i = 0; i < compartmentShorts.length; i++) {
								if(compartmentShorts[i] == CELL_PIXEL_VAL){
									minx = Math.min(minx,i%xsize_uncrop);
									miny = Math.min(miny,i/xsize_uncrop);
									maxx = Math.max(maxx,i%xsize_uncrop);
									maxy = Math.max(maxy,i/xsize_uncrop);
								}
							}
							//Pad
							minx = Math.max(minx-3,0);
							miny = Math.max(miny-3,0);
							maxx = Math.min(maxx+3,xsize_uncrop);
							maxy = Math.min(maxy+3,ysize_uncrop);
							//Crop
							int cropIndex = 0;
							short[] proteinCropShorts = new short[(maxx-minx+1) * (maxy-miny+1)];
							short[] compartmentCropShorts = new short[proteinCropShorts.length];
							for (int y = 0; y < ysize_uncrop; y++) {
								for (int x = 0; x < xsize_uncrop; x++) {
									if(y>= miny && y<= maxy && x>= minx && x <= maxx){
										proteinCropShorts[cropIndex] = proteinShorts[y*xsize_uncrop+x];
										compartmentCropShorts[cropIndex] = compartmentShorts[y*xsize_uncrop+x];
										cropIndex++;
									}
								}
							}
							final int xsize = maxx-minx+1;
							final int ysize = maxy-miny+1;
							
							FieldDataFileOperationSpec fdos_composite = new FieldDataFileOperationSpec();
							fdos_composite.variableTypes = new VariableType[] {VariableType.VOLUME,VariableType.VOLUME };
							fdos_composite.varNames = new String[] { "protein_"+"generated","compartment_"+"generated" };
							fdos_composite.shortSpecData = new short[][][] {{proteinCropShorts,compartmentCropShorts}};
							fdos_composite.times = new double[] { 0 };
							fdos_composite.origin = new Origin(0,0,0);
							fdos_composite.extent = new Extent(10,10,.5);
							fdos_composite.isize = new ISize(xsize,ysize,1);
							fdos_composite.annotation = "Protocol: ";
							selectedFDOS = fdos_composite;
							
							SwingUtilities.invokeLater(new Runnable(){
								public void run() {
									xsizeLabel_1.setText(xsize+"");
									ysizeLabel_1.setText(ysize+"");
									zsizeLabel_1.setText(1+"");
								}});
							//Make Display Image
							proteinDisplayImage =
								new java.awt.image.BufferedImage(xsize,ysize,java.awt.image.BufferedImage.TYPE_INT_ARGB);
							compartmentDisplayImage =
								new java.awt.image.BufferedImage(xsize,ysize,java.awt.image.BufferedImage.TYPE_INT_ARGB);
							int index=0;
							for (int y = 0; y < ysize; y++) {
								for (int x = 0; x < xsize; x++) {
									proteinDisplayImage.setRGB(x, y, 0xFF000000 |
											(0x000000FF & proteinCropShorts[index]) |
											(0x0000FF00 & proteinCropShorts[index]<<8) |
											(0x00FF0000 & proteinCropShorts[index]<<16)
									);
									compartmentDisplayImage.setRGB(x, y, 0xFF000000 |
											//(0x00FF0000 & compartmentCropShorts[index]<<16)
											(compartmentCropShorts[index] == EXTRACELLULAR_PIXEL_VAL?0x00000000:0x00000000) |
											(compartmentCropShorts[index] == CELL_PIXEL_VAL?0x00FF0000:0x00000000) |
											(compartmentCropShorts[index] == NUCLEUS_PIXEL_VAL?0x0000FF00:0x00000000)
									);
									index+= 1;
								}
							}
							lastDownloadedImageSetID = imageIDcomboBox.getSelectedItem();
							
						} catch (Exception e) {
							lastDownloadedImageSetID = null;
							pp[0].stop();
							e.printStackTrace();
							if (!(e instanceof InterruptedException)) {
								PopupGenerator.showErrorDialog(
									"Error downloading PSLID Image Set (generated).\n"
									+ e.getMessage());
							}
						} finally {
							if(is != null){try{is.close();}catch(Exception e){}}
							if(fos != null){try{fos.close();}catch(Exception e){}}
							pp[0].stop();
							guiState();
							getProteinRadioButton().doClick();
						}

					}
				}
			);
		pslidThread[0].start();
	}
	
	private void downloadExperimentalCellProteinData() {

try {
	final Integer selectedImageSet = (Integer)imageIDcomboBox.getSelectedItem();
//	final URL pslidProteinImageURL = new URL(imageID_ProteinImageURL_Hash.get(selectedImageSet));
//	final URL pslidCompartmentImageURL = new URL(imageID_CompartmentImageURL_Hash.get(selectedImageSet));
	final ISize pslidImageDim = imageID_DimHash.get(selectedImageSet);
	final Thread[] pslidThread = new Thread[1];
	final AsynchProgressPopup[] pp = new AsynchProgressPopup[1];
	pp[0] = new AsynchProgressPopup(this, "Downloading PSLID Image Set ("
			+ imageIDcomboBox.getSelectedItem() + ")", "", true, true, true,
			new ProgressDialogListener() {
				public void cancelButton_actionPerformed(EventObject newEvent) {
					pp[0].stop();
					pslidThread[0].interrupt();
				}
			});
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			pp[0].startKeepOnTop();
		}
	});
	pslidThread[0] = new Thread(new Runnable() {
		public void run() {

			InputStream is = null;		
			try {
				WebClientInterface wci = new WebClientInterface(thisPanel, userPreferences,pp[0]);
				threadState = Thread.currentThread();
				wci.requestImage(imageID_ProteinImageURL_Hash.get(selectedImageSet));
				wci.handshake();
				if(wci.isExpired()) {
					System.out.println("Fetch request has expired");
					return;
				}
		        File fileP = new File(wci.getDoc().toString());
		        byte[] selectedProteinImage = WebClientInterface.getBytesFromFile(fileP);
		        
				wci = new WebClientInterface(thisPanel, userPreferences,pp[0]);
				threadState = Thread.currentThread();
				wci.requestImage(imageID_CompartmentImageURL_Hash.get(selectedImageSet));
				wci.handshake();
				if(wci.isExpired()) {
					System.out.println("Fetch request has expired");
					return;
				}
				File fileC = new File(wci.getDoc().toString());
		        byte[] selectedCompartmentImage = WebClientInterface.getBytesFromFile(fileC);
				
//				ISize isize = imageID_DimHash.get(imageIDcomboBox.getSelectedItem());
				Coordinate pixelSizes = imageID_PixelSizeHash.get(imageIDcomboBox.getSelectedItem());
				
				final String[] cell_proteinArr = sortedCellProtenV.elementAt(proteinCellcomboBox.getSelectedIndex());
				final FieldDataFileOperationSpec fdos =
					PSLIDPanel.createCompositeFDOS(selectedProteinImage, selectedCompartmentImage,
							pixelSizes,cell_proteinArr[0]+"_"+cell_proteinArr[1]+"_"+selectedImageSet.toString());
				
				String protocolTitle = imageID_ProtocolTitle_Hash.get(imageIDcomboBox.getSelectedItem());
				String microscopeName = imageID_MicroscopeName_Hash.get(imageIDcomboBox.getSelectedItem());
				fdos.annotation = "Protocol: " + protocolTitle + '\n' + "Microscope Name: "+ microscopeName;
				selectedFDOS = fdos;
				
				proteinDisplayImage =
					new java.awt.image.BufferedImage(fdos.isize.getX(),fdos.isize.getY(),java.awt.image.BufferedImage.TYPE_INT_ARGB);
				compartmentDisplayImage =
					new java.awt.image.BufferedImage(fdos.isize.getX(),fdos.isize.getY(),java.awt.image.BufferedImage.TYPE_INT_ARGB);
//				final ImageIcon imageIcon = new ImageIcon(proteinDisplayImage);

				int index = 0;
				double minProt = fdos.shortSpecData[0][0][0];
				double maxProt = minProt;
				double minComp = fdos.shortSpecData[0][1][0];
				double maxComp = minComp;
				for (int y = 0; y < fdos.isize.getY(); y++) {
					for (int x = 0; x < fdos.isize.getX(); x++) {
						minProt = Math.min(minProt,fdos.shortSpecData[0][0][index]);
						maxProt = Math.max(maxProt,fdos.shortSpecData[0][0][index]);
						minComp = Math.min(minComp,fdos.shortSpecData[0][1][index]);
						maxComp = Math.max(maxComp,fdos.shortSpecData[0][1][index]);
						index+= 1;
					}
				}
				
				//System.out.println(minProt+" "+maxProt+" "+minComp+" "+maxComp);
				double diff = (double)(maxProt-minProt);
				index = 0;
				int[] compValues = new int[]{0xFF000000,0xFFFF0000,0xFF00FF00};
				for (int y = 0; y < fdos.isize.getY(); y++) {
					for (int x = 0; x < fdos.isize.getX(); x++) {
						double scaleVal =
							(diff == 0?(double)fdos.shortSpecData[0][0][index]:((double)fdos.shortSpecData[0][0][index]-minProt)/diff*255.0);
						short protVal = (short)scaleVal;//fdos.shortSpecData[0][0][index];
						proteinDisplayImage.setRGB(x, y, 0xFF000000 |
								(0x000000FF & protVal) |
								(0x0000FF00 & protVal<<8) |
								(0x00FF0000 & protVal<<16)
						);
						compartmentDisplayImage.setRGB(x, y,compValues[fdos.shortSpecData[0][1][index]]);//0xFF000000 | (0x00FFFF00 & (fdos.shortSpecData[0][1][index]<<8)));
						index+= 1;
					}
				}

//				SwingUtilities.invokeLater(new Runnable(){
//				public void run() {
//					scrollPane.setViewportView(new JLabel(imageIcon));
//				}});
				
//				Image image = Toolkit.getDefaultToolkit().createImage(imageBytes);
//				final ImageIcon imageIcon = new ImageIcon(image);
//				SwingUtilities.invokeLater(new Runnable(){
//					public void run() {
//						scrollPane.setViewportView(new JLabel(imageIcon));
//					}});
				lastDownloadedImageSetID = imageIDcomboBox.getSelectedItem();
			} catch (Exception e) {
				lastDownloadedImageSetID = null;
				pp[0].stop();
				e.printStackTrace();
				if (!(e instanceof InterruptedException)) {
					PopupGenerator
							.showErrorDialog("Error downloading PSLID Image Set ("+selectedImageSet+").\n"
									+ e.getMessage());
				}
			} finally {
				if(is != null){try{is.close();}catch(Exception e){}}
				pp[0].stop();
				guiState();
				getProteinRadioButton().doClick();
				if(selectedFDOS != null){
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							xsizeLabel_1.setText(selectedFDOS.isize.getX()+"");
							ysizeLabel_1.setText(selectedFDOS.isize.getY()+"");
							zsizeLabel_1.setText(selectedFDOS.isize.getZ()+"");
					}});
				}
			}
		}
	});
	pslidThread[0].start();
} catch (Exception e) {
		PopupGenerator.showErrorDialog("Error downloading\n"+e.getMessage());
}
	}
	
	
	/**
	 * @return
	 */
	protected JLabel getImageIdLabel() {
		if (imageIdLabel == null) {
			imageIdLabel = new JLabel();
			imageIdLabel.setText("Image Set Identifier");
		}
		return imageIdLabel;
	}
	/**
	 * @return
	 */
	protected JComboBox getImageIDcomboBox() {
		if (imageIDcomboBox == null) {
			imageIDcomboBox = new JComboBox();
			imageIDcomboBox.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if(imageIDcomboBox.getSelectedItem() == lastDownloadedImageSetID){
						return;
					}
					scrollPane.setViewportView(null);
					guiState();
				}
			});
		}
		return imageIDcomboBox;
	}
	/**
	 * @return
	 */
	protected JPanel getPanelControls() {
		if (panelControls == null) {
			panelControls = new JPanel();
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] {0,7,7,7,7,7,7,7};
			panelControls.setLayout(gridBagLayout);
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
			panelControls.add(getXsizeLabel(), gridBagConstraints_1);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 1;
			panelControls.add(getXsizeLabel_1(), gridBagConstraints);
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints_2.gridy = 0;
			gridBagConstraints_2.gridx = 2;
			panelControls.add(getYsizeLabel(), gridBagConstraints_2);
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints_3.gridy = 0;
			gridBagConstraints_3.gridx = 3;
			panelControls.add(getYsizeLabel_1(), gridBagConstraints_3);
			final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
			gridBagConstraints_4.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints_4.gridy = 0;
			gridBagConstraints_4.gridx = 4;
			panelControls.add(getZsizeLabel(), gridBagConstraints_4);
			final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
			gridBagConstraints_5.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints_5.gridy = 0;
			gridBagConstraints_5.gridx = 5;
			panelControls.add(getZsizeLabel_1(), gridBagConstraints_5);
			final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
			gridBagConstraints_6.gridy = 0;
			gridBagConstraints_6.gridx = 7;
			final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
			gridBagConstraints_7.weightx = 1;
			gridBagConstraints_7.gridy = 0;
			gridBagConstraints_7.gridx = 6;
			panelControls.add(getPanel_2(), gridBagConstraints_7);
			panelControls.add(getPanel_1(), gridBagConstraints_6);
		}
		return panelControls;
	}
	
	public PSLIDSelectionInfo getPSLIDSelectionInfo() throws DataAccessException{
//		if(selectedCompartmentImage == null || selectedProteinImage == null){
//			throw new DataAccessException("No PSLID image set initialized");
//		}
		if(selectedFDOS == null){
			throw new DataAccessException("No PSLID image set initialized");
		}
		PSLIDSelectionInfo pslidSelInfo = new PSLIDSelectionInfo();
		pslidSelInfo.cellName = sortedCellProtenV.elementAt(proteinCellcomboBox.getSelectedIndex())[CELL_INDEX];
		pslidSelInfo.proteinName = sortedCellProtenV.elementAt(proteinCellcomboBox.getSelectedIndex())[PROTEIN_INDEX];
		if(sortedCellProtenV.elementAt(proteinCellcomboBox.getSelectedIndex())[KIND_INDEX].equals(CELL_PROTEIN_COLLECTED)){
			pslidSelInfo.imageSetID = imageIDcomboBox.getSelectedItem().toString();
			pslidSelInfo.proteinImageURL = imageID_ProteinImageURL_Hash.get(imageIDcomboBox.getSelectedItem());
			pslidSelInfo.compartmentImageURL = imageID_CompartmentImageURL_Hash.get(imageIDcomboBox.getSelectedItem());
			pslidSelInfo.protocolTitle = imageID_ProtocolTitle_Hash.get(imageIDcomboBox.getSelectedItem());
			pslidSelInfo.microscopeName = imageID_MicroscopeName_Hash.get(imageIDcomboBox.getSelectedItem());
		}else{
			pslidSelInfo.imageSetID = "generated";
			pslidSelInfo.proteinImageURL = imageID_ProteinImageURL_Hash.get(new Integer(0));
			pslidSelInfo.compartmentImageURL = imageID_CompartmentImageURL_Hash.get(new Integer(0));
			pslidSelInfo.protocolTitle = "";
			pslidSelInfo.microscopeName = "";
		}
		pslidSelInfo.fdos = selectedFDOS;
//		pslidSelInfo.proteinImage = selectedProteinImage;
//		pslidSelInfo.compartmentImage = selectedCompartmentImage;
//		pslidSelInfo.isize = imageID_DimHash.get(imageIDcomboBox.getSelectedItem());
//		pslidSelInfo.origin = new Origin(0,0,0);
//		Coordinate tempCoord = imageID_PixelSizeHash.get(imageIDcomboBox.getSelectedItem());
//		pslidSelInfo.extent =
//			new Extent(pslidSelInfo.isize.getX()*tempCoord.getX(),
//					pslidSelInfo.isize.getY()*tempCoord.getY(),
//					(tempCoord.getZ() == 0?.5:pslidSelInfo.isize.getZ()*tempCoord.getZ()));

		return pslidSelInfo;
	}
	
	/**
	 * @return
	 */
	protected JLabel getXsizeLabel() {
		if (xsizeLabel == null) {
			xsizeLabel = new JLabel();
			xsizeLabel.setText("Xsize:");
		}
		return xsizeLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getXsizeLabel_1() {
		if (xsizeLabel_1 == null) {
			xsizeLabel_1 = new JLabel();
			xsizeLabel_1.setText("Xsize");
		}
		return xsizeLabel_1;
	}
	/**
	 * @return
	 */
	protected JLabel getYsizeLabel() {
		if (ysizeLabel == null) {
			ysizeLabel = new JLabel();
			ysizeLabel.setText("Ysize:");
		}
		return ysizeLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getYsizeLabel_1() {
		if (ysizeLabel_1 == null) {
			ysizeLabel_1 = new JLabel();
			ysizeLabel_1.setText("Ysize:");
		}
		return ysizeLabel_1;
	}
	/**
	 * @return
	 */
	protected JLabel getZsizeLabel() {
		if (zsizeLabel == null) {
			zsizeLabel = new JLabel();
			zsizeLabel.setText("Zsize:");
		}
		return zsizeLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getZsizeLabel_1() {
		if (zsizeLabel_1 == null) {
			zsizeLabel_1 = new JLabel();
			zsizeLabel_1.setText("Zsize:");
		}
		return zsizeLabel_1;
	}

	/**
	 * @return
	 */
	protected JButton getQueryButton() {
		if (queryButton == null) {
			queryButton = new JButton();
			queryButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					initCellProteinImageInfo();
				}
			});
			queryButton.setText("Query Now...");
		}
		return queryButton;
	}
	/**
	 * @return
	 */
	protected JButton getDownloadButton() {
		if (downloadButton == null) {
			downloadButton = new JButton();
			downloadButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					downloadPSLIDData();
				}
			});
			downloadButton.setText("Download...");
		}
		return downloadButton;
	}
	/**
	 * @return
	 */
	protected JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] {0,7};
			panel_1.setLayout(gridBagLayout);
			panel_1.add(getProteinRadioButton(), new GridBagConstraints());
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 1;
			panel_1.add(getCompartmentRadioButton(), gridBagConstraints);
		}
		return panel_1;
	}
	/**
	 * @return
	 */
	protected JRadioButton getProteinRadioButton() {
		if (proteinRadioButton == null) {
			proteinRadioButton = new JRadioButton();
			proteinRadioButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if(getProteinRadioButton().isSelected()){
						SwingUtilities.invokeLater(new Runnable(){public void run() {scrollPane.setViewportView(new JLabel(new ImageIcon(proteinDisplayImage)));}});
					}
				}
			});
			proteinRadioButton.setSelected(true);
			proteinRadioButton.setText("Protein");
		}
		return proteinRadioButton;
	}
	/**
	 * @return
	 */
	protected JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
		}
		return panel_2;
	}
	/**
	 * @return
	 */
	protected JRadioButton getCompartmentRadioButton() {
		if (compartmentRadioButton == null) {
			compartmentRadioButton = new JRadioButton();
			compartmentRadioButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if(getCompartmentRadioButton().isSelected()){
						SwingUtilities.invokeLater(new Runnable(){public void run() {scrollPane.setViewportView(new JLabel(new ImageIcon(compartmentDisplayImage)));}});
					}
				}
			});
			compartmentRadioButton.setText("Compartment");
		}
		return compartmentRadioButton;
	}
	/**
	 * @return
	 */

}
