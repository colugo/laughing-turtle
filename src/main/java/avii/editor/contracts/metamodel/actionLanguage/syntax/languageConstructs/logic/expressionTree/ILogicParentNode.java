package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

public interface ILogicParentNode extends ILogicNode {

	public void setLeft(ILogicNode left);
	public void setRight(ILogicNode right);
	public ILogicNode getLeft();
	public ILogicNode getRight();
	
}
