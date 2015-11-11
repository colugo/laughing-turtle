package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class EventNotGeneratedFromClassValidationError extends BaseActionLanguageValidationError {

	private String _className;
	private String _eventName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public EventNotGeneratedFromClassValidationError(String className,String eventName, int line, IActionLanguageSyntax syntax)
	{
		this._className = className;
		this._eventName = eventName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Could not find a generation syntax for Event called '" + _eventName + "' in class : "+_className+" -> '" + _syntax + "'";
	}

}
