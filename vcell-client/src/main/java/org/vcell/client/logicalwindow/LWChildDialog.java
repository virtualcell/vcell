package org.vcell.client.logicalwindow;

import java.awt.Window;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.vcell.client.logicalwindow.LWTraits.InitialPosition;

/**
 * Base class for logical <b>modeless child</b> windows of an {@link LWContainerHandle}.
 * <p>
 * This is the <i>owned-window</i> counterpart to {@link LWChildFrame}. Where {@code LWChildFrame}
 * extends {@link javax.swing.JFrame} — which in AWT is always a top-level root and therefore has
 * <b>no native owner</b> — this class extends {@link JDialog} constructed <b>with the logical
 * parent's window as its native owner</b> and {@link ModalityType#MODELESS} modality.
 * <p>
 * The practical difference is z-order robustness. {@code LWChildFrame} can only be kept above its
 * logical parent by a best-effort {@link Window#toFront()}, which modern macOS (13.3+/Sonoma
 * cooperative activation) and Windows (foreground lock) refuse to honor for a non-foreground
 * application — the "window appears behind its parent" bug. A natively-<b>owned</b> window is kept
 * above its owner <b>by the OS itself</b>, regardless of which application is active, so it does not
 * depend on {@code toFront()} at all.
 * <p>
 * Trade-off (deliberate): an owned window does not get its own taskbar button / independent
 * minimize on Windows, and travels with its owner under Spaces / Stage Manager / Snap / virtual
 * desktops. For a child editor/viewer that logically belongs to a document window, that is the
 * desired behavior.
 * <p>
 * See {@code docs/windowing-design-patterns.md} §7 for the platform analysis motivating this class.
 *
 * @see LWChildFrame the un-owned {@code JFrame} counterpart (fragile z-order)
 */
@SuppressWarnings("serial")
public abstract class LWChildDialog extends JDialog implements LWFrameOrDialog, LWContainerHandle {

	private final LWManager lwManager;
	protected LWTraits traits;

	/**
	 * @param parent logical owner; also becomes the native AWT owner so the OS enforces z-order.
	 *               Null is tolerated (for WindowBuilder / transition), but a non-null parent is the
	 *               whole point — pass one.
	 * @param title  window title (also the default menu description)
	 */
	public LWChildDialog(LWContainerHandle parent, String title) {
		super(parent != null ? parent.getWindow() : null, title, ModalityType.MODELESS);
		lwManager = new LWManager(parent, this);
		traits = new LWTraits(InitialPosition.STAGGERED_ON_PARENT);
		if (parent != null) {
			parent.manage(this);
			LWContainerHandle.stagger(parent.getWindow(), this);
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public LWChildDialog(LWContainerHandle parent) {
		this(parent, null);
	}

	@Override
	public Window getWindow() {
		return this;
	}

	@Override
	public Iterator<LWHandle> iterator() {
		return lwManager.visible();
	}

	@Override
	public void manage(LWHandle child) {
		lwManager.manage(this, child);
	}

	@Override
	public LWModality getLWModality() {
		return LWModality.MODELESS;
	}

	@Override
	public LWContainerHandle getlwParent() {
		return lwManager.getLwParent();
	}

	@Override
	public void closeRecursively() {
		lwManager.closeRecursively();
	}

	@Override
	public void unIconify() {
		// a JDialog cannot be iconified; no-op (mirrors LWDialog)
	}

	@Override
	public JMenuItem menuItem(int level) {
		return LWMenuItemFactory.menuFor(level, this);
	}

	@Override
	public LWTraits getTraits() {
		return traits;
	}

	/**
	 * Default menu description is the window title. Subclasses may override.
	 */
	@Override
	public String menuDescription() {
		String t = getTitle();
		return (t != null) ? t : "";
	}

	@Override
	public Window self() {
		return this;
	}
}
