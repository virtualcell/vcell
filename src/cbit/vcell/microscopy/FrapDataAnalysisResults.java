/**
 * 
 */
package cbit.vcell.microscopy;

import cbit.util.Matchable;
import cbit.vcell.parser.Expression;

/**
 */
public class FrapDataAnalysisResults implements Matchable {
	public static int BleachType_CirularDisk = 0;
	public static int BleachType_GaussianSpot = 1;
	public static int BleachType_HalfCell = 2;
	public static final String[] BLEACH_TYPE_NAMES =
		new String[] {"Circular Disk","Gaussian Spot","Half Cell"};
	
	private Double recoveryTau = null;
	private Double bleachWidth = null;
	private Double bleachWhileMonitoringTau = null;
	private Double recoveryDiffusionRate = null;
	private Double mobilefraction = null;
	private Integer startingIndexForRecovery = null;
	private Integer bleachType = null;
	private Expression fitExpression = null;
	private double[] bleachRegionData = null;
	private Expression fitBleachWhileMonitorExpression = null;
	private double[] cellRegionData = null;
	
	private Double slowerRate = null;
	
	public FrapDataAnalysisResults() {
	}

	public static final int getBleachTypeFromBleachTypeName(String bleachTypeName){
		for (int i = 0; i < BLEACH_TYPE_NAMES.length; i++) {
			if(BLEACH_TYPE_NAMES[i].equals(bleachTypeName)){
				return i;
			}
		}
		throw new IllegalArgumentException("Unknown bleach type name "+bleachTypeName);
	}
	/**
	 * Method getBleachWhileMonitoringTau.
	 * @return Double
	 */
	public Double getBleachWhileMonitoringTau() {
		return bleachWhileMonitoringTau;
	}

	/**
	 * Method setBleachWhileMonitoringTau.
	 * @param bleachWhileMonitoringTau Double
	 */
	public void setBleachWhileMonitoringTau(Double bleachWhileMonitoringTau) {
		this.bleachWhileMonitoringTau = bleachWhileMonitoringTau;
	}

	/**
	 * Method getBleachWidth.
	 * @return Double
	 */
	public Double getBleachWidth() {
		return bleachWidth;
	}

	/**
	 * Method setBleachWidth.
	 * @param bleachWidth Double
	 */
	void setBleachWidth(Double bleachWidth) {
		this.bleachWidth = bleachWidth;
	}

	/**
	 * Method getRecoveryDiffusionRate.
	 * @return Double
	 */
	public Double getRecoveryDiffusionRate() {
		return recoveryDiffusionRate;
	}

	/**
	 * Method setRecoveryDiffusionRate.
	 * @param recoveryDiffusionRate Double
	 */
	public void setRecoveryDiffusionRate(Double recoveryDiffusionRate) {
		this.recoveryDiffusionRate = recoveryDiffusionRate;
	}

	/**
	 * Method getRecoveryTau.
	 * @return Double
	 */
	public Double getRecoveryTau() {
		return recoveryTau;
	}

	/**
	 * Method setRecoveryTau.
	 * @param recoveryTau Double
	 */
	void setRecoveryTau(Double recoveryTau) {
		this.recoveryTau = recoveryTau;
	}

	/**
	 * Method getFitExpression.
	 * @return Expression
	 */
	public Expression getFitExpression() {
		return fitExpression;
	}

	/**
	 * Method setFitExpression.
	 * @param fitExpression Expression
	 */
	void setFitExpression(Expression fitExpression) {
		this.fitExpression = fitExpression;
	}

	/**
	 * Method getStartingIndexForRecovery.
	 * @return Integer
	 */
	public Integer getStartingIndexForRecovery() {
		return startingIndexForRecovery;
	}

	/**
	 * Method setStartingIndexForRecovery.
	 * @param startingIndexForRecovery Integer
	 */
	void setStartingIndexForRecovery(Integer startingIndexForRecovery) {
		this.startingIndexForRecovery = startingIndexForRecovery;
	}

	/**
	 * Method getBleachRegionData.
	 * @return double[]
	 */
	public double[] getBleachRegionData() {
		return bleachRegionData;
	}

	/**
	 * Method setBleachRegionData.
	 * @param bleachRegionData double[]
	 */
	void setBleachRegionData(double[] bleachRegionData) {
		this.bleachRegionData = bleachRegionData;
	}

	public Double getMobilefraction() {
		return mobilefraction;
	}

	public void setMobilefraction(Double mobilefraction) {
		this.mobilefraction = mobilefraction;
	}

