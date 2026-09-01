package cbit.vcell.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.client.logicalwindow.LWChildDialog;
import org.vcell.client.logicalwindow.LWChildFrame;
import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWFrameOrDialog;
import org.vcell.client.logicalwindow.LWHandle;
import org.vcell.client.logicalwindow.LWHandle.LWModality;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.client.logicalwindow.LWTitledDialog;
import org.vcell.client.logicalwindow.LWTraits;
import org.vcell.client.logicalwindow.LWTraits.InitialPosition;
import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.ProgrammingException;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.desktop.TopLevelWindow;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.title.TitleChanger;
import cbit.vcell.solver.TempSimulation;
//import cbit.vcell.client.desktop.biomodel.ChildWindowListener;
import edu.uchc.connjur.wb.ExecutionTrace;



public class ChildWindowManager {
	private final ArrayList<ChildWindow> childWindows = new ArrayList<ChildWindow>();
	
	private JFrame parent = null;
	private LWContainerHandle owner = null; 
	private static final Logger LG = LogManager.getLogger(ChildWindowManager.class);
	private interface ManagedChild {
		ChildWindowManager getChildWindowManager(); 
	}
	
	/**
	 * issue: window z-order. Modeless child windows are now OWNED windows ({@link LWChildDialog},
	 * an owned modeless JDialog) rather than un-owned {@link LWChildFrame}s. An un-owned JFrame could
	 * only be kept above its parent by a best-effort {@code Window.toFront()}, which modern macOS
	 * (13.3+/Sonoma cooperative activation) and Windows (foreground lock) refuse for a non-foreground
	 * app — the "child window hides behind the document window" bug. A natively-owned window is kept
	 * above its owner by the OS itself. Trade-off (accepted): no independent taskbar button; the child
	 * travels with its owner. Mirrors the already-owned {@link ParentModalChild} (modal) sibling.
	 * See {@code docs/windowing-design-patterns.md} §7.
	 */
	@SuppressWarnings("serial")
	private static class ModelessChild extends LWChildDialog implements ManagedChild {
		private final ChildWindowManager childWindowManager;
		private ModelessChild(ChildWindowManager cwm,LWContainerHandle parent, String title, LWTraits tr) throws HeadlessException {
			super(parent, title);
			Objects.requireNonNull(cwm);
			childWindowManager = cwm;
			traits = tr;
		}

		@Override
		public String menuDescription() {
			return getTitle( );
		}
		
		public ChildWindowManager getChildWindowManager() {
			return childWindowManager; 
		}
	}
	/**
	 * The DETACHED counterpart of {@link ModelessChild}: an un-owned {@link LWChildFrame}.
	 *
	 * Owning a child window buys OS-guaranteed z-order (see ModelessChild), but the price is
	 * paid by the user: a Dialog has no taskbar/dock button and, not being a Frame, has no
	 * iconified state at all, so it cannot be minimised; and because the OS keeps it above its
	 * owner, on a small screen it can cover the document window with no way to raise that
	 * window above it. Detaching trades the z-order guarantee back for an ordinary top-level
	 * window the user can minimise, stack and arrange freely.
	 *
	 * Still a logical-window child, so it keeps its place in the Window menu and in
	 * {@link ChildWindowManager#findChildWindowManager(Component)} - only the NATIVE ownership
	 * is given up, not the logical parentage.
	 */
	@SuppressWarnings("serial")
	private static class DetachedModelessChild extends LWChildFrame implements ManagedChild {
		private final ChildWindowManager childWindowManager;
		private DetachedModelessChild(ChildWindowManager cwm, LWContainerHandle parent, String title, LWTraits tr) throws HeadlessException {
			super(parent, title);
			Objects.requireNonNull(cwm);
			childWindowManager = cwm;
			traits = tr;
		}

		@Override
		public String menuDescription() {
			return getTitle();
		}

		@Override
		public ChildWindowManager getChildWindowManager() {
			return childWindowManager;
		}
	}

	@SuppressWarnings("serial")
	private static class ParentModalChild extends LWTitledDialog implements ManagedChild {
		private final ChildWindowManager childWindowManager;

		public ParentModalChild(ChildWindowManager cwm, LWContainerHandle parent, String title, LWTraits tr) {
			super(parent, title);
			Objects.requireNonNull(cwm);
			childWindowManager = cwm;
			traits = tr; 
		}

		@Override
		public ChildWindowManager getChildWindowManager() {
			return childWindowManager; 
		}
		
	}
	
