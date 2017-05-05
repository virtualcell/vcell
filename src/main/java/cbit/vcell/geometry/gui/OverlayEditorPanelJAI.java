/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.vcell.util.BeanUtils;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.ISize;
import org.vcell.util.NumberUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.ColorIcon;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.gui.ROIMultiPaintManager.ComboboxROIName;
import cbit.vcell.graph.ZoomShapeIcon;
import cbit.vcell.graph.ZoomShapeIcon.Sign;
//comments added Jan 2008, this is the panel that displayed at the top of the FRAPDataPanel which deals with serials of images.
/**
 */
public class OverlayEditorPanelJAI extends JPanel{

	public static java.awt.Color[] CONTRAST_COLORS = new java.awt.Color[] {
		
		new Color(0xFF000000),//background
		
		new Color(0xFFFF0000),
		new Color(0xFF00FF00),
		new Color(0xFF0000FF),
		new Color(0xFFC9FA50),
		new Color(0xFFFF00FF),
		new Color(0xFF00FFFF),
		
		new Color(0xFFFF8080),
		new Color(0xFF80FF80),
		new Color(0xFFFFFF80),
		new Color(0xFFFDC2EB),
		new Color(0xFFFF80FF),
		new Color(0xFF80FFFF),
		
		new Color(0xFFFF4040),
		new Color(0xFF40FF40),
		new Color(0xFFFFFF40),
		new Color(0xFF630350),
		new Color(0xFFFF40FF),
		new Color(0xFF40FFFF),

		new Color(0xFF0C352A),
		new Color(0xFF014305),
		new Color(0xFFC3E4C8),
		new Color(0xFF3D260C),
		new Color(0xFFD5F82B),
		new Color(0xFF4900C9),
		new Color(0xFFE0D3BE),
		new Color(0xFF0A3668),
		new Color(0xFF84FADE),
		new Color(0xFF1B15FB),
		new Color(0xFFCEFB00),
		new Color(0xFF67068E),
		new Color(0xFFF6E20A),
		new Color(0xFF3D2A4D),
		new Color(0xFF9EFB63),
		new Color(0xFF76162A),
		new Color(0xFF8AFA93),
		new Color(0xFF0536C5),
		new Color(0xFFE6D06F),
		new Color(0xFF9A0D10),
		new Color(0xFFF4D22E),
		new Color(0xFF113994),
		new Color(0xFFBCD4A9),
		new Color(0xFF3B21A9),
		new Color(0xFFDEB5D7),
		new Color(0xFF5E2F03),
		new Color(0xFF49FDF1),
		new Color(0xFF741173),
		new Color(0xFFE8BF90),
		new Color(0xFF1A5B02),
		new Color(0xFF64FBB0),
		new Color(0xFF960485),
		new Color(0xFFA2E484),
		new Color(0xFF443188),
		new Color(0xFF6BE7FB),
		new Color(0xFF185557),
		new Color(0xFF89E0C8),
		new Color(0xFF543F37),
		new Color(0xFFA9E845),
		new Color(0xFF4028EF),
		new Color(0xFF98CFEF),
		new Color(0xFFAC0C64),
		new Color(0xFF98F52E),
		new Color(0xFF6A14E6),
		new Color(0xFFBAE711),
		new Color(0xFF822651),
		new Color(0xFF8DFF0D),
		new Color(0xFF3734D1),
		new Color(0xFFF6BA58),
		new Color(0xFFB5132B),
		new Color(0xFFD7C847),
		new Color(0xFF415220),
		new Color(0xFFDAD112),
		new Color(0xFF841B91),
		new Color(0xFFBCD25C),
		new Color(0xFFA200D7),
		new Color(0xFFD2B1B4),
		new Color(0xFFCC1303),
		new Color(0xFFA8B6FD),
		new Color(0xFF5E3C60),
		new Color(0xFF2CFFB4),
		new Color(0xFF016292),
		new Color(0xFFB1B8C5),
		new Color(0xFFAB05B0),
		new Color(0xFFC0A7EF),
		new Color(0xFF8F14BC),
		new Color(0xFFEA93E8),
		new Color(0xFF952F32),
		new Color(0xFF59F955),
		new Color(0xFF6C2AC0),
		new Color(0xFFF9B510),
		new Color(0xFFF40715),
		new Color(0xFF49F29A),
		new Color(0xFF1B6F3C),
		new Color(0xFF48EAC5),
		new Color(0xFF555E01),
		new Color(0xFFBDC62F),
		new Color(0xFF2B6070),
		new Color(0xFFB1BB8D),
		new Color(0xFF5B3CA7),
		new Color(0xFF11F6FA),
		new Color(0xFF81441F),
		new Color(0xFF74DC79),
		new Color(0xFF2D50C1),
		new Color(0xFF7EC3DA),
		new Color(0xFF0463D2),
		new Color(0xFFE8AB37),
		new Color(0xFFCA0C8F),
		new Color(0xFF68EA3C),
		new Color(0xFF2057E5),
		new Color(0xFF8ED34C),
		new Color(0xFF8C1EEF),
		new Color(0xFF8CC688),
		new Color(0xFFB20DF0),
		new Color(0xFFA3CF1E),
		new Color(0xFF0A8156),
		new Color(0xFF1DECD7),
		new Color(0xFFE90977),
		new Color(0xFFDD93A0),
		new Color(0xFFAE2399),
		new Color(0xFF59F116),
		new Color(0xFF16891D),
		new Color(0xFF38D4FD),
		new Color(0xFFC91E6E),
		new Color(0xFF69CAAB),
		new Color(0xFF415C99),
		new Color(0xFFF09360),
		new Color(0xFFBC15C4),
		new Color(0xFF00FFA2),
		new Color(0xFFE61A40),
		new Color(0xFFFB808F),
		new Color(0xFFCA3021),
		new Color(0xFF38F044),
		new Color(0xFF3C50FC),
		new Color(0xFFFE8E33),
		new Color(0xFFE206CD),
		new Color(0xFFD4AD00),
		new Color(0xFF963A8E),
		new Color(0xFF38E471),
		new Color(0xFFFC01B8),
		new Color(0xFFC5A15A),
		new Color(0xFF2F6FA8),
		new Color(0xFFFE6FB0),
		new Color(0xFF726721),
		new Color(0xFF0FF672),
		new Color(0xFFB64235),
		new Color(0xFF51D183),
		new Color(0xFFA05804),
		new Color(0xFF45C7D6),
		new Color(0xFF9E485C),
		new Color(0xFFE29A13),
		new Color(0xFF407D44),
		new Color(0xFF8AA8B4),
		new Color(0xFF905741),
		new Color(0xFF15E997),
		new Color(0xFF815186),
		new Color(0xFF9DB345),
		new Color(0xFF1AA200),
		new Color(0xFFABB806),
		new Color(0xFF6948FD),
		new Color(0xFF889EE4),
		new Color(0xFF893DDE),
		new Color(0xFFAD8FD3),
		new Color(0xFF676D4E),
		new Color(0xFF7ACB14),
		new Color(0xFFB02EDC),
		new Color(0xFFE16DD6),
		new Color(0xFFD3394E),
		new Color(0xFF85AE74),
		new Color(0xFFED380F),
		new Color(0xFF55AEF6),
		new Color(0xFF5F8017),
		new Color(0xFF1BF41B),
		new Color(0xFF7554C0),
		new Color(0xFF00FA34),
		new Color(0xFFDF23A8),
		new Color(0xFFCD818A),
		new Color(0xFFFE179C),
		new Color(0xFFC171FE),
		new Color(0xFF15A041),
		new Color(0xFF0DCFE8),
		new Color(0xFFF011EB),
		new Color(0xFF44D433),
		new Color(0xFF0886F2),
		new Color(0xFF57C356),
		new Color(0xFF0598A4),
		new Color(0xFF46BA9D),
		new Color(0xFF527C6E),
		new Color(0xFFB39D1B),
		new Color(0xFF616E8B),
		new Color(0xFFE76E80),
		new Color(0xFF6163DC),
		new Color(0xFF65A3BE),
		new Color(0xFF06A47D),
		new Color(0xFF8EAD1D),
		new Color(0xFF3F8585),
		new Color(0xFF11CDB9),
		new Color(0xFFC13EAA),
		new Color(0xFFE754F4),
		new Color(0xFF2E80E0),
		new Color(0xFF728DF0),
		new Color(0xFFB84E74),
		new Color(0xFFFF6255),
		new Color(0xFF5874B2),
		new Color(0xFFF25A98),
		new Color(0xFFBB5E1A),
		new Color(0xFF9C869A),
		new Color(0xFF00B550),
		new Color(0xFFAD8371),
		new Color(0xFF8F7B03),
		new Color(0xFFB073BA),
		new Color(0xFFBA36FE),
		new Color(0xFF11B6FF),
		new Color(0xFFDE3B85),
		new Color(0xFF7790AC),
		new Color(0xFFF23769),
		new Color(0xFF16D351),
		new Color(0xFF06BF23),
		new Color(0xFFEC711B),
		new Color(0xFF38A330),
		new Color(0xFF8C76F2),
		new Color(0xFF259C8F),
		new Color(0xFF04E60C),
		new Color(0xFF3989BA),
		new Color(0xFF649A93),
		new Color(0xFF966A6C),
		new Color(0xFF0AC98F),
		new Color(0xFFA05AAE),
		new Color(0xFF6FA92E),
		new Color(0xFF5473F8),
		new Color(0xFF3ACA10),
		new Color(0xFF459A5A),
		new Color(0xFFC759E2),
		new Color(0xFFD934E9),
		new Color(0xFF21B5AC),
		new Color(0xFF78910B),
		new Color(0xFFBF8109),
		new Color(0xFFA77336),
		new Color(0xFF00BBCB),
		new Color(0xFFB86652),
		new Color(0xFFD551B7),
		new Color(0xFFFE4D1E),
		new Color(0xFF1CA9D9),
		new Color(0xFF5E9841),
		new Color(0xFFA161E8),
		new Color(0xFF886FA3),
		new Color(0xFFD36167),
		new Color(0xFFE45545),
		new Color(0xFFEC41CB),
		new Color(0xFFF45B01),
		new Color(0xFFCF6B2F),
		new Color(0xFF47B400),
		new Color(0xFFC15E96),
		new Color(0xFF23B564),
		new Color(0xFFFFFF00),
		new Color(0xFF8080FF),
		new Color(0xFF4040FF)
};

	private static final long serialVersionUID = 1L;
	