	public Integer getBleachType() {
		return bleachType;
	}

	public void setBleachType(Integer bleachType) {
		this.bleachType = bleachType;
	}

	public boolean compareEqual(Matchable obj) {
		
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof FrapDataAnalysisResults)
		{
			FrapDataAnalysisResults fdar = (FrapDataAnalysisResults)obj;
			if((getRecoveryTau()!= null && fdar.getRecoveryTau() == null) || (getRecoveryTau()== null && fdar.getRecoveryTau() != null))
			{
				return false;
			}
			if(getRecoveryTau()!= null && fdar.getRecoveryTau()!= null && getRecoveryTau().doubleValue() != fdar.getRecoveryTau().doubleValue())
			{
				return false;
			}
			if((getBleachWidth()!= null && fdar.getBleachWidth() == null) || (getBleachWidth()== null && fdar.getBleachWidth() != null))
			{
				return false;
			}
			if(getBleachWidth()!= null && fdar.getBleachWidth()!= null && getBleachWidth().doubleValue() != fdar.getBleachWidth().doubleValue())
			{
				return false;
			}
			if((getBleachWhileMonitoringTau()!= null && fdar.getBleachWhileMonitoringTau() == null) || (getBleachWhileMonitoringTau()== null && fdar.getBleachWhileMonitoringTau() != null))
			{
				return false;
			}
			if(getBleachWhileMonitoringTau()!= null && fdar.getBleachWhileMonitoringTau()!= null && getBleachWhileMonitoringTau().doubleValue() != fdar.getBleachWhileMonitoringTau().doubleValue())
			{
				return false;
			}
			if((getRecoveryDiffusionRate()!= null && fdar.getRecoveryDiffusionRate() == null) || (getRecoveryDiffusionRate()== null && fdar.getRecoveryDiffusionRate() != null))
			{
				return false;
			}
			if(getRecoveryDiffusionRate()!= null && fdar.getRecoveryDiffusionRate()!= null && getRecoveryDiffusionRate().doubleValue() != fdar.getRecoveryDiffusionRate().doubleValue())
			{
				return false;
			}
			if((getMobilefraction()!= null && fdar.getMobilefraction() == null) || (getMobilefraction()== null && fdar.getMobilefraction() != null))
			{
				return false;
			}
			if(getMobilefraction()!= null && fdar.getMobilefraction()!= null && getMobilefraction().doubleValue() != fdar.getMobilefraction().doubleValue())
			{
				return false;
			}
			if((getStartingIndexForRecovery()!= null && fdar.getStartingIndexForRecovery() == null) || (getStartingIndexForRecovery()== null && fdar.getStartingIndexForRecovery() != null))
			{
				return false;
			}
			if(getStartingIndexForRecovery()!= null && fdar.getStartingIndexForRecovery()!= null && getStartingIndexForRecovery().intValue() != fdar.getStartingIndexForRecovery().intValue())
			{
				return false;
			}
			if((getBleachType()!= null && fdar.getBleachType() == null) || (getBleachType()== null && fdar.getBleachType() != null))
			{
				return false;
			}
			if(getBleachType()!= null && fdar.getBleachType()!= null && getBleachType().intValue() != fdar.getBleachType().intValue())
			{
				return false;
			}
			
			if(!cbit.util.Compare.isEqualOrNull(getFitExpression(), fdar.getFitExpression()))
			{
				return false;
			}
			if(!cbit.util.Compare.isEqualOrNull(getBleachRegionData(), fdar.getBleachRegionData()))
			{
				return false;
			}
			if(!cbit.util.Compare.isEqualOrNull(getFitBleachWhileMonitorExpression(), fdar.getFitBleachWhileMonitorExpression())){
				return false;
			}
			if(!cbit.util.Compare.isEqualOrNull(getCellRegionData(), fdar.getCellRegionData())){
				return false;
			}

			return true;
		}
		return false;	
	}

	public Double getSlowerRate() {
		return slowerRate;
	}

	public void setSlowerRate(Double slowerRate) {
		this.slowerRate = slowerRate;
	}

	public Expression getFitBleachWhileMonitorExpression() {
		return fitBleachWhileMonitorExpression;
	}

	public void setFitBleachWhileMonitorExpression(
			Expression fitBleachWhileMonitorExpression) {
		this.fitBleachWhileMonitorExpression = fitBleachWhileMonitorExpression;
	}

	public double[] getCellRegionData() {
		return cellRegionData;
	}

	public void setCellRegionData(double[] cellRegionData) {
		this.cellRegionData = cellRegionData;
	}

}