	/**
	 * @param title not null
	 * @param modality not null
	 * @return implementing class
	 */
	private LWFrameOrDialog createContainerImplementation(String title,LWModality modality, boolean parentCentered, boolean detached) {
		LWTraits traits = parentCentered ? new LWTraits(InitialPosition.CENTERED_ON_PARENT) : new LWTraits(InitialPosition.STAGGERED_ON_PARENT);
		if (owner == null) {
			// every ChildWindowManager host is an LWTopFrame, so findLWOwner(parent) always resolves an
			// owner. A null owner would mean a non-LW host was introduced — fail loudly rather than fall
			// back to an un-owned dialog (the retired JDiagAdapter transition path).
			throw new IllegalStateException("ChildWindowManager has no logical-window owner for parent " + parent
					+ "; the host frame must extend LWTopFrame");
		}
		switch (modality) {
		case MODELESS:
			// detach is only meaningful for modeless windows; a parent-modal window that the
			// user could send behind its parent would be a trap (unreachable modal blocker).
			return detached ? new DetachedModelessChild(this,owner, title,traits)
							: new ModelessChild(this,owner, title,traits);
		case PARENT_ONLY:
			return new ParentModalChild(this,owner, title,traits);
		}
		throw new UnsupportedOperationException("Modality " + modality + " not supported");
	}

	public class ChildWindow {
		
		private WindowListener windowListener = new WindowAdapter(){
//			public void windowActivated(WindowEvent e) {}
//			public void windowClosed(WindowEvent e) { }
			public void windowClosing(WindowEvent e) {
				for (ChildWindowListener listener : listeners){
					listener.closing(ChildWindow.this);
				}
				if (ChildWindow.this != null) {
					closeChildWindow(ChildWindow.this);
				}
			}
//			public void windowDeactivated(WindowEvent e) {}
//			public void windowDeiconified(WindowEvent e) {}
//			public void windowIconified(WindowEvent e) {}
//			public void windowOpened(WindowEvent e) {}
		};

		
		private Container contentPane;
		private Object contextObject;
		private LWFrameOrDialog impl;
		
		private String title = null;
		private Dimension preferredSize = null;
		private Boolean resizable = null;
		private Boolean pack = null;
		private Dimension size = null;
		private Boolean isCenteredOnParent = true;
		private boolean detached = false;
		/** bounds carried across a detach/reattach, which must rebuild the window */
		private Rectangle rememberedBounds = null;
		private LWModality shownModality = null;
		
		
		private ArrayList<ChildWindowListener> listeners = new ArrayList<ChildWindowListener>();
	
		private ChildWindow(Container contentPane, Object aContextObject, String title) {
			this.contentPane = contentPane;
			this.contextObject = aContextObject;
			this.title = title;
		}
		
		public void setIsCenteredOnParent(){
			if (impl==null){
				isCenteredOnParent = true;
				return;
			}
			throw new IllegalStateException("must be called before show( )");
		}
		
		public void addChildWindowListener(ChildWindowListener childWindowListener) {
			listeners.add(childWindowListener);
		}
		
		public void close(){
			closeChildWindow(this);
		}
		
		private void dispose(){			
			if (impl != null){
				impl.setVisible(false);
				if (LG.isDebugEnabled()) {
					LG.debug(impl.getTitle() + " sizes\n" + GuiUtils.getPreferredSizes(impl.self()));
				}
				impl.dispose();
				impl = null;
			} else {
				//DebugUtils.stop("ChildWindowManager.ChildWindow.dispose(): I was just asked to displose of a null JDialog ");
			}
		}
		
		private Container getContentPane() {
			return this.contentPane;
		}
		
		private Object getContextObject() {
			return contextObject;
		}
				
		public JFrame getParent() {
			return parent;
		}
		
		boolean isShowing() {
			if (impl!=null){
				return impl.isShowing();
			}else{
				return false;
			}
		}
		
		public void setPreferredSize(Dimension preferredSize){
			this.preferredSize = preferredSize;
			if (impl!=null){
				impl.setPreferredSize(preferredSize);
			}
		}
		
		void setResizable(boolean resizable){
			this.resizable = resizable;
			if (impl!=null){
				impl.setResizable(resizable);
			}
		}

		public void pack(){
			this.pack = true;
			if (impl!=null){
				impl.pack();
			}
		}
		
	
		/**
		 * @Deprecated -- use layout manager
		 * @param i
		 * @param j
		 */
		@Deprecated
		public void setSize(int i, int j) {
			this.size = new Dimension(i,j);
			if (impl!=null){
				impl.setSize(i,j);
			}
		}
		
