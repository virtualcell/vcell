/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;
import java.awt.Color;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.vcell.util.Range;
/**
 * Insert the type's description here.
 * Creation date: (10/3/00 6:07:06 PM)
 * @author: 
 */
public class DisplayAdapterService implements org.vcell.util.Stateful, java.beans.PropertyChangeListener {

	public static final String PROP_NAME_AUTOSCALE = "autoScale";
	public static final String CUSTOM_SCALE_RANGE = "customScaleRange";
	public static final String ACTIVE_SCALE_RANGE = "activeScaleRange";
	public static final String PROP_NAME_ALLTIMES = "allTimes";
	
	public static final String BLUERED = "BlueRed";
	public static final String GRAY = "Gray";
	public static final String CIVIDIS = "Cividis";
	
	public class DisplayAdapterServiceState {
		private Range customScaleRange;
//		private int[] customSpecialColors;
		public DisplayAdapterServiceState(Range customScaleRange/*,int[] customSpecialColors*/) {
			this.customScaleRange = customScaleRange;
//			this.customSpecialColors = customSpecialColors;
		}
		public Range getCustomScaleRange() {
			return customScaleRange;
		}
//		public int[] getCustomSpecialColors(){
//			return customSpecialColors;
//		}
	}
	private java.util.Hashtable<String, DisplayAdapterServiceState> states = new java.util.Hashtable<String, DisplayAdapterServiceState>();
	protected transient java.beans.PropertyChangeSupport propertyChange;
	//
	private LinkedHashMap<String, int[]> colorModels = new LinkedHashMap<String, int[]>();
	private java.util.Hashtable<String, int[]> specialColors = new java.util.Hashtable<String, int[]>();
	private java.lang.String fieldActiveColorModelID = null;
	private Range fieldActiveScaleRange = null;
	private Range fieldValueDomain = null;
	private int[] fieldActiveColorModel = null;
	private int[] fieldSpecialColors = null;
	//
	public static final int ERROR_COLOR = Color.pink.getRGB();
	//Note: change this to the number of special colors defined below
	public static final int NUM_SPECIAL_COLORS = 8;
	public static final int BELOW_MIN_COLOR_OFFSET = 0;
	public static final int ABOVE_MAX_COLOR_OFFSET = 1;
	public static final int NAN_COLOR_OFFSET = 2;
	public static final int NOT_IN_DOMAIN_COLOR_OFFSET = 3;
	public static final int NO_RANGE_COLOR_OFFSET = 4;
	public static final int FOREGROUND_HIGHLIGHT_COLOR_OFFSET = 5;
	public static final int FOREGROUND_NONHIGHLIGHT_COLOR_OFFSET = 6;
	public static final int NULL_COLOR_OFFSET = 7;
	//
	double scaleRangeLength = 0;
	private static final int MAX_VALID_PIXEL_INDEX_UNKNOWN = -1;
	private int maxValidPixelIndex = MAX_VALID_PIXEL_INDEX_UNKNOWN;
	private java.lang.String fieldCurrentStateID = new String();
	private java.lang.String[] fieldColorModelIDs = null;
	private Range fieldCustomScaleRange = null;
	private boolean fieldAutoScale = false;

/**
 * DisplayAdapterService constructor comment.
 */
public DisplayAdapterService() {
	super();
	addPropertyChangeListener(this);
}


/**
 * DisplayAdapterService constructor comment.
 */
public DisplayAdapterService(DisplayAdapterService argDAS) {
	super();
	addPropertyChangeListener(this);

	states = (Hashtable<String, DisplayAdapterServiceState>)argDAS.states.clone();
	fieldColorModelIDs = (String[])argDAS.fieldColorModelIDs.clone();

	for (String s : argDAS.colorModels.keySet()) {
		colorModels.put(s, argDAS.colorModels.get(s).clone());
	}

	java.util.Enumeration<String> enum1 = argDAS.specialColors.keys();
	while(enum1.hasMoreElements()){
		String s = (String)enum1.nextElement();
		specialColors.put(s,((int[])argDAS.specialColors.get(s)).clone());
	}
	//
	fieldActiveColorModel = (int[])colorModels.get(argDAS.fieldActiveColorModelID);
	fieldSpecialColors = (int[])specialColors.get(argDAS.fieldActiveColorModelID);
	
	fieldActiveColorModelID = argDAS.fieldActiveColorModelID;
	fieldActiveScaleRange = argDAS.fieldActiveScaleRange;
	fieldValueDomain = argDAS.fieldValueDomain;
	scaleRangeLength = argDAS.scaleRangeLength;
	maxValidPixelIndex = argDAS.maxValidPixelIndex;
	java.lang.String fieldCurrentStateID = argDAS.fieldCurrentStateID;
	fieldCustomScaleRange = argDAS.fieldCustomScaleRange;
	fieldAutoScale = argDAS.fieldAutoScale;
	fieldAllTimes = argDAS.fieldAllTimes;
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:59:49 AM)
 * @param stateID java.lang.String
 */
public void activateMarkedState(java.lang.String stateID) {

	if (hasStateID(stateID)) {
		DisplayAdapterServiceState newState = (DisplayAdapterServiceState) states.get(stateID);
//		setSpecialColors(newState.getCustomSpecialColors().clone());
		setCustomScaleRange(newState.getCustomScaleRange());
	}else{
		setCustomScaleRange(null);
	}
	setAutoScale(getCustomScaleRange() == null);
	updateScaleRange();
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:30:43 AM)
 * @param colorModel java.awt.image.ColorModel
 * @param id java.lang.String
 */
private void addColorModel(int[] colorModel, int[] argSpecialColors, String id) {
	if (colorModel == null) {
		return;
	}
	if(id.equals(BLUERED) && !Arrays.equals(colorModel, createBlueRedColorModel())){
		throw new IllegalArgumentException(BLUERED+" color model id must match 'DisplayAdapterService.createBluRedColorModel()'");
	}else if(id.equals(GRAY) && !Arrays.equals(colorModel, createGrayColorModel())){
		throw new IllegalArgumentException(GRAY+" color model id must match 'DisplayAdapterService.createGrayColorModel()'");
	}else if(id.equals(CIVIDIS) && !Arrays.equals(colorModel, createCividisColorModel())){
		throw new IllegalArgumentException(CIVIDIS+" color model id must match 'DisplayAdapterService.createCividisColorModel()'");
	}
	colorModels.put(id, colorModel);
	if (argSpecialColors != null) {
		specialColors.put(id, argSpecialColors);
	}
	setColorModelIDs(fetchColorModelIDs());
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:30:43 AM)
 * @param colorModel java.awt.image.ColorModel
 * @param id java.lang.String
 */
public void addColorModelForIndexes(int[] colorModel,String id) {
	addColorModel(colorModel, null, id);
 } 


/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:30:43 AM)
 * @param colorModel java.awt.image.ColorModel
 * @param id java.lang.String
 */
