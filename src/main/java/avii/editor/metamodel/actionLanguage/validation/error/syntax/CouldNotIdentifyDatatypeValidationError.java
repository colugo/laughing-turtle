package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;

public class CouldNotIdentifyDatatypeValidationError extends BaseActionLanguageValidationError {

	private IActionLanguageToken _token;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public CouldNotIdentifyDatatypeValidationError(IActionLanguageToken token, int line, IActionLanguageSyntax syntax)
	{
		this._token = token;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Couldn't identify datatype from token '" + _token.AsString() + "' -> '" + _syntax + "'";
	}

}
