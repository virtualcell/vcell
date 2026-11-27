package org.vcell.api.dto;

import java.time.OffsetDateTime;
public record BioModelSummary(String bioModelId, String name, UserRef owner, OffsetDateTime savedAt, Curation curation) { }
