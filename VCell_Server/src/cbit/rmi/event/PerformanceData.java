package cbit.rmi.event;
public class PerformanceData implements java.io.Serializable {
		private String methodName;
		private int dataType;
		PerformanceDataEntry[] entries;
		public PerformanceData(String methodName, int dataType, PerformanceDataEntry[] entries) {
			this.methodName = methodName;
			this.dataType = dataType;
			this.entries = entries;
		}
		public String getMethodName() {
			return methodName;
		}
		public int getDataType() {
			return dataType;
		}
		public PerformanceDataEntry[] getEntries() {
			return entries;
		}
	}