		/**
		 * @deprecated -- use layout manager
		 * @param dim
		 */
		@Deprecated
		public void setSize(Dimension dim){
			this.size = dim;
			if (impl!=null){
				impl.setSize(dim);
			}
		}
		public void setTitle(String title) {
			this.title = title;
			if (impl!=null){
				impl.setTitle(title);
			}
		}
		public String getTitle(){
			return this.title;
		}
		/**
		 * show with specified modality 
		 * @throws ProgrammingException if previously shown with different modality 
		 */
		public void show(LWModality modality) {
			if (impl != null)  {
				if (impl.getLWModality() != modality) {
					throw new ProgrammingException("Requested modality " + modality + " is different from previous " + impl.getLWModality());
				}
				impl.setVisible(true);
				return;
			}
			if (LG.isDebugEnabled()) {
				LG.debug(ExecutionTrace.justClassName(ChildWindowManager.this) + " making a child window.  My parent is a "+ this.getParent().getName());
			}	
			shownModality = modality;
			impl = createContainerImplementation(title,modality,isCenteredOnParent,detached);
			impl.addWindowListener(windowListener);
			{ //assemble pieces
				Container cp = impl.getContentPane();
				cp.setLayout(new BorderLayout());
				JMenuBar mb = LWNamespace.createRightSideIconMenuBar();
				if (modality == LWModality.MODELESS) {
					mb.add(createAttachmentMenuItem());
				}
				cp.add(mb,BorderLayout.NORTH);
				cp.add(contentPane, BorderLayout.CENTER);
			}

			impl.setAlwaysOnTop(false);
			if (preferredSize != null){
				impl.setPreferredSize(preferredSize);
			}
			if (pack!=null && pack){
				impl.pack();
				if (LG.isDebugEnabled()) {
					
				}
			}

			if (resizable != null){
				impl.setResizable(resizable);
			}
			if (size != null){
				impl.setSize(size);
			}

			if (rememberedBounds != null) {
				// coming back from a detach/reattach: keep the window exactly where the user
				// had it rather than re-centring it on the parent.
				impl.self().setBounds(rememberedBounds);
				rememberedBounds = null;
			} else if (isCenteredOnParent != null) {
				impl.setLocationRelativeTo(impl.getParent());
			}
			impl.toFront();
			impl.setVisible(true);
			
			return;
		}

		/**
		 * show as {@link LWHandle.LWModality#MODELESS}
		 * @throws ProgrammingException if {@link #showModal()} previously called
		 */
		public void show(){
			show(LWModality.MODELESS);
		}
		public void hide(){
			impl.setVisible(false);
		}
		/**
		 * show as {@link LWHandle.LWModality#PARENT_ONLY}
		 * @throws ProgrammingException if {@link #show()} previously called
		 */
		public void showModal() {
			show(LWModality.PARENT_ONLY);
		}

		public boolean isDetached() {
			return detached;
		}

		/**
		 * Detach this window from its document window, or re-attach it.
		 *
		 * A window's native owner is fixed when it is created, so this cannot be flipped in
		 * place - the window is rebuilt. That is cheap here only because ChildWindow already
		 * keeps the caller's contentPane separate from the window it is currently sitting in,
		 * and rebuilds that window on every show(). The content pane, and therefore all viewer
		 * state, is carried across untouched; the user sees the window's frame change, not its
		 * contents reload.
		 *
		 * Position and size are carried across too, so a detach does not move the window out
		 * from under the mouse.
		 */
		public void setDetached(boolean bDetached) {
			if (detached == bDetached) {
				return;
			}
			if (shownModality != null && shownModality != LWModality.MODELESS) {
				throw new IllegalStateException("only a MODELESS child window can be detached");
			}
			detached = bDetached;
			if (impl == null) {
				return;         // not realized yet; the flag alone is enough
			}
			boolean wasVisible = impl.isVisible();
			rememberedBounds = impl.self().getBounds();
			// take the content pane back BEFORE disposing, so it is never disposed with the window
			impl.getContentPane().remove(contentPane);
			dispose();
			if (wasVisible) {
				show(shownModality == null ? LWModality.MODELESS : shownModality);
			}
		}

