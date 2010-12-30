/**
 * 
 */
package cbit.vcell.microscopy;

import cbit.vcell.parser.Expression;

/**
 */
public class FrapDataAnalysisResults /*implements Matchable*/ 
{
	//nested class
	//for diffusion only, explicit expression fitting
	public static class DiffusionOnlyAnalysisRestults 
	{
		//public static int BleachType_CirularDisk = 0;
		public static int BleachType_GaussianSpot = 0;
		public static int BleachType_HalfCell = 1;
		public static final String[] BLEACH_TYPE_NAMES = new String[] {/*"Circular Disk",*/"Gaussian Spot","Half Cell"};
		
		private Double recoveryTau = null;
		private Double bleachWhileMonitoringTau = null;
		private Double recoveryDiffusionRate = null;
		private Double mobilefraction = null;
		private Integer bleachType = null;
		private Expression diffFitExpression = null;
		private Expression fitBleachWhileMonitorExpression = null;
		
		public static final int getBleachTypeFromBleachTypeName(String bleachTypeName){
			for (int i = 0; i < BLEACH_TYPE_NAMES.length; i++) {
				if(BLEACH_TYPE_NAMES[i].equals(bleachTypeName)){
					return i;
				}
			}
			throw new IllegalArgumentException("Unknown bleach type name "+bleachTypeName);
		}
		
		public Double getBleachWhileMonitoringTau() {
			return bleachWhileMonitoringTau;
		}

		public void setBleachWhileMonitoringTau(Double bleachWhileMonitoringTau) {
			this.bleachWhileMonitoringTau = bleachWhileMonitoringTau;
		}

		public Double getRecoveryDiffusionRate() {
			return recoveryDiffusionRate;
		}

		public void setRecoveryDiffusionRate(Double recoveryDiffusionRate) {
			this.recoveryDiffusionRate = recoveryDiffusionRate;
		}

		public Double getRecoveryTau() {
			return recoveryTau;
		}

		void setRecoveryTau(Double recoveryTau) {
			this.recoveryTau = recoveryTau;
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

		public Expression getFitBleachWhileMonitorExpression() {
			return fitBleachWhileMonitorExpression;
		}

		public void setFitBleachWhileMonitorExpression(
				Expression fitBleachWhileMonitorExpression) {
			this.fitBleachWhileMonitorExpression = fitBleachWhileMonitorExpression;
		}
		
		public Expression getDiffFitExpression() {
			return diffFitExpression;
		}

		public void setDiffFitExpression(Expression fitExpression) {
			this.diffFitExpression = fitExpression;
		}
	}
	//nested class
	//for reaction only, explicit expressions fitting
	public static class ReactionOnlyAnalysisRestults 
	{
		private Double fittingParamA = null;
		private Double offRate = null;
		private Expression offRateFitExpression = null;
		private Double bleachWhileMonitoringTau = null;
		private Expression fitBleachWhileMonitorExpression = null;
		
		public Double getBleachWhileMonitoringTau() {
			return bleachWhileMonitoringTau;
		}

		public void setBleachWhileMonitoringTau(Double bleachWhileMonitoringTau) {
			this.bleachWhileMonitoringTau = bleachWhileMonitoringTau;
		}

		public Expression getFitBleachWhileMonitorExpression() {
			return fitBleachWhileMonitorExpression;
		}

		public void setFitBleachWhileMonitorExpression(
				Expression fitBleachWhileMonitorExpression) {
			this.fitBleachWhileMonitorExpression = fitBleachWhileMonitorExpression;
		}
		
		public Expression getOffRateFitExpression() {
			return offRateFitExpression;
		}

		public void setOffRateFitExpression(Expression offRateFitExpression) {
			this.offRateFitExpression = offRateFitExpression;
		}
		public Double getFittingParamA() {
			return fittingParamA;
		}

		public void setFittingParamA(Double fittingParamA) {
			this.fittingParamA = fittingParamA;
		}

		public Double getOffRate() {
			return offRate;
		}

		public void setOffRate(Double offRate) {
			this.offRate = offRate;
		}
	}
	
	

	
	
//	public boolean compareEqual(Matchable obj) {
//		
//		if (this == obj) {
//			return true;
//		}
//		if (obj != null && obj instanceof FrapDataAnalysisResults)
//		{
//			FrapDataAnalysisResults fdar = (FrapDataAnalysisResults)obj;
//			if((getRecoveryTau()!= null && fdar.getRecoveryTau() == null) || (getRecoveryTau()== null && fdar.getRecoveryTau() != null))
//			{
//				return false;
//			}
//			if(getRecoveryTau()!= null && fdar.getRecoveryTau()!= null && getRecoveryTau().doubleValue() != fdar.getRecoveryTau().doubleValue())
//			{
//				return false;
//			}
//			if((getBleachWhileMonitoringTau()!= null && fdar.getBleachWhileMonitoringTau() == null) || (getBleachWhileMonitoringTau()== null && fdar.getBleachWhileMonitoringTau() != null))
//			{
//				return false;
//			}
//			if(getBleachWhileMonitoringTau()!= null && fdar.getBleachWhileMonitoringTau()!= null && getBleachWhileMonitoringTau().doubleValue() != fdar.getBleachWhileMonitoringTau().doubleValue())
//			{
//				return false;
//			}
//			if((getRecoveryDiffusionRate()!= null && fdar.getRecoveryDiffusionRate() == null) || (getRecoveryDiffusionRate()== null && fdar.getRecoveryDiffusionRate() != null))
//			{
//				return false;
//			}
//			if(getRecoveryDiffusionRate()!= null && fdar.getRecoveryDiffusionRate()!= null && getRecoveryDiffusionRate().doubleValue() != fdar.getRecoveryDiffusionRate().doubleValue())
//			{
//				return false;
//			}
//			if((getMobilefraction()!= null && fdar.getMobilefraction() == null) || (getMobilefraction()== null && fdar.getMobilefraction() != null))
//			{
//				return false;
//			}
//			if(getMobilefraction()!= null && fdar.getMobilefraction()!= null && getMobilefraction().doubleValue() != fdar.getMobilefraction().doubleValue())
//			{
//				return false;
//			}
//			if(!Compare.isEqualOrNull(getDiffFitExpression(), fdar.getDiffFitExpression()))
//			{
//				return false;
//			}
//			if(!Compare.isEqualOrNull(getFitBleachWhileMonitorExpression(), fdar.getFitBleachWhileMonitorExpression())){
//				return false;
//			}
//			if((getBleachType()!= null && fdar.getBleachType() == null) || (getBleachType()== null && fdar.getBleachType() != null))
//			{
//				return false;
//			}
//			if(getBleachType()!= null && fdar.getBleachType()!= null && getBleachType().intValue() != fdar.getBleachType().intValue())
//			{
//				return false;
//			}
//			if((getFittingParamA()!= null && fdar.getFittingParamA() == null) || (getFittingParamA() == null && fdar.getFittingParamA() != null))
//			{
//				return false;
//			}
//			if(getFittingParamA()!= null && fdar.getFittingParamA()!= null && getFittingParamA().intValue() != fdar.getFittingParamA().intValue())
//			{
//				return false;
//			}
//			if((getOffRate() != null && fdar.getOffRate() == null) || (getOffRate() == null && fdar.getOffRate() != null))
//			{
//				return false;
//			}
//			if(getOffRate()!= null && fdar.getOffRate()!= null && getOffRate().intValue() != fdar.getOffRate().intValue())
//			{
//				return false;
//			}
//			return true;
//		}
//		return false;	
//	}
}