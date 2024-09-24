package cbit.vcell.mapping.stoch;

import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.parser.Expression;

import java.util.List;

public record MassActionStochasticFunction (
        Expression forwardRate,
        Expression reverseRate,
        List<ReactionParticipant> reactants,
        List<ReactionParticipant> products) implements StochasticFunction {
    public boolean hasForwardRate() {
        return forwardRate != null && !forwardRate.isZero();
    }
    public boolean hasReverseRate() {
        return reverseRate != null && !reverseRate.isZero();
    }
}
