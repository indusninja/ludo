package com.fuzzy;

import java.util.ArrayList;
import java.util.List;

public class FuzzyOutput {

	// Inner class to store the pair fuzzy label / firing strength of a fuzzy
	// output.
	public class OutputConstraint {
		// The label of a fuzzy output
		private String label;
		// The firing strength of a fuzzy rule, to be applied to the label
		private double firingStrength;

		public OutputConstraint(String label, double firingStrength) {
			this.label = label;
			this.firingStrength = firingStrength;
		}

		public String getLabel() {
			return label;
		}

		public double getFiringStrength() {
			return firingStrength;
		}
	}

	// the linguistic variables repository
	private List<OutputConstraint> outputList;

	// the output linguistic variable
	private LinguisticVariable outputVar;

	public List<OutputConstraint> getOutputList() {
		return outputList;
	}

	public LinguisticVariable getOutputVariable() {
		return outputVar;
	}

	public FuzzyOutput(LinguisticVariable outputVar) {
		// instance of the constraints list
		this.outputList = new ArrayList<OutputConstraint>();

		// output linguistic variable
		this.outputVar = outputVar;
	}

	// / Adds an output to the Fuzzy Output.
	public void AddOutput(String labelName, double firingStrength) {
		// check if the label exists in the linguistic variable
		this.outputVar.GetLabel(labelName);

		// adding label
		this.outputList.add(new OutputConstraint(labelName, firingStrength));
	}

	// Removes all the linguistic variables of the database.
	public void ClearOutput() {
		this.outputList.clear();
	}
}
