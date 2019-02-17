package com.fuzzy;

import java.util.HashMap;

public class Database {
	// the linguistic variables repository
	private HashMap<String, LinguisticVariable> variables;

	public Database() {
		this.variables = new HashMap<String, LinguisticVariable>();
	}

	public void AddVariable(LinguisticVariable variable) throws Exception {
		// checking for existing name
		if (this.variables.containsKey(variable.getName()))
			throw new Exception(
					"The linguistic variable name already exists in the database.");

		// adding label
		this.variables.put(variable.getName(), variable);
	}

	public void ClearVariables() {
		this.variables.clear();
	}

	public LinguisticVariable GetVariable(String variableName) {
		return variables.get(variableName);
	}
}
