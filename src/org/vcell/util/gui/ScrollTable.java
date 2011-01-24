package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import cbit.gui.ReactionEquation;
import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;

/**
 * ScrollTable extends JTable and has a JScrollPane which encloses the table itself. The default cell renderer
 * is {@link DefaultScrollTableCellRenderer} which has special background for noneditable table cells. The cell render
 * for {@link ScopedExpression} is {@link ScopedExpressionTableCellRenderer} and the cell editor for {@link ScopedExpression}
 * is {@link TableCellEditorAutoCompletion}.
 * 
 * The default auto resize mode is AUTO_RESIZE_OFF because most of tables have expression columns. The expression 
 * columns will be formatted nicely automatically. You need to set auto resize mode if you don't want AUTO_RESIZE_OFF.
 * 
 * By default, when editing an expression, it will automatically try to bind expression to find errors. However, 
 * there are exceptions like in Kinetics Editor, new parameter will be added it is not already defined. Here you 
 * don't want to validate expression bindings. So you need to call ScrollTable.setValidateExpressionBinding(false).
 * 
 * To add ScrollTable to a JPanel, call ScrollTable.getEnclosingScrollPane() and add that to JPanel.
 * @author fgao
 *
 */
@SuppressWarnings("serial")
public class ScrollTable extends JTable {
	private static final Border tableCellHeaderBorder = (UIManager.getBorder("TableHeader.cellBorder"));
	private static final Color tableHeaderColor = new Color(0xb9c9fe);
	
	private JScrollPane enclosingScrollPane = null;
	private boolean bValidateExpressionBinding = true;
	private ComponentAdapter componentListener = null;
	protected int hoverRow = -1, hoverColumn = -1;
	private DefaultScrollTableCellRenderer defaultTableCellRenderer = null;
	private ScrollTableActionManager scrollTableActionManager = null;
	
	class ScrollTableBooleanEditor extends DefaultCellEditor {
		static final int CHECKBOX_WIDTH = 10;
		static final int CHECKBOX_HEIGHT = 10;
		public ScrollTableBooleanEditor() {
		    super(new JCheckBox());
		    final JCheckBox checkBox = (JCheckBox)getComponent();
		    checkBox.setHorizontalAlignment(JCheckBox.CENTER);
		    delegate = new EditorDelegate() {
	            public void setValue(Object value) { 
	            	boolean selected = false; 
					if (value instanceof Boolean) {
					    selected = ((Boolean)value).booleanValue();
					} else if (value instanceof String) {
					    selected = value.equals("true");
					}
					checkBox.setSelected(selected);
	            }		
				public Object getCellEditorValue() {
					return Boolean.valueOf(checkBox.isSelected());
				}	 
	            public boolean isCellEditable(EventObject anEvent) {
	        	    boolean b = super.isCellEditable(anEvent);
	        	    if (!b) {
	        	    	return false; 
	        	    }
	        	    if (anEvent instanceof MouseEvent) { 
	        	    	MouseEvent mouseEvent = (MouseEvent)anEvent;
	        	    	Point mousePoint = mouseEvent.getPoint();
	        	    	int column = ScrollTable.this.columnAtPoint(mousePoint);
	        	    	int row = ScrollTable.this.rowAtPoint(mousePoint);
	        	    	Rectangle bounds = ScrollTable.this.getCellRect(row, column, false);
	        	    	Rectangle checkBoxBounds = new Rectangle(bounds.x + (bounds.width - CHECKBOX_WIDTH)/2, bounds.y + (bounds.height - CHECKBOX_HEIGHT) / 2, 
	        	    			CHECKBOX_WIDTH, CHECKBOX_HEIGHT);
						if (checkBoxBounds.contains(mouseEvent.getPoint())) {
	        	    		return true;
	        	    	}
	        	    }
	        	    return false;
	            }	            
	        };
		}
	}
	
	static class ScrollTableBooleanCellRenderer extends JCheckBox implements TableCellRenderer {
		public ScrollTableBooleanCellRenderer() {
			super();
			setHorizontalAlignment(JLabel.CENTER);
			setBorderPainted(true);
			setToolTipText("Double click to check or uncheck");
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,  int column) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				if (table instanceof ScrollTable && ((ScrollTable)table).getHoverRow() == row) {
					setBackground(DefaultScrollTableCellRenderer.hoverColor);
				} else { 
					setBackground(row % 2 == 0 ? table.getBackground() : DefaultScrollTableCellRenderer.everyOtherRowColor);
				}
				setForeground(table.getForeground());
			}
			if (table.isEnabled() && table.getModel().isCellEditable(row, column)) {
				setEnabled(true);
			} else {
				setEnabled(false);
			}
			 
			setSelected((value != null && ((Boolean) value).booleanValue()));			
			if (hasFocus) {
				setBorder(DefaultScrollTableCellRenderer.focusHighlightBorder);
			} else {
				setBorder(DefaultScrollTableCellRenderer.noFocusBorder);
			}

