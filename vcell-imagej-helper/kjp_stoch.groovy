#@VCellHelper vh

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;

import org.vcell.imagej.helper.VCellHelper.ModelType
import net.imglib2.img.array.ArrayImg;
import net.imglib2.util.Fraction;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.Img
import net.imglib2.img.array.ArrayImgs;


		mt = ModelType.bm;
		int[] timeIndexes = [3];
    	searchedData = vh.getSearchedData("frm",true, ModelType.bm,null,null, "Copy of Copy of fully implicit", "C_cyt",timeIndexes,0);
    	double[] data = searchedData.getDatas().iterator().next();
    	long[] xyzDims = searchedData.getXYZDimensions(data);
		img1 = ArrayImgs.doubles(data,xyzDims)
        ImageJFunctions.show( img1 );



		URL url = new URL("http://172.16.129.126:8010"+"/"+"getinfo/"+"?"+"open=true"+"&"+"type"+"="+"bm");
		Document doc = vh.getDocument(url);//Get all modelInfos we have permission to access that match the url query
		String docStr = vh.documentToString(doc);//convert xml document to string
		System.out.println(docStr);//print sml as string
		NodeList si = (NodeList)doc.getElementsByTagName("modelInfo");
		double[] times = null;
		for(int i=0;i<si.getLength();i++){
			Node node = si.item(i);
			String user = node.getAttributes().getNamedItem("user").getNodeValue();
			if(user.equals("kjp15105")){//get modelInfos owned by user
				NodeList modelChildren = node.getChildNodes();
				TreeMap<String,double[]> mapAppVarToSum = new TreeMap();
				breakhere:
				for(int j=0;j<modelChildren.getLength();j++){
					Node modelChild = modelChildren.item(j);
					if(modelChild.getNodeName().equals("context") && modelChild.getAttributes().getNamedItem("name").getNodeValue().endsWith("NFSim")){//get applications (simulationContexts) with names ending in "NFSim"
						String contextName = modelChild.getAttributes().getNamedItem("name").getNodeValue();
						System.out.println(contextName/*modelChild.getAttributes().getNamedItem("name")*/);
						NodeList contextChildren = modelChild.getChildNodes();
						for(int k=0;k<contextChildren.getLength();k++){
							Node contextChild = contextChildren.item(k);
							if(contextChild.getNodeName().equals("simInfo")){
								String cacheKey = contextChild.getAttributes().getNamedItem("cacheKey").getNodeValue();
								System.out.println("context="+j+" sim="+k+" cacheKey"+cacheKey);
								Document varDoc = vh.getDocument(new URL("http://172.16.129.126:8010"+"/"+"getdata/"+"?"+"cachekey"+"="+cacheKey));//get variable names
								if(times == null){
									times = vh.getTimesFromVarInfos(varDoc);
								}
								NodeList varInfoNodeList = (NodeList)varDoc.getElementsByTagName("ijVarInfo");
								StringBuffer urlEncodedVarNames = new StringBuffer();
								for(int l=0;l<varInfoNodeList.getLength();l++){
									Node varInfoNode = varInfoNodeList.item(l);
									String varName = varInfoNode.getAttributes().getNamedItem("name").getNodeValue();
									if(varName.endsWith("_Count")){
										urlEncodedVarNames.append("&varname="+URLEncoder.encode(varName, Charset.forName("UTF-8").name()));
									}
								}
								URL dataUrl = new URL("http://172.16.129.126:8010"+"/getdata/?cachekey="+cacheKey+urlEncodedVarNames.toString());
								Document dataDoc = vh.getDocument(dataUrl);
//								printDocument(dataDoc, System.out);
//								System.out.println(dataDoc.getChildNodes().item(0).getChildNodes().item(1).getNodeName().equals("ijData"));
								NodeList ijDataNodes = (NodeList)dataDoc.getElementsByTagName("ijData");
								for(int l=0;l<ijDataNodes.getLength();l++){
									double[] data = vh.getData(ijDataNodes.item(l));
									String appVarName = contextName+":"+ijDataNodes.item(l).getAttributes().getNamedItem("varname").getNodeValue();
									double[] counts = mapAppVarToSum.get(appVarName);
									if(counts == null){
										counts = new double[data.length];
										Arrays.fill(counts,0.0);
										mapAppVarToSum.put(appVarName,counts);
									}
									for(int m=0;m<counts.length;m++){
										counts[m]+= data[m];
									}
								}
							}
						}
					}
				}
				for(double time:times){
					System.out.print(time+",");
				}
				System.out.println();
				for(String appVar:mapAppVarToSum.keySet()){
					double[] vals = mapAppVarToSum.get(appVar);
					System.out.print(appVar+" ");
					for(double val:vals){
						System.out.print(val+",");
					}
					System.out.println();
				}
			}
		}















