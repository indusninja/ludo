package com.fuzzy;

import java.util.HashMap;

public class Rulebase {

	// the fuzzy rules repository
	private HashMap<String, Rule> rules;

	public Rulebase() {
		// instance of the rules list
		this.rules = new HashMap<String, Rule>();
	}

	public void AddRule(Rule rule) throws Exception {
		// checking for existing name
		if (this.rules.containsKey(rule.getName()))
			throw new Exception(
					"The fuzzy rule name already exists in the rulebase.");

		// adding rule
		this.rules.put(rule.getName(), rule);
	}

	public void ClearRules() {
		this.rules.clear();
	}

	public Rule GetRule(String ruleName) {
		return rules.get(ruleName);
	}

	public Rule[] GetRules() {
		Rule[] r = new Rule[rules.size()];

		int i = 0;
		for (Rule kvp : rules.values())
			r[i++] = kvp;

		return r;
	}
}
