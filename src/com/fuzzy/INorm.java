package com.fuzzy;

public interface INorm {

	// Calculates the numerical result of a Norm (AND) operation applied to
	// two fuzzy membership values. Returns the numerical result the operation
	// AND applied to membershipA and membershipB
	double Evaluate(double membershipA, double membershipB);
}
