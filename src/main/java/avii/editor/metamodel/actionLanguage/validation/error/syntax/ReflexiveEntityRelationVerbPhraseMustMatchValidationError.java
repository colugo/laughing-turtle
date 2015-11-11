package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class ReflexiveEntityRelationVerbPhraseMustMatchValidationError extends BaseActionLanguageValidationError {

	private int _line;
	private IActionLanguageSyntax _syntax;
	private String _relationName;
	private String _verbPhrase;
	private String _verbA;
	private String _verbB;

	public ReflexiveEntityRelationVerbPhraseMustMatchValidationError(String specifiedVerbPhrase, EntityRelation relation, int lineNumber,
			IActionLanguageSyntax syntax) {
		this._relationName = relation.getName();
		this._verbPhrase = specifiedVerbPhrase;
		this._verbA = relation.getClassAVerb();
		this._verbB = relation.getClassBVerb();
		this._line = lineNumber;
		this._syntax = syntax;
	}
	
	public String explainError() {
		return "Line : " + _line + ". Verb phrase '"+_verbPhrase+"' should match either '"+_verbA+"' or '"+_verbB+"' in Relation '"+_relationName+"'  -> '" + _syntax + "'";
	}

}
