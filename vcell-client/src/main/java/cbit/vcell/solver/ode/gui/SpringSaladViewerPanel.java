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

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.math.MathDescription;
import cbit.vcell.simdata.SpringSaladTrajectory;
import org.vcell.util.gui.VCFileChooser;
import org.vcell.util.gui.exporter.FileFilters;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
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
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/**
 * Movie player for a {@link SpringSaladTrajectory}: a {@link SpringSaladViewerCanvas} (3D glyph
 * view) with a transport bar (play/pause, frame scrubber, speed, reset view, movie export) and a
 * time/scene readout, plus a sidebar of visibility controls — the scene toggles and the
 * {@link SpringSaladSpeciesPanel} species checklist. See {@code docs/salad-3d-renderer-design.md}
 * Phase 1.
 */
@SuppressWarnings("serial")
public class SpringSaladViewerPanel extends JPanel {

	private final SpringSaladViewerCanvas canvas = new SpringSaladViewerCanvas();
	private final SpringSaladSpeciesPanel speciesPanel = new SpringSaladSpeciesPanel(canvas::setSiteTypeVisible);
	private MathDescription mathDescription;
	private final JSlider frameSlider = new JSlider();
	private final JButton playButton = new JButton("▶"); // ▶
	private final JButton saveMovieButton = new JButton("Save movie…");
	private final JLabel readout = new JLabel(" ");
	private final JComboBox<String> speedCombo = new JComboBox<>(new String[] { "2 fps", "5 fps", "10 fps", "20 fps", "30 fps" });
	private final Timer timer;
	private boolean playing = false;
	private boolean adjustingSlider = false;

