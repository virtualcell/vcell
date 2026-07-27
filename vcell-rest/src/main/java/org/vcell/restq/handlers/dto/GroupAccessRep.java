package org.vcell.restq.handlers.dto;

import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Flat, JSON-safe DTO for the sharing state of a model ({@link GroupAccess}).
 * <p>
 * The core {@link GroupAccess} hierarchy caused a run of client-breaking serialization bugs, all
 * from serializing the polymorphic core objects directly:
 * <ul>
 *   <li>{@code GroupAccessSome} emitted no {@code type} discriminator, so the polymorphic client
 *       failed with {@code InvalidTypeIdException}.</li>
 *   <li>Its member {@code User}s serialized {@code key} as a nested object, not a string.</li>
 *   <li>Jackson serialized the getters ({@code normalGroupMembers}/{@code hiddenGroupMembers}) while
 *       the OpenAPI schema declared the fields ({@code groupMembers}/{@code hiddenMembers}), so the
 *       client's {@code hiddenMembers} came back null and NPE'd.</li>
 * </ul>
 * This DTO replaces the polymorphic hierarchy with a single record discriminated by a plain
 * {@code type} string ({@code "all"}, {@code "none"}, {@code "some"}) and carries members as an
 * always-present list of {@link GroupMemberRep}. The server-side integrity {@code hash} is
 * intentionally NOT transmitted — it is a database-internal value the read-only client never writes
 * back; the client recomputes a consistent hash when it rebuilds a native {@code GroupAccessSome}.
 */
public record GroupAccessRep(
        String type,
        String groupid,
        List<GroupMemberRep> members
) {
    public static final String TYPE_ALL = "all";
    public static final String TYPE_NONE = "none";
    public static final String TYPE_SOME = "some";

    public static GroupAccessRep fromGroupAccess(GroupAccess ga) {
        if (ga == null) {
            return null;
        }
        String groupid = ga.getGroupid() == null ? null : ga.getGroupid().toString();
        List<GroupMemberRep> members = new ArrayList<>();
        String type;
        if (ga instanceof GroupAccessSome some) {
            type = TYPE_SOME;
            User[] normal = some.getNormalGroupMembers();
            if (normal != null) {
                for (User u : normal) {
                    members.add(new GroupMemberRep(UserRep.fromUser(u), false));
                }
            }
            User[] hidden = some.getHiddenGroupMembers();
            if (hidden != null) {
                for (User u : hidden) {
                    members.add(new GroupMemberRep(UserRep.fromUser(u), true));
                }
            }
        } else if (ga instanceof GroupAccessNone) {
            type = TYPE_NONE;
        } else {
            type = TYPE_ALL; // GroupAccessAll
        }
        return new GroupAccessRep(type, groupid, members);
    }
}