public void addColorModelForValues(int[] colorModel, int[] argSpecialColors, String id) {
    addColorModel(colorModel, argSpecialColors, id);
 }


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 12:25:58 PM)
 */
public void clearMarkedState(java.lang.String stateID) {
	states.remove(stateID);
}


public void clearMarkedStates() {
	states.clear();
}


public final static int[] createBlueRedColorModel() {
	return createBlueRedColorModel0(true);
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/00 1:56:43 PM)
 * @return int[]
 */
public final static int[] createBlueRedColorModel0(boolean bSpecial) {
	int[] fullMap = new int[1148];
	// dark blue to blue
	for (int i = 128; i < 256; i++) {
		fullMap[i - 128] = new Color(0, 0, i).getRGB();
	}
	// blue to cyan
	for (int i = 1; i < 256; i++) {
		fullMap[i + 127] = new Color(0, i, 255).getRGB();
	}
	// cyan to green
	for (int i = 1; i < 256; i++) {
		fullMap[i + 382] = new Color(0, 255, 255 - i).getRGB();
	}
	// green to yellow
	for (int i = 1; i < 256; i++) {
		fullMap[i + 637] = new Color(i, 255, 0).getRGB();
	}
	// yellow to red
	for (int i = 1; i < 256; i++) {
		fullMap[i + 892] = new Color(255, 255 - i, 0).getRGB();
	}
	// now scale down to 256 colors
	int[] colorMap = new int[256];
	if(bSpecial){
		for (int i = 0; i < (256-NUM_SPECIAL_COLORS); i++) {
			colorMap[i] = fullMap[i * 1147 / (255-NUM_SPECIAL_COLORS)];
		}
	}else{
		for (int i = 0; i < (256); i++) {
			colorMap[i] = fullMap[i * 1147 / (255)];
		}		
	}
	//
	return colorMap;
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/00 1:56:43 PM)
 * @return int[]
 */
