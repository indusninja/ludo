package com.fuzzy;

public class ProductNorm implements INorm {

	public double Evaluate(double membershipA, double membershipB) {
		return membershipA * membershipB;
	}

}
