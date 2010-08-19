package org.vcell.smoldyn.model.util;


import java.util.ArrayList;
import org.vcell.smoldyn.model.Participant.Product;
import org.vcell.smoldyn.model.Participant.Reactant;


/**
 * A Smoldyn reaction has participants.
 * Reactions may be 0th, 1st, or 2nd order, and may have 0, 1, or 2 products, but may not have both 0 reactants AND 0 products.
 * 
 * @author mfenwick
 *
 */
public class ReactionParticipants {

	private final ArrayList<Reactant> reactants = new ArrayList<Reactant>(2);
	private final ArrayList<Product> products = new ArrayList<Product>(2);	
	

	public ReactionParticipants(Reactant reactant1) {
		this.reactants.add(reactant1);
	}
	
	public ReactionParticipants(Product product1) {
		this.products.add(product1);
	}
	
	public ReactionParticipants(Reactant reactant1, Product product1) {
		this.reactants.add(reactant1);
		this.products.add(product1);
	}
	
	public ReactionParticipants(Reactant reactant1, Reactant reactant2) {
		this.reactants.add(reactant1);
		this.reactants.add(reactant2);
	}
	
	public ReactionParticipants(Product product1, Product product2) {
		this.products.add(product1);
		this.products.add(product2);
	}
	
	public ReactionParticipants(Reactant reactant1, Reactant reactant2, Product product1) {
		this.reactants.add(reactant1);
		this.reactants.add(reactant2);
		this.products.add(product1);
	}
	
	public ReactionParticipants(Reactant reactant1, Product product1, Product product2) {
		this.reactants.add(reactant1);
		this.products.add(product1);
		this.products.add(product2);
	}
	
	public ReactionParticipants(Reactant reactant1, Reactant reactant2, Product product1, Product product2) {
		this.reactants.add(reactant1);
		this.reactants.add(reactant2);
		this.products.add(product1);
		this.products.add(product2);
	}

	
	public Reactant [] getReactants() {
		return this.reactants.toArray(new Reactant [this.reactants.size()]);
	}
	
	public Product [] getProducts() {
		return this.products.toArray(new Product [this.products.size()]);
	}
}
