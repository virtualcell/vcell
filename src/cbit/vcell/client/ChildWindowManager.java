package cbit.vcell.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.help.UnsupportedOperationException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.client.logicalwindow.LWChildFrame;
import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWFrameOrDialog;
import org.vcell.client.logicalwindow.LWHandle;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.client.logicalwindow.LWHandle.LWModality;
import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.util.BeanUtils;
import org.vcell.util.ProgrammingException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.desktop.TopLevelWindow;
//import cbit.vcell.client.desktop.biomodel.ChildWindowListener;
import edu.uchc.connjur.wb.ExecutionTrace;



public class ChildWindowManager {
	private final ArrayList<ChildWindow> childWindows = new ArrayList<ChildWindow>();
	
	private JFrame parent = null;
	private LWContainerHandle owner = null; 
	private static final Logger LG = Logger.getLogger(ChildWindowManager.class);
	
	@SuppressWarnings("serial")
	private static class TitledChild extends LWChildFrame {

		private TitledChild(LWContainerHandle parent, String title)
				throws HeadlessException {
			super(parent, title);
			
		}

		@Override
		public String menuDescription() {
			return getTitle( );
		}
	}
	
	/**
	 * transition dialog for hiearchies without logicalwindow parents 
	 */
	@SuppressWarnings("serial")
	private static class JDiagAdapter extends JDialog implements LWFrameOrDialog {

		private JDiagAdapter(Window owner, String title, ModalityType modalityType) {
			super(owner, title, modalityType);
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
	}

	/**
	 * @param title not null
	 * @param modality not null
	 * @return implementing class
	 */
	private LWFrameOrDialog createContainerImplementation(String title,LWModality modality) {
		if (owner != null) {
			switch (modality) {
			case MODELESS:
				return new TitledChild(owner, title);
			case PARENT_ONLY:
				return new DialogUtils.TitledDialog(owner,title);
			}
		}
		else { //remove eventually
			switch (modality) {
			case MODELESS:
				return new JDiagAdapter(parent, title, ModalityType.MODELESS);
			case PARENT_ONLY:
				return new JDiagAdapter(parent, title, ModalityType.DOCUMENT_MODAL);
			}
		}		
		//this shouldn't happen
		throw new UnsupportedOperationException("Modality " + modality + " no supported");
	}

	public class ChildWindow {
		
		private WindowListener windowListener = new WindowListener(){
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {
				for (ChildWindowListener listener : listeners){
					listener.closed(ChildWindow.this);
				}
				
			}
			public void windowClosing(WindowEvent e) {
				for (ChildWindowListener listener : listeners){
					listener.closing(ChildWindow.this);
				}
				if (ChildWindow.this != null) {
					closeChildWindow(ChildWindow.this);
				}
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		};

		
		private boolean bModal;
		private Container contentPane;
		private Object contextObject;
		private LWFrameOrDialog impl;
		
		private String title = null;
		private Point location = null;
		private Dimension preferredSize = null;
		private Boolean resizable = null;
		private Boolean pack = null;
		private Dimension size = null;
		private Boolean isCenteredOnScreen = null;
		private Boolean isCenteredOnParent = true;
		
		
		private ArrayList<ChildWindowListener> listeners = new ArrayList<ChildWindowListener>();
	
		private ChildWindow(Container contentPane, Object aContextObject, String title) {
			this.contentPane = contentPane;
			this.contextObject = aContextObject;
			this.title = title;
		}
					
		public void setLocationRelativeToParent(int x, int y) {
			if (impl!=null){
				impl.setLocationRelativeTo(impl.getParent());
				impl.setLocation(x, y);
				isCenteredOnScreen = false;
				isCenteredOnParent = false;
			}
		}
		
		public void setLocationRelativeToComponent(Component component) {
			if (impl!=null){
				impl.setLocationRelativeTo(component);
				isCenteredOnScreen = false;
				isCenteredOnParent = false;
			}
		}
		
		public void setIsCenteredOnParent(){
			isCenteredOnParent = true;
			isCenteredOnScreen = false;
			if (impl!=null){
				impl.setLocationRelativeTo(impl.getParent());
				
			}
		}
		
		public void setIsCenteredOnScreen(){
			isCenteredOnParent = false;
			isCenteredOnScreen = true;
			if (impl!=null){
				impl.setLocationRelativeTo(null);  // as specified in java.awt.Window javadocs 
			}
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
				impl.dispose();
				impl = null;
			} else {
				//DebugUtils.stop("ChildWindowManager.ChildWindow.dispose(): I was just asked to displose of a null JDialog ");
			}
		}
		
		public Container getContentPane() {
			return this.contentPane;
		}
		
		public Object getContextObject() {
			return contextObject;
		}
				
		public Point getLocation(){
			if (impl!=null){
				return impl.getLocation();
			}else{
				return location;
			}
		}
		
		public Point getLocationOnScreen(){
			if (impl!=null){
				return impl.getLocationOnScreen();
			}else{
				return null;
			}
		}
		
		public boolean getModal(){
			return bModal;
		}
		
		public JFrame getParent() {
			return parent;
		}
		
