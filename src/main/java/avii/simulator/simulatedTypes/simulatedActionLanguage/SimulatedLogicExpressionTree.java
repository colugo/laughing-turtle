package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IAddSubtractOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IEqualityOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IMultiplyDivideOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.AndBooleanOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ClosingBracket;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.MinusArithmeticOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.OpeningBracket;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.OrBooleanOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.BaseLogicParentNode;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.logicNodeProvider.LogicNodeProvider;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenAttribute;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenIdentifier;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenRcvdEvent;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenTemp;
import main.java.avii.util.Text;

public class SimulatedLogicExpressionTree {

	private IExpressionTokenLookup _expressionTokenLookup;
	private String _logic;

	public SimulatedLogicExpressionTree(LogicExpressionTree logicTree) {
		this._logic = logicTree.getRawLogic();
	}

	public void setExpressionTokenLookup(IExpressionTokenLookup theExpressionTokenLookup) {
		this._expressionTokenLookup = theExpressionTokenLookup;
	}

	public Object evaluateExpression() {
		String result = null;
		String logicString = this._logic;

		logicString = replaceVariables(logicString);

		logicString = evaluateBracktededExpressions(logicString);

		result = evaluateNonBracketedExpression(logicString);

		Object objectResult = result;
		objectResult = tryToMakeResultANumber(result);
		objectResult = tryToMakeResultABoolean(objectResult);

		return objectResult;
	}

	private ArrayList<ILogicNode> getTokenArrayListFromString(String logicString) {
		try {
			LogicExpressionTree tree = new LogicExpressionTree(logicString, true);
			ArrayList<ILogicNode> tokens = tree.getAllNodesAsArrayList(new LogicNodeProvider(logicString));
			return tokens;
		} catch (Exception e) {
			// this cant fail - its been validated
			return null;
		}
	}
	
	private ArrayList<ILogicNode> getTokenArrayListFromStringPreservingWhiteSpace(String logicString) {
		try {
			LogicExpressionTree tree = new LogicExpressionTree(logicString, true);
			LogicNodeProvider provider = new LogicNodeProvider(logicString, true);
			ArrayList<ILogicNode> tokens = tree.getAllNodesAsArrayList(provider);
			return tokens;
		} catch (Exception e) {
			// this cant fail - its been validated
			return null;
		}
	}

	private ArrayList<ILogicNode> getTokenOfTypeArrayListFromString(String logicString, Class<?> specificType) {
		try {
			LogicExpressionTree tree = new LogicExpressionTree(logicString, true);
			ArrayList<ILogicNode> tokens = tree.getAllDisconnectedNodesOfType(specificType, logicString);
			return tokens;
		} catch (Exception e) {
			// this cant fail - its been validated
			return new ArrayList<ILogicNode>();
		}
	}

	private Object tryToMakeResultANumber(String result) {
		try {
			double d = Double.parseDouble(result);
			return d;
		} catch (NumberFormatException nfe) {
			return result;
		}
	}

	private Object tryToMakeResultABoolean(Object result) {
		String resultString = result.toString();
		if (resultString.toLowerCase().equals("true")) {
			return true;
		}
		if (resultString.toLowerCase().equals("false")) {
			return false;
		}
		return result;
	}

	private String replaceVariables(String logicString) {
		String replacedLogicString = logicString;

		ArrayList<IActionLanguageToken> nonConstantTokens = this._getNonConstantTokens(replacedLogicString);
		for (IActionLanguageToken token : nonConstantTokens) {
			String nonConstantTokenIdentifier = token.AsString();
			String nonConstantTokenValue = this._expressionTokenLookup.lookupExpressionToken(token);
			replacedLogicString = replacedLogicString.replace(nonConstantTokenIdentifier, nonConstantTokenValue);
		}

		return replacedLogicString;
	}

