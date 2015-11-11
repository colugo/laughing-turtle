package main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock;

public interface ICodeBlockOpen extends ICodeBlock{
	public boolean acceptsCloseBlock(ICodeBlockClose codeBlockClose);
}
