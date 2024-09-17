package cbit.vcell.model;

import cbit.vcell.parser.Expression;

import java.util.List;

public class StochasticKineticsResults {
    public final MassActionSolver.MassActionFunction massActionFunction;
    public final GeneralKineticsSolver.GeneralKineticsStochasticFunction generalKineticsStochasticFunction;
    private final List<ReactionParticipant> reactants;
    private final List<ReactionParticipant> products;

    public StochasticKineticsResults(MassActionSolver.MassActionFunction massActionFunction,
                                     List<ReactionParticipant> reactants,
                                     List<ReactionParticipant> products) {
        this.massActionFunction = massActionFunction;
        this.generalKineticsStochasticFunction = null;
        this.reactants = reactants;
        this.products = products;
    }

    public StochasticKineticsResults(GeneralKineticsSolver.GeneralKineticsStochasticFunction generalKineticsStochasticFunction,
                                     List<ReactionParticipant> reactants,
                                     List<ReactionParticipant> products) {
        this.massActionFunction = null;
        this.generalKineticsStochasticFunction = generalKineticsStochasticFunction;
        this.reactants = reactants;
        this.products = products;
    }

    public List<ReactionParticipant> getReactants() {
        return reactants;
    }

    public List<ReactionParticipant> getProducts() {
        return products;
    }

    public Expression getForwardRate() {
        return massActionFunction.getForwardRate();
    }

    public Expression getReverseRate() {
        return massActionFunction.getReverseRate();
    }

}
