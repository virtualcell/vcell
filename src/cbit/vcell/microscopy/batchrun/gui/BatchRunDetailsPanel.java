package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.batchrun.TreeHandler;



public class BatchRunDetailsPanel extends JPanel implements ActionListener
{
	public URL[] iconFiles = {getClass().getResource("/images/add.gif"),
							  getClass().getResource("/images/delete.gif"),
							  getClass().getResource("/images/deleteAll.gif")};
	public String[] buttonLabels = {"Add a file to batch run", "Delete a file from batch run", "Delete all batch run files"};
	private ImageIcon[] icons = new ImageIcon[iconFiles.length];
	private JButton addButton, deleteButton, delAllButton;
	
	private JSplitPane leftSplit = null;
	// bottom component definition
	private JPanel botPanel = null;
	private JTextArea parameterTa = null;
	private JScrollPane tascrollPane = null;
	
	// top component definition
	private JPanel frapDocViewPanel= null; 
	private JToolBar toolbar = null;
	private JTree frapDocViewTree= null;
	private JScrollPane treeScrollPane = null;
	
	private DefaultMutableTreeNode frapDocTreeNode = null;
	
	//tree handler
	private TreeHandler th=new TreeHandler();

	//constructor
	public BatchRunDetailsPanel()
	{
		super();
	    //topTabPane.addMouseListener(th);
	    leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,getFrapDocViewPanel(), getBottomPanel());
	    leftSplit.setDividerSize(2);
	    leftSplit.setDividerLocation(Math.round(Toolkit.getDefaultToolkit().getScreenSize().height*1/2));
	    setLayout(new BorderLayout());
	    add(leftSplit, BorderLayout.CENTER);
	    setBorder(new EmptyBorder(0,0,0,0));
	    //addMouseListener(th);
	    setVisible(true);
	}

	// set up top component function
	public JPanel getFrapDocViewPanel()
	{
		if(frapDocViewPanel == null)
		{
			frapDocViewPanel = new JPanel();
	//	    frapDocViewPanel.setFont(new Font("Tahoma", Font.PLAIN, 11));
			
		    frapDocViewPanel.setLayout(new BorderLayout());
		    frapDocViewPanel.add(getTreeScrollPane(),BorderLayout.CENTER);
		    
		    //add toolbar
		    frapDocViewPanel.add(getToolBar(), BorderLayout.SOUTH);
		}
	    return frapDocViewPanel;
	}
	
	public JScrollPane getTreeScrollPane()
	{
		if(treeScrollPane == null)
		{
			treeScrollPane = new JScrollPane();
			frapDocTreeNode = new DefaultMutableTreeNode("FRAP Batch-run Docs");
		    frapDocViewTree = new JTree(frapDocTreeNode);
		    //set font for treenodes
		    frapDocViewTree.setFont(new Font("Tahoma", Font.PLAIN, 11));
		    frapDocViewTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		    frapDocViewTree.addTreeSelectionListener(th);
		    frapDocViewTree.addMouseListener(th);
	//	    frapDocViewTree.setCellRenderer(new MyTreeRenderer());
	
		    treeScrollPane.getViewport().add(frapDocViewTree);
		}
		return treeScrollPane;
	}
	
	public JToolBar getToolBar()
    {
    	if(toolbar == null)
    	{
	    	toolbar=new JToolBar();
	        //System.out.println(buttonLabels.length);
	        for (int i = 0; i < buttonLabels.length; ++i)
	        {
	             icons[i]=new ImageIcon(iconFiles[i]);
	        }
	        addButton=new JButton(icons[0]);
	        addButton.setMargin(new Insets(0, 0, 0, 0));
	        addButton.setToolTipText(buttonLabels[0]);
	        addButton.setBorderPainted(false);
	        addButton.addActionListener(this);
	        deleteButton=new JButton(icons[1]);
	        deleteButton.setMargin(new Insets(0, 0, 0, 0));
	        deleteButton.setToolTipText(buttonLabels[1]);
	        deleteButton.setBorderPainted(false);
	        deleteButton.addActionListener(this);
	        delAllButton=new JButton(icons[2]);
	        delAllButton.setMargin(new Insets(0, 0, 0, 0));
	        delAllButton.setToolTipText(buttonLabels[1]);
	        delAllButton.setBorderPainted(false);
	        delAllButton.addActionListener(this);
	        
	        toolbar.add(addButton);
	        toolbar.add(deleteButton);
	        toolbar.add(delAllButton);
    	}
    	return toolbar;
       
    }//end of method setupToolBar()
	
	// set up bottom component function
	public JPanel getBottomPanel()
	{
		if(botPanel == null)
		{
			botPanel = new JPanel();
		    botPanel.setLayout(new BorderLayout());
		    botPanel.add(getParameterScrollPanel(),BorderLayout.CENTER);
		    TitledBorder tb=new TitledBorder(new EtchedBorder(),"Parameters:", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));
		    botPanel.setBorder(tb);
		}
		return botPanel;
	}
	
	public JScrollPane getParameterScrollPanel()
	{
		if(tascrollPane == null)
		{
		    parameterTa= new JTextArea();
		    parameterTa.setFont(new Font("Tahoma", Font.PLAIN, 11));
		    tascrollPane=new JScrollPane(parameterTa);
		}
		return tascrollPane;
	}
	
	public void clearFRAPViewTree()
	{
	    if (frapDocTreeNode != null)
		{
	    	frapDocTreeNode.clone();//TODO: find out why need clone first?
	    	frapDocTreeNode.removeAllChildren();
	    	frapDocViewTree.updateUI();
		}
	}
	// for initating cellWareViewTree
	public void initiateFrapViewTree(FRAPBatchRunWorkspace brWorkSpace)
	{
		if(true)//TODO: put condition
		{
			frapDocTreeNode.removeAllChildren();
//	    	  frapDocTreeNode.add  //TODO: add children

			expendFRAPViewTree();
			frapDocViewTree.updateUI();
		}
	}// end of method initiateCellWareViewTree

	protected void expendFRAPViewTree()
	{
		DefaultMutableTreeNode node=frapDocTreeNode.getFirstLeaf();
		while (node != null)
		{
	        DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)node.getParent();
	         if (parentNode == null) return; 
	        TreeNode[] treeNodeArray = parentNode.getPath();
	        TreePath path = new TreePath(treeNodeArray);

	        if (path != null)
	            frapDocViewTree.expandPath(path);
	        // and get the next sibling
	        node = node.getNextLeaf();
		}
	}

	// for updating cellWareViewTree
	public void updateFRAPViewTree(FRAPBatchRunWorkspace brWorkSpace)
	{
		if(brWorkSpace!=null)
		{
			initiateFrapViewTree(brWorkSpace);

			//TODO: add tree nodes
			
			expendFRAPViewTree();
		}
		else
		{
			frapDocTreeNode.removeAllChildren();
		}
		frapDocViewTree.updateUI();
	}// end of method initiateCellWareViewTree

	public static void main(java.lang.String[] args) {
		try {
			try{
		    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }catch(Exception e){
		    	throw new RuntimeException(e.getMessage(),e);
		    }
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunDetailsPanel aPanel = new BatchRunDetailsPanel();
			frame.add(aPanel);
			frame.pack();
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}

	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
  	    if (source == addButton)
	    {
            System.out.println("Add button pressed.");
        }
        else if(source == deleteButton)
        {
        	System.out.println("Delete button pressed.");
        }
        else if(source == delAllButton)
        {
        	System.out.println("Delete all button pressed.");
        }
	}
}//end of class DetailsFrame