	//properties
	public static final String FRAP_DATA_INIT_PROPERTY = "FRAP_DATA_INIT_PROPERTY";
	public static final String FRAP_DATA_DUPLICATE_PROPERTY = "FRAP_DATA_DUPLICATE_PROPERTY";
	public static final String FRAP_DATA_PAD_PROPERTY = "FRAP_DATA_PAD_PROPERTY";
	public static final String FRAP_DATA_CHANNEL_PROPERTY = "FRAP_DATA_CHANNEL_PROPERTY";
//	public static final String FRAP_DATA_ADDALLDISTINCT_PROPERTY = "FRAP_DATA_ADDALLDISTINCT_PROPERTY";
	public static final String FRAP_DATA_END_PROPERTY = "FRAP_DATA_END_PROPERTY";
	public static final String FRAP_DATA_FILL_PROPERTY = "FRAP_DATA_FILL_PROPERTY";
	public static final String FRAP_DATA_PAINTERASE_PROPERTY = "FRAP_DATA_PAINTERASE_PROPERTY";
	public static final String FRAP_DATA_CROP_PROPERTY = "FRAP_DATA_CROP_PROPERTY";
	public static final String FRAP_DATA_AUTOCROP_PROPERTY = "FRAP_DATA_AUTOCROP_PROPERTY";
	public static final String FRAP_DATA_TIMEPLOTROI_PROPERTY = "FRAP_DATA_TIMEPLOTROI_PROPERTY";
	public static final String FRAP_DATA_CURRENTROI_PROPERTY = "FRAP_DATA_CURRENTROI_PROPERTY";
	public static final String FRAP_DATA_UNDOROI_PROPERTY = "FRAP_DATA_UNDOROI_PROPERTY";
	public static final String FRAP_DATA_DELETEROI_PROPERTY = "FRAP_DATA_DELETEROI_PROPERTY";
	public static final String FRAP_DATA_ADDNEWROI_PROPERTY = "FRAP_DATA_ADDNEWROI_PROPERTY";
	public static final String FRAP_DATA_CLEARROI_PROPERTY = "FRAP_DATA_CLEARROI_PROPERTY";
	public static final String FRAP_DATA_BLEND_PROPERTY = "FRAP_DATA_BLEND_PROPERTY";
	public static final String FRAP_DATA_HISTOGRAM_PROPERTY = "FRAP_DATA_HISTOGRAM_PROPERTY";
	public static final String FRAP_DATA_HISTOUPDATEHIGHLIGHT_PROPERTY = "FRAP_DATA_HISTOUPDATEHIGHLIGHT_PROPERTY";
	public static final String FRAP_DATA_UPDATEROI_WITHHIGHLIGHT_PROPERTY = "FRAP_DATA_UPDATEROI_WITHHIGHLIGHT_PROPERTY";
	public static final String FRAP_DATA_UNDERLAY_SMOOTH_PROPERTY = "FRAP_DATA_UNDERLAY_SMOOTH_PROPERTY";
	public static final String FRAP_DATA_DISCARDHIGHLIGHT_PROPERTY = "FRAP_DATA_DISCARDHIGHLIGHT_PROPERTY";
	public static final String FRAP_DATA_PAINTERASE_FINISH_PROPERTY = "FRAP_DATA_PAINTERASE_FINISH_PROPERTY";
	public static final String FRAP_DATA_RESOLVEDHIGHLIGHT_PROPERTY = "FRAP_DATA_RESOLVEDHIGHLIGHT_PROPERTY";
	public static final String FRAP_DATA_RESOLVEDMERGE_PROPERTY = "FRAP_DATA_RESOLVEDMERGE_PROPERTY";
	public static final String FRAP_DATA_FINDROI_PROPERTY = "FRAP_DATA_FINDROI_PROPERTY";
	public static final String FRAP_DATA_SELECTIMGROI_PROPERTY = "FRAP_DATA_SELECTIMGROI_PROPERTY";
	public static final String FRAP_DATA_CONVERTDOMAIN_PROPERTY = "FRAP_DATA_CONVERTDOMAIN_PROPERTY";
	public static final String FRAP_DATA_SEPARATE_PROPERTY = "FRAP_DATA_SEPARATE_PROPERTY";

	//scale factors
	public static final double DEFAULT_SCALE_FACTOR = 1.0;
	public static final double DEFAULT_OFFSET_FACTOR = 0.0;
	private double originalScaleFactor = DEFAULT_SCALE_FACTOR;
	private double originalOffsetFactor = DEFAULT_OFFSET_FACTOR;
	private JComboBox roiComboBox;
	private JButton contrastButtonMinus;
	private JButton contrastButtonPlus;
	private JToggleButton cropButton;
	private JToggleButton fillButton;
	private JToggleButton eraseButton;
	private JToggleButton paintButton;
	private JButton clearROIbutton;
	private JButton autoCropButton;
	private OverlayImageDisplayJAI imagePane = null;
	private JSlider timeSlider = null;
	private ImageDataset imageDataset = null;
	private ROI highliteInfo = null;
	private StringBuffer sb = new StringBuffer();
	private JScrollPane jScrollPane2 = null;
	private JPanel toolButtonPanel = null;
	private JButton zoomInButton = null;
	private JButton zoomOutButton = null;
	private Color highlightColor = Color.yellow.darker();
	private JSlider zSlider = null;
	private ButtonGroup roiDrawButtonGroup = new ButtonGroup();
	private Point lastMousePoint = null;
	private JLabel textLabel = null;
	private JPanel editROIPanel = null;
	private JLabel viewTLabel;
	private JLabel viewZLabel;
	private JButton discardHighlightsButton;
	private JSlider blendPercentSlider;
	private JPanel blendPercentPanel;
	private JSlider smoothslider;
	private JPanel panel_3;
	private JLabel domainRegionLabel;
	private JButton mergeButton;
	private JButton borderToolButton;
	private JButton extrudeToolButton;
	private JLabel blendPercentImageLabel;
	private JLabel blendPercentROILabel;
	private JToggleButton selectButton;