//	        try {
//				if(VCellClientTest.getVCellClient() != null){
////					TreeMap<String, double[]> mapObsTpData = new TreeMap<>();
//					double[] times = null;
//					BioModelInfo[] bioModelInfos = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModelInfos();
//					for (int i = 0; i < bioModelInfos.length; i++) {
//						if(bioModelInfos[i].getVersion().getName().equals("EGFR Full_Model_Compart_v3")) {
//							System.out.println(bioModelInfos[i]);
//							BioModel bioModel = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModel(bioModelInfos[i]);
//							SimulationContext[] simulationContexts = bioModel.getSimulationContexts();
//							for (int j = 0; j < simulationContexts.length; j++) {
//								if(simulationContexts[j].getName().toLowerCase().contains("nfsim")) {
//									TreeMap<String, double[]> mapObsTpData = new TreeMap<>();
//									SimulationContext simulationContext = simulationContexts[j];
//									Simulation[] sims = simulationContext.getSimulations();
//									for (int k = 0; k < sims.length; k++) {
//										VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(sims[k].getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), 0);
//										ODESimData odeSimData = VCellClientTest.getVCellClient().getClientServerManager().getVCDataManager().getODEData(vcSimulationDataIdentifier);
//										for (int l = 0; l < odeSimData.getRowCount(); l++) {
//											ColumnDescription columnDescription = odeSimData.getColumnDescriptions(l);
////											System.out.println(columnDescription.getName()+"\n-----");
//											double[] rowData = odeSimData.extractColumn(l);
//											if(times == null && columnDescription.getName().toLowerCase().equals("t")) {
//												times = rowData;
//												System.out.print("'Model:App:Variable'");
//												for (int m = 0;m < times.length; m++) {
//													System.out.print((m!= 0?",":"")+(times[m]));
//												}
//												System.out.println();
//											}
//											String keyName = simulationContexts[j].getName()+":"+columnDescription.getName();
//											double[] accumData = mapObsTpData.get(keyName);
//											if(accumData == null) {
//												accumData = rowData;
//												mapObsTpData.put(keyName, accumData);
//											}
//											if(accumData.length != rowData.length) {
//												throw new Exception("Rows are different lengths");
//											}
//											for (int m = 0; m < rowData.length; m++) {
//												accumData[m]+= rowData[m];
//											}
////											System.out.println();
//										}
////										DataIdentifier[] dataIdentifiers = VCellClientTest.getVCellClient().getClientServerManager().getVCDataManager().getDataIdentifiers(null, vcSimulationDataIdentifier);
////										for (int l = 0; l < dataIdentifiers.length; l++) {
////											if(dataIdentifiers[l].getName().toLowerCase().endsWith("_count")){
////												VCellClientTest.getVCellClient().getClientServerManager().getVCDataManager().getODEData(dataIdentifiers[l]);
////											}
////										}
//									}
//									for(String s:mapObsTpData.keySet()) {
//										System.out.print("'"+/*bioModelInfos[i].getVersion().getName()+":"+simulationContexts[j].getName()+":"+*/s+"'");
//										double[] accumVals = mapObsTpData.get(s);
//										for (int k = 0; k < accumVals.length; k++) {
//											System.out.print((k!= 0?",":"")+(accumVals[k]/sims.length));
//										}
//										System.out.println();
//									}
//									System.out.println();
//								}
//							}
//						}
//					}
////	        	VCellClientTest.getVCellClient().getClientServerManager().getVCDataManager().
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
