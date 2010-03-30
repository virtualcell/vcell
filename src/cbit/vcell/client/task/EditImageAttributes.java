package cbit.vcell.client.task;

import java.awt.Component;
import java.util.Calendar;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.ZEnforcer;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.image.VCImageUncompressed;
import cbit.vcell.client.RequestManager;
import cbit.vcell.geometry.gui.ImageAttributePanel;

public class EditImageAttributes extends AsynchClientTask {

	public static final String STATUS_IMPORT = "import";
	public static final String STATUS_MANUAL_SEGMENT = "Manual Segment";
	public static final String STATUS_CANCEL = "cancel";

	public EditImageAttributes() {
		super("Editting image attributes", AsynchClientTask.TASKTYPE_SWING_BLOCKING);
	}
	
	@Override
	public void run(Hashtable<String, Object> hashTable) throws Exception {
		ClientTaskStatusSupport pp = getClientTaskStatusSupport();
		
		Component guiParent = (Component)hashTable.get("guiParent");
		VCImage image = (VCImage)hashTable.get("vcImage");
		RequestManager theRequestManager = (RequestManager)hashTable.get("requestManager");
		
		if (image == null) {
			throw new RuntimeException("EditImageAttributes, no image!");				
		}

		//Set image on panel and see if there are any error before proceeding
		ImageAttributePanel imageAttributePanel = new ImageAttributePanel();

		try{
			imageAttributePanel.setImage(image);
		}catch(Throwable e){
			throw new ImageException("Failed to setup ImageAttributes\n"+(e.getMessage() != null?e.getMessage():null));
		}
		
		JDialog d = new JDialog(JOptionPane.getFrameForComponent(guiParent));
		d.setTitle("Review regions and set initial geometry attributes");
		d.setModal(true);
		d.getContentPane().add(imageAttributePanel);
		imageAttributePanel.setDialogParent(d);
		d.setSize(600,600);
		d.setLocation(300,200);
		ZEnforcer.showModalDialogOnTop(d, guiParent);

		Object choice = imageAttributePanel.getStatus();
		
		if (choice != null && choice.equals(STATUS_IMPORT)) {
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
			Calendar calendar = Calendar.getInstance();
			while (!bNameIsGood){
				newName = "image_"+generateDateTimeString(calendar);
//				newName = PopupGenerator.showInputDialog(guiParent, "type a name for this IMAGE and proceed to view/edit GEOMETRY",image.getName());
//				if (newName == null || newName.length() == 0){
//					bNameIsGood = false;
//					continue;
//				}
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
					if(!bNameExists){
						bNameIsGood = true;
					}
//					if (bNameExists){
//						PopupGenerator.showErrorDialog(guiParent, "IMAGE name '"+newName+"' already exists, please enter new name");
//					}else{
//						bNameIsGood = true;
//					}
				}
				if(!bNameIsGood){
					//wait 1 second so generateImageName is sure to be different
					Thread.sleep(1000);
				}
			}
			hashTable.put("newName", newName);			
			
		}else if( choice != null && choice.equals(STATUS_MANUAL_SEGMENT)){
//			VCImage syncVCImage = new VCImageUncompressed(image);
//			imageAttributePanel.synchronize(syncVCImage, imageAttributePanel);
//			hashTable.put("syncAttributesVCImage", syncVCImage);
			throw UserCancelException.CANCEL_EDIT_IMG_ATTR;
		}else{
			throw UserCancelException.CANCEL_GENERIC;
		}
		if (image == null){
			throw new RuntimeException("failed to create new Geometry, no image");
		}
	}
	public static String generateDateTimeString(Calendar calendar){
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int sec = calendar.get(Calendar.SECOND);
		String imageName =
		year+""+
		(month < 10?"0"+month:month)+""+
		(day < 10?"0"+day:day)+
		"_"+
		(hour < 10?"0"+hour:hour)+""+
		(min < 10?"0"+min:min)+""+
		(sec < 10?"0"+sec:sec);

		return imageName;
	}
}