public final static int[] createBlueRedSpecialColors() {
	int[] blueRedSpecialColors = new int[NUM_SPECIAL_COLORS];
	// The 248 data colors are unchanged. These eight are the out-of-range and overlay
	// indicators. Legend order is below-min, above-max, NaN, not-in-domain, no-range,
	// and that row sits on a black gap. Each of those five clears 3:1 against the gap
	// and against its legend neighbor. Below-min also clears the dark-blue data end,
	// and above-max clears the red data end.
	blueRedSpecialColors[BELOW_MIN_COLOR_OFFSET] = new Color(140, 105, 0).getRGB();
	blueRedSpecialColors[ABOVE_MAX_COLOR_OFFSET] = Color.white.getRGB();
	blueRedSpecialColors[NAN_COLOR_OFFSET] = new Color(60, 108, 108).getRGB();
	blueRedSpecialColors[NOT_IN_DOMAIN_COLOR_OFFSET] = new Color(240, 252, 36).getRGB();
	blueRedSpecialColors[NO_RANGE_COLOR_OFFSET] = new Color(200, 0, 80).getRGB();
	blueRedSpecialColors[FOREGROUND_HIGHLIGHT_COLOR_OFFSET] = new Color(200, 80, 200).getRGB();
	blueRedSpecialColors[FOREGROUND_NONHIGHLIGHT_COLOR_OFFSET] = new Color(180, 180, 180).getRGB();
	blueRedSpecialColors[NULL_COLOR_OFFSET] = new Color(110, 110, 110).getRGB();
	return blueRedSpecialColors;
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/00 1:56:43 PM)
 * @return int[]
 */
public final static int[] createContrastColorModel() {
	int[] contrastColors = new int[256];
	for (int i = 0; i < 256; i += 1) {
		double hue        = (i%10)/10.0; //Math.sin(0*Math.PI/2.0 + 32.0*Math.PI*i/255)/2.0 + 0.5;
		double saturation = 1.0 - (i%2)*0.5; // alternating pattern of 1.0 and 0.5
		double brightness = 1.0 - (i%3)*0.25; // alternating pattern of 1.0 and 0.5
		contrastColors[i] = Color.getHSBColor((float)hue,(float)saturation,(float)brightness).getRGB();
	}
	return contrastColors;
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/00 1:56:43 PM)
 * @return int[]
 */
public final static int[] createGrayColorModel() {
    int[] gray = new int[256];
    for (int i = 0; i < (256-NUM_SPECIAL_COLORS); i += 1) {
        gray[i] = i | i << 8 | i << 16 | 0xFF << 24;//opaque colors
    }
    return gray;
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/00 1:56:43 PM)
 * @return int[]
 */
