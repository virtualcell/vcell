package org.vcell.restq.handlers.dto;

import org.vcell.util.document.PublicationInfo;

import java.util.Arrays;
import java.util.List;

/**
 * Flat, JSON-safe DTO for a {@link PublicationInfo}. The core object embeds a {@link org.vcell.util.document.User}
 * (whose {@code key} serializes as a nested object), {@code KeyValue}s, and a {@code Date} — all of which
 * this DTO flattens to strings/lists/epoch-millis so the wire shape is deterministic.
 */
public record PublicationInfoRep(
        String publicationKey,
        String versionKey,
        String title,
        List<String> authors,
        String citation,
        String pubmedid,
        String doi,
        String url,
        String vcDocumentType,
        UserRep user,
        Long pubDate
) {
    public static PublicationInfoRep fromPublicationInfo(PublicationInfo p) {
        if (p == null) {
            return null;
        }
        return new PublicationInfoRep(
                p.getPublicationKey() == null ? null : p.getPublicationKey().toString(),
                p.getVersionKey() == null ? null : p.getVersionKey().toString(),
                p.getTitle(),
                p.getAuthors() == null ? null : Arrays.asList(p.getAuthors()),
                p.getCitation(),
                p.getPubmedid(),
                p.getDoi(),
                p.getUrl(),
                p.getVcDocumentType() == null ? null : p.getVcDocumentType().name(),
                UserRep.fromUser(p.getUser()),
                p.getPubDate() == null ? null : p.getPubDate().getTime());
    }

    /** Null-safe transform of the core {@code PublicationInfo[]} to a DTO list (null -> null). */
    public static List<PublicationInfoRep> fromArray(PublicationInfo[] infos) {
        if (infos == null) {
            return null;
        }
        return Arrays.stream(infos).map(PublicationInfoRep::fromPublicationInfo).toList();
    }
}
