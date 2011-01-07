package org.vcell.util.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
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
//	private static final String javaVersion = System.getProperty("java.version");	
	
	private JScrollPane enclosingScrollPane = null;
	private boolean bValidateExpressionBinding = true;
	private ComponentAdapter componentListener = null;
	protected int hoverRow = -1, hoverColumn = -1;
	private DefaultScrollTableCellRenderer defaultTableCellRenderer = null;

//	private JTextField popupTextField = null;
//	private JPopupMenu popupMenu = null;
//	private JLabel popupMenuTitleLabel = null;
//	private JSeparator popupMenuSeparator = null;
//	private JMenuItem popupMenuItemCheckSelected = null;
//	private JMenuItem popupMenuItemUncheckSelected = null;
//	private JLabel popupTextFieldLabel1 = null;
//	private JLabel popupTextFieldLabel2 = null;
//	private PopupActionListner popupActionListner = new PopupActionListner();
//	private Set<Integer> disabledColumnPopups = new HashSet<Integer>();

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
	
	static class ScrollTableBooleanEditor extends DefaultCellEditor {
		public ScrollTableBooleanEditor() {
		    super(new JCheckBox());
		    JCheckBox checkBox = (JCheckBox)getComponent();
		    checkBox.setHorizontalAlignment(JCheckBox.CENTER);
		    clickCountToStart = 2;
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
	
//	private class PopupActionListner implements ActionListener {
//
//		private int[] rows;
//		private int column = -1;
//		void setRowsAndColumn(int[] rows, int column) {
//			this.rows = rows;
//			this.column = column;
//		}
//		public void actionPerformed(ActionEvent e) {
//			popupMenu.setVisible(false);
//			for (int row : rows) {
//				Object value = null;
//				if (e.getSource() == popupMenuItemCheckSelected) {
//					value = true;
//				} else if (e.getSource() == popupMenuItemUncheckSelected) {
//					value = false;
//				} else if (e.getSource() == popupTextField) {
//					value = popupTextField.getText();
//				}
//				dataModel.setValueAt(value, row, column);
//			}			
//		}
//		
//	}
	
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
		
		// to gain focus if being clicked.
		MouseAdapter mouseListener = new MouseAdapter() {
			
//			@Override
//			public void mousePressed(MouseEvent e) {
//				popupTriggered(e);
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				// TODO Auto-generated method stub
//				popupTriggered(e);
//			}

//			@Override
//			public void mouseClicked(final MouseEvent e) {
//				if (!javaVersion.startsWith("1.5")) {
//					if (!hasFocus()) {
//						if (e.getButton() == MouseEvent.BUTTON1) {
//							addFocusListener(new FocusListener() {
//								public void focusLost(FocusEvent e) {
//								}
//								public void focusGained(FocusEvent e) {
//									removeFocusListener(this);
//									Robot robot;
//									try {
//										robot = new Robot();
//										robot.mousePress(InputEvent.BUTTON1_MASK);
//										robot.mouseRelease(InputEvent.BUTTON1_MASK);
//									} catch (AWTException ex) {
//										ex.printStackTrace();
//									}
//								}
//							});
//						}
//						requestFocusInWindow();
//					}
//				}
//			}

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
	
//	public void disablePopupAtColumn(int column) {
//		disabledColumnPopups.add(column);
//	}
//	protected void popupTriggered(MouseEvent mouseEvent) {
//		if (!mouseEvent.isPopupTrigger()) {
//			return;
//		}
//		int rows[] = getSelectedRows();
//		if (rows == null || rows.length == 0) {
//			return;
//		}
//		int selectedColumn = columnAtPoint(mouseEvent.getPoint());
//		if (selectedColumn < 0 || selectedColumn >= dataModel.getColumnCount()) {
//			return;
//		}
//		if (disabledColumnPopups.contains(selectedColumn)) {
//			return;
//		}
//		boolean bUneditable = true;
//		for (int row : rows) {
//			if (dataModel.isCellEditable(row, selectedColumn)) {
//				bUneditable = false;
//				break;
//			}
//		}
//		if (bUneditable) {
//			return;
//		}
//		String columnName = dataModel.getColumnName(selectedColumn);
//		if (columnName.equalsIgnoreCase("name")) {
//			return;
//		}
//		Class<?> columnClass = dataModel.getColumnClass(selectedColumn);
//		Component editorComponent = null;
//		TableCellEditor cellEditor = getColumnModel().getColumn(selectedColumn).getCellEditor();
//		if (cellEditor == null) {
//			cellEditor = getDefaultEditor(columnClass); 
//		}
//		if (cellEditor instanceof DefaultCellEditor) {
//			editorComponent = ((DefaultCellEditor)cellEditor).getComponent();
//		}
//		if (editorComponent == null || !(editorComponent instanceof JCheckBox) && !(editorComponent instanceof JTextField)) {
//			return;
//		}
//		popupActionListner.setRowsAndColumn(rows, selectedColumn);
//		if (popupMenu == null) {
//			popupMenu = new JPopupMenu();
//			popupMenuTitleLabel = new JLabel();
//			popupMenuTitleLabel.setFont(popupMenuTitleLabel.getFont().deriveFont(Font.BOLD));
//			popupMenuSeparator = new JSeparator();
//		}		
//		popupMenuTitleLabel.setText(columnName);
//		popupMenu.removeAll();
//		popupMenu.add(popupMenuTitleLabel);
//		popupMenu.add(popupMenuSeparator);		
//		if (editorComponent instanceof JCheckBox) {
//			if (popupMenuItemCheckSelected == null) {
//				popupMenuItemCheckSelected = new JMenuItem("Check Selected");
//				popupMenuItemCheckSelected.addActionListener(popupActionListner);
//				popupMenuItemUncheckSelected = new JMenuItem("Uncheck Selected");
//				popupMenuItemUncheckSelected.addActionListener(popupActionListner);
//			}
//			popupMenu.add(popupMenuItemCheckSelected);
//			popupMenu.add(popupMenuItemUncheckSelected);
//		} else if (editorComponent instanceof JTextField) {
//			if (popupTextField == null) {
//				popupTextField = new JTextField(5);
//				popupTextField.addActionListener(popupActionListner);
//				popupTextField.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(2, 4, 2, 4), popupTextField.getBorder()));
//				popupTextFieldLabel1 = new javax.swing.JLabel(" Set Selected to ");
//				popupTextFieldLabel2 = new JLabel(" (Press Enter or Return) ");
//				popupTextFieldLabel2.setFont(popupTextFieldLabel2.getFont().deriveFont(popupTextFieldLabel2.getFont().getSize2D() - 1));
//			}
//			popupMenu.add(popupTextFieldLabel1);
//			popupMenu.add(popupTextField);
//			popupMenu.add(popupTextFieldLabel2);
//		}
//		popupMenu.show(this,mouseEvent.getX(),mouseEvent.getY());		
//	}

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
}
