package com.fuzzy;

public class MaximumCoNorm implements ICoNorm {

	public double Evaluate(double membershipA, double membershipB) {
		return Math.max(membershipA, membershipB);
	}

}
