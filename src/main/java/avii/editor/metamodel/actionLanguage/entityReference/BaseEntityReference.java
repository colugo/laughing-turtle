package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;


public abstract class BaseEntityReference {

	private int _lineNumber;
	private IActionLanguageSyntax _syntax;

	public BaseEntityReference(int lineNumber, IActionLanguageSyntax syntax) {
		this._lineNumber = lineNumber;
		this._syntax = syntax;
	}

	public int getReferencedLineNumber() {
		return this._lineNumber;
	}

	public String getReferencedLineText() {
		return this._syntax.toString();
	}
	
	public IActionLanguageSyntax getSyntax()
	{
		return this._syntax;
	}
	
}
