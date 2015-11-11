/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IArithmeticOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IBooleanOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IEqualityOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.CannotInterpretExpressionNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.EncounteredNonItemLeafNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicParentNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ClosingBracket;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.NotBooleanOperatorToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.OpeningBracket;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.logicNodeProvider.LogicNodeProvider;
import main.java.avii.util.Text;

public class LogicExpressionTree {

	public class OpeningClosingRange {
		public int opening;
		public int closing;

		public OpeningClosingRange(int o, int c) {
			this.opening = o;
			this.closing = c;
		}
	}

	private String _rawLogic = "";
	private String _invalidMessage = null;
	private ArrayList<LogicTreeValidationException> _validationErrors;
	
	public Boolean isValid()
	{
		return this._invalidMessage == null;
	}
	
	private void invalidate(String message)
	{
		this._invalidMessage = message;
	}
	
	public String getInvalidMessage()
	{
		return this._invalidMessage;
	}
	
	private void setupForNegativeNumbers()
	{
		this._rawLogic = Text.wrapNegativeNumbersInBrackets(this._rawLogic);
		this._rawLogic = Text.replaceStringWhenNotWithinQuotes(this._rawLogic, "(-", "(0-");
	}
	
	
	public LogicExpressionTree(String rawLogic){
		this._rawLogic = rawLogic;
		//this.setupForNegativeNumbers();
		if(rawLogic.trim().length() == 0)
		{
			invalidate("Expression cannot be empty");
		}
		try {
			new LogicNodeProvider(this._rawLogic);
		} catch (CannotInterpretExpressionNodeException cannotInterpretExpression) {
			invalidate(cannotInterpretExpression.getRawExpression());
		}
	}
	
	public LogicExpressionTree(String rawLogic, boolean useAlternateTokens){
		this._rawLogic = rawLogic;
		this.setupForNegativeNumbers();
		if(rawLogic.trim().length() == 0)
		{
			invalidate("Expression cannot be empty");
		}
		try {
			new LogicNodeProvider(this._rawLogic);
		} catch (CannotInterpretExpressionNodeException cannotInterpretExpression) {
			invalidate(cannotInterpretExpression.getRawExpression());
		}
	}

	public String getRawLogic() {
		return _rawLogic;
	}

	public ILogicNode getRootNode() throws CannotInterpretExpressionNodeException {
		if(!this.isValid())
		{
			throw new CannotInterpretExpressionNodeException("Cannot get root of invalid expression!");
		}
		ArrayList<ILogicNode> tokens = getAllNodesAsArrayList(new LogicNodeProvider(this._rawLogic));
		while (tokensContainsBrackets(tokens)) {
			tokens = replaceBracketedExpressionWithEvaluatedNode(tokens);
		}
		ArrayList<ILogicNode> collapsedTokens = collapseEqualityParents(new LogicNodeProvider(tokens));
		if(collapsedTokens.size()!= 1)
		{
			throw new CannotInterpretExpressionNodeException("Encountered multiple root expressions : " + getMultiRootExpressionString(collapsedTokens));
		}
		return collapsedTokens.get(0);
	}

	private String getMultiRootExpressionString(ArrayList<ILogicNode> tokens)
	{
		String output = "\n";
		for(ILogicNode root : tokens)
		{
			output += root.asString() + "\n";
		}
		return output;
	}
	
	private ArrayList<ILogicNode> replaceBracketedExpressionWithEvaluatedNode(ArrayList<ILogicNode> tokens) throws CannotInterpretExpressionNodeException {
		boolean bracketsAreForNegation = false;
		OpeningClosingRange range = findFirstSelfContainedBracketRange(tokens);
		ArrayList<ILogicNode> before = getSubList(tokens, 0, range.opening - 1);
		ArrayList<ILogicNode> brackets = getSubList(tokens, range.opening + 1, range.closing - 1);
		ArrayList<ILogicNode> after = getSubList(tokens, range.closing + 1, tokens.size() - 1);

		ArrayList<ILogicNode> collapsedBrackets = collapseEqualityParents(new LogicNodeProvider(brackets));

		if(!before.isEmpty())
		{
			if(before.get(before.size()-1) instanceof NotBooleanOperatorToken)
			{
				bracketsAreForNegation = true;
			}
		}
		
		if(bracketsAreForNegation)
		{
			if(collapsedBrackets.size() != 1)
			{
				throw new CannotInterpretExpressionNodeException("Encountered multiple root expressions : " + getMultiRootExpressionString(collapsedBrackets));
			}
			NotBooleanOperatorToken notToken = (NotBooleanOperatorToken) before.get(before.size()-1);
			ILogicNode negatedRoot = collapsedBrackets.get(0);
			notToken.addNegatedRoot(negatedRoot);
			before.addAll(after);
			return before;
		}
		else
		{
			collapsedBrackets.addAll(after);
			before.addAll(collapsedBrackets);
			return before;
		}
	}

