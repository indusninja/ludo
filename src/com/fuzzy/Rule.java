package com.fuzzy;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Rule {

	// name of the rule
	private String name;
	// the original expression
	private String rule;
	// the parsed RPN (reverse polish notation) expression
	private List<Object> rpnTokenList;
	// the output of the rule
	private Clause output;
	// the database with the linguistic variables
	private Database database;
	// the norm operator
	private INorm normOperator;
	// the conorm operator
	private ICoNorm conormOperator;
	// the complement operator
	private IUnaryOperator notOperator;
	// the unary operators that the rule parser supports
	private String unaryOperators = "NOT;VERY";

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	public Clause getOutput() {
		return output;
	}

	// Initializes a new instance of the Rule class.
	public Rule(Database fuzzyDatabase, String name, String rule,
			INorm normOperator, ICoNorm coNormOperator) throws Exception {
		// the list with the RPN expression
		rpnTokenList = new ArrayList<Object>();

		// setting attributes
		this.name = name;
		this.rule = rule;
		this.database = fuzzyDatabase;
		this.normOperator = normOperator;
		this.conormOperator = coNormOperator;
		this.notOperator = new NotOperator();

		// parsing the rule to obtain RPN of the expression
		ParseRule();
	}

	// Initializes a new instance of the Rule class using as
	// CoNorm the MaximumNorm and as Norm the MinimumNorm.
	public Rule(Database fuzzyDatabase, String name, String rule)
			throws Exception {
		this(fuzzyDatabase, name, rule, new MinimumNorm(), new MaximumCoNorm());
	}

	// Converts the RPN fuzzy expression into a String representation.
	public String GetRPNExpression() {
		String result = "";
		for (Object o : rpnTokenList) {
			// if its a fuzzy clause we can call clause's ToString()
			if (o instanceof Clause) {
				Clause c = (Clause) o;
				result += c.toString();
			} else
				result += o.toString();
			result += ", ";
		}
		result += "#";
		result = result.replaceAll(", #", "");
		return result;
	}

	// Defines the priority of the fuzzy operators.
	private int Priority(String Operator) {
		// if its unary
		if (unaryOperators.indexOf(Operator) >= 0)
			return 4;

		if (Operator == "(")
			return 1;
		else if (Operator == "OR")
			return 2;
		else if (Operator == "AND")
			return 3;
		return 0;
	}

	// / Converts the Fuzzy Rule to RPN (Reverse Polish Notation). For debug
	// proposes, the String representation of the
	// / RPN expression can be accessed by calling GetRPNExpression method.
	private void ParseRule() throws Exception {
		// flag to indicate we are on consequent state
		boolean consequent = false;

		// tokens like IF and THEN will be searched always in upper case
		String upRule = rule.toUpperCase();

		// the rule must start with IF, and must have a THEN somewhere
		if (!upRule.startsWith("IF"))
			throw new Exception("A Fuzzy Rule must start with an IF statement.");
		if (upRule.indexOf("THEN") < 0)
			throw new Exception("Missing the consequent (THEN) statement.");

		// building a list with all the expression (rule) String tokens
		String spacedRule = rule.replaceAll("(", " ( ").replaceAll(")", " ) ");
		// getting the tokens list
		String[] tokensList = GetRuleTokens(spacedRule);

		// stack to convert to RPN
		Stack<String> s = new Stack<String>();
		// storing the last token
		String lastToken = "IF";
		// linguistic var read, used to build clause
		LinguisticVariable lingVar = null;

		// verifying each token
		for (int i = 0; i < tokensList.length; i++) {
			// removing spaces
			String token = tokensList[i].trim();
			// getting upper case
			String upToken = token.toUpperCase();

			// ignoring these tokens
			if (upToken == "" || upToken == "IF")
				continue;

			// if the THEN is found, the rule is now on consequent
			if (upToken == "THEN") {
				lastToken = upToken;
				consequent = true;
				continue;
			}

			// if we got a linguistic variable, an IS statement and a label is
			// needed
			if (lastToken == "VAR") {
				if (upToken == "IS")
					lastToken = upToken;
				else
					throw new Exception(
							"An IS statement is expected after a linguistic variable.");
			}
			// if we got an IS statement, a label must follow it
			else if (lastToken == "IS") {
				FuzzySet fs = lingVar.GetLabel(token);
				Clause c = new Clause(lingVar, fs);
				if (consequent)
					output = c;
				else
					rpnTokenList.add(c);
				lastToken = "LAB";
			}
			// not VAR and not IS statement
			else {
				// Opening new scope
				if (upToken == "(") {
					// if we are on consequent, only variables can be found
					if (consequent)
						throw new Exception(
								"Linguistic variable expected after a THEN statement.");
					// if its a (, just push it
					s.push(upToken);
					lastToken = upToken;
				}
				// operators
				else if (upToken == "AND" || upToken == "OR"
						|| unaryOperators.indexOf(upToken) >= 0) {
					// if we are on consequent, only variables can be found
					if (consequent)
						throw new Exception(
								"Linguistic variable expected after a THEN statement.");

					// pop all the higher priority operators until the stack is
					// empty
					while ((s.size() > 0)
							&& (Priority(s.peek()) > Priority(upToken)))
						rpnTokenList.add(s.pop());

					// pushing the operator
					s.push(upToken);
					lastToken = upToken;
				}
				// closing the scope
				else if (upToken == ")") {
					// if we are on consequent, only variables can be found
					if (consequent)
						throw new Exception(
								"Linguistic variable expected after a THEN statement.");

					// if there is nothing on the stack, an opening parenthesis
					// is missing.
					if (s.size() == 0)
						throw new Exception("Opening parenthesis missing.");

					// pop the tokens and copy to output until opening is found
					while (s.peek() != "(") {
						rpnTokenList.add(s.pop());
						if (s.size() == 0)
							throw new Exception("Opening parenthesis missing.");
					}
					s.pop();

					// saving last token...
					lastToken = upToken;
				}
				// finally, the token is a variable
				else {
					// find the variable
					lingVar = database.GetVariable(token);
					lastToken = "VAR";
				}
			}
		}

		// popping all operators left in stack
		while (s.size() > 0)
			rpnTokenList.add(s.pop());
	}

	// / Performs a pre-processing on the rule, placing unary operators in proper
	// position and breaking the String
	// / space separated tokens. Takes Rule in String format. Returns an array
	// of Strings with tokens of the rule.
	private String[] GetRuleTokens(String rule) {
		// breaking in tokens
		String[] tokens = rule.split(" ");

		// looking for unary operators
		for (int i = 0; i < tokens.length; i++) {
			// if its unary and there is an "IS" token before, we must change
			// positions
			if ((unaryOperators.indexOf(tokens[i].toUpperCase()) >= 0)
					&& (i > 1) && (tokens[i - 1].toUpperCase() == "IS")) {
				// placing VAR name
				tokens[i - 1] = tokens[i - 2];
				tokens[i - 2] = tokens[i];
				tokens[i] = "IS";
			}
		}

		return tokens;
	}

	// Evaluates the firing strength of the Rule, the degree of confidence that
	// the consequent of this Rule
	// must be executed. The firing strength [0..1] of the Rule.
	public double EvaluateFiringStrength() {
		// Stack to store the operand values
		Stack<Double> s = new Stack<Double>();

		// Logic to calculate the firing strength
		for (Object o : rpnTokenList) {
			// if its a clause, then its value must be calculated and pushed
			if (o instanceof Clause) {
				Clause c = (Clause) o;
				s.push(c.Evaluate());
			}
			// if its an operator (AND / OR) the operation is performed and the
			// result
			// returns to the stack
			else {
				double y = s.pop();
				double x = 0;

				// unary pops only one value
				if (unaryOperators.indexOf(o.toString()) < 0)
					x = s.pop();

				// operation
				if (o.toString().equalsIgnoreCase("AND")) {
					s.push(normOperator.Evaluate(x, y));
				} else if (o.toString().equalsIgnoreCase("OR")) {
					s.push(conormOperator.Evaluate(x, y));
				} else if (o.toString().equalsIgnoreCase("NOT")) {
					s.push(notOperator.Evaluate(y));
				}
			}
		}

		// result on the top of stack
		return s.pop();
	}
}
