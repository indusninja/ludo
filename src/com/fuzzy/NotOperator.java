package com.fuzzy;

public class NotOperator implements IUnaryOperator {

	public double Evaluate(double membership) {
		return (1 - membership);
	}

}
