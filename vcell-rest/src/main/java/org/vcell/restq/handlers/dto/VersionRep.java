package org.vcell.restq.handlers.dto;

import org.vcell.util.document.Version;

/**
 * Flat, JSON-safe DTO for a {@link Version}, used by the read-only summary responses
 * (e.g. {@code GET /api/v1/vcInfoContainer}).
 * <p>
 * The core {@link Version} embeds objects that were never designed for Jackson — a polymorphic
 * {@link org.vcell.util.document.GroupAccess} (see {@link GroupAccessRep}) and a
 * {@link org.vcell.util.document.User} whose {@code key} serializes as a nested object (see
 * {@link UserRep}). Serializing {@code Version} directly therefore broke real clients. This DTO is
 * composed entirely of primitives, strings, and other flat DTOs, so its wire shape is deterministic
 * and matches the generated client contract.
 */
public record VersionRep(
        String versionKey,
        String name,
        UserRep owner,
        String branchPointRefKey,
        String branchId,
        Long date,
        Integer flag,
        String annotation,
        GroupAccessRep groupAccess
) {
    public static VersionRep fromVersion(Version v) {
        if (v == null) {
            return null;
        }
        return new VersionRep(
                v.getVersionKey() == null ? null : v.getVersionKey().toString(),
                v.getName(),
                UserRep.fromUser(v.getOwner()),
                v.getBranchPointRefKey() == null ? null : v.getBranchPointRefKey().toString(),
                v.getBranchID() == null ? null : v.getBranchID().toString(),
                v.getDate() == null ? null : v.getDate().getTime(),
                v.getFlag() == null ? null : v.getFlag().getIntValue(),
                v.getAnnot(),
                GroupAccessRep.fromGroupAccess(v.getGroupAccess()));
    }
}
