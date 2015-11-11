package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityClass;

public class EntityClassesAreNotInTheSameGeneralisationValidationError extends BaseActionLanguageValidationError {

	
	private IActionLanguageSyntax _lineSyntax;
	private int _lineNumber;
	private EntityClass _classA;
	private EntityClass _classB;

	public EntityClassesAreNotInTheSameGeneralisationValidationError(EntityClass classA, EntityClass classB, IActionLanguageSyntax lineSyntax, int lineNumber) {
		this._lineSyntax = lineSyntax;
		this._lineNumber = lineNumber;
		this._classA = classA;
		this._classB = classB;
	}
	
	public String explainError()
	{
		return "line : " + _lineNumber + " -> '" + _lineSyntax + "' - '" + _classA.getName() + "' and '" + _classB.getName() + "' are not in the same generalisation.";
	}

}
