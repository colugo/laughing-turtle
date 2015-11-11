package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class CreatorEventIsNotFromInitialState extends BaseActionLanguageValidationError {

	private String _className;
	private String _eventName;
	private int _line;
	private IActionLanguageSyntax _syntax;
	private String _initialStateName;
	public CreatorEventIsNotFromInitialState(String className,String eventName, String initialStateName, int line, IActionLanguageSyntax syntax)
	{
		this._className = className;
		this._eventName = eventName;
		this._initialStateName = initialStateName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Creator event: '" + _eventName + "' on class : "+_className+" must exit the initial state : '" + this._initialStateName+ "' -> '" + _syntax + "'";
	}

}