/**
 * The class MyTreeRenderer is written for customize the tree cell renderers.
 */
class MyTreeRenderer extends DefaultTreeCellRenderer
{
    protected ImageIcon batchRunViewIcon = new ImageIcon(getClass().getResource("/images/open.gif"));
    protected ImageIcon treeLeafIcon = new ImageIcon(getClass().getResource("images/treeLeaf.gif"));
    protected ImageIcon rootIcon = new ImageIcon(getClass().getResource("images/project.gif"));

    public MyTreeRenderer()
    {
    }
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus)
    {
        super.getTreeCellRendererComponent( tree, value, sel,expanded, leaf, row,hasFocus);
        if(isLeaf(value)) setIcon(treeLeafIcon);
        else
        {
            //System.out.println("cellwareviewtree: " +cellWareViewTree);
            if((value.toString()).compareTo("FRAP Batch-run Docs")==0)
            {
                setIcon(rootIcon);
            }
            
//	            else if (isSecondToRoot(value)) setIcon(null);
        }
        /*else
        {
            if (isLeaf(value)) setIcon(treeLeafIcon);
            else if (isSecondToRoot(value)) setIcon(null);
        } */
        return this;
    }

    protected boolean isLeaf(Object value)
    {
        DefaultMutableTreeNode node =(DefaultMutableTreeNode)value;
        DefaultMutableTreeNode parent=(DefaultMutableTreeNode)node.getParent();
        if (parent!=null)
            if(!(((String)parent.getUserObject()).compareTo("FRAP Batch-run Docs")==0))
               return true;
        return false;
    }

}
