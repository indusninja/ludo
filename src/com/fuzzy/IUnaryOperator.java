package com.fuzzy;

public interface IUnaryOperator {
	// Calculates the numerical result of a Unary operation applied to one
	// fuzzy membership value. Returns the numerical result of the operation
	// applied to membership.
	double Evaluate(double membership);
}
