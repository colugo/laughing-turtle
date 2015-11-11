package main.java.avii.editor.metamodel.actionLanguage.codeBlock;

import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockClose;
import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockOpen;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_ForInstanceInInstanceset;

public class CodeBlockOpenFor extends BaseCodeBlock implements ICodeBlockOpen {

	private Syntax_ForInstanceInInstanceset _forSyntax;

	public boolean acceptsCloseBlock(ICodeBlockClose codeBlockClose) {
		return codeBlockClose instanceof CodeBlockCloseFor;
	}
	
	public void setForSyntax(Syntax_ForInstanceInInstanceset forSyntax)
	{
		this._forSyntax = forSyntax;
	}

	public Syntax_ForInstanceInInstanceset getForSyntax() {
		return this._forSyntax;
	}

}
