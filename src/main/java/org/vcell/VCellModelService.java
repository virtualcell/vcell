package org.vcell;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.sbml.jsbml.*;
import org.sbml.jsbml.xml.XMLAttributes;
import org.sbml.jsbml.xml.XMLNode;
import org.sbml.jsbml.xml.XMLTriple;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.query.BioModelsQuerySpec;
import org.vcell.api.common.ApplicationRepresentation;
import org.vcell.api.common.BiomodelRepresentation;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
			    			SBMLDocument sbml = vCellService.getSBML(vcml, applicationReps[0].getName());
			    			VCellModel vCellModel = new VCellModel(biomodelRep.getName(), biomodelRep.getBmKey(), sbml);
			    			vCellModels.add(vCellModel);
			    			modelsLoaded++;
			    			setProgress(modelsLoaded * 100 / modelsToLoad);
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
    
    private String getVCML(BiomodelRepresentation biomodelRep) throws ClientProtocolException, IOException {
    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	HttpGet httpGet = new HttpGet("https://" + HOST + ":" + PORT + "/biomodel/" + biomodelRep.getBmKey() + "/biomodel.vcml");
    	System.out.println("About to execute");
    	CloseableHttpResponse response = httpClient.execute(httpGet);
    	System.out.println("Execution completed");
    	try {
    		HttpEntity entity = response.getEntity();
//        	entity.writeTo(System.out);
        	EntityUtils.consume(entity);
    	} finally {
    		response.close();
    	}
    	return null;
    }
}
