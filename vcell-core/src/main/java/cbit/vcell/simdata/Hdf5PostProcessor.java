package cbit.vcell.simdata;

import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hdf5PostProcessor {

    enum StatisticType {
        average(0), total(1), min(2), max(3);
        private final int index;
        StatisticType(int index) {
            this.index = index;
        }
        public static StatisticType fromIndex(int index) {
            for (StatisticType statistic : StatisticType.values()) {
                if (statistic.index == index) {
                    return statistic;
                }
            }
            throw new IllegalArgumentException("No Statistic with index " + index);
        }

        public static StatisticType fromName(String name) {
            for (StatisticType statistic : StatisticType.values()) {
                if (statistic.name().equals(name)) {
                    return statistic;
                }
            }
            throw new IllegalArgumentException("No Statistic with name " + name);
        }
    }

    public record VariableInfo(int channel_index,
                        String stat_var_name,
                        StatisticType statisticType,
                        String unit,
                        int var_index,
                        String var_name) {}
    public static class ImageMetadata {
        String groupPath;
        String name;
        double[] extent;  // extentX, extentY, extentZ.  extentZ defaults to 1.0 for 2D images
        double[] origin;
        int[] shape;
        ImageMetadata(String groupPath, String name) {
            this.groupPath = groupPath;
            this.name = name;
        }

        Dataset getDataset(HdfFile hdfFile, int timeIndex) {
            return hdfFile.getDatasetByPath(groupPath + "time" + String.format("%06d", timeIndex));
        }

        void read(HdfFile hdfFile) {
            Dataset imageDataset_t0 = getDataset(hdfFile, 0);
            this.shape = imageDataset_t0.getDimensions();
            if (this.shape.length == 2){
                this.shape = new int[] {1, this.shape[1], this.shape[0]};
            } else if (this.shape.length == 3) {
                this.shape = new int[] {this.shape[2], this.shape[1], this.shape[0]};
            } else {
                throw new RuntimeException("Image dataset has unexpected number of dimensions: " + this.shape.length);
            }

            Group parentGroup = imageDataset_t0.getParent();
            Attribute extentX = parentGroup.getAttribute("ExtentX");
            Attribute extentY = parentGroup.getAttribute("ExtentY");
            Attribute extentZ = parentGroup.getAttribute("ExtentZ");
            Attribute originX = parentGroup.getAttribute("OriginX");
            Attribute originY = parentGroup.getAttribute("OriginY");
            Attribute originZ = parentGroup.getAttribute("OriginZ");
            double ex = (extentX != null) ? (Float)extentX.getData() : 0.0;
            double ey = (extentY != null) ? (Float)extentY.getData() : 0.0;
            double ez = (extentZ != null) ? (Float)extentZ.getData() : 1.0; // a vcell default, could be confusing
            double ox = (originX != null) ? (Float)originX.getData() : 0.0;
            double oy = (originY != null) ? (Float)originY.getData() : 0.0;
            double oz = (originZ != null) ? (Float)originZ.getData() : 0.0;
            this.extent = new double[] { ex, ey, ez };
            this.origin = new double[] { ox, oy, oz };
        }
    }

    public static class PostProcessing {
        Path postprocessingHdf5Path;
        double[] times;
        List<VariableInfo> variables;
        List<double[]> statistics_by_channel = new ArrayList<>();
        List<ImageMetadata> imageMetadata;

        public PostProcessing(Path postprocessingHdf5Path) {
            this.postprocessingHdf5Path = postprocessingHdf5Path;
            this.variables = new ArrayList<>();
            this.imageMetadata = new ArrayList<>();
        }

        public void read() {
            try (HdfFile hdfFile = new HdfFile(postprocessingHdf5Path.toFile())) {
                // Read dataset with path /PostProcessing/Times
                Dataset timesDataset = hdfFile.getDatasetByPath("/PostProcessing/Times");
                this.times = (double[]) timesDataset.getData();

                // Read attributes from group /PostProcessing/VariableStatistics
                Node postProcessing_node = hdfFile.getChild("PostProcessing");
                if (!(postProcessing_node instanceof Group postProcessing_group)) {
                    throw new RuntimeException("PostProcessing is not a group");
                }
                Node variableStatistics_node = postProcessing_group.getChild("VariableStatistics");
                if (!(variableStatistics_node instanceof Group variableStatistics_group)) {
                    throw new RuntimeException("VariableStatistics is not a group");
                }

                // read attributes of a variableStatistics_group
                Map<Integer,String> var_name_by_channel = new HashMap<>();
                Map<Integer,String> var_unit_by_channel = new HashMap<>();
                for (Attribute attribute : variableStatistics_group.getAttributes().values()) {
                    // attributes have keys like "comp_0_name" or "comp_0_unit" and values like "Ca" or "uM"
                    String[] parts = attribute.getName().split("_");
                    if (attribute.getName().endsWith("_name")) {
                        var_name_by_channel.put(Integer.parseInt(parts[1]), (String) attribute.getData());
                    } else if (attribute.getName().endsWith("_unit")) {
                        var_unit_by_channel.put(Integer.parseInt(parts[1]), (String) attribute.getData());
                    }
                }
                this.variables = new ArrayList<>();
                for (int channel : var_name_by_channel.keySet()) {
                    String stat_var_name = var_name_by_channel.get(channel);
                    String unit = var_unit_by_channel.get(channel);
                    int var_index = channel / 4;  // because there are 4 statistics per variable
                    String var_name = stat_var_name.substring(0, stat_var_name.lastIndexOf("_"));
                    String statistic_name = stat_var_name.substring(stat_var_name.lastIndexOf("_") + 1);
                    StatisticType statisticType = StatisticType.fromName(statistic_name);
                    this.variables.add(new VariableInfo(channel, stat_var_name, statisticType, unit, var_index, var_name));
                }

                // read VariableStatistics datasets for each time
                List<double[]> statistics_by_time = new ArrayList<>();
                for (int time_index=0; time_index<times.length; time_index++) {
                    // Read dataset with path /PostProcessing/VariableStatistics/time000000
                    Dataset variableStatisticsDataset = hdfFile.getDatasetByPath("/PostProcessing/VariableStatistics/time" + String.format("%06d", time_index));
                    statistics_by_time.add((double[])variableStatisticsDataset.getData());
                }
                // transpose statistics_by_time into statistics_by_channel
                for (int channel=0; channel<variables.size(); channel++) {
                    double[] statistics = new double[times.length];
                    for (int time_index=0; time_index<times.length; time_index++) {
                        statistics[time_index] = statistics_by_time.get(time_index)[channel];
                    }
                    this.statistics_by_channel.add(statistics);
                }

                // Get list of child groups from /PostProcessing which are not Times or VariableStatistics
                for (Node node : postProcessing_group.getChildren().values()) {
                    if (node instanceof Group group) {
                        if (group.getName().equals("Times") || group.getName().equals("VariableStatistics")) {
                            continue;
                        }
                        ImageMetadata imageMetadata = new ImageMetadata(group.getPath(), group.getName());
                        imageMetadata.read(hdfFile);
                        this.imageMetadata.add(imageMetadata);
                    }
                }
            }
        }

        public List<VariableInfo> getVariables() {
            return this.variables;
        }

        public double[] readVariableData(VariableInfo variableInfo) {
            return this.statistics_by_channel.get(variableInfo.channel_index);
        }

        /**
         * Read image data from the HDF5 file
         * @return double[][][] 3D array of doubles (z, y, x) z=1 for 2D images
         */
        public double[][][] readImageData(ImageMetadata imageMetadata, int timeIndex) {
            try (HdfFile hdfFile = new HdfFile(postprocessingHdf5Path.toFile())) {
                Dataset imageDataset = imageMetadata.getDataset(hdfFile, timeIndex);
                if (imageDataset.getDimensions().length == 2) {
                    return new double[][][] { (double[][]) imageDataset.getData() };
                } else if (imageDataset.getDimensions().length == 3) {
                    return (double[][][]) imageDataset.getData();
                } else {
                    throw new RuntimeException("Image dataset has unexpected number of dimensions: " + imageDataset.getDimensions().length);
                }
            }
        }
    }
}
