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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Range;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.server.UserPreferences;

public class HistogramPanel extends JPanel {

//	public static final String HISTOGRAM_HIDE_ACTION = "HISTOGRAM_HIDE_ACTION";
	public static final String HISTOGRAM_APPLY_ACTION = "HISTOGRAM_APPLY_ACTION";
	public static final String HISTOGRAM_SELECT_PROPERTY = "HISTOGRAM_SELECT_PROPERTY";

	private static final int VERT_EDGE_OFFSET = 25;
	private static final int HORZ_EDGE_OFFSET = 75;
	private int maxCount;
	private TreeMap<Integer, Integer> originalTreeMap;//distinct pixel values and count
	private Range viewPixelRange;
	private DefaultListSelectionModel pixelListSelectionModel = new DefaultListSelectionModel();
	
	private int height = 100;
	
	private Point mouseStartPoint;
	private boolean dragStartUnselect;
	private String mouseSelectDescr;
	private Integer specialValue;

	private JSlider vertScaleSlider;
	private JSlider horzScaleSlider;
	private JSlider horzOffsetSlider;
	private JLabel pixelValJLabel;
	private JLabel pixValLowJLabel = new JLabel("0");
	private JLabel pixValHighJLabel = new JLabel("100");
	private JLabel stretchJLabel = new JLabel("zoom");
	private JLabel moveJLabel = new JLabel("scroll");
	private JLabel titleJLabel = new JLabel("Histogram Tool");
	private JButton applyButton = new JButton("Apply...");
//	private JButton hideButton = new JButton("Hide");
//	private JScrollBar moveScrollBar = new JScrollBar();
		
	private class HistogramLayoutManager implements LayoutManager{

		public void addLayoutComponent(String name, Component comp) {
			// TODO Auto-generated method stub
		}

		public void layoutContainer(Container parent) {
			// TODO Auto-generated method stub
			int SIDE_BUTTON_WIDTH = applyButton.getPreferredSize().width;//70;
			applyButton.setBounds(getWidth()-SIDE_BUTTON_WIDTH-2,
			2,
			SIDE_BUTTON_WIDTH/*hideButton.getPreferredSize().width*/, VERT_EDGE_OFFSET-2);

//			hideButton.setBounds(getWidth()-SIDE_BUTTON_WIDTH-2,
//					2,
//					SIDE_BUTTON_WIDTH/*hideButton.getPreferredSize().width*/, VERT_EDGE_OFFSET-2);
//			applyButton.setBounds(getWidth()-SIDE_BUTTON_WIDTH-2/*-hideButton.getBounds().width*/,
//					hideButton.getPreferredSize().height+2,
//					SIDE_BUTTON_WIDTH/*applyButton.getPreferredSize().width*/, VERT_EDGE_OFFSET-2);
			
			pixValLowJLabel.setBounds(HORZ_EDGE_OFFSET,
					parent.getHeight()-VERT_EDGE_OFFSET+2,
					pixValLowJLabel.getPreferredSize().width, pixValLowJLabel.getPreferredSize().height);
			pixValHighJLabel.setBounds(parent.getWidth()-HORZ_EDGE_OFFSET-pixValHighJLabel.getPreferredSize().width-2,
					parent.getHeight()-VERT_EDGE_OFFSET+2,
					pixValHighJLabel.getPreferredSize().width, pixValHighJLabel.getPreferredSize().height);
			
			
			int valX = parent.getWidth()/2-pixelValJLabel.getPreferredSize().width/2;
			int valY = parent.getHeight()-VERT_EDGE_OFFSET+2;
			pixelValJLabel.setBounds(valX,valY,pixelValJLabel.getPreferredSize().width,pixelValJLabel.getPreferredSize().height);
			
			titleJLabel.setBounds(
					parent.getWidth()/2-titleJLabel.getPreferredSize().width/2,
					VERT_EDGE_OFFSET-2-(int)titleJLabel.getPreferredSize().getHeight(),
					titleJLabel.getPreferredSize().width,titleJLabel.getPreferredSize().height);
			
			int SLIDER_HEIGHT = 20;
			int SLIDER_LENGTH = 100;
			int TITLE_GAP = 10;
			horzScaleSlider.setBounds(titleJLabel.getBounds().x-TITLE_GAP-SLIDER_LENGTH,
					VERT_EDGE_OFFSET-2-SLIDER_HEIGHT/*parent.getHeight()-VERT_EDGE_OFFSET+4*/,
					SLIDER_LENGTH,SLIDER_HEIGHT);
			
			
			stretchJLabel.setBounds(horzScaleSlider.getBounds().x-stretchJLabel.getPreferredSize().width-2,
					horzScaleSlider.getBounds().y+horzScaleSlider.getHeight()/2-stretchJLabel.getPreferredSize().height/2,
					stretchJLabel.getPreferredSize().width,stretchJLabel.getPreferredSize().height);
			
			moveJLabel.setBounds(
					titleJLabel.getBounds().x+titleJLabel.getBounds().width+TITLE_GAP,
					horzOffsetSlider.getBounds().y+horzOffsetSlider.getHeight()/2-moveJLabel.getPreferredSize().height/2,
					moveJLabel.getPreferredSize().width,moveJLabel.getPreferredSize().height);
			
			horzOffsetSlider.setBounds(
					moveJLabel.getBounds().x+moveJLabel.getBounds().width+2,
					VERT_EDGE_OFFSET-2-SLIDER_HEIGHT/*parent.getHeight()-VERT_EDGE_OFFSET+4*/,
					SLIDER_LENGTH,SLIDER_HEIGHT);

			vertScaleSlider.setOrientation(SwingConstants.VERTICAL);
			final int sliderWidth = vertScaleSlider.getPreferredSize().width;
			final int sliderHeight = height-2*VERT_EDGE_OFFSET;
			vertScaleSlider.setBounds(
				HORZ_EDGE_OFFSET-sliderWidth-15,
				VERT_EDGE_OFFSET,
				sliderWidth, sliderHeight);
//
//			moveScrollBar.setBounds(
//					titleJLabel.getBounds().x+titleJLabel.getBounds().width+20,
//					VERT_EDGE_OFFSET-2-SLIDER_HEIGHT/*parent.getHeight()-VERT_EDGE_OFFSET+4*/,
//				100,20);

		}

