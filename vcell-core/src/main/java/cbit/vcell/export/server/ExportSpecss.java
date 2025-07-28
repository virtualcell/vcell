package cbit.vcell.export.server;

import org.vcell.util.document.VCDataIdentifier;

public abstract class ExportSpecss {

    public record ExportRequest(
            VCDataIdentifier dataIdentifier,
            ExportFormat format,
            TimeSpecs timeSpecs,
            VariableSpecs variableSpecs,
            GeometrySpecs geometrySpecs,
            FormatSpecificSpecs formatSpecificSpecs,
            String simulationName,
            String contextName
    ){ }


    public enum GeometryMode{
        GEOMETRY_SELECTIONS(0),
        GEOMETRY_SLICE(1),
        GEOMETRY_FULL(2);

        public final int intValue;
        public static GeometryMode getGeometryMode(int intValue) {
            switch (intValue) {
                case 0: return GEOMETRY_SELECTIONS;
                case 1: return GEOMETRY_SLICE;
                case 2: return GEOMETRY_FULL;
            }
            throw new IllegalArgumentException();
        }
        GeometryMode(int i){
            intValue = i;
        }
    }

    public enum TimeMode{
        TIME_POINT(0),
        TIME_RANGE(1);

        public final int intValue;
        public static TimeMode getTimeMode(int i){
            switch(i){
                case 0: return TIME_POINT;
                case 1: return TIME_RANGE;
            }
            throw new IllegalArgumentException();
        }
        TimeMode(int i){
            intValue = i;
        }
    }

    public enum VariableMode{
        VARIABLE_ONE(0),
        VARIABLE_MULTI(1),
        VARIABLE_ALL(2);

        public final int intValue;
        public static VariableMode getVariableMode(int i){
            switch(i){
                case 0: return VARIABLE_ONE;
                case 1: return VARIABLE_MULTI;
                case 2: return VARIABLE_ALL;
            }
            throw new IllegalArgumentException();
        }

        VariableMode(int i){
            intValue = i;
        }
    }

    public enum RasterFormats{
        NRRD_SINGLE(0),
        NRRD_BY_TIME(1),
        NRRD_BY_VARIABLE(2);

        public final int intValue;
        RasterFormats(int i){
            intValue = i;
        }
    }

    public enum CompressionFormats{
        UNCOMPRESSED(0),
        COMPRESSED_GIF_DEFAULT(1),
        COMPRESSED_LZW(2),
        COMPRESSED_JPEG_DEFAULT(3);

        public final int intValue;
        CompressionFormats(int i){
            intValue = i;
        }
    }

    public enum ExportableDataType {
        ODE_VARIABLE_DATA,
        PDE_VARIABLE_DATA,
        PDE_PARTICLE_DATA
    }

    public enum SimulationDataType {
        NO_DATA_AVAILABLE(0),
        ODE_SIMULATION(1),
        PDE_SIMULATION_NO_PARTICLES(2),
        PDE_SIMULATION_WITH_PARTICLES(3);

        public final int intValue;
        SimulationDataType(int i){
            intValue = i;
        }
    }

    public enum ExportProgressType {
        EXPORT_START(1008),
        EXPORT_COMPLETE(1007),
        EXPORT_FAILURE(1006),
        EXPORT_ASSEMBLING(1005),
        EXPORT_PROGRESS(1004);

        public static ExportProgressType getExportProgressType(int i) {
            switch (i) {
                case 1008: return EXPORT_START;
                case 1007: return EXPORT_COMPLETE;
                case 1006: return EXPORT_FAILURE;
                case 1005: return EXPORT_ASSEMBLING;
                case 1004: return EXPORT_PROGRESS;
            }
            throw new IllegalArgumentException("Unknown ExportProgressType int value: " + i);
        }

        public final int intValue;
        ExportProgressType(int i){
            intValue = i;
        }
    }

    public enum MirroringMethod {
        NO_MIRRORING(0),
        MIRROR_LEFT(1),
        MIRROR_TOP(2),
        MIRROR_RIGHT(3),
        MIRROR_BOTTOM(4);

        public static MirroringMethod getMirroringMethod(int value) {
            MirroringMethod[] values = MirroringMethod.values();
            return values[value];
        }

        public final int value;
        MirroringMethod(int value) {
            this.value = value;
        }
    }
}
