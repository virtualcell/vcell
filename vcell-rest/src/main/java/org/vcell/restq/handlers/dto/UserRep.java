package org.vcell.restq.handlers.dto;

import org.vcell.util.document.User;

/**
 * Flat, JSON-safe DTO for a {@link User} as it appears inside read-only summary responses.
 * <p>
 * The core {@link User#getID()} returns a {@code KeyValue}, which Jackson serializes as a nested
 * object ({@code {"value": 1}}) — but the generated REST client expects {@code key} to be a plain
 * string, so serializing the core object directly broke the client. This DTO carries {@code key}
 * as a string, matching the client contract.
 */
public record UserRep(
        String userName,
        String key
) {
    public static UserRep fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new UserRep(user.getName(), user.getID() == null ? null : user.getID().toString());
    }
}
