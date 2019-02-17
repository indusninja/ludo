package com.fuzzy;

import java.util.HashMap;

public class LinguisticVariable {
	// name of the linguistic variable
	private String name;
	// right limit within the lingusitic variable works
	private double start;
	// left limit within the lingusitic variable works
	private double end;
	// the linguistic labels of the linguistic variable
	private HashMap<String, FuzzySet> labels;
	// the numeric input of this variable
	private double numericInput;

	public double getNumericInput() {
		return numericInput;
	}

	public void setNumericInput(double value) {
		numericInput = value;
	}

	// / Name of the linguistic variable.
	public String getName() {
		return name;
	}

	// Left limit of the valid variable range.
	public double getStart() {
		return start;
	}

	// Right limit of the valid variable range.
	public double getEnd() {
		return end;
	}

	public LinguisticVariable(String name, double start, double end) {
		this.name = name;
		this.start = start;
		this.end = end;

		// instance of the labels list - usually a linguistic variable has no
		// more than 10 labels
		labels = new HashMap<String, FuzzySet>();
	}

	// Adds a linguistic label to the variable.
	public void AddLabel(FuzzySet label) throws Exception {
		// checking for existing name
		if (this.labels.containsKey(label.getName()))
			throw new Exception(
					"The linguistic label name already exists in the linguistic variable.");

		// checking ranges
		if (label.getLeftLimit() < this.start)
			throw new Exception(
					"The left limit of the fuzzy set can not be lower than the linguistic variable's starting point.");
		if (label.getReftLimit() > this.end)
			throw new Exception(
					"The right limit of the fuzzy set can not be greater than the linguistic variable's ending point.");

		// adding label
		this.labels.put(label.getName(), label);
	}

	// Removes all the linguistic labels of the linguistic variable.
	public void ClearLabels() {
		labels.clear();
	}

	// Returns an existing label from the linguistic variable.
	public FuzzySet GetLabel(String labelName) {
		return this.labels.get(labelName);
	}

	/*
	 * Calculate the membership of a given value to a given label. Used to
	 * evaluate linguistics clauses like "X IS A", where X is a value and A is a
	 * linguistic label.
	 */
	public double GetLabelMembership(String labelName, double value) {
		FuzzySet fs = labels.get(labelName);
		return fs.GetMembership(value);
	}
}
