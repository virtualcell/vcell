package org.vcell;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.sbml.jsbml.SBMLDocument;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.query.BioModelsQuerySpec;
import org.vcell.api.common.ApplicationRepresentation;
import org.vcell.api.common.BiomodelRepresentation;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModelService {
    
    // VCell API
    private final static String HOST = "vcellapi.cam.uchc.edu";
    private final static int PORT = 8080;
    private final static String DEFAULT_CLIENT_ID = "85133f8d-26f7-4247-8356-d175399fc2e6";
    
    public Task<List<VCellModel>, String> getModels(VCellService vCellService) {
    	
    	final Task<List<VCellModel>, String> task = new Task<List<VCellModel>, String>() {

			@Override
			protected List<VCellModel> doInBackground() throws Exception {
				
				boolean bIgnoreCertProblems = true;
				boolean bIgnoreHostMismatch = true;
				VCellApiClient vCellApiClient = null;
				List<VCellModel> vCellModels = new ArrayList<VCellModel>();
				
		    	try {
		    		vCellApiClient = new VCellApiClient(HOST, PORT, DEFAULT_CLIENT_ID, bIgnoreCertProblems, bIgnoreHostMismatch);
					vCellApiClient.authenticate("ImageJ", "richarddberlin");
		    		BioModelsQuerySpec querySpec = new BioModelsQuerySpec();
					querySpec.owner = "tutorial";
					final BiomodelRepresentation[] biomodelReps = vCellApiClient.getBioModels(querySpec);
					final int modelsToLoad = biomodelReps.length;
					int modelsLoaded = 0;
			    	
			    	for (BiomodelRepresentation biomodelRep : biomodelReps) {
			    		setSubtask(biomodelRep.getName());
			    		ApplicationRepresentation[] applicationReps = biomodelRep.getApplications();
			    		if (applicationReps.length > 0) {
			    			String vcml = getVCML(biomodelRep);
			    			if (vcml!=null){
				    			SBMLDocument sbml = vCellService.getSBML(vcml, applicationReps[0].getName());
				    			VCellModel vCellModel = new VCellModel(biomodelRep.getName(), biomodelRep.getBmKey(), sbml);
				    			vCellModels.add(vCellModel);
				    			modelsLoaded++;
				    			setProgress(modelsLoaded * 100 / modelsToLoad);
			    			}else{
			    				System.err.println("failed to return VCML for "+biomodelRep.bmKey);
			    			}
			    		}
			    	}
			    	
		    	} catch (Exception e) {
		    		e.printStackTrace(System.out);
		    	}
		    	return vCellModels;
			}
    		
		};
		
		return task;
    }
    
    public Task<BufferedImage, Void> getVCellImageForModel(VCellModel vCellModel) {
    	
    	Task<BufferedImage, Void> task = new Task<BufferedImage, Void>() {

			@Override
			protected BufferedImage doInBackground() throws Exception {
				try {
		    		BufferedImage img = ImageIO.read(
		    				new URL("https://" + HOST + ":" + PORT + "/biomodel/" + vCellModel.getBiomodelKey() + "/diagram"));
		    		System.out.println("read " + img.toString());
		    		return img;
				} catch (IOException e) {
					System.out.println("Could not load image for: " + vCellModel.getName());
				}
				return null;
			}
    		
    	};
    	
    	return task;
    }
    
    private String getVCML(BiomodelRepresentation biomodelRep) {
    	try {
			VCellApiClient client = new VCellApiClient(HOST,PORT,"12345",true,true);
			try {
				String vcml = client.getBioModelVCML(biomodelRep.getBmKey());
				return vcml;
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve VCML for biomodel key "+biomodelRep.getBmKey(),e);
			}
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to connect to vcellAPI",e);
		}
//    	VCellApiClient.CloseableHttpClient httpClient = HttpClients.createDefault();
//    	HttpGet httpGet = new HttpGet("https://" + HOST + ":" + PORT + "/biomodel/" + biomodelRep.getBmKey() + "/biomodel.vcml");
//    	System.out.println("About to execute");
//    	CloseableHttpResponse response = httpClient.execute(httpGet);
//    	System.out.println("Execution completed");
//    	try {
//    		HttpEntity entity = response.getEntity();
////        	entity.writeTo(System.out);
//        	EntityUtils.consume(entity);
//    	} finally {
//    		response.close();
//    	}
//    	return null;
    }
}
