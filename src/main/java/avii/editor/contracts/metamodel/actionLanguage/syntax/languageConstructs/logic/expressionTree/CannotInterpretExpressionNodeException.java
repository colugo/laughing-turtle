package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

@SuppressWarnings("serial")
public class CannotInterpretExpressionNodeException extends Exception {
	private String _rawExpression;
	
	
	public String getRawExpression()
	{
		return _rawExpression;
	}
	
	public CannotInterpretExpressionNodeException(String rawExpression)
	{
		this._rawExpression = rawExpression;
	}
	
	public String toString()
	{
		return "Encountered unknown token when processing expression '" + _rawExpression + "'.";
	}
}