			return this;
		}
	}
	
	public ScrollTable() {
		super();		
		initialize();
	}
	
	public void disableUneditableForeground() {
		defaultTableCellRenderer.disableUneditableForeground();
	}
	
	private void initialize() {
		autoResizeMode = AUTO_RESIZE_OFF;
		// make it bigger on Mac
		setRowHeight(getRowHeight() + 4);
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		scrollTableActionManager = new DefaultScrollTableActionManager(this);
		
		enclosingScrollPane = new JScrollPane(this);
		enclosingScrollPane.getViewport().setBackground(Color.WHITE);
		setPreferredScrollableViewportSize(new Dimension(200,100));
		setIntercellSpacing(new Dimension(2,2));
		
		defaultTableCellRenderer = new DefaultScrollTableCellRenderer();
		setDefaultRenderer(Object.class, defaultTableCellRenderer);		
		setDefaultRenderer(Number.class, defaultTableCellRenderer);		
		setDefaultRenderer(Double.class, defaultTableCellRenderer);		
		setDefaultRenderer(Boolean.class, new ScrollTableBooleanCellRenderer());
		
		setDefaultEditor(Boolean.class, new ScrollTableBooleanEditor());
		
		final TableCellRenderer defaultTableCellHeaderRenderer = getTableHeader().getDefaultRenderer();
		TableCellRenderer defaultTableHeaderRenderer = new TableCellRenderer() {

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = defaultTableCellHeaderRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (component instanceof JLabel){
					JLabel label = (JLabel)component;
					label.setHorizontalAlignment(SwingConstants.CENTER);
					label.setBackground(tableHeaderColor);
					label.setBorder(BorderFactory.createCompoundBorder(tableCellHeaderBorder, new EmptyBorder(2, 0, 2, 0)));
				}
				return component;
			}			
		};
		getTableHeader().setDefaultRenderer(defaultTableHeaderRenderer);
		
		MouseAdapter mouseListener = new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				scrollTableActionManager.triggerPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				scrollTableActionManager.triggerPopup(e);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				hoverRow = rowAtPoint(e.getPoint());
				hoverColumn = columnAtPoint(e.getPoint());
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hoverRow = -1;
				hoverColumn = -1;
				repaint();
			}			
		};
		MouseMotionAdapter mouseMotionListener = new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				hoverRow = rowAtPoint(e.getPoint());
				hoverColumn = columnAtPoint(e.getPoint());
				repaint();
			}
			
		};
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
	}

	public final JScrollPane getEnclosingScrollPane() {
		return enclosingScrollPane;
	}

	public void setValidateExpressionBinding(boolean bValidateExpressionBinding) {
		this.bValidateExpressionBinding = bValidateExpressionBinding;
		setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(this, bValidateExpressionBinding));
		setDefaultEditor(ReactionEquation.class,new TableCellEditorAutoCompletion(this, bValidateExpressionBinding));
	}

	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		enclosingScrollPane.setVisible(aFlag);
	}

	@Override
	public void setAutoResizeMode(int mode) {
		super.setAutoResizeMode(mode);
		if (autoResizeMode == AUTO_RESIZE_OFF) {
			setDefaultRenderer(ScopedExpression.class, new ScopedExpressionTableCellRenderer());
			
			if (componentListener == null) {
				componentListener  = new ComponentAdapter() {
					public void componentResized(ComponentEvent e) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								ScopedExpressionTableCellRenderer.formatTableCellSizes(ScrollTable.this);
							}
						});	
					}
				};
			}
			enclosingScrollPane.removeComponentListener(componentListener);
			enclosingScrollPane.addComponentListener(componentListener);
		} else {
			if (componentListener != null) {
				enclosingScrollPane.removeComponentListener(componentListener);
			}
		}
	}

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		
		// setting up listeners 
		boolean bHasScopedExpressionColumn = false;
		for (int i = 0; i < dataModel.getColumnCount(); i ++) {
			if (getColumnClass(i).equals(ScopedExpression.class)) {
				bHasScopedExpressionColumn = true;
				break;
			} else if (getColumnClass(i).equals(ReactionEquation.class)) {
				bHasScopedExpressionColumn = true;
				break;
			}
		}
		if (bHasScopedExpressionColumn) {
			setValidateExpressionBinding(bValidateExpressionBinding);
			setAutoResizeMode(autoResizeMode);
		} else {
			if (autoResizeMode == AUTO_RESIZE_OFF) {
				autoResizeMode = AUTO_RESIZE_SUBSEQUENT_COLUMNS;
			}
			setAutoResizeMode(autoResizeMode);
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (autoResizeMode == JTable.AUTO_RESIZE_OFF) {
			ScopedExpressionTableCellRenderer.formatTableCellSizes(ScrollTable.this);
		}
	}

	public final int getHoverRow() {
		return hoverRow;
	}

	public final int getHoverColumn() {
		return hoverColumn;
	}

	public final void setScrollTableActionManager(ScrollTableActionManager scrollTableActionManager) {
		if (scrollTableActionManager.getOwnerTable() != this) {
			throw new RuntimeException("ScrollTableActionManager ownerTable doesn't match ScrollTable.this.");
		}
		this.scrollTableActionManager = scrollTableActionManager;
	}	
}
