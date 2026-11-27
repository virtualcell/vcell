package org.vcell.api.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** One concept, two variants (style guide 1.3): a discriminated union. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "format", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = CsvExport.class, name = "csv"), @JsonSubTypes.Type(value = N5Export.class, name = "n5") })
public sealed interface ExportRequest permits CsvExport, N5Export {
    String format();
}
