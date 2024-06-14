package org.vcell.restq.models.simulation;

import cbit.vcell.math.Constant;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.MathOverrides;

public class OverrideRepresentationGenerator {
    public final static String OVERRIDE_TYPE_Variable = "Variable";
    public final static String OVERRIDE_TYPE_Single = "Single";
    public final static String OVERRIDE_TYPE_List = "List";
    public final static String OVERRIDE_TYPE_LinearInterval = "LinearInterval";
    public final static String OVERRIDE_TYPE_LogInterval = "LogInterval";

    public static OverrideRepresentation generate(String name, String type, int cardinality, String[] values, String expression) {
        return new OverrideRepresentation(name, type, expression, values, cardinality);
    }

    public static OverrideRepresentation generate(MathOverrides.Element element) throws ExpressionException {
        String name;
        String type;
        int cardinality;
        String[] values;
        String expression;


        name = element.getName();

        //
        // single value (overridden to another real number ... no scan)
        //
        if (element.getActualValue()!=null){
            cardinality = 1;
            if (element.getActualValue().isNumeric()){
                type = OVERRIDE_TYPE_Single;
                values = new String[] { element.getActualValue().infix() };
                expression = null;
            }else{
                type = OVERRIDE_TYPE_Variable;
                values = null;
                expression = element.getActualValue().infix();
            }
            //
            // multiple values
            //
        }else if (element.getSpec()!=null){
            expression = null;
            ConstantArraySpec arraySpec = element.getSpec();
            cardinality = arraySpec.getNumValues();
            switch (arraySpec.getType()){
                //
                // explicit list of overridden values
                //
                case ConstantArraySpec.TYPE_LIST:{
                    type = OVERRIDE_TYPE_List;
                    values = new String[cardinality];
                    for (int i=0;i<cardinality;i++){
                        values[i] = arraySpec.getConstants()[i].getExpression().infix();
                    }
                    break;
                }
                //
                // specified number of values within a range (either linear or log spacing)
                //
                case ConstantArraySpec.TYPE_INTERVAL:{
                    if (arraySpec.isLogInterval()){
                        type = OVERRIDE_TYPE_LogInterval;
                    }else{
                        type = OVERRIDE_TYPE_LinearInterval;
                    }
                    values = new String[] { arraySpec.getMinValue().infix(), arraySpec.getMaxValue().infix() };
                    break;
                }
                default:{
                    throw new RuntimeException("unexpected constant array spec type "+arraySpec.getType());
                }
            }
        }else{
            throw new RuntimeException("expecting either an actualValue or a constant array spec in math override for "+element.getName());
        }

        return new OverrideRepresentation(name, type, expression, values, cardinality);
    }

    public static void applyMathOverrides(MathOverrides mathOverrides, OverrideRepresentation overrideRepresentation) throws ExpressionException{
        String name = overrideRepresentation.name;
        String type = overrideRepresentation.type;
        int cardinality = overrideRepresentation.cardinality;
        String[] values = overrideRepresentation.values;
        String expression = overrideRepresentation.expression;

        switch (type){
            case OVERRIDE_TYPE_Single:{
                mathOverrides.putConstant(new Constant(name, new Expression(values[0])));
                break;
            }
            case OVERRIDE_TYPE_Variable:{
                mathOverrides.putConstant(new Constant(name, new Expression(expression)));
                break;
            }
            case OVERRIDE_TYPE_List:{
                String[] valuesStringArray = new String[values.length];
                for (int i=0; i<values.length; i++){
                    valuesStringArray[i] = ""+values[i];
                }
                mathOverrides.putConstantArraySpec(ConstantArraySpec.createListSpec(name, valuesStringArray));
                break;
            }
            case OVERRIDE_TYPE_LinearInterval:{
                boolean bLogInterval = false;
                mathOverrides.putConstantArraySpec(ConstantArraySpec.createIntervalSpec(name, values[0], values[1], values.length, bLogInterval));
                break;
            }
            case OVERRIDE_TYPE_LogInterval:{
                boolean bLogInterval = true;
                mathOverrides.putConstantArraySpec(ConstantArraySpec.createIntervalSpec(name, values[0], values[1], values.length, bLogInterval));
                break;
            }
            default:{
                throw new RuntimeException("unsupported math override type: "+type);
            }
        }
    }

    public record OverrideRepresentation(String name,
                                         String type,
                                         String expression,
                                         String[] values,
                                         int cardinality) {

    }

}
