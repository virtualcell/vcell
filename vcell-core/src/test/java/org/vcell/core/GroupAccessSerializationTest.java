package org.vcell.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression guard for the REST {@code GroupAccess} polymorphic discriminator.
 * <p>
 * Every concrete {@code GroupAccess} subtype must serialize a {@code "type"} property so the
 * generated REST client can resolve the subtype (used by {@code GET /api/v1/vcInfoContainer}).
 * {@code GroupAccessSome} previously declared its {@code type} field {@code private} (no getter),
 * so shared biomodels serialized without the discriminator and clients failed with
 * {@code InvalidTypeIdException: missing type id property 'type'}. All three subtypes now use a
 * {@code public final String type} field.
 */
@Tag("Fast")
public class GroupAccessSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    @Test
    public void allSubtypesSerializeTypeDiscriminator() throws Exception {
        assertTrue(mapper.writeValueAsString(new GroupAccessAll()).contains("\"type\":\"GroupAccessAll\""),
                "GroupAccessAll must serialize its type discriminator");
        assertTrue(mapper.writeValueAsString(new GroupAccessNone()).contains("\"type\":\"GroupAccessNone\""),
                "GroupAccessNone must serialize its type discriminator");

        KeyValue memberKey = new KeyValue("1");
        User[] members = new User[]{ new User("alice", memberKey) };
        boolean[] hiddenMembers = new boolean[]{ false };
        BigDecimal hash = GroupAccess.calculateHash(new KeyValue[]{ memberKey }, hiddenMembers);
        GroupAccessSome some = new GroupAccessSome(new BigDecimal(100), hash, members, hiddenMembers);
        assertTrue(mapper.writeValueAsString(some).contains("\"type\":\"GroupAccessSome\""),
                "GroupAccessSome must serialize its type discriminator (regression: field was private)");
    }
}
