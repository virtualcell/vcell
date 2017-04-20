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

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;

import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.ModelProcessEquation;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.gui.TextFieldAutoCompletion;

/**
 * EditorScrollTable extends ScrollTable and enables the user to keep editing using key from one cell to another.
 * 
 * after a value is typed, check to see if the new value is valid. If not, 
 * editing is not stopped and tooltip is set to the error message.
 * 
 * Also it gets auto completion list from table model
 *  
 * @author fgao
 *
 */
@SuppressWarnings("serial")
public class EditorScrollTable extends JSortTable {
	
	public class DefaultScrollTableComboBoxEditor extends DefaultCellEditor {
		private JComboBox comboBox = null;
		public DefaultScrollTableComboBoxEditor(JComboBox comboBox) {
			super(comboBox);
			this.clickCountToStart = 2;
			this.comboBox = (JComboBox) getComponent();
			this.comboBox.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {					
				}
				
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						bEscKeyPressed = true;
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						bEditingStoppedFromKey = true;
					}
				}				
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						bEscKeyPressed = true;
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER
						|| e.getKeyCode() == KeyEvent.VK_TAB) {
						bEditingStoppedFromKey = true;
					}
				}
			});
		}
	}
	
	static class ReactionEquationTextFieldAutoCompletion extends TextFieldAutoCompletion {
//		public ReactionEquationTextFieldAutoCompletion() {
//			super();
//			getDocument().addDocumentListener(new DocumentListener() {
//				
//				public void removeUpdate(DocumentEvent e) {
//				}
//				
//				public void insertUpdate(DocumentEvent e) {	
//					if (e.getLength() > 1) {
//						return;
//					}
//					final int caretPos = getCaretPosition();
//					final String text = getText();					
//					final int len = text.length();
//					if (len < 1) {
//						return;
//					}
//					SwingUtilities.invokeLater(new Runnable() {						
//						public void run() {
//							try {
//								Document doc = getDocument();
//								if (text.charAt(caretPos) == '-' && (caretPos + 1 >= len || text.charAt(caretPos + 1) != '>')) {
//									doc.insertString(caretPos + 1, ">", null);
//									setCaretPosition(caretPos + 2);
//								}
//							} catch (Exception ex) {
//								ex.printStackTrace();
//							} finally {				
//							}							
//						}
//					});
//				}
//				
//				public void changedUpdate(DocumentEvent e) {					
//				}
//			});
//		}
		
	}
	
	protected class DefaultScrollTableAutoCompleteCellEditor extends TableCellEditorAutoCompletion {
		public DefaultScrollTableAutoCompleteCellEditor(JTable table) {
			this(new TextFieldAutoCompletion(), table);
		}
		public DefaultScrollTableAutoCompleteCellEditor(TextFieldAutoCompletion textField, JTable table) {
			super(textField, table);
			textFieldAutoCompletion.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					if (textFieldAutoCompletion.getSelectedIndex() < 0) {
						bEditingStoppedFromKey = true;
					}
				}
			});
			textFieldAutoCompletion.addKeyListener(new KeyListener() {				
				public void keyTyped(KeyEvent e) {					
				}
				
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						bEscKeyPressed = true;
					} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
						bEditingStoppedFromKey = true;
					}
				}				
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						bEscKeyPressed = true;
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER
							|| e.getKeyCode() == KeyEvent.VK_TAB) {
						bEditingStoppedFromKey = true;
					}
				}
			});
		}
		@Override
		public boolean stopCellEditing() {
			if (textFieldAutoCompletion.getSelectedIndex() >= 0) {
				return false;
			}
			
			String inputValue = textFieldAutoCompletion.getText();
			boolean bOK = true;
			if (getModel() instanceof AutoCompleteTableModel) {
				AutoCompleteTableModel tableModel = (AutoCompleteTableModel)getModel();
				tableStopEditingErrorMessage = tableModel.checkInputValue(inputValue, getEditingRow(), getEditingColumn()); 
				if (tableStopEditingErrorMessage != null && tableStopEditingErrorMessage.length() > 0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							int row = getEditingRow();
							if (row < 0) {
								return;
							}
							requestFocus();
							setRowSelectionInterval(row, row);
							((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
							textFieldAutoCompletion.requestFocus();	
							textFieldAutoCompletion.setToolTipText(tableStopEditingErrorMessage);
							ToolTipManager.sharedInstance().mouseMoved(
							        new MouseEvent(textFieldAutoCompletion, 0, 0, 0,
							                4, 0, 0, false));
						}				
					});
					bOK = false;
				}
			}
			
			if (bOK) {
				bOK = super.stopCellEditing();
			}
			return bOK;
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			((JComponent)getComponent()).setToolTipText(null);
			if (getModel() instanceof AutoCompleteTableModel) {
				AutoCompleteTableModel tableModel = (AutoCompleteTableModel)getModel();			
				textFieldAutoCompletion.setSymbolTable(tableModel.getSymbolTable(row, column));
				textFieldAutoCompletion.setAutoCompleteSymbolFilter(tableModel.getAutoCompleteSymbolFilter(row, column));
				textFieldAutoCompletion.setAutoCompletionWords(tableModel.getAutoCompletionWords(row, column));
			}
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
		@Override
		public void cancelCellEditing() {
			if (!bEscKeyPressed && tableStopEditingErrorMessage != null && tableStopEditingErrorMessage.length() > 0) {
				DialogUtils.showWarningDialog(EditorScrollTable.this, "Your last entry was discarded due to the following reason:\n\n    " + tableStopEditingErrorMessage);
				textFieldAutoCompletion.setToolTipText(null);
			}
			super.cancelCellEditing();
		}
	}
	
	private boolean bEditingStoppedFromKey = false;
	protected String tableStopEditingErrorMessage = null;
	private boolean bEscKeyPressed = false;
	
	public EditorScrollTable() {
		super();		
		initialize();
	}
		
	private void initialize() {
		setDefaultEditor(Object.class, new DefaultScrollTableAutoCompleteCellEditor(this));
	}
	
	@Override
	public void installCellEditors() {
		super.installCellEditors();
		setDefaultEditor(ModelProcessEquation.class,new DefaultScrollTableAutoCompleteCellEditor(new ReactionEquationTextFieldAutoCompletion(), this));
	}
	
	@Override
	public void editingStopped(ChangeEvent e) {
		int editRow = editingRow;
		int editColumn = editingColumn;
		super.editingStopped(e);
		if (!bEditingStoppedFromKey) {
			return;
		}
		for (int c = editColumn + 1; c < getColumnCount(); c ++) {
			if (dataModel.isCellEditable(editRow, c)) {
				editCellAt(editRow, c);
				return;
			}
		}
		for (int r = editRow + 1; r < getRowCount(); r ++) {
			for (int c = 0; c < getColumnCount(); c ++) {
				if (dataModel.isCellEditable(r, c)) {
					editCellAt(r, c);
					return;
				}
			}
		}
	}

	@Override
	public boolean editCellAt(final int row, final int column, EventObject e) {
		hoverColumn = -1;
		hoverRow = -1;
		bEditingStoppedFromKey = false;
		bEscKeyPressed = false;
		tableStopEditingErrorMessage = null;
		Rectangle rect =  getCellRect(row, column, true);
		scrollRectToVisible(rect);
		boolean r = super.editCellAt(row, column, e);
		if (r) {
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					if (getEditorComponent() != null) {
						getEditorComponent().requestFocusInWindow();
						if (getEditorComponent() instanceof JTextField) {
							((JTextField)getEditorComponent()).selectAll();
						}
					}
				}
			});
		}
		return r;
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		if (getCellEditor() != null) {
			if (!getCellEditor().stopCellEditing()) {
				getCellEditor().cancelCellEditing();
			}
		}
	}
}
