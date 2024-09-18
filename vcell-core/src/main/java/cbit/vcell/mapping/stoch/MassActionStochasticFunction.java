package cbit.vcell.mapping.stoch;

import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.parser.Expression;

import java.util.List;

public class MassActionStochasticFunction {
    private Expression fRate = null;
    private Expression rRate = null;
    private List<ReactionParticipant> reactants = null;
    private List<ReactionParticipant> products = null;

    public MassActionStochasticFunction() {
    }

    public MassActionStochasticFunction(Expression forwardRate, Expression reverseRate) {
        this(forwardRate, reverseRate, null, null);
    }

    public MassActionStochasticFunction(Expression forwardRate, Expression reverseRate, List<ReactionParticipant> reactants, List<ReactionParticipant> products) {
        this.fRate = forwardRate;
        this.rRate = reverseRate;
        this.reactants = reactants;
        this.products = products;
    }

    public Expression getForwardRate() {
        return fRate;
    }

    void setForwardRate(Expression rate) {
        fRate = rate;
    }

    public Expression getReverseRate() {
        return rRate;
    }

    void setReverseRate(Expression rate) {
        rRate = rate;
    }

    public List<ReactionParticipant> getReactants() {
        return reactants;
    }

    public void setReactants(List<ReactionParticipant> reactants) {
        this.reactants = reactants;
    }

    public List<ReactionParticipant> getProducts() {
        return products;
    }

    public void setProducts(List<ReactionParticipant> products) {
        this.products = products;
    }
//		public void show()
//		{
//			System.out.println("Forward rate is " + getForwardRate().infix());
//			System.out.println("Reverse rate is " + getReverseRate().infix());
//		}
}
