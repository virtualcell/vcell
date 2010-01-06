package cbit.vcell.microscopy.batchrun; 

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * The Class handles action events, which is triggered by both
 * tree-selection events and mouse-click events. When a tree node
 * is clicked twice, a dialog will be appear for editting the selected
 * species.
 * @author Li Ye
 * @version 1.1
 */
public class TreeHandler extends MouseAdapter implements TreeSelectionListener
{
    /*public DetailsFrame detailsFrame;
    private DefaultMutableTreeNode parent;
    private Object parentInfo;
    private Object lastNodeInfo;//to keep nodeInfo when mouse pressed first time.
    private DrawFrame drawFrame;*/
    public void valueChanged(TreeSelectionEvent e)
    {

           /*DefaultMutableTreeNode node = (DefaultMutableTreeNode)detailsFrame.cellWareViewTree.getLastSelectedPathComponent();

           if (node == null) return;
           else
           {
              Object nodeInfo = node.getUserObject();
              if(nodeInfo instanceof String)
              {
                    lastNodeInfo=nodeInfo;
                    //find it's parent
                    parent=(DefaultMutableTreeNode)node.getParent();
                    if (parent!=null)
                    parentInfo=parent.getUserObject();
                    if(parentInfo instanceof String)
                    {  //selected node is a gene
                       if(((String)parentInfo).compareTo("Gene")==0)
                       {
                           if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                           {
                               drawFrame=(DrawFrame)MainFrame.mf.workingFrame.desktop.getSelectedFrame();
                               //find that gene
                               Gene temGene=null;
                               for(int i=0;i<drawFrame.cellwareModel.geneList.size();i++)
                               {
                                  if(((Gene)drawFrame.cellwareModel.geneList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                                  {
                                     temGene=((Gene)drawFrame.cellwareModel.geneList.elementAt(i));
                                     drawFrame.drawPanel.mh.MouseEventsIni();
                                     //MouseHandler.doubleClickedGeneID=temGene.geneID;
                                     MouseHandler.selectedindex=i;
                                     MouseHandler.isInsideObject=true;
                                     MouseHandler.selectedObject=CellWareProject.GENE;
                                     temGene.setSelected(true);
                                     drawFrame.cellwareModel.getModelFrame().drawPanel.repaint();
                                     MainFrame.setMenusAndButtons(true);
                                     break;
                                  }
                               }
                           }
                       }
                       //selected node is a protein
                       else if(((String)parentInfo).compareTo("Protein")==0)
                       {
                           if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                           {
                               drawFrame=(DrawFrame)MainFrame.mf.workingFrame.desktop.getSelectedFrame();
                               //find that protein
                               Protein temProtein=null;
                               for(int i=0;i<drawFrame.cellwareModel.proteinList.size();i++)
                               {
                                  if(((Protein)drawFrame.cellwareModel.proteinList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                                  {
                                     temProtein=((Protein)drawFrame.cellwareModel.proteinList.elementAt(i));
                                     drawFrame.drawPanel.mh.MouseEventsIni();
                                     //MouseHandler.doubleClickedProteinID=temPrtoein.proteinID;
                                     MouseHandler.selectedindex=i;
                                     MouseHandler.isInsideObject=true;
                                     MouseHandler.selectedObject=CellWareProject.PROTEIN;
                                     temProtein.setSelected(true);
                                     drawFrame.cellwareModel.getModelFrame().drawPanel.repaint();
                                     MainFrame.setMenusAndButtons(true);
                                     break;
                                  }
                               }
                           }
                       }
                       else if(((String)parentInfo).compareTo("Modifier")==0)
                       {
                           if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                           {
                               drawFrame=(DrawFrame)MainFrame.mf.workingFrame.desktop.getSelectedFrame();
                               //find that modifier
                               Modifier temModifier=null;
                               for(int i=0;i<drawFrame.cellwareModel.modifierList.size();i++)
                               {
                                  if(((Modifier)drawFrame.cellwareModel.modifierList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                                  {
                                     temModifier=((Modifier)drawFrame.cellwareModel.modifierList.elementAt(i));
                                     //MouseHandler.doubleClickedModifierID=temModifier.modifierID;
                                     drawFrame.drawPanel.mh.MouseEventsIni();
                                     MouseHandler.selectedindex=i;
                                     MouseHandler.isInsideObject=true;
                                     MouseHandler.selectedObject=CellWareProject.MODIFIER;
                                     temModifier.setSelected(true);
                                     drawFrame.cellwareModel.getModelFrame().drawPanel.repaint();
                                     MainFrame.setMenusAndButtons(true);
                                     break;
                                  }
                               }
                           }
                       }
                       else if(((String)parentInfo).compareTo("Metabolite")==0)
                       {
                           if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                           {
                               drawFrame=(DrawFrame)MainFrame.mf.workingFrame.desktop.getSelectedFrame();
                               //find that Metabolite
                               Metabolite temMetabolite=null;
                               for(int i=0;i<drawFrame.cellwareModel.metaboliteList.size();i++)
                               {
                                  if(((Metabolite)drawFrame.cellwareModel.metaboliteList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                                  {
                                     temMetabolite=((Metabolite)drawFrame.cellwareModel.metaboliteList.elementAt(i));
                                     //MouseHandler.doubleClickedMetaboliteID=temMetabolite.metaboliteID;
                                     drawFrame.drawPanel.mh.MouseEventsIni();
                                     MouseHandler.selectedindex=i;
                                     MouseHandler.isInsideObject=true;
                                     MouseHandler.selectedObject=CellWareProject.METABOLITE;
                                     temMetabolite.setSelected(true);
                                     drawFrame.cellwareModel.getModelFrame().drawPanel.repaint();
                                     MainFrame.setMenusAndButtons(true);
                                     break;
                                  }
                               }
                           }
                       }
                       else if(((String)parentInfo).compareTo("MRNA")==0)
                       {
                           if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                           {
                               drawFrame=(DrawFrame)MainFrame.mf.workingFrame.desktop.getSelectedFrame();
                               //find that mRNA
                               MRNA temMRNA=null;
                               for(int i=0;i<drawFrame.cellwareModel.mRNAList.size();i++)
                               {
                                  if(((MRNA)drawFrame.cellwareModel.mRNAList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                                  {
                                     temMRNA=((MRNA)drawFrame.cellwareModel.mRNAList.elementAt(i));
                                     //MouseHandler.doubleClickedMRNAID=temMRNA.mRNAID;
                                     drawFrame.drawPanel.mh.MouseEventsIni();
                                     MouseHandler.selectedindex=i;
                                     MouseHandler.isInsideObject=true;
                                     MouseHandler.selectedObject=CellWareProject.MRNA;
                                     temMRNA.setSelected(true);
                                     drawFrame.cellwareModel.getModelFrame().drawPanel.repaint();
                                     MainFrame.setMenusAndButtons(true);
                                     break;
                                  }
                               }
                           }
                       }

                    }//if(parentInfo instanceof string)
              }
           }*/
//            System.out.println("location 2");
//            DefaultMutableTreeNode node2 = (DefaultMutableTreeNode)detailsFrame.cellWareViewTree.getLastSelectedPathComponent();
//            if (node2 == null) return;
//            else
//            {
//                Object nodeInfo = node2.getUserObject();
//                System.out.println("nodeInfo:"+nodeInfo);
//                if(nodeInfo instanceof String)
//                {
//                    lastNodeInfo=nodeInfo;
//                    //find it's parent
//                    parent=(DefaultMutableTreeNode)node2.getParent();
//                    if (parent!=null)
//                    parentInfo=parent.getUserObject();
//                    System.out.println("parentInfo:"+parentInfo);
//                }
//            }

    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
        /*if (e.getClickCount() == 2) {
            TreePath path = detailsFrame.cellWareViewTree.getSelectionPath();
            Object node = path.getLastPathComponent();
            // DefaultMutableTreeNode node = (DefaultMutableTreeNode)detailsFrame.cellWareViewTree.getLastSelectedPathComponent();
            if (node instanceof Experiment) {
                Experiment exp = (Experiment) node;
                StandardInternalFrame report = exp.getReprot();
                MainFrame.mf.workingFrame.desktop.moveToFront(report);
                try{
                    if (report.isIcon()) report.setIcon(false);
                    report.setSelected(true);
                }catch (java.beans.PropertyVetoException ex) {ex.printStackTrace();}
                // MainFrame.mf.workingFrame.desktop.setSelectedFrame(report); // TODO: XXX doesn't work?

            }

        }*/
    }

