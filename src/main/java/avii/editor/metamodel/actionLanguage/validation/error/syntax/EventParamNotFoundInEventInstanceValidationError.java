package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class EventParamNotFoundInEventInstanceValidationError extends BaseActionLanguageValidationError {

	private String _paramName;
	private EntityEventSpecification _eventSpecification;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public EventParamNotFoundInEventInstanceValidationError(EntityEventSpecification eventSpec, String paramName, int line, IActionLanguageSyntax syntax)
	{
		this._eventSpecification = eventSpec;
		this._paramName = paramName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". An EventParameter called '" + _paramName + "' is required for event : "+_eventSpecification.getName()+" -> '" + _syntax + "'";
	}

}
