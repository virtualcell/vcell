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

import cbit.vcell.math.MathDescription;
import cbit.vcell.simdata.SpringSaladTrajectory;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Movie player for a {@link SpringSaladTrajectory}: a {@link SpringSaladViewerCanvas} (3D glyph
 * view) with a transport bar (play/pause, frame scrubber, speed, links toggle, reset view) and a
 * time/scene readout. See {@code docs/salad-3d-renderer-design.md} Phase 1.
 */
@SuppressWarnings("serial")
public class SpringSaladViewerPanel extends JPanel {

	private final SpringSaladViewerCanvas canvas = new SpringSaladViewerCanvas();
	private final SpringSaladSpeciesPanel speciesPanel = new SpringSaladSpeciesPanel(canvas::setSiteTypeVisible);
	private MathDescription mathDescription;
	private final JSlider frameSlider = new JSlider();
	private final JButton playButton = new JButton("▶"); // ▶
	private final JLabel readout = new JLabel(" ");
	private final JComboBox<String> speedCombo = new JComboBox<>(new String[] { "2 fps", "5 fps", "10 fps", "20 fps", "30 fps" });
	private final Timer timer;
	private boolean playing = false;
	private boolean adjustingSlider = false;

	public SpringSaladViewerPanel() {
		super(new BorderLayout());
		timer = new Timer(100, e -> advanceFrame());
		speedCombo.setSelectedIndex(2); // 10 fps
		applySpeed();

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, speciesPanel);
		split.setResizeWeight(1.0);   // the canvas takes the extra room when the window grows
		split.setDividerLocation(-1);
		split.setContinuousLayout(true);
		speciesPanel.setMinimumSize(new Dimension(120, 0));
		speciesPanel.setPreferredSize(new Dimension(190, 0));
		add(split, BorderLayout.CENTER);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
		playButton.setToolTipText("Play / Pause");
		playButton.addActionListener(e -> togglePlay());
		controls.add(playButton);

		frameSlider.setMinimum(0);
		frameSlider.setValue(0);
		frameSlider.addChangeListener(e -> {
			if (adjustingSlider) return;
			canvas.setFrameIndex(frameSlider.getValue());
			updateReadout();
		});
		controls.add(frameSlider);

		controls.add(new JLabel("Speed:"));
		speedCombo.addActionListener(e -> applySpeed());
		controls.add(speedCombo);

		JCheckBox links = new JCheckBox("Links", true);
		links.addActionListener(e -> canvas.setShowLinks(links.isSelected()));
		controls.add(links);

		JCheckBox box = new JCheckBox("Box", true);
		box.addActionListener(e -> canvas.setShowBox(box.isSelected()));
		controls.add(box);

		JCheckBox membrane = new JCheckBox("Membrane", true);
		membrane.addActionListener(e -> canvas.setShowMembrane(membrane.isSelected()));
		controls.add(membrane);

		JButton reset = new JButton("Reset view");
		reset.addActionListener(e -> canvas.resetView());
		controls.add(reset);

		controls.add(Box.createHorizontalStrut(8));
		controls.add(readout);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		bottom.add(controls, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		setTrajectory(null);
	}

	/**
	 * Supply the math description of the run, which names the species and site types in the
	 * selector. Optional — without it the selector falls back to color-based labels. Set this
	 * before {@link #setTrajectory}, or call it again afterwards to relabel.
	 */
	public void setMathDescription(MathDescription mathDescription) {
		this.mathDescription = mathDescription;
		speciesPanel.setLegend(SpringSaladSpeciesLegend.build(canvas.getTrajectory(), mathDescription));
	}

	/** Load a trajectory (null clears the view). Resets to the first frame and stops playback. */
	public void setTrajectory(SpringSaladTrajectory t) {
		stop();
		canvas.setTrajectory(t);
		canvas.showAllSiteTypes();
		speciesPanel.setLegend(SpringSaladSpeciesLegend.build(t, mathDescription));
		int n = canvas.getFrameCount();
		adjustingSlider = true;
		frameSlider.setMinimum(0);
		frameSlider.setMaximum(Math.max(0, n - 1));
		frameSlider.setValue(0);
		frameSlider.setEnabled(n > 1);
		playButton.setEnabled(n > 1);
		adjustingSlider = false;
		updateReadout();
	}

