/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.Version;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.clientdb.DocumentManager;

/**
 * The Database File Info of the BioModel open in an editor: the same contents tree the database browsers show,
 * under a notice that can't be missed when it matters: that the model has never been saved to the database, or that
 * it has been changed since its last save, whose record is what's shown. A model as it was saved shows no notice.
 */
@SuppressWarnings("serial")
public class OpenBioModelDatabaseInfoPanel extends JPanel {
	private static final Logger lg = LogManager.getLogger(OpenBioModelDatabaseInfoPanel.class);

	/** How loudly a notice speaks: an alert asks for action, a caution qualifies what's shown under it. */
	enum NoticeLevel { ALERT, CAUTION }

	private final NoticeBanner notice = new NoticeBanner();
	private final BioModelMetaDataPanel contentsPanel = new BioModelMetaDataPanel(); // never given a SelectionManager, so only this panel sets what it shows
	private BioModel bioModel = null;
	private DocumentManager documentManager = null;
	private int lookupGeneration = 0; // a lookup still running when the model changes doesn't overwrite the newer one
	private int changeCheckGeneration = 0; // likewise for the check for changes since the last save

	public OpenBioModelDatabaseInfoPanel() {
		super(new BorderLayout(0, 6));
		this.setName("OpenBioModelDatabaseInfoPanel");
		this.add(this.notice, BorderLayout.NORTH);
		this.add(this.contentsPanel, BorderLayout.CENTER);
		// edits can be made while this is open: whether there are changes since the save is checked again as it's shown
		this.addHierarchyListener(e -> {
			if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) == 0 || !this.isShowing()) return;
			if (!this.isUnsaved()) this.checkForChanges(this.bioModel.getVersion());
		});
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	/** True when the model has no database record, so the tab showing this panel can call attention to itself. */
	public boolean isUnsaved() {
		return this.bioModel == null || this.bioModel.getVersion() == null;
	}

	/** Shows the given model's saved record; called whenever the model's root is selected, so a save is picked up. */
	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
		this.lookupGeneration++;
		this.contentsPanel.setBioModelInfo(null);
		this.contentsPanel.setVisible(!this.isUnsaved()); // nothing to list, so no empty box under the notice
		if (this.isUnsaved()) {
			this.notice.display(NoticeLevel.ALERT, "Not saved to the VCell database yet",
					"This model has no database record, so there is no Database File Info to show. "
					+ "Save it (File → Save) to create one.");
			return;
		}
		Version version = this.bioModel.getVersion();
		this.notice.setVisible(false); // until the check says whether there are changes this record doesn't show
		this.lookUpBioModelInfo(version);
		this.checkForChanges(version);
	}

	// the record is the last save's: say so only when the model has changed since, as found by comparing it with that save
	// (the same comparison as when closing its window); comparing can mean reading the save back, so it's off the EDT
	private void checkForChanges(Version version) {
		if (this.documentManager == null) return;
		final int generation = ++this.changeCheckGeneration;
		final DocumentManager manager = this.documentManager;
		final BioModel model = this.bioModel;
		new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				return manager.isChanged(model);
			}

			@Override
			protected void done() {
				if (generation != OpenBioModelDatabaseInfoPanel.this.changeCheckGeneration || model != OpenBioModelDatabaseInfoPanel.this.bioModel) return;
				boolean changed;
				try {
					changed = this.get();
				} catch (InterruptedException | ExecutionException e) {
					OpenBioModelDatabaseInfoPanel.lg.warn("Couldn't compare the open model with its last save", e);
					changed = true; // unknown: the caution is the safe side
				}
				OpenBioModelDatabaseInfoPanel.this.showChanged(changed, version);
			}
		}.execute();
	}

	private void showChanged(boolean changed, Version version) {
		if (!changed) {
			this.notice.setVisible(false);
			return;
		}
		this.notice.display(NoticeLevel.CAUTION, "Changed since its last save (saved " + OpenBioModelDatabaseInfoPanel.describeSave(version) + ")",
				"This is the record of that save: the changes made since aren't reflected here. Save the model to update it.");
	}

	private static String describeSave(Version version) {
		if (version.getDate() == null) return "an unknown date";
		return ModelListRenderer.describeDate(version.getDate());
	}

	// the database info may need a server round trip if the local cache doesn't have it, so it's fetched off the EDT
	private void lookUpBioModelInfo(Version version) {
		if (this.documentManager == null) return;
		final int generation = this.lookupGeneration;
		final DocumentManager manager = this.documentManager;
		new SwingWorker<BioModelInfo, Void>() {
			@Override
			protected BioModelInfo doInBackground() throws Exception {
				return manager.getBioModelInfo(version.getVersionKey());
			}

			@Override
			protected void done() {
				if (generation != OpenBioModelDatabaseInfoPanel.this.lookupGeneration) return;
				OpenBioModelDatabaseInfoPanel.this.showLookupResult(this);
			}
		}.execute();
	}

	private void showLookupResult(SwingWorker<BioModelInfo, Void> lookup) {
		BioModelInfo bioModelInfo;
		try {
			bioModelInfo = lookup.get();
		} catch (InterruptedException | ExecutionException e) {
			OpenBioModelDatabaseInfoPanel.lg.warn("Couldn't load the Database File Info of the open model", e);
			bioModelInfo = null;
		}
		if (bioModelInfo != null) {
			this.contentsPanel.setBioModelInfo(bioModelInfo);
			return;
		}
		this.contentsPanel.setVisible(false);
		this.notice.display(NoticeLevel.ALERT, "Couldn't load this model's database record",
				"The VCell database didn't return it. Check your connection, or whether the saved model has been deleted.");
	}

	/**
	 * A notice that stands out from the panel around it: a tinted band with a strong left edge, an icon, a bold
	 * headline and an explanation under it. Its colors follow the light or dark theme, recomputed when it changes.
	 */
	static class NoticeBanner extends JPanel {
		private final JLabel iconLabel = new JLabel();
		private final JLabel headlineLabel = new JLabel();
		private final JLabel detailLabel = new JLabel();
		private NoticeLevel level = NoticeLevel.CAUTION;

		NoticeBanner() {
			super(new BorderLayout(10, 2));
			this.setName("DatabaseInfoNotice");
			this.iconLabel.setVerticalAlignment(SwingConstants.TOP);
			JPanel text = new JPanel(new BorderLayout(0, 2));
			text.setOpaque(false);
			text.add(this.headlineLabel, BorderLayout.NORTH);
			text.add(this.detailLabel, BorderLayout.CENTER);
			this.add(this.iconLabel, BorderLayout.WEST);
			this.add(text, BorderLayout.CENTER);
			this.applyColors();
		}

		void display(NoticeLevel level, String headline, String detail) {
			this.level = level;
			this.headlineLabel.setText(headline);
			this.detailLabel.setText("<html>" + detail + "</html>"); // wraps to the panel's width
			this.getAccessibleContext().setAccessibleName(headline + ". " + detail);
			this.applyColors();
			this.setVisible(true); // a notice displayed is seen, whatever was shown (or hidden) before
		}

		@Override
		public void updateUI() {
			super.updateUI();
			if (this.headlineLabel != null) this.applyColors(); // null while the superclass constructor runs
		}

		private void applyColors() {
			boolean alert = this.level == NoticeLevel.ALERT;
			Color accent = alert ? VCellColors.getErrorForeground() : VCellColors.getWarningForeground();
			Color band = alert ? VCellColors.getErrorBackground() : VCellColors.getThemedBackground(new Color(0xFFE08A));
			Icon icon = alert ? VCellIcons.getErrorIcon() : VCellIcons.getWarningIcon();
			this.setOpaque(true);
			this.setBackground(band);
			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
					BorderFactory.createEmptyBorder(8, 10, 8, 10)));
			this.iconLabel.setIcon(icon);
			this.headlineLabel.setFont(this.headlineLabel.getFont().deriveFont(Font.BOLD));
			this.headlineLabel.setForeground(accent);
		}
	}
}
