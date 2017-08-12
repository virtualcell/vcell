/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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
import java.util.EventObject;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.vcell.util.Issue;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.gui.ModelProcessEquation;
import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;
import cbit.vcell.units.VCUnitDefinition;

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
	private static final String javaVersion = System.getProperty("java.version");
	
	private JPanel contentPanel = new JPanel();
	private JToolBar pageToolBar = null;
	private JLabel pageLabel = null;
	private JButton firstPageButton, previousPageButton, nextPageButton, lastPageButton; 
	private JScrollPane enclosingScrollPane = null;
	private ComponentAdapter componentListener = null;
	protected int hoverRow = -1, hoverColumn = -1;
	private DefaultScrollTableCellRenderer defaultTableCellRenderer = null;
	private ScrollTableActionManager scrollTableActionManager = null;
	private VCellSortTableModel<?> vcellSortTableModel = null;	
	
	class ScrollTableBooleanEditor extends DefaultCellEditor {
		static final int CHECKBOX_WIDTH = 20;
		static final int CHECKBOX_HEIGHT = 20;
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
	
public static class ScrollTableBooleanCellRenderer extends JCheckBox implements TableCellRenderer {
	public ScrollTableBooleanCellRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
		setBorderPainted(true);
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
		TableModel tableModel = table.getModel();
		if(tableModel instanceof SortTableModel) {
			issueRenderer(this, table, row, column, (SortTableModel)tableModel);
		}
		return this;
	}
	private static void issueRenderer(ScrollTableBooleanCellRenderer renderer, JTable table, int row, int column, SortTableModel tableModel) {
		List<Issue> issueListError = tableModel.getIssues(row, column, Issue.SEVERITY_ERROR);
		List<Issue> issueListWarning = tableModel.getIssues(row, column, Issue.SEVERITY_WARNING);
		Icon icon = null;	// we don't set any icon if column is boolean, it interferes with checkbox paint on screen
		Color red = Color.getHSBColor(0f, 0.4f, 1.0f);		// hue, saturation, brightness

		if (issueListError.size() > 0) {
			if (column == 0) {
				//icon = VCellIcons.issueErrorIcon;
				renderer.setToolTipText(Issue.getHtmlIssueMessage(issueListError));
				renderer.setBorder(new MatteBorder(1,1,1,0, red));
			} else if (column == table.getColumnCount() - 1) {
				renderer.setBorder(new MatteBorder(1,0,1,1, red));
			} else {
				renderer.setBorder(new MatteBorder(1,0,1,0, red));
			}
		} else if(issueListWarning.size() > 0) {
			if (column == 0) {
				//icon = VCellIcons.issueWarningIcon;
				renderer.setToolTipText(Issue.getHtmlIssueMessage(issueListWarning));
				renderer.setBorder(new MatteBorder(1,1,1,0,Color.orange));
			} else if (column == table.getColumnCount() - 1) {
				renderer.setBorder(new MatteBorder(1,0,1,1,Color.orange));
			} else {
				renderer.setBorder(new MatteBorder(1,0,1,0,Color.orange));
			}
		} else {
			if(column == 0) {
				//icon = VCellIcons.issueGoodIcon;
			}
			renderer.setToolTipText(null);
			renderer.setBorder(DefaultScrollTableCellRenderer.DEFAULT_GAP);
		}
		renderer.setIcon(icon);
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
		putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
		scrollTableActionManager = new DefaultScrollTableActionManager(this);
		
		enclosingScrollPane = new JScrollPane(this);
		enclosingScrollPane.getViewport().setBackground(Color.WHITE);
		
		contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(enclosingScrollPane, BorderLayout.CENTER);
		
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
					label.setBorder(BorderFactory.createCompoundBorder(tableCellHeaderBorder, new EmptyBorder(1, 0, 1, 0)));
				}
				return component;
			}			
		};
		getTableHeader().setDefaultRenderer(defaultTableHeaderRenderer);
		
		MouseAdapter mouseListener = new MouseAdapter() {
		
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (!javaVersion.startsWith("1.5")) {
					if (!hasFocus()) {
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
						requestFocusInWindow();
					}
				}
			}
		
			
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
	
	/**
	 * set a speciality renderer
	 * @param str not null
	 */
	public void setSpecialityRenderer(SpecialtyTableRenderer str) {
		Objects.requireNonNull(str);
		for (Class<?> clzz : str.supportedTypes()) {
			setDefaultRenderer(clzz, str);
		}
	}

	public final JComponent getEnclosingScrollPane() {
		return contentPanel;
	}

	public void installCellEditors() {
		setDefaultEditor(VCUnitDefinition.class,new TableCellEditorAutoCompletion(this));
		setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(this));
		setDefaultEditor(ModelProcessEquation.class,new TableCellEditorAutoCompletion(this));
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
			setDefaultRenderer(VCUnitDefinition.class, new DefaultScrollTableCellRenderer(){
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (value instanceof VCUnitDefinition){
						setText(((VCUnitDefinition)value).getSymbolUnicode());
					}
					return this;
				}
			});
			
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
		if (dataModel instanceof VCellSortTableModel<?>) {
			vcellSortTableModel = (VCellSortTableModel<?>) dataModel;
		}
		
		// setting up listeners 
		boolean bHasScopedExpressionColumn = false;
		for (int i = 0; i < dataModel.getColumnCount(); i ++) {
			if (getColumnClass(i).equals(ScopedExpression.class)) {
				bHasScopedExpressionColumn = true;
				break;
			} else if (getColumnClass(i).equals(ModelProcessEquation.class)) {
				bHasScopedExpressionColumn = true;
				break;
			}
		}
		if (bHasScopedExpressionColumn) {
			installCellEditors();
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
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ScopedExpressionTableCellRenderer.formatTableCellSizes(ScrollTable.this);
				}
			});
		}
		if (vcellSortTableModel != null) {
			if (vcellSortTableModel.getNumPages() == 1) {
				if (pageToolBar != null && getPageToolBar().getParent() != null) {
					contentPanel.remove(getPageToolBar());
					contentPanel.repaint();
				}
			} else if (vcellSortTableModel.getNumPages() > 1) {
				if (getPageToolBar().getParent() == null) {
					contentPanel.add(getPageToolBar(), BorderLayout.SOUTH);
					contentPanel.repaint();
				}
				updatePageToolBar();
			}
		}
	}
	
	private ActionListener pageButtonActionListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			if (vcellSortTableModel == null) {
				return;
			}
			Object source = e.getSource();
			if (source == firstPageButton) {
				vcellSortTableModel.gotoFirstPage();
			} else if (source == previousPageButton) {
				vcellSortTableModel.gotoPreviousPage();
			} else if (source == nextPageButton) {
				vcellSortTableModel.gotoNextPage();
			} else if (source == lastPageButton) {
				vcellSortTableModel.gotoLastPage();
			}
			updatePageToolBar();
		}
	};
	
	private JButton createToolBarButton(Icon icon) {
		JButton button = new JButton(icon);
		button.addActionListener(pageButtonActionListener);
		button.setMargin(new Insets(2, 5, 2, 2));
		return button;
	}
	private JToolBar getPageToolBar() {
		if (pageToolBar == null) {
			
			pageLabel = new JLabel();
			pageLabel.setBorder(new EmptyBorder(2, 5, 2, 2));
			firstPageButton = createToolBarButton(VCellIcons.firstPageIcon);
			previousPageButton = createToolBarButton(VCellIcons.previousPageIcon);
			nextPageButton = createToolBarButton(VCellIcons.nextPageIcon);
			lastPageButton = createToolBarButton(VCellIcons.lastPageIcon);
			
			pageToolBar = new JToolBar();
			pageToolBar.setFloatable(false);
			pageToolBar.add(Box.createHorizontalGlue());
			pageToolBar.add(firstPageButton);
			pageToolBar.add(previousPageButton);
			pageToolBar.add(pageLabel);
			pageToolBar.add(nextPageButton);
			pageToolBar.add(lastPageButton);
		}
		return pageToolBar;
	}

	public final int getHoverRow() {
		return hoverRow;
	}

	public final int getHoverColumn() {
		return hoverColumn;
	}

