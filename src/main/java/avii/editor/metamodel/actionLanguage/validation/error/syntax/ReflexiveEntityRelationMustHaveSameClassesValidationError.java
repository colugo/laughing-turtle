package main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public class ReflexiveEntityRelationMustHaveSameClassesValidationError extends BaseActionLanguageValidationError {

	private int _line;
	private IActionLanguageSyntax _syntax;
	private String _relationName;
	private String _classAName;
	private String _classBName;
	public ReflexiveEntityRelationMustHaveSameClassesValidationError(String classAName, String classBName, String relationName, int line, IActionLanguageSyntax syntax)
	{
		this._relationName = relationName;
		this._classAName = classAName;
		this._classBName = classBName;
		this._line = line;
		this._syntax = syntax;
	}
	public String explainError() {
		return "Line : " + _line + ".Reflexive relation '" + _relationName + " does not have classes of the same type '"+ _classAName +"' & '"+ _classBName +"' ! ' -> '" + _syntax + "'";
	}

}
