package cbit.vcell.mapping;

import org.vcell.model.rbm.LinkNode;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.RbmElementAbstract;
import org.vcell.util.Matchable;

public class StructuralSite extends RbmElementAbstract implements LinkNode {

    private String name;

    public StructuralSite(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return null;
    }



    @Override
    public boolean compareEqual(Matchable aThat) {
        if (this == aThat) {
            return true;
        }
        if (!(aThat instanceof MolecularComponentPattern)) {
            return false;
        }
        StructuralSite that = (StructuralSite)aThat;
        if(!this.name.equals(that.name)) {
            return false;
        }
        return true;
    }

    public static final String typeName = "Site";
    @Override
    public String getDisplayName() {
        return null;
    }
    @Override
    public String getDisplayType() {
        return typeName;
    }

}
