package com.fuzzy;

public class SingletonFunction implements IMembershipFunction {

	// The unique point where the membership value is 1.
	protected double support;

	public SingletonFunction(double support) {
		this.support = support;
	}

	public double GetMembership(double x) {
		// if x is the support, returns 1, otherwise, returns 0
		return (support == x) ? 1 : 0;
	}

	public double getLeftLimit() {
		return support;
	}

	public double getRightLimit() {
		return support;
	}
}
