package cbit.image;

import org.vcell.util.ISize;

public class ImageSizeInfo{
	private String imagePath;
	private ISize iSize;
	private int numChannels;
	private double[] timePoints;
	private Integer selectedTimeIndex;
	public ImageSizeInfo(String imagePath, ISize iSize, int numChannels,double[] timePoints,Integer selectedTimeIndex) {
		super();
		this.imagePath = imagePath;
		this.iSize = iSize;
		this.numChannels = numChannels;
		this.timePoints = timePoints;
		this.selectedTimeIndex = selectedTimeIndex;
	}
	public String getImagePath() {
		return imagePath;
	}
	public ISize getiSize() {
		return iSize;
	}
	public int getNumChannels() {
		return numChannels;
	}
	public double[] getTimePoints(){
		return timePoints;
	}
	public Integer getSelectedTimeIndex(){
		return selectedTimeIndex;
	}
	
}