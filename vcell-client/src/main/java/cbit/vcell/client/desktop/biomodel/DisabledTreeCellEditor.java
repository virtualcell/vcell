package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DisabledTreeCellEditor extends DefaultTreeCellEditor {
    public DisabledTreeCellEditor(JTree tree, RbmTreeCellRenderer renderer) {
                   super(tree, (DefaultTreeCellRenderer)renderer);
//Note: http://stackoverflow.com/questions/5031101/why-i-lose-the-focus-for-a-short-time-from-jtree-after-editing-a-node?rq=1 
//Andr� mentions "The tree adds a DefaultCellEditor to the JTree hierarchy when editing starts. This textfield gains the focus."                    
//it's not as simple as just this.addFocusListener()

//2 variables: editingComponent and editingContainer, are inherited from DefaultTreeCellEditor
//the constructor doesn't initialize the editingComponent
//it's null atm, so we can't add focusListener to it, (yet at a later time it will gain focus)    
//FocusOwner (on edit): javax.swing.tree.DefaultTreeCellEditor$DefaultTextField
//editingComponent is by default a DefaultTextField                    
       }        

   @Override
   public Component getTreeCellEditorComponent(final JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
      Component container = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
//Note: System.out.println("Components Type: "+containeractually.getClass().getName());
//was used to show it was container, javax.swing.tree.DefaultTreeCellEditor$EditorContainer

//getTreeCellEditorComponent(parameters) is called as soon as editing begins
//also at this time editingComponent != null (aka initialized)
//so it's a good place to add in a Focus Listener

editingComponent.addFocusListener( new FocusListener(){
               @Override  public void focusGained(FocusEvent e) { }
               @Override  public void focusLost(FocusEvent e) {
               tree.stopEditing();}                  } );

//EditorContainer is responsible for displaying the editingComponent
//so we added focusListener, after editingComponent initialized, and before it's used
//(I think the return statement means it's about to be used)
       return container;
       }
}