		public String getTitle() {
			if (impl!=null){
				return impl.getTitle();
			}else{
				return title;
			}
		}
		public void hide(){
			if (impl!=null && impl.isVisible()){
				impl.setVisible(false);
				for (ChildWindowListener listener : listeners){
					listener.closed(this);
				}
			}
		}
		public boolean isShowing() {
			if (impl!=null){
				return impl.isShowing();
			}else{
				return false;
			}
		}
		public void removehildWindowListener(ChildWindowListener childWindowListener) {
			listeners.remove(childWindowListener);
		}
		
		public void requestFocus(){
			if (impl!=null){
				impl.requestFocus();
			}
		}
		
		public boolean requestFocusInWindow(){
			boolean isLikelyToSucceed = false;
			if (impl!=null){
				isLikelyToSucceed = impl.requestFocusInWindow();
			}
			return isLikelyToSucceed;
		}

		public void setContextObject(Object context) {
			this.contextObject = context;
		}
		
		public void setLocation(Point point){
			location = point;
			if (impl!=null){
				impl.setLocation(point);
			}
		}
		
		public void setPreferredSize(Dimension preferredSize){
			this.preferredSize = preferredSize;
			if (impl!=null){
				impl.setPreferredSize(preferredSize);
			}
		}
		
		public void setResizable(boolean resizable){
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
		 * @deprecated doesn't do anything
		 */
		public void setPosition(int i, int j) {
			
		}
		
		public void setSize(int i, int j) {
			this.size = new Dimension(i,j);
			if (impl!=null){
				impl.setSize(i,j);
			}
		}
		
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
			impl = createContainerImplementation(title,modality);
			impl.addWindowListener(windowListener);
			{ //assemble pieces
				Container cp = impl.getContentPane();
				cp.setLayout(new BorderLayout());
				JMenuBar mb = new JMenuBar();
				mb.add( LWTopFrame.createWindowMenu(false) );			
				cp.add(mb,BorderLayout.NORTH);
				cp.add(contentPane, BorderLayout.CENTER);
			}

			impl.setAlwaysOnTop(false);
			if (location!=null){
				impl.setLocation(location);
			}
			if (preferredSize != null){
				impl.setPreferredSize(preferredSize);
			}
			if (pack!=null && pack){
				impl.pack();
			}

			if (resizable != null){
				impl.setResizable(resizable);
			}
			if (size != null){
				impl.setSize(size);
			}

			if (isCenteredOnScreen !=null) {
				impl.setLocationRelativeTo(null);
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

		/**
		 * show as {@link LWHandle.LWModality#PARENT_ONLY}
		 * @throws ProgrammingException if {@link #show()} previously called
		 */
		public void showModal() {
			show(LWModality.PARENT_ONLY);
		}

		public void toFront() {
			if(impl != null){
				impl.toFront();
			}
		}
	};   
	
	public static ChildWindowManager findChildWindowManager(Component component){
		ChildWindowManager childWindowManager = null;
		Frame topLevelFrame = JOptionPane.getFrameForComponent(component);
		if (topLevelFrame instanceof TopLevelWindow){
			childWindowManager = ((TopLevelWindow)topLevelFrame).getChildWindowManager();
		}
		
		if (childWindowManager==null){
			System.err.println("ChildWindowManager.findChildWindowManager(Component) could not find a ChildWindowManager for component: "+component.getName()+" which is a "+component.getClass().getCanonicalName());
			Thread.dumpStack();
			System.err.println();
		}
		
		return childWindowManager;
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
	
	public ChildWindow addChildWindow(Container contentPane, Object contextObject, String title, boolean resizable){
		ChildWindow childWindow = addChildWindow(contentPane, contextObject, title);
		childWindow.setResizable(resizable);
		return childWindow;
	}
	
	public void closeAllChildWindows(){
		while (childWindows.size()>0){
			ChildWindow child = childWindows.get(0);
			child.hide();
			child.dispose();
			childWindows.remove(child);
		}
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

	public void cascadeWindows() {
		Dimension sizeOfCurrentDisplay = new Dimension(1000,1000);
		int countShownWindows = 0;
		for (ChildWindow childWindow : childWindows){
			if (childWindow.isShowing()){
				countShownWindows++;
			}
		}
		if (countShownWindows==0){
			return;
		}
		
		double dx = sizeOfCurrentDisplay.getWidth() / countShownWindows / 4;
		double dy = sizeOfCurrentDisplay.getHeight() / countShownWindows / 4;
		int count = 0;
		for (ChildWindow childWindow : childWindows){
			if (childWindow.isShowing()){
				childWindow.setLocation(new Point((int)(dx*count),(int)(dy*count)));
				childWindow.show();
				count++;
			}
		}
	}

	public void tileWindows(boolean horizontal) {
//		JInternalFrame[] iframes = getOpenWindows();
//		Rectangle[] bounds = BeanUtils.getTiledBounds(iframes.length, getJDesktopPane().getWidth(), getJDesktopPane().getHeight(), horizontal);
//		for (int i=0;i<iframes.length;i++) {
//			iframes[i].setBounds(bounds[i]);
//			iframes[i].show();
//		}
	}	
	

}
	
	