package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class EventParamNotFoundInEventSpecificationValidationError extends BaseActionLanguageValidationError {

	private String _paramName;
	private EntityEventSpecification _eventSpecification;
	private int _line;
	private IActionLanguageSyntax _syntax;
	public EventParamNotFoundInEventSpecificationValidationError(EntityEventSpecification eventSpec, String paramName, int line, IActionLanguageSyntax syntax)
	{
		this._eventSpecification = eventSpec;
		this._paramName = paramName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Could not find an EventParameter called '" + _paramName + "' on event : "+_eventSpecification.getName()+" -> '" + _syntax + "'";
	}

}