		private JMenuItem createAttachmentMenuItem() {
			final JMenuItem item = new JMenuItem();
			item.setText(detached ? "Reattach Window" : "Detach Window");
			// a JMenuItem in a JMenuBar is stretched to fill the bar by the bar's BoxLayout,
			// which would turn every bit of empty menu-bar space into a detach button.
			item.setMaximumSize(item.getPreferredSize());
			item.setToolTipText(detached
					? "Keep this window in front of its document window again"
					: "Let this window be minimized and arranged freely, at the cost of it no "
							+ "longer being kept in front of its document window");
			item.addActionListener(e -> setDetached(!detached));
			return item;
		}

		public void toFront() {
			if(impl != null){
				if (LG.isDebugEnabled()) {
					LG.debug(impl.getTitle() + " toFront( )");
				}
				impl.toFront();
			}
			else { 
				LG.debug(" toFront( ) called on unrealized window");
			}
		}
		
		public void logFocusLoss( ) {
			if(impl != null){
					
				
			}
			
		}
	};   
	
	/**
	 * @param component not null 
	 * @return ChildWindowManager
	 * @throws ProgrammingException if unable to find ChildWindowManager
	 */
	public static ChildWindowManager findChildWindowManager(Component component){
		ManagedChild mc = LWNamespace.findOwnerOfType(ManagedChild.class, component);
		if (mc != null) {
			return mc.getChildWindowManager( );
		}
		if (LG.isDebugEnabled()) {
			LG.debug(ExecutionTrace.justClassName(component) + " does not have ManagedChild parent");
		}
		
		TopLevelWindow dw = LWNamespace.findOwnerOfType(TopLevelWindow.class, component);
		if (dw != null) {
			return dw.getChildWindowManager();
		}
		
		throw new ProgrammingException("ChildWindowManager.findChildWindowManager(Component) could not find a ChildWindowManager for component: "+component.getName()+" which is a "+component.getClass().getCanonicalName());
	}
	
	public ChildWindowManager(JFrame parent){
		this.parent = parent;
		owner = LWNamespace.findLWOwner(parent);
		GeneralGuiUtils.addCloseWindowKeyboardAction(this.parent.getRootPane());
	}
	
	public ChildWindow addChildWindow(Container contentPane, Object contextObject, String title){
		if (getChildWindowFromContentPane(contentPane)!=null){
			throw new IllegalArgumentException("child window with content pane already exists");
		}
		if (getChildWindowFromContext(contextObject)!=null){
			throw new IllegalArgumentException("child window with contextobject "+contextObject+" already exists, title='"+title+"'");
		}
		
		ChildWindow childWindow = new ChildWindow(contentPane, contextObject, title);
		childWindows.add(childWindow);
		return childWindow;
	}
	
	public ChildWindow addChildWindow(Container contentPane, TitleChanger titleChanger){
		ChildWindow cw = addChildWindow(contentPane, titleChanger, titleChanger.getTitle( ));
		titleChanger.addTitleListener( titleEvent -> cw.setTitle( titleEvent.getTitle() ) );
		return cw;
	}
	
	
	public ChildWindow addChildWindow(Container contentPane, Object contextObject, String title, boolean resizable){
		ChildWindow childWindow = addChildWindow(contentPane, contextObject, title);
		childWindow.setResizable(resizable);
		return childWindow;
	}
	
	public void closeAllChildWindows(){
		for (ChildWindow c : childWindows) {
			c.dispose();
		}
		childWindows.clear( );
	}
	
	public void closeChildWindow(ChildWindow childWindow){
		if(childWindow != null) {
			childWindow.dispose();
			childWindows.remove(childWindow);
		}
	}

	
	public ChildWindow[] getAllChildWindows(){
		return childWindows.toArray(new ChildWindow[childWindows.size()]);
	}

	public ChildWindow getChildWindowFromContentPane(Container contentPane){
		for (ChildWindow child : childWindows){
			if (child.getContentPane()==contentPane){
				return child;
			}
		}
		return null;
	}

	public ChildWindow getChildWindowFromContext(Object contextObject){
		for (ChildWindow child : childWindows){
			if (child.getContextObject().equals(contextObject)){
				return child;
			}
		}
		return null;
	}

	public SimulationWindow getTempSimWindow(String simID) {
		for (ChildWindow child : childWindows){
			if (child.getContextObject() instanceof SimulationWindow &&
				((SimulationWindow)child.getContextObject()).getSimulation() instanceof TempSimulation &&
				((SimulationWindow)child.getContextObject()).getSimulation().getKey().toString().equals(simID)){
				return (SimulationWindow)child.getContextObject();
			}
		}
		return null;
		
	}
}
	
	