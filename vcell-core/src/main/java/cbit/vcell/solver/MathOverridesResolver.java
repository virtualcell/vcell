package cbit.vcell.solver;

import cbit.vcell.parser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    /**
     * @return set of constant names belonging to physical constants and unit conversions
     *         or 'null' if it cannot fulfill request (e.g. MathSymbolMapping is missing)
     */
    Set<String> getNonOverridableConstantNames();

}
