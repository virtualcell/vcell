/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.constants.GuiConstants;

import cbit.vcell.parser.ASTFuncNode;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;

@SuppressWarnings("serial")
public class TableCellEditorAutoCompletion extends DefaultCellEditor {
	protected TextFieldAutoCompletion textFieldAutoCompletion = null;
	private JTable thisTable = null;
	private String expressionError = null;
	
	public TableCellEditorAutoCompletion(JTable table) {		
		this(new TextFieldAutoCompletion(), table);
	}
	
	public TableCellEditorAutoCompletion(TextFieldAutoCompletion textField, JTable table) {		
		super(textField);
		this.textFieldAutoCompletion = (TextFieldAutoCompletion)getComponent();
		this.thisTable = table;	
	}
	
	@Override
	public boolean stopCellEditing() {
		if (thisTable.getCellEditor() == null) {
			return true;
		}
		if (textFieldAutoCompletion.getSelectedIndex() >= 0) {
			return false;
		}
		
		final int editingRow = thisTable.getEditingRow();
		final int editingColumn = thisTable.getEditingColumn();
		textFieldAutoCompletion.stopEditing();
		boolean bExpressionValid = true;
		String error = null;
		if (thisTable.getColumnClass(editingColumn).equals(ScopedExpression.class)) {
			if (textFieldAutoCompletion.getSymbolTable() != null) {
				ScopedExpression scopedExpression = (ScopedExpression) thisTable.getValueAt(editingRow, editingColumn);
				String text = textFieldAutoCompletion.getText();
				if (text.trim().length() > 0) {
					try {
						Expression exp = new Expression(text);
						exp.validateUnscopedSymbols();
						if (scopedExpression == null || scopedExpression.isValidateFunctionBinding()){
							FunctionInvocation[] functionInvocations = exp.getFunctionInvocations(null);
							for (FunctionInvocation functionInvocation : functionInvocations){
								String formalDefinition = functionInvocation.getFormalDefinition();
								if (functionInvocation.getFunctionId() == FunctionType.USERDEFINED){
									SymbolTableFunctionEntry stfe = (SymbolTableFunctionEntry) textFieldAutoCompletion.getSymbolTable().getEntry(formalDefinition);
									if (stfe == null){
										//
										// check for wrong number of arguments
										//
										Map<String, SymbolTableEntry> entries = new HashMap<String, SymbolTableEntry>();
										textFieldAutoCompletion.getSymbolTable().getEntries(entries);
										System.out.println("available symbols");
										for (String symbol : entries.keySet()){
											System.out.print(symbol+",");
										}
										System.out.println("");
										throw new ExpressionBindingException("unknown function "+formalDefinition,formalDefinition);
									}
								}else{ // built in function, check arguments
									FunctionType functionType = functionInvocation.getFunctionId();
									String formalDefinitionBuiltin = ASTFuncNode.getFormalDefinition(functionType.getName(),functionType.getArgTypes());
									if (!formalDefinition.equals(formalDefinitionBuiltin)){
										throw new ExpressionBindingException("expecting "+formalDefinitionBuiltin, formalDefinition);
									}
								}
							}
						}
						if (scopedExpression == null || scopedExpression.isValidateIdentifierBinding()) {
							exp.bindExpression(textFieldAutoCompletion.getSymbolTable());
						}
					} catch (ExpressionBindingException ex) {
						ex.printStackTrace(System.out);
						error = ex.getMessage() + "\n\nUse 'Ctrl-Space' to see a list of available names in your model or 'Esc' to revert to the original expression.";
						DialogUtils.showErrorDialog(thisTable.getParent(), error);
						bExpressionValid = false;
					} catch (ExpressionException ex) {
						ex.printStackTrace(System.out);
						error = ex.getMessage() + "\n\nUse 'Esc' to revert to the original expression.";
						DialogUtils.showErrorDialog(thisTable.getParent(), error);
						bExpressionValid = false;
					}
				}
			}
		}
		if (!bExpressionValid) {
			final String message = error != null ? error : "The expression is not valid.";
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					thisTable.requestFocus();
					thisTable.setRowSelectionInterval(editingRow, editingRow);
					showExpressionError(message);
					textFieldAutoCompletion.requestFocus();										
				}				
			});
			return false;
		}
		clearExpressionError();
		return super.stopCellEditing();
	}

	/** Word and sentence that stay on the editor after the error dialog closes. */
	void showExpressionError(String message) {
		expressionError = message;
		JComponent editor = (JComponent) getComponent();
		editor.setBorder(BorderFactory.createTitledBorder(new LineBorder(GuiConstants.ERROR_TEXT_COLOR), "error"));
		editor.getAccessibleContext().setAccessibleDescription(message);
		editor.setToolTipText(message);
	}

	void clearExpressionError() {
		expressionError = null;
		JComponent editor = (JComponent) getComponent();
		editor.setBorder(null);
		editor.getAccessibleContext().setAccessibleDescription(null);
		editor.setToolTipText(null);
	}

	String expressionError() {
		return expressionError;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof ScopedExpression) {
			ScopedExpression scopedExpression = (ScopedExpression)value;
			if (scopedExpression.getNameScope() != null) {
				textFieldAutoCompletion.setSymbolTable(scopedExpression.getNameScope().getScopedSymbolTable());
				textFieldAutoCompletion.setAutoCompleteSymbolFilter(scopedExpression.getAutoCompleteSymbolFilter());
			}
		} else if (value instanceof ModelProcessEquation) {
			ModelProcessEquation reactionEquation = (ModelProcessEquation)value;
			if (reactionEquation.getNameScope() != null) {
				textFieldAutoCompletion.setSymbolTable(reactionEquation.getNameScope().getScopedSymbolTable());
				textFieldAutoCompletion.setAutoCompleteSymbolFilter(reactionEquation.getAutoCompleteSymbolFilter());
			}
		}
		clearExpressionError();
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	@Override
	public void cancelCellEditing() {
		if (textFieldAutoCompletion.getSelectedIndex() >= 0) {
			return;
		} 
		super.cancelCellEditing();
	 }	 
}
