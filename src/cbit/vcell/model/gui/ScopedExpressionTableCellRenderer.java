package cbit.vcell.model.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import cbit.gui.DefaultTableCellRendererEnhanced;
import cbit.vcell.parser.ExpressionPrintFormatter;
import cbit.vcell.parser.ScopedExpression;

/**
 * Insert the type's description here.
 * Creation date: (8/8/2006 12:22:39 PM)
 * @author: Frank Morgan
 */
public class ScopedExpressionTableCellRenderer implements javax.swing.table.TableCellRenderer {
	private java.util.Hashtable scopedExpressionImageIconHash = new java.util.Hashtable();//Cache ScopedExpression ImageIcons
	private java.util.Hashtable scopedExpressionSelectedHash = new java.util.Hashtable();//Cache ScopedExpression Selected
	private int scopedExpressionCacheSize = 0;
	private static final int CACHE_SIZE_LIMIT = 250000;
	private DefaultTableCellRendererEnhanced stringRenderer = new DefaultTableCellRendererEnhanced();
	private DefaultTableCellRendererEnhanced imageRenderer = new DefaultTableCellRendererEnhanced() {
		private Icon icon = null;
		private Insets borderInsets = null;
		
		public void setIcon(Icon imageIcon){
			icon = imageIcon;
		}
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
		public void paintComponent(Graphics graphics){
			Dimension size = getSize();
			graphics.setColor(getBackground());
			graphics.fillRect(0,0,size.width,size.height);
			if (icon!=null){
				icon.paintIcon(this,graphics,0,0);
			}
		}
	};
	
