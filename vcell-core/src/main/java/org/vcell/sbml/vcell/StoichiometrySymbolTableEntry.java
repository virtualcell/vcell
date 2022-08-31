package org.vcell.sbml.vcell;

import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.units.VCUnitDefinition;

public class StoichiometrySymbolTableEntry extends AbstractDummySymbolTableEntry {
    private final ReactionParticipant reactionParticipant;
    private final String type;

    public StoichiometrySymbolTableEntry(Reactant reactant) {
        this.reactionParticipant = reactant;
        this.type = "reactant";
    }

    public StoichiometrySymbolTableEntry(Product product) {
        this.reactionParticipant = product;
        this.type = "product";
    }

    public ReactionParticipant getReactionParticipant() {
        return this.reactionParticipant;
    }

    @Override
    public void setExpression(Expression expression) throws ExpressionBindingException {
        try {
            double value = expression.evaluateConstant();
            if (value == (int)value){
                reactionParticipant.setStoichiometry((int)value);
            }else{
                throw new RuntimeException("cannot set non-integer stoichiometry " + value +
                        " on " + this.reactionParticipant.getName() + " for reaction " + reactionParticipant.getReactionStep().getName());
            }
        } catch (ExpressionException e){
            throw new RuntimeException("cannot set non-constant stoichiometry " + expression.infix() +
                    " on " + this.reactionParticipant.getName() + " for reaction " + reactionParticipant.getReactionStep().getName());
        }
    }

    @Override
    public String getDescription() {
        return "stoichiometry '"+reactionParticipant.getStoichiometry()+"' for " + this.type
                + " '" + reactionParticipant.getSpeciesContext().getName() + "' for reaction '"+reactionParticipant.getReactionStep().getName()+"'";
    }

    @Override
    public String toString(){
        return super.toString() + " stoich="+reactionParticipant.getStoichiometry() +
                ", speciesContext='" + reactionParticipant.getSpeciesContext().getName() + "'" +
                ", reaction='"+reactionParticipant.getReactionStep().getName()+"'";
    }

    @Override
    public double getConstantValue() throws ExpressionException {
        return reactionParticipant.getStoichiometry();
    }

    @Override
    public Expression getExpression() {
        return new Expression(reactionParticipant.getStoichiometry());
    }

    @Override
    public String getName() {
        return reactionParticipant.getReactionStep().getName()+ "."+this.type+"_"+reactionParticipant.getSpeciesContext().getName()+".stoichiometry";
    }

    @Override
    public NameScope getNameScope() {
        return reactionParticipant.getReactionStep().getNameScope();
    }

    @Override
    public VCUnitDefinition getUnitDefinition() {
        return reactionParticipant.getSpeciesContext().getUnitDefinition().divideBy(reactionParticipant.getSpeciesContext().getUnitDefinition());
    }
}

