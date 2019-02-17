package com.fuzzy;

public class InferenceSystem {
	// The linguistic variables of this system
	private Database database;
	// The fuzzy rules of this system
	private Rulebase rulebase;
	// The defuzzifier method chosen
	private IDefuzzifier defuzzifier;
	// Norm operator used in rules and deffuzification
	private INorm normOperator;
	// CoNorm operator used in rules
	private ICoNorm conormOperator;

	public InferenceSystem(Database database, IDefuzzifier defuzzifier) {
		this(database, defuzzifier, new MinimumNorm(), new MaximumCoNorm());
	}

	public InferenceSystem(Database database, IDefuzzifier defuzzifier,
			INorm normOperator, ICoNorm conormOperator) {
		this.database = database;
		this.defuzzifier = defuzzifier;
		this.normOperator = normOperator;
		this.conormOperator = conormOperator;
		this.rulebase = new Rulebase();
	}

	// Creates a new "Rule" and add it to the "Rulebase" of the
	// "InferenceSystem"
	public Rule NewRule(String name, String rule) throws Exception {
		Rule r = new Rule(database, name, rule, normOperator, conormOperator);
		this.rulebase.AddRule(r);
		return r;
	}

	// Sets a numerical input for one of the linguistic variables of the
	// "Database"
	public void SetInput(String variableName, double value) {
		this.database.GetVariable(variableName).setNumericInput(value);
	}

	// Gets one of the "LinguisticVariable" of the "Database"
	public LinguisticVariable GetLinguisticVariable(String variableName) {
		return this.database.GetVariable(variableName);
	}

	// Gets one of the Rules of the "Rulebase"
	public Rule GetRule(String ruleName) {
		return this.rulebase.GetRule(ruleName);
	}

	// Executes the fuzzy inference, obtaining a numerical output for a chosen
	// output linguistic variable.
	public double Evaluate(String variableName) throws Exception {
		// call the defuzzification on fuzzy output
		FuzzyOutput fuzzyOutput = ExecuteInference(variableName);
		double res = defuzzifier.Defuzzify(fuzzyOutput, normOperator);
		return res;
	}

	// Executes the fuzzy inference, obtaining the "FuzzyOutput" of the system
	// for the required "LinguisticVariable"
	public FuzzyOutput ExecuteInference(String variableName) {
		// gets the variable
		LinguisticVariable lingVar = database.GetVariable(variableName);

		// object to store the fuzzy output
		FuzzyOutput fuzzyOutput = new FuzzyOutput(lingVar);

		// select only rules with the variable as output
		Rule[] rules = rulebase.GetRules();
		for (Rule r : rules) {
			if (r.getOutput().getVariable().getName() == variableName) {
				String labelName = r.getOutput().getLabel().getName();
				double firingStrength = r.EvaluateFiringStrength();
				if (firingStrength > 0)
					fuzzyOutput.AddOutput(labelName, firingStrength);
			}
		}

		// returns the fuzzy output obtained
		return fuzzyOutput;
	}
}
