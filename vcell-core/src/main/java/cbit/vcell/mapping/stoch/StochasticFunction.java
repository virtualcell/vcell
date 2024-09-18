package cbit.vcell.mapping.stoch;

import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.parser.Expression;

import java.util.List;

public record StochasticFunction (
        boolean isMassAction,
        Expression forwardRate,
        Expression reverseRate,
        List<ReactionParticipant> reactants,
        List<ReactionParticipant> products) {
}