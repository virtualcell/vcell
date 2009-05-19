package cbit.vcell.client.task;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JDialog;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.Version;

import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.RequestManager;
import cbit.vcell.geometry.gui.ImageAttributePanel;

public class EditImageAttributes extends AsynchClientTask {

	public EditImageAttributes() {
		super("Editting image attributes", AsynchClientTask.TASKTYPE_SWING_BLOCKING);
	}
	private static Object showImagePropertiesDialog(final ImageAttributePanel imageAttributePanel) {
			// Cannot use JOptionPane because it will not allow some children to resize
			JDialog d = new JDialog();
			d.setModal(true);
			d.getContentPane().add(imageAttributePanel);
			imageAttributePanel.setDialogParent(d);
			d.setSize(400,600);
			d.setLocation(300,200);
			BeanUtils.centerOnComponent(d,null);
			org.vcell.util.gui.ZEnforcer.showModalDialogOnTop(d,null);
			return imageAttributePanel.getStatus();
	}
	
	@Override
	public void run(Hashtable<String, Object> hashTable) throws Exception {
		ClientTaskStatusSupport pp = getClientTaskStatusSupport();
		
		VCImage image = (VCImage)hashTable.get("vcImage");
		RequestManager theRequestManager = (RequestManager)hashTable.get("requestManager");
		
		if (image == null) {
			PopupGenerator.showErrorDialog("No image!");				
		}

		//Set image on panel and see if there are any error before proceeding
		ImageAttributePanel imageAttributePanel = new ImageAttributePanel();

		try{
			imageAttributePanel.setImage(image);
		}catch(Throwable e){
			throw new cbit.image.ImageException("Failed to setup ImageAttributes\n"+(e.getMessage() != null?e.getMessage():null));
		}

		Object choice = showImagePropertiesDialog(imageAttributePanel);
		
		if (choice != null && choice.equals("Import")) {
			VCImageInfo imageInfos[] = null;
			pp.setMessage("Getting existing Image names");
			try {
				imageInfos = theRequestManager.getDocumentManager().getImageInfos();
			}catch (DataAccessException e){
				e.printStackTrace(System.out);
			}
			pp.setMessage("found "+(imageInfos != null?imageInfos.length:0)+" existing image names");
			String newName = null;
			boolean bNameIsGood = false;
			while (!bNameIsGood){
				newName = PopupGenerator.showInputDialog((Component)null,  "type a name for this IMAGE and proceed to view/edit GEOMETRY",image.getName());
				if (newName == null || newName.length() == 0){
					bNameIsGood = false;
					continue;
				}
				if (imageInfos==null){
					bNameIsGood = true; // if no image information assume image name is good
				}else{	
					boolean bNameExists = false;
					for (int i = 0; i < imageInfos.length; i++){
						if (imageInfos[i].getVersion().getName().equals(newName)){
							bNameExists = true;
							break;
						}
					}
					if (bNameExists){
						PopupGenerator.showErrorDialog("IMAGE name '"+newName+"' already exists, please enter new name");
					}else{
						bNameIsGood = true;
					}
				}
			}
			hashTable.put("newName", newName);			
			
		}else{
			throw org.vcell.util.UserCancelException.CANCEL_EDIT_IMG_ATTR;
		}			
		if (image == null){
			throw new RuntimeException("failed to create new Geometry, no image");
		}
	}
}
