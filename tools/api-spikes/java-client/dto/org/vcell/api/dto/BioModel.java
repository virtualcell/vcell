package org.vcell.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
public record BioModel(String bioModelId, String name, String description, UserRef owner, OffsetDateTime savedAt,
                       Curation curation, List<Application> applications) { }
