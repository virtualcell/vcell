package cbit.vcell.mapping.stoch;

import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.parser.Expression;

import java.util.List;

public interface StochasticFunction {
        boolean hasForwardRate();
        boolean hasReverseRate();
        List<ReactionParticipant> reactants();
        List<ReactionParticipant> products();
}