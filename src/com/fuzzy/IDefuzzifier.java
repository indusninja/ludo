package com.fuzzy;

public interface IDefuzzifier {

	// Defuzzification method to obtain the numerical representation of a fuzzy
	// output.
	//
	// Parameter 1: fuzzyOutputA containing the output of several rules of a
	// Fuzzy Inference System.
	//
	// Parameter 2: INorm operator to be used when constraining the label to the
	// firing strength.
	double Defuzzify(FuzzyOutput fuzzyOutput, INorm normOperator) throws Exception;
}
