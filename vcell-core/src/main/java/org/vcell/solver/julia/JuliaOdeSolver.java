package org.vcell.solver.julia;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import org.vcell.util.CommentStringTokenizer;

import java.beans.PropertyVetoException;
import java.util.List;
import java.util.stream.Collectors;

public class JuliaOdeSolver {
    public final static String mathVCML = "MathDescription {\n" +
            "\n" +
            "Constant  _F_\t96485.3321;\n" +
            "Constant  _F_nmol_\t9.64853321E-5;\n" +
            "Constant  _K_GHK_\t1.0E-9;\n" +
            "Constant  _N_pmol_\t6.02214179E11;\n" +
            "Constant  _PI_\t3.141592653589793;\n" +
            "Constant  _R_\t8314.46261815;\n" +
            "Constant  _T_\t300.0;\n" +
            "Constant  K_millivolts_per_volt\t1000.0;\n" +
            "Constant  Kf_r0\t1.0;\n" +
            "Constant  Kf_r1\t3.0;\n" +
            "Constant  KMOLE\t0.001660538783162726;\n" +
            "Constant  Kr_r0\t2.0;\n" +
            "Constant  Kr_r1\t4.0;\n" +
            "Constant  s0_init_uM\t1.0;\n" +
            "Constant  s1_init_uM\t2.0;\n" +
            "Constant  s2_init_uM\t3.0;\n" +
            "Constant  s3_init_uM\t4.0;\n" +
            "Constant  Size_c0\t50000.0;\n" +
            "\n" +
            "VolumeVariable   Compartment::s0\n" +
            "VolumeVariable   Compartment::s1\n" +
            "VolumeVariable   Compartment::s2\n" +
            "VolumeVariable   Compartment::s3\n" +
            "\n" +
            "Function  Compartment::J_r0\t ((Kf_r0 * s0) - ((Kr_r0 * s1) * s2));\n" +
            "Function  Compartment::J_r1\t ((Kf_r1 * s2) - (Kr_r1 * s3));\n" +
            "\n" +
            "CompartmentSubDomain Compartment {\n" +
            "\tOdeEquation s0 {\n" +
            "\t\tRate\t - J_r0;\n" +
            "\t\tInitial\t s0_init_uM;\n" +
            "\t}\n" +
            "\tOdeEquation s1 {\n" +
            "\t\tRate\tJ_r0;\n" +
            "\t\tInitial\t s1_init_uM;\n" +
            "\t}\n" +
            "\tOdeEquation s2 {\n" +
            "\t\tRate\t(J_r0 - J_r1);\n" +
            "\t\tInitial\t s2_init_uM;\n" +
            "\t}\n" +
            "\tOdeEquation s3 {\n" +
            "\t\tRate\tJ_r1;\n" +
            "\t\tInitial\t s3_init_uM;\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "}";

    public static void main(String[] args) throws ExpressionException, MathException, PropertyVetoException {
        MathDescription mathDesc = new MathDescription("math");
        mathDesc.setGeometry(new Geometry("geo1",0));
        mathDesc.read_database(new CommentStringTokenizer(mathVCML));
        JuliaOdeSolver juliaOdeSolver = new JuliaOdeSolver();
        System.out.println(juliaOdeSolver.writeJuliaProblem(mathDesc, UseDSL.USE_NO_DSL));
    }

    enum UseDSL {
        USE_DSL,
        USE_NO_DSL
    }

    public String writeJuliaProblem(MathDescription mathDesc, UseDSL useDSL) throws ExpressionException {
        if (mathDesc.getGeometry().getDimension()!=0){
            throw new IllegalArgumentException("Geometry must be 0-dimensional for JuliaOdeSolver");
        }
        if (mathDesc.getSubDomainCollection().size()!=1){
            throw new IllegalArgumentException("Math must have exactly one domain for JuliaOdeSolver, " +
                    "found "+mathDesc.getSubDomainCollection().size()+" domains");
        }
        CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain) mathDesc.getSubDomains().nextElement();

        List<Constant> constants = mathDesc.getVariableList().stream()
                .filter(c -> c instanceof Constant)
                .map(c -> (Constant) c).collect(Collectors.toList());
        List<OdeEquation> odeEquationList = compartmentSubDomain.getEquationCollection().stream()
                .filter(eq -> eq instanceof OdeEquation)
                .map(eq -> (OdeEquation) eq).collect(Collectors.toList());

        List<Expression> initExprList = odeEquationList.stream()
                .map(OdeEquation::getInitialExpression)
                .map(expr -> {
                    try {
                        return MathUtilities.substituteFunctions(expr, mathDesc).flatten();
                    } catch (ExpressionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        if (useDSL==UseDSL.USE_DSL) {
            return writeJuliaWithDSL(mathDesc, constants, odeEquationList, initExprList);
        } else {
            return writeJuliaWithoutDSL(mathDesc, constants, odeEquationList, initExprList);
        }
    }

    public String writeJuliaWithDSL(MathDescription mathDesc, List<Constant> constants, List<OdeEquation> odeEquationList, List<Expression> initExprList) throws ExpressionException {
        StringBuilder sb = new StringBuilder();
        // flatten functions (leave only true parameters)
        sb.append("import Pkg; Pkg.add(\"SciMLBase\"); Pkg.add(\"DifferentialEquations\"); Pkg.add(\"ParameterizedFunctions\");\n");
        sb.append("# Pkg.add(\"BenchmarkTools\");\n)");
        sb.append("# Pkg.add(\"Plots\");\n");
        sb.append("using SciMLBase\n");
        sb.append("using DifferentialEquations\n");
        sb.append("# using BenchmarkTools\n");
        sb.append("# using Plots;\n");
        sb.append("using ParameterizedFunctions\n");
        sb.append("\n");
        sb.append("# parameters\n");
        int i = 0;
        sb.append("pfunc = @ode_def begin\n");
        for (OdeEquation ode: odeEquationList) {
            sb.append("    d"+ode.getVariable().getName());
            sb.append(" = ");
            sb.append(MathUtilities.substituteFunctions(ode.getRateExpression(), mathDesc).flattenSafe().infix());
            sb.append("\n");
        }
        sb.append("end ");
        sb.append(constants.stream().map(Constant::getName).collect(Collectors.joining(" ")));
        sb.append("\n\n");
        sb.append("p = [");
        sb.append(constants.stream().map(c -> {
            try {
                return (MathUtilities.substituteFunctions(c.getExpression(), mathDesc).flattenSafe().infix());
            } catch (ExpressionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining(", ")));
        sb.append("]\n");
        sb.append("u0 = [");
        sb.append(initExprList.stream().map(Expression::infix).collect(Collectors.joining("; ")));
        sb.append("]\n");
        sb.append("tspan = (0.0, 1.0)\n");
        sb.append("\n");
        sb.append("# Test that it worked\n");
        sb.append("prob = ODEProblem(pfunc,u0,tspan,p)\n");
//        sb.append("@benchmark sol = solve(prob, Tsit5(), reltol = 1e-8, abstol = 1e-8)\n");
        sb.append("sol = solve(prob, Tsit5(), reltol = 1e-8, abstol = 1e-8)\n");
//        sb.append("println(sol)\n");
        String labels = odeEquationList.stream().map(ode -> "\""+ode.getVariable().getName()+"\"").collect(Collectors.joining(" "));
        sb.append("# plot(sol,labels=["+labels+"])\n");
        return sb.toString();
    }

    public String writeJuliaWithoutDSL(MathDescription mathDesc, List<Constant> constants, List<OdeEquation> odeEquationList, List<Expression> initExprList) throws ExpressionException {
        StringBuilder sb = new StringBuilder();
        // flatten functions (leave only true parameters)
        sb.append("import Pkg; Pkg.add(\"SciMLBase\"); Pkg.add(\"DifferentialEquations\");\n");
        sb.append("# Pkg.add(\"BenchmarkTools\");\n");
        sb.append("# Pkg.add(\"Plots\");\n");
        sb.append("using SciMLBase\n");
        sb.append("using DifferentialEquations\n");
        sb.append("# using BenchmarkTools\n");
        sb.append("# using Plots;\n");
        sb.append("\n");
        sb.append("# parameters\n");
        int i = 0;
        sb.append("p = zeros("+(constants.size())+")\n");
        for (Constant constant: constants) {
            sb.append("# parameter "+constant.getName()+"\n");
            sb.append("p["+(i+1)+"] = ");
            sb.append(MathUtilities.substituteFunctions(constant.getExpression(), mathDesc).flattenSafe().infix());
            sb.append("\n");
            i++;
        }
        sb.append("function f!(du,u,p,t)\n");
        i = 0;
        for (Constant constant: constants) {
            sb.append("    ");
            sb.append(constant.getName());
            sb.append(" = p["+(i+1)+"]\n");
            i++;
        }
        i = 0;
        for (OdeEquation ode: odeEquationList) {
            sb.append("    "+ode.getVariable().getName()+" = u["+(i+1)+"]\n");
            i++;
        }
        i=0;
        for (OdeEquation ode: odeEquationList) {
            sb.append("    # ");
            sb.append(ode.getVariable().getName());
            sb.append("'\n");
            sb.append("    du[");
            sb.append(i+1);
            sb.append("] = ");
            sb.append(MathUtilities.substituteFunctions(ode.getRateExpression(), mathDesc).flattenSafe().infix());
            sb.append("\n");
            i++;
        }
        sb.append("end\n");
        sb.append("u0 = [");
        sb.append(initExprList.stream().map(Expression::infix).collect(Collectors.joining(";\n")));
        sb.append("]\n");
        sb.append("tspan = (0.0, 1.0)\n");
        sb.append("\n");
        sb.append("# Test that it worked\n");
        sb.append("prob = ODEProblem(f!,u0,tspan,p)\n");
//        sb.append("@benchmark sol = solve(prob, Tsit5(), reltol = 1e-8, abstol = 1e-8)\n");
        sb.append("sol = solve(prob, Tsit5(), reltol = 1e-8, abstol = 1e-8)\n");
//        sb.append("println(sol)\n");
        String labels = odeEquationList.stream().map(ode -> "\""+ode.getVariable().getName()+"\"").collect(Collectors.joining(" "));
        sb.append("# plot(sol,labels=["+labels+"])\n");
        return sb.toString();
    }

}
