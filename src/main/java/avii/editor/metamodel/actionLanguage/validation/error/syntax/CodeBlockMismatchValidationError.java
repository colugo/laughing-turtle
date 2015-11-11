package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlock;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class CodeBlockMismatchValidationError extends BaseActionLanguageValidationError {

	private ICodeBlock _codeBlock;
	private IActionLanguageSyntax _lineSyntax;
	private int _lineNumber;

	public CodeBlockMismatchValidationError(ICodeBlock codeBlock) {
		this._codeBlock = codeBlock;
	}
	
	public CodeBlockMismatchValidationError(IActionLanguageSyntax syntax, int lineNumber)
	{
		this._lineSyntax = syntax;
		this._lineNumber = lineNumber;
	}
	
	public CodeBlockMismatchValidationError() {
	}

	public String explainError()
	{
		if(this._codeBlock != null)
		{
			return "There was a mismatch in the code block structure. Check that all FOR loops have a matching END FOR and all IF/ELSE(s) have a matching END IF. Also ensure that an END FOR is not matched to an IF - " + this._codeBlock.getClass().getName();
		}
		if(_lineSyntax != null)
		{
			return "line : " + _lineNumber + " -> '" + _lineSyntax + "' Created an error in the code block structure";
		}
		return "There was a mismatch in the code block structure. Check that all FOR loops have a matching END FOR and all IF/ELSE(s) have a matching END IF. Also ensure that an END FOR is not matched to an IF";
	}

}