public final static int[] createGraySpecialColors() {
	int[] graySpecialColors = new int[NUM_SPECIAL_COLORS];
	//
	graySpecialColors[BELOW_MIN_COLOR_OFFSET] 					= Color.blue.getRGB();
	graySpecialColors[ABOVE_MAX_COLOR_OFFSET]					= Color.red.getRGB();
	graySpecialColors[NAN_COLOR_OFFSET]							= Color.yellow.getRGB();
	graySpecialColors[NOT_IN_DOMAIN_COLOR_OFFSET]						= Color.cyan.getRGB();
	graySpecialColors[NO_RANGE_COLOR_OFFSET]					= Color.magenta.getRGB();
	graySpecialColors[FOREGROUND_HIGHLIGHT_COLOR_OFFSET]		= new Color(0,192,192).getRGB();
	graySpecialColors[FOREGROUND_NONHIGHLIGHT_COLOR_OFFSET]		= new Color(0,160,160).getRGB();
	graySpecialColors[NULL_COLOR_OFFSET]						= new Color(0,64,64).getRGB();
	//
	return graySpecialColors;
}


/**
 * 248 data colors sampled from {@link CividisColorTable}, plus {@link #NUM_SPECIAL_COLORS}
 * unused slots reserved at the end of the 256-entry map.
 */
public final static int[] createCividisColorModel() {
	int[] colors = new int[256];
	int dataCount = 256 - NUM_SPECIAL_COLORS;
	for (int i = 0; i < dataCount; i += 1) {
		int source = (int) Math.round(i * 255.0 / (dataCount - 1));
		colors[i] = 0xFF000000 | CividisColorTable.RGB[source];
	}
	return colors;
}


/**
 * Out-of-range colors for Cividis. Each one contrasts at least 3:1 with both stored endpoints.
 * The measurement gradient is identified by value; these states are identified by
 * {@link #specialStateLabel(int)} as well as by color.
 */
public final static int[] createCividisSpecialColors() {
	int[] colors = new int[NUM_SPECIAL_COLORS];
	colors[BELOW_MIN_COLOR_OFFSET] = new Color(240, 8, 20).getRGB();
	colors[ABOVE_MAX_COLOR_OFFSET] = new Color(20, 120, 220).getRGB();
	colors[NAN_COLOR_OFFSET] = new Color(64, 136, 20).getRGB();
	colors[NOT_IN_DOMAIN_COLOR_OFFSET] = new Color(204, 60, 164).getRGB();
	colors[NO_RANGE_COLOR_OFFSET] = new Color(216, 68, 8).getRGB();
	colors[FOREGROUND_HIGHLIGHT_COLOR_OFFSET] = new Color(188, 52, 228).getRGB();
	colors[FOREGROUND_NONHIGHLIGHT_COLOR_OFFSET] = new Color(80, 116, 208).getRGB();
	colors[NULL_COLOR_OFFSET] = new Color(236, 20, 68).getRGB();
	return colors;
}


/**
 * WCAG 2 contrast ratio for two packed ARGB colors. Alpha is ignored.
 */
public static double contrastRatio(int argbA, int argbB) {
	double lighter = Math.max(relativeLuminance(argbA), relativeLuminance(argbB));
	double darker = Math.min(relativeLuminance(argbA), relativeLuminance(argbB));
	return (lighter + 0.05) / (darker + 0.05);
}

/**
 * Black or white text, whichever has the higher contrast on {@code fill}.
 */
public static int contrastingTextRgb(int fill) {
	double onBlack = contrastRatio(fill, 0xFF000000);
	double onWhite = contrastRatio(fill, 0xFFFFFFFF);
	return onBlack >= onWhite ? 0xFF000000 : 0xFFFFFFFF;
}

