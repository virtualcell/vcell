package cbit.vcell.field.gui;

import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataIdentifier;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;

class FieldDataGUIDataTransferObjects {


    static class FieldDataIDList {
        //		public KeyValue key = null;
        private final String descr;

        public FieldDataIDList(KeyValue k) {
//			this.key = k;
            this.descr = "Key (" + k + ")";
        }

        public String toString() {
            return descr;
        }
    }

    static class FieldDataISizeList {
        public ISize isize;
        private final String descr;

        public FieldDataISizeList(ISize arg) {
            isize = arg;
            descr = "Size ( " +
                    isize.getX() + " , " +
                    isize.getY() + " , " +
                    isize.getZ() + " )";
        }

        public String toString() {
            return descr;
        }
    }

    static class FieldDataMainList {
        public ExternalDataIdentifier externalDataIdentifier;
        public String extDataAnnot;

        public FieldDataMainList(ExternalDataIdentifier argExternalDataIdentifier, String argExtDataAnnot) {
            externalDataIdentifier = argExternalDataIdentifier;
            extDataAnnot = argExtDataAnnot;
        }

        public String toString() {
            return externalDataIdentifier.getName();
        }
    }

    static class FieldDataOriginList {
        public Origin origin;
        private final String descr;

        public FieldDataOriginList(Origin arg) {
            origin = arg;
            descr = "Origin ( " +
                    origin.getX() + " , " +
                    origin.getY() + " , " +
                    origin.getZ() + " )";
        }

        public String toString() {
            return descr;
        }
    }

    static class FieldDataTimeList {
        public double[] times;
        private final String descr;

        public FieldDataTimeList(double[] argTimes) {
            times = argTimes;
            descr =
                    "Times ( " + times.length + " )   Begin=" + times[0] +
                            "   End=" + times[times.length - 1];
        }

        public String toString() {
            return descr;
        }
    }

    static class FieldDataVarList {
        public DataIdentifier dataIdentifier;
        private final String descr;

        public FieldDataVarList(DataIdentifier argDataIdentifier) {
            dataIdentifier = argDataIdentifier;
            if (dataIdentifier.getVariableType().compareEqual(VariableType.VOLUME) ||
                    dataIdentifier.getVariableType().compareEqual(VariableType.VOLUME_REGION)) {
                descr = "(Vol" + (dataIdentifier.isFunction() ? "Fnc" : "") + ") " + dataIdentifier.getName();
            } else if (dataIdentifier.getVariableType().compareEqual(VariableType.MEMBRANE) ||
                    dataIdentifier.getVariableType().compareEqual(VariableType.MEMBRANE_REGION)) {
                descr = "(Mem" + (dataIdentifier.isFunction() ? "Fnc" : "") + ") " + dataIdentifier.getName();
            } else {
                descr = "(---" + (dataIdentifier.isFunction() ? "Fnc" : "") + ") " + dataIdentifier.getName();
            }
        }

        public String toString() {
            return descr;
        }
    }

    static class FieldDataVarMainList {
        public static final String LIST_NODE_VAR_LABEL = "Variables";

        public FieldDataVarMainList() {
        }

        public String toString() {
            return LIST_NODE_VAR_LABEL;
        }
    }

    static class InitializedRootNode {
        private final String rootNodeString;

        public InitializedRootNode(String argRNS) {
            rootNodeString = argRNS;
        }

        public String toString() {
            return rootNodeString;
        }
    }

    static class FieldDataExtentList {
        public Extent extent;
        private final String descr;

        public FieldDataExtentList(Extent arg) {
            extent = arg;
            descr = "Extent ( " +
                    extent.getX() + " , " +
                    extent.getY() + " , " +
                    extent.getZ() + " )";
        }

        public String toString() {
            return descr;
        }
    }


}
