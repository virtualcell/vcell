package cbit.vcell.solver;

import cbit.vcell.parser.Expression;

public interface MathOverridesResolver {
    
    class SymbolReplacement {
        public final String newName;
        public final Expression factor;
        public SymbolReplacement(String newName, Expression factor) {
            this.newName = newName;
            this.factor = factor;
        }
    }

    SymbolReplacement getSymbolReplacement(String name);
}
