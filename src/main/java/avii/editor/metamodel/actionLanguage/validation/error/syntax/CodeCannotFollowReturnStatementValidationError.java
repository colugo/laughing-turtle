package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

public class CodeCannotFollowReturnStatementValidationError extends BaseActionLanguageValidationError {

	private int _lineNumber;

	public CodeCannotFollowReturnStatementValidationError(int lineNumber)
	{
		this._lineNumber = lineNumber;
	}
	
	@Override
	public String explainError() {
		String message = "Code cannot follow return statement. The return statement is on line : " + this._lineNumber;
		return message;
	}

}
