srcImage = "Sim fluor-1"
ij.IJ.selectWindow(srcImage)
imp = ij.IJ.getImage()
ij.IJ.resetMinAndMax(imp)
ij.IJ.run(imp, "Select All", "");
ij.IJ.run(imp, "Copy", "");
ij.IJ.run(imp, "Add Slice", "");
ij.IJ.run(imp, "Paste", "");
imp.setSlice(1);
ij.IJ.run(imp, "Clear", "slice");

expImage = "abp-1"
ij.IJ.selectWindow(expImage)
ij.IJ.run(imp, "32-bit", "");
expimp = ij.IJ.getImage()
ij.IJ.resetMinAndMax(expimp)

/*
imp.setRoi(51,0,45,88);
imp2 = imp.crop("stack");
imp2.setTitle("rhs")
imp2.show()
ij.IJ.run(imp2, "RGB Color", "");

ij.IJ.selectWindow(srcImage)
ij.IJ.run(imp, "Make Inverse", "");
imp3 = imp.crop("stack");
imp3.setTitle("lhs")
imp3.show()
ij.IJ.run(imp3, "ICA", "");
ij.IJ.run(imp3, "RGB Color", "");
ij.IJ.run("Combine...", "stack1=lhs stack2=rhs");
ij.IJ.getImage().setTitle("fluorProc")
ij.IJ.run(expimp, "RGB Color", "");
*/

//imp3.setSlice(26);
//ij.IJ.selectWindow("lhs")
//ij.IJ.setTool("wand");
//ij.IJ.doWand(imp3, 10, 13, 0.0, "Legacy");
//ij.IJ.setBackgroundColor(0, 0, 0);
//ij.IJ.run(imp3, "Clear", "stack");
//if(true){return}
//IJ.run(imp, "Flip Horizontally", "stack");

//ij.IJ.run(expimp, "RGB Color", "");
ij.IJ.selectWindow(srcImage)
//ij.IJ.run(imp, "RGB Color", "");
ij.IJ.run(imp, "Translate...", "x=0 y=-5 interpolation=None stack");
ij.IJ.run("Combine...", "stack1='Sim fluor-1' stack2=abp-1");


/*


import ij.measure.ResultsTable
import ij.plugin.frame.RoiManager
import ij.gui.Roi

	RoiManager roiManager = RoiManager.getRoiManager()
	roiManager.reset()
//roiManager.runCommand("set line width", "0")
lhsRoi = new Roi(0,0,96,88)
temp = roiManager.addRoi(lhsRoi)
rhsRoi = new Roi(96,0,96,88)
temp = roiManager.addRoi(rhsRoi)
//ij.IJ.selectWindow("Combined Stacks")
//roiManager.select(0);
//ResultsTable rtlhs = roiManager.multiMeasure(ij.IJ.getImage());
//rtlhs.show("Results");
//ij.IJ.run("Summarize", "");
//if(true){return}
//ij.IJ.selectWindow("Combined Stacks")
//roiManager.select(1);
//ResultsTable rtrhs = roiManager.multiMeasure(ij.IJ.getImage());
//rtrhs.show("Results");
//ij.IJ.run("Summarize", "");
//if(true){return}
max = findMax(roiManager,0)
ij.IJ.run(ij.IJ.getImage(), "Divide...", "value="+max+" stack");
max = findMax(roiManager,1)
ij.IJ.run(ij.IJ.getImage(), "Divide...", "value="+max+" stack");

//roix = 55
//roiy = 55
//roixs = 8;
//roiys = 8
//roi1 = new Roi(roix,roiy,roixs,roiys)
//temp = roiManager.addRoi(roi1)
//roi2 = new Roi(roix+88,roiy,roixs,roiys)
//temp = roiManager.addRoi(roi2)


def double findMax(RoiManager roiManager,int roiIndex){
ij.IJ.selectWindow("Combined Stacks-1")
roiManager.select(roiIndex);
ResultsTable rt = roiManager.multiMeasure(ij.IJ.getImage());
rt.show("Results");
ij.IJ.run("Summarize", "");
rowLabels = rt.getRowLabels()
for(int i=0;i<rowLabels.length;i++){
	if(rowLabels[i] != null && rowLabels[i] instanceof String && rowLabels[i].equals("Max")){
		println(rt.getValue("Max1",i))
		return rt.getValue("Max1",i)
	}
}
}


*/