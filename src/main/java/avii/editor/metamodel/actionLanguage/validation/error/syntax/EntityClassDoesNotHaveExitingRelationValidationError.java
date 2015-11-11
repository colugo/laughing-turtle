package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class EntityClassDoesNotHaveExitingRelationValidationError extends BaseActionLanguageValidationError {

	private int _line;
	private IActionLanguageSyntax _syntax;
	private EntityRelation _relation;
	private EntityClass _class;
	public EntityClassDoesNotHaveExitingRelationValidationError(EntityClass theClass, int line, IActionLanguageSyntax syntax, EntityRelation relation)
	{
		this._relation = relation;
		this._class = theClass;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ". Class '" + _class.getName() + "' does not have exiting relation '"+ _relation.getName() +"' -> '" + _syntax + "'";
	}

}