    public void mousePressed(MouseEvent e)
    {
       /*if(e.getClickCount() == 2)
       {
//            System.out.println("in side mousePressed");
            if((lastNodeInfo instanceof String)&&(parentInfo instanceof String))
            {  //selected node is a gene
               if(((String)parentInfo).compareTo("Gene")==0)
               {
                   if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                   {
                       //find that gene by caption
                       Gene temGene=null;
                       for(int i=0;i<drawFrame.cellwareModel.geneList.size();i++)
                       {
                          if(((Gene)drawFrame.cellwareModel.geneList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                          {
                             temGene=((Gene)drawFrame.cellwareModel.geneList.elementAt(i));
                             break;
                          }
                       }
                       if(temGene != null)
                       {
                           GeneDialog genedialog=null;
                           if(genedialog==null)
                           {
                             genedialog=new GeneDialog(MainFrame.mf,temGene);
                             genedialog.showGeneInfo();
                           }
                           //set dialog to null after use
                           if(genedialog != null)
                             genedialog=null;
                       }
                   }
               }
               else if(((String)parentInfo).compareTo("Protein")==0)
               {
                   if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                   {
                       //find that Protein by caption
                       Protein temProtein=null;
                       for(int i=0;i<drawFrame.cellwareModel.proteinList.size();i++)
                       {
                          if(((Protein)drawFrame.cellwareModel.proteinList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                          {
                             temProtein=((Protein)drawFrame.cellwareModel.proteinList.elementAt(i));
                             break;
                          }
                       }
                       if(temProtein!=null)
                       {
                           ProteinDialog proteindialog=null;
                           if(proteindialog==null)
                           {
                             proteindialog=new ProteinDialog(MainFrame.mf,temProtein);
                             proteindialog.showProteinInfo();
                           }
                           //set dialog to null after use
                           if(proteindialog != null)
                             proteindialog=null;
                       }
                   }
               }
               else if(((String)parentInfo).compareTo("Modifier")==0)
               {
                   if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                   {
                       //find that modifier by caption
                       Modifier temModifier=null;
                       for(int i=0;i<drawFrame.cellwareModel.modifierList.size();i++)
                       {
                          if(((Modifier)drawFrame.cellwareModel.modifierList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                          {
                             temModifier=((Modifier)drawFrame.cellwareModel.modifierList.elementAt(i));
                             break;
                          }
                       }
                       if(temModifier!=null)
                       {
                           ModifierDialog modifierdialog=null;
                           if(modifierdialog==null)
                           {
                             modifierdialog=new ModifierDialog(MainFrame.mf,temModifier);
                             modifierdialog.showModidierInfo();
                           }
                           //set dialog to null after use
                           if(modifierdialog != null)
                             modifierdialog=null;
                       }
                   }
               }
               else if(((String)parentInfo).compareTo("Metabolite")==0)
               {
                   if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                   {
                       //find that Metabolite by caption
                       Metabolite temMetabolite=null;
                       for(int i=0;i<drawFrame.cellwareModel.metaboliteList.size();i++)
                       {
                          if(((Metabolite)drawFrame.cellwareModel.metaboliteList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                          {
                             temMetabolite=((Metabolite)drawFrame.cellwareModel.metaboliteList.elementAt(i));
                             break;
                          }
                       }
                       if(temMetabolite!=null)
                       {
                           MetaboliteDialog metabolitedialog=null;
                           if(metabolitedialog==null)
                           {
                             metabolitedialog=new MetaboliteDialog(MainFrame.mf,temMetabolite);
                             metabolitedialog.showMetaboliteInfo();
                           }
                           //set dialog to null after use
                           if(metabolitedialog != null)
                             metabolitedialog=null;
                       }
                   }
               }
               else if(((String)parentInfo).compareTo("MRNA")==0)
               {
                   if(MainFrame.mf.workingFrame.desktop.getSelectedFrame() instanceof DrawFrame)
                   {
                       //find that MRNA by caption
                       MRNA temMRNA=null;
                       for(int i=0;i<drawFrame.cellwareModel.mRNAList.size();i++)
                       {
                          if(((MRNA)drawFrame.cellwareModel.mRNAList.elementAt(i)).caption.compareTo(((String)lastNodeInfo).trim())==0)
                          {
                             temMRNA=((MRNA)drawFrame.cellwareModel.mRNAList.elementAt(i));
                             break;
                          }
                       }
                       if(temMRNA!=null)
                       {
                           MRNADialog mRNAdialog=null;
                           if(mRNAdialog==null)
                           {
                             mRNAdialog=new MRNADialog(MainFrame.mf,temMRNA);
                             mRNAdialog.showMRNAInfo();
                           }
                           //set dialog to null after use
                           if(mRNAdialog != null)
                             mRNAdialog=null;
                       }
                   }
               }
//               else if(((String)parentInfo).compareTo("Experiments")==0)
//               {
//                   System.out.println("location 1");
//                   JInternalFrame frameList[]=MainFrame.mf.workingFrame.desktop.getAllFrames();
//                   for(int i=0;i<frameList.length;i++)
//                   {
//                       if(((StandardInternalFrame)frameList[i]).getTitle().equals(((String)lastNodeInfo).trim()))
//                       {
//                           System.out.println("index:"+i);
//                           MainFrame.mf.workingFrame.desktop.setSelectedFrame(((StandardInternalFrame)frameList[i]));
//                           break;
//                       }
//                   }
//               }
           }//if(parentInfo instanceof string)
           parentInfo="";
       }*/
    }
}
