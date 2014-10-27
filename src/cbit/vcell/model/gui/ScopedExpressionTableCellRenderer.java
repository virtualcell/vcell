/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.vcell.util.Issue;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

import cbit.gui.ModelProcessEquation;
import cbit.gui.ScopedExpression;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionPrintFormatter;

/**
 * Insert the type's description here.
 * Creation date: (8/8/2006 12:22:39 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class ScopedExpressionTableCellRenderer implements javax.swing.table.TableCellRenderer {
	private class ReusableExpressionIcon {
		ImageIcon imageIcon;
		Color background;
		Color foreground;
		private ReusableExpressionIcon(ImageIcon imageIcon, Color background,
				Color foreground) {
			super();
			this.imageIcon = imageIcon;
			this.background = background;
			this.foreground = foreground;
		}
	}
	private Hashtable<String, ReusableExpressionIcon> scopedExpressionImageIconHash = new Hashtable<String, ReusableExpressionIcon>();//Cache ScopedExpression ImageIcons
	private int scopedExpressionCacheSize = 0;
	private static final int CACHE_SIZE_LIMIT = 250000;
	private DefaultScrollTableCellRenderer stringRenderer = new DefaultScrollTableCellRenderer();
	private DefaultScrollTableCellRenderer imageRenderer = new DefaultScrollTableCellRenderer() {
		private Icon icon = null;
		private Insets borderInsets = null;
		
		@Override
		public void setIcon(Icon imageIcon){
			icon = imageIcon;
		}
		
		@Override
	    public Dimension getPreferredSize() {
	    	if (borderInsets==null){
	    		borderInsets = getBorder().getBorderInsets(this);
	    	}
	    	if (icon!=null){
	    		return new Dimension(icon.getIconWidth()+borderInsets.left+borderInsets.right, icon.getIconHeight()+borderInsets.top+borderInsets.bottom);
	    	}else{
	    		return new Dimension(borderInsets.left+borderInsets.right, borderInsets.top+borderInsets.bottom);
	    	}
	    }
	    
	    @Override
		public void paintComponent(Graphics graphics){
			Dimension size = getSize();
			graphics.setColor(getBackground());
			graphics.fillRect(0,0,size.width,size.height);
			if (icon!=null && borderInsets != null){
				icon.paintIcon(this,graphics,borderInsets.left, borderInsets.top);
			}
		}
	};
	
	private DefaultScrollTableCellRenderer templateJLabel = new DefaultScrollTableCellRenderer();//Template componet
	private javax.swing.border.Border BORDER = new javax.swing.border.EmptyBorder(2,2,2,2);//Template border
	private java.awt.Font italicFont = new java.awt.Font("SansSerif", java.awt.Font.ITALIC, 11);
	private java.awt.image.BufferedImage graphicsContextProvider = new java.awt.image.BufferedImage(10,10,java.awt.image.BufferedImage.TYPE_BYTE_GRAY);

/**
 * ScopedExpressionTableCellRenderer constructor comment.
 */
public ScopedExpressionTableCellRenderer() {
	super();

	templateJLabel.setBorder(BORDER);
	imageRenderer.setBorder(BORDER);
	stringRenderer.setBorder(BORDER);
}


	/**
	 * This fine grain notification tells listeners the exact range
	 * of cells, rows, or columns that changed.
	 */
