package com.fuzzy;

public class FuzzySet {
	// name of the fuzzy set
	protected String name;
	// membership functions that defines the shape of the fuzzy set
	protected IMembershipFunction function;

	public String getName() {
		return name;
	}

	public double getLeftLimit() {
		return function.getLeftLimit();
	}

	public double getReftLimit() {
		return function.getRightLimit();
	}

	public FuzzySet(String name, IMembershipFunction function) {
		this.name = name;
		this.function = function;
	}

	public double GetMembership(double x) {
		return function.GetMembership(x);
	}
}
