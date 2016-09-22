package cbit.vcell.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Objects;

import javax.help.UnsupportedOperationException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.client.logicalwindow.LWChildFrame;
import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWFrameOrDialog;
import org.vcell.client.logicalwindow.LWHandle;
import org.vcell.client.logicalwindow.LWHandle.LWModality;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.client.logicalwindow.LWTitledDialog;
import org.vcell.client.logicalwindow.LWTraits;
import org.vcell.client.logicalwindow.LWTraits.InitialPosition;
import org.vcell.util.BeanUtils;
import org.vcell.util.ProgrammingException;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.TopLevelWindow;
import cbit.vcell.client.title.TitleChanger;
//import cbit.vcell.client.desktop.biomodel.ChildWindowListener;
import edu.uchc.connjur.wb.ExecutionTrace;



public class ChildWindowManager {
	private final ArrayList<ChildWindow> childWindows = new ArrayList<ChildWindow>();
	
	private JFrame parent = null;
	private LWContainerHandle owner = null; 
	private static final Logger LG = Logger.getLogger(ChildWindowManager.class);
	private interface ManagedChild {
		ChildWindowManager getChildWindowManager(); 
	}
	
	@SuppressWarnings("serial")
	private static class ModelessChild extends LWChildFrame implements ManagedChild {
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
	 * transition dialog for hierarchies without logicalwindow parents 
	 */
	@SuppressWarnings("serial")
	private static class JDiagAdapter extends JDialog implements LWFrameOrDialog {
		private final LWTraits traits;

		private JDiagAdapter(Window owner, String title, ModalityType modalityType, LWTraits tr) {
			super(owner, title, modalityType);
			traits = tr; 
		}
		

		@Override
		public LWTraits getTraits() {
			return traits;
		}


		@Override
		public LWModality getLWModality() {
			ModalityType smt = getModalityType( );
			switch (smt) {
			case MODELESS:
				return LWModality.MODELESS;
			case DOCUMENT_MODAL:
				return LWModality.PARENT_ONLY;
			default:
				if (LG.isEnabledFor(Level.WARN)) {
					LG.warn(ExecutionTrace.justClassName(this) + " titled " + getTitle() + 
							" using unsupported modality "  + smt);
				}
				return LWModality.PARENT_ONLY;
			}
		}

		@Override
		public Window self() {
			return this;
		}
	}
	
	/**
	 * @param title not null
	 * @param modality not null
	 * @return implementing class
	 */
	private LWFrameOrDialog createContainerImplementation(String title,LWModality modality, boolean parentCentered) {
		LWTraits traits = parentCentered ? new LWTraits(InitialPosition.CENTERED_ON_PARENT) : new LWTraits(InitialPosition.STAGGERED_ON_PARENT);
		if (owner != null) {
			switch (modality) {
			case MODELESS:
				return new ModelessChild(this,owner, title,traits);
			case PARENT_ONLY:
				return new ParentModalChild(this,owner, title,traits);
			}
		}
		else { //remove eventually
			switch (modality) {
			case MODELESS:
				return new JDiagAdapter(parent, title, ModalityType.MODELESS,traits);
			case PARENT_ONLY:
				return new JDiagAdapter(parent, title, ModalityType.DOCUMENT_MODAL,traits);
			}
		}		
		//this shouldn't happen
		throw new UnsupportedOperationException("Modality " + modality + " no supported");
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
				if (LG.isTraceEnabled()) {
					LG.trace(impl.getTitle() + " sizes\n" + GuiUtils.getPreferredSizes(impl.self()));
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
			impl = createContainerImplementation(title,modality,isCenteredOnParent);
			impl.addWindowListener(windowListener);
			{ //assemble pieces
				Container cp = impl.getContentPane();
				cp.setLayout(new BorderLayout());
				JMenuBar mb = LWNamespace.createRightSideIconMenuBar(); 
				cp.add(mb,BorderLayout.NORTH);
				cp.add(contentPane, BorderLayout.CENTER);
			}

			impl.setAlwaysOnTop(false);
			if (preferredSize != null){
				impl.setPreferredSize(preferredSize);
			}
			if (pack!=null && pack){
				impl.pack();
				if (LG.isTraceEnabled()) {
					
				}
			}

			if (resizable != null){
				impl.setResizable(resizable);
			}
			if (size != null){
				impl.setSize(size);
			}

			if (isCenteredOnParent != null) {
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

		public void toFront() {
			if(impl != null){
				if (LG.isTraceEnabled()) {
					LG.trace(impl.getTitle() + " toFront( )");
				}
				impl.toFront();
			}
			else { 
				LG.trace(" toFront( ) called on unrealized window");
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
		BeanUtils.addCloseWindowKeyboardAction(this.parent.getRootPane());
	}
	
	public ChildWindow addChildWindow(Container contentPane, Object contextObject, String title){
		if (getChildWindowFromContentPane(contentPane)!=null){
			throw new IllegalArgumentException("child window with content pane already exists");
		}
		if (getChildWindowFromContext(contextObject)!=null){
			throw new IllegalArgumentException("child window with content object "+contextObject+" already exists");
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

}
	
	