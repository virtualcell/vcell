package org.vcell.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

/** Values may be added in later versions (guide 6.1); an older client reads an unrecognized value as UNKNOWN. */
public enum Curation {
    CURRENT, ARCHIVED, PUBLISHED,
    UNKNOWN;

    @JsonCreator
    public static Curation fromWire(String value) {
        for (Curation c : values()) {
            if (c != UNKNOWN && c.name().equals(value)) {
                return c;
            }
        }
        return UNKNOWN;
    }
}