	private javax.swing.JLabel templateJLabel = new javax.swing.JLabel();//Template componet
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
public static void formatTableCellSizes(javax.swing.JTable targetTable,int[] targetRows,int[] targetColumns) {

	try{
		if(targetRows == null){
			targetRows = new int[targetTable.getRowCount()];
			for(int i=0;i<targetRows.length;i+= 1){
				targetRows[i] = i;
			}
		}else{
			targetRows = (int[])targetRows.clone();
			java.util.Arrays.sort(targetRows);
		}
		if(targetColumns == null){
			targetColumns = new int[targetTable.getColumnCount()];
			for(int i=0;i<targetColumns.length;i+= 1){
				targetColumns[i] = i;
			}
		}else{
			targetColumns = (int[])targetColumns.clone();
			java.util.Arrays.sort(targetColumns);
		}
		int[] maxRowHeights = new int[targetTable.getRowCount()];
		int[] maxColumnWidths = new int[targetTable.getColumnCount()];
		java.util.Arrays.fill(maxRowHeights,0);
		java.util.Arrays.fill(maxColumnWidths,0);
		//
		//Calculate dimension of all cells
		//
		for(int columnIndex = 0;columnIndex < targetTable.getColumnCount();columnIndex+= 1){
			for(int rowIndex=0;rowIndex<targetTable.getRowCount();rowIndex+= 1){
				java.awt.Component comp =
					targetTable.getCellRenderer(rowIndex,columnIndex).getTableCellRendererComponent(
						targetTable,targetTable.getValueAt(rowIndex,columnIndex),false,false,rowIndex,columnIndex);
					
				maxColumnWidths[columnIndex] = Math.max(maxColumnWidths[columnIndex],comp.getPreferredSize().width);
				maxRowHeights[rowIndex] = Math.max(maxRowHeights[rowIndex],comp.getPreferredSize().height);
			}
		}

		//
		//Set column widths to fit widest component in targetColumns
		//without making any smaller than preferred
		//
		for(int columnIndex = 0;columnIndex < targetTable.getColumnCount();columnIndex+= 1){
			if((java.util.Arrays.binarySearch(targetColumns,columnIndex) >= 0) &&
				targetTable.getTableHeader().getColumnModel().getColumn(columnIndex).getPreferredWidth() < maxColumnWidths[columnIndex]){
				targetTable.getTableHeader().getColumnModel().
					getColumn(columnIndex).setPreferredWidth(maxColumnWidths[columnIndex]+targetTable.getTableHeader().getColumnModel().getColumnMargin());
			}
		}
		
		//
		//Set row heights to fit tallest component in row
		//without making any smaller than preferred
		//
		//Do the following just for compatability with VAJ JRE
		//JTable setRowHeight(int,int) method template doesn't exist in VAJ old JRE
		//but is preferred and available in newer JRE.
		//Check for preferred setRowHeight(int,int) by introspection to avoid compile errors in VAJ
		for(int rowIndex=0;rowIndex<targetTable.getRowCount();rowIndex+= 1){
			if((java.util.Arrays.binarySearch(targetRows,rowIndex) >= 0) && 
					targetTable.getRowHeight(rowIndex) < maxRowHeights[rowIndex]){
					targetTable.setRowHeight(rowIndex,maxRowHeights[rowIndex]+targetTable.getRowMargin());
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
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable theTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

	try{
		if(theTable != null){
			templateJLabel.setBackground((isSelected?theTable.getSelectionBackground():theTable.getBackground()));
			templateJLabel.setForeground((isSelected?theTable.getSelectionForeground():theTable.getForeground()));
			imageRenderer.setBackground((isSelected?theTable.getSelectionBackground():theTable.getBackground()));
			imageRenderer.setForeground((isSelected?theTable.getSelectionForeground():theTable.getForeground()));
			
			// set cell color to gray if the cell is not editable
			if (!theTable.getModel().isCellEditable(row, column)) {
				templateJLabel.setBackground(Color.WHITE);
				templateJLabel.setForeground( new Color(128,128,128));
				stringRenderer.setBackground(Color.WHITE);
				stringRenderer.setForeground( new Color(128,128,128));
				imageRenderer.setBackground(Color.WHITE);
				imageRenderer.setForeground( new Color(128,128,128));
			}
		}

		templateJLabel.setText(null);
		templateJLabel.setIcon(null);
		if(value == null){
			return templateJLabel;
		}
		
		if (value instanceof String){
			String color = "gray";
			if (isSelected){
				color = "white";
			}
			String str = "<html><em><font color='"+color+"'>"+((String)value)+"</font></em></html>";
			return stringRenderer.getTableCellRendererComponent(theTable, str, isSelected, hasFocus, row, column);
//			templateJLabel.setText((String)value);
//			return templateJLabel;
		}
		String scopedExpressInfix = ((cbit.vcell.parser.ScopedExpression)value).infix();
		if(scopedExpressionImageIconHash.containsKey(scopedExpressInfix) && 
			scopedExpressionSelectedHash.containsKey(scopedExpressInfix) &&
			((Boolean)scopedExpressionSelectedHash.get(scopedExpressInfix)).booleanValue() == isSelected){
			//Re-use existing ImageIcon is it exists and is same select state

			imageRenderer.setIcon((javax.swing.ImageIcon)scopedExpressionImageIconHash.get(scopedExpressInfix));
			return imageRenderer;

		
		}else{
			//Create new ImageIcon
			try{
				ExpressionPrintFormatter epf = new ExpressionPrintFormatter(((ScopedExpression)value).getExpression());
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
				if(imageRenderer != null){
					g2d.setBackground(imageRenderer.getBackground());
					g2d.setColor(imageRenderer.getForeground());
				}
				g2d.clearRect(0,0,dim.width,dim.height);//paint background
				epf.paint(g2d);//paint expression into image
				//
				//Limit cacheing in case a large number of DIFFERENT expressions are being serviced by this TableCellRenderer.
				if((scopedExpressionCacheSize+(dim.width*dim.height)) >= CACHE_SIZE_LIMIT){
					scopedExpressionImageIconHash.clear();
					scopedExpressionSelectedHash.clear();
					scopedExpressionCacheSize = 0;
				}
				javax.swing.ImageIcon newImageIcon = new javax.swing.ImageIcon(bi);
				if(theTable != null){//don't cache if we didn't get some color info from theTable
					if(scopedExpressionImageIconHash.put(scopedExpressInfix,newImageIcon) == null){
						scopedExpressionCacheSize+= dim.width*dim.height;
					}
					scopedExpressionSelectedHash.put(scopedExpressInfix,new Boolean(isSelected));
				}
				imageRenderer.setIcon(newImageIcon);
				return imageRenderer;
			}catch(Exception e){
				//Fallback to String
				e.printStackTrace();
				templateJLabel.setText(((cbit.vcell.parser.ScopedExpression)value).infix());
				return templateJLabel;
			}

		}
	}catch(Exception e){
		e.printStackTrace();
		templateJLabel.setText("ScopedExpressionTableCellRenderer Error - "+e.getMessage()+" "+value);
		return templateJLabel;
	}
}


}