	private static final String DOMAIN_LIST_TEXT = "Domain Regions";
	private ListSelectionListener resolvedListSelectionListener =
		new ListSelectionListener() {public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					resolvedListSelection(false);
				}
			}
		};

	private JButton addROIButton;
	private JButton delROIButton;
	private boolean bAllowAddROI = true;
		
	private BufferedImage[] allROICompositeImageArr;
		
	private JFileChooser openJFileChooser = new JFileChooser();
	public static class AllPixelValuesRange {
		private int allPixelValMin;
		private int allPixelValMax;
		private double scaleFactor;
		public AllPixelValuesRange(int min,int max){
			this.allPixelValMin = min;
			this.allPixelValMax = max;
			this.scaleFactor = (double)255/(double)max;
		}
		public int getMin(){
			return allPixelValMin;
		}
		public int getMax(){
			return allPixelValMax;
		}
		public double getScaleFactor(){
			return scaleFactor;
		}
	}
	private AllPixelValuesRange allPixelValuesRange;
	
	private Cursor paintCursor = createCursor("paint");
	private Cursor eraserCursor = createCursor("eraser");
	private Cursor fillCursor = createCursor("fill");
	private Cursor cropCursor = createCursor("crop");
	
	//ROI comboBox action
	ActionListener ROI_COMBOBOX_ACTIONLISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				ROIMultiPaintManager.ComboboxROIName selectedItem = ((ROIMultiPaintManager.ComboboxROIName)roiComboBox.getSelectedItem());
				if(selectedItem != null){
					delROIButton.setEnabled(selectedItem.isDeleteable());
					firePropertyChange(FRAP_DATA_CURRENTROI_PROPERTY, null,selectedItem.getROIName());
				}else{
					delROIButton.setEnabled(false);
					firePropertyChange(FRAP_DATA_CURRENTROI_PROPERTY, null,null);
				}
			}
		};
		private ActionListener addROIActionListener  = new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				firePropertyChange(FRAP_DATA_ADDNEWROI_PROPERTY, getAllCompositeROINamesAndColors(), (e.getSource() == OverlayEditorPanelJAI.this?e.getActionCommand():null));
			}
		};
		
	IndexColorModel greyIndexColorModel;
	/**
	 * This is the default constructor
	 */
	public OverlayEditorPanelJAI() {
		super();
		initialize();
		
		//colormap (grayscale)
		int[] cmap = new int[256];
		for(int i=0;i<cmap.length;i+= 1){
			int iv = (int)(0x000000FF&i);
			cmap[i] = 0xFF000000 | iv<<16 | iv<<8 | i;
		}

		greyIndexColorModel =
			new java.awt.image.IndexColorModel(
				8, cmap.length,cmap,0,
				false /*false means NOT USE alpha*/   ,
				-1/*NO transparent single pixel*/,
				java.awt.image.DataBuffer.TYPE_BYTE);
		
		initCtrlz();
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(734, 710);
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowWeights = new double[]{0.0, 1.0,0};
		gridBagLayout_1.columnWeights = new double[]{1.0};
		gridBagLayout_1.rowHeights = new int[] {0,0, 0};  
		this.setLayout(gridBagLayout_1);

		editROIPanel = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,0,7};
		gridBagLayout_2.columnWidths = new int[] {0,7};
		editROIPanel.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.insets = new Insets(2, 2, 5, 2);
		gridBagConstraints_6.weightx = 1.0;
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.gridx = 0;
		add(editROIPanel, gridBagConstraints_6);

		final JLabel infoLabel = new JLabel();
		infoLabel.setText("Data Info:");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 0;
		gridBagConstraints_12.gridx = 0;
		editROIPanel.add(infoLabel, gridBagConstraints_12);

		textLabel = new JLabel();
		textLabel.setPreferredSize(new Dimension(500, 20));
		textLabel.setMinimumSize(new Dimension(500, 20));
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.weightx = 1;
		gridBagConstraints_2.insets = new Insets(0, 2, 0, 0);
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 1;
		editROIPanel.add(textLabel, gridBagConstraints_2);
		textLabel.setText("No FRAP DataSet loaded.");

		autoCropButton = new JButton(new ImageIcon(getClass().getResource("/images/autoCrop.gif")));
		autoCropButton.setName("roiAutoCropBtn");
		autoCropButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				firePropertyChange(FRAP_DATA_AUTOCROP_PROPERTY, null,null);
			}
		});
		
		clearROIbutton = new JButton(new ImageIcon(getClass().getResource("/images/clearROI.gif")));
		clearROIbutton.setEnabled(false);
		clearROIbutton.setName("clearROIBtn");
		clearROIbutton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(roiComboBox.getItemCount() == 0){
					giveROIRequiredWarning("Clear Domain");
					return;
				}
				firePropertyChange(FRAP_DATA_CLEARROI_PROPERTY, ((ROIMultiPaintManager.ComboboxROIName)roiComboBox.getSelectedItem()), null);
			}
		});

		viewZLabel = new JLabel();
		viewZLabel.setText("View Z:");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_17.anchor = GridBagConstraints.EAST;
		gridBagConstraints_17.gridy = 1;
		gridBagConstraints_17.gridx = 0;
		editROIPanel.add(viewZLabel, gridBagConstraints_17);

		final JPanel panel_1 = new JPanel();
		final GridBagLayout gridBagLayout_4 = new GridBagLayout();
		gridBagLayout_4.columnWeights = new double[]{1.0};
		gridBagLayout_4.columnWidths = new int[] {7};
		panel_1.setLayout(gridBagLayout_4);
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.anchor = GridBagConstraints.WEST;
		gridBagConstraints_18.insets = new Insets(0, 2, 0, 0);
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.weightx = 0;
		gridBagConstraints_18.gridy = 1;
		gridBagConstraints_18.gridx = 1;
		editROIPanel.add(panel_1, gridBagConstraints_18);
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_19.anchor = GridBagConstraints.WEST;
		gridBagConstraints_19.weightx = 1;
		gridBagConstraints_19.gridy = 0;
		gridBagConstraints_19.gridx = 0;
		panel_1.add(getZSlider(), gridBagConstraints_19);

		viewTLabel = new JLabel();
		viewTLabel.setText("View Time:");
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_13.anchor = GridBagConstraints.EAST;
		gridBagConstraints_13.gridy = 2;
		gridBagConstraints_13.gridx = 0;
		editROIPanel.add(viewTLabel, gridBagConstraints_13);

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.weightx = 1;
		gridBagConstraints_15.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_15.anchor = GridBagConstraints.WEST;
		gridBagConstraints_15.gridy = 0;
		gridBagConstraints_15.gridx = 0;
		panel.add(getTimeSlider(), gridBagConstraints_15);
		
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_14.insets = new Insets(0, 2, 0, 0);
		gridBagConstraints_14.anchor = GridBagConstraints.WEST;
		gridBagConstraints_14.gridy = 2;
		gridBagConstraints_14.gridx = 1;
		editROIPanel.add(panel, gridBagConstraints_14);

		final JPanel editROIButtonPanel = new JPanel();
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.rowWeights = new double[]{0.0, 1.0};
		gridBagLayout_3.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0};
		gridBagLayout_3.columnWidths = new int[] {0, 0,7,7, 0, 0};
		editROIButtonPanel.setLayout(gridBagLayout_3);
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridwidth = 2;
		gridBagConstraints_8.weightx = 0;
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.insets = new Insets(0, 2, 0, 0);
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.gridx = 0;
		editROIPanel.add(editROIButtonPanel, gridBagConstraints_8);
		
		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(2, 2, 2, 2);
		gbc_panel_2.weighty = 1.0;
		gbc_panel_2.weightx = 1.0;
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0};
		gbl_panel_2.columnWeights = new double[]{0, 1.0, 0.0};
		gbl_panel_2.rowWeights = new double[]{0.0};
		panel_2.setLayout(gbl_panel_2);
		
		panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.insets = new Insets(2, 2, 2, 2);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		panel_2.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0, 0};
		gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		domainRegionLabel = new JLabel(DOMAIN_LIST_TEXT);
		GridBagConstraints gbc_domainRegionLabel = new GridBagConstraints();
		gbc_domainRegionLabel.insets = new Insets(0, 0, 2, 0);
		gbc_domainRegionLabel.gridx = 0;
		gbc_domainRegionLabel.gridy = 0;
		panel_3.add(domainRegionLabel, gbc_domainRegionLabel);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 2, 0, 0);
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panel_3.add(scrollPane, gbc_scrollPane);
		scrollPane.setPreferredSize(new Dimension(125, 10));
		scrollPane.setMinimumSize(new Dimension(125,10));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		resolvedList = new JList();
		resolvedList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(showConvertPopup(e,false) == SHOWCONVERT.HANDLED){
					return;
				}
				if(e.getClickCount() == 2){
					firePropertyChange(FRAP_DATA_FINDROI_PROPERTY, null, resolvedList.getSelectedValue());
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				if(showConvertPopup(e,false) == SHOWCONVERT.HANDLED){
					return;
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseReleased(e);
				if(showConvertPopup(e,false) == SHOWCONVERT.HANDLED){
					return;
				}
			}
			
		});
		resolvedList.addListSelectionListener(resolvedListSelectionListener);
		resolvedList.setCellRenderer(resolvedObjectListCellRenderer);
		scrollPane.setViewportView(resolvedList);
		
		mergeButton = new JButton("Auto-Merge");
		mergeButton.setToolTipText("Remove regions by merging with neighbor");
		mergeButton.setEnabled(false);
		mergeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resolvedListSelection(true);
			}
		});
		GridBagConstraints gbc_mergeButton = new GridBagConstraints();
		gbc_mergeButton.gridx = 0;
		gbc_mergeButton.gridy = 2;
		panel_3.add(mergeButton, gbc_mergeButton);
		GridBagConstraints gbc_jScrollPane2 = new GridBagConstraints();
		gbc_jScrollPane2.weighty = 1.0;
		gbc_jScrollPane2.weightx = 1.0;
		gbc_jScrollPane2.fill = GridBagConstraints.BOTH;
		gbc_jScrollPane2.insets = new Insets(2, 2, 2, 2);
		gbc_jScrollPane2.gridx = 1;
		gbc_jScrollPane2.gridy = 0;
		panel_2.add(getJScrollPane2(), gbc_jScrollPane2);
		GridBagConstraints gbc_toolButtonPanel = new GridBagConstraints();
		gbc_toolButtonPanel.weighty = 1.0;
		gbc_toolButtonPanel.insets = new Insets(2, 2, 0, 2);
		gbc_toolButtonPanel.anchor = GridBagConstraints.NORTH;
		gbc_toolButtonPanel.gridx = 2;
		gbc_toolButtonPanel.gridy = 0;
		panel_2.add(getToolButtonPanel(), gbc_toolButtonPanel);

		roiComboBox = new JComboBox();
		roiComboBox.setName("activeROIComboBox");
		roiComboBox.setRenderer(new ListCellRenderer() {
			private DefaultListCellRenderer listCellRender = new DefaultListCellRenderer();
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				ROIMultiPaintManager.ComboboxROIName comboboxROIName = (ROIMultiPaintManager.ComboboxROIName)value;
				if(comboboxROIName == null){//return blank
					return listCellRender.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
				}
				if(comboboxROIName.getHighlightColor() == null){//return text only
					return listCellRender.getListCellRendererComponent(list, comboboxROIName.getROIName(), index, isSelected, cellHasFocus);
				}
				//return text with small color box
				Icon icon = new ColorIcon(20,20,comboboxROIName.getHighlightColor());
				JLabel jlable = (JLabel)listCellRender.getListCellRendererComponent(list, icon, index, isSelected, cellHasFocus);
				jlable.setText(comboboxROIName.getROIName());
				return jlable;
			}
		});
		roiComboBox.addActionListener(ROI_COMBOBOX_ACTIONLISTENER);
		
		lblNewLabel = new JLabel("Active Domain:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(4, 4, 4, 4);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		editROIButtonPanel.add(lblNewLabel, gbc_lblNewLabel);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.insets = new Insets(4, 4, 5, 5);
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 1;
		editROIButtonPanel.add(roiComboBox, gridBagConstraints_1);

		addROIButton = new JButton();
		addROIButton.setName("roiAddBtn");
		addROIButton.addActionListener(addROIActionListener);
		addROIButton.setText("Add Domain...");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(4, 4, 5, 5);
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 2;
		editROIButtonPanel.add(addROIButton, gridBagConstraints_3);

		delROIButton = new JButton();
		delROIButton.setName("roiDeleteBtn");
		delROIButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				firePropertyChange(FRAP_DATA_DELETEROI_PROPERTY, ((ROIMultiPaintManager.ComboboxROIName)roiComboBox.getSelectedItem()), null);
			}
		});
		delROIButton.setText("Delete Domain...");
		delROIButton.setEnabled(false);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(4, 4, 5, 5);
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 3;
		editROIButtonPanel.add(delROIButton, gridBagConstraints_4);
		
		discardHighlightsButton = new JButton("Clear Selections");
		discardHighlightsButton.setEnabled(false);
		discardHighlightsButton.setName("clearHighlightsBtn");
		discardHighlightsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				firePropertyChange(FRAP_DATA_DISCARDHIGHLIGHT_PROPERTY,null,null);
			}
		});

		GridBagConstraints gbc_specialActionsButton = new GridBagConstraints();
		gbc_specialActionsButton.insets = new Insets(4, 4, 5, 5);
		gbc_specialActionsButton.gridx = 4;
		gbc_specialActionsButton.gridy = 0;
		editROIButtonPanel.add(discardHighlightsButton, gbc_specialActionsButton);
		
		channelComboBox = new JComboBox();
		GridBagConstraints gbc_channelComboBox = new GridBagConstraints();
		gbc_channelComboBox.insets = new Insets(4, 4, 5, 4);
		gbc_channelComboBox.gridx = 5;
		gbc_channelComboBox.gridy = 0;
		editROIButtonPanel.add(channelComboBox, gbc_channelComboBox);
		channelComboBox.addActionListener(channelActionListener);
		channelComboBox.setPreferredSize(new Dimension(100, 22));
		channelComboBox.setMinimumSize(new Dimension(100, 20));
		
		blendPercentPanel = new JPanel();
		blendPercentPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_2a = new GridBagConstraints();
		gbc_panel_2a.gridwidth = 6;
		gbc_panel_2a.insets = new Insets(4, 4, 4, 4);
		gbc_panel_2a.fill = GridBagConstraints.BOTH;
		gbc_panel_2a.gridx = 0;
		gbc_panel_2a.gridy = 1;
		editROIButtonPanel.add(blendPercentPanel, gbc_panel_2a);
		GridBagLayout gbl_panel_2a = new GridBagLayout();
		blendPercentPanel.setLayout(gbl_panel_2a);
		
		blendPercentROILabel = new JLabel("Domains");
		GridBagConstraints gbc_blendPercentROILabel = new GridBagConstraints();
		gbc_blendPercentROILabel.anchor = GridBagConstraints.WEST;
		gbc_blendPercentROILabel.gridx = 0;
		gbc_blendPercentROILabel.gridy = 0;
		blendPercentPanel.add(blendPercentROILabel, gbc_blendPercentROILabel);
		
		blendPercentSlider = new JSlider();
		blendPercentSlider.setToolTipText("Mix view of Domains and background image");
		blendPercentSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setBlendPercent(blendPercentSlider.getValue());
