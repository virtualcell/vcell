package org.vcell.api.dto;

import java.util.List;
public record CsvExport(String format, String simulationId, List<String> variables) implements ExportRequest {
    public CsvExport(String simulationId, List<String> variables) { this("csv", simulationId, variables); }
}
