package cbit.vcell.export.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HumanReadableExportData implements Serializable {
    public record DifferentParameterValues(
            String parameterName,
            String originalValue,
            String changedValue
    ){
        public static List<DifferentParameterValues> fromJSON(String json) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<List<DifferentParameterValues>>() {});
        }
    }

    public final String simulationName;
    public final String biomodelName;
    public final String applicationName;
    public List<DifferentParameterValues> differentParameterValues;
    public String applicationType;
    // File name that is saved by the user or server. In N5 case it'll be the dataset name. This way individual datasets can be automatically opened
    public String serverSavedFileName;
    public boolean nonSpatial;
    public Map<Integer, String> subVolume;
    public int zSlices;
    public int tSlices;
    public int numChannels;

    @JsonCreator
    public HumanReadableExportData(@JsonProperty("simulationName") String simulationName, @JsonProperty("applicationName") String applicationName,
                                   @JsonProperty("biomodelName") String biomodelName, @JsonProperty("differentParameterValues") List<DifferentParameterValues> differentParameterValues,
                                   @JsonProperty("serverSavedFileName") String serverSavedFileName, @JsonProperty("applicationType") String applicationType,
                                   @JsonProperty("nonSpatial") boolean nonSpatial, @JsonProperty("subVolume") Map<Integer, String> subVolume){
        this.simulationName = simulationName;
        this.applicationName = applicationName;
        this.biomodelName = biomodelName;
        this.differentParameterValues = differentParameterValues;
        this.serverSavedFileName = serverSavedFileName;
        this.applicationType = applicationType;
        this.nonSpatial = nonSpatial;
        this.subVolume = subVolume;
    }
}
