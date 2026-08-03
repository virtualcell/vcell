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

import GIFUtils.GIFImage;
import GIFUtils.GIFOutputStream;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Writes a {@link SpringSaladViewerCanvas}'s trajectory out as a movie, in the view the user has
 * set up — same rotation, zoom, species selection and box/membrane/link toggles.
 * <p>
 * MP4 is encoded with JCodec, a pure-Java H.264 encoder, so no native codec has to be installed.
 * Animated GIF reuses the in-tree {@code GIFUtils} writer and is capped at 256 colors, which shows
 * as banding on the shaded spheres; MP4 is the better choice for anything but a quick embed.
 */
public class SpringSaladMovieExporter {

	/** Output formats offered by the export dialog. */
	public enum Format {
		MP4(".mp4", "MP4 video (*.mp4)"),
		ANIMATED_GIF(".gif", "Animated GIF (*.gif)");

		private final String extension;
		private final String description;

		Format(String extension, String description) {
			this.extension = extension;
			this.description = description;
		}

		public String getExtension() { return extension; }
		public String getDescription() { return description; }
	}

	/** H.264 codes in 16x16 macroblocks; frame sizes are rounded down to a whole number of them. */
	public static final int MACROBLOCK = 16;

	private SpringSaladMovieExporter() {
	}

	/**
	 * Render every frame of the canvas's trajectory and write it to {@code file}.
	 * <p>
	 * Call this off the EDT — encoding hundreds of frames takes seconds to minutes. It does not
	 * mutate the canvas, but it does read its view state, so the caller must keep the user from
	 * changing the view while it runs (the export runs under a modal progress dialog).
	 *
	 * @param canvas   the canvas to render from
	 * @param file     the destination file
	 * @param format   the container/codec to write
	 * @param width    frame width in pixels (rounded down to a multiple of 16, see {@link #MACROBLOCK})
	 * @param height   frame height in pixels (rounded down to a multiple of 16)
	 * @param fps      playback rate
	 * @param progress optional progress sink; also polled for cancellation
	 * @throws UserCancelException if the user cancelled through {@code progress}
	 */
	public static void writeMovie(SpringSaladViewerCanvas canvas, File file, Format format,
								  int width, int height, int fps, ClientTaskStatusSupport progress)
			throws IOException, UserCancelException {
		int frameCount = canvas.getFrameCount();
		if (frameCount == 0) {
			throw new IOException("no trajectory frames to export");
		}
		// Encode at a whole number of macroblocks. H.264 can crop a partial one, but then the frame
		// the decoder holds is larger than the frame the container advertises, and players that
		// ignore the crop show padding at the edges. Rounding down sidesteps it for at most 15px.
		int w = Math.max(MACROBLOCK, width - (width % MACROBLOCK));
		int h = Math.max(MACROBLOCK, height - (height % MACROBLOCK));

		if (format == Format.MP4) {
			writeMp4(canvas, file, w, h, fps, frameCount, progress);
		} else {
			writeAnimatedGif(canvas, file, w, h, fps, frameCount, progress);
		}
	}

	private static void writeMp4(SpringSaladViewerCanvas canvas, File file, int w, int h, int fps,
								 int frameCount, ClientTaskStatusSupport progress)
			throws IOException, UserCancelException {
		AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(file, fps);
		boolean complete = false;
		try {
			for (int i = 0; i < frameCount; i++) {
				checkCancelled(progress, file);
				report(progress, i, frameCount);
				encoder.encodeImage(canvas.renderFrameToImage(i, w, h));
			}
			encoder.finish();
			complete = true;
		} finally {
			if (!complete) {
				// finish() writes the MP4 index; without it the file is unplayable, so drop it
				deleteQuietly(file);
			}
		}
	}

	private static void writeAnimatedGif(SpringSaladViewerCanvas canvas, File file, int w, int h, int fps,
										 int frameCount, ClientTaskStatusSupport progress)
			throws IOException, UserCancelException {
		int delayCentiseconds = Math.max(1, Math.round(100f / fps));
		GIFImage gif = null;
		boolean complete = false;
		try {
			for (int i = 0; i < frameCount; i++) {
				checkCancelled(progress, file);
				report(progress, i, frameCount);
				// GIF allows only 256 colors per frame; let Java2D quantize and dither for us
				BufferedImage indexed = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
				Graphics g = indexed.getGraphics();
				g.drawImage(canvas.renderFrameToImage(i, w, h), 0, 0, null);
				g.dispose();
				int[] pixels = indexed.getRGB(0, 0, w, h, null, 0, w);
				if (gif == null) {
					gif = new GIFImage(pixels, w);
					gif.setDelay(delayCentiseconds);
				} else {
					gif.addImage(pixels, w, true);
					gif.setDelay(gif.countImages() - 1, delayCentiseconds);
				}
			}
			gif.setIterationCount(0); // loop forever
			try (GIFOutputStream out = new GIFOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
				gif.write(out);
			}
			complete = true;
		} finally {
			if (!complete) {
				deleteQuietly(file);
			}
		}
	}

	private static void checkCancelled(ClientTaskStatusSupport progress, File file) throws UserCancelException {
		if (progress != null && progress.isInterrupted()) {
			throw UserCancelException.CANCEL_GENERIC;
		}
	}

	private static void report(ClientTaskStatusSupport progress, int frame, int frameCount) {
		if (progress != null) {
			progress.setProgress(100 * frame / frameCount);
			progress.setMessage(String.format(Locale.ROOT, "Rendering frame %d of %d", frame + 1, frameCount));
		}
	}

	private static void deleteQuietly(File file) {
		if (file.exists() && !file.delete()) {
			file.deleteOnExit();
		}
	}
}
