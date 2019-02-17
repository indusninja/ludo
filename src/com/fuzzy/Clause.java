package com.fuzzy;

public class Clause {
	// the linguistic variable of the clause
	private LinguisticVariable variable;
	// the label of the clause
	private FuzzySet label;

	public LinguisticVariable getVariable() {
		return variable;
	}

	public FuzzySet getLabel() {
		return label;
	}

	public Clause(LinguisticVariable variable, FuzzySet label) {
		// check if label belongs to variable
		variable.GetLabel(label.getName());

		// initializing attributes
		this.label = label;
		this.variable = variable;
	}

	// Evaluates the fuzzy clause.
	public double Evaluate() {
		return label.GetMembership(variable.getNumericInput());
	}

	// Returns the fuzzy clause in its linguistic representation.
	public String toString() {
		return this.variable.getName() + " IS " + this.label.getName();
	}
}
