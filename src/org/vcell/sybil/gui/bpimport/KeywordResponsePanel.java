package org.vcell.sybil.gui.bpimport;

/*   KeywordResponsePanel  --- by Oliver Ruebenacker, UCHC --- March 2009 to January 2010
 *   Panel to display the response to a keyword search
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.models.tree.pckeyword.PathwayTreeSelectionListener;
import org.vcell.sybil.models.tree.pckeyword.ResponseTreeManager;
import org.vcell.sybil.util.event.Accepter;
import org.vcell.sybil.util.event.Resetable;
import org.vcell.sybil.util.gui.ButtonFormatter;
import org.vcell.sybil.util.http.pathwaycommons.search.PCIDPathwayRequest;
import org.vcell.sybil.util.http.pathwaycommons.search.PCKeywordResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;
import org.vcell.util.gui.DialogUtils;

public class KeywordResponsePanel extends ResponsePanel {
	
	private static final long serialVersionUID = 5569708707128075620L;
	
	@SuppressWarnings("serial")
	public static class GetPathwayAction extends SpecAction implements Accepter<Pathway>, Resetable,
	ItemListener {

		protected ImportManager importManager;
		protected Pathway pathway;
		protected boolean requestSmelting;
		
		public GetPathwayAction(ImportManager importMan) {
			super(new ActionSpecs("Get Selected Pathway", "Download selected pathway", 
					"Download Pathway from Pathway Commons"));
			this.importManager = importMan;
			setEnabled(false);
		}

		public void accept(Pathway pathway) {
			this.pathway = pathway;
			setEnabled(pathway != null);
		}

		public void reset() {
			pathway = null;
			setEnabled(false);
		}

		public void setRequestSmelting(boolean requestSmelting) { 
			this.requestSmelting = requestSmelting; 
		}
		
		public void actionPerformed(ActionEvent e) {
			importManager.performSearch(new PCIDPathwayRequest(pathway.primaryId()), requestSmelting);
		}

		public void itemStateChanged(ItemEvent event) {
			requestSmelting = (event.getStateChange() == ItemEvent.SELECTED);
		}
		
	}
	
	protected ImportManager importManager;
	protected ResponseTreeManager treeManager = new ResponseTreeManager();
	protected JTree responseTree = new JTree(treeManager.tree());
	private int mouseOverRow = -1;// wei's
	public KeywordResponsePanel(ImportManager importManNew, PCKeywordResponse responseNew) {
		super(responseNew);
		importManager = importManNew;
		treeManager.accept(responseNew);
		setLayout(new BorderLayout());
		add(new JScrollPane(responseTree));
		GetPathwayAction getPathwayAction = new GetPathwayAction(importManNew);
		responseTree.getSelectionModel()
		.addTreeSelectionListener(new PathwayTreeSelectionListener(getPathwayAction));
		
		//wei's code 
		// mouse action for response tree

		responseTree.addMouseMotionListener(new MouseMotionAdapter() { 
		    @Override
			public void mouseMoved(MouseEvent e) { 		
		    	TreePath path = responseTree.getPathForLocation(e.getX(), e.getY());
		    	if(path != null){
		    		if(responseTree.getModel().isLeaf(path.getLastPathComponent())){
		    			//show tooltip when mouse on the leaves
		    			String tip = generateTIP(path.getLastPathComponent().toString());
		    			// check whether tree leaves are clickable
		    			String url = generateURL(path);
		    			if(url.indexOf("http")>=0){ // contain URL, 
		    				// change mouse to hand cursor
		    				responseTree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		    				int currentRow = responseTree.getClosestRowForLocation(e.getX(), e.getY());
		    				if(currentRow != mouseOverRow){
		    					mouseOverRow = currentRow;
		    					repaint();
		    				}
		    				// set tooltips
		    				responseTree.setToolTipText("Double click to launch the web page");
		    			}else{ // no URLs
		    				//set tooltips
		    				responseTree.setToolTipText(tip);	
		    				mouseOverRow = -1;
		    				repaint();
		    			}		    			    			
		    		}else{ // when mouse is over the non-leaf node
		    			responseTree.setCursor(Cursor.getDefaultCursor());
		    			mouseOverRow = -1;
		    			repaint();
		    		}
		    		((DefaultTreeModel)responseTree.getModel()).nodeChanged((TreeNode)path.getLastPathComponent());
		    	}else{
		    		// set mouse to default shape
		    		responseTree.setCursor(Cursor.getDefaultCursor());
		    		mouseOverRow = -1;
		    		repaint();
		    	}
		    } 
		});
		// highlight the hyperlink notes when mouse is on
		responseTree.setCellRenderer(new DefaultTreeCellRenderer(){
			/**
			 * Changing color and font of text when the node is clickable
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, 
					boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus){
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if(row == mouseOverRow){
					setForeground(Color.BLUE);
					setFont(new java.awt.Font("Helvetca", java.awt.Font.BOLD, 12));
					
				}else{
					setFont(UIManager.getFont("Tree.font"));
				}
				return this;
			}
		});
		
		// adding hyperlink to tree leaves
		// when double clicking the node, a web browser will be launched
		responseTree.addMouseListener( new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				TreePath path = responseTree.getPathForLocation(e.getX(), e.getY());
		    	if(path != null){
		    		if(responseTree.getModel().isLeaf(path.getLastPathComponent())){
		    			
		    			if(e.getClickCount()>1){
		    				String url = generateURL(path);
		    				if (url.indexOf("http")>=0){ // only launch a webpage if a valid url exists
		    					DialogUtils.browserLauncher(KeywordResponsePanel.this, url, "failed to open " + url, false);
							}
		    			}
		    		}
				}
			}
		});
		// done

		
		JToolBar getPathwayToolbar = new JToolBar();
		JButton buttonGet = new JButton(getPathwayAction);
		ButtonFormatter.format(buttonGet);
		getPathwayToolbar.add(new JLabel("Double click the responses icon to view.              "));//wei
		getPathwayToolbar.add(buttonGet);
		getPathwayToolbar.add(new JLabel("              "));//wei
		JCheckBox checkBox = new JCheckBox("Try to Fix Non-Standard Data", true);
		checkBox.addItemListener(getPathwayAction);
		getPathwayAction.setRequestSmelting(checkBox.isSelected());
		getPathwayToolbar.add(checkBox);
		add(getPathwayToolbar, "North");
	}
	
	// wei's code
	// get the tip for response tree
	public String generateTIP(String path){
		String tip;
		if(path.indexOf("http") >0){
			tip = path.substring(0,path.indexOf("http")-2);
		}else{
			tip = path;
		}
		return tip;
	}
	
	// get the URL for hyperlink
	public String generateURL(TreePath long_path){
		String path = long_path.getLastPathComponent().toString();
		String l_path = long_path.toString();
		String url;
		// different webpages have different ways of their url. they also be updated so frequently.
		// make sure to change them here
		// for instance the NCBI using the new ways to create its urls.
		if(l_path.indexOf("pathways")>=0){
			url = "http://www.pathwaycommons.org/pc/record2.do?id=" + path.substring(0,6);
		}else{
			if(path.indexOf("http") >=0){ // URL is in the content
				if(path.indexOf("RefSeq Protein")>=0){
					url = "http://www.ncbi.nlm.nih.gov/protein/" + path.substring(15, 24);
				}else if(path.indexOf("Entrez Gene")>=0){
					url = "http://www.ncbi.nlm.nih.gov/gene/" + path.substring(path.length()-4);
				}else if(path.indexOf("OMIM")>=0){
					url = "http://www.ncbi.nlm.nih.gov/omim/" + path.substring(path.length()-6);
				}else{
					url = path.substring(path.indexOf("http"));
				}
			}else{
				if(path.indexOf("Organism")>=0){
					String[] temp = path.split(",");
					url = "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="+
						temp[2].substring(1,temp[2].length()-1);
				}else{
					url = "";
				}
			}
		}
		// clean the url by removing ","
		// how to deal with multiple url link???
		// I just displace the first url in a web browser so far.
		if(url.indexOf(",")>=0){
			url = url.substring(0,url.indexOf(","));
		}
		return url;
	}
	//done
	@Override
	public PCKeywordResponse response() { return (PCKeywordResponse) super.response(); }
	
}