package com.fuzzy;

public class MinimumNorm implements INorm {

	public double Evaluate(double membershipA, double membershipB) {
		return Math.min(membershipA, membershipB);
	}

}