	private String evaluateBracktededExpressions(String logicString) {
		ArrayList<ILogicNode> tokens = getTokenOfTypeArrayListFromString(logicString, OpeningBracket.class);
		while (!tokens.isEmpty()) {
			String containedBracktedExpression = this.findContainedBracketedExpression(logicString);

			boolean wasContainedBracketedExpressionANot = this.wasContainedBracketedExpressionANot(containedBracktedExpression, logicString);

			String originalContainedBracketedExpression = "(" + containedBracktedExpression + ")";

			String bracketedExpressionResult = evaluateNonBracketedExpression(containedBracktedExpression);

			// replace the bracketed expression with its result
			if (wasContainedBracketedExpressionANot) {
				String negatedBooleanValue = "true";
				// the return has to be a boolean
				if (bracketedExpressionResult.toLowerCase().equals("true")) {
					negatedBooleanValue = "false";
				}
				bracketedExpressionResult = negatedBooleanValue;

				originalContainedBracketedExpression = getNotAndBracketedResultFromOriginalLogicString(containedBracktedExpression, logicString);
			}
			logicString = logicString.replace(originalContainedBracketedExpression, bracketedExpressionResult);

			tokens = getTokenOfTypeArrayListFromString(logicString, OpeningBracket.class);
		}

		return logicString;
	}

	private String getNotAndBracketedResultFromOriginalLogicString(String containedBracktedExpression, String logicString) {
		String resultString = "(" + containedBracktedExpression + ")";
		int index = logicString.indexOf(containedBracktedExpression);
		index -=2;
		while(!resultString.startsWith("NOT"))
		{
			char previous = logicString.charAt(index--);
			resultString = previous + resultString;
		}
		
		return resultString;
	}

	private boolean wasContainedBracketedExpressionANot(String containedBracktedExpression, String logicString) {
		boolean found = logicString.replace(" ","").contains("NOT(" + containedBracktedExpression.replace(" ","") + ")");
		return found;
	}

