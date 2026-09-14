/*
 * Copyright (C) 1999-2026 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package cbit.vcell.solver.ode.gui;

import cbit.vcell.simdata.SpringSaladTrajectory;
import cbit.vcell.solver.ode.gui.SpringSaladSpeciesLegend.SiteType;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Show/hide checklist for the site types in a {@link SpringSaladTrajectory}, each decorated with a
 * shaded ball in that type's own color. Entries are grouped under their species (molecule) when the
 * model could name them; see {@link SpringSaladSpeciesLegend} for how names are resolved.
 */
@SuppressWarnings("serial")
public class SpringSaladSpeciesPanel extends JPanel {

	private static final int BALL_SIZE = 14;

	private final JPanel list = new JPanel();
	private final BiConsumer<String, Boolean> onVisibilityChanged;

	/** Site-type key -> its checkbox, in display order. */
	private final Map<String, JCheckBox> boxesByKey = new LinkedHashMap<>();
	/** Species header checkbox -> the site types it governs. */
	private final Map<JCheckBox, List<SiteType>> headerChildren = new LinkedHashMap<>();

	/**
	 * @param onVisibilityChanged called with (site-type key, visible) whenever an entry is toggled
	 *                            — normally {@code canvas::setSiteTypeVisible}
	 */
	public SpringSaladSpeciesPanel(BiConsumer<String, Boolean> onVisibilityChanged) {
		super(new BorderLayout());
		this.onVisibilityChanged = onVisibilityChanged;
		setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 4));

		JLabel title = new JLabel("Species");
		title.setFont(title.getFont().deriveFont(Font.BOLD));
		title.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		add(title, BorderLayout.NORTH);

		list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
		JScrollPane scroll = new JScrollPane(list,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.getVerticalScrollBar().setUnitIncrement(12);
		add(scroll, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
		JButton all = new JButton("All");
		all.setName("SpringSaladSpeciesAllButton");
		all.addActionListener(e -> setAll(true));
		JButton none = new JButton("None");
		none.setName("SpringSaladSpeciesNoneButton");
		none.addActionListener(e -> setAll(false));
		buttons.add(all);
		buttons.add(none);
		add(buttons, BorderLayout.SOUTH);
	}

	/** Rebuild the checklist for a legend; a null or empty legend leaves the panel blank. */
	public void setLegend(SpringSaladSpeciesLegend legend) {
		list.removeAll();
		boxesByKey.clear();
		headerChildren.clear();
		if (legend != null && !legend.isEmpty()) {
			Map<String, List<SiteType>> bySpecies = legend.bySpecies();
			// Without model names every entry lands in one "Unidentified" bucket; a lone header
			// would be noise, so in that case the site types are listed flat.
			boolean grouped = legend.isNamed() && bySpecies.size() > 1;
			for (Map.Entry<String, List<SiteType>> species : bySpecies.entrySet()) {
				if (grouped) {
					list.add(speciesHeader(species.getKey(), species.getValue()));
				}
				for (SiteType siteType : species.getValue()) {
					list.add(siteRow(siteType, grouped));
				}
			}
		}
		list.add(Box.createVerticalGlue());
		list.revalidate();
		list.repaint();
	}

	/** A species header whose checkbox toggles all of that species' site types at once. */
	private Component speciesHeader(String speciesName, List<SiteType> members) {
		JCheckBox box = new JCheckBox(speciesName, true);
		// Named per species, so a script can address one species rather than a row index
		box.setName("SpringSaladSpeciesHeader_" + speciesName);
		box.setFont(box.getFont().deriveFont(Font.BOLD));
		box.setAlignmentX(LEFT_ALIGNMENT);
		box.addActionListener(e -> {
			for (SiteType siteType : members) {
				JCheckBox child = boxesByKey.get(siteType.getKey());
				if (child != null) {
					child.setSelected(box.isSelected());
				}
				onVisibilityChanged.accept(siteType.getKey(), box.isSelected());
			}
		});
		headerChildren.put(box, members);
		return box;
	}

	private Component siteRow(SiteType siteType, boolean indented) {
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		row.setAlignmentX(LEFT_ALIGNMENT);
		if (indented) {
			row.add(Box.createHorizontalStrut(16));
		}
		String tip = String.format("%s, radius %.4g (%d sites)",
				siteType.getColorName(), siteType.getRadius(), siteType.getSiteCount());
		JCheckBox box = new JCheckBox("", true);
		// The label beside this box is a separate JLabel, so the checkbox itself has no
		// text at all; its site-type key is the only durable way to address it.
		box.setName("SpringSaladSite_" + siteType.getKey());
		box.setToolTipText(tip);
		box.addActionListener(e -> {
			onVisibilityChanged.accept(siteType.getKey(), box.isSelected());
			refreshHeaders();
		});
		boxesByKey.put(siteType.getKey(), box);
		row.add(box);
		row.add(new JLabel(SpringSaladViewerCanvas.ballIcon(siteType.getColor(), BALL_SIZE)));
		row.add(Box.createHorizontalStrut(4));
		JLabel name = new JLabel(indented ? siteType.getSiteLabel() : siteType.getLabel());
		name.setToolTipText(tip);
		row.add(name);
		row.add(Box.createHorizontalGlue());
		// keep rows at their natural height rather than stretching to fill the BoxLayout
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
		return row;
	}

	/** A species header stays checked only while all of its site types are shown. */
	private void refreshHeaders() {
		for (Map.Entry<JCheckBox, List<SiteType>> header : headerChildren.entrySet()) {
			boolean allOn = true;
			for (SiteType siteType : header.getValue()) {
				JCheckBox child = boxesByKey.get(siteType.getKey());
				allOn &= child == null || child.isSelected();
			}
			header.getKey().setSelected(allOn);
		}
	}

	private void setAll(boolean visible) {
		for (Map.Entry<String, JCheckBox> entry : new ArrayList<>(boxesByKey.entrySet())) {
			entry.getValue().setSelected(visible);
			onVisibilityChanged.accept(entry.getKey(), visible);
		}
		refreshHeaders();
	}
}