//
// A way to provide separate tooltips for each cell of a table
// Right now we only offer tooltips for the table rows with errors / warnings associated
// Implementation of that mechanism in DefaultScrollTableCellRenderer
//
//@Override	
//	public String getToolTipText(MouseEvent e) {
//		String tip = null;
//		java.awt.Point p = e.getPoint();
//		int rowIndex = rowAtPoint(p);
//		int colIndex = columnAtPoint(p);
//		try {
//			//comment row, exclude heading
//			if(rowIndex >= 0){
//				tip = getValueAt(rowIndex, colIndex).toString();
//			}
//		} catch (RuntimeException e1) {
//			//catch null pointer exception if mouse is over an empty line
//		}
//		return tip;
//	}

	public final void setScrollTableActionManager(ScrollTableActionManager scrollTableActionManager) {
		if (scrollTableActionManager.getOwnerTable() != this) {
			throw new RuntimeException("ScrollTableActionManager ownerTable doesn't match ScrollTable.this.");
		}
		this.scrollTableActionManager = scrollTableActionManager;
	}
	public final ScrollTableActionManager getScrollTableActionManager() {
		return scrollTableActionManager;
	}

	private void updatePageToolBar() {
		firstPageButton.setEnabled(vcellSortTableModel.hasPreviousPage());
		previousPageButton.setEnabled(vcellSortTableModel.hasPreviousPage());
		nextPageButton.setEnabled(vcellSortTableModel.hasNextPage());
		lastPageButton.setEnabled(vcellSortTableModel.hasNextPage());
		pageLabel.setText(vcellSortTableModel.getPageDescription());
	}	
}
