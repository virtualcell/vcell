package org.vcell.restq.models;

public record PublishModelsRequest(
        Long[] biomodelKeys,
        Long[] mathmodelKeys
) {}