public static void formatTableCellSizes(final javax.swing.JTable targetTable) {
	if (targetTable.getTableHeader() == null) {
		return;
	}
	try{
		int[] targetRows, targetColumns;
		targetRows = new int[targetTable.getRowCount()];
		for(int i=0;i<targetRows.length;i+= 1){
			targetRows[i] = i;
		}
		targetColumns = new int[targetTable.getColumnCount()];
		for(int i=0;i<targetColumns.length;i+= 1){
			targetColumns[i] = i;
		}
		final int[] maxRowHeights = new int[targetTable.getRowCount()];
		final int[] maxColumnWidths = new int[targetTable.getColumnCount()];
		java.util.Arrays.fill(maxRowHeights,0);
		java.util.Arrays.fill(maxColumnWidths,0);
		//
		//Calculate dimension of all cells
		//
		TableColumnModel columnModel = targetTable.getTableHeader().getColumnModel();
		for(int columnIndex = 0;columnIndex < targetTable.getColumnCount();columnIndex+= 1){
			// calculate header preferred width
			TableColumn column = columnModel.getColumn(columnIndex);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null) {
				headerRenderer = targetTable.getTableHeader().getDefaultRenderer();
			}
			if (headerRenderer != null) {
				java.awt.Component comp = headerRenderer.getTableCellRendererComponent(targetTable, column.getHeaderValue(), false, false, 0, columnIndex); 
				maxColumnWidths[columnIndex] = Math.max(maxColumnWidths[columnIndex],comp.getPreferredSize().width);
			}
			for(int rowIndex=0;rowIndex<targetTable.getRowCount();rowIndex+= 1){
				TableCellRenderer cellRenderer = targetTable.getCellRenderer(rowIndex,columnIndex);
				if (cellRenderer == null) {
					continue;
				}
				java.awt.Component comp = cellRenderer.getTableCellRendererComponent(
						targetTable,targetTable.getValueAt(rowIndex,columnIndex),false,false,rowIndex,columnIndex);					
				maxColumnWidths[columnIndex] = Math.max(maxColumnWidths[columnIndex],comp.getPreferredSize().width);
				maxRowHeights[rowIndex] = Math.max(maxRowHeights[rowIndex],comp.getPreferredSize().height);
			}
		}

		//
		//Set column widths to fit widest component in targetColumns
		//without making any smaller than preferred
		//
		ArrayList<Integer> expressionColumnList = new ArrayList<Integer>();
		for (int columnIndex : targetColumns) {
			TableColumn column = columnModel.getColumn(columnIndex);
			// set prefer width if it is expression since the size is computed through image.
			int preferredWidth = maxColumnWidths[columnIndex] + columnModel.getColumnMargin();
			Class<?> columnClass = targetTable.getColumnClass(columnIndex);
			if (columnClass.equals(ScopedExpression.class) || columnClass.equals(ModelProcessEquation.class)) {
				expressionColumnList.add(columnIndex);
				column.setPreferredWidth(preferredWidth);
			} else if (column.getPreferredWidth() < preferredWidth){
				column.setPreferredWidth(preferredWidth);
			}
		}
		
		// expand the table to parent width, give the extra space to expression columns 		
		if (expressionColumnList.size() > 0 && targetTable.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) {
			int extraWidth = targetTable.getParent().getSize().width;
			int tableWidth = targetTable.getPreferredSize().width;
			if (extraWidth > tableWidth) {
				TableColumnModel tcm = targetTable.getColumnModel();
				int total_columns = tcm.getColumnCount();
				for (int i = 0; i < total_columns; i++)	{
					TableColumn column = tcm.getColumn(i);
					extraWidth = extraWidth - column.getPreferredWidth();
				}
	
				extraWidth /= expressionColumnList.size();
				for (int c : expressionColumnList) {
					TableColumn exprColumn = tcm.getColumn(c);
					exprColumn.setPreferredWidth(exprColumn.getPreferredWidth() + extraWidth);
				}
			}
		}
			
		//
		//Set row heights to fit tallest component in row
		//without making any smaller than preferred
		for(final int rowIndex : targetRows){
			if (targetTable.getRowHeight(rowIndex) < maxRowHeights[rowIndex]){
				targetTable.setRowHeight(rowIndex, maxRowHeights[rowIndex]);
			}
		}
	}catch(Exception exc){
		exc.printStackTrace();
	}	
}


	/**
	 *  This method is sent to the renderer by the drawing table to
	 *  configure the renderer appropriately before drawing.  Return
	 *  the Component used for drawing.
	 *
	 * @param	table		the JTable that is asking the renderer to draw.
	 *				This parameter can be null.
	 * @param	value		the value of the cell to be rendered.  It is
	 *				up to the specific renderer to interpret
	 *				and draw the value.  eg. if value is the
	 *				String "true", it could be rendered as a
	 *				string or it could be rendered as a check
	 *				box that is checked.  null is a valid value.
	 * @param	isSelected	true is the cell is to be renderer with
	 *				selection highlighting
	 * @param	row	        the row index of the cell being drawn.  When
	 *				drawing the header the rowIndex is -1.
	 * @param	column	        the column index of the cell being drawn
	 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	try{
		TableModel tableModel = table.getModel();
		if(value == null){
			templateJLabel.setIcon(null);
			templateJLabel.setText(null);
			templateJLabel.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (tableModel instanceof VCellSortTableModel) {
				if (row < ((VCellSortTableModel<?>) tableModel).getRowCount()) {
					Object rowObject = ((VCellSortTableModel<?>) tableModel).getValueAt(row);
					if (rowObject instanceof SpeciesContextSpecParameter) {
						templateJLabel.setText(((SpeciesContextSpecParameter) rowObject).getNullExpressionDescription());
					}
				}
			}
			return templateJLabel;
		}
		
		if (!(value instanceof ScopedExpression)){
			return stringRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		
		imageRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		JLabel renderer = imageRenderer;
		ScopedExpression scopedExpression = (ScopedExpression)value;
		String scopedExpressInfix = scopedExpression.infix();
		ReusableExpressionIcon rei = scopedExpressionImageIconHash.get(scopedExpressInfix);
		if (rei != null && rei.background.equals(imageRenderer.getBackground())
			&& rei.foreground.equals(imageRenderer.getForeground())) {
			//Re-use existing ImageIcon is it exists and is same state
			imageRenderer.setIcon(rei.imageIcon);
		} else {
			//Create new ImageIcon
			try{
				ExpressionPrintFormatter epf = new ExpressionPrintFormatter(scopedExpression.getRenamedExpression());
				//
				//Use graphicsContextProvider BufferedImage to get started because
				//theTable may have a null GC and epf needs a non-null GC
				//
				java.awt.Graphics2D tempG2D = (java.awt.Graphics2D)graphicsContextProvider.getGraphics();
				tempG2D.setFont(italicFont);//Must set here before epf.getSize is called
				//Determine how big the expression image will be
				java.awt.Dimension dim = epf.getSize(tempG2D);
				//Create and render the expression image
				java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(dim.width,dim.height,java.awt.image.BufferedImage.TYPE_INT_RGB);
				java.awt.Graphics2D g2d = bi.createGraphics();
				g2d.setClip(0,0,dim.width,dim.height);//epf.paint needs a non-null clipBounds
				g2d.setFont(italicFont);//set the SAME font used in epf.getSize
				if(table != null && imageRenderer != null){
					g2d.setBackground(imageRenderer.getBackground());
					g2d.setColor(imageRenderer.getForeground());
				}
				g2d.clearRect(0,0,dim.width,dim.height);//paint background
				epf.paint(g2d);//paint expression into image
				//
				//Limit cacheing in case a large number of DIFFERENT expressions are being serviced by this TableCellRenderer.
				if((scopedExpressionCacheSize+(dim.width*dim.height)) >= CACHE_SIZE_LIMIT){
					scopedExpressionImageIconHash.clear();
					scopedExpressionCacheSize = 0;
				}
				javax.swing.ImageIcon newImageIcon = new javax.swing.ImageIcon(bi);
				rei = new ReusableExpressionIcon(newImageIcon, imageRenderer.getBackground(), imageRenderer.getForeground());
				if (scopedExpressionImageIconHash.put(scopedExpressInfix, rei) == null){
					scopedExpressionCacheSize += dim.width * dim.height;
				}
				imageRenderer.setIcon(newImageIcon);		
				
			}catch(Exception e){
				//Fallback to String
				e.printStackTrace();
				templateJLabel.setText(scopedExpression.infix());
				renderer = templateJLabel;
			}

		}
		ExpressionBindingException expressionBindingException = scopedExpression.getExpressionBindingException();
		if (expressionBindingException != null) {
			renderer.setBorder(BorderFactory.createLineBorder(Color.red));
			renderer.setToolTipText(expressionBindingException.getMessage());
		} else {
			renderer.setBorder(BorderFactory.createEmptyBorder());
			renderer.setToolTipText(null);
		}
		if (tableModel instanceof VCellSortTableModel) {
			List<Issue> issueList = ((VCellSortTableModel<?>) tableModel).getIssues(row, column, Issue.SEVERITY_ERROR);
			if (issueList.size() > 0) {
				renderer.setToolTipText(Issue.getHtmlIssueMessage(issueList));
				if (column == 0) {
					renderer.setBorder(new MatteBorder(1,1,1,0,Color.red));
				} else if (column == table.getColumnCount() - 1) {
					renderer.setBorder(new MatteBorder(1,0,1,1,Color.red));
				} else {
					renderer.setBorder(new MatteBorder(1,0,1,0,Color.red));
				}
			} else {
				renderer.setBorder(BORDER);
			}
		}
		return renderer;
	} catch(Exception e) {
		e.printStackTrace();
		templateJLabel.setText("ScopedExpressionTableCellRenderer Error - "+e.getMessage()+" "+value);
		return templateJLabel;
	}
}


}