	public SpringSaladViewerPanel() {
		super(new BorderLayout());
		// Names, not layout positions, are how the debug bridge and recorded UI scripts
		// address these controls - see tools/debug-bridge. They are inert at runtime.
		setName("SpringSaladViewerPanel");
		canvas.setName("SpringSaladViewerCanvas");
		speciesPanel.setName("SpringSaladSpeciesPanel");
		frameSlider.setName("SpringSaladFrameSlider");
		playButton.setName("SpringSaladPlayButton");
		saveMovieButton.setName("SpringSaladSaveMovieButton");
		speedCombo.setName("SpringSaladSpeedCombo");
		readout.setName("SpringSaladReadout");
		timer = new Timer(100, e -> advanceFrame());
		speedCombo.setSelectedIndex(2); // 10 fps
		applySpeed();

		// Everything that controls WHAT is drawn lives in one column beside the view; the bar along
		// the bottom is purely transport. Keeping the scene toggles out of that bar also stops it
		// overflowing and dropping controls off the end in a narrow window.
		JPanel sceneToggles = new JPanel();
		sceneToggles.setLayout(new BoxLayout(sceneToggles, BoxLayout.Y_AXIS));
		sceneToggles.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 4));
		JLabel sceneTitle = new JLabel("Scene");
		sceneTitle.setFont(sceneTitle.getFont().deriveFont(Font.BOLD));
		sceneTitle.setAlignmentX(LEFT_ALIGNMENT);
		sceneToggles.add(sceneTitle);
		sceneToggles.add(sceneToggle("Links", true, canvas::setShowLinks));
		sceneToggles.add(sceneToggle("Box", true, canvas::setShowBox));
		sceneToggles.add(sceneToggle("Membrane", true, canvas::setShowMembrane));

		JPanel sidebar = new JPanel(new BorderLayout());
		sidebar.add(sceneToggles, BorderLayout.NORTH);
		sidebar.add(speciesPanel, BorderLayout.CENTER);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, sidebar);
		split.setResizeWeight(1.0);   // the canvas takes the extra room when the window grows
		split.setDividerLocation(-1);
		split.setContinuousLayout(true);
		sidebar.setMinimumSize(new Dimension(120, 0));
		sidebar.setPreferredSize(new Dimension(190, 0));
		add(split, BorderLayout.CENTER);

		// The scrubber is the ONLY control allowed to absorb width changes, so it goes in the
		// CENTER and everything else keeps its preferred size at the edges. A FlowLayout here
		// silently wraps the overflow onto a second row that BorderLayout.SOUTH then clips, which
		// hid the movie-export button entirely below about 800px wide.
		JPanel controls = new JPanel(new BorderLayout(6, 0));
		playButton.setToolTipText("Play / Pause");
		playButton.addActionListener(e -> togglePlay());
		controls.add(playButton, BorderLayout.WEST);

		frameSlider.setMinimum(0);
		frameSlider.setValue(0);
		frameSlider.setMinimumSize(new Dimension(40, frameSlider.getMinimumSize().height));
		frameSlider.addChangeListener(e -> {
			if (adjustingSlider) return;
			canvas.setFrameIndex(frameSlider.getValue());
			updateReadout();
		});
		controls.add(frameSlider, BorderLayout.CENTER);

		JPanel transportRight = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		transportRight.add(new JLabel("Speed:"));
		speedCombo.addActionListener(e -> applySpeed());
		transportRight.add(speedCombo);

		JButton reset = new JButton("Reset view");
		reset.setName("SpringSaladResetViewButton");
		reset.addActionListener(e -> canvas.resetView());
		transportRight.add(reset);

		saveMovieButton.setToolTipText("Save the whole trajectory as a movie, in the current view");
		saveMovieButton.addActionListener(e -> saveMovie());
		transportRight.add(saveMovieButton);

		readout.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
		transportRight.add(readout);
		controls.add(transportRight, BorderLayout.EAST);

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
		saveMovieButton.setEnabled(n > 0);
		adjustingSlider = false;
		updateReadout();
	}

	/**
	 * Ask for a destination and write the whole trajectory out as a movie, in the view currently on
	 * screen. Rendering runs off the EDT behind a modal progress dialog — which also keeps the user
	 * from rotating the scene halfway through the export.
	 */
	private void saveMovie() {
		stop();
		if (canvas.getFrameCount() == 0) {
			return;
		}
		VCFileChooser fileChooser = new VCFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MP4);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_GIF);
		fileChooser.setFileFilter(FileFilters.FILE_FILTER_MP4);
		fileChooser.setDialogTitle("Save trajectory movie");
		if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		// the chosen filter picks the format; a typed extension overrides it
		SpringSaladMovieExporter.Format format =
				fileChooser.getFileFilter() == FileFilters.FILE_FILTER_GIF
						? SpringSaladMovieExporter.Format.ANIMATED_GIF
						: SpringSaladMovieExporter.Format.MP4;
		File selectedFile = fileChooser.getSelectedFile();
		String name = selectedFile.getName().toLowerCase(Locale.ROOT);
		if (name.endsWith(".gif")) {
			format = SpringSaladMovieExporter.Format.ANIMATED_GIF;
		} else if (name.endsWith(".mp4")) {
			format = SpringSaladMovieExporter.Format.MP4;
		} else {
			selectedFile = new File(selectedFile.getAbsolutePath() + format.getExtension());
		}
		if (selectedFile.exists()) {
			String overwrite = "Yes";
			String answer = PopupGenerator.showWarningDialog(this,
					"Overwrite existing file:\n" + selectedFile.getAbsolutePath() + "?",
					new String[] { overwrite, "No" }, overwrite);
			if (!overwrite.equals(answer)) {
				return;
			}
		}

		final File movieFile = selectedFile;
		final SpringSaladMovieExporter.Format movieFormat = format;
		final int width = Math.max(320, canvas.getWidth());
		final int height = Math.max(240, canvas.getHeight());
		final int fps = selectedFps();
		AsynchClientTask writeTask = new AsynchClientTask("Saving trajectory movie",
				AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SpringSaladMovieExporter.writeMovie(canvas, movieFile, movieFormat,
						width, height, fps, getClientTaskStatusSupport());
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<>(), new AsynchClientTask[] { writeTask }, true);
	}

	private JCheckBox sceneToggle(String label, boolean initial, java.util.function.Consumer<Boolean> apply) {
		JCheckBox box = new JCheckBox(label, initial);
		box.setName("SpringSaladToggle" + label.replace(" ", ""));
		box.setAlignmentX(LEFT_ALIGNMENT);
		box.addActionListener(e -> apply.accept(box.isSelected()));
		return box;
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
		timer.setDelay(Math.max(1, 1000 / selectedFps()));
	}

	/** The playback rate chosen in the transport bar; movie export uses it too. */
	private int selectedFps() {
		return Integer.parseInt(((String) speedCombo.getSelectedItem()).split(" ")[0]);
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
