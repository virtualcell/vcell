package cbit.vcell.solver;

import cbit.vcell.parser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface MathOverridesResolver {
    
    class SymbolReplacement {
        public final String newName;
        public final Expression factor;
        public SymbolReplacement(String newName, Expression factor) {
            this.newName = newName;
            this.factor = factor;
        }

        public List<String> getFactorSymbols() {
            String[] symbols = factor.getSymbols();
            if (symbols != null){
                return Arrays.asList(symbols);
            }else{
                return new ArrayList<>();
            }
        }
    }

    SymbolReplacement getSymbolReplacement(String name);
}
