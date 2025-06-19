package org.vcell.util.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.vcell.util.Immutable;
import org.vcell.util.Matchable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


@Schema(implementation = User.class)
public class SpecialUser extends User implements Serializable, Matchable, Immutable {
    private final static String PREVIOUS_DATABASE_VALUE_ADMIN = "special0";
    private final static String PREVIOUS_DATABASE_VALUE_POWERUSER = "special1";
    private final static String PREVIOUS_DATABASE_VALUE_PUBLICATION = "publication";

    public enum SPECIAL_CLAIM {
        admins/*special0*/,
        powerUsers/*special1*/,
        publicationEditors /*publication*/,  // users allowed to modify publications
        vcellSupport;

        public static SPECIAL_CLAIM fromDatabase(String databaseString){
            if (databaseString.equals(PREVIOUS_DATABASE_VALUE_ADMIN)){
                return admins;
            }
            if (databaseString.equals(PREVIOUS_DATABASE_VALUE_POWERUSER)){
                return powerUsers;
            }
            if (databaseString.equals(PREVIOUS_DATABASE_VALUE_PUBLICATION)){
                return publicationEditors;
            }
            return SPECIAL_CLAIM.valueOf(databaseString);
        }

        public String toDatabaseString(){
            return name();
        }
    };//Must match a name 'special' column of 'vc_specialusers'// table

    public SpecialUser(String userid, KeyValue key, SPECIAL_CLAIM[] mySpecials) {
        super(userid, key);
        this.mySpecials = mySpecials;
    }

    public SPECIAL_CLAIM[] getMySpecials() {
        return mySpecials;
    }

    @JsonIgnore
    public boolean isAdmin() {
        return Arrays.asList(mySpecials).contains(SPECIAL_CLAIM.admins);
    }

    @JsonIgnore
    public boolean isPublisher() {
        return Arrays.asList(mySpecials).contains(SPECIAL_CLAIM.publicationEditors);
    }

    @JsonIgnore
    public boolean isVCellSupport() {
        return Arrays.asList(mySpecials).contains(SPECIAL_CLAIM.vcellSupport);
    }

    /**
     * Builder so that the original classes claim array stays as an unmodified entity.
     */
    public static class SpecialUserBuilder{
        private final ArrayList<SPECIAL_CLAIM> claims = new ArrayList<>();
        private final String userID;
        private final KeyValue key;

        public SpecialUserBuilder(String userID, KeyValue key) {
            this.userID = userID;
            this.key = key;
        }

        public SpecialUserBuilder addSpecial(SPECIAL_CLAIM special) {
            claims.add(special);
            return this;
        }

        public org.vcell.util.document.SpecialUser build() {
            return new SpecialUser(userID,key,claims.toArray(new SPECIAL_CLAIM[]{}));
        }
    }
}
