package com.fuzzy;

public interface ICoNorm {

	// Calculates the numerical result of a CoNorm (OR) operation applied to
	// two fuzzy membership values. Returns the numerical result the operation
	// OR applied to membershipA and membershipB.
	double Evaluate(double membershipA, double membershipB);
}
