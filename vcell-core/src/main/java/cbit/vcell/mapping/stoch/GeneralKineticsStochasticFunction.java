package cbit.vcell.mapping.stoch;

import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.parser.Expression;

import java.util.List;

public class GeneralKineticsStochasticFunction {
    private Expression fRate = null;
    private Expression rRate = null;
    private List<ReactionParticipant> reactants = null;
    private List<ReactionParticipant> products = null;

    public GeneralKineticsStochasticFunction(Expression forwardRate, Expression reverseRate, List<ReactionParticipant> reactants, List<ReactionParticipant> products) {
        this.fRate = forwardRate;
        this.rRate = reverseRate;
        this.reactants = reactants;
        this.products = products;
    }

    public Expression getForwardRate() {
        return fRate;
    }

    public Expression getReverseRate() {
        return rRate;
    }

    public List<ReactionParticipant> getReactants() {
        return reactants;
    }

    public List<ReactionParticipant> getProducts() {
        return products;
    }
}
