package cbit.vcell.mapping.gui;

import java.awt.Color;

import cbit.vcell.graph.ShapeModeInterface;

public class SmallShapeManager implements ShapeModeInterface {
	
	private boolean showDifferencesOnly = true;
	private boolean bShowMoleculeColor = true;
	private boolean bShowNonTrivialOnly = false;

	public final Color uneditableShape = new Color(0x9F4F07);
	private boolean editable = true;
	
	SmallShapeManager(boolean showDifferencesOnly, boolean bShowMoleculeColor, boolean bShowNonTrivialOnly, boolean editable) {
		this.showDifferencesOnly = showDifferencesOnly;
		this.bShowMoleculeColor = bShowMoleculeColor;
		this.bShowNonTrivialOnly = bShowNonTrivialOnly;
		this.editable = editable;
	}
	@Override
	public void setShowDifferencesOnly(boolean showDifferencesOnly) {
		this.showDifferencesOnly = showDifferencesOnly;
	}
	@Override
	public boolean isShowDifferencesOnly() {
		return showDifferencesOnly;
	}
	@Override
	public void setShowMoleculeColor(boolean bShowMoleculeColor) {
		this.bShowMoleculeColor = bShowMoleculeColor;
	}
	@Override
	public boolean isShowMoleculeColor() {
		return bShowMoleculeColor;
	}
	@Override
	public void setShowNonTrivialOnly(boolean bShowNonTrivialOnly) {
		this.bShowNonTrivialOnly = bShowNonTrivialOnly;
	}
	@Override
	public boolean isShowNonTrivialOnly() {
		return bShowNonTrivialOnly;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public boolean isEditable() {
		return editable;
	}
}