package org.vcell.restq.handlers.dto;

/**
 * One member of a shared group ({@link GroupAccessRep}): the user plus whether the record is hidden
 * from the owner. Carrying the {@code hidden} flag per-member (instead of the core object's parallel
 * {@code hiddenMembers boolean[]}) keeps the wire shape self-consistent and never null.
 */
public record GroupMemberRep(
        UserRep user,
        boolean hidden
) {
}