	public ArrayList<ILogicNode> getSubList(ArrayList<ILogicNode> tokens, int opening, int closing) {
		ArrayList<ILogicNode> subList = new ArrayList<ILogicNode>();
		for (int i = opening; i <= closing; i++) {
			subList.add(tokens.get(i));
		}
		return subList;
	}

	public OpeningClosingRange findFirstSelfContainedBracketRange(ArrayList<ILogicNode> tokens) {
		int openingBracket = -1;
		for (int i = 0; i < tokens.size(); i++) {
			ILogicNode token = tokens.get(i);

			if (token instanceof OpeningBracket) {
				openingBracket = i;
			}

			if (token instanceof ClosingBracket) {
				return new OpeningClosingRange(openingBracket, i);
			}
		}
		return new OpeningClosingRange(0, tokens.size() - 1);
	}

	private boolean tokensContainsBrackets(ArrayList<ILogicNode> tokens) {
		for (ILogicNode node : tokens) {
			if (node instanceof OpeningBracket) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<ILogicNode> getAllNodesAsArrayList(LogicNodeProvider provider) {
		ArrayList<ILogicNode> tokens = new ArrayList<ILogicNode>();
		while (provider.hasNext()) {
			ILogicNode next = provider.next();
			tokens.add(next);
		}
		return tokens;
	}
	
	
	public ArrayList<ILogicNode> buildArithmeticParents(LogicNodeProvider provider) {
		ArrayList<ILogicNode> tokens = new ArrayList<ILogicNode>();
		while (provider.hasNext()) {
			ILogicNode next = provider.next();
			if (!next.getHasBeenArithmeticProcessed()) {
				next.setHasBeenArithmeticProcessed();
				if (next instanceof IArithmeticOperatorToken) {
					ILogicNode leftOperand = popLastToken(tokens);
					ILogicParentNode equality = (ILogicParentNode) next;
					equality.setLeft(leftOperand);
					ILogicNode right = provider.next();
					equality.setRight(right);
					tokens.add(equality);
				} else {
					tokens.add(next);
				}
			} else {
				tokens.add(next);
			}
		}
		return tokens;
	}


	
	public ArrayList<ILogicNode> buildEqualityParents(LogicNodeProvider initialProvider) {
		ArrayList<ILogicNode> arithmeticParents = buildArithmeticParents(initialProvider);
		LogicNodeProvider provider = new LogicNodeProvider(arithmeticParents);
		ArrayList<ILogicNode> tokens = new ArrayList<ILogicNode>();

		while (provider.hasNext()) {
			ILogicNode next = provider.next();
			if (!next.getHasBeenEqualityProcessed()) {
				next.setHasBeenEqualityProcessed();
				if (next instanceof IEqualityOperatorToken) {
					ILogicNode left = popLastToken(tokens);
					ILogicNode right = provider.next();
					ILogicParentNode bool = (ILogicParentNode) next;
					bool.setLeft(left);
					bool.setRight(right);
					tokens.add(bool);
				} else {
					tokens.add(next);
				}
			} else {
				tokens.add(next);
			}
		}
		return tokens;
	}

	public ArrayList<ILogicNode> collapseEqualityParents(LogicNodeProvider initialProvider) {
		ArrayList<ILogicNode> equalityParents = buildEqualityParents(initialProvider);
		LogicNodeProvider provider = new LogicNodeProvider(equalityParents);
		ArrayList<ILogicNode> tokens = new ArrayList<ILogicNode>();

		while (provider.hasNext()) {
			ILogicNode next = provider.next();
			if (!next.getHasBeenBooleanProcessed()) {
				next.setHasBeenBooleanProcessed();
				if (next instanceof IBooleanOperatorToken) {
					ILogicNode left = popLastToken(tokens);
					ILogicNode right = provider.next();
					ILogicParentNode bool = (ILogicParentNode) next;
					bool.setLeft(left);
					bool.setRight(right);
					tokens.add(bool);
				} else {
					tokens.add(next);
				}
			} else {
				tokens.add(next);
			}
		}
		return tokens;
	}

	private ILogicNode popLastToken(ArrayList<ILogicNode> tokens) {
		if (tokens.isEmpty()) {
			return null;
		}
		ILogicNode last = tokens.get(tokens.size() - 1);
		tokens.remove(tokens.size() - 1);
		return last;
	}

	public Stack<String> breakExpressionIntoBracketedStatements() {
		Stack<String> statements = new Stack<String>();

		return statements;
	}

	@Override
	public String toString()
	{
		if(!this.isValid())
		{
			return "invalid expression tree";
		}
		try {
			return getRootNode().asString();
		} catch (CannotInterpretExpressionNodeException e) {
			return e.getRawExpression();
		}
	}

	
	private ArrayList<ConstantToken> convertILogicNodeLeavesToConstantTokens(ArrayList<ILogicNode> leafNodes) throws EncounteredNonItemLeafNodeException{
		ArrayList<ConstantToken> leafConstantTokens = new ArrayList<ConstantToken>();
		for(ILogicNode node : leafNodes)
		{
			if(node instanceof ConstantToken){
				leafConstantTokens.add((ConstantToken)node);
			}
			else
			{
				throw new EncounteredNonItemLeafNodeException(node,_rawLogic);
			}
		}
		return leafConstantTokens;
	}
	
	public ArrayList<ConstantToken> getLeafNodes() throws EncounteredNonItemLeafNodeException {
		ArrayList<ILogicNode> leafNodes = new ArrayList<ILogicNode>();
		
		ILogicNode root;
		try {
			root = getRootNode();
		} catch (CannotInterpretExpressionNodeException e) {
			return  new ArrayList<ConstantToken>();
		}
		
		root.getLeafNodes(leafNodes);
		
		return convertILogicNodeLeavesToConstantTokens(leafNodes);
	}

	public ArrayList<ILogicNode> getAllNodesOfType(Class<?> specificType) throws CannotInterpretExpressionNodeException {
		ArrayList<ILogicNode> allNodesOrig = new ArrayList<ILogicNode>();
		ArrayList<ILogicNode> specificNodesOrig = new ArrayList<ILogicNode>();
		getRootNode().getAllNodes(allNodesOrig);
		for(ILogicNode currentNode : allNodesOrig)
		{
			ArrayList<Class<?>> interfacesForNode = new ArrayList<Class<?>>(Arrays.asList(currentNode.getClass().getInterfaces()));
			if(interfacesForNode.contains(specificType))
			{
				specificNodesOrig.add(currentNode);
			}
		}
		return specificNodesOrig;
	}
	
	public ArrayList<ILogicNode> getAllDisconnectedNodesOfType(Class<?> specificType, String logicString) throws CannotInterpretExpressionNodeException
	{
		ArrayList<ILogicNode> allNodes = getAllNodesAsArrayList(new LogicNodeProvider(logicString));
		ArrayList<ILogicNode> specificNodes = new ArrayList<ILogicNode>(); 
		for(ILogicNode currentNode : allNodes)
		{
			ArrayList<Class<?>> interfacesForNode = new ArrayList<Class<?>>(Arrays.asList(currentNode.getClass().getInterfaces()));
			interfacesForNode.add(currentNode.getClass());
			if(interfacesForNode.contains(specificType))
			{
				specificNodes.add(currentNode);
			}
		}
		return specificNodes;

	}


	public boolean validate(IActionLanguageTokenIdentifier tokenIdentifier)
	{
		try {
			ILogicNode rootNode = this.getRootNode();
			rootNode.validate(tokenIdentifier);

			this._validationErrors = rootNode.getValidationErrors();
			return this._validationErrors.isEmpty();
		} catch (CannotInterpretExpressionNodeException e) {
			return false;
		}
	}
	
	public ArrayList<LogicTreeValidationException> getValidationErrors() {
		return this._validationErrors;
	}
	
}
