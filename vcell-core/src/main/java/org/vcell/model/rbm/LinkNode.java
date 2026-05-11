package org.vcell.model.rbm;

import org.vcell.util.Displayable;
import org.vcell.util.Matchable;

// Used to introduce a common endpoint type for SpringSaLaD related entities
// more exactly, connects StructuralSites and MolecularComponentPatterns as linkable entities
public interface LinkNode extends Displayable, Matchable {

    // used only for MolecularComponentPattern as getMolecularComponent().getName()
    // and directly as getName() for StructuralSite
    public String getName();
}
