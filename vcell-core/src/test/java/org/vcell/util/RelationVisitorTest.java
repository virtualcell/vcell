package org.vcell.util;

import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelRelationVisitor;
import cbit.vcell.model.ModelTest;
import cbit.vcell.parser.Expression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class RelationVisitorTest {

    @Test
    public void testModel_relationVisitor_vs_compareEquals() throws Exception {
        Model model1 = ModelTest.getExampleForFluorescenceIndicatorProtocol();
        Model model2 = ModelTest.getExampleForFluorescenceIndicatorProtocol();

        // compare with standard test
        Assertions.assertTrue(model1.compareEqual(model2));

        // create two visitors - one with strict policy, one with non-strict policy (equivalence)
        RelationVisitor relationVisitorStrict = new ModelRelationVisitor(true);
        RelationVisitor relationVisitorNotStrict = new ModelRelationVisitor(false);

        // two models are identical, both visitors should return true (same/equiv)
        Assertions.assertTrue(model1.relate(model2, relationVisitorStrict));
        Assertions.assertTrue(model1.relate(model2, relationVisitorNotStrict));

        // modify a kinetics parameter (kf from "1.0" to "1.0*KMOLE/KMOLE") ... different but still equivalent.
        Kinetics.KineticsParameter kf = model1.getReactionSteps()[0].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KForward);
        Model.ReservedSymbol KMOLE = model1.getKMOLE();
        Expression kmoleExp = new Expression(KMOLE, null);
        kf.setExpression(Expression.mult(kf.getExpression(), kmoleExp, Expression.invert(kmoleExp)));

        // strict should return false, non-strict should return true
        Assertions.assertFalse(model1.relate(model2, relationVisitorStrict));
        Assertions.assertTrue(model1.relate(model2, relationVisitorNotStrict));
    }
}
