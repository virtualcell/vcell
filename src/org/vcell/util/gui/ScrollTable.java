package org.vcell.util.gui;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

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
public class ScrollTable extends JTable {
	public enum CheckOption {
		CheckAll("Check All"),
		CheckSelected("Check Selected"),
		UncheckAll("Uncheck All"),
		UncheckSelected("Uncheck Selected");
		
		private String text = null;
		CheckOption(String text) {
			this.text = text;
		}
		public final String getText() {
			return text;
		}		
	}
	
	static class ScrollTableBooleanCellRenderer extends JCheckBox implements TableCellRenderer {
		public ScrollTableBooleanCellRenderer() {
			super();
			setHorizontalAlignment(JLabel.CENTER);
			setBorderPainted(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,  int column) {
			if (table.isEnabled() && table.getModel().isCellEditable(row, column)) {
				setEnabled(true);
				if (isSelected) {
					setBackground(table.getSelectionBackground());
					setForeground(table.getSelectionForeground());
				} else {
					setBackground(table.getBackground());
					setForeground(table.getForeground());
				}
			} else {
				setEnabled(false);
				if (isSelected) {
					setBackground(table.getSelectionBackground());
					setForeground(table.getSelectionForeground());
				} else {
					setBackground(DefaultScrollTableCellRenderer.uneditableBackGround);
					setForeground(table.getForeground());
				}
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
	
	private JScrollPane enclosingScrollPane = null;
	private boolean bValidateExpressionBinding = true;
	private TableModelListener tableModelListener = null;
	private ComponentAdapter componentListener = null;
	
	public ScrollTable() {
		super();		
		initialize();
	}
	
	private void initialize() {		
		autoResizeMode = AUTO_RESIZE_OFF;
		// make it bigger on Mac
		setRowHeight(getRowHeight() + 2);
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		enclosingScrollPane = new JScrollPane(this);
		setPreferredScrollableViewportSize(new Dimension(200,100));
		
		setDefaultRenderer(Object.class, new DefaultScrollTableCellRenderer());		
		setDefaultRenderer(Number.class, new DefaultScrollTableCellRenderer());		
		setDefaultRenderer(Double.class, new DefaultScrollTableCellRenderer());		
		setDefaultRenderer(Boolean.class, new ScrollTableBooleanCellRenderer());
		setDefaultRenderer(ScopedExpression.class, new ScopedExpressionTableCellRenderer());
		
		// to gain focus if being clicked.
		MouseAdapter mouseListener = new MouseAdapter() {
			public void mouseClicked(final MouseEvent e) {
				if (!hasFocus()) {
					requestFocusInWindow();
						
					if (e.getButton() == MouseEvent.BUTTON1) {
						addFocusListener(new FocusListener() {
							public void focusLost(FocusEvent e) {
							}									
							public void focusGained(FocusEvent e) {
								removeFocusListener(this);
								Robot robot;
								try {
									robot = new Robot();
									robot.mousePress(InputEvent.BUTTON1_MASK);
									robot.mouseRelease(InputEvent.BUTTON1_MASK);
								} catch (AWTException ex) {
									ex.printStackTrace();
								}											
							}
						});
					}
				}
			}
		};
		addMouseListener(mouseListener);
	}
	
	public final JScrollPane getEnclosingScrollPane() {
		return enclosingScrollPane;
	}
	
	private void initConnections() {
		boolean bHasScopedExpressionColumn = false;
		for (int i = 0; i < dataModel.getColumnCount(); i ++) {
			if (getColumnClass(i).equals(ScopedExpression.class)) {
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

	public final void setValidateExpressionBinding(boolean bValidateExpressionBinding) {
		this.bValidateExpressionBinding = bValidateExpressionBinding;
		setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(this, bValidateExpressionBinding));
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
			if (componentListener == null) {
				componentListener  = new ComponentAdapter() {
					public void componentResized(ComponentEvent e) {
						ScopedExpressionTableCellRenderer.formatTableCellSizes(ScrollTable.this,null,null);
					}
				};
			}
			enclosingScrollPane.removeComponentListener(componentListener);
			enclosingScrollPane.addComponentListener(componentListener);
			
			if (tableModelListener == null) {
				tableModelListener  = new javax.swing.event.TableModelListener(){
					public void tableChanged(javax.swing.event.TableModelEvent e){
						ScopedExpressionTableCellRenderer.formatTableCellSizes(ScrollTable.this,null,null);
					}
				};
			}
			getModel().removeTableModelListener(tableModelListener);
			getModel().addTableModelListener(tableModelListener);			
		} else {
			if (componentListener != null) {
				enclosingScrollPane.removeComponentListener(componentListener);
			}
			if (tableModelListener != null) {
				getModel().removeTableModelListener(tableModelListener);
			}
		}
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (tableModelListener != null) {
			getModel().removeTableModelListener(tableModelListener);
		}
		super.setModel(dataModel);
		initConnections();
	}	
}
