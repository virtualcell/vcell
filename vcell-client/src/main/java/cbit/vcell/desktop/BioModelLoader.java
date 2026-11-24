package cbit.vcell.desktop;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;

/**
 * Loads a saved BioModel whole, for the reaction diagram in {@link ModelExplorerDetailsPanel}: fetched from the server
 * and read off the event thread (a large model takes seconds), through whichever document manager is logged in. The
 * last few loaded are kept, so stepping back and forth among them is instant.
 */
public class BioModelLoader implements ModelExplorerDetailsPanel.ModelLoader {
	private static final Logger lg = LogManager.getLogger(BioModelLoader.class);
	private static final int MODELS_KEPT = 4;

	private final Supplier<DocumentManager> documentManager;
	private final Map<String, BioModel> loadedModels = new LinkedHashMap<>(8, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, BioModel> eldest) {
			return this.size() > MODELS_KEPT;
		}
	};

	/** Loads through the document manager logged in when asked (null: no one, and nothing loads). */
	public BioModelLoader(Supplier<DocumentManager> documentManager) {
		this.documentManager = documentManager;
	}

	@Override
	public void load(BioModelInfo info, Consumer<BioModel> done, Consumer<String> failed) {
		String key = String.valueOf(info.getVersion().getVersionKey());
		BioModel loaded = this.loadedModels.get(key);
		if (null != loaded) {
			done.accept(loaded);
			return;
		}
		DocumentManager documentManager = this.documentManager.get();
		if (null == documentManager) {
			failed.accept("not logged in");
			return;
		}
		new SwingWorker<BioModel, Void>() {
			@Override
			protected BioModel doInBackground() throws Exception {
				return documentManager.getBioModel(info);
			}

			@Override
			protected void done() {
				try {
					BioModel bioModel = this.get();
					BioModelLoader.this.loadedModels.put(key, bioModel);
					done.accept(bioModel);
				} catch (ExecutionException e) {
					lg.warn("Could not load " + info.getVersion().getName() + " for its reaction diagram", e.getCause());
					failed.accept(null == e.getCause() || null == e.getCause().getMessage() ? "it couldn't be read" : e.getCause().getMessage());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}.execute();
	}

	/** A guest can't load a saved model whole, so no reaction diagram: none is tried. */
	@Override
	public String unavailable() {
		DocumentManager documentManager = this.documentManager.get();
		if (null == documentManager || null == documentManager.getUser()) return null;
		if (!User.isGuest(documentManager.getUser().getName())) return null;
		return "Reaction diagrams can't be loaded as a guest. Log in with an account to see them.";
	}

	/** Forgets the models kept: a new login may not see them. */
	public void clear() {
		this.loadedModels.clear();
	}
}
