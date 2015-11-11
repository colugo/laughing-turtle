package main.java.avii.editor.metamodel.actionLanguage.codeBlock;

import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlock;

public class BaseCodeBlock implements ICodeBlock {

	private int _lineNumber;

	public void setLineNumber(int lineNumber)
	{
		this._lineNumber = lineNumber;
	}
	
	public int getLineNumber()
	{
		return this._lineNumber;
	}
	
}
