package cbit.vcell.mapping.stoch;

import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.parser.Expression;

import java.util.List;

public record GeneralKineticsStochasticFunction (
        Expression forwardNetRate,
        Expression reverseNetRate,
        List<ReactionParticipant> reactants,
        List<ReactionParticipant> products) implements StochasticFunction {
    public boolean hasForwardRate() {
        return forwardNetRate != null && !forwardNetRate.isZero();
    }
    public boolean hasReverseRate() {
        return reverseNetRate != null && !reverseNetRate.isZero();
    }
}
