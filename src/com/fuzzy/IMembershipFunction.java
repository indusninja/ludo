package com.fuzzy;

public interface IMembershipFunction {

	double GetMembership(double x);

	double getLeftLimit();

	double getRightLimit();
}
