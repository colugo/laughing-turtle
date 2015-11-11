package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

@SuppressWarnings("serial")
public class EncounteredNonItemLeafNodeException extends Exception {
	private ILogicNode _errorNode;
	private String _rawExpression;
	
	
	public String getRawExpression()
	{
		return _rawExpression;
	}
	
	public EncounteredNonItemLeafNodeException(ILogicNode errorNode, String rawExpression)
	{
		this._rawExpression = rawExpression;
		this._errorNode = errorNode;
	}
	
	public String toString()
	{
		return "Encountered non leaf token '"+ _errorNode +"' when processing expression '" + _rawExpression + "'.";
	}
}