		public Dimension minimumLayoutSize(Container parent) {
			// TODO Auto-generated method stub
			return new Dimension(parent.getWidth()+2*HORZ_EDGE_OFFSET,height+2*VERT_EDGE_OFFSET);
		}

		public Dimension preferredLayoutSize(Container parent) {
			// TODO Auto-generated method stub
			return minimumLayoutSize(parent);
		}

		public void removeLayoutComponent(Component comp) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public HistogramPanel() {
		super();
		init();
	}

	private JSlider getHorzOffsetSlider(){
		if(horzOffsetSlider != null){
			return horzOffsetSlider;
		}
		horzOffsetSlider = new JSlider();
		horzOffsetSlider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				if(originalTreeMap == null){
					return;
				}
				int min = horzOffsetSlider.getValue();
				int max = min + originalTreeMap.lastKey()/horzScaleSlider.getValue();
				if(max > originalTreeMap.lastKey()){
					min-= (max)-originalTreeMap.lastKey();
				}
				min = Math.max(originalTreeMap.firstKey(), min);
				max= Math.min(max,originalTreeMap.lastKey());
				viewPixelRange =
					new Range(min,max);
				updateLowHigh();
				repaint();
			}
		});

		horzOffsetSlider.setMinimum(0);
		horzOffsetSlider.setMaximum(33000);
		horzOffsetSlider.setValue(0);
		return horzOffsetSlider;

	}
	private JSlider getHorzScaleSlider(){
		if(horzScaleSlider != null){
			return horzScaleSlider;
		}
		horzScaleSlider = new JSlider();
		horzScaleSlider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				if(originalTreeMap == null){
					return;
				}
				int min = getHorzOffsetSlider().getValue();
				int interval = originalTreeMap.lastKey()/horzScaleSlider.getValue();
				int max = min + interval;
				if(max > originalTreeMap.lastKey()){
					min-= (max)-originalTreeMap.lastKey();
				}
				min = Math.max(originalTreeMap.firstKey(), min);
				max= Math.min(max,originalTreeMap.lastKey());
				viewPixelRange =
					new Range(min,max);
				horzOffsetSlider.setValue(min);
				horzOffsetSlider.setMaximum(originalTreeMap.lastKey()-interval);
				updateLowHigh();
				getHorzOffsetSlider().setEnabled(getHorzScaleSlider().getValue() > 1);
				
//				moveScrollBar.getModel().setRangeProperties(moveScrollBar.getValue(), interval, min, originalTreeMap.lastKey(),false);
//				System.out.println("val="+horzScaleSlider.getValue()+" min="+min+" interv="+interval+" max="+(originalTreeMap.lastKey()-interval));
//				System.out.println(" "+moveScrollBar.getModel());
				repaint();
			}
		});

		horzScaleSlider.setMinimum(1);
		horzScaleSlider.setValue(1);
		return horzScaleSlider;
	}
	private void updateLowHigh(){
		pixValLowJLabel.setText(((int)viewPixelRange.getMin())+"");
		pixValHighJLabel.setText(((int)viewPixelRange.getMax())+"");
	}

	private JSlider getVertSlider(){
		if(vertScaleSlider != null){
			return vertScaleSlider;
		}
		vertScaleSlider = new JSlider();
		vertScaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				repaint();
			}
		});
		vertScaleSlider.setMinimum(1);
		vertScaleSlider.setMaximum(400);
		vertScaleSlider.setValue(1);
		return vertScaleSlider;
	}
	private JLabel getPixelValuesJLabel(){
		if(pixelValJLabel == null){
			pixelValJLabel = new JLabel("( Pixel Values )  Click-Drag mouse to create highlight regions");
		}
		return pixelValJLabel;
	}

	private Range getDragRange(Point dragReleasePoint){
		SortedMap<Integer, Integer> subsetTreeMap = getTreeMapView();
		Point dragStartPointTemp = mouseStartPoint;
		if(dragStartPointTemp.x > dragReleasePoint.x){
			Point temp = dragStartPointTemp;
			dragStartPointTemp = dragReleasePoint;
			dragReleasePoint = temp;
		}
		int dragStartIndex = getHorizontalIndex(dragStartPointTemp.x, subsetTreeMap.size());
		int dragReleaseIndex = getHorizontalIndex(dragReleasePoint.x, subsetTreeMap.size());
		int dragReleaseNextIndex = getHorizontalIndex(dragReleasePoint.x+1, subsetTreeMap.size());
		int startPixelVal = dragStartIndex+subsetTreeMap.firstKey();
		int endPixelVal = (dragReleaseIndex+subsetTreeMap.firstKey());

		if(dragReleaseNextIndex-dragReleaseIndex > 1){
			endPixelVal = (dragReleaseNextIndex-1+subsetTreeMap.firstKey());
		}
		return new Range(startPixelVal,endPixelVal);
	}
	
	private void calcSelectedPixelRanges(MouseEvent e){
		if(mouseStartPoint != null){
			Range dragRange = getDragRange(e.getPoint());
			if(calcRangeBinCount(dragRange) == 0){//selected bin range on histogram with no pixels
				pixelListSelectionModel.clearSelection();
				return;
			}
			if((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK)) != 0){
				if(!pixelListSelectionModel.isSelectionEmpty() &&
						pixelListSelectionModel.getAnchorSelectionIndex() == pixelListSelectionModel.getLeadSelectionIndex()){
					pixelListSelectionModel.addSelectionInterval(pixelListSelectionModel.getAnchorSelectionIndex(), (int)dragRange.getMin());
				}
			}
			boolean isShiftDown = (e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK)) != 0;
			boolean isCntrlDown = (e.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK)) != 0;
			if(isCntrlDown || isShiftDown){
				if(!isShiftDown && dragStartUnselect){
					pixelListSelectionModel.removeSelectionInterval((int)dragRange.getMin(), (int)dragRange.getMax());
				}else{
					pixelListSelectionModel.addSelectionInterval((int)dragRange.getMin(), (int)dragRange.getMax());
				}
			}else{
				pixelListSelectionModel.setSelectionInterval((int)dragRange.getMin(), (int)dragRange.getMax());
			}
		}
	}
	//
	//Create listener to manage the problems of knowing when the mouse has exited a composite component
	//requires this listener be added to main component and all children to work properly
	//Examples of problematic mouseexit events:
	//mouse over internal components triggers mouseexit from parent (not a real exit from parent)
	//mouseexit from child component back into main component (mouseexit from child but not an exit from main component)
	//quick mouse from child component to outside parent skips parent component (parent mouseexited never trigggered)
	//
	private boolean bEverWarnedApply = false;//this makes warning appear only once per editor session (requested by Les)
	private MouseAdapter exitListener = new MouseAdapter() {
		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			//Check if the user ever said they never want to see this warning again in userprefernces
			boolean bGlobalApplyWarning = (userPreferences==null?true:userPreferences.getShowWarning(UserPreferences.WARN_GEOMEDIT_HISTOGRAM_APPLY));
			//If user has never been warned and the user wants to be warned and there is a selection to warn about
			if(!bEverWarnedApply && bGlobalApplyWarning && !isSelectionEmpty()){
				Point convertedPoint = SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), HistogramPanel.this.getParent());
				if(!HistogramPanel.this.getBounds().contains(convertedPoint)){
					bEverWarnedApply = true;
					PopupGenerator.showWarningDialog(HistogramPanel.this, userPreferences, UserMessage.warn_geom_histogram_apply,null);
				}
			}
		}
		
	};
	private void init(){
		applyButton.setFont(Font.decode("Dialog-BOLD-12"));
//		hideButton.setFont(Font.decode("Dialog-10"));
		setLayout(new HistogramLayoutManager());
		this.add(getVertSlider());
		this.add(getHorzScaleSlider());
		this.add(getHorzOffsetSlider());
		this.add(getPixelValuesJLabel());
		this.add(pixValLowJLabel);
		this.add(pixValHighJLabel);
		this.add(stretchJLabel);
		this.add(moveJLabel);
		this.add(titleJLabel);
		this.add(applyButton);
//		this.add(hideButton);
//		this.add(moveScrollBar);
//		moveScrollBar.setOrientation(JScrollBar.HORIZONTAL);
		getHorzOffsetSlider().setEnabled(false);
		titleJLabel.setFont(titleJLabel.getFont().deriveFont(Font.BOLD,16f));
		
		getVertSlider().setToolTipText("Scale pixel 'count' display");
		getHorzScaleSlider().setToolTipText("Zoom pixel 'value' display");
		getHorzOffsetSlider().setToolTipText("Scroll through pixel 'value' display");
		applyButton.setToolTipText("Update/Create Domain Regions using histogram selection");
//		hideButton.setToolTipText("Hide histogram tool");
		
//		hideButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				firePropertyChange(HISTOGRAM_HIDE_ACTION, null, null);
//			}
//		});
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				firePropertyChange(HISTOGRAM_APPLY_ACTION, null, null);
			}
		});

		addMouseListener(
			new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					super.mouseReleased(e);
					calcSelectedPixelRanges(e);
					if(mouseStartPoint != null){
						firePropertyChange(HistogramPanel.HISTOGRAM_SELECT_PROPERTY, null,pixelListSelectionModel);
						mouseStartPoint = e.getPoint();
						mouseStartPoint.x = forceInBounds(mouseStartPoint.x);
						mouseSelectDescr = calculateDescription(e.getPoint());
					}
					repaint();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					super.mousePressed(e);
					repaint();
					int dragStartIndex = getHorizontalIndex(forceInBounds(e.getPoint().x), getTreeMapView().size());
					int dragStartPixelVal = dragStartIndex+(Integer)getTreeMapView().firstKey();
					dragStartUnselect = pixelListSelectionModel.isSelectedIndex(dragStartPixelVal);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					super.mouseExited(e);
					if((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == 0){
						mouseSelectDescr = null;
						mouseStartPoint = null;
					}
					repaint();
				}
			}
		);
		addMouseMotionListener(
			new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					// TODO Auto-generated method stub
					super.mouseDragged(e);
					calcSelectedPixelRanges(e);
					mouseSelectDescr = calculateDescription(e.getPoint());
					repaint();
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					super.mouseMoved(e);
					mouseSelectDescr = null;
					if(originalTreeMap == null){
						return;
					}
					if(e.getPoint().y <VERT_EDGE_OFFSET){
						mouseStartPoint = null;
						repaint();
						return;
					}
					if(mouseStartPoint == null && (e.getPoint().x < HORZ_EDGE_OFFSET ||
						e.getPoint().x >HistogramPanel.this.getWidth()-HORZ_EDGE_OFFSET)){
						return;
					}
					if(mouseStartPoint != null && mouseStartPoint.x ==e.getPoint().x){
						return;
					}

					mouseStartPoint = new Point(e.getPoint());
					mouseStartPoint.x = forceInBounds(e.getPoint().x);
					mouseSelectDescr = calculateDescription(e.getPoint());
					repaint();
				}
			}
		);
		
		//Setup for mouse exit warning if histogram selection exists
		addMouseListener(exitListener);
		Component[] children = getComponents();
		for (int i = 0; i < children.length; i++) {
			if(!(children[i] instanceof JLabel)){
				children[i].addMouseListener(exitListener);
			}
		}
		//
	}
	private int forceInBounds(int forceThis){
		if(forceThis < HORZ_EDGE_OFFSET){
			forceThis = HORZ_EDGE_OFFSET;
		}
		if(forceThis > HistogramPanel.this.getWidth()-HORZ_EDGE_OFFSET){
			forceThis = HistogramPanel.this.getWidth()-HORZ_EDGE_OFFSET;
		}
		return forceThis;
	}

	private int calcRangeBinCount(Range dragRange){
		int count = 0;
		for (int rangePixVal = (int)dragRange.getMin(); rangePixVal <= (int)dragRange.getMax(); rangePixVal++) {
			count+= originalTreeMap.get(rangePixVal/*+subsetTreeMap.firstKey()*/);
		}
		return count;
	}
	private String calculateDescription(Point point){
		if(mouseStartPoint == null){
			return null;
		}
		String rangeDescr = null;
		Range dragRange = getDragRange(point);
		if(dragRange.getMax()-dragRange.getMin() > 0){
			rangeDescr = ((int)dragRange.getMin()/*+subsetTreeMap.firstKey()*/)+" to "+((int)dragRange.getMax()/*+subsetTreeMap.firstKey()*/);
		}else{
			rangeDescr = ((int)dragRange.getMin()/*+subsetTreeMap.firstKey()*/)+"";
		}
		return "pix("+rangeDescr+") cnt="+calcRangeBinCount(dragRange);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		drawHistogram((Graphics2D)g);
	}

	public void setHistogram(TreeMap<Integer, Integer> origHistoTreeMap){
		pixelListSelectionModel.clearSelection();
		applyButton.setEnabled(false);
		
		originalTreeMap = new TreeMap<Integer, Integer>();
		originalTreeMap.putAll(origHistoTreeMap);
		int minPixVal = originalTreeMap.firstKey();
		int maxPixVal = originalTreeMap.lastKey();
		maxCount = 0;
		int minNonZeroCount = 0;
		for (int i = minPixVal; i <= maxPixVal; i++) {
			if(!originalTreeMap.containsKey(i)){
				originalTreeMap.put(i,0);
			}
			maxCount = Math.max(maxCount,originalTreeMap.get(i));
			if(originalTreeMap.get(i) != 0){
				if(minNonZeroCount == 0){
					minNonZeroCount = originalTreeMap.get(i);
				}else{
					minNonZeroCount = Math.min(minNonZeroCount, originalTreeMap.get(i));
				}
			}
		}

		horzOffsetSlider.setMaximum(0);
		horzOffsetSlider.setValue(0);
		horzScaleSlider.setValue(1);
		viewPixelRange = new Range(originalTreeMap.firstKey(),originalTreeMap.lastKey());
		updateLowHigh();
		if(originalTreeMap.size()==1){
			horzScaleSlider.setEnabled(false);
			vertScaleSlider.setEnabled(false);
		}else{
			horzScaleSlider.setEnabled(true);
			vertScaleSlider.setEnabled(true);
		}
		double maxHeight = (getPreferredSize().height-2*VERT_EDGE_OFFSET);
		double minStartHeight = 3.0;
		//h1/h0 = (cnt/maxcnt)^scaleFactor -- use antilog to find scalefactor
		double scaleFrac = Math.log10(minStartHeight/maxHeight)/Math.log10((double)minNonZeroCount/(double)maxCount);
		vertScaleSlider.setValue((int)(Math.round(10.0/scaleFrac)));
		
		repaint();
	}

	private double getIncrement(int mapSize){
		return (double)(mapSize-1)/(double)(this.getWidth()-2*HORZ_EDGE_OFFSET/*-1*/);
	}
	private int getHorizontalIndex(int xPoint,int mapSize){
		xPoint = forceInBounds(xPoint);
		return (int)Math.round(((xPoint-HORZ_EDGE_OFFSET)*getIncrement(mapSize)));
	}
	private SortedMap<Integer, Integer> getTreeMapView(){
		SortedMap<Integer, Integer> subsetTreeMap = originalTreeMap;
		if(viewPixelRange != null){
			subsetTreeMap = originalTreeMap.subMap((int)viewPixelRange.getMin(), (int)viewPixelRange.getMax()+1);
		}
		return subsetTreeMap;
	}
	public void highlightsChanged(String action){
		if(!action.equals(OverlayEditorPanelJAI.FRAP_DATA_HISTOUPDATEHIGHLIGHT_PROPERTY)){
			applyButton.setEnabled(false);
			if(!pixelListSelectionModel.isSelectionEmpty()){
				pixelListSelectionModel.clearSelection();
				repaint();
			}
		}else{
			applyButton.setEnabled(!pixelListSelectionModel.isSelectionEmpty());
		}
	}
	private static final int MIN_MAX_BARWIDTH_COMPENSATION = 2;//add this much to width when drawing xmin bar and xmax bar
	private static Color BAR_OTHER_COLOR = new Color(144,144,144);
	public void drawHistogram(Graphics2D g){
		try{
			if(originalTreeMap != null){
				SortedMap<Integer, Integer> subsetTreeMap = getTreeMapView();
				Integer[] histoPixelVals = subsetTreeMap.keySet().toArray(new Integer[0]);
				int y0 = this.getHeight()-VERT_EDGE_OFFSET;
				int pixelVal = 0;
				int countVal = 0;
				int index0 = 0;
				int index1 = 0;
				for (int xPoint = HORZ_EDGE_OFFSET; xPoint < this.getWidth()-HORZ_EDGE_OFFSET/*-1*/; xPoint++) {
					boolean bMaxEnd = xPoint == (this.getWidth()-HORZ_EDGE_OFFSET-1);
					index0 = getHorizontalIndex(xPoint,histoPixelVals.length);
					index1 = getHorizontalIndex(xPoint+1,histoPixelVals.length);
					int pixCount = 0;
					boolean bInSelection = false;
					for (int pixValIndex = index0; pixValIndex < (index1==index0?index0+1:index1+(bMaxEnd?1:0)); pixValIndex++) {
						pixelVal = histoPixelVals[pixValIndex].intValue();
						countVal = subsetTreeMap.get(pixelVal).intValue();
						bInSelection|= (countVal != 0 && pixelListSelectionModel.isSelectedIndex(pixelVal));
						pixCount+= countVal;					
					}
					if(bInSelection){
						g.setColor(Color.cyan);
					}else{
						g.setColor((index0%2==0?Color.gray:BAR_OTHER_COLOR));//make alternating histogram bars a different color
					}
					double gammaScale = Math.pow((pixCount/((double)maxCount)), 1/(vertScaleSlider.getValue()/10.0));
					if(pixCount != 0){
						int y1 = (int)(y0-((this.getHeight()-2*VERT_EDGE_OFFSET)*gammaScale));
//						y1-= 0;
						if(y1 < VERT_EDGE_OFFSET){
							y1=VERT_EDGE_OFFSET;
						}
						//min max bar width filler
						for (int i = 0; i <= ((xPoint == HORZ_EDGE_OFFSET) || bMaxEnd ?MIN_MAX_BARWIDTH_COMPENSATION:0); i++) {
							int offset = (xPoint == HORZ_EDGE_OFFSET?-i:i);
							g.drawLine(xPoint+offset,y0,xPoint+offset,y1);
							if(getSpecialValue() != null && getSpecialValue().intValue() >= histoPixelVals[index0].intValue() && getSpecialValue().intValue() <= histoPixelVals[index1].intValue()){
								g.setColor(Color.red);
								g.drawLine(xPoint+offset,y0,xPoint+offset,(int)(y0-((this.getHeight()-2*VERT_EDGE_OFFSET))));
							}							
						}
					}
				}
				
				g.setColor(Color.black);
				//X-axis base
				g.drawLine(HORZ_EDGE_OFFSET-MIN_MAX_BARWIDTH_COMPENSATION, this.getHeight()-VERT_EDGE_OFFSET+1, this.getWidth()-HORZ_EDGE_OFFSET-1+MIN_MAX_BARWIDTH_COMPENSATION, this.getHeight()-VERT_EDGE_OFFSET+1);
				//Y-axis base
				g.drawLine(HORZ_EDGE_OFFSET-1-MIN_MAX_BARWIDTH_COMPENSATION, this.getHeight()-VERT_EDGE_OFFSET+1, HORZ_EDGE_OFFSET-1-MIN_MAX_BARWIDTH_COMPENSATION, VERT_EDGE_OFFSET);
				//Y-axis 0 label
				g.drawString("0",HORZ_EDGE_OFFSET-10,this.getHeight()-VERT_EDGE_OFFSET);
				//Y-axis maxcount label
				int horz = HORZ_EDGE_OFFSET-(int)(g.getFontMetrics().stringWidth(maxCount+""));
				g.drawString(maxCount+"",
						(horz<1?1:horz),
						VERT_EDGE_OFFSET-1/*+(int)(g.getFontMetrics().getAscent()/2)*/);
				//Y-axis vertical "Count" label
				Graphics2D newG = (Graphics2D)g.create();
				newG.translate(0, 0);
				newG.rotate(Math.PI/180*-90);
				newG.drawString("Count",
						-VERT_EDGE_OFFSET-newG.getFontMetrics().stringWidth("Count")-
						(height-2*VERT_EDGE_OFFSET-newG.getFontMetrics().stringWidth("Count"))/2,
						HORZ_EDGE_OFFSET-2-MIN_MAX_BARWIDTH_COMPENSATION);
				newG.dispose();
				//Mouse-over description
				if(mouseSelectDescr != null){
					int drawX = mouseStartPoint.x;
					if(mouseStartPoint.x+g.getFontMetrics().stringWidth(mouseSelectDescr)>=getWidth()-2-HORZ_EDGE_OFFSET){
						drawX = getWidth()-2-g.getFontMetrics().stringWidth(mouseSelectDescr)-HORZ_EDGE_OFFSET;
					}else if(drawX <= HORZ_EDGE_OFFSET){
						drawX+=2;
					}
					g.drawString(mouseSelectDescr,
							drawX,
							VERT_EDGE_OFFSET+g.getFontMetrics().getAscent());
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void setHeight(int height){
		this.height = height;
		revalidate();
	}
	@Override
	public Dimension getMaximumSize() {
		// TODO Auto-generated method stub
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		// TODO Auto-generated method stub
		return getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(super.getPreferredSize().width, height);
	}

	public Integer getSpecialValue() {
		return specialValue;
	}

	public void setSpecialValue(Integer specialValue) {
		if(Compare.isEqualOrNull(this.specialValue, specialValue)){
			return;
		}
		this.specialValue = specialValue;
		repaint();
	}

	public boolean isSelectionEmpty(){
		return pixelListSelectionModel.isSelectionEmpty();
	}
	private UserPreferences userPreferences;
	public void setUserPreferences(UserPreferences userPreferences){
		this.userPreferences = userPreferences;
	}
}
