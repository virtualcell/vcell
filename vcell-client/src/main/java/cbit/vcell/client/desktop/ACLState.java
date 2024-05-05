package cbit.vcell.client.desktop;

import com.google.common.collect.ImmutableList;
import org.vcell.util.document.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ACL State class
public class ACLState {
    private final ACLType aclType;
    private final ImmutableList<String> aclList;

    public static final ACLState PRIVATE_TYPE = new ACLState(ACLType.PRIVATE);
    public static final ACLState PUBLIC_TYPE = new ACLState(ACLType.PUBLIC);
    static final ACLState EMPTY_ACL = new ACLState(ACLType.ACL);

    private ACLState(ACLType aclType) {
        this.aclType = aclType;
        this.aclList = ImmutableList.copyOf(new String[0]);
    }

    // annotate argument as not expecting null

    public ACLState addUserToACL(String user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User cannot be empty");
        }
        if (aclList.contains(user)) {
            System.err.println("User already in ACL");
            return this;
        }
        ArrayList<String> argList = new ArrayList<>(aclList);
        argList.add(user);
        return new ACLState(argList);
    }

    public ACLState removeUserFromACL(String user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User cannot be empty");
        }
        if (!aclList.contains(user)) {
            System.err.println("User not already in ACL");
            return this;
        }
        ArrayList<String> argList = new ArrayList<>(aclList);
        argList.remove(user);
        if (argList.isEmpty()) {
            return PRIVATE_TYPE;
        } else {
            return new ACLState(argList);
        }
    }

    ACLState(List<String> argACLList) {
        if (argACLList.isEmpty()) {
            this.aclType = ACLType.PRIVATE;
            this.aclList = ImmutableList.copyOf(new String[0]);
        } else {
            this.aclType = ACLType.ACL;
            assert argACLList != null;
            aclList = ImmutableList.copyOf(argACLList);
        }
    }

    public static ACLState fromGroupAccess(GroupAccess argGroupAccess) {
        if (argGroupAccess instanceof GroupAccessNone) {
            return PRIVATE_TYPE;
        } else if (argGroupAccess instanceof GroupAccessAll) {
            return PUBLIC_TYPE;
        } else if (argGroupAccess instanceof GroupAccessSome groupAccessSome) {
            User[] normalUsers = groupAccessSome.getNormalGroupMembers();
            User[] hiddenUsers = groupAccessSome.getHiddenGroupMembers();
            ArrayList<String> aclList = new ArrayList<>();
            if (normalUsers != null) {
                Arrays.stream(normalUsers).forEach(user -> aclList.add(user.getName()));
            }
            if (hiddenUsers != null) {
                Arrays.stream(hiddenUsers).forEach(user -> aclList.add(user.getName()));
            }
            assert !aclList.isEmpty() : "GroupAccessSome should have at least one user";
            return new ACLState(aclList);
        } else {
            throw new IllegalArgumentException("GroupAccess type not recognized");
        }
    }

    public ACLType getAclType() {
        return aclType;
    }

    public ImmutableList<String> getAccessList() {
        return aclList;
    }

    public enum ACLType {
        PRIVATE, PUBLIC, ACL
    }
}
