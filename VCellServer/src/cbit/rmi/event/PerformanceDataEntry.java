package cbit.rmi.event;
public class PerformanceDataEntry implements java.io.Serializable {
		// just a generic pair
		private String identifier;
		private String value;
		public PerformanceDataEntry(String identifier, String value) {
			this.identifier = identifier;
			this.value = value;
		}
		public String getIdentifier() {
			return identifier;
		}
		public String getValue() {
			return value;
		}
	}
