package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.logicNodeProvider;

import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.ILogicToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IMatchedToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.CannotInterpretExpressionNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.AndBooleanOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ClosingBracket;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantNumericToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantStringToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.DivisionArithmeticOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.GenericEqualityToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.GreaterThenEqualityToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.GreaterThenOrEqualToEqualityToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.LessThenEqualityToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.LessThenOrEqualToEqualityToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.MatchedToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.MinusArithmeticOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.MultiplicationArithmeticOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.NotBooleanOperatorToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.NotEqualityToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.OpeningBracket;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.OrBooleanOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.PlusArithmeticOperator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.WhiteSpace;

public class LogicNodeProvider {

	private static ArrayList<ILogicToken> tokenTypes = new ArrayList<ILogicToken>() {
		private static final long serialVersionUID = -4339658956764494270L;
		{
			add(new AndBooleanOperator());
			add(new ClosingBracket());
			add(new ConstantNumericToken());
			add(new ConstantStringToken());
			add(new DivisionArithmeticOperator());
			add(new GenericEqualityToken());
			add(new GreaterThenEqualityToken());
			add(new GreaterThenOrEqualToEqualityToken());
			add(new LessThenEqualityToken());
			add(new LessThenOrEqualToEqualityToken());
			add(new MinusArithmeticOperator());
			add(new MultiplicationArithmeticOperator());
			add(new NotBooleanOperatorToken());
			add(new NotEqualityToken());
			add(new OrBooleanOperator());
			add(new OpeningBracket());
			add(new PlusArithmeticOperator());
			add(new WhiteSpace());
		}
	};

	private ArrayList<ILogicNode> _tokens = new ArrayList<ILogicNode>();
	private int _position = 0;
	private String _expression = "";
	private ArrayList<ILogicToken> usedTokenTypes = LogicNodeProvider.tokenTypes;

	private boolean _preserveWhiteSpace = false;
	

	public LogicNodeProvider(String expression) throws CannotInterpretExpressionNodeException {
		this._expression = expression;
		this.parseToTokens();
	}
	
	public LogicNodeProvider(String expression, boolean preserveWhiteSpace) throws CannotInterpretExpressionNodeException {
		this._expression = expression;
		this._preserveWhiteSpace = preserveWhiteSpace;
		this.parseToTokens();
	}

	public LogicNodeProvider(ArrayList<ILogicNode> theTokens) {
		this._tokens = theTokens;
		this._position = 0;
	}
	
	private void parseToTokens() throws CannotInterpretExpressionNodeException {
		String line = this._expression;
		boolean tokenMatched = true;
		int offset = 0;
		_tokens = new ArrayList<ILogicNode>();
		while (line.length() > 0 && tokenMatched) {
			tokenMatched = false;
			
			// greedy match of strings
			if(line.startsWith("\""))
			{
				int endingQuote = line.indexOf("\"", 1) + 1;
				
				// mismatched quotes
				if(endingQuote == 0)
				{
					throw new CannotInterpretExpressionNodeException(this._expression);
				}
				String quotedString = line.substring(0,endingQuote);
				
				
				ConstantToken constantToken = new ConstantStringToken();
				constantToken.forceInternalValue(quotedString);
				MatchedToken match = new MatchedToken(constantToken, quotedString);
				tokenMatched = true;
				appendToken(match);
				
				line = line.substring(quotedString.length());
				offset += quotedString.length();
			}
			else
			{
				for (ILogicToken token : usedTokenTypes) {
					ILogicToken newToken = null;
					try {
						newToken = token.getClass().newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
					IMatchedToken match = newToken.matchesString(line, offset);
					
					if(match.isMatchFound() && token instanceof ConstantNumericToken)
					{
						// check the previous token was not another constant numeric
						if(!this._tokens.isEmpty())
						{
							if(this._tokens.get(this._tokens.size()-1) instanceof ConstantNumericToken)
							{
								continue;
							}
						}
					}
					
					if (match.isMatchFound()) {
						line = line.substring(match.getMatch().length());
						offset += match.getMatch().length();
						appendToken(match);
						tokenMatched = true;
						break;
					}
				}
			}
			if (!tokenMatched) {
				throw new CannotInterpretExpressionNodeException(line);
			}
		}

		if (!checkExpressionHasCorrectBracketing()) {
			throw new CannotInterpretExpressionNodeException(line);
		}
	}

	private boolean checkExpressionHasCorrectBracketing() {
		int bracketCount = 0;
		try {

			for (ILogicNode next : _tokens) {
				if (next instanceof OpeningBracket) {
					bracketCount++;
				}
				if (next instanceof ClosingBracket) {
					bracketCount--;
				}
				if(bracketCount <0)
				{
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return bracketCount == 0;
	}

	public void preserveWhiteSpace() {
		this._preserveWhiteSpace  = true;
	}
	
	private void appendToken(IMatchedToken match) {
		if(this._preserveWhiteSpace)
		{
			_tokens.add((ILogicNode) match.getMatchedTokenType());
			return;
		}
		
		if (match.getMatchedTokenType().getClass() != WhiteSpace.class) {
			_tokens.add((ILogicNode) match.getMatchedTokenType());
		}
	}

	public boolean hasNext() {
		return this._position < this._tokens.size();
	}

	private ILogicNode getNodeAtPositionInArrayOrNull() {
		if (_position >= 0 && this.hasNext()) {
			return _tokens.get(_position);
		}
		return null;
	}

	public void advance() {
		advance(1);
	}

	public void advance(int amount) {
		if (this.hasNext()) {
			_position += amount;
		}
	}

	public ILogicNode next() {
		ILogicNode next = getNodeAtPositionInArrayOrNull();
		advance();
		return next;
	}



}
