package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_ForInstanceInInstanceset;


public class CanNotChangeIteratingInstanceSetValidationError extends BaseActionLanguageValidationError {

	private int _line;
	private IActionLanguageSyntax _selectSyntax;
	private Syntax_ForInstanceInInstanceset _forSyntax;

	public CanNotChangeIteratingInstanceSetValidationError(int lineNumber, IActionLanguageSyntax selectSyntax, Syntax_ForInstanceInInstanceset forSyntax) {
		this._line = lineNumber;
		this._selectSyntax = selectSyntax;
		this._forSyntax = forSyntax;
	}
	
	@Override
	public String explainError() {
		return "Line : " + _line + ". Can not change the instance set '" + this._selectSyntax.toString() + "' which is iterating from line '" + _forSyntax + "'";
	}

}