//				if(!blendPercentSlider.getValueIsAdjusting()){
//					setBlendPercent(blendPercentSlider.getValue());
//				}
			}
		});
		GridBagConstraints gbc_blendPercentSlider = new GridBagConstraints();
		gbc_blendPercentSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_blendPercentSlider.weightx = 0.5;
		gbc_blendPercentSlider.gridx = 1;
		gbc_blendPercentSlider.gridy = 0;
		blendPercentPanel.add(blendPercentSlider, gbc_blendPercentSlider);
		
		blendPercentImageLabel = new JLabel("Image");
		GridBagConstraints gbc_blendPercentImageLabel = new GridBagConstraints();
		gbc_blendPercentImageLabel.anchor = GridBagConstraints.EAST;
		gbc_blendPercentImageLabel.gridx = 2;
		gbc_blendPercentImageLabel.gridy = 0;
		blendPercentPanel.add(blendPercentImageLabel, gbc_blendPercentImageLabel);
		
		smoothOrigLabel = new JLabel("Original");
		GridBagConstraints gbc_smoothOrigLabel = new GridBagConstraints();
		gbc_smoothOrigLabel.insets = new Insets(0, 20, 0, 0);
		gbc_smoothOrigLabel.gridx = 3;
		gbc_smoothOrigLabel.gridy = 0;
		blendPercentPanel.add(smoothOrigLabel, gbc_smoothOrigLabel);
		
		smoothslider = new JSlider();
		smoothslider.setToolTipText("Smooth background image");
		smoothslider.setSnapToTicks(true);
		smoothslider.setPaintTicks(true);
		smoothslider.setMajorTickSpacing(1);
		smoothslider.setMaximum(10);
		smoothslider.setValue(0);
		smoothslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(!smoothslider.getValueIsAdjusting()){
					firePropertyChange(FRAP_DATA_UNDERLAY_SMOOTH_PROPERTY, null, new Integer(smoothslider.getValue()));
				}
			}
		});
		GridBagConstraints gbc_smoothslider = new GridBagConstraints();
		gbc_smoothslider.fill = GridBagConstraints.HORIZONTAL;
		gbc_smoothslider.weightx = 0.5;
		gbc_smoothslider.gridx = 4;
		gbc_smoothslider.gridy = 0;
		blendPercentPanel.add(smoothslider, gbc_smoothslider);
		
		smootherLabel = new JLabel("Smoother");
		GridBagConstraints gbc_smootherLabel = new GridBagConstraints();
		gbc_smootherLabel.gridx = 5;
		gbc_smootherLabel.gridy = 0;
		blendPercentPanel.add(smootherLabel, gbc_smootherLabel);
		
		roiDrawButtonGroup.add(selectButton);
		roiDrawButtonGroup.add(paintButton);
		roiDrawButtonGroup.add(eraseButton);
		roiDrawButtonGroup.add(fillButton);
		roiDrawButtonGroup.add(cropButton);
		
		BeanUtils.enableComponents(getToolButtonPanel(), false);
		BeanUtils.enableComponents(editROIPanel, false);
		
		histogramPanel = new HistogramPanel();
		histogramPanel.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(HistogramPanel.HISTOGRAM_SELECT_PROPERTY)){
					OverlayEditorPanelJAI.this.firePropertyChange(
					OverlayEditorPanelJAI.FRAP_DATA_HISTOUPDATEHIGHLIGHT_PROPERTY, null, (ListSelectionModel)evt.getNewValue());
				}else if(evt.getPropertyName().equals(HistogramPanel.HISTOGRAM_APPLY_ACTION)){
					firePropertyChange(OverlayEditorPanelJAI.FRAP_DATA_UPDATEROI_WITHHIGHLIGHT_PROPERTY,null,null);
				}

			}
		});
		histogramPanel.setVisible(false);
		histogramPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_histogramButton = new GridBagConstraints();
		gbc_histogramButton.fill = GridBagConstraints.BOTH;
		gbc_histogramButton.insets = new Insets(2, 2, 2, 2);
		gbc_histogramButton.gridx = 0;
		gbc_histogramButton.gridy = 2;
		add(histogramPanel, gbc_histogramButton);
	}
	
	private void resolvedListSelection(boolean bMerge){
		RegionInfo[] selectedRegionInfos = Arrays.asList(resolvedList.getSelectedValues()).toArray(new RegionImage.RegionInfo[0]);
		if(!bMerge){
			int numListItems = resolvedList.getModel().getSize();
			boolean bEnableMerge = (numListItems > 1) && (selectedRegionInfos==null?false:(selectedRegionInfos.length == 0?false:true));
			bEnableMerge = bEnableMerge && (numListItems != selectedRegionInfos.length);
			mergeButton.setEnabled(bEnableMerge);
			firePropertyChange(FRAP_DATA_RESOLVEDHIGHLIGHT_PROPERTY, null, selectedRegionInfos);
		}else{
			firePropertyChange(FRAP_DATA_RESOLVEDMERGE_PROPERTY, null, selectedRegionInfos);
		}
	}
	public void setHistogram(TreeMap<Integer, Integer> origHistoTreeMap){
		histogramPanel.setHistogram(origHistoTreeMap);
	}
	public void showHistogram(){
		histogramPanel.setVisible(true);
	}
	
	private void refreshROI(){
		if (getHighliteInfo()!=null){
			getImagePane().setHighlightImageAndWritebackBuffer(
				createHighlightImageFromROI(getHighliteInfo()),
				getHighliteInfo().getRoiImages()[getRoiImageIndex()].getPixels());
		}else{
			getImagePane().setHighlightImageAndWritebackBuffer(null,null);
		}
		
		getImagePane().setAllROICompositeImage((allROICompositeImageArr==null?null:allROICompositeImageArr[getRoiImageIndex()]));
	}

	public ROIMultiPaintManager.ComboboxROIName[] getAllCompositeROINamesAndColors(){
		ROIMultiPaintManager.ComboboxROIName[] allCompositeROINamesAndColors = new ROIMultiPaintManager.ComboboxROIName[roiComboBox.getItemCount()];
		for (int i = 0; i < roiComboBox.getItemCount(); i++) {
			allCompositeROINamesAndColors[i] = (ROIMultiPaintManager.ComboboxROIName)roiComboBox.getItemAt(i);
		}
		return allCompositeROINamesAndColors;
	}
	public ROI getHighliteInfo(){
		return highliteInfo;
	}
	public ROIMultiPaintManager.ComboboxROIName getCurrentROIInfo(){
		return (ROIMultiPaintManager.ComboboxROIName)roiComboBox.getSelectedItem();
	}
	public void setBlendPercent(int blendPercent){
		Integer newBlendPercent = new Integer(blendPercent);
		if(!newBlendPercent.equals(blendPercentSlider.getValue())){
			blendPercentSlider.setValue(blendPercent);
		}

		imagePane.setBlendPercent(blendPercent);
	}
	public int getBlendPercent(){
		return blendPercentSlider.getValue();
	}
	public boolean isHistogramSelectionEmpty(){
		return histogramPanel.isSelectionEmpty();
	}
	/**
	 * Method setROI.
	 * @param argHighliteInfo ROI
	 */
	public void setHighliteInfo(ROI highliteInfo,String action){
//		System.out.println(highliteInfo+" "+action+" bHistEmpty="+histogramPanel.isSelectionEmpty()+" bListEmpty="+resolvedList.isSelectionEmpty());
		discardHighlightsButton.setEnabled(highliteInfo != null);
		histogramPanel.highlightsChanged(action);
		this.highliteInfo = highliteInfo;
		refreshROI();
		if(highliteInfo == null || action == FRAP_DATA_HISTOUPDATEHIGHLIGHT_PROPERTY){
			resolvedList.removeListSelectionListener(resolvedListSelectionListener);
			resolvedList.clearSelection();
			resolvedList.addListSelectionListener(resolvedListSelectionListener);
		}
	}
	
	public void setTimeIndex(int timeIndex){
		if(timeSlider.getValue() == timeIndex){
			forceImage();
		}else{
			timeSlider.setValue(timeIndex);
		}
	}
	public void placeMarkerOverResolved(CoordinateIndex ci,int starSize){
		if(ci == null){
			imagePane.drawStar(null);
			return;
		}
		zSlider.setValue(ci.z);
		GeneralPath generalPath = new GeneralPath();
		generalPath.moveTo((float)(ci.x-starSize),(float)ci.y);
		generalPath.lineTo((float)(ci.x+starSize),(float)ci.y);
		generalPath.moveTo((float)(ci.x),(float)(ci.y-starSize));
		generalPath.lineTo((float)(ci.x),(float)(ci.y+starSize));
		generalPath.moveTo((float)(ci.x-starSize),(float)(ci.y-starSize));
		generalPath.lineTo((float)(ci.x+starSize),(float)(ci.y+starSize));
		generalPath.moveTo((float)(ci.x-starSize),(float)(ci.y+starSize));
		generalPath.lineTo((float)(ci.x+starSize),(float)(ci.y-starSize));
		imagePane.drawStar(generalPath);
	}
	public void saveUserChangesToROI(){
		short[] roiPixels = getImagePane().getHighlightImageWritebackImageBuffer();
		if (roiPixels!=null){
			BufferedImage highlightImage = imagePane.getHighlightImage();
			byte[] hiLiteArr = ((DataBufferByte)highlightImage.getRaster().getDataBuffer()).getData();
			for (int i = 0; i < roiPixels.length; i++) {
				roiPixels[i] = (hiLiteArr[i] == 0?0:(short)0xffff);
			}
		}
	}	
	/**
	 * Method createUnderlyingImage.
	 * @param image UShortImage
	 * @return BufferedImage
	 */
	private BufferedImage createUnderlyingImage(UShortImage image){
		short[] pixels = image.getPixels();

		WritableRaster raster = greyIndexColorModel.createCompatibleWritableRaster(image.getNumX(), image.getNumY());
		byte[] byteData = ((DataBufferByte)raster.getDataBuffer()).getData();
		if(allPixelValuesRange.getScaleFactor() >= 1){
			for (int i = 0; i < byteData.length; i++) {
				byteData[i] = (byte)(pixels[i]&0x0000FFFF);
			}
		}else{
			for (int i = 0; i < byteData.length; i++) {
				byteData[i] = (byte)((pixels[i]&0x0000FFFF)*allPixelValuesRange.getScaleFactor());
			}			
		}

	    BufferedImage result = new BufferedImage(greyIndexColorModel,raster, false, null);
	    return result;
	}
	
	public void displaySpecialData(short[] specialData,int width, int height) throws Exception{
		if(specialData == null){
			forceImage();
			return;
		}
		UShortImage specialUShortImage = new UShortImage(specialData,null,null,width,height,1);
		BufferedImage specialBufferedImage = createUnderlyingImage(specialUShortImage);
		imagePane.setUnderlyingImage(specialBufferedImage,/* false,*/null);
	}

	IndexColorModel hiLiteColorModel;
	private IndexColorModel getHiliteColorModel(){
		if(hiLiteColorModel == null){
			int[] cmap = new int[256];
			for(int i=0;i<256;i+= 1){
				if(i != 0){
					cmap[i] = 0xFF000000 | highlightColor.getRGB();
				}else{
					cmap[1] = 0xFF000000;
				}
			}
			hiLiteColorModel =
				new java.awt.image.IndexColorModel(
					8, cmap.length,cmap,0,
					false /*false means NOT USE alpha*/   ,
					-1/*NO transparent single pixel*/,
					java.awt.image.DataBuffer.TYPE_BYTE);
		}
		return hiLiteColorModel;
	}
	/**
	 * Method createHighlightImageFromROI.
	 * @return BufferedImage
	 */
	private BufferedImage createHighlightImageFromROI(ROI highlightImageROI){
		UShortImage roiImage = highlightImageROI.getRoiImages()[getRoiImageIndex()];
		int width = roiImage.getNumX();
		int height = roiImage.getNumY();
		BufferedImage hiLiteImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, getHiliteColorModel());
		byte[] hiLiteArr = ((DataBufferByte)hiLiteImage.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < roiImage.getPixels().length; i++) {
			hiLiteArr[i] = (roiImage.getPixels()[i] != 0?(byte)highlightColor.getRed():0);
		}
		return hiLiteImage;
	}
	
	public void setAllowAddROI(boolean bAllowAddROI){
		this.bAllowAddROI = bAllowAddROI;
		roiComboBox.setVisible(bAllowAddROI);
		addROIButton.setVisible(bAllowAddROI);
		delROIButton.setVisible(bAllowAddROI);
		discardHighlightsButton.setVisible(bAllowAddROI);
		channelComboBox.setVisible(bAllowAddROI);
		cropButton.setVisible(bAllowAddROI);
		autoCropButton.setVisible(bAllowAddROI);
		paintButton.setVisible(bAllowAddROI);
		eraseButton.setVisible(bAllowAddROI);
		fillButton.setVisible(bAllowAddROI);
		clearROIbutton.setVisible(bAllowAddROI);
		getUndoButton().setVisible(bAllowAddROI);
		extrudeToolButton.setVisible(bAllowAddROI);
		borderToolButton.setVisible(bAllowAddROI);
	}

	public void addROIName(String roiName,
			boolean isNameEditable,String selectROIName,boolean bDeleteable,/*boolean bPaintable,boolean bErasable,*/
			int contrastColorIndex){
		try{
			roiComboBox.removeActionListener(ROI_COMBOBOX_ACTIONLISTENER);
			roiComboBox.setEnabled(true);
			roiComboBox.addItem(new ROIMultiPaintManager.ComboboxROIName(roiName,isNameEditable,bDeleteable,/*bPaintable,bErasable,*/contrastColorIndex));
			for (int i = 0; i < roiComboBox.getItemCount(); i++) {
				if(((ROIMultiPaintManager.ComboboxROIName)roiComboBox.getItemAt(i)).getROIName().equals(selectROIName)){
					roiComboBox.setSelectedIndex(i);
					break;
				}
			}

		}finally{
			roiComboBox.addActionListener(ROI_COMBOBOX_ACTIONLISTENER);
			ROI_COMBOBOX_ACTIONLISTENER.actionPerformed(new ActionEvent(roiComboBox,0,roiComboBox.getSelectedItem().toString()));
			clearROIbutton.setEnabled(true);
		}
	}
	public void deleteROIName(ROIMultiPaintManager.ComboboxROIName delComboboxROIName){
		if(delComboboxROIName != null){
			roiComboBox.removeItem(delComboboxROIName);			
		}else{
			roiComboBox.removeAllItems();
		}
		if(roiComboBox.getItemCount() == 0){
			roiComboBox.setEnabled(false);
			clearROIbutton.setEnabled(false);
		}else{
			clearROIbutton.setEnabled(true);
		}

	}
	public void setAllROICompositeImage(BufferedImage[] allROICompositeImageArr,String action){
		this.allROICompositeImageArr = allROICompositeImageArr;
		imagePane.setAllROICompositeImage((allROICompositeImageArr==null?null:allROICompositeImageArr[getZ()]));
		histogramPanel.highlightsChanged(action);
	}
	public void setContrastToMinMax(){
		imagePane.setContrastToMinMax();
	}
	/** Sets the viewer to display the given images. * @param argImageDataset ImageDataset
	 */
	public void setImages(ImageDataset argImageDataset,double originalScaleFactor,double originalOffsetFactor,
			AllPixelValuesRange allPixelValuesRange) {
		imageDataset = argImageDataset;
		BufferedImage underlyingImage = null;
		if (imageDataset!=null){
			this.allPixelValuesRange = allPixelValuesRange;

			this.originalScaleFactor = originalScaleFactor;
			this.originalOffsetFactor = originalOffsetFactor;
			if(!timeSlider.isEnabled()) //if the component is already enabled, don't do anything
			{
				boolean bUndoEnbld = getUndoButton().isEnabled();//undo state controlled elsewhere
				BeanUtils.enableComponents(toolButtonPanel, true);
				BeanUtils.enableComponents(editROIPanel, true);
				getUndoButton().setEnabled(bUndoEnbld);
				extrudeToolButton.setEnabled(imageDataset.getISize().getZ()==1);
				discardHighlightsButton.setEnabled(highliteInfo != null);
			}
			if(!bAllowAddROI){
				addROIButton.setEnabled(false);
				delROIButton.setEnabled(false);
				clearROIbutton.setEnabled(false);
			}else{
				delROIButton.setEnabled(roiComboBox.getItemCount() != 0);
				roiComboBox.setEnabled(roiComboBox.getItemCount() != 0);
				clearROIbutton.setEnabled(roiComboBox != null && roiComboBox.getItemCount()>0);
			}
			
			timeSlider.setVisible(imageDataset.getSizeT() > 1);
			viewTLabel.setVisible(timeSlider.isVisible());
			int currTimeSliderValue = timeSlider.getValue();
			if(imageDataset.getSizeT() > 1){
//				timeSlider.setLabelTable(timeSlider.createStandardLabels(imageDataset.getSizeT()-1,1));
				Hashtable<Integer, JComponent> labeltable = new Hashtable<Integer, JComponent>();
				labeltable.put(1, new JLabel(imageDataset.getImageTimeStamps()[0]+""));
				labeltable.put(imageDataset.getSizeT(),
					new JLabel(NumberUtils.formatNumber(imageDataset.getImageTimeStamps()[imageDataset.getSizeT()-1])));
				timeSlider.setLabelTable(labeltable);
				timeSlider.setMaximum(imageDataset.getSizeT());
				//Added because setMaximum SOMETIMES resets the label table
				timeSlider.setLabelTable(labeltable);
			}
			timeSlider.setValue(Math.max(1,Math.min(imageDataset.getSizeT(),currTimeSliderValue)));
			timeSlider.setMajorTickSpacing(imageDataset.getSizeT());
			timeSlider.setEnabled(imageDataset.getSizeT() > 1);
			
			zSlider.setVisible(imageDataset.getSizeZ() > 1);
			viewZLabel.setVisible(zSlider.isVisible());
			if(imageDataset.getSizeZ() > 1){
				zSlider.setLabelTable(zSlider.createStandardLabels(imageDataset.getSizeZ()-1,1));
			}
			int currZSliderValue = zSlider.getValue();
			zSlider.setValue(Math.max(1,Math.min(imageDataset.getSizeZ(),currZSliderValue)));
			zSlider.setMaximum(imageDataset.getSizeZ());
			zSlider.setMajorTickSpacing(imageDataset.getSizeZ());
			zSlider.setEnabled(imageDataset.getSizeZ() > 1);
			
			underlyingImage = createUnderlyingImage(imageDataset.getImage((zSlider.getValue()-1),0,(timeSlider.getValue()-1)));
		}else{
			this.allPixelValuesRange = null;
			this.originalScaleFactor = DEFAULT_SCALE_FACTOR;
			this.originalOffsetFactor = DEFAULT_OFFSET_FACTOR;
			timeSlider.setValue(1);
			timeSlider.setMaximum(1);
			timeSlider.setLabelTable(null);
			timeSlider.setEnabled(false);
			zSlider.setValue(1);
			zSlider.setMaximum(1);
			zSlider.setLabelTable(null);
			zSlider.setEnabled(false);
			BeanUtils.enableComponents(toolButtonPanel, false);
			BeanUtils.enableComponents(editROIPanel, false);
			underlyingImage = null;
		}
		
		updateLabel(-1, -1);
		getImagePane().setUnderlyingImage(underlyingImage,this.allPixelValuesRange);
	}

	public int getDisplayContrastFactor(){
		return imagePane.getDisplayContrastFactor();
	}
	public void setDisplayContrastFactor(int displayContrastFactor){
		imagePane.setDisplayContrastFactor(displayContrastFactor);
	}
	
	
	public ComboboxROIName getComboboxROIName(RegionInfo regionInfo){
		for (int i = 0; i < roiComboBox.getItemCount(); i++) {
			ROIMultiPaintManager.ComboboxROIName comboboxROIName = (ROIMultiPaintManager.ComboboxROIName)roiComboBox.getItemAt(i);
			if(comboboxROIName.getContrastColorIndex() == regionInfo.getPixelValue()){
				return comboboxROIName;
			}
		}
		return null;
	}
	/** Gets the currently displayed image. * @return BufferedImage
	 */
	private BufferedImage getImage() {
		if(imageDataset == null){
			return null;
		}
		int ndx = getImageIndex();
		if (imageDataset == null || ndx >= imageDataset.getAllImages().length){
			return null;
		}
		UShortImage image = imageDataset.getAllImages()[ndx];
		return createUnderlyingImage(image);
	}

	private JPanel panel_2;
	private HistogramPanel histogramPanel;
	private JComboBox channelComboBox;
	private ActionListener channelActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			firePropertyChange(OverlayEditorPanelJAI.FRAP_DATA_CHANNEL_PROPERTY, null, channelComboBox.getSelectedIndex());
		}
	};
	public void setChannelNames(String[] channelNames){
		channelComboBox.removeAllItems();
		if(channelNames == null){
			channelComboBox.setVisible(false);
		}else{
			channelComboBox.removeActionListener(channelActionListener);
			for (int i = 0; i < channelNames.length; i++) {
				channelComboBox.insertItemAt(channelNames[i], i);
			}
			channelComboBox.addActionListener(channelActionListener);
			channelComboBox.setVisible(true);
			channelComboBox.setSelectedIndex(0);
		}
	}
	private ISize getISizeDataset(){
		ISize isizeDataset = null;
		if(imageDataset != null){
			return imageDataset.getISize();
		}else if(getHighliteInfo() != null){
			return getHighliteInfo().getISize();
		}else if(allROICompositeImageArr != null){
			isizeDataset = new ISize(allROICompositeImageArr[0].getWidth(), allROICompositeImageArr[0].getHeight(), allROICompositeImageArr.length);
		}
		return isizeDataset;
	}
	
	private void initCtrlz(){
		this.getActionMap().put("Undo",
			    new AbstractAction("Undo") {
			        public void actionPerformed(ActionEvent evt) {
			            getUndoButton().doClick();
			        }
			   }
		);
		this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo" );
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo" );
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo" );
		//comp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo" );//redo, to be impl
		
		this.getActionMap().put("Cibele_sep",
			    new AbstractAction("Cibele_sep") {
			        public void actionPerformed(ActionEvent evt) {
			            OverlayEditorPanelJAI.this.firePropertyChange(FRAP_DATA_SEPARATE_PROPERTY, null, null);
			        }
			   }
		);
		this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke('!'), "Cibele_sep" );
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('!'), "Cibele_sep" );
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('!'), "Cibele_sep" );

	}
	public void resolvedSelectionChange(ROIMultiPaintManager.SELECT_FUNC selectFunc,int index){
		if(selectFunc == ROIMultiPaintManager.SELECT_FUNC.ADD){
			resolvedList.addSelectionInterval(index, index);
		}else if(selectFunc == ROIMultiPaintManager.SELECT_FUNC.REMOVE){
			resolvedList.removeSelectionInterval(index, index);
		}else if(selectFunc == ROIMultiPaintManager.SELECT_FUNC.REPLACE){
			resolvedList.setSelectedIndex(index);
		}
	}
	enum SHOWCONVERT {HANDLED,IGNORED};
	private SHOWCONVERT showConvertPopup(MouseEvent mouseEvent,boolean bFromImage){
		if(!mouseEvent.isPopupTrigger() || (!selectButton.isSelected() && bFromImage)){
			return SHOWCONVERT.IGNORED;
		}
		if(resolvedList.getModel().getSize() == 1){
			return SHOWCONVERT.HANDLED;
		}

		if(bFromImage){
			//from OverlayImageDisplayJAI
			fireSelectFromImage(mouseEvent,true);
		}else{
			//from JList
			int jlistIndex = resolvedList.locationToIndex(mouseEvent.getPoint());
			if(resolvedList.isSelectedIndex(jlistIndex)){
//				if(mouseEvent.isControlDown()){
//					resolvedList.removeSelectionInterval(jlistIndex, jlistIndex);
//				}else{
//					resolvedList.addSelectionInterval(jlistIndex, jlistIndex);
//				}
			}else{
				resolvedList.setSelectedIndex(jlistIndex);
			}
		}

		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenu convertMenu = new JMenu("Convert selected regions to Domain");
		jPopupMenu.add(convertMenu);
		final ROIMultiPaintManager.ComboboxROIName[]  roiComboboxROINames = getAllCompositeROINamesAndColors();
		for (int i = -1; i < roiComboboxROINames.length; i++) {
			String roiName = (i==-1?ICON_BKGRND_NAME:roiComboboxROINames[i].getROIName());
			final int colorIndex = (i==-1?0:roiComboboxROINames[i].getContrastColorIndex());
			JMenuItem jMenuItem = new JMenuItem(roiName, resolvedColorPatchImageIconArr[colorIndex]);
			jMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					RegionImage.RegionInfo[] regionInfoArr = Arrays.asList(resolvedList.getSelectedValues()).toArray(new RegionImage.RegionInfo[0]);
					firePropertyChange(FRAP_DATA_CONVERTDOMAIN_PROPERTY, regionInfoArr,colorIndex);
				}
			});
			convertMenu.add(jMenuItem);
		}
		jPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
		return SHOWCONVERT.HANDLED;
	}
	private void  fireSelectFromImage(MouseEvent mouseEvent,boolean bIgnoreIfSelected){
		Rectangle selectRectangle = new Rectangle((int)(mouseEvent.getPoint().x/imagePane.getZoom()), (int)(mouseEvent.getPoint().y/imagePane.getZoom()), 0, 0);
		firePropertyChange(FRAP_DATA_SELECTIMGROI_PROPERTY, null, new  ROIMultiPaintManager.SelectImgInfo(mouseEvent,/*imagePane.getZoom(),*/resolvedList,selectRectangle,bIgnoreIfSelected));
	}
	/**
	 * Method getImagePane.
	 * @return OverlayImageDisplayJAI
	 */
	private OverlayImageDisplayJAI getImagePane(){
		if (imagePane == null){
			imagePane = new OverlayImageDisplayJAI();
			//imagePane = new ZoomableOverlayImagePane();
			imagePane.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseExited(MouseEvent e){
					imagePane.setCursor(Cursor.getDefaultCursor());
					imagePane.setBrush(null);
					imagePane.refreshImage();
					updateLabel(-1, -1);
					if(histogramPanel.isVisible()){
						histogramPanel.setSpecialValue(null);
					}
				}
				public void mousePressed(MouseEvent e){
					if(!bAllowAddROI){
						return;
					}

					updateLabel(e.getX(), e.getY());
					lastMousePoint = e.getPoint();
					if(showConvertPopup(e,true) == SHOWCONVERT.HANDLED){
						return;
					}else if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && !brushToolHelper.isBrushSizeClickDragMode()){
						if(roiComboBox.getItemCount() == 0){
							giveROIRequiredWarning("paint or erase");
							return;
						}
						firePropertyChange(FRAP_DATA_PAINTERASE_PROPERTY, null, null);
						getImagePane().setBrush(null);
						drawHighlight(e.getX(), e.getY(), brushToolHelper.getBrushSize(getImagePane().getZoom()), eraseButton.isSelected());
					}else if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && brushToolHelper.isBrushSizeClickDragMode()){
						brushToolHelper.startBrushSizeClickDragMode(e);
					}
				}
				public void mouseReleased(MouseEvent e){
					if(!bAllowAddROI){
						return;
					}
					if(showConvertPopup(e,true) == SHOWCONVERT.HANDLED){
						return;
					}else if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && !brushToolHelper.isBrushSizeClickDragMode()){
						getImagePane().setBrush(brushToolHelper.getBrushShape(e));
						firePropertyChange(FRAP_DATA_PAINTERASE_FINISH_PROPERTY, null, e);
					}else if(SwingUtilities.isLeftMouseButton(e) && cropButton.isSelected()){
						ISize iSize = getISizeDataset();
						if(iSize == null || imagePane.getCrop() == null){
							imagePane.setCrop(null, null);
							return;
						}
						//Copy crop from mousedragged rectangle
						Rectangle cropRect =(Rectangle)imagePane.getCrop().clone();
						//Check crop within bounds of dataset
						cropRect.x = (int)(cropRect.x/imagePane.getZoom());
						cropRect.y = (int)(cropRect.y/imagePane.getZoom());
						cropRect.width = (int)(cropRect.width/imagePane.getZoom());
						cropRect.height = (int)(cropRect.height/imagePane.getZoom());
						if(cropRect.x < 0){cropRect.x = 0;}
						if(cropRect.y < 0){cropRect.y = 0;}
						if(cropRect.x >= iSize.getX() ||
							cropRect.y >= iSize.getY()){
							imagePane.setCrop(null, null);
							return;
						}
						if(cropRect.x+cropRect.width >= iSize.getX()){
							cropRect.width = iSize.getX()-cropRect.x;
						}
						if(cropRect.y+cropRect.height >= iSize.getY()){
							cropRect.height = iSize.getY()-cropRect.y;
						}
						if(cropRect.width <= 0){
							cropRect.width = 1;
						}if(cropRect.height <= 0){
							cropRect.height = 1;
						}

						//tell manager about crop
						firePropertyChange(FRAP_DATA_CROP_PROPERTY, null, cropRect);
					}else if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && brushToolHelper.isBrushSizeClickDragMode()){
						brushToolHelper.stopBrushSizeClickDragMode(e,imagePane);
					}
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					if(!bAllowAddROI){
						return;
					}
					if(showConvertPopup(e,true) == SHOWCONVERT.HANDLED){
						return;
					}else if(SwingUtilities.isLeftMouseButton(e) && fillButton.isSelected()){
						if(roiComboBox.getItemCount() == 0){
							giveROIRequiredWarning("fill");
							return;
						}
						firePropertyChange(FRAP_DATA_FILL_PROPERTY, null,
								new Point((int)(e.getPoint().getX()/imagePane.getZoom()),(int)(e.getPoint().getY()/imagePane.getZoom())));
					}else if(SwingUtilities.isLeftMouseButton(e) && selectButton.isSelected()){
						Object[] selectedResolvedObjects = resolvedList.getSelectedValues();
						if(selectedResolvedObjects != null && selectedResolvedObjects.length > 0){
							if(selectedResolvedObjects[0] instanceof String){
								return;
							}
						}
						fireSelectFromImage(e,false);
					}
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					//Set cursor
					changeCursor();
					if(!paintButton.isSelected() && !eraseButton.isSelected()){
						getImagePane().setBrush(null);
					}
				}
			});
			imagePane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {   
				@Override
				public void mouseDragged(java.awt.event.MouseEvent e) {
					updateLabel(e.getX(), e.getY());
					if(!bAllowAddROI){
						return;
					}
					if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && !brushToolHelper.isBrushSizeClickDragMode()){
						drawHighlight(e.getX(), e.getY(), brushToolHelper.getBrushSize(getImagePane().getZoom()), eraseButton.isSelected());
						lastMousePoint = e.getPoint();
					}else if(SwingUtilities.isLeftMouseButton(e) && cropButton.isSelected()){
						imagePane.setCrop(lastMousePoint, e.getPoint());
					}else if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && brushToolHelper.isBrushSizeClickDragMode()){
						brushToolHelper.dragBrushSizeClickDragMode(e, imagePane);
					}
				}
				@Override
				public void mouseMoved(java.awt.event.MouseEvent e) {
					updateLabel(e.getX(), e.getY());
					if(!brushToolHelper.isBrushSizeClickDragMode() && (paintButton.isSelected() || eraseButton.isSelected())){
						getImagePane().setBrush(brushToolHelper.getBrushShape(e));
						getImagePane().refreshImage();
					}
				}
			});
		}
		return imagePane;
	}
	
	private void changeCursor(){
		if(brushToolHelper.isBrushSizeClickDragMode()){
			imagePane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}else if(paintButton.isSelected()){
			imagePane.setCursor(paintCursor);
		}else if(eraseButton.isSelected()){
			imagePane.setCursor(eraserCursor);
		}else if(fillButton.isSelected()){
			imagePane.setCursor(fillCursor);
		}else if(cropButton.isSelected()){
			imagePane.setCursor(cropCursor);
		}else{
			imagePane.setCursor(Cursor.getDefaultCursor());
		}
	}
	private Cursor createCursor(String cursorName){
		URL cursorURL = null;
		Dimension bestCursorDim = Toolkit.getDefaultToolkit().getBestCursorSize(16, 16);
		boolean bUseSmall = true;
		if((bestCursorDim.width*bestCursorDim.height) == (32*32)){
			bUseSmall = false;
		}
		int xp = 0;
		int yp = 0;
		if(cursorName.equals("paint")){
			cursorURL = (bUseSmall?OverlayEditorPanelJAI.class.getResource("/images/paint_cursor_16x16.gif"):OverlayEditorPanelJAI.class.getResource("/images/paint_cursor_32x32.gif"));
		}else if(cursorName.equals("eraser")){
			cursorURL = (bUseSmall?OverlayEditorPanelJAI.class.getResource("/images/eraser_cursor_16x16.gif"):OverlayEditorPanelJAI.class.getResource("/images/eraser_cursor_32x32.gif"));
			yp = 15;
		}else if(cursorName.equals("fill")){
			cursorURL = (bUseSmall?OverlayEditorPanelJAI.class.getResource("/images/fill_cursor_16x16.gif"):OverlayEditorPanelJAI.class.getResource("/images/fill_cursor_32x32.gif"));
			yp = 15;
		}else if(cursorName.equals("crop")){
			cursorURL = (bUseSmall?OverlayEditorPanelJAI.class.getResource("/images/crop_cursor_16x16.gif"):OverlayEditorPanelJAI.class.getResource("/images/crop_cursor_32x32.gif"));
		}
		if(cursorURL != null){
			ImageIcon imageIcon = new ImageIcon(cursorURL);
			if(imageIcon.getImage() != null){
				return Toolkit.getDefaultToolkit().createCustomCursor(imageIcon.getImage(), new Point(xp,yp), cursorName);
			}
		}
		return Cursor.getDefaultCursor();
	}
	private void giveROIRequiredWarning(String toolDescription){
		ActionEvent actionEvent = new ActionEvent(this, 0, "You must add at least 1 Domain before trying to use the '"+toolDescription+"' tool.");
		addROIActionListener.actionPerformed(actionEvent);
	}

	public void cropDrawAndConfirm(Rectangle cropRect){
		try{
			imagePane.setCrop(
				new Point(
					(int)(cropRect.x*imagePane.getZoom()),
					(int)(cropRect.y*imagePane.getZoom())),
				new Point(
					(int)((cropRect.x+cropRect.width)*imagePane.getZoom()),
					(int)((cropRect.y+cropRect.height)*imagePane.getZoom())));
			
			JLabel cropConfirmJlabel =
				new JLabel("Crop data to new bounds?: ("+cropRect.x+","+cropRect.y+") to ("+
						(cropRect.x+cropRect.width-1)+","+(cropRect.y+cropRect.height-1)+")");
			cropConfirmJlabel.setPreferredSize(new Dimension(300,40));
			cropConfirmJlabel.setMinimumSize(new Dimension(300,40));
			if(DialogUtils.showComponentOKCancelDialog(
				OverlayEditorPanelJAI.this,
				cropConfirmJlabel,
				"Confirm Crop Data to new boundaries.") != JOptionPane.OK_OPTION){
				throw UserCancelException.CANCEL_GENERIC;
			}
		}finally{
			imagePane.setCrop(null, null);

		}
	}
	/**
	 * Method drawHighlight.
	 * @param x int
	 * @param y int
	 * @param radius int
	 * @param erase boolean
	 */
	private void drawHighlight(int x, int y, int radius, boolean bErase){
		if(roiComboBox.getSelectedItem() == null){
			return;
		}
//		histogramPanel.highlightsChanged(FRAP_DATA_PAINT_PROPERTY);
		imagePane.drawPaint(x, y, radius, bErase, /*highlightColor,*/
				((ROIMultiPaintManager.ComboboxROIName)roiComboBox.getSelectedItem()).getHighlightColor(), lastMousePoint);
//		repaint();
	}

	/**
	 * This method initializes timeSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getTimeSlider() {
		if (timeSlider == null) {
			timeSlider = new JSlider(1,1);
			timeSlider.setPaintLabels(true);
			timeSlider.setMajorTickSpacing(100);
			timeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					forceImage();
				}
			});
		}
		return timeSlider;
	}
	private void forceImage(){
		updateLabel(-1, -1);
		if(imageDataset != null){
			BufferedImage image = getImage();
			if (image != null){
				imagePane.setUnderlyingImage(image,/*false,*/allPixelValuesRange);
			}
		}
		imagePane.repaint();

	}
	/** Gets the index of the currently displayed image. * @return int
	 */
	private int getImageIndex() { return imageDataset.getIndexFromZCT(getZ(),0,getT()); }
	
	/** Gets the index of the currently displayed image. * @return int
	 */
	private int getRoiImageIndex() { return getZ(); }
	
	/** Gets the T value of the currently displayed image. * @return int
	 */
	public int getT() { return Math.max(0,Math.min(imageDataset.getSizeT(),timeSlider.getValue())) - 1; }

	/** Gets the Z value of the currently displayed image. * @return int
	 */
	public int getZ() { return Math.max(0,Math.min(imageDataset.getSizeZ(),zSlider.getValue())) - 1; }


	/** Updates cursor probe label. * @param x int
	 * @param y int
	 */
	private void updateLabel(int inx, int iny) {
		if (imageDataset == null) {
			return;
		}
		float zoom = imagePane.getZoom();
		if((inx/zoom)>= imageDataset.getISize().getX() || (iny/zoom)>= imageDataset.getISize().getY()){
			inx = -1;
			iny = -1;
		}
		int x = (int)(inx/zoom);
		int y = (int)(iny/zoom);
		UShortImage[] images = imageDataset.getAllImages();
		sb.setLength(0);
		boolean bMultipleZ = imageDataset.getSizeZ() > 1;
		if (bMultipleZ) {
			sb.append("; Z=");
			sb.append(getZ() + 1);
			sb.append("/");
			sb.append(imageDataset.getSizeZ());
		}
		if (imageDataset.getSizeT() > 1) {
			sb.append((bMultipleZ?"; ":"")+"T=");
			sb.append(imageDataset.getImageTimeStamps()[getT()]);
		}
		int w = imageDataset == null ? -1 : imageDataset.getISize().getX();
		int h = imageDataset == null ? -1 : imageDataset.getISize().getY();
		if (x >= w) x = w - 1;
		if (y >= h) y = h - 1;
		if (x >= 0 && y >= 0 && inx >= 0 && iny >= 0 &&
				x < imageDataset.getISize().getX() &&
				y < imageDataset.getISize().getY()){
			if (images.length > 1) sb.append("; ");
			sb.append("X=");
			sb.append(x);
			if (w > 0) {
				sb.append("/");
				sb.append(w);
			}
			sb.append("; Y=");
			sb.append(y);
			if (h > 0) {
				sb.append("/");
				sb.append(h);
			}
			sb.append("; zoom("+NumberUtils.formatNumber(imagePane.getZoom(), 3)+")");
			sb.append("; contr("+imagePane.getContrastDescription()+")");
			if (imageDataset != null) {
				short[] pix = null;
				try{
					pix = new short[imageDataset.getSizeC()];
					for (int i = 0; i < pix.length; i++) {
						pix[i] = imageDataset.getImage(getZ(), i, getT()).getPixel(x, y, 0);						
					}
				}catch(Exception e){
					pix = null;
					e.printStackTrace();
					//do nothing
				}
				sb.append("; value"+(isOriginalValueScaled()?"(scld)":""));
				if(pix != null){
					if(histogramPanel.isVisible()){
						histogramPanel.setSpecialValue((int)(pix[0]&0x0000FFFF));
					}
					sb.append(pix.length > 1 ? "s=(" : "=");
					for (int i=0; i<pix.length; i++) {
						if (i > 0) sb.append(", ");
						sb.append((int)(pix[i]&0x0000FFFF));
					}
					if (pix.length > 1) sb.append(")");
				}else{
					sb.append(" error");
				}
				if(isOriginalValueScaled()){
					sb.append("; value(orig)");
					if(pix != null){
						sb.append(pix.length > 1 ? "s=(" : "=");
						for (int i=0; i<pix.length; i++) {
							if (i > 0) sb.append(", ");
							sb.append(
								NumberUtils.formatNumber(
								(((int)(pix[i]&0x0000FFFF))-originalOffsetFactor)/
								originalScaleFactor
								, 6));
						}
						if (pix.length > 1) sb.append(")");
					}else{
						sb.append(" error");
					}
					
				}
			}
		}else{
			sb.append((sb.length() != 0?"; ":"")+"zoom("+NumberUtils.formatNumber(imagePane.getZoom(), 3)+")");
			sb.append("; contr("+imagePane.getContrastDescription()+")");
			if(histogramPanel.isVisible()){
				histogramPanel.setSpecialValue(null);
			}
		}
		sb.append(" ");
		textLabel.setText(sb.toString());
	}

	private boolean isOriginalValueScaled(){
		if(originalOffsetFactor != 0 || originalScaleFactor != 1){
			return true;
		}
		return false;
	}
	/**
	 * This method initializes jScrollPane2	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getImagePane());
		}
		return jScrollPane2;
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	
	public static class BrushToolHelper extends MouseAdapter implements ChangeListener{
		public interface CursorChanger{
			void changeCursors();
		}
		private boolean bBrushSizeMode = false;
		private int brushRadius;
		private AbstractButton[] jButtons;
		private JPopupMenu jPopupMenu;
		CursorChanger cursorChanger;
		public BrushToolHelper(AbstractButton[] jButtons,int brushRadius,CursorChanger cursorChanger){
			this.jButtons = jButtons;
			this.brushRadius = brushRadius;
			this.cursorChanger = cursorChanger;
			
			jPopupMenu = new JPopupMenu();
			JMenuItem sizeManual = new JMenuItem("brush size manual...");
			sizeManual.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						String result = DialogUtils.showInputDialog0(BrushToolHelper.this.jButtons[0].getParent(), "Enter brush radius (0 for single pixel):", BrushToolHelper.this.brushRadius+"");
						int newcircleSize = Integer.parseInt(result);
						if(newcircleSize < 0){
							throw new Exception("brush radius must be >= 0");
						}
						BrushToolHelper.this.brushRadius = newcircleSize;
					}catch(UtilCancelException uce){
						//ignore, user cancelled
					}catch(Exception e2){
						DialogUtils.showErrorDialog(BrushToolHelper.this.jButtons[0].getParent(), "Error: "+e2.getMessage());
					}
				}
			});
			JMenuItem sizeClickDrag = new JMenuItem("brush size click/drag...");
			sizeClickDrag.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					bBrushSizeMode = true;
				}
			});
			
			jPopupMenu.add(sizeManual);
			jPopupMenu.add(sizeClickDrag);
			
			for(AbstractButton jButton:jButtons){
				jButton.addMouseListener(this);
				jButton.addChangeListener(this);
			}
		}
		private MouseEvent clickDragStart;
//		private MouseEvent clickDragEnd;
		public void startBrushSizeClickDragMode(MouseEvent e){
			clickDragStart = e;
			updateBrushRadius(null);
		}
		public interface BrushRefresh {
			void setBrush(Ellipse2D.Double brushShape);
			void refreshImage();
		}
		public void stopBrushSizeClickDragMode(MouseEvent e,BrushRefresh overlayImageDisplayJAI){
			bBrushSizeMode = false;
			if(cursorChanger!=null){
				cursorChanger.changeCursors();
			}
			overlayImageDisplayJAI.setBrush(getBrushShape(e));
			clickDragStart = null;
			overlayImageDisplayJAI.refreshImage();
		}
		public void dragBrushSizeClickDragMode(MouseEvent e,BrushRefresh overlayImageDisplayJAI){
			updateBrushRadius(e);
			overlayImageDisplayJAI.setBrush(getBrushShape(e));
			overlayImageDisplayJAI.refreshImage();
		}
		private void updateBrushRadius(MouseEvent clickDragEnd){
			brushRadius = (int)clickDragStart.getPoint().distance((clickDragEnd==null?clickDragStart.getPoint():clickDragEnd.getPoint()));
		}
		public Ellipse2D.Double getBrushShape(MouseEvent e){
			return new Ellipse2D.Double(
					e.getPoint().getX()-brushRadius,
					e.getPoint().getY()-brushRadius,
					brushRadius*2,brushRadius*2
				);
		}
		public boolean isBrushSizeClickDragMode(){
			return bBrushSizeMode;
		}
		public int getBrushSize(double zoom){
			return (int)(brushRadius*2/zoom);
		}
		public void hidePopup(){
			if(jPopupMenu.isVisible()){
				jPopupMenu.setVisible(false);
			}
		}
		private void doClick(MouseEvent e){
			for(AbstractButton jButton:jButtons){
				if(e.getSource() == jButton){
					jButton.doClick();
					break;
				}
			}
		}
		@Override
		public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			if(e.isPopupTrigger()){
				doClick(e);
				jPopupMenu.show((Component)e.getSource(), e.getX(), e.getY());
			}
		}
		@Override
		public void mouseReleased(final MouseEvent e) {
			super.mouseReleased(e);
			if(e.isPopupTrigger()){
				doClick(e);
				jPopupMenu.show((Component)e.getSource(), e.getX(), e.getY());
			}
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseClicked(e);
			if(e.isPopupTrigger()){
				doClick(e);
				jPopupMenu.show((Component)e.getSource(), e.getX(), e.getY());
			}
		}
		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			if(bBrushSizeMode){
				boolean bAllOff = true;
				for(AbstractButton jButton:jButtons){
					if(jButton.isSelected()){
						bAllOff = false;
						break;
					}
				}
				if(bAllOff){
					bBrushSizeMode = false;
					if(cursorChanger != null){
						cursorChanger.changeCursors();
					}
				}
			}
		}
		
	};

	private BrushToolHelper brushToolHelper;
	
	private JPanel getToolButtonPanel() {
		if (toolButtonPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.ipady = 0;
			gridBagConstraints3.ipadx = 0;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.ipady = 0;
			gridBagConstraints.gridy = 1;
			
			GridBagConstraints gridBagConstraintsUndo = new GridBagConstraints();
			gridBagConstraintsUndo.insets = new Insets(0, 0, 5, 5);
			gridBagConstraintsUndo.gridy = 0;
			gridBagConstraintsUndo.ipady = 0;
			gridBagConstraintsUndo.ipadx = 0;
			gridBagConstraintsUndo.gridx = 0;
			
			toolButtonPanel = new JPanel();
			final GridBagLayout gbl_toolButtonPanel = new GridBagLayout();
			gbl_toolButtonPanel.rowHeights = new int[] {0,0,0,0,0,0, 0};
			toolButtonPanel.setLayout(gbl_toolButtonPanel);
			toolButtonPanel.add(getUndoButton(), gridBagConstraintsUndo);
			
			borderToolButton = new JButton(new ImageIcon(getClass().getResource("/images/addBorder.gif")));
			borderToolButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					firePropertyChange(FRAP_DATA_PAD_PROPERTY, null, null);
				}
			});
			
			selectButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/geSelect.gif")));
			selectButton.setToolTipText("Disitnct domain region pick tool");
			selectButton.setPreferredSize(new Dimension(32, 32));
			selectButton.setMinimumSize(new Dimension(32, 32));
			selectButton.setMaximumSize(new Dimension(32, 32));
			GridBagConstraints gbc_selectButton = new GridBagConstraints();
			gbc_selectButton.insets = new Insets(0, 0, 5, 0);
			gbc_selectButton.gridx = 1;
			gbc_selectButton.gridy = 0;
			toolButtonPanel.add(selectButton, gbc_selectButton);
			borderToolButton.setToolTipText("Add/Crop borders or Resize dataset");
			borderToolButton.setMinimumSize(new Dimension(32, 32));
			borderToolButton.setMaximumSize(new Dimension(32, 32));
			borderToolButton.setPreferredSize(new Dimension(32, 32));
			GridBagConstraints gbc_borderToolButton = new GridBagConstraints();
			gbc_borderToolButton.insets = new Insets(10, 0, 0, 5);
			gbc_borderToolButton.gridx = 0;
			gbc_borderToolButton.gridy = 6;
			toolButtonPanel.add(borderToolButton, gbc_borderToolButton);
			toolButtonPanel.add(getZoomInButton(), gridBagConstraints);
			toolButtonPanel.add(getZoomOutButton(), gridBagConstraints3);

			contrastButtonPlus = new JButton(new ImageIcon(getClass().getResource("/images/contrastUp.gif")));
			contrastButtonPlus.setName("contrastPlusBtn");
			contrastButtonPlus.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					imagePane.increaseContrast();
					updateLabel(-1,-1);
				}
			});
			contrastButtonPlus.setPreferredSize(new Dimension(32, 32));
			contrastButtonPlus.setMinimumSize(new Dimension(32, 32));
			contrastButtonPlus.setMaximumSize(new Dimension(32, 32));
			contrastButtonPlus.setMargin(new Insets(2, 2, 2, 2));
			contrastButtonPlus.setToolTipText("Increase Contrast");
			final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
			gridBagConstraints_4.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints_4.gridy = 2;
			gridBagConstraints_4.gridx = 0;
			toolButtonPanel.add(contrastButtonPlus, gridBagConstraints_4);

			contrastButtonMinus = new JButton(new ImageIcon(getClass().getResource("/images/contrastDown.gif")));
			contrastButtonMinus.setName("contrastMinusBtn");
			contrastButtonMinus.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					imagePane.decreaseContrast();
					updateLabel(-1,-1);
				}
			});
			contrastButtonMinus.setPreferredSize(new Dimension(32, 32));
			contrastButtonMinus.setMinimumSize(new Dimension(32, 32));
			contrastButtonMinus.setMaximumSize(new Dimension(32, 32));
			contrastButtonMinus.setMargin(new Insets(2, 2, 2, 2));
			contrastButtonMinus.setToolTipText("Decrease Contrast");
			final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
			gridBagConstraints_6.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints_6.gridy = 2;
			gridBagConstraints_6.gridx = 1;
			toolButtonPanel.add(contrastButtonMinus, gridBagConstraints_6);

			cropButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/crop.gif")));
			cropButton.setName("roiCropBtn");
			cropButton.setPreferredSize(new Dimension(32, 32));
			cropButton.setMinimumSize(new Dimension(32, 32));
			cropButton.setMaximumSize(new Dimension(32, 32));
			cropButton.setMargin(new Insets(2, 2, 2, 2));
			cropButton.setToolTipText("Crop");
			final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
			gridBagConstraints_5.insets = new Insets(10, 0, 5, 5);
			gridBagConstraints_5.gridy = 3;
			gridBagConstraints_5.gridx = 0;
			toolButtonPanel.add(cropButton, gridBagConstraints_5);
			
			
			autoCropButton.setPreferredSize(new Dimension(32, 32));
			autoCropButton.setMinimumSize(new Dimension(32, 32));
			autoCropButton.setMaximumSize(new Dimension(32, 32));
			autoCropButton.setMargin(new Insets(2, 2, 2, 2));
			autoCropButton.setToolTipText("Auto Crop");
			final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
			gridBagConstraints_7.insets = new Insets(10, 0, 5, 0);
			gridBagConstraints_7.gridy = 3;
			gridBagConstraints_7.gridx = 1;
			toolButtonPanel.add(autoCropButton, gridBagConstraints_7);
			
			paintButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/paint.gif")));
			paintButton.setName("roiPaintBtn");
			paintButton.setSelected(true);
			paintButton.setPreferredSize(new Dimension(32, 32));
			paintButton.setMinimumSize(new Dimension(32, 32));
			paintButton.setMaximumSize(new Dimension(32, 32));
			paintButton.setPreferredSize(new Dimension(32, 32));
			paintButton.setMinimumSize(new Dimension(32, 32));
			paintButton.setMaximumSize(new Dimension(32, 32));
			paintButton.setMargin(new Insets(2, 2, 2, 2));
			paintButton.setToolTipText("Paint, rt-clk menu");
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints_1.gridy = 4;
			gridBagConstraints_1.gridx = 0;
			toolButtonPanel.add(paintButton, gridBagConstraints_1);

			eraseButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/eraser.gif")));
			eraseButton.setName("roiEraseBtn");
			eraseButton.setPreferredSize(new Dimension(32, 32));
			eraseButton.setMinimumSize(new Dimension(32, 32));
			eraseButton.setMaximumSize(new Dimension(32, 32));
			eraseButton.setPreferredSize(new Dimension(32, 32));
			eraseButton.setMinimumSize(new Dimension(32, 32));
			eraseButton.setMaximumSize(new Dimension(32, 32));
			eraseButton.setMargin(new Insets(2, 2, 2, 2));
			eraseButton.setToolTipText("Erase, rt-clk menu");
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints_2.gridy = 4;
			gridBagConstraints_2.gridx = 1;
			toolButtonPanel.add(eraseButton, gridBagConstraints_2);

			brushToolHelper = new BrushToolHelper(new JToggleButton[] {paintButton,eraseButton}, 10,
				new BrushToolHelper.CursorChanger() {
					@Override
					public void changeCursors() {
						OverlayEditorPanelJAI.this.changeCursor();
					}
				});
			getImagePane().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					super.mouseEntered(e);
					brushToolHelper.hidePopup();
				}
				
			});

			fillButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/fill.gif")));
			fillButton.setName("roiFillBtn");
			fillButton.setPreferredSize(new Dimension(32, 32));
			fillButton.setMinimumSize(new Dimension(32, 32));
			fillButton.setMaximumSize(new Dimension(32, 32));
			fillButton.setPreferredSize(new Dimension(32, 32));
			fillButton.setMinimumSize(new Dimension(32, 32));
			fillButton.setMaximumSize(new Dimension(32, 32));
			fillButton.setMargin(new Insets(2, 2, 2, 2));
			fillButton.setToolTipText("Fill");
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints_3.gridy = 5;
			gridBagConstraints_3.gridx = 0;
			toolButtonPanel.add(fillButton, gridBagConstraints_3);
			
			clearROIbutton.setPreferredSize(new Dimension(32, 32));
			clearROIbutton.setMinimumSize(new Dimension(32, 32));
			clearROIbutton.setMaximumSize(new Dimension(32, 32));
			clearROIbutton.setPreferredSize(new Dimension(32, 32));
			clearROIbutton.setMinimumSize(new Dimension(32, 32));
			clearROIbutton.setMaximumSize(new Dimension(32, 32));
			clearROIbutton.setMargin(new Insets(2, 2, 2, 2));
			clearROIbutton.setToolTipText("Clear Domain");
			final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
			gridBagConstraints_9.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints_9.gridy = 5;
			gridBagConstraints_9.gridx = 1;
			toolButtonPanel.add(clearROIbutton, gridBagConstraints_9);
			
			extrudeToolButton = new JButton(new ImageIcon(getClass().getResource("/images/extrude.gif")));
			extrudeToolButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					firePropertyChange(FRAP_DATA_DUPLICATE_PROPERTY, null, null);
				}
			});
			extrudeToolButton.setToolTipText("Extrude 2D to 3D");
			extrudeToolButton.setPreferredSize(new Dimension(32, 32));
			extrudeToolButton.setMinimumSize(new Dimension(32, 32));
			extrudeToolButton.setMaximumSize(new Dimension(32, 32));
			GridBagConstraints gbc_extrudeToolButton = new GridBagConstraints();
			gbc_extrudeToolButton.insets = new Insets(10, 0, 0, 0);
			gbc_extrudeToolButton.gridx = 1;
			gbc_extrudeToolButton.gridy = 6;
			toolButtonPanel.add(extrudeToolButton, gbc_extrudeToolButton);
			
		}
		return toolButtonPanel;
	}

	private JButton undoButton;
	private JLabel smoothOrigLabel;
	private JLabel smootherLabel;
	private JList resolvedList;
	private JScrollPane scrollPane;
	private JButton getUndoButton() {
		if (undoButton == null) {
			undoButton = new JButton();
			undoButton.setName("undoBtn");
			undoButton.setMargin(new Insets(2, 2, 2, 2));
			undoButton.setMinimumSize(new Dimension(32, 32));
			undoButton.setMaximumSize(new Dimension(32, 32));
			undoButton.setIcon(new ImageIcon(getClass().getResource("/images/undo.gif")));
			undoButton.setPreferredSize(new Dimension(32, 32));
			undoButton.setToolTipText("Zoom In");
			undoButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					firePropertyChange(FRAP_DATA_UNDOROI_PROPERTY, null, null);
				}
			});
		}
		return undoButton;
	}

	/**
	 * This method initializes zoomInButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getZoomInButton() {
		if (zoomInButton == null) {
			zoomInButton = new JButton();
			zoomInButton.setName("zoomInBtn");
			zoomInButton.setMargin(new Insets(2, 2, 2, 2));
			zoomInButton.setMinimumSize(new Dimension(32, 32));
			zoomInButton.setMaximumSize(new Dimension(32, 32));
			zoomInButton.setPreferredSize(new Dimension(32, 32));
			ZoomShapeIcon.setZoomOverlayEditorMod(zoomInButton, ZoomShapeIcon.Sign.plus);
			zoomInButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getImagePane().setZoom(getImagePane().getZoom()*1.3f);
					updateLabel(-1,-1);
				}
			});
		}
		return zoomInButton;
	}
	
	/**
	 * This method initializes zoomOutButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getZoomOutButton() {
		if (zoomOutButton == null) {
			zoomOutButton = new JButton();
			zoomOutButton.setName("zoomOutBtn");
			zoomOutButton.setPreferredSize(new Dimension(32, 32));
			zoomOutButton.setMinimumSize(new Dimension(32, 32));
			zoomOutButton.setMaximumSize(new Dimension(32, 32));
			zoomOutButton.setPreferredSize(new Dimension(32, 32));
			zoomOutButton.setMargin(new Insets(2, 2, 2, 2));
			ZoomShapeIcon.setZoomOverlayEditorMod(zoomOutButton, ZoomShapeIcon.Sign.minus);
			zoomOutButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getImagePane().setZoom(getImagePane().getZoom()/1.3f);
					updateLabel(-1,-1);
				}
			});
		}
		return zoomOutButton;
	}


	/**
	 * This method initializes zSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getZSlider() {
		if (zSlider == null) {
			zSlider = new JSlider(1,1);
			zSlider.setMajorTickSpacing(100);
			zSlider.setPaintLabels(true);
			zSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					saveUserChangesToROI();
					updateLabel(-1, -1);
					BufferedImage image = getImage();
					if (image != null){
						imagePane.setUnderlyingImage(image,/*false,*/allPixelValuesRange);
					}
					refreshROI();
					imagePane.repaint();
				}
			});
		}
		return zSlider;
	}
	
	public void setDefaultImportDirAndFilters(File defaultImportDir,FileFilter[] fileFilterArr){
	    openJFileChooser.setCurrentDirectory(defaultImportDir);
	    for (int i = 0; i < fileFilterArr.length; i++) {
	    	openJFileChooser.addChoosableFileFilter(fileFilterArr[i]);
		}
	}
	public void setUndoAndFocus(Boolean bUndo){
		if(bUndo != null){
			getUndoButton().setEnabled(bUndo);
		}
		//needed because focus for this window is lost (prevents keyboard event listening for ctrl-z) when button enable changes
		//or dialogs are shown for user actions.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				OverlayEditorPanelJAI.this.requestFocusInWindow();
			}
		});
		
	}

	private static final String ICON_BKGRND_NAME = "bkgrnd";
	private ImageIcon[] resolvedColorPatchImageIconArr;
	private DefaultListCellRenderer resolvedObjectListCellRenderer = new DefaultListCellRenderer(){
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			try {
				String listText = value.toString();
				RegionInfo regionInfo = null;
				if(value instanceof RegionInfo){
					regionInfo = (RegionInfo)value;
					if(regionInfo.getPixelValue() == 0){
						listText = ICON_BKGRND_NAME+" ("+regionInfo.getNumPixels()+")";
					}else{
						ComboboxROIName comboboxROIName = getComboboxROIName(regionInfo);
						listText = comboboxROIName.getROIName()+" ("+regionInfo.getNumPixels()+")";
					}
				}
				Component comp = super.getListCellRendererComponent(list, listText, index, isSelected,
						cellHasFocus);
				if(value instanceof String && comp instanceof JLabel){
					((JLabel)comp).setText((String)value);
				}else if(regionInfo != null && comp instanceof JLabel){
					if(resolvedColorPatchImageIconArr == null){
						resolvedColorPatchImageIconArr = new ImageIcon[ROIMultiPaintManager.getContrastIndexColorModel().getMapSize()];
						for (int j = 0; j < resolvedColorPatchImageIconArr.length; j++) {
							BufferedImage resolvedListColorPatch =
								new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_INDEXED,ROIMultiPaintManager.getContrastIndexColorModel());
							byte[] colorData =
								((DataBufferByte)resolvedListColorPatch.getRaster().getDataBuffer()).getData();
							Arrays.fill(colorData, (byte)(j&0x000000FF));
							resolvedColorPatchImageIconArr[j] = new ImageIcon(resolvedListColorPatch);
						}
					}
					((JLabel)comp).setIcon(resolvedColorPatchImageIconArr[regionInfo.getPixelValue()]);
				}

				return comp;
			} catch (Exception e) {
				setText("error:"+e.getMessage());
				return this;
			}
		}
		
	};
	private JLabel lblNewLabel;
	public void setUnderlayState(boolean bIgnoreUnderlay){
		if(bIgnoreUnderlay){
			blendPercentSlider.setValue(0);
			BeanUtils.enableComponents(blendPercentPanel, false);
		}else{
//			blendPercentSlider.setValue(50);
			BeanUtils.enableComponents(blendPercentPanel, true);
		}
	}
	public void setResolvedList(final Object[] allRegionInfos){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				resolvedList.setListData(allRegionInfos);
				if(allRegionInfos.length == 1){
					domainRegionLabel.setText(DOMAIN_LIST_TEXT);
				}else{
					domainRegionLabel.setText(allRegionInfos.length+" "+DOMAIN_LIST_TEXT);
				}
				resolvedListSelection(false);
			}
		});
	}
	public void setUserPreferences(UserPreferences userPreferences){
		histogramPanel.setUserPreferences(userPreferences);
	}
}