	public String findContainedBracketedExpression(String logicString) {
		ArrayList<ILogicNode> tokens = getTokenArrayListFromStringPreservingWhiteSpace(logicString);

		int openingBracketIndex = -1;
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i) instanceof OpeningBracket) {
				openingBracketIndex = i;
			}
			if (tokens.get(i) instanceof ClosingBracket) {
				String subExpression = "";
				for (int j = openingBracketIndex + 1; j < i; j++) {
					String subPart = tokens.get(j).getTokenValue();
					subExpression += subPart;
				}
				return subExpression;
			}
		}
		return logicString;
	}

	private boolean isNumber(String logicString) {
		try {
			Float.parseFloat(logicString);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	private String evaluateNonBracketedExpression(String logicString) {	
		// break if already a number
		if(isNumber(logicString))
		{
			return logicString;
		}

		// ARITHMETIC

		// multiply and divide with equal precedence
		ArrayList<ILogicNode> tokens = getTokenOfTypeArrayListFromString(logicString, IMultiplyDivideOperatorToken.class);
		while (!tokens.isEmpty()) {
			logicString = evaluateFirstArithmeticOperatorOfType(logicString, IMultiplyDivideOperatorToken.class);
			tokens = getTokenOfTypeArrayListFromString(logicString, IMultiplyDivideOperatorToken.class);
		}

		// add and subtract ( order as above)
		tokens = getTokenOfTypeArrayListFromString(logicString, IAddSubtractOperatorToken.class);
		while (!tokens.isEmpty()) {
			logicString = evaluateFirstArithmeticOperatorOfType(logicString, IAddSubtractOperatorToken.class);
			tokens = getTokenOfTypeArrayListFromString(logicString, IAddSubtractOperatorToken.class);
			if (isNumber(logicString)) {
				break;
			}

		}

		// EQUALITY
		// ANDs first
		tokens = getTokenOfTypeArrayListFromString(logicString, IEqualityOperatorToken.class);
		while (!tokens.isEmpty()) {
			logicString = evaluateFirstArithmeticOperatorOfType(logicString, IEqualityOperatorToken.class);
			tokens = getTokenOfTypeArrayListFromString(logicString, IEqualityOperatorToken.class);
		}

		// BOOLEAN

		// NOTs are taken care of when evaluating brackets

		// ANDs first
		tokens = getTokenOfTypeArrayListFromString(logicString, AndBooleanOperator.class);
		while (!tokens.isEmpty()) {
			logicString = evaluateFirstArithmeticOperatorOfType(logicString, AndBooleanOperator.class);
			tokens = getTokenOfTypeArrayListFromString(logicString, AndBooleanOperator.class);
		}

		// ORs last
		tokens = getTokenOfTypeArrayListFromString(logicString, OrBooleanOperator.class);
		while (!tokens.isEmpty()) {
			logicString = evaluateFirstArithmeticOperatorOfType(logicString, OrBooleanOperator.class);
			tokens = getTokenOfTypeArrayListFromString(logicString, OrBooleanOperator.class);
		}

		// last ditch effort to remove quotes from literal strings
		logicString = Text.removeOuterQuotes(logicString);

		return logicString;
	}

	private String evaluateFirstArithmeticOperatorOfType(String logicString, Class<?> operatorClassType) {
		ArrayList<ILogicNode> tokens = getTokenArrayListFromString(logicString);

		if (tokens.get(0) instanceof MinusArithmeticOperator && tokens.size() >= 2) {
			String result = "-" + tokens.get(1).getTokenValue();
			tokens.get(0).setValue(result);
			tokens.remove(1);
			String newLogicString = this.calculateLogicFromTokenArray(tokens);
			if (!newLogicString.equals(logicString)) {
				return newLogicString;
			}
		}

		// why is this 2?
		for (int i = 1; i <= tokens.size() - 2; i++) {
			int leftValueIndex = i - 1;
			int operatorIndex = i;
			int rightValueIndex = i + 1;

			ILogicNode operatorNode = tokens.get(operatorIndex);
			ILogicNode leftNode = tokens.get(leftValueIndex);
			ILogicNode rightNode = tokens.get(rightValueIndex);

			if (operatorClassType.isAssignableFrom(operatorNode.getClass())) {
				String leftValue = leftNode.getTokenValue();
				String rightValue = rightNode.getTokenValue();

				leftValue = Text.removeOuterQuotes(leftValue);
				rightValue = Text.removeOuterQuotes(rightValue);

				Object localResult = ((BaseLogicParentNode) operatorNode).calculateValue(leftValue, rightValue);
				if (localResult instanceof String) {
					localResult = Text.quote(localResult.toString());
				}

				operatorNode.setValue(localResult.toString());
				leftNode.clearValue();
				rightNode.clearValue();
				String newLogicString = this.calculateLogicFromTokenArray(tokens);

				return newLogicString;
			}
		}
		// cant get here - passed validation
		return null;
	}

	private String calculateLogicFromTokenArray(ArrayList<ILogicNode> tokens) {
		String value = "";
		for (ILogicNode token : tokens) {
			value += token.getTokenValue();
		}
		return value;
	}

	private ArrayList<IActionLanguageToken> _getNonConstantTokens(String logicString) {
		ArrayList<IActionLanguageToken> values = new ArrayList<IActionLanguageToken>();

		ArrayList<ILogicNode> nodes = getTokenArrayListFromString(logicString);

		for (ILogicNode node : nodes) {

			if (node instanceof ConstantToken) {
				String tokenValue = node.getTokenValue();
				IActionLanguageToken nodeToken = ActionLanguageTokenIdentifier.IdentifyToken(tokenValue);
				if (nodeToken instanceof ActionLanguageTokenAttribute) {
					values.add(nodeToken);
				}
				if (nodeToken instanceof ActionLanguageTokenTemp) {
					values.add(nodeToken);
				}
				if (nodeToken instanceof ActionLanguageTokenRcvdEvent) {
					values.add(nodeToken);
				}
			}
		}

		return values;
	}

}