	private void togglePlay() {
		if (playing) stop(); else start();
	}

	private void start() {
		if (canvas.getFrameCount() <= 1) return;
		playing = true;
		playButton.setText("❙❙"); // ❙❙ pause
		timer.start();
	}

	private void stop() {
		playing = false;
		playButton.setText("▶");
		timer.stop();
	}

	private void advanceFrame() {
		int n = canvas.getFrameCount();
		if (n == 0) { stop(); return; }
		int next = (canvas.getFrameIndex() + 1) % n; // loop
		canvas.setFrameIndex(next);
		adjustingSlider = true;
		frameSlider.setValue(next);
		adjustingSlider = false;
		updateReadout();
	}

	private void applySpeed() {
		int fps = Integer.parseInt(((String) speedCombo.getSelectedItem()).split(" ")[0]);
		timer.setDelay(Math.max(1, 1000 / fps));
	}

	private void updateReadout() {
		SpringSaladTrajectory t = canvas.getTrajectory();
		int n = canvas.getFrameCount();
		if (t == null || n == 0) { readout.setText("No trajectory data"); return; }
		int i = canvas.getFrameIndex();
		double time = t.getFrames().get(i).getTime();
		readout.setText(String.format("frame %d / %d    t = %.4g", i + 1, n, time));
	}

	// ---- standalone demo with a synthetic trajectory ----
	public static void main(String[] args) {
		SpringSaladTrajectory demo = makeDemoTrajectory();
		javax.swing.SwingUtilities.invokeLater(() -> {
			javax.swing.JFrame frame = new javax.swing.JFrame("SpringSaLaD Viewer (demo)");
			frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			SpringSaladViewerPanel panel = new SpringSaladViewerPanel();
			panel.setTrajectory(demo);
			frame.setContentPane(panel);
			frame.setSize(720, 640);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

	static SpringSaladTrajectory makeDemoTrajectory() {
		int nSites = 60, nFrames = 120;
		// spread across the SpringSaLaD palette, including the colors an earlier hand-written
		// subset in the canvas used to render as gray
		String[] palette = { "RED", "LIME", "BLUE", "YELLOW", "ORANGE", "CYAN",
				"LIME_GREEN", "PURPLE", "TEAL", "GOLD", "CRIMSON", "SLATE_BLUE" };
		// deterministic pseudo-random initial positions/velocities (no Math.random for reproducibility)
		double[] px = new double[nSites], py = new double[nSites], pz = new double[nSites];
		double[] vx = new double[nSites], vy = new double[nSites], vz = new double[nSites];
		double[] rad = new double[nSites];
		String[] col = new String[nSites];
		for (int i = 0; i < nSites; i++) {
			double a = i * 2.399963, b = i * 0.7 + 1;
			px[i] = 30 * Math.cos(a); py[i] = 30 * Math.sin(a * 1.3); pz[i] = 20 * Math.sin(b);
			vx[i] = Math.sin(a * 2.1); vy[i] = Math.cos(b * 1.7); vz[i] = Math.sin(a + b);
			rad[i] = 1.5 + 1.5 * ((i * 37) % 5) / 4.0;
			col[i] = palette[i % palette.length];
		}
		List<SpringSaladTrajectory.Frame> frames = new ArrayList<>();
		for (int f = 0; f < nFrames; f++) {
			double t = f * 1e-4;
			List<SpringSaladTrajectory.Site> sites = new ArrayList<>();
			for (int i = 0; i < nSites; i++) {
				double x = px[i] + 8 * Math.sin(0.06 * f + i) * vx[i];
				double y = py[i] + 8 * Math.cos(0.05 * f + i) * vy[i];
				double z = pz[i] + 8 * Math.sin(0.04 * f + i * 0.5) * vz[i];
				sites.add(new SpringSaladTrajectory.Site(i, rad[i], col[i], x, y, z));
			}
			List<int[]> links = new ArrayList<>();
			for (int i = 0; i + 1 < nSites; i += 2) links.add(new int[] { i, i + 1 });
			frames.add(new SpringSaladTrajectory.Frame(f, t, sites, links));
		}
		return new SpringSaladTrajectory(nFrames * 1e-4, 1e-4, 80, 80, 40, 40, frames);
	}
}
