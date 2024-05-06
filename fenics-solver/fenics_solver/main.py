import fenics as fe

N_POINTS_P_AXIS = 12
FORCING_MAGNITUDE = 1.0

def main():
    # Create mesh and function space
    mesh = fe.UnitSquareMesh(N_POINTS_P_AXIS, N_POINTS_P_AXIS)
    lagrange_polynomial_space = fe.FunctionSpace(mesh, 'Lagrange', 1)

    # Define boundary condition
    def boundary_bool_function(x, on_boundary: bool):
        return on_boundary

    homogeneous_bc = fe.DirichletBC(
        lagrange_polynomial_space,
        fe.Constant(0.0),
        boundary_bool_function)

    u_trial = fe.TrialFunction(lagrange_polynomial_space)
    v_test = fe.TestFunction(lagrange_polynomial_space)

    # weak form
    forcing = fe.Constant(FORCING_MAGNITUDE)
    weak_form_lhs = fe.dot(fe.grad(u_trial), fe.grad(v_test)) * fe.dx
    weak_form_rhs = forcing * v_test * fe.dx

    # Finite Element Assembly and Linear System solve
    u_solution = fe.Function(lagrange_polynomial_space)
    fe.solve(weak_form_lhs == weak_form_rhs, u_solution, bc=homogeneous_bc)

    # Save solution to file
    fe.File('solution.pvd') << u_solution


if __name__ == '__main__':
    main()