package org.vcell.api.dto;

public record N5Export(String format, String simulationId, int compressionLevel) implements ExportRequest {
    public N5Export(String simulationId, int compressionLevel) { this("n5", simulationId, compressionLevel); }
}