private static double relativeLuminance(int argb) {
	double r = linearize(((argb >> 16) & 0xFF) / 255.0);
	double g = linearize(((argb >> 8) & 0xFF) / 255.0);
	double b = linearize((argb & 0xFF) / 255.0);
	return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

private static double linearize(double channel) {
	return channel <= 0.04045 ? channel / 12.92 : Math.pow((channel + 0.055) / 1.055, 2.4);
}

/**
 * Text name for a special-color slot. The results viewer also shows a short label on the swatch.
 */
public static String specialStateLabel(int offset) {
	switch (offset) {
		case BELOW_MIN_COLOR_OFFSET:
			return "Below minimum";
		case ABOVE_MAX_COLOR_OFFSET:
			return "Above maximum";
		case NAN_COLOR_OFFSET:
			return "Not a number";
		case NOT_IN_DOMAIN_COLOR_OFFSET:
			return "Not in domain";
		case NO_RANGE_COLOR_OFFSET:
			return "No range";
		case FOREGROUND_HIGHLIGHT_COLOR_OFFSET:
			return "Highlight";
		case FOREGROUND_NONHIGHLIGHT_COLOR_OFFSET:
			return "Not highlighted";
		case NULL_COLOR_OFFSET:
			return "No value";
		default:
			throw new IllegalArgumentException("unknown special color offset " + offset);
	}
}


/**
 * Gray, BlueRed, then Cividis. {@link #fetchColorModelIDs()} returns this registration order.
 */
public static void addStandardColorModels(DisplayAdapterService das) {
	das.addColorModelForValues(createGrayColorModel(), createGraySpecialColors(), GRAY);
	das.addColorModelForValues(createBlueRedColorModel(), createBlueRedSpecialColors(), BLUERED);
	das.addColorModelForValues(createCividisColorModel(), createCividisSpecialColors(), CIVIDIS);
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:41:01 AM)
 * @return java.awt.image.ColorModel
 * @param colorModelID java.lang.String
 */
public int[] fetchColorModel(String colorModelID) {
	return (int[])colorModels.get(colorModelID);
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:39:13 AM)
 * @return java.lang.String[]
 */
private String[] fetchColorModelIDs() {
    if (colorModels.size() != 0) {
        return colorModels.keySet().toArray(new String[0]);
    } else {
        return null;
    }
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:41:01 AM)
 * @return java.awt.image.ColorModel
 * @param colorModelID java.lang.String
 */
public int[] fetchSpecialColors(String colorModelID) {
	return (int[])((int[])specialColors.get(colorModelID)).clone();
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Gets the activeColorModel property (int[]) value.
 * @return The activeColorModel property value.
 * @see #setActiveColorModel
 */
public int[] getActiveColorModel() {
	return fieldActiveColorModel;
}


/**
 * Gets the activeColorModelID property (java.lang.String) value.
 * @return The activeColorModelID property value.
 * @see #setActiveColorModelID
 */
public java.lang.String getActiveColorModelID() {
	return fieldActiveColorModelID;
}


/**
 * Gets the activeScaleRange property (cbit.image.Range) value.
 * @return The activeScaleRange property value.
 * @see #setActiveScaleRange
 */
public Range getActiveScaleRange() {
	return fieldActiveScaleRange;
}


/**
 * Gets the autoScale property (boolean) value.
 * @return The autoScale property value.
 * @see #setAutoScale
 */
public boolean getAutoScale() {
	return fieldAutoScale;
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/00 6:42:07 PM)
 * @return int
 * @param index int
 */
public int getColorFromIndex(int index) {
	if (index < 0 || fieldActiveColorModel == null || index >= fieldActiveColorModel.length) {
		return ERROR_COLOR;
	}
	return fieldActiveColorModel[index];
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/00 6:42:07 PM)
 * @return int
 * @param index int
 */
public int getColorFromValue(double dataValue) {
	//
	if (maxValidPixelIndex == MAX_VALID_PIXEL_INDEX_UNKNOWN) {
		//This happens when no colorModel is set
		return ERROR_COLOR;
	}
	//
	if (Double.isNaN(dataValue) || Double.isInfinite(dataValue)) {
		return fieldSpecialColors[NAN_COLOR_OFFSET];
	}
	//
	if (getValueDomain() != null) {
		if (dataValue < getValueDomain().getMin()) {
			return fieldSpecialColors[NOT_IN_DOMAIN_COLOR_OFFSET];
		} else if (dataValue > getValueDomain().getMax()) {
			return fieldSpecialColors[NOT_IN_DOMAIN_COLOR_OFFSET];
		}
	}
	//
	if (scaleRangeLength == 0) {
		return fieldSpecialColors[NO_RANGE_COLOR_OFFSET];
	}
	//
	//double pixelIndex = (maxValidPixelIndex * (dataValue - getActiveScaleRange().getMin()) / scaleRangeLength);
	////
	//if (pixelIndex < 0) {
		//return fieldSpecialColors[BELOW_MIN_COLOR_OFFSET];
	//} else if (pixelIndex > maxValidPixelIndex) {
		//return fieldSpecialColors[ABOVE_MAX_COLOR_OFFSET];
	//} else {
		//return fieldActiveColorModel[(int)pixelIndex];
	//}


	
	if (dataValue < getActiveScaleRange().getMin()) {
		return fieldSpecialColors[BELOW_MIN_COLOR_OFFSET];
	} else if (dataValue > getActiveScaleRange().getMax()) {
		return fieldSpecialColors[ABOVE_MAX_COLOR_OFFSET];
	} else {
		return fieldActiveColorModel[(int)(maxValidPixelIndex * (dataValue - getActiveScaleRange().getMin()) / scaleRangeLength)];
	}
}


/**
 * Gets the colorModelIDs property (java.lang.String[]) value.
 * @return The colorModelIDs property value.
 * @see #setColorModelIDs
 */
public java.lang.String[] getColorModelIDs() {
	return fieldColorModelIDs;
}


/**
 * Gets the currentStateID property (java.lang.String) value.
 * @return The currentStateID property value.
 * @see #setCurrentStateID
 */
public java.lang.String getCurrentStateID() {
	return fieldCurrentStateID;
}


/**
 * Gets the customScaleRange property (cbit.image.Range) value.
 * @return The customScaleRange property value.
 * @see #setCustomScaleRange
 */
public Range getCustomScaleRange() {
	return fieldCustomScaleRange;
}


/**
 * Insert the method's description here.
 * Creation date: (2/28/01 5:33:46 PM)
 * @return cbit.vcell.simdata.gui.DisplayPreferences
 * @param stateID java.lang.String
 */
public DisplayPreferences getDisplayPreferences(String stateID) {
    if (hasStateID(stateID) && !getAutoScale()) {
        DisplayAdapterServiceState storedState = (DisplayAdapterServiceState) states.get(stateID);
        return new DisplayPreferences(getActiveColorModelID(), storedState.getCustomScaleRange(),getSpecialColors()/*storedState.getCustomSpecialColors()*/,getAutoScale(),getAllTimes());
    } else {
        return new DisplayPreferences(getActiveColorModelID(), getActiveScaleRange(),getSpecialColors(),getAutoScale(),getAllTimes());
    }
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2005 9:26:25 AM)
 * @return int
 */
public int getNullColor() {
	return fieldSpecialColors[NULL_COLOR_OFFSET];
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the specialColors property (int[]) value.
 * @return The specialColors property value.
 * @see #setSpecialColors
 */
public int[] getSpecialColors() {
	return fieldSpecialColors;
}


/**
 * Gets the valueDomain property (cbit.image.Range) value.
 * @return The valueDomain property value.
 * @see #setValueDomain
 */
public Range getValueDomain() {
	return fieldValueDomain;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (11/2/2000 12:17:17 PM)
 * @return boolean
 * @param stateID java.lang.String
 */
public boolean hasStateID(java.lang.String stateID) {
	return states.containsKey(stateID);
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:59:49 AM)
 * @param stateID java.lang.String
 */
public void markCurrentState(java.lang.String stateID) {
	if(fieldActiveColorModelID != null && getActiveScaleRange() != null){
		states.put(stateID, new DisplayAdapterServiceState(getCustomScaleRange()/*,fetchSpecialColors(getActiveColorModelID())*/));
	}
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	//
	if (evt.getSource() == this) {
		if (evt.getPropertyName().equals(DisplayAdapterService.VALUE_DOMAIN_PROP)) {
			updateScaleRange();
		}else if (evt.getPropertyName().equals("defaultScaleRange")){
			updateScaleRange();
		}else if (evt.getPropertyName().equals(CUSTOM_SCALE_RANGE)){
			updateScaleRange();
		}else if (evt.getPropertyName().equals(PROP_NAME_AUTOSCALE)){
			updateScaleRange();
		}else if (evt.getPropertyName().equals(ACTIVE_SCALE_RANGE)){
		}else if (evt.getPropertyName().equals("activeColorModelID")){
		    if (getActiveColorModelID() != null) {
		        setActiveColorModel((int[]) colorModels.get(getActiveColorModelID()));
		    } else {
		        setActiveColorModel(null);
		    }
		    if (getActiveColorModelID() != null && specialColors.containsKey(getActiveColorModelID())) {
		        setSpecialColors((int[]) specialColors.get(getActiveColorModelID()));
		    } else {
		        setSpecialColors(null);
		    }
		}else if (evt.getPropertyName().equals("activeColorModel")){
		}else if (evt.getPropertyName().equals("specialColors")){
		}else if (evt.getPropertyName().equals("colorModelIDs")){
		}else if (evt.getPropertyName().equals("propertyChangeComplete")){
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:30:43 AM)
 * @param colorModel java.awt.image.ColorModel
 * @param id java.lang.String
 */
public void removeColorModel(String id) {
	colorModels.remove(id);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Sets the activeColorModel property (int[]) value.
 * @param activeColorModel The new value for the property.
 * @see #getActiveColorModel
 */
private void setActiveColorModel(int[] activeColorModel) {
	
	int[] oldValue = fieldActiveColorModel;
	fieldActiveColorModel = activeColorModel;
	if (fieldActiveColorModel != null && activeColorModel.length != 0) {
		maxValidPixelIndex = fieldActiveColorModel.length - 1 - NUM_SPECIAL_COLORS;
	} else {
		maxValidPixelIndex = MAX_VALID_PIXEL_INDEX_UNKNOWN;
	}
	firePropertyChange("activeColorModel", oldValue, activeColorModel);
}


/**
 * Sets the activeColorModelID property (java.lang.String) value.
 * @param activeColorModelID The new value for the property.
 * @see #getActiveColorModelID
 */
public void setActiveColorModelID(java.lang.String activeColorModelID) {
	    if (activeColorModelID != null) {
	        if (!colorModels.containsKey(activeColorModelID)) {
	            throw new IllegalArgumentException("No colormodel with id="+activeColorModelID+" found");
	        }
	    }
	    String oldValue = fieldActiveColorModelID;
	    fieldActiveColorModelID = activeColorModelID;
	    firePropertyChange("activeColorModelID", oldValue, activeColorModelID);
}


/**
 * Sets the activeScaleRange property (cbit.image.Range) value.
 * @param activeScaleRange The new value for the property.
 * @see #getActiveScaleRange
 */
public void setActiveScaleRange(Range activeScaleRange) {
	Range oldValue = fieldActiveScaleRange;
	fieldActiveScaleRange = activeScaleRange;
	if (fieldActiveScaleRange != null) {
		scaleRangeLength = fieldActiveScaleRange.getMax() - fieldActiveScaleRange.getMin();
	} else {
		scaleRangeLength = 0;
	}
	firePropertyChange(ACTIVE_SCALE_RANGE, oldValue, activeScaleRange);
}


/**
 * Sets the autoScale property (boolean) value.
 * @param autoScale The new value for the property.
 * @see #getAutoScale
 */
public void setAutoScale(boolean autoScale) {
	boolean oldValue = fieldAutoScale;
	fieldAutoScale = autoScale;
	firePropertyChange(PROP_NAME_AUTOSCALE, new Boolean(oldValue), new Boolean(autoScale));
}

private boolean fieldAllTimes = false;
public void setAllTimes(boolean bAllTimes) {
	boolean oldValue = fieldAllTimes;
	fieldAllTimes = bAllTimes;
	firePropertyChange(PROP_NAME_ALLTIMES, new Boolean(oldValue), new Boolean(fieldAllTimes));
}
public boolean getAllTimes(){
	return fieldAllTimes;
}
/**
 * Sets the colorModelIDs property (java.lang.String[]) value.
 * @param colorModelIDs The new value for the property.
 * @see #getColorModelIDs
 */
private void setColorModelIDs(java.lang.String[] colorModelIDs) {
	java.lang.String[] oldValue = fieldColorModelIDs;
	fieldColorModelIDs = colorModelIDs;
	firePropertyChange("colorModelIDs", oldValue, colorModelIDs);
}

/**
 * Sets the customScaleRange property (cbit.image.Range) value.
 * @param customScaleRange The new value for the property.
 * @see #getCustomScaleRange
 */
public void setCustomScaleRange(Range customScaleRange) {
	Range oldValue = fieldCustomScaleRange;
	fieldCustomScaleRange = customScaleRange;
	firePropertyChange(CUSTOM_SCALE_RANGE, oldValue, customScaleRange);
}


/**
 * Sets the specialColors property (int[]) value.
 * @param specialColors The new value for the property.
 * @see #getSpecialColors
 */
private void setSpecialColors(int[] specialColors) {
		if(specialColors == fieldSpecialColors){
			return;
		}
		if(specialColors != null && specialColors.length != NUM_SPECIAL_COLORS){
			return;
		}
		int[] oldValue = fieldSpecialColors;
		fieldSpecialColors = specialColors;
		firePropertyChange("specialColors", oldValue, specialColors);
}

/**
 * Sets the valueDomain property (cbit.image.Range) value.
 * @param valueDomain The new value for the property.
 * @see #getValueDomain
 */
public static String VALUE_DOMAIN_PROP = "valueDomain";
public void setValueDomain(Range valueDomain) {
	Range oldValue = fieldValueDomain;
	fieldValueDomain = valueDomain;
	firePropertyChange(VALUE_DOMAIN_PROP, oldValue, valueDomain);
}


/**
 * Comment
 */
private void updateScaleRange() {
	Range newASRange = null;
	if (getAutoScale()) {
		newASRange = getValueDomain();
	} else {
		if (getCustomScaleRange() != null) {
			newASRange =  getCustomScaleRange();
		} else {
			newASRange =  getValueDomain();
		}
	}
	setActiveScaleRange(newASRange);
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2005 10:51:30 AM)
 * @param newSpecialColors int[]
 */
public void updateSpecialColors(String colorModelID,int[] newSpecialColors) {

	int[] oldSpecialColors = (int[])specialColors.get(colorModelID);
	if(oldSpecialColors != null){
		if(oldSpecialColors.length != newSpecialColors.length){
			throw new IllegalArgumentException("NewSpecialColors length="+newSpecialColors.length+" != SpecialColors length="+oldSpecialColors.length);
		}
		boolean bChanged = false;
		for(int i=0;i<oldSpecialColors.length;i+= 1){
			if(oldSpecialColors[i] != newSpecialColors[i]){
				bChanged = true;
				break;
			}
		}
		if(!bChanged){
			return;
		}
		for(int i=0;i<oldSpecialColors.length;i+= 1){
			oldSpecialColors[i] = newSpecialColors[i];
		}
	}else{
		throw new IllegalArgumentException("Unknown colorModelID="+colorModelID);
	}
	if(colorModelID.equals(getActiveColorModelID())){
		firePropertyChange("specialColors", null, specialColors);
	}
}


/**
 * This method was created in VisualAge.
 * @return java.awt.image.IndexColorModel
 */
public static final java.awt.image.IndexColorModel getHandleColorMap() {

	int[] contrastColors = createContrastColorModel();

	byte red[] = new byte[contrastColors.length];
	byte green[] = new byte[contrastColors.length];
	byte blue[] = new byte[contrastColors.length];

	for (int i=0;i<contrastColors.length;i++){
		java.awt.Color color = new java.awt.Color(contrastColors[i]);
		red[i] = (byte)color.getRed();
		green[i] = (byte)color.getGreen();
		blue[i] = (byte)color.getBlue();
	}

	return new java.awt.image.IndexColorModel(8,contrastColors.length,red,green,blue);
}
}
